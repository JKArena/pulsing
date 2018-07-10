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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.data.Stat;
import org.jhk.pulsing.zookeeper.StateBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jhk.pulsing.zookeeper.taskdistribution.TaskDistributionConstants.*;

/**
 * @author Ji Kim
 */
public final class WorkerMimic extends StateBase {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(WorkerMimic.class);
    
    private static final String WORKER_PREFIX_PATH = "/workers/worker-";
    
    public enum States {
        IDLE, WORKING;
    }
    
    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private final List<String> processingTasks = new LinkedList<>();
    
    public WorkerMimic() throws UnknownHostException {
        super(WORKER_PREFIX_PATH + InetAddress.getLocalHost().getHostName(), States.IDLE.name().getBytes());
        // /workers/worker-1
    }

    public WorkerMimic(String path, byte[] data) {
        super(path, data);
    }
    
    private final Watcher NEW_TASK_WATCHER = new Watcher() {
        @Override
        public void process(WatchedEvent event) {
            if (event.getType() == EventType.NodeChildrenChanged) {
                getAssignTasks();
            }
        }
    };
    
    private void getAssignTasks() {
        // /assign/worker-1
        String assignPath = new StringBuilder()
                .append(ASSIGN_PATH)
                .append("/")
                .append(path.substring( path.lastIndexOf("/") + 1)) // worker-1
                .toString();
        
        zookeeper.getChildren(assignPath, NEW_TASK_WATCHER, GET_ASSIGN_TASK_CALLBACK, null);
    }
    
    private final ChildrenCallback GET_ASSIGN_TASK_CALLBACK = new ChildrenCallback() {
        @Override
        public void processResult(int responseCode, String path, Object context, List<String> children) {
            switch(Code.get(responseCode)) {
            case CONNECTIONLOSS:
                getAssignTasks();
                break;
            case OK:
                executor.execute(new Runnable() {
                    List<String> children;
                    
                    public Runnable init(List<String> children) {
                        this.children = children;
                        return this;
                    }
                    
                    @Override
                    public void run() {
                        _LOGGER.info("Going through tasks in a separate thread");
                        
                        synchronized(processingTasks) {
                            children.forEach(task -> {
                                if (!processingTasks.contains(task)) {
                                    //not started yet so queue up
                                    // /assign/worker-1/task-1-1
                                    String assignWorkerTaskPath = new StringBuilder()
                                            .append(ASSIGN_PATH)
                                            .append("/")
                                            .append(path.substring( path.lastIndexOf("/") + 1))
                                            .append("/")
                                            .append(task)
                                            .toString();
                                    
                                    getTaskData(assignWorkerTaskPath, task.getBytes());
                                    processingTasks.add(task);
                                }
                            });
                        }
                    }
                    
                }.init(children));
                
                break;
            default:
                _LOGGER.error("getChildren for worker.task failed.", KeeperException.create(Code.get(responseCode), path));
            }
        }
    };
    
    private final DataCallback GET_TASK_EXECUTE_DATA_CALLBACK = new DataCallback() {
        @Override
        public void processResult(int responseCode, String path, Object context, byte[] data, Stat stat) {
            switch(Code.get(responseCode)) {
            case CONNECTIONLOSS:
                getTaskData(path, data);
                break;
            case OK:
                _LOGGER.info("Got assigned task for path {}", path);
                synchronized(processingTasks) {
                    //just assume executed
                    processingTasks.remove(context);
                }
                break;
            default:
                _LOGGER.error("getTaskData failed.", KeeperException.create(Code.get(responseCode), path));
            }
        }
    };
    
    private void getTaskData(String path, byte[] task) {
        zookeeper.getData(path, false, GET_TASK_EXECUTE_DATA_CALLBACK, task);
    }
    
    public void registerWorker() throws IOException {
        if (zookeeper == null) {
            startZooKeeper();
        }
        
        initialSetup();
    }

    @Override
    public void process(WatchedEvent event) {
        
    }
    
}
