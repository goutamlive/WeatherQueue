package com.gom.weatherqueue.repository;

/**
 * Created by goukumar on 2/25/2018.
 */
import com.gom.weatherqueue.model.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, Long> {
    List<Weather> findByCityName(String cityName);
}
