package org.openskye.domain;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * User: atcmostafavi
 * Date: 12/12/13
 * Time: 12:59 PM
 * Project: platform
 */
public class ProjectUserTest extends AbstractDomainTest {
    @Test
    public void serializeToJSON() throws Exception {
        final ProjectUser projectUser = new ProjectUser();
        projectUser.setId("89er-15pl-46po-10op");
        assertThat("a ProjectUser can be serialized to JSON",
                asJson(projectUser),
                is(equalTo(jsonFixture("fixtures/ProjectUser.json"))));

    }
}
