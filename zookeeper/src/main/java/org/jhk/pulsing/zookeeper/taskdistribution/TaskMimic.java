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
package org.jhk.pulsing.zookeeper.taskdistribution;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.jhk.pulsing.zookeeper.SequentialBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jhk.pulsing.zookeeper.taskdistribution.TaskDistributionConstants.*;

/**
 * To be used by clients to add tasks to MasterMimic for assigning tasks to WorkerMimic
 * 
 * @author Ji Kim
 */
public final class TaskMimic extends SequentialBase {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(TaskMimic.class);
    
    private static final String TASK_PATH_PREFIX = "/tasks/task-";
    
    public static class Task {
        
        private long timestamp;
        private String sequentialTaskId;
        private String task;
        
        /**
         * @param task run xxx
         */
        public Task(String task) {
            this.task = task;
            timestamp = Instant.now().toEpochMilli();
        }
        
        public String getTask() {
            return task;
        }
        public String getSequentialTaskId() {
            return sequentialTaskId;
        }
        public long getTimestamp() {
            return timestamp;
        }
        
    }
    
    private final ConcurrentMap<String, Object> contextMap = new ConcurrentHashMap<>();
    
    private final StringCallback QUEUE_TASK_CALLBACK = new StringCallback() {
        @Override
        public void processResult(int responseCode, String path, Object context, String name) {
            Task task = null;
            switch (Code.get(responseCode)) {
            case CONNECTIONLOSS:
                task = (Task) context;
                queueTask(task.task, task);
                break;
            case OK:
                _LOGGER.info("Queued task {}.", name);;
                task = (Task) context;
                task.sequentialTaskId = name;
                
                String watchStatusPath = new StringBuilder()
                        .append(STATUS_PATH)
                        .append("/")
                        .append(name.replace(TASKS_PATH + "/", ""))
                        .toString();
                
                watchStatus(watchStatusPath, context);
                break;
            default:
                _LOGGER.error("Error while queing task.", KeeperException.create(Code.get(responseCode), path));
            }
        }
    };
    
    private void watchStatus(String path, Object context) {
        contextMap.put(path, context);
        // /status/task-1-1
        zookeeper.exists(path, STATUS_WATCHER, EXISTS_CALLBACK, context);
    }
    
    public void queueTask(String task, Task context) {
        context.task = task;
        zookeeper.create(TASK_PATH_PREFIX, task.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, 
                CreateMode.PERSISTENT_SEQUENTIAL, QUEUE_TASK_CALLBACK, context);
    }
    
    private final Watcher STATUS_WATCHER = new Watcher() {
        @Override
        public void process(WatchedEvent event) {
            if (event.getType() == EventType.NodeCreated) {
                getStatusData(event.getPath(), contextMap.get(event.getPath()));
            }
        }
    };
    
    private final StatCallback EXISTS_CALLBACK = new StatCallback() {
        @Override
        public void processResult(int responseCode, String path, Object context, Stat stat) {
            switch (Code.get(responseCode)) {
            case CONNECTIONLOSS:
                watchStatus(path, context);
                break;
            case OK:
                getStatusData(path, null);
                break;
            case NONODE:
                break;
            default:
                _LOGGER.error("Error while checking for status node exists.", KeeperException.create(Code.get(responseCode), path));
            }
        }
    };
    
    private final DataCallback GET_STATUS_DATA_CALLBACK = new DataCallback() {
        @Override
        public void processResult(int responseCode, String path, Object context, byte[] data, Stat stat) {
            switch(Code.get(responseCode)) {
            case CONNECTIONLOSS:
                getStatusData(path, context);
                break;
            case OK:
                _LOGGER.info("Got status data for {} : {}.", path, context);
                break;
            default:
                _LOGGER.error("Get task data failed.", KeeperException.create(Code.get(responseCode), path));
            }
        }
    };
    
    private void getStatusData(String path, Object context) {
        zookeeper.getData(path, false, GET_STATUS_DATA_CALLBACK, context);
    }
    
    public String queueTask(String task) throws KeeperException, InterruptedException {
        
        return createSequential(TASK_PATH_PREFIX + task, task.getBytes(), true);
    }
    
    public void initialize() throws IOException {
        startZooKeeper();
    }

    @Override
    public void process(WatchedEvent event) {
        
    }

}
