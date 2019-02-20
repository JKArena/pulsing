Project for playing around with Realtime big data app (mainly to tinker around with techs I read from books and etc).

Allows creation of Pulse which others around the area can subscribe to and when good the group can start. For example driving down and one sees a restaurant that one wants to eat at and friends are too far away; create a pulse, get others to join up, and go together xD (in nutshell any things that don't fit the count). Allows chatting for the pulse group as well as creating private chat lobbies like in video games.

1. Kafka as queue with Zookeeper / Flume
2. Storm/Trident/Spark-streaming for real time data processing
3. Hadoop for HDFS + Yarn
   * Cascading+JCascade+Pail
   * Spark ml and TensorFlow for machine learning
   * Hive+Spark SQL for submitted location data using ESRI
   * Spark Graphx/Apache Giraph for graph (think Graphx getting deprecated though)
4. MySQL/Hibernate for user data and possibly location (GIS) data using Spark SQL
5. Cassandra for chatLobby messages which are created by users
6. HBase for key value stores
7. Avro+Parquet/Thrift for SerDe (to compare)
8. Bootstrap+ReactJS+Redux+Router+etc
9. Spring with Redis (websocket+session management and cache (short lived pulse data with geo radius search)) for service calls
10. Python Django with Memcached/Redis for service calls (to compare with Spring + Redis)
11. ElasticSearch/Lucene for searches
12. ElasticSearch+Logstash+Kibana+Flume for log analysis + metrics 
13. Debezium for change data capture + Apache Sqoop for micro/macro batch
14. Docker as container + Kubernite for cluster manager
15. Apache Oozie + Hue initially for orchestration
16. Nginx (for RTMP and with zookeeper for dynamic load balancing), Consul for configuration management and service discovery, Zookeeper/Curator and etc

![ScreenShot](https://github.com/JKArena/pulsing/blob/master/spring/nonsrc/diagram.png?raw=true)

![ScreenShot](https://github.com/JKArena/pulsing/blob/master/spring/nonsrc/chatLobbyAlertSystem.png?raw=true)

![ScreenShot](https://github.com/JKArena/pulsing/blob/master/spring/nonsrc/chatViewCountTrending.png?raw=true)
