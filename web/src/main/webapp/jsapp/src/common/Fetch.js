'use strict';

const SPRING_CONTROLLER_SUFFIX = ':8083/pulsing-web/controller/';

function controllerUrl() {
  let location = global.location;
  return location.protocol + '//' + location.hostname + SPRING_CONTROLLER_SUFFIX;
}

export default Object.seal(

    Object.defineProperties(Object.create(null),
        {
          'GET' : {
            get: function() {

              return (gPath, options=Object.create(null)) => {
                
                const DEFAULT_HEADERS = new Headers({'Accept': 'application/json'});
                const DEFAULT_OPTIONS = {method: 'GET',  mode: 'cors', headers: DEFAULT_HEADERS};

                let request = new Request(controllerUrl() + gPath);
                let gOptions = Object.assign(DEFAULT_OPTIONS, options);

                return new Promise(function(resolve, reject) {

                  fetch(request, gOptions)
                    .then(function(response) {

                      if(response.ok) {

                        response.json()
                          .then(function(json) {
                            resolve(json);
                          })
                          .catch(function(err) {
                            console.error('Failure in parsing json ', err);
                            reject(err);
                          });

                      }else {
                        console.error('Failure in getting trending with response ', response);
                        reject(response);
                      }

                    })
                    .catch(function(err) {
                      console.error('Failure in getting trending ', err);
                      reject(err);
                    });

                });

              }
            },

            set: function() {},
            enumerable: true
          }
        }
    )

);
