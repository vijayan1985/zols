/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.elasticsearch;

import com.fasterxml.jackson.databind.util.BeanUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import static org.elasticsearch.common.settings.Settings.settingsBuilder;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import static org.slf4j.LoggerFactory.getLogger;
import org.zols.datastore.DataStore;
import org.zols.datastore.jsonschema.JSONSchema;
import org.zols.datastore.query.Filter;
import static org.zols.datastore.query.Filter.Operator.EQUALS;
import org.zols.datastore.query.Query;
import org.zols.datatore.exception.DataStoreException;

/**
 *
 * Elastic Search Implementation of DataStore
 *
 */
public class ElasticSearchDataStore extends DataStore {

    private static final org.slf4j.Logger LOGGER = getLogger(ElasticSearchDataStore.class);

    private final Client client;

    private final String indexName;

    public ElasticSearchDataStore() {
        this("zols", null);
    }

    public ElasticSearchDataStore(String indexName) {
        this(indexName, null);
    }

    public ElasticSearchDataStore(String indexName, Client client) {
        this.indexName = indexName;
        if (client == null) {
            Settings settings = settingsBuilder()
                    .put("path.home", "/")
                    .put("path.data", "data")
                    .put("index.number_of_replicas", 0).build();
            this.client = nodeBuilder().settings(settings).local(true).build().start().client();
        } else {
            this.client = client;
        }

        createIndexIfNotExists();
    }

    private void createIndexIfNotExists() {
        IndicesExistsRequest indicesExistsRequest = new IndicesExistsRequest(indexName);
        if (!client.admin().indices().exists(indicesExistsRequest).actionGet().isExists()) {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
            client.admin().indices().create(createIndexRequest).actionGet();
            LOGGER.debug("New index [{}] created", indexName);
        }
    }

    @Override
    protected Map<String, Object> create(JSONSchema jsonSchema, Map<String, Object> validatedDataObject) {
        LOGGER.debug("Create Data for ", jsonSchema.baseType());
        Object idValue = validatedDataObject.get(jsonSchema.idField());
        IndexRequestBuilder indexRequestBuilder;
        if (idValue == null) {
            indexRequestBuilder = client.prepareIndex(indexName, jsonSchema.baseType()).setRefresh(true);
        } else {
            indexRequestBuilder = client.prepareIndex(indexName, jsonSchema.baseType(), idValue.toString()).setRefresh(true);
        }

        IndexResponse response = indexRequestBuilder
                .setSource(validatedDataObject)
                .execute()
                .actionGet();

        Map<String, Object> createdData = null;

        if (response.isCreated()) {
            createdData = read(jsonSchema, response.getId());
            //If autogenerated if then update idField
            if (idValue == null) {
                createdData.put(jsonSchema.idField(), response.getId());
                update(jsonSchema, createdData);
            }
        }

        return createdData;

    }

    @Override
    protected Map<String, Object> read(JSONSchema jsonSchema, String idValue) {
        GetResponse getResponse = client
                .prepareGet(indexName, jsonSchema.baseType(), idValue)
                .execute()
                .actionGet();

        return getResponse.getSource();
    }

    @Override
    protected boolean delete(JSONSchema jsonSchema) {
        SearchResponse response = client
                .prepareSearch()
                .setSearchType(SearchType.SCAN)
                .setIndices(indexName)
                .setTypes(jsonSchema.baseType())
                .setScroll(new TimeValue(60000))
                .setSize(100)
                .execute()
                .actionGet();
        while (true) {
            response = client.prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(600000)).execute().actionGet();
            boolean hitsRead = false;
            for (SearchHit hit : response.getHits()) {
                hitsRead = true;
                client
                        .prepareDelete(indexName, hit.getType(), hit.getId()).setRefresh(true)
                        .execute()
                        .actionGet();
            }
            //Break condition: No hits are returned
            if (!hitsRead) {
                break;
            }
        }
        return true;
    }

    @Override
    protected boolean delete(JSONSchema jsonSchema, String idValue) {
        DeleteResponse response = client
                .prepareDelete(indexName, jsonSchema.baseType(), idValue).setRefresh(true)
                .execute()
                .actionGet();
        client.admin().indices().refresh(new RefreshRequest(indexName));

        return response.isFound();
    }

    @Override
    protected boolean delete(JSONSchema jsonSchema, Query query) {
        SearchResponse response = client
                .prepareSearch()
                .setSearchType(SearchType.SCAN)
                .setIndices(indexName)
                .setTypes(jsonSchema.baseType())
                .setQuery(getQueryBuilder(query))
                .setScroll(new TimeValue(60000))
                .setSize(100)
                .execute()
                .actionGet();
        while (true) {
            response = client.prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(600000)).execute().actionGet();
            boolean hitsRead = false;
            for (SearchHit hit : response.getHits()) {
                hitsRead = true;
                client
                        .prepareDelete(indexName, hit.getType(), hit.getId()).setRefresh(true)
                        .execute()
                        .actionGet();
            }
            //Break condition: No hits are returned
            if (!hitsRead) {
                break;
            }
        }
        return true;
    }

    @Override
    protected boolean update(JSONSchema jsonSchema, Map<String, Object> validatedDataObject) {
        String idValue = validatedDataObject.get(jsonSchema.idField()).toString();
        IndexResponse response = client.prepareIndex(indexName, jsonSchema.baseType(), idValue).setRefresh(true)
                .setSource(validatedDataObject)
                .execute()
                .actionGet();
        client.admin().indices().refresh(new RefreshRequest(indexName));

        return response.isCreated();
    }

    @Override
    protected List<Map<String, Object>> list(JSONSchema jsonSchema) {
        List<Map<String, Object>> list = null;
        SearchResponse response = client
                .prepareSearch()
                .setIndices(indexName)
                .setTypes(jsonSchema.baseType())
                .execute().actionGet();
        SearchHits hits = response.getHits();
        if (hits != null) {
            SearchHit[] searchHits = hits.getHits();
            if (searchHits != null) {
                list = new ArrayList<>(searchHits.length);
                for (SearchHit searchHit : searchHits) {
                    list.add(searchHit.getSource());
                }
            }
        }
        return list;
    }

    @Override
    protected List<Map<String, Object>> list(JSONSchema jsonSchema, Query query) {
        List<Map<String, Object>> list = null;
        SearchResponse response = client
                .prepareSearch()
                .setIndices(indexName)
                .setTypes(jsonSchema.baseType())
                .setQuery(getQueryBuilder(query))
                .execute()
                .actionGet();
        SearchHits hits = response.getHits();
        if (hits != null) {
            SearchHit[] searchHits = hits.getHits();
            if (searchHits != null) {
                list = new ArrayList<>(searchHits.length);
                for (SearchHit searchHit : searchHits) {
                    list.add(searchHit.getSource());
                }
            }
        }
        return list;
    }

    @Override
    protected void drop() throws DataStoreException {
        client.admin().indices().delete(new DeleteIndexRequest(indexName));
    }

    public static QueryBuilder getQueryBuilder(Query query) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (query != null) {
            List<Filter> queries = query.getFilters();
            if (queries != null) {
                int size = queries.size();
                Filter filter;
                for (int index = 0; index < size; index++) {
                    filter = queries.get(index);
                    switch (filter.getOperator()) {
                        case EQUALS:
                            queryBuilder.must(QueryBuilders.matchQuery(filter.getName(), filter.getValue()));
                            break;
                        case IS_NULL:
                            queryBuilder.must(QueryBuilders.missingQuery(filter.getName()));
                            break;
                        case IS_NOTNULL:
                            queryBuilder.must(QueryBuilders.existsQuery(filter.getName()));
                            break;
                        case EXISTS_IN:
                            Collection collection = (Collection) filter.getValue();
                            for (Object object : collection) {
                                queryBuilder.should(QueryBuilders.matchQuery(filter.getName(), object));
                            }
                            break;
                        case IN_BETWEEN:
                            Object[] rangeValues = (Object[]) filter.getValue();
                            queryBuilder.must(QueryBuilders.rangeQuery(filter.getName())
                                    .from(rangeValues[0])
                                    .to(rangeValues[1]));
                            break;
                    }
                }
            }
        }
        LOGGER.debug("Executing elastic search query {}", queryBuilder.toString());
        return queryBuilder;
    }

}
