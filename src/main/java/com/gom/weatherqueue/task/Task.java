package com.gom.weatherqueue.task;

import java.util.UUID;

/**
 * Created by goukumar on 2/24/2018.
 */
public abstract class Task {
    public String queueName;
    public String id = UUID.randomUUID().toString();
    public String message;

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
