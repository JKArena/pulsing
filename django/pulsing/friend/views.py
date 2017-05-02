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

import logging
import uuid

from django.http import JsonResponse, HttpResponseBadRequest
from django.core.cache import get_cache, cache

from shared.models import User

logger = logging.getLogger(__name__)

def friendRequest(request, userId, friendId):
    """ 
    technically does not have to perform the round trip as can pass off
    to websocket on the spring side; however to play around with redis initially
    with django am sending the request here with websocket controller only sending
    out the system alert
    """
    logger.debug('friendRequest %s- %s/%s ', userId, friendId)
    
    userKey = 'user_' + userId
    friendKey = 'user_' + friendId
    
    user = User.objects.get_user(id=userId)
    friend = User.objects.get(id=friendId)
    # TODO need to check if they are friends or not
    
    redis_cache = get_cache('redis')
    # TODO push the invitation id to the redis cache to sync with Spring
    invitation_id = uuid.uuid4()
    
    return JsonResponse({
        'code': 'SUCCESS',
        'data': {'invitationId': invitation_id},
        'message': ''
    })
    
