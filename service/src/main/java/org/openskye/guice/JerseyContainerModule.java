package org.openskye.guice;

import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.spi.container.WebApplication;
import org.apache.shiro.guice.web.GuiceShiroFilter;

public class JerseyContainerModule extends JerseyServletModule {
    private final GuiceContainer container;

    public JerseyContainerModule(final GuiceContainer container) {
        this.container = container;
    }

    @Override
    protected void configureServlets() {
        bind(GuiceContainer.class).toInstance(container);
    }

    @Override
    public WebApplication webApp(com.sun.jersey.guice.spi.container.servlet.GuiceContainer guiceContainer) {
        return container.getWebApplication();
    }
}