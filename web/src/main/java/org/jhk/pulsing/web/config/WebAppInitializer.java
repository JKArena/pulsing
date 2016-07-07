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

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * @author Ji Kim
 */
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer  {
    
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
        
        servletContext.setInitParameter("spring.profiles.active", "prod");
    }
    
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{ Config.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{ WebControllerConfig.class, WebSocketConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/controller/*"};
    }
    
    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter cFilter = new CharacterEncodingFilter();
        cFilter.setEncoding("UTF-8");
        cFilter.setForceEncoding(true);
        
        return new Filter[] { new HiddenHttpMethodFilter(), cFilter, new HttpPutFormContentFilter(), new ShallowEtagHeaderFilter() };
    }
    
}
