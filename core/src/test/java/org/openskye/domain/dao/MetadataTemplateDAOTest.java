package org.openskye.domain.dao;

import com.google.common.base.Optional;
import org.junit.Test;
import org.openskye.domain.Domain;
import org.openskye.domain.MetadataTemplate;
import org.openskye.domain.Project;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
Test the  MetadataTemplateDAO
 */
public class MetadataTemplateDAOTest extends AbstractDAOTestBase<MetadataTemplate> {

    @Inject
    public MetadataTemplateDAO metadataTemplateDAO;

    @Override
    public AbstractPaginatingDAO getDAO() {
        return metadataTemplateDAO;
    }

    @Override
    public MetadataTemplate getNew() {
        Domain domain = new Domain();
        domain.setName("Fishstick");
        Project project = new Project();
        project.setName("Starship 1");
        project.setDomain(domain);
        MetadataTemplate metadataTemplate = new MetadataTemplate();
        metadataTemplate.setDescription("Metadata");
        metadataTemplate.setDomain(domain);
        metadataTemplate.setName("Test Def 2");

        return metadataTemplate;
    }

    @Override
    public void update(MetadataTemplate instance) {
        Domain domain=new Domain();
        instance.setName("Test Def 2");
        instance.setDescription("Metadata");
        instance.setDomain(domain);

    }


}


