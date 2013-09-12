package org.skye.mysql.metadata;

import com.google.common.base.Optional;
import org.skye.core.ObjectMetadata;
import org.skye.domain.InformationStoreDefinition;
import org.skye.domain.InformationStoreDefinition;
import org.skye.domain.Task;
import org.skye.metadata.ObjectMetadataRepository;

/**
 * Created with IntelliJ IDEA.
 * User: joshua
 * Date: 9/12/13
 * Time: 4:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class MySqlObjectMetadataRepository implements ObjectMetadataRepository
{
    @Override
    public Optional<ObjectMetadata> get(String id)
    {
        return null;
    }

    @Override
    public void put(ObjectMetadata objectMetadata)
    {
        return;
    }

    @Override
    public Iterable<ObjectMetadata> getObjects(InformationStoreDefinition informationStoreDefinition)
    {
        return null;
    }

    @Override
    public Iterable<ObjectMetadata> getObjects(Task task)
    {
        return null;
    }
}
