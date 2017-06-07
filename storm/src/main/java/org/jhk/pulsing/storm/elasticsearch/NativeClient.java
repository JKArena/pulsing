/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jhk.pulsing.storm.elasticsearch;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;

import static org.elasticsearch.index.query.QueryBuilders.*;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.node.NodeValidationException;
import org.elasticsearch.script.Script;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.jhk.pulsing.shared.util.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ok rather than hard coding the address and port of nodes to connect to, will coordinate with a node (non data, master, or ingest) 
 * and get client information from it. The reasonining behind this is this node will appear in the cluster state nodes and can use 
 * the discovery capabilities of Elasticsearch to join to the cluster (allowing no node address to be required in connecting to a cluster).
 * Cool thing about this is the client can reduce node routing due to knowledge of cluster topology and the below dummy PluginNode allows 
 * to load Elasticsearch plugins when I look into that area later 0_0.
 * 
 * Note no mapping here, since and going to be using the streaming to just add any documents and etc with the mapping created as the norm
 * 
 * @author Ji Kim
 */
public final class NativeClient {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(NativeClient.class);
    
    private static final Settings SETTINGS;
    
    static {
        SETTINGS = Settings.builder()
                .put("path.home", "/tmp")
                .put("client.transport.sniff", true)
                .put("cluster.name", "pulsing").build();
    }
    
    private final Client _client;
    
    public NativeClient() throws NodeValidationException, UnknownHostException {
        super();
        
        _client = new PreBuiltTransportClient(SETTINGS)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(CommonConstants.PROJECT_POINT), CommonConstants.ELASTICSEARCH_NODE_PORT));
    }
    
    public IndexResponse addDocument(NativeClientDocument document) {
        _LOGGER.info("NativeClient.addDocument: " + document);

        IndexRequestBuilder irBuilder = addDocumentBuilder(document);
        IndexResponse iResponse = irBuilder.execute().actionGet();
        
        _LOGGER.info("NativeClient.addDocument: Response " + iResponse);
        
        return iResponse;
    }
    
    private IndexRequestBuilder addDocumentBuilder(NativeClientDocument document) {
        return _client.prepareIndex(document._index, document._type, document._id).setSource(document._source);
    }
    
    public GetResponse getDocument(NativeClientDocument document) {
        _LOGGER.info("NativeClient.getDocument: " + document);
        
        GetResponse gResponse = getDocumentBuilder(document).execute().actionGet();
        
        _LOGGER.info("NativeClient.getDocument: Response " + gResponse);
        
        return gResponse;
    }
    
    private GetRequestBuilder getDocumentBuilder(NativeClientDocument document) {
        return _client.prepareGet(document._index, document._type, document._id);
    }
    
    /**
     * Not sure if I wish to query from here
     * 
     * TermQueryBuilder filter = termQuery("entry", 1)
     * RangeQueryBuilder range = rangeQuery("entry").gt(500)
     * BoolQueryBuilder query = boolQuery().must(range).filter(filter);
     * 
     * @param index
     * @param type
     * @param query
     * @return
     */
    public SearchResponse query(NativeClientDocument document, QueryBuilder query) {
        _LOGGER.info("NativeClient.query: " + document + "/" + query);
        
        SearchResponse sResponse = _client.prepareSearch(document._index).setTypes(document._type).setQuery(query).execute().actionGet();
        
        _LOGGER.info("NativeClient.query: Response " + sResponse);
        
        return sResponse;
    }
    
    public UpdateResponse updateDocument(NativeClientDocument document) {
        _LOGGER.info("NativeClient.updateDocument: " + document);
        
        UpdateRequestBuilder urBuilder = updateDocumentBuilder(document);
        UpdateResponse uResponse = urBuilder.execute().actionGet();
        
        _LOGGER.info("NativeClient.updateDocument: Response " + uResponse);
        
        return uResponse;
    }
    
    private UpdateRequestBuilder updateDocumentBuilder(NativeClientDocument document) {
        UpdateRequestBuilder urBuilder = _client.prepareUpdate(document._index, document._type, document._id);
        
        StringBuilder updateScript = new StringBuilder();
        for(String field : document._updateValues.keySet()) {
            updateScript.append("ctx._source." + field + " = " + document._updateValues.get(field));
        }
        
        urBuilder.setScript(new Script(updateScript.toString()));
        return urBuilder;
    }
    
    public DeleteResponse deleteDocument(NativeClientDocument document) {
        _LOGGER.info("NativeClient.deleteDocument: " + document);
        
        DeleteResponse dResponse = deleteDocumentBuilder(document).execute().actionGet();
        
        _LOGGER.info("NativeClient.deleteDocument: Response " + dResponse);
        
        return dResponse;
    }
    
    private DeleteRequestBuilder deleteDocumentBuilder(NativeClientDocument document) {
        return _client.prepareDelete(document._index, document._type, document._id);
    }
    
    public boolean isIndexPresent(String name) {
        _LOGGER.info("NativeClient.isIndexPresent: " + name);
        
        IndicesExistsResponse response = _client.admin().indices().prepareExists(name).execute().actionGet();
        return response.isExists();
    }
    
    public void createIndex(String index) {
        _LOGGER.info("NativeClient.createIndex: " + index);
        
        _client.admin().indices().prepareCreate(index).execute().actionGet();
    }
    
    public void deleteIndex(String index) {
        _LOGGER.info("NativeClient.deleteIndex: " + index);
        
        _client.admin().indices().prepareDelete(index).execute().actionGet();
    }
    
    public void closeIndex(String index) {
        _LOGGER.info("NativeClient.closeIndex: " + index);
        
        _client.admin().indices().prepareClose(index).execute().actionGet();
    }
    
    public void openIndex(String index) {
        _LOGGER.info("NativeClient.openIndex: " + index);
        
        _client.admin().indices().prepareOpen(index).execute().actionGet();
    }
    
    public void refresIndices(String...indices) {
        _LOGGER.info("NativeClient.refresIndices: " + indices);
        
        _client.admin().indices().prepareRefresh(indices).execute().actionGet();
    }
    
    public BulkResponse bulkAdd(List<NativeClientDocument> documents) {
        _LOGGER.info("NativeClient.bulkAdd: " + documents);
        BulkRequestBuilder bulker = _client.prepareBulk();
        
        documents.stream().forEach(document -> {
            bulker.add(addDocumentBuilder(document));
        });
        
        _LOGGER.info("NativeClient.bulkAdd - numberOfActions: " + bulker.numberOfActions());
        return bulker.execute().actionGet();
    }
    
    public BulkResponse bulkUpdate(List<NativeClientDocument> documents) {
        _LOGGER.info("NativeClient.bulkUpdate: " + documents);
        BulkRequestBuilder bulker = _client.prepareBulk();
        
        documents.stream().forEach(document -> {
            bulker.add(updateDocumentBuilder(document));
        });
        
        _LOGGER.info("NativeClient.bulkUpdate - numberOfActions: " + bulker.numberOfActions());
        return bulker.execute().actionGet();
    }
    
    public BulkResponse bulkDelete(List<NativeClientDocument> documents) {
        _LOGGER.info("NativeClient.bulkDelete: " + documents);
        BulkRequestBuilder bulker = _client.prepareBulk();
        
        documents.stream().forEach(document -> {
            bulker.add(deleteDocumentBuilder(document));
        });
        
        _LOGGER.info("NativeClient.bulkDelete - numberOfActions: " + bulker.numberOfActions());
        return bulker.execute().actionGet();
    }
    
    public static class NativeClientDocument {
        
        private String _index;
        private String _type;
        private String _id;
        private String _source;
        private Map<String, String> _updateValues;
        
        public NativeClientDocument(String index, String type, String id, String source) {
            super();
            
            _index = index;
            _type = type;
            _id = id;
            _source = source;
        }
        
        public NativeClientDocument() {
            super();
        }
        
        public NativeClientDocument setIndex(String index) {
            _index = index;
            return this;
        }
        public NativeClientDocument setType(String type) {
            _type = type;
            return this;
        }
        public NativeClientDocument setId(String id) {
            _id = id;
            return this;
        }
        public NativeClientDocument setSource(String source) {
            _source = source;
            return this;
        }
        public NativeClientDocument setUpdateMap(Map<String, String> updateValues) {
            _updateValues = updateValues;
            return this;
        }
        
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("{");
            builder.append("index: " + _index + ", ");
            builder.append("type: " + _type + ", ");
            builder.append("id: " + _id + ", ");
            builder.append("source: " + _source + ", ");
            builder.append("updateValues: " + _updateValues + ", ");
            builder.append("}");
            return builder.toString();
        }
        
    }
    
}
