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
import java.util.concurrent.CompletableFuture;

import org.apache.avro.specific.SpecificRecord;
import org.jhk.pulsing.client.okhttp.interceptors.LoggingInterceptor;
import org.jhk.pulsing.client.payload.Result;
import org.jhk.pulsing.client.payload.Result.CODE;
import org.jhk.pulsing.serialization.avro.serializers.AvroJsonSerializer;
import org.jhk.pulsing.serialization.avro.serializers.JsonAvroDeserializer;
import org.jhk.pulsing.serialization.avro.serializers.SerializationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Ji Kim
 */
public abstract class AbstractService {

    private static final Logger _LOGGER = LoggerFactory.getLogger(AbstractService.class);

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;

    private final ObjectMapper mapper = new ObjectMapper();
    
    protected AbstractService() {
        client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new LoggingInterceptor())
                .build();
        
        SimpleModule module = new SimpleModule();
        SerializationHelper.getAvroRecordStream()
        .forEach(avroRecord -> {
            Class<? extends SpecificRecord> clazz = avroRecord.getClazz();
            module.addDeserializer(clazz, new JsonAvroDeserializer<>(clazz, avroRecord.getSchema()));
            module.addSerializer(clazz, new AvroJsonSerializer(clazz));
        });
        
        mapper.registerModule(module);
    }
    
    protected OkHttpClient getClient() {
        return client;
    }
    
    protected ObjectMapper getMapper() {
        return mapper;
    }
    
    protected <T> Result<T> synchronousResponse(Request request, IResponseDeserializer<T> deserializer) {
        try (Response response = client.newCall(request).execute()) {

            return responseProcessor(response, deserializer);
        } catch (Exception exception) {
            return new Result<>(exception);
        }
    }
    
    protected <T> CompletableFuture<Result<T>> asynchronousResponse(Request request, IResponseDeserializer<T> deserializer) {
        CompletableFuture<Result<T>> wrapper = new CompletableFuture<>();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException io) {
                _LOGGER.error("Failure while processing request {} with call {}", request, call, io);
                wrapper.complete(new Result<>(io));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                wrapper.complete(responseProcessor(response, deserializer));
            }
        });
        
        return wrapper;
    }
    
    private <T> Result<T> responseProcessor(Response response, IResponseDeserializer<T> deserializer) {
        if (!response.isSuccessful()) {
            return new Result<>(new IOException(String.format("Unexpected code %s", response)));
        }

        return new Result<>(CODE.SUCCESS, deserializer.deserialize(response));
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
