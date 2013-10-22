package org.openskye.domain;

/**
 * Created with IntelliJ IDEA.
 * User: Krishna
 * Date: 10/21/13
 * Time: 1:03 PM
 * To change this template use File | Settings | File Templates.
 */
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
public class RetentionPolicyTest extends AbstractDomainTest {

    @Test
    public void serializeToJSON() throws Exception{
         final RetentionPolicy retentionPolicy=new RetentionPolicy();
        retentionPolicy.setId("89er-15pl-46po-10op");
        retentionPolicy.setOnHold(false);
        assertThat("a RetentionPolicy can be serialized to JSON",
                asJson(retentionPolicy),
                is(equalTo(jsonFixture("fixtures/RetentionPolicy.json"))));

    }

}
