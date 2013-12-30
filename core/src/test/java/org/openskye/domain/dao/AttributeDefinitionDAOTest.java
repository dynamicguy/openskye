package org.openskye.domain.dao;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import org.junit.Test;
import org.openskye.domain.AttributeDefinition;
import org.openskye.domain.AttributeType;
import org.openskye.domain.Domain;
import org.openskye.domain.MetadataTemplate;

import javax.validation.ConstraintViolationException;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
Test the  AttributeDefinitionDAO
 */
public class AttributeDefinitionDAOTest extends AbstractDAOTestBase<AttributeDefinition> {

    @Inject
    public AttributeDefinitionDAO attributeDefinitionDAO;

    @Override
    public AbstractPaginatingDAO getDAO() {
        return attributeDefinitionDAO;
    }

    @Override
    public AttributeDefinition getNew() {
        Domain domain=new Domain();
        AttributeDefinition isd = new AttributeDefinition();
        isd.setName("Test Def");
        isd.setDescription("Description");
        isd.setEmbedInObject(false);
        MetadataTemplate metadataTemplate=new MetadataTemplate();
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
        Domain domain=new Domain();
        instance.setEmbedInObject(false);
        instance.setName("Philip Dodds");
        MetadataTemplate metadataTemplate=new MetadataTemplate();
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


}



