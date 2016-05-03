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
package org.jhk.interested.web.config;

import java.util.List;

import org.jhk.interested.serialization.avro.Interest;
import org.jhk.interested.web.controller.InterestController;
import org.jhk.interested.web.serialization.AvroJsonSerializer;
import org.jhk.interested.web.serialization.JsonAvroDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author Ji Kim
 */
@Configuration
@EnableWebMvc
public class WebControllerConfig extends WebMvcConfigurerAdapter {
    
    @Bean(name="interestController")
    public InterestController getInterestController() {
        return new InterestController();
    }
    
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.deserializerByType(Interest.class, new JsonAvroDeserializer<>(Interest.class, Interest.getClassSchema()));
        builder.serializerByType(Interest.class, new AvroJsonSerializer<Interest>(Interest.class));
        converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
    }
    
}
