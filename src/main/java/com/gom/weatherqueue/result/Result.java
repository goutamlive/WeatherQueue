package com.gom.weatherqueue.result;

/**
 * Created by goukumar on 2/24/2018.
 */
public class Result {
    private String code;
    private String city;
    private float temperatureDifferenceInFahrenheit;
    private long timeDifferenceInMinute;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public float getTemperatureDifferenceInFahrenheit() {
        return temperatureDifferenceInFahrenheit;
    }

    public void setTemperatureDifferenceInFahrenheit(float temperatureDifferenceInFahrenheit) {
        this.temperatureDifferenceInFahrenheit = temperatureDifferenceInFahrenheit;
    }

    public long getTimeDifferenceInMinute() {
        return timeDifferenceInMinute;
    }

    public void setTimeDifferenceInMinute(long timeDifferenceInMinute) {
        this.timeDifferenceInMinute = timeDifferenceInMinute;
    }
}
