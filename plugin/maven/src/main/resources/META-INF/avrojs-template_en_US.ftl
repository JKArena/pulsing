'use strict';

export default function(avroclass) {
  
  switch(avroclass) {
  <#list classinfos as cInfo>
  case '${cInfo.clazz}': return ${cInfo.skeleton};
  </#list>
  default: return null;
  }
}