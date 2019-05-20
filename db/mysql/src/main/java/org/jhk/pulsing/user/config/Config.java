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
package org.jhk.pulsing.user.config;

import java.sql.SQLException;
import java.util.Properties;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.jhk.pulsing.db.mysql.user.MySqlUserDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Ji Kim
 */
@EnableTransactionManagement
@Configuration
@PropertySource({"classpath:application.properties"})
public class Config {

    @Inject
    private Environment env;
    
    @Bean(name="mySqlUserDao")
    public MySqlUserDao getMySqlUserDao() {
        return new MySqlUserDao();
    }
    
    @Bean
    public PlatformTransactionManager transactionManager() throws SQLException {
        HibernateTransactionManager tManager = new HibernateTransactionManager();
        tManager.setSessionFactory(sessionFactory().getObject()); 
        return tManager;
    }
    
    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sFactory = new LocalSessionFactoryBean();
        sFactory.setDataSource(dataSource());
        sFactory.setPackagesToScan(new String[] {"org.jhk.pulsing.db.mysql.model"});
        sFactory.setHibernateProperties(hibernateProperties());
        return sFactory;
    }
    
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getRequiredProperty("datasource.driver_class"));
        dataSource.setUrl(env.getRequiredProperty("datasource.url"));
        dataSource.setUsername(env.getRequiredProperty("datasource.user"));
        dataSource.setPassword(env.getRequiredProperty("datasource.password"));
        return dataSource;
    }
    
    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", env.getRequiredProperty("hibernate.dialect"));
        properties.put("hibernate.hbm2ddl.auto", env.getRequiredProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.show_sql", env.getRequiredProperty("hibernate.show_sql"));
        properties.put("hibernate.format_sql", env.getRequiredProperty("hibernate.format_sql"));
        properties.put("hibernate.cache.provider_class", env.getRequiredProperty("hibernate.cache.provider_class"));
        properties.put("hibernate.archive.autodetection", env.getRequiredProperty("hibernate.archive.autodetection"));
        properties.put("hibernate.c3p0.min_size", env.getRequiredProperty("hibernate.c3p0.min_size"));
        properties.put("hibernate.c3p0.max_size", env.getRequiredProperty("hibernate.c3p0.max_size"));
        properties.put("hibernate.c3p0.timeout", env.getRequiredProperty("hibernate.c3p0.timeout"));
        properties.put("hibernate.c3p0.max_statements", env.getRequiredProperty("hibernate.c3p0.max_statements"));
        properties.put("hibernate.c3p0.idle_test_period", env.getRequiredProperty("hibernate.c3p0.idle_test_period"));
        properties.put("hibernate.id.new_generator_mappings", env.getRequiredProperty("hibernate.id.new_generator_mappings"));
        return properties;
    }
    
}
