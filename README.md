Project for playing around with Lamba architecture/Realtime big data app.

Allows creation of Pulse which others around the area can subscribe to and when good the group can gogo *-*. For example driving down and one sees a restaurant that one wants to eat at and friends are too far away; create a pulse, get others to join up, and go together xD. Allows chatting for the pulse group as well as creating private chat lobbies.

1. Kafka as queue with Zookeeper
2. Storm+Trident/Spark-streaming for real time data processing
3. Hadoop for HDFS + Yarn
   * Cascading+JCascade+Pail
   * Spark ml for machine learning
   * Hive+Spark SQL
4. MySQL/Hibernate for user data and location data
5. Cassandra for chatLobby messages which are created by users
6. Redis for computed + session management (pulse data with geo radius search)
4. Avro+Thrift for serializations
5. Bootstrap+ReactJS+Router+NodeJS
6. Spring (WebSocket,Aspect, and etc)
7. Python Django (for compare)
8. Java8+Ecmascript6 and etc (i.e. maybe Apache Mesos + Oozie)

![ScreenShot](https://github.com/JHKTruth/pulsing/blob/master/web/nonsrc/ssChat.png?raw=true)


![ScreenShot](https://github.com/JHKTruth/pulsing/blob/master/web/nonsrc/diagram.png?raw=true)
