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
package org.jhk.pulsing.web.common;

/**
 * @author Ji Kim
 */
public final class Result<T> {
    
    public enum CODE { 
        SUCCESS, FAILURE;
    }
    
    private CODE _code;
    private String _message;
    private T _data;
    
    public Result(CODE code, T data) { 
        super();
        
        _code = code;
        _data = data;
    }
    
    public Result(CODE code, T data, String message) { 
        this(code, data);
        
        _message = message;
    }
    
    public CODE getCode() { 
        return _code;
    }
    public void setCode(CODE code) { 
        _code = code;
    }
    
    public String getMessage() { 
        return _message;
    }
    public void setMessage(String message) { 
        _message = message;
    }
    
    public T getData() {
        return _data;
    }
    public void setData(T data) {
        _data = data;
    }
    
    @Override
    public String toString() {
        StringBuilder content = new StringBuilder();
        
        content.append("data [ ");
        content.append(_data);
        content.append("] code [ ");
        content.append(_code);
        content.append(" ] message [ ");
        content.append(_message);
        content.append(" ] ");
        
        return content.toString();
    }
    
}
