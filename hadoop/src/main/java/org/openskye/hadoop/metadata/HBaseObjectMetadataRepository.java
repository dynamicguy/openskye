package org.openskye.hadoop.metadata;

import com.google.common.base.Optional;
import com.google.inject.Provider;
import org.openskye.core.ArchiveContentBlock;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.ObjectSet;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.InformationStoreDefinition;
import org.openskye.domain.Task;
import org.openskye.domain.dao.ArchiveStoreDefinitionDAO;
import org.openskye.domain.dao.InformationStoreDefinitionDAO;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.metadata.impl.jpa.JPAObjectMetadataRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * An implementation of the {@link ObjectMetadataRepository} that uses HBase as the
 * storage solution
 */
public class HBaseObjectMetadataRepository extends JPAObjectMetadataRepository {

    @Inject
    protected ArchiveStoreDefinitionDAO archiveStores;
    @Inject
    protected InformationStoreDefinitionDAO informationStores;
    @Inject
    private Provider<EntityManager> emf;

    protected EntityManager getEntityManager() {
        return emf.get();
    }

    @Override
    public void deleteObjectSet(ObjectSet objectSet) {
        return;
    }

    @Override
    public void addObjectToSet(ObjectSet objectSet, ObjectMetadata objectMetadata) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isObjectInSet(ObjectSet objectSet, ObjectMetadata objectMetadata) {
        return false;
    }

    @Override
    public void removeObjectToSet(ObjectSet objectSet, ObjectMetadata objectMetadata) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<ObjectMetadata> get(String id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void put(ObjectMetadata objectMetadata) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<ObjectMetadata> getObjects(InformationStoreDefinition informationStoreDefinition) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<ObjectMetadata> getObjects(Task task) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<ObjectMetadata> getObjects(ObjectSet objectSet) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<ObjectSet> getObjectSet(String objectSetId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InformationStoreDefinition getSourceInformationStoreDefinition(ObjectMetadata objectMetadata) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ArchiveStoreDefinition getArchiveStoreDefinition(ArchiveContentBlock archiveContentBlock) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
