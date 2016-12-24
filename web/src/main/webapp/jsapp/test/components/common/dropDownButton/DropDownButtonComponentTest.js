/* eslint-env node, mocha */
/* global expect */
/* eslint no-console: 0 */
'use strict';

// Uncomment the following lines to use the react test utilities
// import TestUtils from 'react-addons-test-utils';
import createComponent from 'helpers/shallowRenderHelper';

import DropDownButtonComponent from 'components/common/dropDownButton/DropDownButtonComponent.js';

describe('DropDownButtonComponent', () => {
  let component;

  beforeEach(() => {
    component = createComponent(DropDownButtonComponent);
  });

  it('should have its component name as default className', () => {
    expect(component.props.className).to.equal('dropdownbutton-component');
  });
});
