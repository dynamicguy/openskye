package org.openskye.domain.dao;

import com.google.common.base.Optional;
import com.google.guiceberry.junit4.GuiceBerryRule;
import com.google.inject.persist.PersistService;
import io.dropwizard.jackson.Jackson;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openskye.domain.Identifiable;

import javax.inject.Inject;

import java.io.IOException;
import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * An abstract base for tests that need to access an in-memory database for stuff
 */
public abstract class AbstractDAOTestBase<T extends Identifiable> {
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(H2DatabaseTestModule.class);
    @Inject
    PersistService persistService;

    public abstract AbstractPaginatingDAO<T> getDAO();

    public abstract T getNew();

    public abstract void update(T instance);

    protected String jsonFixture(String name) throws IOException {
        return IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(name), "UTF-8");
    }

    protected String asJson(Object obj) throws IOException {
        StringWriter sb = new StringWriter();
        Jackson.newObjectMapper().writeValue(sb, obj);
        return sb.toString();
    }

    @Before
    public void checkStarted() {
        try {
            persistService.start();
        } catch (IllegalStateException e) {
            // Ignore it we are started
        }
    }

    @Test
    public void doCreate() throws Exception {
        T instance = getNew();
        getDAO().create(instance);

        assertThat(instance.getId(), is(notNullValue()));

        Optional<T> optionalDomain = getDAO().get(instance.getId());

        assertThat(optionalDomain.isPresent(), is(true));
    }

    @Test
    public void doUpdate() throws Exception {
        T instance = getNew();
        getDAO().create(instance);
        assertThat(instance.getId(), is(notNullValue()));

        Optional<T> optionalDomain = getDAO().get(instance.getId());

        assertThat(optionalDomain.isPresent(), is(true));

        T instanceToUpdate = optionalDomain.get();
        update(instanceToUpdate);

        getDAO().update(instanceToUpdate);

        assertThat(instanceToUpdate.getId().equals(instance.getId()), is(true));

    }

    @Test
    public void doDelete() throws Exception {
        T instance = getNew();
        getDAO().create(instance);
        assertThat(instance.getId(), is(notNullValue()));

        Optional<T> optionalInstance = getDAO().get(instance.getId());

        assertThat(optionalInstance.isPresent(), is(true));

        getDAO().delete(instance);

        Optional<T> deletedInstance = getDAO().get(instance.getId());

        assertThat(deletedInstance.isPresent(), is(false));

    }
}
