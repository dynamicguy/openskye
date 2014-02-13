package org.openskye.healthcheck;

import org.elasticsearch.client.Client;
import org.openskye.guice.InjectableHealthCheck;

import javax.inject.Inject;

public class SearchHealthCheck extends InjectableHealthCheck {

    @Inject
    private Client client;

    @Override
    protected Result check() throws Exception {
        try {
            client.admin().indices().prepareStatus().execute().actionGet();
            return Result.healthy();
        } catch (Exception e) {
            return Result.unhealthy("Cannot connect to Search layer");
        }
    }

    @Override
    public String getName() {
        return "search-health-check";
    }
}
