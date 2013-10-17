package org.openskye.domain;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;


/**
 * Test the serialization and handling of the {@link Domain}
 */
public class ArchiveStoreInstanceTest extends AbstractDomainTest {

    @Test
    public void serializesToJSON() throws Exception {
        final ArchiveStoreInstance archiveStoreInstance = new ArchiveStoreInstance();
        archiveStoreInstance.setId("d6d0c091-95f8-46f4-bdda-fb172ed05490");
        assertThat("a ArchiveStoreInstance can be serialized to JSON",
                asJson(archiveStoreInstance),
                is(equalTo(jsonFixture("fixtures/ArchiveStoreInstance.json"))));
    }
}
