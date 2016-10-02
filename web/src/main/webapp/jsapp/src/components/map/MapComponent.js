/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * @author Ji Kim
 */
'use strict';

require('./Map.scss');

import React, {Component} from 'react';
import {Grid, Row, Col} from 'react-bootstrap';

import WebSockets from '../../common/WebSockets';
import {TOPICS, API} from '../../common/PubSub';
import Storage from '../../common/Storage';
import Pulse from '../../avro/Pulse';
import GMapPulseStore from './store/GMapPulseStore';

const ZOOM_DEFAULT = 20;
const API_URL = 'http://maps.googleapis.com/maps/api/js?key=AIzaSyAcUzIUuUTuOZndo3OGs2J4FV-8Ay963ug';
const KEY_STORE_MAPPER = Object.freeze(
  {
    __proto__: null,
    'pulse': () => { return new GMapPulseStore(); }
  }
);

class MapComponent extends Component {

  constructor(props) {
    super(props);
    
    this.store = KEY_STORE_MAPPER[props.params.store]();
    this.mapId = props.location.query.mapId;
    this.map = null;
    this.geoChangeHandler = this._onGeoChange.bind(this);
    this.dataPointsHandler = this._onDataPoints.bind(this);
    this.pulseCreatedHandler = this._onPulseCreated.bind(this);

    let user = Storage.user;

    this.state = {
      lat: user.lat,
      lng: user.lng,
      zoom: ZOOM_DEFAULT
    };

  }

  componentDidMount() {
    this.store.addDataPointsListener(this.dataPointsHandler);
    this.ws = new WebSockets('pulseSocketJS');
    this.ws.connect()
      .then(frame => {
        console.debug('frame', frame);
        this.sub = this.ws.subscribe('/topics/pulseCreated', this.pulseCreatedHandler);
      });
    /* Just for note
    this.ws.send('/pulsingSocket/pulseSocketJS', {},
                  JSON.stringify({pulseId: evt.target.id, userId: user.id.id}));
    */
    API.subscribe(TOPICS.USER_GEO_CHANGE, this.geoChangeHandler);
    this._initialFetchDataPoints();
  }

  componentWillUnmount() {
    if(this.store) {
      this.store.removeDataPointsListener(this.dataPointsHandler);
      this.store = null;
    }

    if(this.ws) {
      this.ws.destroy();
      this.ws = null;
    }

    API.unsubscribe(TOPICS.USER_GEO_CHANGE, this.geoChangeHandler);
  }

  componentWillMount() {
    console.debug('fetching map api');

    if(!global.google) {
      let script = document.createElement('script');
      script.src = API_URL;
      script.onload = () => {
        this.setState(this.state);
      };
      document.body.appendChild(script);
    }
  }

  componentDidUpdate() {
    console.debug('componentDidUpdate');

    this._initialFetchDataPoints();
  }

  _initialFetchDataPoints() {
    if(global.google && !this.map) {
      this.map = new global.google.maps.Map(document.getElementById(this.mapId), {
        center: {lat: this.state.lat, lng: this.state.lng},
        zoom: this.state.zoom
      });

      this.store.fetchDataPoints(this.map, {lat: this.state.lat, lng: this.state.lng});
    }
  }

  _onPulseCreated(mPulseCreate) {
    console.debug('_onPulseCreated', mPulseCreate);
    
    if(mPulseCreate && mPulseCreate.body) {
      let parsed = JSON.parse(mPulseCreate.body);

      this.store.addDataPoint(this.map, Pulse.deserialize(JSON.parse(parsed.pulse)), [parsed.userLight]);
    }
  }

  _onGeoChange(coordinates) {
    console.debug('geoChange');

    this.map = null;
    this.state = {
      lat: coordinates.lat,
      lng: coordinates.lng,
      zoom: ZOOM_DEFAULT
    };

    this.setState(this.state);
  }

  _onDataPoints(dataPoints) {
    console.debug('fetched dataPoints', dataPoints);

  }

  render() {
    
    return (
      <div className='map-component'>
        <Grid>
          <Row>
            <Col sm={12}>
              <div id={this.mapId} className='map-node'>
              </div>
            </Col>
          </Row>
        </Grid>
      </div>
    );
  }
}

MapComponent.displayName = 'MapComponent';

MapComponent.propTypes = {};
MapComponent.defaultProps = {};

export default MapComponent;
