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
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.jhk.pulsing.shared.util.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Ji Kim
 */
public class ESRestClient {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(ESRestClient.class);
    
    private final ObjectMapper _objectMapper = new ObjectMapper();
    private final Header[] EMPTY_HEADER = new Header[0];
    
    private final String _host;
    private final int _port;
    private final String _protocol;
    
    private RestClient _client;
    
    public ESRestClient() {
        super();
        
        _host = CommonConstants.PROJECT_POINT;
        _port = CommonConstants.ELASTICSEARCH_REST_PORT;
        _protocol = "http";
        
        init();
    }
    
    public ESRestClient(String protocol, String host, int port) {
        super();
        
        _protocol = protocol;
        _host = host;
        _port = port;
    }
    
    private void init() {
        _client = RestClient.builder(new HttpHost(_host, _port, _protocol)).build();
    }
    
    public Optional<String> getDocument(String index, String type, String id) {
        String endpoint = new StringJoiner("/").add(index).add(type).add(id).toString();
        
        try {
            Optional<Response> response = performRequest("GET", endpoint, Collections.EMPTY_MAP, null, EMPTY_HEADER);
            if(response.isPresent()) {
                HttpEntity hEntity = response.get().getEntity();
                String result = EntityUtils.toString(hEntity);
                
                _LOGGER.debug("ESRestClient.getDocument: result - " + result);
                return Optional.of(result);
            }
        } catch (IOException iException) {
            iException.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    public Optional<String> putDocument(String index, String type, long id, Map<String, String> jsonObject) {
        String endpoint = new StringJoiner("/").add(index).add(type).add(id + "").toString();
        
        _LOGGER.debug("ESRestClient.putDocument: " + endpoint + " - " + jsonObject);
        try {
            Optional<Response> response = performRequest("PUT", endpoint, Collections.EMPTY_MAP,
                    new NStringEntity(_objectMapper.writeValueAsString(jsonObject)), EMPTY_HEADER);
            
            if(response.isPresent()) {
                HttpEntity hEntity = response.get().getEntity();
                String result = EntityUtils.toString(hEntity);
                
                _LOGGER.debug("ESRestClient.putDocument: result - " + result);
                return Optional.of(result);
            }
        } catch (IOException iException) {
            iException.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    public Optional<Response> performRequest(String method, String endpoint, Map<String, String> params, HttpEntity entity, Header... headers) throws IOException {
        _LOGGER.debug("ESRestClient.performRequest: " + method + "-" + endpoint + ": " + params + " > " + entity);
        
        Response response = _client.performRequest(method, endpoint, params, entity, headers);
        if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            _LOGGER.warn("ESRestClient.performRequest: Failed - " + response.getStatusLine());
        }else {
            _LOGGER.debug("ESRestClient.performRequest: response - " + response);
            return Optional.of(response);
        }
        
        return Optional.empty();
    }
    
}
