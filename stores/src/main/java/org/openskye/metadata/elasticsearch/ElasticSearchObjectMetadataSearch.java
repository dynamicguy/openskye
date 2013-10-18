package org.openskye.metadata.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.SkyeException;
import org.openskye.core.SkyeSession;
import org.openskye.domain.Domain;
import org.openskye.domain.Project;
import org.openskye.metadata.ObjectMetadataSearch;
import org.openskye.util.Page;

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
                                        .setQuery(query)
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
                                        .setQuery(query)
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

        client.prepareIndex(domainId, projectId, objectMetadata.getId())
              .setSource(json)
              .execute()
              .actionGet();
    }
}
