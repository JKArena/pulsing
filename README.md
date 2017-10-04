Project for playing around with Realtime big data app.

Allows creation of Pulse which others around the area can subscribe to and when good the group can gogo. For example driving down and one sees a restaurant that one wants to eat at and friends are too far away; create a pulse, get others to join up, and go together xD (in nutshell any things that don't fit the count). Allows chatting for the pulse group as well as creating private chat lobbies.

1. Kafka as queue with Zookeeper / Flume (to compare)
2. Storm+Trident/Spark-streaming for real time data processing (to compare)
3. Hadoop for HDFS + Yarn
   * Cascading+JCascade+Pail
   * Spark ml for machine learning
   * Hive+Spark SQL for submitted location data using ESRI
   * Spark Graphx (swap with Apache Giraph later as Graphx deprecated)
4. MySQL/Hibernate for user data and possibly location (GIS) data using Spark SQL
5. Cassandra for chatLobby messages which are created by users
6. Avro+Parquet/Thrift for serializations (to compare)
7. Bootstrap+ReactJS+Redux+Router+etc
8. Spring with Redis (websocket+session management and cache (short lived pulse data with geo radius search)) for service calls
9. Python Django with Memcached for service calls (to compare with Spring + Redis)
10. ElasticSearch/Lucene for searches
11. ElasticSearch+Logstash+Kibana for log analysis + metrics
12. Debezium for CDC + Apache Sqoop for macro/micro batch
13. Nginx (for RTMP), Java8, Scala, Ecmascript6 and etc

![ScreenShot](https://github.com/JKArena/pulsing/blob/master/spring/nonsrc/chatLobbyAlertSystem.png?raw=true)

![ScreenShot](https://github.com/JKArena/pulsing/blob/master/spring/nonsrc/chatViewCountTrending.png?raw=true)

![ScreenShot](https://github.com/JKArena/pulsing/blob/master/spring/nonsrc/diagram.png?raw=true)
