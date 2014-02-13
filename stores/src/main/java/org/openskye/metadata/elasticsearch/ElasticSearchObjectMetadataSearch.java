package org.openskye.metadata.elasticsearch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingResponse;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.hibernate.tool.hbm2ddl.IndexMetadata;
import org.openskye.core.*;
import org.openskye.domain.Domain;
import org.openskye.domain.Project;
import org.openskye.metadata.ObjectMetadataSearch;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

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
        String message = "Deleting all indexed data from project " + project;
        log.debug(message);

        // If the project has never been used to index data, then we have nothing to delete.
        if(isProjectIndexed(project) == false)
            return;

        // We now know that information exists for the project.
        // Since we have the metadata for the domain (domainIndex) and the project (projectType),
        // we can use them to perform the delete without the expense of performing a full query.
        client.admin()
              .indices()
              .prepareDeleteMapping()
              .setIndices(project.getDomain().getId())
              .setType(project.getId())
              .execute()
              .actionGet();
    }

    /**
     * Attempts to get information about the index created for the {@link Domain}.
     *
     * @param domain The {@link Domain} for which index information is retrieved.
     *
     * @return The {@link IndexMetaData} relating to the {@link Domain}, if it is found.
     */
    protected Optional<IndexMetaData> getDomainIndex(Domain domain)
    {
        // First, query Elastic Search about its current state.
        ClusterStateResponse stateResponse = client.admin()
                                                   .cluster()
                                                   .prepareState()
                                                   .execute()
                                                   .actionGet();

        // Obtain information about objects indexed for the domain, or null if it is not found.
        IndexMetaData domainIndex = stateResponse.getState()
                                                 .getMetaData()
                                                 .getIndices()
                                                 .get(domain.getId());

        if(domainIndex == null)
        {
            log.debug("No objects have been indexed for the domain " + domain);

            return Optional.absent();
        }

        return Optional.of(domainIndex);
    }

    /**
     * Determines if the any objects have been indexed for the given {@link Domain}.
     *
     * @param domain The {@link Domain} for which we are checking.
     *
     * @return True if at least one object has been indexed for the {@link Domain} or false if none have been indexed.
     */
    protected boolean isDomainIndexed(Domain domain)
    {
        if(getDomainIndex(domain).isPresent())
            return true;

        return false;
    }

    /**
     * Attempts to get information about the ElasticSearch type mappings created for the {@link Project}.
     *
     * @param project The {@link Project} for which type mapping information will be retrieved.
     *
     * @return The {@link MappingMetaData} related to the {@link Project}, if it is found.
     */
    protected Optional<MappingMetaData> getProjectMapping(Project project)
    {
        Optional<IndexMetaData> domainIndex = getDomainIndex(project.getDomain());

        if(!domainIndex.isPresent())
            return Optional.absent();

        MappingMetaData projectMapping = domainIndex.get().getMappings().get(project.getId());

        if(projectMapping == null)
        {
            log.debug("No objects have been indexed for the project " + project);

            return Optional.absent();
        }

        return Optional.of(projectMapping);
    }

    /**
     * Determines if any objects have been indexed for the given {@link Project}.
     *
     * @param project The {@link Project} for which we are checking.
     *
     * @return True if at least one object has been indexed for the {@link Project} or false if none have been indexed.
     */
    protected boolean isProjectIndexed(Project project)
    {
        if(getProjectMapping(project).isPresent())
            return true;

        return false;
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
    protected void performBulkOperation(Iterable<ObjectMetadata> objectMetadataList, OperationType type)
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
                    {
                        if(item.getResponse() instanceof DeleteResponse)
                        {
                            DeleteResponse deleteResponse = item.getResponse();

                            if(deleteResponse.isNotFound() == false)
                                aggregateException.add(new SkyeException(item.getFailureMessage()));
                        }
                        else
                        {
                            aggregateException.add(new SkyeException(item.getFailureMessage()));
                        }
                    }
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

    protected Set<String> getIndexNameSet()
    {
        IndicesStatusResponse response = this.client.admin()
                .indices()
                .prepareStatus()
                .execute()
                .actionGet();

        return response.getIndices().keySet();
    }

    protected String[] getIndexNames() {

        return getIndexNameSet().toArray(new String[0]);
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
