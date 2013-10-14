package org.skye.util;

import com.wordnik.swagger.config.ScannerFactory;
import com.wordnik.swagger.jaxrs.config.DefaultJaxrsScanner;
import com.wordnik.swagger.jaxrs.listing.ApiDeclarationProvider;
import com.wordnik.swagger.jaxrs.listing.ApiListingResourceJSON;
import com.wordnik.swagger.jaxrs.listing.ResourceListingProvider;
import com.wordnik.swagger.jaxrs.reader.DefaultJaxrsApiReader;
import com.wordnik.swagger.reader.ClassReaders;
import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * A little bundle to handle hooking in the swagger stuff
 */
public class SwaggerBundle implements Bundle {

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        // Nothing to do
    }

    @Override
    public void run(Environment environment) {
        environment.jersey().register(new ApiListingResourceJSON());
        // add swagger providers
        environment.jersey().register(new ApiDeclarationProvider());
        environment.jersey().register(new ResourceListingProvider());

        // add a class scanner.  The DefaultJaxrsScanner will look for an Application context and getSingletons() and getClasses()
        ScannerFactory.setScanner(new DefaultJaxrsScanner());

        // add a reader, the DefaultJaxrsApiReader will scan @Api annotations and create the swagger spec from them
        ClassReaders.setReader(new DefaultJaxrsApiReader());

    }
}