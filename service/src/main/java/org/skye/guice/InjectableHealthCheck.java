package org.skye.guice;

import com.yammer.metrics.core.HealthCheck;

public abstract class InjectableHealthCheck extends HealthCheck {
    protected InjectableHealthCheck(String name) {
        super(name);
    }

    public abstract String getName();
}
