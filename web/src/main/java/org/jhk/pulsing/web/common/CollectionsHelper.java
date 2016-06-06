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

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Whenever possible use specialized versions of functional interfaces to avoid autoboxing.
 *  
 * <ul> For example :
 *   <li>IntPredicate</li>
 *   <li>IntConsumer</li>
 *   <li>IntFunction</li>
 *   <li>IntSupplier</li>
 *   <li>IntUnaryOperator</li>
 *   <li>IntBinaryOperator</li>
 *   <li>ObjIntConsumer</li>
 *   <li>ToIntBiFunction</li>
 * </ul>
 * 
 * @author Ji Kim
 */
public final class CollectionsHelper {
    
    private CollectionsHelper() {
        super();
    }
    
    /**
     * 
     * <pre>
     * {@code
     * filter(listOfStrings, (String str) -> !str.isEmpty());
     * 
     * IntPredicate evnNumbers = (int i) -> i % 2 == 0; //to avoid autoboxing rather than (Integer i) -> i % 2 == 0;
     * filter(listOfIntegers, evenNumbers);
     * }
     * </pre>
     * 
     * @param list
     * @param predicate
     * @return
     */
    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        List<T> filtered = new LinkedList<>();
        for(T entry : list) {
            if(predicate.test(entry)) {
                filtered.add(entry);
            }
        }
        return filtered;
    }
    
    /**
     * 
     * <pre>
     * {@code
     * forEach(Arrays.asList(1, 2, 3), (Integer content) -> System.out.format("Have been consumed %d", content));
     * }
     * </pre>
     * 
     * @param list
     * @param consumer
     */
    public static <T> void forEach(List<T> list, Consumer<T> consumer) {
        for(T entry : list) {
            consumer.accept(entry);
        }
    }
    
    /**
     * 
     * <pre>
     * {@code
     * map(Arrays.asList("a", "b", "c"), (String content) -> content.length());
     * }
     * </pre>
     * 
     * @param list
     * @param function
     * @return
     */
    public static <T, R> List<R> map(List<T> list, Function<T, R> function) {
        List<R> mapped = new LinkedList<>();
        
        for(T entry : list) {
            mapped.add(function.apply(entry));
        }
        
        return mapped;
    }
    
}
