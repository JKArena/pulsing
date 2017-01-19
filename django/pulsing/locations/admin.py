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

from django.contrib import admin
from .models import Locations

class LocationAdmin(admin.ModelAdmin):
  list_display = ('name', 'user_id', 'creation_date')
  search_fields = ('user_id',)
  list_filter = ('creation_date',)
  date_hierarchy = 'creation_date'
  ordering = ('user_id', '-creation_date')

admin.site.register(Locations, LocationAdmin)
