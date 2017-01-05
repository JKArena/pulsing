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

class Locations(models.Model):
  name = models.CharField(max_length=30)
  description = models.CharField(max_length=100, blank=True)
  lat = models.DecimalField(max_digits=7, decimal_places=4, verbose_name='latitude')
  lng = models.DecimalField(max_digits=7, decimal_places=4, verbose_name='longitude')
  user_id = models.BigIntegerField()
  creation_date = models.DateField()

  def __str__(self):
    return u'Location: %s at %d/%d' % (self.name, self.lat, self.lng)

  class Meta:
    ordering = ['name']
