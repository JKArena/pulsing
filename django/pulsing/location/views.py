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
from django import forms
from shared.kafka import Publisher

import logging
import datetime

LOCATION_CREATE_TOPIC = 'LOCATION_CREATE'
logger = logging.getLogger(__name__)
publisher = Publisher()

def addLocation(request):
    logger.debug('addLocation')

    if('location' in request.POST):
        location = request.POST['location']
        publisher.publish(LOCATION_CREATE_TOPIC, location)

        return JsonResponse({
          'code': 'SUCCESS',
          'data': [],
          'message': ''
        })
    else:
        return HttpResponseBadRequest()

def queryLocation(request, userId, lat, lng):
    logger.debug('queryLocation %s- %s/%s ', userId, lat, lng)
    return JsonResponse({
        'code': 'SUCCESS',
        'data': [
            {
                'name': 'TEST',
                'description': 'Test',
                'lat': 55.2344,
                'lng': 56.4900,
                'user_id': 1,
                'creation_date': datetime.datetime.now()
            },
            {
                'name': 'ANOTHER',
                'description': 'Another',
                'lat': 79.2534,
                'lng': 32.9720,
                'user_id': 2,
                'creation_date': datetime.datetime.now()
            }
        ]
    })
