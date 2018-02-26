package com.gom.weatherqueue.agent;

/**
 * Created by goukumar on 2/25/2018.
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gom.weatherqueue.constraint.City;
import com.gom.weatherqueue.constraint.Constraint;
import com.gom.weatherqueue.constraint.Country;
import com.gom.weatherqueue.model.Weather;
import com.gom.weatherqueue.model.WeatherType;
import com.gom.weatherqueue.queue.WorkerQueue;
import com.gom.weatherqueue.repository.WeatherRepository;
import com.gom.weatherqueue.result.Result;
import com.gom.weatherqueue.service.ConfigService;
import com.gom.weatherqueue.task.Task;
import com.gom.weatherqueue.task.WorkerTask;
import com.gom.weatherqueue.weather.WeatherConditions;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@RabbitListener(queues = "result.queue")
public class Supervisor {
    @Autowired
    private ConfigService configService;

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private WorkerQueue workerQueue;

    @Autowired
    private WeatherRepository weatherRepository;

    @Scheduled(fixedDelay = 5000)
    public void doTask(){
        Constraint constraint = configService.constraint;
        splitTask(constraint);
    }

    public void delegateSubTask(WorkerTask task) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String message = mapper.writeValueAsString(task);
            template.convertAndSend(workerQueue.getName(), message);
            System.out.println(" [x] Sent '" + message + "'");
        }catch (JsonProcessingException ex){
            System.out.println("Cannot convert object to json");
            ex.printStackTrace();
        }
    }

    private void splitTask(Constraint constraint){
        Country country = null;
        City city = null;
        for(int i = 0; i < constraint.countries.length; i++){
            country = constraint.countries[i];
            delegateSubTask(getWorkerTask(country));
        }
    }

    private WorkerTask getWorkerTask(Country country){
        WorkerTask workerTask = new WorkerTask();
        WeatherConditions[] lastPooledWeatherConditions = new WeatherConditions[country.cities.length];
        for(int i = 0; i < country.cities.length; i++){
            lastPooledWeatherConditions[i] = getLastPolledWeatherCondition(country.cities[i].code, country.cities[i].name);
        }
        workerTask.setLastPolledWeatherConditions(lastPooledWeatherConditions);
        workerTask.setMessage("Get current conditions");
        workerTask.setQueueName(country.code);
        workerTask.setPoolInterval(country.pollingIntervalInMs);
        return workerTask;
    }

    private WeatherConditions getLastPolledWeatherCondition(String code, String name){
        List<Weather> weathers = weatherRepository.findByCityName(name);
        Weather currentWeather = null;
        WeatherConditions lastPolledWeatherConditions = null;
        for (Weather weather : weathers) {
            if (weather.getWeatherType().equals(WeatherType.CURRENT)) {
                currentWeather = weather;
            }
        }
        if(currentWeather==null) {
            lastPolledWeatherConditions = new WeatherConditions(code, name, 0, new Date());
        }else{
            lastPolledWeatherConditions = new WeatherConditions(code, name, currentWeather.getLastPooledTemperature(), currentWeather.getUpdatedAt());
        }
        return lastPolledWeatherConditions;
    }

    @RabbitHandler
    private void receive(String in) {
        System.out.println(" [y] Received '" + in + "'");
        ObjectMapper mapper = new ObjectMapper();
        try {
            Result result = mapper.readValue(in, Result.class);
            combineResults(result);
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    private void combineResults(Result result){
        List<Weather> weathers = weatherRepository.findByCityName(result.getCity());
        Country country = getCountryByCode(result.getCode());
        Weather lastPooledWeather = null;
        Weather currentWeather = null;
        if(weathers.size()==0){
            currentWeather = new Weather();
            currentWeather.setCountryName(country.name);
            currentWeather.setCountryCode(country.code);
            currentWeather.setCityCode(result.getCode());
            currentWeather.setCityName(result.getCity());
            currentWeather.setLastPooledTemperature(0 - result.getTemperatureDifferenceInFahrenheit());
            currentWeather.setWeatherType(WeatherType.CURRENT);
            weatherRepository.save(currentWeather);
        }else if(weathers.size()==1){
            lastPooledWeather = weathers.get(0);
            currentWeather = new Weather();
            currentWeather.setCountryName(country.name);
            currentWeather.setCountryCode(country.code);
            currentWeather.setCityCode(result.getCode());
            currentWeather.setCityName(result.getCity());
            currentWeather.setLastPooledTemperature(lastPooledWeather.getLastPooledTemperature() - result.getTemperatureDifferenceInFahrenheit());
            currentWeather.setWeatherType(WeatherType.CURRENT);
            weatherRepository.save(currentWeather);
            lastPooledWeather.setWeatherType(WeatherType.LAST_POOLED);
            weatherRepository.save(lastPooledWeather);
        }else {
            for (Weather weather : weathers) {
                if (weather.getWeatherType().equals(WeatherType.CURRENT)) {
                    currentWeather = weather;
                } else {
                    lastPooledWeather =  weather;
                }
            }
            lastPooledWeather.setLastPooledTemperature(currentWeather.getLastPooledTemperature());
            weatherRepository.save(lastPooledWeather);
            currentWeather.setLastPooledTemperature(currentWeather.getLastPooledTemperature() - result.getTemperatureDifferenceInFahrenheit());
            weatherRepository.save(currentWeather);
        }
    }

    private Country getCountryByCode(String code){
        Constraint constraint = configService.constraint;
        for(Country country: constraint.countries){
            for(City city: country.cities){
                if(city.code.equals(code)){
                    return country;
                }
            }
        }
        return null;
    }
}
