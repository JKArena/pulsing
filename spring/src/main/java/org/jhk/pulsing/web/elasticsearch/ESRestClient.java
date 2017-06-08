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
package org.jhk.pulsing.web.elasticsearch;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.jhk.pulsing.shared.util.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public class ESRestClient {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(ESRestClient.class);
    
    private final String _host;
    private final int _port;
    private final String _protocol;
    
    private RestClient _client;
    
    public ESRestClient() {
        super();
        
        _host = CommonConstants.PROJECT_POINT;
        _port = CommonConstants.ELASTICSEARCH_REST_PORT;
        _protocol = "https";
        
        init();
    }
    
    private void init() {
        _client = RestClient.builder(new HttpHost(_host, _port, _protocol)).build();
    }
    
    public Optional<String> getDocument(String index, String type, String id) {
        String endpoint = new StringJoiner("/").add(index).add(type).add(id).toString();
        
        _LOGGER.debug("ESRestClient.getDocument: " + endpoint);
        try {
            Response response = _client.performRequest("GET", endpoint);
            if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                _LOGGER.warn("ESRestClient.getDocument: Failed in retrieving document - " + response.getStatusLine());
            }else {
                HttpEntity hEntity = response.getEntity();
                String result = EntityUtils.toString(hEntity);
                
                _LOGGER.debug("ESRestClient.getDocument: Got - " + result);
                return Optional.of(result);
            }
        } catch (IOException iException) {
            iException.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    public Optional<String> performRequest(String method, String endpoint, Map<String, String> params, HttpEntity entity, Header... headers) {
        _LOGGER.debug("ESRestClient.performRequest: " + method + "-" + endpoint + ": " + params + " > " + entity);
        try {
            Response response = _client.performRequest(method, endpoint, params, entity, headers);
            if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                _LOGGER.warn("ESRestClient.performRequest: Failed - " + response.getStatusLine());
            }else {
                HttpEntity hEntity = response.getEntity();
                String result = EntityUtils.toString(hEntity);
                
                _LOGGER.debug("ESRestClient.performRequest: result - " + result);
                return Optional.of(result);
            }
        } catch (IOException iException) {
            iException.printStackTrace();
        }
        
        return Optional.empty();
    }
    
}
