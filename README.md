Project for playing around with Lamba architecture/Realtime big data app.

Allows creation of Pulse which others around the area can subscribe to and when good the group can gogo *-*. For example driving down and one sees a restaurant that one wants to eat at and friends are too far away; create a pulse, get others to join up, and go together xD. Allows chatting for the pulse group as well as creating private chat lobbies.

1. Kafka as queue with Zookeeper
2. Storm+Trident/Spark-streaming for real time data processing (both to compare)
3. Hadoop for HDFS + Yarn
   * Cascading+JCascade+Pail
   * Spark ml for machine learning
   * Hive+Spark SQL for submitted location data using ESRI
4. MySQL/Hibernate for user data and possibly location (GIS) data using Spark SQL
5. Cassandra for chatLobby messages which are created by users
6. Avro+Thrift for serializations (both to compare)
7. Bootstrap+ReactJS+Router
8. Spring with Redis (websocket+session management and cache (short lived pulse data with geo radius search)) for service calls
9. Python Django with Memcached for service calls (to compare with Spring + Redis)
10. Nginx (for RTMP), Java8, Scala, Ecmascript6 and etc

![ScreenShot](https://github.com/JHKTruth/pulsing/blob/master/spring/nonsrc/ssChat.png?raw=true)


![ScreenShot](https://github.com/JHKTruth/pulsing/blob/master/spring/nonsrc/diagram.png?raw=true)
