package org.skye;

import com.google.inject.AbstractModule;
import org.skye.metadata.ObjectMetadataRepository;
import org.skye.metadata.impl.InMemoryObjectMetadataRepository;
import org.skye.stores.StoreRegistry;


/**
 * A basic module for Skye using the testing components
 */
public class SkyeTestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ObjectMetadataRepository.class).to(InMemoryObjectMetadataRepository.class).asEagerSingleton();
        bind(StoreRegistry.class).asEagerSingleton();
    }
}