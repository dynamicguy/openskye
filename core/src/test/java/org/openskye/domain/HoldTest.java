package org.openskye.domain;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * User: atcmostafavi Date: 1/10/14 Time: 1:36 PM Project: platform
 */
public class HoldTest extends AbstractDomainTest {
    @Test
    public void serializesToJSON() throws Exception {
        final Hold hold = new Hold();
        hold.setId("11d4844c-f600-4570-85a8-b94431203370");
        hold.setName("Test");
        hold.setDescription("Test");
        hold.setQuery("foo:bar");
        assertThat("a Hold can be serialized to JSON",
                asJson(hold),
                is(equalTo(jsonFixture("fixtures/Hold.json"))));
    }
}
