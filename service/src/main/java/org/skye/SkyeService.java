package org.skye;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import org.skye.config.SkyeConfiguration;
import org.skye.core.SkyePlatform;

/**
 * The Skye Service
 */
public class SkyeService extends Service<SkyeConfiguration> {

    public static void main(String[] args) throws Exception {
        new SkyeService().run(args);
    }

    @Override
    public void initialize(Bootstrap<SkyeConfiguration> bootstrap) {
        bootstrap.setName("skye");
    }

    @Override
    public void run(SkyeConfiguration configuration,
                    Environment environment) {
        environment.addResource(new SkyePlatform());
    }

}