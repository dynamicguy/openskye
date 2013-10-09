package org.skye.domain;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Test the serialization and handling of the {@link org.skye.domain.Domain}
 */
public class DomainArchiveStoreTest extends AbstractDomainTest {

    @Test
    public void serializesToJSON() throws Exception {
        final ArchiveStoreDefinition archiveStoreDefinition = new ArchiveStoreDefinition();
        archiveStoreDefinition.setId("44252357-f7bb-4e77-8d0c-c478e129bf1d");
        assertThat("a DomainArchiveStore can be serialized to JSON",
                asJson(archiveStoreDefinition),
                is(equalTo(jsonFixture("fixtures/DomainArchiveStore.json"))));
    }
}
