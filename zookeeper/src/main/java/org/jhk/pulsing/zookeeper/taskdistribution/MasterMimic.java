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
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.MultiCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.Op;
import org.apache.zookeeper.OpResult;
import org.apache.zookeeper.Transaction;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.jhk.pulsing.zookeeper.LeaderBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import static org.jhk.pulsing.zookeeper.taskdistribution.TaskDistributionConstants.*;

/**
 * Master ->
 *   /master [server-id]
 *   /workers
 *      /worker-1 [jhk:2181]
 *   /tasks
 *      /task-1-1 [run xxx]
 *   /assign
 *      /assign/worker-1
 *          /assign/worker-1/task-1-1
 *   /status
 *      /status/task-1-1 [in progress]
 *   
 * @author Ji Kim
 */
public final class MasterMimic extends LeaderBase {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(MasterMimic.class);
    
    private static final Map<String, byte[]> MASTER_BOOTSTRAP_PERSISTENT_META_DATA = ImmutableMap.<String, byte[]>builder()
            .put(WORKERS_PATH, new byte[0])
            .put(ASSIGN_PATH, new byte[0])
            .put(TASKS_PATH, new byte[0])
            .put(STATUS_PATH, new byte[0])
            .build();
    
    private List<String> workers;
    
    public MasterMimic() throws UnknownHostException {
        super(MASTER_PATH, InetAddress.getLocalHost().getHostName().getBytes());
    }

    public MasterMimic(String path, byte[] data) {
        super(path, data);
    }
    
    private final Watcher WORKERS_CHANGE_WATCHER = new Watcher() {
        @Override
        public void process(WatchedEvent event) {
            if (event.getType() == EventType.NodeChildrenChanged) {
                //workers list changed
                
                getWorkers();
            }
        }
    };
    
    private final ChildrenCallback WORKERS_GET_CHILDREN_CALLBACK = new ChildrenCallback() {
        @Override
        public void processResult(int responseCode, String path, Object context, List<String> children) {
            switch(Code.get(responseCode)){
            case CONNECTIONLOSS:
                getWorkers();
                break;
            case OK:
                _LOGGER.info("Got list of workers: {}", children);
                workersUpdate(children);
                break;
            default:
                _LOGGER.error("getChildren for workers failed.", KeeperException.create(Code.get(responseCode), path));
            }
        }
    };
    
    private void getWorkers() {
        // /workers
        zookeeper.getChildren(WORKERS_PATH, WORKERS_CHANGE_WATCHER, WORKERS_GET_CHILDREN_CALLBACK, null);
    }
    
    /**
     * When workers listing changed
     * 
     * @param children list of workers [worker-1, worker-2]
     */
    private void workersUpdate(List<String> children) {
        if (workers == null) {
            //first time getting the workers
            workers = children;
        } else {
            /*
             * not the first time, then
             * 1) check if some workers have been removed, if so then then need to reassign tasks associated to the workers
             * 2) set workers to the current list
             */
            
            List<String> removed = workers.stream()
                .filter( ((Predicate<String>) children::contains).negate() )
                .collect(Collectors.toList());
            
            _LOGGER.info("Following workers were removed: {}", removed);
            removed.forEach(this::reassignTasksForRemovedWorker);
            
            workers = children;
        }
    }
    
    private void reassignTasksForRemovedWorker(String worker) {
        //TODO: Will prob refactor this code since getting complex, when doing so implement this part since too will require descent
        //amount of code
    }
    
    private final Watcher TASKS_CHANGE_WATCHER = new Watcher() {
        @Override
        public void process(WatchedEvent event) {
            if (event.getType() == EventType.NodeChildrenChanged) {
                getTasks();
            }
        }
    };
    
    private final ChildrenCallback TASKS_GET_CHILDREN_CALLBACK = new ChildrenCallback() {
        @Override
        public void processResult(int responseCode, String path, Object context, List<String> children) {
            switch(Code.get(responseCode)){
            case CONNECTIONLOSS:
                getTasks();
                break;
            case OK:
                _LOGGER.info("Got list of tasks: {}", children);
                tasksChanged(children);
                break;
            default:
                _LOGGER.error("getChildren for tasks failed.", KeeperException.create(Code.get(responseCode), path));
            }
        }
    };
    
    private void getTasks() {
        // /tasks
        zookeeper.getChildren(TASKS_PATH, TASKS_CHANGE_WATCHER, TASKS_GET_CHILDREN_CALLBACK, null);
    }
    
    /**
     * When tasks listing changed
     * 
     * @param children list of tasks [task-1-1, task-1-2]
     */
    private void tasksChanged(List<String> children) {
        children.forEach(this::getTaskData);
    }
    
    private final DataCallback GET_TASK_DATA_CALLBACK = new DataCallback() {
        @Override
        public void processResult(int responseCode, String path, Object context, byte[] data, Stat stat) {
            switch(Code.get(responseCode)) {
            case CONNECTIONLOSS:
                getTaskData((String) context);
                break;
            case OK:
                //improve later, but choose randomly
                String worker = workers.get((int) Math.random() * workers.size());
                // /assign/worker-1/task-1-1
                String assignPath = new StringBuilder()
                        .append(ASSIGN_PATH)
                        .append("/")
                        .append(worker)
                        .append("/")
                        .append(context)
                        .toString();
                
                assignTaskToWorker(assignPath, data);
                break;
            default:
                _LOGGER.error("Get task data failed.", KeeperException.create(Code.get(responseCode), path));
            }
        }
    };
    
    /**
     * @param task task-1 so need to prepend /tasks/
     */
    private void getTaskData(String task) {
        zookeeper.getData(TASKS_PATH + "/" + task, false, GET_TASK_DATA_CALLBACK, task);
    }
    
    /**
     * @param path /assign/worker-1/task-1-1
     * @param data run xxx
     * @throws KeeperException 
     * @throws InterruptedException 
     */
    private void assignTaskToWorker(String path, byte[] data){
        // can use Transaction or zookeeper.multi with Op commands; however in nutshell needs it to be a single transaction
        // since assigning the task to a worker, must mean one creates the path and deletes the task from the task pool
        Transaction transaction = zookeeper.transaction();
        transaction.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        transaction.delete(TASKS_PATH + "/" + path.substring( path.lastIndexOf("/") + 1), -1);
        try {
            _LOGGER.info("Task assign and delete transaction for path {}, result {}", path, transaction.commit());
        } catch (InterruptedException | KeeperException exception) {
            _LOGGER.error("Error while assign and delete transaction ", exception);
        }
        /*
        zookeeper.multi(Arrays.asList(
                Op.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT),
                Op.delete(TASKS_PATH + "/" + path.substring( path.lastIndexOf("/") + 1), -1)
                ), ASSIGN_DELETE_TASK_CALLBACK, data);
        */
    }
    
    public void startMaster() throws IOException {
        if (zookeeper == null) {
            startZooKeeper();
        }
        
        bootstrap(MASTER_BOOTSTRAP_PERSISTENT_META_DATA);
        
        // /worker [server-id]
        runForLeader();
    }
    
    @Override
    public void process(WatchedEvent event) {
        
    }

    @Override
    protected Stat getStat() {
        return null;
    }

}
