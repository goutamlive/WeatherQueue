package com.gom.weatherqueue.queue;

import org.springframework.amqp.core.Queue;

/**
 * Created by goukumar on 2/25/2018.
 */
public class WorkerQueue extends Queue {
    public WorkerQueue(String name) {
        super(name);
    }
}
