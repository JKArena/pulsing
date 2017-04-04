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

from django.http import JsonResponse, HttpResponseBadRequest

from shared.elastic import Search

import json
import logging
import uuid
import datetime

logger = logging.getLogger(__name__)

"""
Will be using store which marks the field to be stored in a separate index fragment for fast retrieving but of course eats up more disk.

text type allows textual queries (term, match, span queries)
keyword type is for exact term match and for aggregation and sorting

Since Elasticsearch supports multivalue fieds (arrays) transparently can pass in
{...'tags': ['foo', 'bar', 'stuff'}

"""
tagSearch = Search('pulse')
tagSearch.map('pulse_tags', {'properties': 
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

def addPulseDocument(request):
    logger.debug('addPulseDocument')
    
    if not 'pulse' in request.POST:
        return HttpResponseBadRequest()

    logger.debug('addPulseDocument got pulse - %s', request.POST['pulse'])
    pulse = json.loads(request.POST['pulse'])

    logger.debug('addPulseDocument deserialized pulse - %s', pulse)
    tagSearch.index('pulse_tags', pulse['id']['id']['long'],
                    {'description': pulse['description']['string'], 
                     'name': pulse['value']['string'],
                     'user_id': pulse['userId']['id']['long'], 
                     'tags': pulse['tags']['array'], 'timestamp': datetime.datetime.now()})

    return JsonResponse({
        'code': 'SUCCESS',
        'data': {'id': pulse['id']['id']},
        'message': ''
    })

def searchPulseDocument(request):
    logger.debug('searchDocument')
    
    if not 'search' in request.GET:
        return HttpResponseBadRequest()
    
    logger.debug('searchDocument got query - ' + request.GET['search'])
    search = json.loads(request.GET['search'])

    # for now just name, later pass the field
    result = tagSearch.search('pulse_tags', search)

    logger.debug('searchDocument query result - %s', result)

    return JsonResponse({
        'code': 'SUCCESS',
        'data': {'result': result},
        'message': ''
    })
    