package com.gom.weatherqueue.weather;

import java.util.Date;

/**
 * Created by goukumar on 2/24/2018.
 */
public class WeatherConditions {
    private String code;
    private String city;
    private float temperature;
    private Date observationTime;

    public WeatherConditions(){}

    public WeatherConditions(String code, String city, float temperature, Date observationTime) {
        this.code =  code;
        this.city = city;
        this.temperature = temperature;
        this.observationTime = observationTime;
    }

    public String getCode() {
        return code;
    }

    public String getCity() {
        return city;
    }

    public float getTemperature() {
        return temperature;
    }

    public Date getObservationTime() {
        return observationTime;
    }
}
