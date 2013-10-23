package org.openskye.domain;

/**
 Test the serialization and handling of the {@link Domain}
 */

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;


public class MetadataTemplateTest extends AbstractDomainTest {
    @Test
    public void serializesToJSON() throws Exception {

        final MetadataTemplate metadataTemplate = new MetadataTemplate();
        metadataTemplate.setId("12dfd-32fdfd-232cdcd-22cdfd");
        assertThat("a MetaDataTemplate can be serialized to JSON",
                asJson(metadataTemplate),
                is(equalTo(jsonFixture("fixtures/MetadataTemplate.json"))));

    }
}
