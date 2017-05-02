'use strict';

require('./InfoNode.scss');

import React from 'react';
import Url from '../../../common/Url';

const SUBSCRIBE_ACTION = 'subscribe';
const UN_SUBSCRIBE_ACTION = 'unSubscribe';

const InfoNodeStateLess = (props) => {
  const {pulse, userLights} = props;
  const desc = new Date(pulse.timeStamp*1000).toLocaleString(); //since held as seconds on server
  const subscribed = [];
  const actionText = props.actionType === SUBSCRIBE_ACTION ? 'Subscribe' : 'UnSubscribe';

  userLights.forEach(uLight => {
    const pPath = Url.getPicturePath(uLight.picturePath);

    subscribed.push(<li className='map-subscribed-entry' key={uLight.id}>
        <img src={pPath} className='map-subscribed-img'></img>
        <h3 className='map-subscribed-name'>{uLight.name}</h3>
        <p className='map-subscribed-detail'>Foobar</p>
      </li>);
  });

  return (<div className='map-info-node'>
    <h2 className='map-info-header'>{pulse.value}</h2>
    <div className='map-info-date'>
      <span className='map-info-date-text'>{desc}</span>
    </div>
    <ul className='map-subscriptions'>
      {subscribed}
    </ul>
    <a href='#' onClick={props.clickHandler}>{actionText}</a>
  </div>);
};

InfoNodeStateLess.displayName = 'InfoNodeStateLess';

export {InfoNodeStateLess, SUBSCRIBE_ACTION, UN_SUBSCRIBE_ACTION};
