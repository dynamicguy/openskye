package com.infobelt.skye.platform;

import org.joda.time.DateTime;

import java.util.Properties;

/**
 * A representation of an Information Store
 * <p/>
 * An information store is basically any source system that is capable of providing access
 * to a type of {@link SimpleObject} for ingestion into the Skye ILM framework
 */
public interface InformationStore<T extends SimpleObject> {

    void initialize(Properties properties);

    Properties getMetadata();

    String getName();

    String getUrl();

    Iterable<T> getSince(DateTime dateTime);

    Iterable<T> getAll();

}
