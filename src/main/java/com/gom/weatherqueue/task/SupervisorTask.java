package com.gom.weatherqueue.task;

import com.gom.weatherqueue.constraint.Constraint;

/**
 * Created by goukumar on 2/24/2018.
 */
public class SupervisorTask extends Task {
    public Constraint constraint;

    public Constraint getConstraint() {
        return constraint;
    }

    public void setConstraint(Constraint constraint) {
        this.constraint = constraint;
    }
}
