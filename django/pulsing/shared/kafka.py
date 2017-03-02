"""
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

@author Ji Kim
"""

from confluent_kafka import Producer

import logging

class Publisher():

  def __init__(self, config={'bootstrap.servers': 'pulsing.jhk.org:9092', 'retries': 3, 'api.version.request': True}):
    super().__init__()
    self.__producer = Producer(config)
    self.logger = logging.getLogger(__name__)

  def publish(self, topic, data):
    self.logger.debug('publish ' + topic + ' - ' + data)
    self.__producer.produce(topic, data.encode('utf-8'))
    self.__producer.flush()

  @property
  def producer(self):
    return self.__producer

  def __eq__(self, other):
    return self.__producer == other.__producer

  def __str__(self):
    return self.__producer.__str__()

  def __hash__(self):
    return self.__producer.__hash__()
