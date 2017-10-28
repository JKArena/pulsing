Project for playing around with Realtime big data app.

Allows creation of Pulse which others around the area can subscribe to and when good the group can gogo. For example driving down and one sees a restaurant that one wants to eat at and friends are too far away; create a pulse, get others to join up, and go together xD (in nutshell any things that don't fit the count). Allows chatting for the pulse group as well as creating private chat lobbies.

1. Kafka as queue with Zookeeper / Flume
2. Storm/Trident/Spark-streaming for real time data processing
3. Hadoop for HDFS + Yarn
   * Cascading+JCascade+Pail
   * Spark ml for machine learning
   * Hive+Spark SQL for submitted location data using ESRI
   * Spark Graphx/Apache Giraph for graph (think Graphx getting deprecated though)
4. MySQL/Hibernate for user data and possibly location (GIS) data using Spark SQL
5. Cassandra for chatLobby messages which are created by users
6. HBase for generated friendship and etc
7. Avro+Parquet/Thrift for SerDe (to compare)
8. Bootstrap+ReactJS+Redux+Router+etc
9. Spring with Redis (websocket+session management and cache (short lived pulse data with geo radius search)) for service calls
10. Python Django with Memcached/Redis for service calls (to compare with Spring + Redis)
11. ElasticSearch/Lucene for searches
12. ElasticSearch+Logstash+Kibana+Flume for log analysis + metrics
13. Debezium for CDC + Apache Sqoop for micro/macro batch
14. Apache Oozie + Hue initially for orchestration
15. Nginx (for RTMP), Java8, Scala, Ecmascript6 and etc

![ScreenShot](https://github.com/JKArena/pulsing/blob/master/spring/nonsrc/diagram.png?raw=true)

![ScreenShot](https://github.com/JKArena/pulsing/blob/master/spring/nonsrc/chatLobbyAlertSystem.png?raw=true)

![ScreenShot](https://github.com/JKArena/pulsing/blob/master/spring/nonsrc/chatViewCountTrending.png?raw=true)
