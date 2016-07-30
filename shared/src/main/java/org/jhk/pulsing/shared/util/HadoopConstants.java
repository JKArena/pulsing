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
package org.jhk.pulsing.shared.util;

import java.io.File;
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
public final class HadoopConstants {
    
    public static final String HDFS_URL_PORT = "hdfs://localhost:54310";
    
    public static final String MASTER_WORKSPACE;
    public static final String NEW_DATA_WORKSPACE;
    private static final String ROOT_DATA_WORKSPACE;
    
    public enum DIRECTORIES {
        TEMP, TEMP_NEW_DATA_SNAPSHOT, TEMP_SNAPSHOT, TEMP_SHREDDED, TEMP_EQUIVS_ITERATE;
    };
    
    private static final String MASTER_WORKSPACE_KEY = "MASTER_WORKSPACE";
    private static final String NEW_DATA_WORKSPACE_KEY = "NEW_DATA_WORKSPACE";
    private static final String ROOT_DATA_WORKSPACE_KEY = "ROOT_DATA_WORKSPACE";
    private static final String CONFIG_XML = "hadoop-localhost.xml";
    
    private HadoopConstants() {
        super();
    }
    
    static {
        
        Map<String, String> tempParseMap = new HashMap<>();
        
        try{
            parseSetProperties(tempParseMap);
        }catch(Exception exception){
            throw new RuntimeException("Failure in parsing of " + CONFIG_XML, exception);
        }
        
        MASTER_WORKSPACE = tempParseMap.get(MASTER_WORKSPACE_KEY);
        NEW_DATA_WORKSPACE = tempParseMap.get(NEW_DATA_WORKSPACE_KEY);
        ROOT_DATA_WORKSPACE = tempParseMap.get(ROOT_DATA_WORKSPACE_KEY);
    }
    
    public static String getWorkingDirectory(DIRECTORIES... paths) {
        if(paths == null || paths.length == 0) {
            throw new IllegalArgumentException("Can't pass null or empty");
        }
        
        StringBuilder wDirectory = new StringBuilder(ROOT_DATA_WORKSPACE);
        
        for(DIRECTORIES dir : paths) {
            wDirectory.append(dir);
            wDirectory.append(File.separator);
        }
        
        return wDirectory.toString();
    }
    
    private static void parseSetProperties(Map<String, String> tempParseMap) throws IOException, ParserConfigurationException, SAXException{
        
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        parser.parse(HadoopConstants.class.getResourceAsStream(CONFIG_XML), new DefaultHandler(){
            
            private boolean masterWorkSpace = false;
            private boolean newDataWorkSpace = false;
            private boolean rootDataWorkSpace = false;
            
            private StringBuilder nodeValue;
            
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                super.startElement(uri, localName, qName, attributes);
                
                if(qName.equals("newdata.workspace")){
                    newDataWorkSpace = true;
                }else if(qName.equals("master.workspace")) {
                    masterWorkSpace = true;
                }else if(qName.equals("rootdata.workspace")) {
                    rootDataWorkSpace = true;
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
                    tempParseMap.put(MASTER_WORKSPACE_KEY, currValue);
                    masterWorkSpace = false;
                } else if(newDataWorkSpace) {
                    tempParseMap.put(NEW_DATA_WORKSPACE_KEY, currValue);
                    newDataWorkSpace = false;
                } else if(rootDataWorkSpace) {
                    tempParseMap.put(ROOT_DATA_WORKSPACE_KEY, currValue);
                    rootDataWorkSpace = false;
                }
                
            }
            
            public void characters(char[] ch, int start, int length) throws SAXException {
                super.characters(ch, start, length);
                
                nodeValue.append(new String(ch, start, length));
            }
            
        });
        
    }
    
}
