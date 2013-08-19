package org.skye.stores;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * A set of basic tests for the registry
 */
public class InMemoryArchiveTest {

    StoreRegistry registry = new StoreRegistry();

    @Test
    public void checkWeHaveInMemoryStores() {
        assertThat("We have 1 information store in the registry", registry.getInformationStoreCount(), is(equalTo(1)));
        assertThat("We have 1 archive store in the registry", registry.getArchiveStoreCount(), is(equalTo(1)));
    }
}
