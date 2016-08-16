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
package com.backtype.pail.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backtype.cascading.tap.PailTap;
import com.backtype.cascading.tap.PailTap.PailTapOptions;
import com.backtype.hadoop.pail.Pail;
import com.backtype.hadoop.pail.PailSpec;

import cascalog.ops.IdentityBuffer;
import cascalog.ops.RandLong;
import jcascalog.Api;
import jcascalog.Subquery;

import org.jhk.pulsing.pail.thrift.structures.SplitDataPailstructure;
import org.jhk.pulsing.serialization.thrift.data.DataUnit;

/**
 * Some of the things from Nathan Marz, since not distributed in repo with  
 * changes for cascade 2.5.x and minor tweaks
 */
public final class PailTapUtil {
    
    private static final Logger _LOG = LoggerFactory.getLogger(PailTapUtil.class);
    
    /**
     * Returns reading a subset of the data within the pail
     * 
     * @param path
     * @param fields
     * @return
     */
    @SuppressWarnings("unchecked")
    public static PailTap attributetap(String path, final DataUnit._Fields... fields) {
        _LOG.info("PailTapUtil.attributetap " + path + " : " + fields);
        
        PailTapOptions options = new PailTapOptions();
        
        options.attrs = new List[] {
                new ArrayList<String>() {{
                    for(DataUnit._Fields field : fields ) {
                        add("" + field.getThriftFieldId());
                    }
                }}
        };
        
        return new PailTap(path, options);
    }
    
    /**
     * Given the source Path
     * 
     * @param sourcePath - i.e. new data snapshot
     * @param shredPath
     * @return
     * @throws IOException
     */
    public static Pail shred(String sourcePath, String shredPath) throws IOException {
        _LOG.info("PailTapUtil.shred " + sourcePath + ", " + shredPath);
        
        PailTap source = new PailTap(sourcePath);
        PailTap sink = splitDataTap(shredPath);
        
        _LOG.info("PailTapUtil.shred " + source.getPath() + " - " + sink.getPath());
        Subquery reduced = new Subquery("?rand", "?data")
                .predicate(source, "_", "?data-in")
                .predicate(new RandLong())
                    .out("?rand")
                .predicate(new IdentityBuffer(), "?data-in")
                    .out("?data");
        
        Api.execute(sink,  new Subquery("?data").predicate(reduced, "_", "?data"));
        
        Pail shreddedPail = new Pail(shredPath);
        shreddedPail.consolidate();
        
        return shreddedPail;
    }
    
    /**
     * when sinking data from queries to brand new pails, need to declare the type of records writing to PailTap
     * 
     * @param path
     * @return
     */
    public static PailTap splitDataTap(String path) {
        _LOG.info("PailTapUtil.splitDataTap " + path);
        
        PailTapOptions options = new PailTapOptions();
        
        options.spec = new PailSpec(new SplitDataPailstructure());
        return new PailTap(path, options);
    }
    
    private PailTapUtil() {
        super();
    }
    
}
