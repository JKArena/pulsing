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

from django.contrib.gis.db import models
from django.db import connection

class Locations(models.Model):
  OGR_FID = models.AutoField(primary_key=True)
  SHAPE = models.GeometryField(spatial_index=True, null=False)
  osm_id = models.CharField(max_length=100)
  name = models.CharField(max_length=30)
  barrier = models.CharField(max_length=100)
  highway = models.CharField(max_length=100)
  ref = models.CharField(max_length=100)
  address = models.CharField(max_length=100)
  is_in = models.CharField(max_length=100)
  place = models.CharField(max_length=100)
  man_made = models.CharField(max_length=100)
  other_tags = models.CharField(max_length=100)
  description = models.CharField(max_length=100, blank=True)
  user_id = models.BigIntegerField()
  creation_date = models.DateField()

  def customSQL(self):
    cursor = connection.cursor()
    cursor.execute("SELECT * FROM locations LIMIT 10")
    desc = cursor.description
    return [
      dict(zip([col[0] for col in desc], row))
      for row in cursor.fetchall()
    ]

  def __str__(self):
    return u'Location: %s at %d/%d' % (self.name, self.lat, self.lng)

  class Meta:
    ordering = ['name']
