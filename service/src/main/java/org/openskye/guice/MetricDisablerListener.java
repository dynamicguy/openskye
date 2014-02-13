package org.openskye.guice;

import com.codahale.metrics.*;
import io.dropwizard.setup.Bootstrap;

/*
 This listener will remove any Dropwizard metric as soon as it's added.
 */
public class MetricDisablerListener extends MetricRegistryListener.Base {
    private Bootstrap bootstrap;

    public MetricDisablerListener(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    void removeMetric(String name) {
        bootstrap.getMetricRegistry().remove(name);
    }

    @Override
    public void onGaugeAdded(String name, Gauge<?> gauge) {
        removeMetric(name);
    }

    @Override
    public void onCounterAdded(String name, Counter counter) {
        removeMetric(name);
    }

    @Override
    public void onHistogramAdded(String name, Histogram histogram) {
        removeMetric(name);
    }

    @Override
    public void onMeterAdded(String name, Meter meter) {
        removeMetric(name);
    }

    @Override
    public void onTimerAdded(String name, Timer timer) {
        removeMetric(name);
    }
}
