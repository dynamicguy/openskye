package org.openskye.metadata.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.SkyeException;
import org.openskye.core.SkyeSession;
import org.openskye.domain.Project;
import org.openskye.metadata.ObjectMetadataSearch;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

/**
 * An implementation of OMS that uses ElasticSearch
 */
@Slf4j
public class ElasticSearchObjectMetadataSearch implements ObjectMetadataSearch {

    private static final SearchType SEARCH_TYPE = SearchType.QUERY_THEN_FETCH;
    private Client client = nodeBuilder().local(true).node().client();
    @Inject
    private SkyeSession session;
    @Inject
    private ObjectMapper objectMapper;

    @Override
    public Iterable<ObjectMetadata> search(String query) {
        List<ObjectMetadata> listMetadata = new ArrayList<>();

        try {
            SearchResponse response = this.client.prepareSearch()
                    .setIndices(session.getDomain().getId())
                    .setSearchType(SEARCH_TYPE)
                    .setQuery(new QueryStringQueryBuilder(query))
                    .execute()
                    .actionGet();
            SearchHits searchHits = response.getHits();

            for (SearchHit hit : searchHits) {
                String json = hit.getSourceAsString();
                JsonObjectMetadata jsonMetadata;

                try {
                    jsonMetadata = this.objectMapper.readValue(json, JsonObjectMetadata.class);
                } catch (IOException ex) {
                    throw new SkyeException("Failed to demarshal ObjectMetadata form JSON.", ex);
                }

                listMetadata.add(jsonMetadata.toObjectMetadata());
            }


        } catch (IndexMissingException ex) {
            log.warn("Attempt to search on index that doesn't exist (domain:" + session.getDomain().getId() + ")");
        }
        return listMetadata;

    }

    @Override
    public Iterable<ObjectMetadata> search(Project project, String query) {
        List<ObjectMetadata> listMetadata = new ArrayList<>();
        try {
            SearchResponse response = client.prepareSearch()
                    .setIndices(session.getDomain().getId())
                    .setTypes(project.getId())
                    .setSearchType(SEARCH_TYPE)
                    .setQuery(new QueryStringQueryBuilder(query))
                    .execute()
                    .actionGet();

            SearchHits searchHits = response.getHits();

            for (SearchHit hit : searchHits) {
                String json = hit.getSourceAsString();
                JsonObjectMetadata jsonMetadata;

                try {
                    jsonMetadata = this.objectMapper.readValue(json, JsonObjectMetadata.class);
                } catch (IOException ex) {
                    throw new SkyeException("Failed to demarshal ObjectMetadata from JSON.", ex);
                }

                listMetadata.add(jsonMetadata.toObjectMetadata());
            }
        } catch (IndexMissingException ex) {
            log.warn("Attempt to search on index that doesn't exist (domain:" + session.getDomain().getId() + ")");
        }

        return listMetadata;
    }

    @Override
    public void index(ObjectMetadata objectMetadata) {
        JsonObjectMetadata metadata = new JsonObjectMetadata(objectMetadata);
        String domainId = this.session.getDomain().getId();
        String projectId = objectMetadata.getProject().getId();

        try {
            String json = this.objectMapper.writeValueAsString(metadata);
            client.prepareIndex(domainId, projectId, objectMetadata.getId())
                    .setSource(json)
                    .execute()
                    .actionGet();
        } catch (JsonProcessingException ex) {
            throw new SkyeException("Failed to marshal ObjectMetadata as JSON.", ex);
        }


    }

    /**
     * Clears the indexed items for the OMS.
     * <p/>
     * This is intended for testing and demo purposes.
     */
    @Override
    public void clear() {
        this.client.prepareDeleteByQuery(this.getIndexNames())
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
}
