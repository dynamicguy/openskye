package org.openskye.domain.dao;

import org.junit.Test;
import org.openskye.domain.Domain;
import org.openskye.domain.Project;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

/**
 * Testing the {@link ProjectDAO}
 */
public class ProjectDAOTest extends AbstractDAOTestBase<Project> {

    @Inject
    public ProjectDAO projectDAO;

    @Override
    public AbstractPaginatingDAO getDAO() {
        return projectDAO;
    }

    @Override
    public Project getNew() {
        Domain domain = new Domain();
        domain.setName("Fishstick");
        Project project = new Project();
        project.setName("Starship 1");
        project.setDomain(domain);
        return project;
    }

    @Override
    public void update(Project instance) {
        instance.setName("Starship 2");
    }

    @Test(expected = ConstraintViolationException.class)
    public void tryMissingName() {
        Project project = getNew();
        project.setName(null);
        getDAO().create(project);
    }
}
