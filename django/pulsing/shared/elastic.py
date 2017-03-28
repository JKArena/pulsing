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

from elasticsearch import Elasticsearch

import logging

class Search():

    def __init__(self, index_name):
        super().__init__()
        self.logger = logging.getLogger(__name__)
        self.__es = Elasticsearch(['pulsing.jhk.org:9200'], sniff_on_start=True)
        self.__index_name = index_name
        if self.__es.indices.exists(self.__index_name):
            self.__es.delete(self.__index_name)
        
        self.__es.indices.create(self.__index_name)
        self.__es.cluster.health(wait_for_status='yellow')
    
    def map(self, type_name, mapping):
        """
        Refer to book+doc since the most annoying part of elastic
        {
            'properties': {
                'uuid': {'type': 'keyword', 'store': 'true'},
                ...
            }
        }
        """
        self.__es.put_mapping(self.__index_name, doc_type=type_name, body={type_name: mapping})
    
    def index(self, type_name, id_value, content):
        self.__es.index(index=self.__index_name, doc_type=type_name, id=id_value, body=content)
    
    def search(self, type_name, q={'match_all': {}}):
        self.__es.search(self.__index_name, type_name, {'query': q})
    
    def get(self, type_name, id_value):
        document = self.__es.get(index=self.__index_name, doc_type=type_name, id=id_value)
        self.logger.debug('got document ' + document)
        return document
    
    def delete(self, type_name, id_value):
        self.__es.delete(index=self.__index_name, doc_type=type_name, id=id_value)

    def optimize(self):
        """ 
        forcemerge allows removal of deleted documents and reducing the number of segments
        (documents are marked as tombstone [like cassandra] but not purged from the segment's 
        index for performance reasons)
        """
        self.logger.debug('optimize')
        self.__es.forcemerge(self.__index_name)

    @property
    def es(self):
        return self.__es

    def __eq__(self, other):
        return self.__es == other.__es

    def __str__(self):
        return self.__es.__str__()

    def __hash__(self):
        return self.__es.__hash__()
