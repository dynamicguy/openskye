package org.skye.domain;

import org.junit.Test;

import java.util.UUID;

import static com.yammer.dropwizard.testing.JsonHelpers.asJson;
import static com.yammer.dropwizard.testing.JsonHelpers.jsonFixture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Test the serialization and handling of the {@link Domain}
 */
public class ArchiveStoreInstanceTest {

    @Test
    public void serializesToJSON() throws Exception {
        final ArchiveStoreInstance archiveStoreInstance = new ArchiveStoreInstance();
        archiveStoreInstance.setId("d6d0c091-95f8-46f4-bdda-fb172ed05490");
        assertThat("a ArchiveStoreInstance can be serialized to JSON",
                asJson(archiveStoreInstance),
                is(equalTo(jsonFixture("fixtures/ArchiveStoreInstance.json"))));
    }
}
