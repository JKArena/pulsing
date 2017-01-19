# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models
import django.contrib.gis.db.models.fields


class Migration(migrations.Migration):

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Locations',
            fields=[
                ('OGR_FID', models.AutoField(primary_key=True, serialize=False)),
                ('SHAPE', django.contrib.gis.db.models.fields.GeometryField(srid=4326)),
                ('osm_id', models.CharField(max_length=100)),
                ('name', models.CharField(max_length=30)),
                ('barrier', models.CharField(max_length=100)),
                ('highway', models.CharField(max_length=100)),
                ('ref', models.CharField(max_length=100)),
                ('address', models.CharField(max_length=100)),
                ('is_in', models.CharField(max_length=100)),
                ('place', models.CharField(max_length=100)),
                ('man_made', models.CharField(max_length=100)),
                ('other_tags', models.CharField(max_length=100)),
                ('description', models.CharField(max_length=100, blank=True)),
                ('user_id', models.BigIntegerField()),
                ('creation_date', models.DateField()),
            ],
            options={
                'ordering': ['name'],
            },
        ),
    ]
