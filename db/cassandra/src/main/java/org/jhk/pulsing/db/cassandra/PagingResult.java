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
package org.jhk.pulsing.db.cassandra;

import org.jhk.pulsing.shared.util.CommonConstants;

/**
 * @author Ji Kim
 */
public final class PagingResult<E> {
    
    private String _paging;
    private E _data;
    
    public PagingResult(String paging, E data) {
        super();
        
        _paging = paging;
        _data = data;
    }
    
    public String getPaging() {
        return _paging;
    }
    public void setPaging(String paging) {
        _paging = paging;
    }
    
    public E getData() {
        return _data;
    }
    public void setData(E data) {
        _data = data;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("paging: " + _paging + ", ");
        builder.append("data: " + _data);
        builder.append("}");
        return builder.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof PagingResult)) {
            return false;
        }
        
        PagingResult<E> casted = (PagingResult<E>) obj;
        return casted._paging.equals(_paging) && casted._data.equals(_data);
    }
    
    @Override
    public int hashCode() {
        int hashCodeVal = CommonConstants.HASH_CODE_INIT_VALUE;
        hashCodeVal = CommonConstants.HASH_CODE_MULTIPLY_VALUE * hashCodeVal + _data.hashCode();
        hashCodeVal = CommonConstants.HASH_CODE_MULTIPLY_VALUE * hashCodeVal + _paging.hashCode();
        return hashCodeVal;
    }
    
}
