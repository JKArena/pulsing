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

from redis import StrictRedis

import logging
from datetime import timedelta, datetime

def getInvitation(fromUserId, invitationType, invitationId, expiration):
    return {'fromUserId': fromUserId, 'invitationType': invitationType, 'invitationId': invitationId, 'expiration': expiration}

class Redis():
    INVITE_EXPIRATION = 300
    
    def __init__(self, host='pulsing.jhk.org', port=6379, password='wsad', db=0, **kwargs):
        super().__init__()
        self.logger = logging.getLogger(__name__)
        self.__client = StrictRedis(host=host, port=port, password=password, db=0, **kwargs)
    
    def storeInvitation(self, user_id, invitation_id, invitationType):
        expiration = datetime.utcnow() + timedelta(seconds=Redis.INVITE_EXPIRATION)
        self.__client.setex(invitation_id, '1', Redis.INVITE_EXPIRATION)
        self.__client.sadd('INVITATIONS_'+user_id,
                           getInvitation(user_id, invitationType, invitation_id, expiration.timestamp() * 1000.0))
        
    @property
    def client(self):
        return self.__client

    def __eq__(self, other):
        return self.__client == other.__client

    def __str__(self):
        return self.__client.__str__()

    def __hash__(self):
        return self.__client.__hash__()
