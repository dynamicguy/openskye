package org.openskye.domain.dao;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import org.junit.Test;
import org.openskye.domain.*;

import com.google.inject.Provider;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.Iterator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test the  AttributeDefinitionDAO
 */
public class AttributeDefinitionDAOTest extends AbstractDAOTestBase<AttributeDefinition> {

    @Inject
    public AttributeDefinitionDAO attributeDefinitionDAO;

    @Inject
    public ChannelDAO channelDAO;

    @Inject
    public AttributeInstanceDAO attributeInstanceDAO;

    @Inject
    public MetadataTemplateDAO metadataTemplateDAO;

    @Inject
    public RetentionPolicyDAO retentionPolicyDAO;

    @Inject
    public DomainDAO domainDAO;

    @Inject
    public Provider<EntityManager> emf;

    @Override
    public AbstractPaginatingDAO getDAO() {
        return attributeDefinitionDAO;
    }

    @Override
    public AttributeDefinition getNew() {
        Domain domain = new Domain();
        AttributeDefinition isd = new AttributeDefinition();
        isd.setName("Test Def");
        isd.setDescription("Description");
        isd.setEmbedInObject(false);
        MetadataTemplate metadataTemplate = new MetadataTemplate();
        metadataTemplate.setName("Philip Dodds");
        metadataTemplate.setDescription("Metadata");
        metadataTemplate.setDomain(domain);
        isd.setMetadataTemplate(metadataTemplate);
        isd.setShortLabel("Label");
        isd.setOptional(false);
        isd.setType(AttributeType.TEXT);
        isd.setPossibleValues(new ArrayList<String>());
        return isd;
    }

    @Override
    public void update(AttributeDefinition instance) {
        instance.setName("Test Def 2");
        Domain domain = new Domain();
        instance.setEmbedInObject(false);
        instance.setName("Philip Dodds");
        MetadataTemplate metadataTemplate = new MetadataTemplate();
        metadataTemplate.setName("Philip Dodds");
        metadataTemplate.setDescription("Metadata");
        metadataTemplate.setDomain(domain);
        instance.setMetadataTemplate(metadataTemplate);
        instance.setDescription("Description");
        instance.setShortLabel("Label");
        instance.setOptional(false);
        instance.setType(AttributeType.TEXT);
        instance.setPossibleValues(new ArrayList<String>());
    }

    @Test
    public void testGetByChannel() {
        emf.get().getTransaction().begin();

        Domain domain = new Domain();
        RetentionPolicy policy = new RetentionPolicy();
        MetadataTemplate template = new MetadataTemplate();
        Channel channel = new Channel();
        AttributeDefinition definitionOne = new AttributeDefinition();
        AttributeDefinition definitionTwo = new AttributeDefinition();
        AttributeInstance instanceOne = new AttributeInstance();

        domain.setName("testGetByChannel() Domain");
        domain = domainDAO.create(domain);

        template.setName("testGetByChannel() Template");
        template.setDomain(domain);

        template = metadataTemplateDAO.create(template);

        policy.setName("testGetByChannel() Policy");
        policy.setRecordsCode("AD_Test_01");
        policy.setPeriodType(PeriodType.PERM);
        policy.setMetadataTemplate(template);

        policy = retentionPolicyDAO.create(policy);

        channel.setName("testGetByChannel() Channel");
        channel.setRetentionPolicy(policy);

        channel = channelDAO.create(channel);

        definitionOne.setName("testGetByChannel() Definition One");
        definitionOne.setShortLabel("One");
        definitionOne.setMetadataTemplate(template);
        definitionOne.setType(AttributeType.TEXT);

        definitionOne = attributeDefinitionDAO.create(definitionOne);

        definitionTwo.setName("testGetByChannel() Definition Two");
        definitionTwo.setShortLabel("Two");
        definitionTwo.setMetadataTemplate(template);
        definitionTwo.setType(AttributeType.TEXT);

        definitionTwo = attributeDefinitionDAO.create(definitionTwo);

        instanceOne.setAttributeDefinition(definitionOne);
        instanceOne.setMetadataOwnerType(MetadataOwnerType.CHANNEL);
        instanceOne.setChannel(channel);
        instanceOne.setAttributeValue("Test Value One");

        instanceOne = attributeInstanceDAO.create(instanceOne);

        Iterable<AttributeDefinition> definitionListOne = attributeDefinitionDAO.getAll(channel);

        int numberOfResults = 0;

        Iterator<AttributeDefinition> iterator = definitionListOne.iterator();

        assertThat("definitions were retrieved from getAll(Channel)", iterator.hasNext());

        while (iterator.hasNext()) {
            numberOfResults++;
            iterator.next();
        }

        assertThat("correct number of results retrieved from getAll(Channel)", numberOfResults, is(2));

        Iterable<AttributeDefinition> definitionListTwo = attributeDefinitionDAO.getUnused(channel);

        numberOfResults = 0;

        iterator = definitionListTwo.iterator();

        assertThat("definitions were retrieved from getUnused(Channel)", iterator.hasNext());

        while (iterator.hasNext()) {
            numberOfResults++;
            AttributeDefinition definitionTest = iterator.next();

            assertThat("The correct attribute definition is retrieved by getUnused(Channel)", definitionTest.getId(), is(equalTo(definitionTwo.getId())));
        }

        assertThat("correct number of results retrieved from getUnused(Channel)", numberOfResults, is(1));

    }

}



