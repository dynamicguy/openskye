package org.openskye.domain;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Test the serialization and handling of the {@link Domain}
 */
public class ProjectTest extends AbstractDomainTest {

    @Test
    public void serializesToJSON() throws Exception {
        final Project project = new Project();
        project.setId("e1c63d32-0984-4247-8f7d-975edab6b39a");
        project.setName("Example");
        assertThat("a Project can be serialized to JSON",
                asJson(project),
                is(equalTo(jsonFixture("fixtures/project.json"))));
    }
}
