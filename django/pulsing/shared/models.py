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

from django.db import models

class User(models.Model):
    id = models.BigIntegerField(primary_key=True)
    email = models.CharField(db_column='EMAIL', max_length=255)  # Field name made lowercase.
    name = models.CharField(db_column='NAME', max_length=255)  # Field name made lowercase.
    password = models.CharField(db_column='PASSWORD', max_length=255)  # Field name made lowercase.
    imagecontent = models.TextField(db_column='imageContent', blank=True, null=True)  # Field name made lowercase.
    imagename = models.CharField(db_column='imageName', max_length=255, blank=True, null=True)  # Field name made lowercase.
    last_modified = models.DateTimeField(db_column='LAST_MODIFIED', blank=True, null=True)  # Field name made lowercase.

    class Meta:
        managed = False
        db_table = 'USER'
