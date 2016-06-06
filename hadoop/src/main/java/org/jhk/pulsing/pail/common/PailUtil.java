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
import org.jhk.pulsing.hadoop.common.Constants;
import static org.jhk.pulsing.hadoop.common.Constants.DIRECTORIES.*;

import com.backtype.hadoop.pail.Pail;
import com.backtype.hadoop.pail.Pail.TypedRecordOutputStream;

/**
 * @author Ji Kim
 */
public final class PailUtil {
    
    private PailUtil() {
        super();
    }
    
    /**
     * When moving new data to the master data
     * 
     * @param masterPail
     * @param newDataPail
     * @throws IOException
     */
    public static void ingest(Pail masterPail, Pail newDataPail) throws IOException {
        FileSystem fSystem = FileSystem.get(new Configuration());
        
        fSystem.delete(new Path(Constants.getTempWorkingDirectory(null)), true);
        fSystem.mkdirs(new Path(Constants.getTempWorkingDirectory(null)));
        
        Pail snapShotPail = newDataPail.snapshot(Constants.getTempWorkingDirectory(TEMP_NEW_DATA_SNAPSHOT));
        appendNewData(masterPail, snapShotPail);
        newDataPail.deleteSnapshot(snapShotPail);
    }
    
    private static void appendNewData(Pail masterPail, Pail snapshotPail) throws IOException {
        Pail shreddedPail = PailTapUtil.shred();
        masterPail.absorb(shreddedPail);
    }
    
    public static <T extends Comparable<T>> void writePailStructures(String path, ThriftPailStructure<T> tpStructure,
                                                                        List<T> content) throws IOException {
        Pail<T> pail = (Pail<T>) Pail.create(path, tpStructure);
        TypedRecordOutputStream out = pail.openWrite();
        
        for(T trav : content) {
            out.writeObject(trav);
        }
        
        out.close();
    }
    
    public static <T extends Comparable<T>> List<T> readPailStructures(String path, T struct) throws IOException {
        List<T> entries = new LinkedList<>();
        Pail<T> pails = new Pail<T>(path);
        
        for(T entry : pails) {
            entries.add(entry);
        }
        
        return entries;
    }
}
