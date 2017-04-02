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

import java.util.Collections;
import java.util.List;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;

import static org.elasticsearch.index.query.QueryBuilders.*;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.elasticsearch.node.internal.InternalSettingsPreparer;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.script.Script;
import org.elasticsearch.transport.Netty4Plugin;
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
                .put("cluster.name", "pulsing")
                .put("node.data", false)
                .put("node.master", false)
                .put("node.ingest", false).build();
    }
    
    private final Node _node;
    private final Client _client;
    
    public NativeClient() throws NodeValidationException {
        super();
        
        _node = new PluginNode(SETTINGS, Collections.<Class<? extends Plugin>>singletonList(Netty4Plugin.class));
        _node.start();
        _client = _node.client();
    }
    
    public IndexResponse addDocument(String index, String type, String id, String source) {
        _LOGGER.info("NativeClient.addDocument: " + index + "/" + type + "/" + id + " - " + source);

        IndexRequestBuilder irBuilder = _client.prepareIndex(index, type, id);
        
        irBuilder.setSource(source);
        
        IndexResponse iResponse = irBuilder.execute().actionGet();
        
        _LOGGER.info("NativeClient.addDocument: Response " + iResponse);
        
        return iResponse;
    }
    
    public GetResponse getDocument(String index, String type, String id) {
        _LOGGER.info("NativeClient.getDocument: " + index + "/" + type + "/" + id);
        
        GetResponse gResponse = _client.prepareGet(index, type, id).execute().actionGet();
        
        _LOGGER.info("NativeClient.getDocument: Response " + gResponse);
        
        return gResponse;
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
    public SearchResponse query(String index, String type, QueryBuilder query) {
        _LOGGER.info("NativeClient.query: " + index + "/" + type + "/" + query);
        
        SearchResponse sResponse = _client.prepareSearch(index).setTypes(type).setQuery(query).execute().actionGet();
        
        _LOGGER.info("NativeClient.query: Response " + sResponse);
        
        return sResponse;
    }
    
    public UpdateResponse updateDocument(String index, String type, String id, String key, String value) {
        _LOGGER.info("NativeClient.updateDocument: " + index + "/" + type + "/" + id + " - " + key + "/" + value);
        
        UpdateRequestBuilder urBuilder = _client.prepareUpdate(index, type, id)
                .setScript(new Script("ctx._source." + key + " = " + value));
        
        UpdateResponse uResponse = urBuilder.execute().actionGet();
        
        _LOGGER.info("NativeClient.updateDocument: Response " + uResponse);
        
        return uResponse;
    }
    
    public DeleteResponse deleteDocument(String index, String type, String id) {
        _LOGGER.info("NativeClient.deleteDocument: " + index + "/" + type + "/" + id);
        
        DeleteResponse dResponse = _client.prepareDelete(index, type, id).execute().actionGet();
        
        _LOGGER.info("NativeClient.deleteDocument: Response " + dResponse);
        
        return dResponse;
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
    
    private static class PluginNode extends Node {
        public PluginNode(Settings settings, List<Class<? extends Plugin>> plugins) {
            super(InternalSettingsPreparer.prepareEnvironment(settings, null), plugins);
        }
    }
    
}
