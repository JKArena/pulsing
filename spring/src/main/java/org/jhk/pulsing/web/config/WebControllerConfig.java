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
package org.jhk.pulsing.web.config;

import java.util.List;
import java.util.UUID;

import org.apache.avro.specific.SpecificRecord;
import org.jhk.pulsing.serialization.avro.serializers.SerializationHelper;
import org.jhk.pulsing.web.controller.ChatController;
import org.jhk.pulsing.web.controller.PulseController;
import org.jhk.pulsing.web.controller.UserController;
import org.jhk.pulsing.web.controller.WebSocketController;
import org.jhk.pulsing.serialization.avro.serializers.AvroJsonSerializer;
import org.jhk.pulsing.serialization.avro.serializers.JsonAvroDeserializer;
import org.jhk.pulsing.web.serialization.StringToAvroRecordFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author Ji Kim
 */
@Configuration
@Import({WebSocketConfig.class})
public class WebControllerConfig extends WebMvcConfigurerAdapter {
    
    @Bean(name="chatController")
    public ChatController getChatController() {
        return new ChatController();
    }
    
    @Bean(name="pulseController")
    public PulseController getPulseController() {
        return new PulseController();
    }
    
    @Bean(name="userController")
    public UserController getUserController() {
        return new UserController();
    }
    
    @Bean(name="webSocketController")
    public WebSocketController getWebSocketController() {
        return new WebSocketController();
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/**");
    }
    
    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver= new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        resolver.setMaxUploadSize(100000);
        return resolver;
    }
    
    @Override
    public void addFormatters(FormatterRegistry registry) {
        super.addFormatters(registry);
        
        registry.addConverterFactory(new StringToAvroRecordFactory());
        registry.addConverter(new Converter<String, UUID>() {
            @Override
            public UUID convert(String source) {
                return UUID.fromString(source);
            }
        });
    }
    
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
    
    
    
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
        
        final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        
        SerializationHelper.getAvroRecordStream()
            .forEach(avroRecord -> {
                Class<? extends SpecificRecord> clazz = avroRecord.getClazz();
                builder.deserializerByType(clazz, new JsonAvroDeserializer<>(clazz, avroRecord.getSchema()));
                builder.serializerByType(clazz, new AvroJsonSerializer(clazz));
            });
        
        converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
        converters.add(new StringHttpMessageConverter());
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new FormHttpMessageConverter());
    }
    
}
