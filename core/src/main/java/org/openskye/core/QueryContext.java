package org.openskye.core;

import com.google.common.base.Optional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple context for a query that is executed against a {@link QueryableStore}
 * <p/>
 * The context is there to show what {@link ObjectMetadata} would be in scope for the query
 */
@Data
@Slf4j
public class QueryContext {

    // Default the offset to 0
    private long offset = 0;
    private List<ObjectMetadata> objects = new ArrayList<>();

    public List<StructuredObject> resolveObjects(ArchiveStore archiveStore) {
        log.debug("QueryContext is starting to resolve objects");
        List<StructuredObject> structuredObjects = new ArrayList<>();
        for (ObjectMetadata om : objects) {
            log.debug("Resolving object metadata " + om);

            Optional<SimpleObject> optionObject = archiveStore.getSimpleObject(om);

            log.debug("Resolved simple object " + optionObject);

            if (optionObject.isPresent() && optionObject.get() instanceof StructuredObject)
                structuredObjects.add((StructuredObject) optionObject.get());
            else {
                throw new SkyeException("Unable to resolve objects since they are not structured, check " + optionObject);
            }
        }
        return structuredObjects;
    }
}
