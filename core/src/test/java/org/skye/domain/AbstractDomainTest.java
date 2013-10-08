package org.skye.domain;

import io.dropwizard.jackson.Jackson;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.StringWriter;

/**
 * A little helper for handling fixtures and JSON
 */
public class AbstractDomainTest {

    protected String jsonFixture(String name) throws IOException {
        return IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(name), "UTF-8");
    }

    protected String asJson(Object obj) throws IOException {
        StringWriter sb = new StringWriter();
        Jackson.newObjectMapper().writeValue(sb, obj);
        return sb.toString();
    }
}
