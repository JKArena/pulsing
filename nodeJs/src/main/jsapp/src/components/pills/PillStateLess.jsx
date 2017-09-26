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

import React from 'react';

const PillsWrapper = props => <span className="pills-wrapper">{props.children}</span>;

PillsWrapper.propTypes = {
  children: React.PropTypes.element.isRequired,
};

const TextPill = props => <span className="pills-value">{props.value}</span>;

TextPill.defaultProps = {
  value: '',
};
TextPill.propTypes = {
  value: React.PropTypes.string,
};

const PillsDelete = props => (<span
  role="button"
  tabIndex="0"
  className="pills-remove"
  onClick={props.clickHandler}
>x
</span>);

PillsDelete.propTypes = {
  clickHandler: React.PropTypes.func.isRequired,
};

export { PillsWrapper, PillsDelete, TextPill };
