package org.skye.metadata.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.skye.core.ObjectMetadata;
import org.skye.core.SkyeException;
import org.skye.core.SkyeSession;
import org.skye.domain.Domain;
import org.skye.domain.Project;
import org.skye.metadata.ObjectMetadataSearch;
import org.skye.util.Page;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of
 */
public class ElasticSearchObjectMetadataSearch implements ObjectMetadataSearch
{

    @Inject
    Client client;

    @Inject
    SkyeSession session;

    @Inject
    ObjectMapper objectMapper;

    protected static final SearchType SEARCH_TYPE = SearchType.DFS_QUERY_THEN_FETCH;

    @Override
    public Iterable<ObjectMetadata> search(Domain domain, String query, Page page)
    {
        int from = (int) (((page.getPageNumber() - 1) * page.getPageSize()) + 1);
        int size = (int) page.getPageSize();
        List<ObjectMetadata> listMetadata = new ArrayList<>();
        SearchResponse response = client.prepareSearch(domain.getId())
                                        .setSearchType(SEARCH_TYPE)
                                        .setFrom(from).setSize(size)
                                        .execute()
                                        .actionGet();
        SearchHits searchHits = response.getHits();

        for(SearchHit hit : searchHits)
        {
            String json = hit.getSourceAsString();
            JsonObjectMetadata jsonMetadata;

            try
            {
                jsonMetadata = this.objectMapper.readValue(json, JsonObjectMetadata.class);
            }
            catch (IOException ex)
            {
                throw new SkyeException("Failed to demarshal ObjectMetadata from JSON.", ex);
            }

            listMetadata.add(jsonMetadata.toObjectMetadata());
        }

        return listMetadata;
    }

    @Override
    public Iterable<ObjectMetadata> search(Domain domain, Project project, String query, Page page)
    {
        int from = (int) (((page.getPageNumber() - 1) * page.getPageSize()) + 1);
        int size = (int) page.getPageSize();
        List<ObjectMetadata> listMetadata = new ArrayList<>();
        SearchResponse response = client.prepareSearch(domain.getId())
                                        .setTypes(project.getId())
                                        .setSearchType(SEARCH_TYPE)
                                        .setFrom(from).setSize(size)
                                        .execute()
                                        .actionGet();
        SearchHits searchHits = response.getHits();

        for(SearchHit hit : searchHits)
        {
            String json = hit.getSourceAsString();
            JsonObjectMetadata jsonMetadata;

            try
            {
                jsonMetadata = this.objectMapper.readValue(json, JsonObjectMetadata.class);
            }
            catch (IOException ex)
            {
                throw new SkyeException("Failed to demarshal ObjectMetadata from JSON.", ex);
            }

            listMetadata.add(jsonMetadata.toObjectMetadata());
        }

        return listMetadata;
    }

    @Override
    public void index(ObjectMetadata objectMetadata)
    {
        JsonObjectMetadata metadata = new JsonObjectMetadata(objectMetadata);
        String json;
        IndexResponse response;
        String domainId = this.session.getDomain().getId();
        String projectId = objectMetadata.getProject().getId();

        try
        {
            json = this.objectMapper.writeValueAsString(metadata);
        }
        catch(JsonProcessingException ex)
        {
            throw new SkyeException("Failed to marshal ObjectMetadata as JSON.", ex);
        }

        response = client.prepareIndex(domainId, projectId, objectMetadata.getId())
                         .setSource(json)
                         .execute()
                         .actionGet();
    }
}
