package org.skye.resource.dao;

import org.hibernate.SessionFactory;
import org.skye.domain.Project;

/**
 * DAO for the {@link org.skye.domain.Project}
 */
public class ProjectDAO extends AbstractPaginatingDAO<Project> {

    public ProjectDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
