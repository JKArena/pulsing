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
 * @author Ji Kim
 */
public final class PailUtil {
    
    private static final Logger _LOG = LoggerFactory.getLogger(PailUtil.class);
    
    private PailUtil() {
        super();
    }
    
    /**
     * Move new data to the master data
     * 
     * @param masterPail
     * @param newDataPail
     * @throws IOException
     */
    public static void ingest(Pail<?> masterPail, Pail<?> newDataPail) throws IOException {
        _LOG.info("PailUtil.ingest " + masterPail.getInstanceRoot() + " - " + newDataPail.getInstanceRoot());
        
        FileSystem fSystem = FileSystem.get(new Configuration());
        
        fSystem.delete(new Path(HadoopConstants.getTempWorkingDirectory(null)), true);
        fSystem.mkdirs(new Path(HadoopConstants.getTempWorkingDirectory(null)));
        
        Pail<?> snapShotPail = newDataPail.snapshot(HadoopConstants.getTempWorkingDirectory(TEMP_NEW_DATA_SNAPSHOT));
        appendNewData(masterPail, snapShotPail);
        newDataPail.deleteSnapshot(snapShotPail);
    }
    
    private static void appendNewData(Pail<?> masterPail, Pail<?> snapshotPail) throws IOException {
        Pail<?> shreddedPail = PailTapUtil.shred();
        masterPail.absorb(shreddedPail);
    }
    
    public static <T extends Comparable<T>> void writePailStructures(String path, AbstractThriftPailStructure<T> tpStructure,
                                                                        List<T> content) throws IOException {
        _LOG.info("PailUtil.writePailStructures " + path);
        
        Pail<T> pail = (Pail<T>) Pail.create(path, tpStructure);
        TypedRecordOutputStream out = pail.openWrite();
        
        for(T struct : content) {
            out.writeObject(struct);
        }
        
        out.close();
    }
    
    public static <T extends Comparable<T>> List<T> readPailStructures(String path, T struct) throws IOException {
        List<T> entries = new LinkedList<>();
        Pail<T> pails = new Pail<T>(path);
        
        for(T pail : pails) {
            entries.add(pail);
        }
        
        return entries;
    }
}
