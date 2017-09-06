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

/**
 * @author Ji Kim
 */

import SocketJs from 'sockjs-client';
import StompJs from 'stompjs';

import urls from './urls';

class WebSocket {

  constructor(url) {
    this.url = [urls.controllerUrl(), url].join('');
    this.socket = new SocketJs(this.url);
    this.stomp = StompJs.over(this.socket);

    this.connected = false;
  }

  connect(login = '', passcode = '') {
    if (this.connected) {
      return Promise.resolve({});
    }

    return new Promise((resolve, reject) => {
      this.stomp.connect(login, passcode,
        (frame) => {
          this.connected = true;
          resolve(frame);
        },
        (error) => {
          console.error(`Failure in WebSocket connect to ${this.url}`);
          reject(error);
        });
    });
  }

  subscribe(topic, callback) {
    return this.stomp.subscribe(topic, callback);
  }

  send(url, headers, body = '') {
    this.stomp.send(url, headers || {}, body);
  }

  destroy() {
    this.disconnect();
    this.connected = false;
    this.socket = null;
    this.stomp = null;
  }

  disconnect() {
    if (this.stomp) {
      this.stomp.disconnect();
    }
  }

}

export default WebSocket;
