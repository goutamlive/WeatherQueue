package com.gom.weatherqueue.config;

/**
 * Created by goukumar on 2/25/2018.
 */

import com.gom.weatherqueue.agent.Supervisor;
import com.gom.weatherqueue.agent.Worker;
import com.gom.weatherqueue.queue.WorkerQueue;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkerQueueConfig {

    @Bean
    public WorkerQueue workerQueue() {
        return new WorkerQueue("worker.queue");
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }

    @Bean
    public Worker receiver() {
        return new Worker();
    }

    @Bean
    public Supervisor sender() {
        return new Supervisor();
    }
}
