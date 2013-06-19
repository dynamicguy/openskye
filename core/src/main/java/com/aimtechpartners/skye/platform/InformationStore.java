package com.aimtechpartners.skye.platform;

import org.joda.time.DateTime;

import java.util.Map;
import java.util.Properties;

/**
 * The base interface for an Information Store
 */
public interface InformationStore<T extends SimpleObject> {

    void initialize(Properties properties);

    Properties getMetadata();

    String getName();

    String getUrl();

    Iterable<T> getSince(DateTime dateTime);

    Iterable<T> getAll();

}
