package com.gom.weatherqueue.task;

import com.gom.weatherqueue.weather.WeatherConditions;

/**
 * Created by goukumar on 2/24/2018.
 */
public class WorkerTask extends Task {
    public long poolInterval;

    public WeatherConditions[] lastPolledWeatherConditions;

    public long getPoolInterval() {
        return poolInterval;
    }

    public void setPoolInterval(long poolInterval) {
        this.poolInterval = poolInterval;
    }

    public WeatherConditions[] getLastPolledWeatherConditions() {
        return lastPolledWeatherConditions;
    }

    public void setLastPolledWeatherConditions(WeatherConditions[] lastPolledWeatherConditions) {
        this.lastPolledWeatherConditions = lastPolledWeatherConditions;
    }
}
