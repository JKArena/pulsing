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
import Storage from '../../common/Storage';

const API_URL = 'http://maps.googleapis.com/maps/api/js?key=AIzaSyAcUzIUuUTuOZndo3OGs2J4FV-8Ay963ug';

class MapComponent extends Component {

  constructor(props) {
    super(props);

    let lat = 52.809167;
    let lng = -0.630556;
    
    let user = Storage.user;
    if(user && user.coordinates) {
      lat = user.coordinates[0];
      lng = user.coordinates[1];
    }

    this._map = null;
    this.state = {
      lat: lat,
      lng: lng,
      zoom: 8
    };
  }

  componentWillMount() {
    console.debug('fetching map api');

    let script = document.createElement('script');
    script.src = API_URL;
    script.onload = () => {
      console.debug('_OnMapScriptLoaded');
      this.setState(this.state);
    };
    document.body.appendChild(script);
  }

  componentDidUpdate() {
    console.debug('componentDidUpdate');

    if(global.google && !this._map) {
      this._map = new global.google.maps.Map(document.getElementById('mapNode'), {
        center: {lat: this.state.lat, lng: this.state.lng},
        zoom: this.state.zoom
      });
    }
  }

  render() {
    return (
      <div className='map-component'>
        <Grid>
          <Row>
            <Col sm={12}>
              <div id='mapNode'>
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
