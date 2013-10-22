package org.openskye.domain;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/*
Test the serialization and handling of the {@link Domain}
 */


public class ArchiveStoreDefinitionTest extends AbstractDomainTest {

    @Test
    public void serializesToJSON() throws Exception{
 final ArchiveStoreDefinition archiveStoreDefinition=new ArchiveStoreDefinition();
        archiveStoreDefinition.setId("34fe-dd33-23dsx-22dc");
        assertThat("a ArchiveStoreDefinition can be serialized to JSON",
                asJson(archiveStoreDefinition),
                is(equalTo(jsonFixture("fixtures/ArchiveStoreDefinition.json"))));
    }
}
