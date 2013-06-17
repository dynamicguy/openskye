package com.aimtechpartners.skye.platform;

import org.joda.time.DateTime;

import java.io.InputStream;
import java.util.Map;

/**
 * The base interface for an Information Store
 */
public interface InformationStore<T extends SimpleObject> {

    Map<String,String> getMetadata();

    String getName();

    String getUrl();

    Iterable<T> getSince(DateTime dateTime);

    Iterable<T> getAll();

    InputStream getStream(T simpleObject);

}
