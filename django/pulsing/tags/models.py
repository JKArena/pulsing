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

from shared.elastic import Search

"""
Will be using store which marks the field to be stored in a separate index fragment for fast retrieving but of course eats up more disk.

text type allows textual queries (term, match, span queries)
keyword type is for exact term match and for aggregation and sorting

Since Elasticsearch supports multivalue fieds (arrays) transparently can pass in
{...'tags': ['foo', 'bar', 'stuff'}

"""
pulseSearch = Search('pulse')
pulseSearch.map('pulse_tags', {'properties': 
    {
        'description': {'type': 'text', 'store': 'true'},   # tokenize the description
        'name': { 
            'type': 'keyword', 
            'copy_to': ['suggest'],
            'fields': {
                'name': {'type': 'keyword'},                # will be default multifield subfield-field => 'Luigi pizza - ABC1234'
                'token': {'type': 'text'}                   # standard analyzed (tokenized) => ['Luigi', 'pizza', 'abc1234']
             }
         },
         'suggest': {
             'type': 'completion',
             'analyzer': 'simple',
             'search_analyzer': 'simple'
         },
        'user_id': {'type': 'long', 'store': 'true'},
        'timestamp': {'type': 'date', 'store': 'true'},
        'tags': {'type': 'keyword', 'store': 'true'}         # won't be tokenized since keyword
    }
})

userSearch = Search('user')
userSearch.map('user_tags', {'properties': 
    {
        'email': {'type': 'keyword', 'store': 'true'},
        'name': { 
            'type': 'keyword', 
            'copy_to': ['suggest'],
            'fields': {
                'name': {'type': 'keyword'},
                'token': {'type': 'text'}
             }
         },
         'suggest': {
             'type': 'completion',
             'analyzer': 'simple',
             'search_analyzer': 'simple'
         }
    }
})
