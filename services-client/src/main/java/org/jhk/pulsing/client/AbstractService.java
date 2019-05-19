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
package org.jhk.pulsing.client;

import java.io.IOException;
import java.util.function.Function;

import org.jhk.pulsing.client.payload.Result;
import org.jhk.pulsing.client.payload.Result.CODE;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Ji Kim
 */
public abstract class AbstractService {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    
    private final OkHttpClient client = new OkHttpClient();
    
    private final ObjectMapper mapper = new ObjectMapper();
    
    protected AbstractService() {
    }
    
    protected OkHttpClient getClient() {
        return client;
    }
    
    protected ObjectMapper getMapper() {
        return mapper;
    }
    
    protected <T> Result<T> synchronousResponse(Request request, IResponseDeserializer<T> deserializer) {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return new Result<>(new IOException(String.format("Unexpected code %s", response)));
            }

            return new Result<>(CODE.SUCCESS, deserializer.deserialize(response));
        } catch (Exception exception) {
            return new Result<>(exception);
        }
    }
    
    protected Request.Builder getBaseRequestBuilder(String pathSegment) {
        HttpUrl.Builder httpUrlBuilder = new HttpUrl.Builder();
        httpUrlBuilder.scheme("https")
            .host(getBaseUrl())
            .addPathSegment(pathSegment);
        
        Request.Builder builder = new Request.Builder();
        builder.url(httpUrlBuilder.build());
        return builder;
    }
    
    protected abstract String getBaseUrl();
    
}
