package org.skye.util;

import com.wordnik.swagger.jaxrs.JaxrsApiReader;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Environment;

public class SwaggerBundle extends AssetsBundle {

    public SwaggerBundle() {
        super("/swagger");
    }

    @Override
    public void run(Environment environment) {
        JaxrsApiReader.setFormatString("");
        ApiListingResourceJSON listing = new ApiListingResourceJSON();
        environment.addResource(listing);
        super.run(environment);
    }
}