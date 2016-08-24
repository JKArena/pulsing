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
package org.jhk.pulsing.pail.common;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backtype.hadoop.pail.Pail;
import com.backtype.hadoop.pail.Pail.TypedRecordOutputStream;

import org.jhk.pulsing.shared.util.HadoopConstants;
import static org.jhk.pulsing.shared.util.HadoopConstants.DIRECTORIES.*;

import org.jhk.pulsing.pail.thrift.AbstractThriftPailStructure;

/**
 * Some of the things from Nathan Marz, since not distributed in repo with  
 * changes for cascade 2.5.x and hadoop
 */
public final class PailUtil {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(PailUtil.class);
    
    /**
     * Move new data to the master data
     * 
     * @param masterPail
     * @param newDataPail
     * @throws IOException
     */
    public static void ingest(Pail<?> masterPail, Pail<?> newDataPail) throws IOException {
        _LOGGER.debug("PailUtil.ingest " + masterPail.getInstanceRoot() + " - " + newDataPail.getInstanceRoot());
        
        FileSystem fSystem = FileSystem.get(new Configuration());
        
        /*
         * In order to avoid race condition:
         * 1) Create a temp directory
         * 2) Create a snapshot of the newDataPail
         * 3) Delete the snapshot (deleteSnapShot removes from the original pail only the data that exists in the snapshot)
         */
        fSystem.delete(new Path(HadoopConstants.getWorkingDirectory(TEMP)), true);
        fSystem.mkdirs(new Path(HadoopConstants.getWorkingDirectory(TEMP)));
        
        String tempSnapShotPath = HadoopConstants.getWorkingDirectory(TEMP, SNAPSHOT);
        Pail<?> snapShotPail = newDataPail.snapshot(tempSnapShotPath);
        
        appendNewData(tempSnapShotPath, HadoopConstants.getWorkingDirectory(TEMP, SHREDDED), masterPail);
        newDataPail.deleteSnapshot(snapShotPail);
    }
    
    
    /**
     * Master dataset is vertically partitioned by property or edge type.
     * As the new data/pail is a dumping ground for new data, so each file may contain data units of all property types 
     * and edges. Before this data can be appended to the master dataset, it must first be reorganized to be consistent 
     * with the structure used for the master dataset pail (need to shred it).
     * 
     * @param sourcePath - i.e. new data path
     * @param shredPath
     * @param sinkPail - i.e. master data
     * @throws IOException
     */
    private static void appendNewData(String sourcePath, String shredPath, Pail<?> sinkPail) throws IOException {
        Pail<?> shreddedPail = PailTapUtil.shred(sourcePath, shredPath);
        sinkPail.absorb(shreddedPail);
    }
    
    public static <T extends Comparable<T>> void writePailStructures(String path, AbstractThriftPailStructure<T> tpStructure,
                                                                        List<T> content) throws IOException {
        _LOGGER.debug("PailUtil.writePailStructures " + path);
        
        Pail<T> pail = (Pail<T>) Pail.create(path, tpStructure);
        TypedRecordOutputStream out = pail.openWrite();
        
        for(T struct : content) {
            out.writeObject(struct);
        }
        
        out.close();
    }
    
    public static <T extends Comparable<T>> List<T> readPailStructures(String path, T struct) throws IOException {
        _LOGGER.debug("PailUtil.readPailStructures " + path + " : " + struct);
        
        List<T> entries = new LinkedList<>();
        Pail<T> pails = new Pail<T>(path);
        
        for(T pail : pails) {
            entries.add(pail);
        }
        
        return entries;
    }
    
    private PailUtil() {
        super();
    }
}
