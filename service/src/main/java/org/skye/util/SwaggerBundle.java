package org.skye.util;

import com.wordnik.swagger.jaxrs.listing.ApiListingResourceJSON;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Environment;

/**
 * A little bundle to handle hooking in the swagger stuff
 */
public class SwaggerBundle extends AssetsBundle {

    public static final String DEFAULT_PATH = "/api-docs";

    public SwaggerBundle() {
        super(DEFAULT_PATH);
    }

    public void initialize(Environment environment) {
        environment.addResource(new ApiListingResourceJSON());
    }
}