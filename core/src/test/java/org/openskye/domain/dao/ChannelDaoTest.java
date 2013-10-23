package org.openskye.domain.dao;

import org.junit.Test;
import org.openskye.domain.*;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

/**
 * Testing the {@link org.openskye.domain.dao.ArchiveStoreDefinitionDAO}
 */
public class ChannelDaoTest extends AbstractDAOTestBase<Channel> {

    @Inject
    public ChannelDAO channelDAO;

    @Override
    public AbstractPaginatingDAO getDAO() {
        return channelDAO;
    }

    @Override
    public Channel getNew() {
        Domain domain = new Domain();
        domain.setName("Fishstick");
        Project project = new Project();
        project.setName("Starship 1");
        project.setDomain(domain);
        ArchiveStoreDefinition asd = new ArchiveStoreDefinition();
        asd.setProject(project);
        asd.setName("Test Def");
        InformationStoreDefinition isd = new InformationStoreDefinition();
        isd.setName("Test Def");
        Channel channel = new Channel();
        channel.setName("Demo Channel");
        channel.setInformationStoreDefinition(isd);
        channel.setProject(project);
        return channel;
    }

    @Override
    public void update(Channel instance) {
        instance.setName("Test Def 2");
    }

    @Test(expected = ConstraintViolationException.class)
    public void tryMissingName() {
        Channel channel = getNew();
        channel.setName(null);
        getDAO().create(channel);
    }
}
