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
package org.jhk.interested.hadoop.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Ji Kim
 */
public final class Constants {
    
    public static final boolean IS_WINDOWS_SYSTEM;
    
    public static final String PAIL_MASTER_WORKSPACE;
    private static final String PAIL_TEMP_WORKSPACE;
    
    public enum DIRECTORIES {
        TEMP_NEW_DATA_SNAPSHOT, TEMP_SNAPSHOT, TEMP_SHREDDED, TEMP_EQUIVS_ITERATE;
    };
    
    private static final String MASTER_WORKSPACE_KEY = "MASTER_WORKSPACE";
    private static final String TEMP_WORKSPACE_KEY = "TEMP_WORKSPACE";
    private static final String CONFIG_XML = "config.xml";
    
    static {
        
        IS_WINDOWS_SYSTEM = (System.getProperty("line.separator").equals("\r\n"));
        
        Map<String, String> tempParseMap = new HashMap<>();
        
        try{
            parseSetProperties(tempParseMap);
        }catch(Exception exception){
            throw new RuntimeException("Failure in parsing of " + CONFIG_XML, exception);
        }
        
        PAIL_MASTER_WORKSPACE = tempParseMap.get(MASTER_WORKSPACE_KEY);
        PAIL_TEMP_WORKSPACE = tempParseMap.get(TEMP_WORKSPACE_KEY);
    }
    
    private Constants() {
        super();
    }
    
    public static String getTempWorkingDirectory(DIRECTORIES directory) {
        if(directory == null) {
            return PAIL_TEMP_WORKSPACE;
        }
        return PAIL_TEMP_WORKSPACE + directory;
    }
    
    private static void parseSetProperties(Map<String, String> tempParseMap) throws IOException, ParserConfigurationException, SAXException{
        
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        parser.parse(Constants.class.getResourceAsStream(CONFIG_XML), new DefaultHandler(){
            
            private boolean windows = false;
            private boolean nonWindows = false;
            
            private boolean masterWorkSpace = false;
            private boolean tempWorkSpace = false;
            
            private StringBuilder nodeValue;
            
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                super.startElement(uri, localName, qName, attributes);
                
                if(qName.equals("windows")){
                    windows = true;
                }else if(qName.equals("non-windows")){
                    windows = false;
                    nonWindows = true;
                }else if(qName.equals("temp-workspace")){
                    tempWorkSpace = true;
                }else if(qName.equals("master-workspace")) {
                    masterWorkSpace = true;
                }
                
                nodeValue = new StringBuilder();
            }
            
            public void endElement(String uri, String localName, String qName) throws SAXException {
                super.endElement(uri, localName, qName);
                
                String currValue = null;
                if(nodeValue != null){
                    currValue = nodeValue.toString().trim();
                }
                
                if(masterWorkSpace) {
                    if((windows && IS_WINDOWS_SYSTEM) || (nonWindows && !IS_WINDOWS_SYSTEM)) {
                        tempParseMap.put(MASTER_WORKSPACE_KEY, currValue);
                    }
                    masterWorkSpace = false;
                } else if(tempWorkSpace){
                    if((windows && IS_WINDOWS_SYSTEM) || (nonWindows && !IS_WINDOWS_SYSTEM)) {
                        tempParseMap.put(TEMP_WORKSPACE_KEY, currValue);
                    }
                    tempWorkSpace = false;
                }else if(windows){
                    windows = false;
                }else if(nonWindows){
                    nonWindows = false;
                }
            }
            
            public void characters(char[] ch, int start, int length) throws SAXException {
                super.characters(ch, start, length);
                
                nodeValue.append(new String(ch, start, length));
            }
            
        });
        
    }
    
}
