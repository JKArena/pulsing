/*
 * Due to nature of Nashorn Javascript engine of Java 8, need to render it differently
 */

function getAvroClassSkeleton(avroclass) {
  switch (avroclass) {
  <#list classinfos as cInfo>
  case '${cInfo.clazz}': return JSON.stringify(${cInfo.skeleton});
  </#list>
  default: return null;
  }
}