package com.gom.weatherqueue.config;

import com.gom.weatherqueue.agent.Supervisor;
import com.gom.weatherqueue.agent.Worker;
import com.gom.weatherqueue.queue.ResultQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by goukumar on 2/25/2018.
 */
@Configuration
public class ResultQueueConfig {
    @Bean
    public ResultQueue resultQueue() {
        return new ResultQueue("result.queue");
    }

    @Bean
    public Supervisor receiver() {
        return new Supervisor();
    }

    @Bean
    public Worker sender() {
        return new Worker();
    }
}
