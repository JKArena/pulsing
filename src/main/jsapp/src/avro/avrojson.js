'use strict';

export default function(avroclass) {
  
  switch(avroclass) {
    case 'Pulse': return {'id':{'id':null},'userId':{'id':null,'cookie':null},'lat':0.0,'lng':0.0,'tags':null,'timeStamp':null,'value':null,'description':null,'action':null};
  case 'PulseId': return {'id':null};
  case 'User': return {'picture':{'content':null,'url':null,'name':null},'lat':0.0,'lng':0.0,'email':null,'id':{'id':null,'cookie':null},'name':null,'password':null};
  case 'UserId': return {'id':null,'cookie':null};
  default: return null;
  }
}