package org.openskye.metadata.elasticsearch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.openskye.core.*;
import org.openskye.domain.Project;
import org.openskye.metadata.ObjectMetadataSearch;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * An implementation of OMS that uses ElasticSearch
 */
@Slf4j
public class ElasticSearchObjectMetadataSearch implements ObjectMetadataSearch {

    private static final SearchType SEARCH_TYPE = SearchType.QUERY_THEN_FETCH;
    private static final int START_PAGE = 0;
    @Inject
    private Client client;
    @Inject
    private SkyeSession session;
    @Inject
    private ObjectMapper objectMapper;

    @JsonIgnore
    public static final long BULK_REQUEST_SIZE = 20;

    private enum OperationType
    {
        INDEX, DELETE
    }

    @Override
    public long count(String query)
    {
        String index = session.getDomain().getId();
        String escapedQuery = smartEscapeQuery(query);
        CountResponse response;

        try
        {
            response = client.prepareCount(index)
                             .setQuery(new QueryStringQueryBuilder(escapedQuery))
                             .execute()
                             .actionGet();
        }
        catch(IndexMissingException ex)
        {
            return 0;
        }

        return response.getCount();
    }

    @Override
    public long count(Project project, String query)
    {
        String index = session.getDomain().getId();
        String type = project.getId();
        String escapedQuery = smartEscapeQuery(query);
        CountResponse response;

        try
        {
            response = client.prepareCount(index)
                             .setTypes(type)
                             .setQuery(new QueryStringQueryBuilder(escapedQuery))
                             .execute()
                             .actionGet();
        }
        catch(IndexMissingException ex)
        {
            return 0;
        }

        return response.getCount();
    }

    @Override
    public Iterable<ObjectMetadata> search(String query)
    {
        long count = count(query);

        if(count == 0)
            return new ArrayList<>();

        SearchPage searchPage = new SearchPage(START_PAGE, count);

        return search(query, searchPage);
    }

    @Override
    public Iterable<ObjectMetadata> search(Project project, String query)
    {
        long count = count(project, query);

        if(count == 0)
            return new ArrayList<>();

        SearchPage searchPage = new SearchPage(START_PAGE, count);

        return search(query, searchPage);
    }

    @Override
    public Iterable<ObjectMetadata> search(String query, SearchPage searchPage) {
        List<ObjectMetadata> listMetadata = new ArrayList<>();

        try {
            log.debug("Start start on "+session.getDomain().getId());
            SearchResponse response = this.client.prepareSearch()
                    .setIndices(session.getDomain().getId())
                    .setSearchType(SEARCH_TYPE)
                    .setQuery(new QueryStringQueryBuilder(smartEscapeQuery(query)))
                    .setFrom((int) searchPage.getPageStart())
                    .setSize((int) searchPage.getPageSize())
                    .execute()
                    .actionGet();
            SearchHits searchHits = response.getHits();

            for (SearchHit hit : searchHits) {
                String json = hit.getSourceAsString();
                ObjectMetadata metadata;

                try {
                    metadata = this.objectMapper.readValue(json, ObjectMetadata.class);
                } catch (IOException ex) {
                    throw new SkyeException("Failed to demarshal ObjectMetadata form JSON.", ex);
                }

                listMetadata.add(metadata);
            }


        } catch (IndexMissingException ex) {
            log.warn("Attempt to search on index that doesn't exist (domain:" + session.getDomain().getId() + ")");
        }
        return listMetadata;

    }

    @Override
    public Iterable<ObjectMetadata> search(Project project, String query, SearchPage searchPage) {
        List<ObjectMetadata> listMetadata = new ArrayList<>();
        try {
            SearchResponse response = client.prepareSearch()
                    .setIndices(session.getDomain().getId())
                    .setTypes(project.getId())
                    .setSearchType(SEARCH_TYPE)
                    .setQuery(new QueryStringQueryBuilder(smartEscapeQuery(query)))
                    .setFrom((int) searchPage.getPageStart())
                    .setSize((int) searchPage.getPageSize())
                    .execute()
                    .actionGet();

            SearchHits searchHits = response.getHits();

            for (SearchHit hit : searchHits) {
                String json = hit.getSourceAsString();
                ObjectMetadata metadata;

                try {
                    metadata = this.objectMapper.readValue(json, ObjectMetadata.class);
                } catch (IOException ex) {
                    throw new SkyeException("Failed to demarshal ObjectMetadata from JSON.", ex);
                }

                listMetadata.add(metadata);
            }
        } catch (IndexMissingException ex) {
            log.warn("Attempt to search on index that doesn't exist (domain:" + session.getDomain().getId() + ")");
        }

        return listMetadata;
    }

    @Override
    public void index(ObjectMetadata objectMetadata) {
        String domainId = objectMetadata.getProject().getDomain().getId();
        String projectId = objectMetadata.getProject().getId();
        String message = "Attempting to index an ObjectMetadata instance.\n" +
                        "Domain Id: " + domainId + "\n" +
                        "Project Id: " + projectId + "\n" +
                        "ObjectMetadata: " + objectMetadata;
        log.debug(message);


        try {
            String json = this.objectMapper.writeValueAsString(objectMetadata);
            client.prepareIndex(domainId, projectId, objectMetadata.getId())
                    .setSource(json)
                    .execute()
                    .actionGet();
        } catch (JsonProcessingException ex) {
            throw new SkyeException("Failed to marshal ObjectMetadata as JSON.", ex);
        }
    }

    @Override
    public void index(Iterable<ObjectMetadata> objectMetadataList)
    {
        performBulkOperation(objectMetadataList, OperationType.INDEX);
    }

    /**
     * Clears the indexed items for the OMS.
     * <p/>
     * This is intended for testing and demo purposes.
     */
    @Override
    public void clear()
    {
        log.debug("Clearing all indexed data across all indexed domains.");
        this.client.prepareDeleteByQuery()
                .setIndices(getIndexNames())
                .setQuery(QueryBuilders.matchAllQuery())
                .execute()
                .actionGet();
    }

    @Override
    public void delete()
    {
        log.debug("Deleting indexed data for the domain " + session.getDomain());
        String domainId = session.getDomain().getId();

        client.prepareDeleteByQuery()
                .setIndices(domainId)
                .setQuery(QueryBuilders.matchAllQuery())
                .execute()
                .actionGet();
    }

    @Override
    public void delete(ObjectMetadata objectMetadata)
    {
        log.debug("Deleting indexed information for the ObjectMetadata " + objectMetadata);
        String domainId = objectMetadata.getProject().getDomain().getId();
        String projectId = objectMetadata.getProject().getId();

        client.prepareDelete(domainId, projectId, objectMetadata.getId())
                .execute()
                .actionGet();
    }

    @Override
    public void delete(Iterable<ObjectMetadata> objectMetadataList)
    {
        performBulkOperation(objectMetadataList, OperationType.DELETE);
    }

    @Override
    public void delete(Project project)
    {
        String domainId = project.getDomain().getId();
        String message = "Deleting all indexed data from project " + project;
        log.debug(message);

        client.prepareDeleteByQuery()
              .setIndices(domainId)
              .setTypes(project.getId())
              .setQuery(QueryBuilders.matchAllQuery())
              .execute()
              .actionGet();
    }

    /**
     * Prepares and performs the operation of the specified type on multiple objects using
     * one or more bulk operations of a maximum size indicated by the BULK_REQUEST_SIZE constant.
     * This is more efficient than performing the operations one at a time, and the operations
     * will continue being performed even if one operation fails.
     * In the event of at least one failure, an {@link AggregateException} will be thrown when all
     * {@link ObjectMetadata} instances have been processed indicating the nature of each failure.
     *
     * @param objectMetadataList An Iterable object that accesses the {@link ObjectMetadata} instances on which the operation is to be performed.
     *
     * @param type The operation to be performed, as specified by the {@link OperationType} enumeration.
     */
    private void performBulkOperation(Iterable<ObjectMetadata> objectMetadataList, OperationType type)
    {
        // Create a bulk request.
        log.debug("Performing a bulk " + type.name() + " operation.");
        BulkRequestBuilder bulkRequest = client.prepareBulk();

        List<Exception> exceptionList = new ArrayList<>();
        AggregateException aggregateException = new AggregateException("The bulk " + type.name() + " operation failed on some objects.");
        Iterator<ObjectMetadata> iterator = objectMetadataList.iterator();

        while(iterator.hasNext())
        {
            ObjectMetadata objectMetadata = iterator.next();

            // For each object in the list, attempt to serialize that object.
            String domainId = objectMetadata.getProject().getDomain().getId();
            String projectId = objectMetadata.getProject().getId();
            String message = "Attempting to add an ObjectMetadata instance to a bulk " + type.name() + " request.\n" +
                    "Domain Id: " + domainId + "\n" +
                    "Project Id: " + projectId + "\n" +
                    "ObjectMetadata: " + objectMetadata;
            log.debug(message);


            // Add the index request to the bulk request.
            if(type == OperationType.INDEX)
            {
                try
                {
                    String json = objectMapper.writeValueAsString(objectMetadata);
                    bulkRequest.add(client.prepareIndex(domainId, projectId, objectMetadata.getId())
                        .setSource(json));
                }
                catch (JsonProcessingException ex)
                {
                    aggregateException.add(ex);
                }
            }
            else
            {
                bulkRequest.add(client.prepareDelete(domainId, projectId, objectMetadata.getId()));
            }

            if(bulkRequest.numberOfActions() >= BULK_REQUEST_SIZE || iterator.hasNext() == false)
            {
                BulkResponse response = bulkRequest.execute().actionGet();

                for(BulkItemResponse item : response.getItems())
                {
                    if(item.isFailed())
                        aggregateException.add(new SkyeException(item.getFailureMessage()));
                }

                if(iterator.hasNext())
                    bulkRequest = client.prepareBulk();
            }
        }

        if(!aggregateException.isEmpty())
            throw aggregateException;
    }

    /**
     * Ensures that all indexed entries are added to internal storage.
     * <p/>
     * This is intended for testing and demo purposes.
     */
    @Override
    public void flush() {
        this.client.admin()
                .indices()
                .prepareFlush(this.getIndexNames())
                .execute()
                .actionGet();
    }

    protected String[] getIndexNames() {
        IndicesStatusResponse response = this.client.admin()
                .indices()
                .prepareStatus()
                .execute()
                .actionGet();
        Set<String> indexSet = response.getIndices().keySet();

        return indexSet.toArray(new String[0]);
    }

    /**
     * Escape the string from bad chars for the search
     *
     * @param str the String that should be escaped
     * @return an escaped String
     */
    @SuppressWarnings({"ConstantConditions"})
    private static String smartEscapeQuery(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '\\' || c == '+' || c == '-' || c == '!' || c ==
                    '(' || c == ')' || c == ':'
                    || c == '^' || c == '[' || c == ']' || c == '\"'
                    || c == '{' || c == '}' || c == '~' || c == '/'
                    || c == '?' || c == '|' || c == '&' || c == ';'
                    || (!Character.isSpaceChar(c) &&
                    Character.isWhitespace(c))) {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

}
