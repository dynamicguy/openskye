package org.openskye.domain;

/**
 * Created with IntelliJ IDEA.
 * User: Krishna
 * Date: 10/21/13
 * Time: 12:03 PM
 * To change this template use File | Settings | File Templates.
 */
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
public class AttributeInstanceTest extends AbstractDomainTest {
    @Test
    public void serializeToJSON() throws Exception{

        final AttributeInstance attributeInstance=new AttributeInstance();
        attributeInstance.setId("78hh-56tt-45uy-92ju");
        attributeInstance.setMetadataOwnerType(null);
        attributeInstance.setOwnerId(null);
        attributeInstance.setChannel(null);
        attributeInstance.setAttributeDefinition(null);
        attributeInstance.setAttributeValue(null);
        assertThat("a AttributeInstance can be serialized to JSON",
                asJson(attributeInstance),
                is(equalTo(jsonFixture("fixtures/AttributeInstance.json"))));

    }

}
