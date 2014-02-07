package org.openskye.metadata.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
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
import org.openskye.core.ObjectMetadata;
import org.openskye.core.SearchPage;
import org.openskye.core.SkyeException;
import org.openskye.core.SkyeSession;
import org.openskye.domain.Project;
import org.openskye.metadata.ObjectMetadataSearch;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
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
        // Create a bulk request.
        log.debug("Creating a bulk index request.");
        BulkRequestBuilder bulkRequest = client.prepareBulk();

        for(ObjectMetadata objectMetadata : objectMetadataList)
        {
            try
            {
                // For each object in the list, attempt to serialize that object.
                String domainId = objectMetadata.getProject().getDomain().getId();
                String projectId = objectMetadata.getProject().getId();
                String message = "Attempting to add an ObjectMetadata instance to a bulk index request.\n" +
                        "Domain Id: " + domainId + "\n" +
                        "Project Id: " + projectId + "\n" +
                        "ObjectMetadata: " + objectMetadata;
                log.debug(message);

                String json = objectMapper.writeValueAsString(objectMetadata);

                // Add the index request to the bulk request.
                bulkRequest.add(client.prepareIndex(domainId, projectId, objectMetadata.getId())
                        .setSource(json));
            }
            catch (JsonProcessingException ex)
            {
                throw new SkyeException("Failed to marshal ObjectMetadata as JSON", ex);
            }
        }

        // Execute the bulk index request.
        log.debug("Executing the bulk index request.");
        BulkResponse response = bulkRequest.execute().actionGet();

        // Upon completion, if there are failures, indicate the nature of each in an exception.
        if(response.hasFailures())
            throw new SkyeException(response.buildFailureMessage());
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
        // Create the bulk delete request.
        log.debug("Creating bulk delete request.");
        BulkRequestBuilder bulkRequest = client.prepareBulk();

        for(ObjectMetadata objectMetadata : objectMetadataList)
        {
            // Add the individual delete request to the bulk request.
            String domainId = objectMetadata.getProject().getDomain().getId();
            String projectId = objectMetadata.getProject().getId();
            String message = "Attempting to add an ObjectMetadata to a bulk delete request.\n" +
                             "Domain Id: " + domainId + "\n" +
                             "Project Id: " + projectId + "\n" +
                             "ObjectMetadata: " + objectMetadata;
            log.debug(message);

            bulkRequest.add(client.prepareDelete(domainId, projectId, objectMetadata.getId()));
        }

        // Executing the bulk delete request.
        log.debug("Executing the bulk delete request.");
        BulkResponse response = bulkRequest.execute().actionGet();

        // Upon completion, if there are failures, indicate them in an exception.
        if(response.hasFailures())
            throw new SkyeException(response.buildFailureMessage());

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
