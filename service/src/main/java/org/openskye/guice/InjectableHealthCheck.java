package org.openskye.guice;

import com.codahale.metrics.health.HealthCheck;

public abstract class InjectableHealthCheck extends HealthCheck {
    public abstract String getName();
}
