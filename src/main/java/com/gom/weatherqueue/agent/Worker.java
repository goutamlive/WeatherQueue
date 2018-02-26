package com.gom.weatherqueue.agent;

/**
 * Created by goukumar on 2/25/2018.
 */
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gom.weatherqueue.queue.ResultQueue;
import com.gom.weatherqueue.result.Result;
import com.gom.weatherqueue.result.WorkerResult;
import com.gom.weatherqueue.task.Task;
import com.gom.weatherqueue.task.WorkerTask;
import com.gom.weatherqueue.weather.WeatherConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RabbitListener(queues = "worker.queue")
public class Worker {

    private static final Logger log = LoggerFactory.getLogger(Worker.class);

    @Value( "${wunderground.api.url}" )
    private String wundergroundUrl;

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private ResultQueue resultQueue;

    @RabbitHandler
    public void receive(String in) {
        log.info(" [x] Received '" + in + "'");
        ObjectMapper mapper = new ObjectMapper();
        try {
            WorkerTask task = mapper.readValue(in, WorkerTask.class);
            doTask(task);
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public void doTask(WorkerTask workerTask){
        log.debug("--------- Starting worker task for --------");
        WeatherConditions[] citiesLastPolledWeatherConditions = workerTask.getLastPolledWeatherConditions();
        WeatherConditions  lastPolledWeatherConditions =  null;
        WeatherConditions currentWeatherConditions = null;
        Result result = null;
        for(int i = 0; i < citiesLastPolledWeatherConditions.length; i++){
            lastPolledWeatherConditions = citiesLastPolledWeatherConditions[i];
            if(lastPolledWeatherConditions!=null){
                currentWeatherConditions = getCurrentWeatherCondition(lastPolledWeatherConditions.getCode(), lastPolledWeatherConditions.getCity());
                if(currentWeatherConditions!=null){
                    result = getResult(lastPolledWeatherConditions , currentWeatherConditions);
                }
            }
            if(result!=null) {
                returnResult(result);
            }
        }
    }

    private Result getResult(WeatherConditions lastPolled, WeatherConditions current){
        Result result = new WorkerResult();
        result.setCode(lastPolled.getCode());
        result.setCity(lastPolled.getCity());
        result.setTemperatureDifferenceInFahrenheit(lastPolled.getTemperature() - current.getTemperature());
        result.setTimeDifferenceInMinute((current.getObservationTime().getTime() - lastPolled.getObservationTime().getTime())/ (60 * 1000) % 60);
        return result;
    }

    private void returnResult(Result result){
       log.info("Sending results to supervisor"+result.getCode()+"---"+result.getCity());
        ObjectMapper mapper = new ObjectMapper();
        try {
            String message = mapper.writeValueAsString(result);
            this.template.convertAndSend(resultQueue.getName(), message);
        }catch (JsonProcessingException ex){
            System.out.println("Cannot convert object to json");
            ex.printStackTrace();
        }
    }

    private WeatherConditions getCurrentWeatherCondition(String code, String city){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(
                wundergroundUrl,
                String.class, code, city);
        if (HttpStatus.OK == response.getStatusCode()) {
            return getCurrentWeatherConditionFromJson(response.getBody(), code, city);
        } else {
            return null;
        }
    }

    private WeatherConditions getCurrentWeatherConditionFromJson(String jsonString, String code, String city) {
        ObjectMapper objectMapper = new ObjectMapper();
        WeatherConditions currentWeatherCondition = null;
        try {
            Map<?, ?> objMap = objectMapper.readValue(jsonString, Map.class);
            Map<?, ?> currentObservation = (Map<?, ?>) objMap.get("current_observation");
            String time = currentObservation.get("observation_time_rfc822").toString();
            log.info(time);
            String temp = currentObservation.get("temp_f").toString();
            log.info(temp);
            if(time != null && temp != null) {
                Date observationTime = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z").parse(time);
                currentWeatherCondition = new WeatherConditions(code, city, Float.parseFloat(temp), observationTime);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentWeatherCondition;
    }
}
