package com.gom.weatherqueue.queue;

import org.springframework.amqp.core.Queue;

/**
 * Created by goukumar on 2/25/2018.
 */
public class ResultQueue  extends Queue {
    public ResultQueue(String name) {
        super(name);
    }
}
