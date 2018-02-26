package com.gom.weatherqueue.controller;

import com.gom.weatherqueue.agent.Supervisor;
import com.gom.weatherqueue.config.WorkerQueueConfig;
import com.gom.weatherqueue.model.Weather;
import com.gom.weatherqueue.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by goukumar on 2/25/2018.
 */
@RestController
@RequestMapping("/api")
public class WeatherQueueController {
    @Autowired
    WeatherRepository weatherRepository;

    @Autowired
    WorkerQueueConfig workerQueueConfig;

    @GetMapping("/index")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @GetMapping("/weather")
    public List<Weather> getWeather(){
        return weatherRepository.findAll();
    }

    @GetMapping("/poll")
    public String poll(){
        Supervisor supervisor = workerQueueConfig.sender();
        supervisor.doTask();
        return "Polled successfully";
    }


}