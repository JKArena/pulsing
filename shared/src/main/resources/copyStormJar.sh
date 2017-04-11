cp $PULSING/serialization/avro/target/pulsing-avro-0.1-SNAPSHOT.jar $STORM/extlib;
cp $PULSING/serialization/thrift/target/pulsing-thrift-0.1-SNAPSHOT.jar $STORM/extlib;
cp $PULSING/hadoop/pail/target/pulsing-hadoop-pail-0.1-SNAPSHOT.jar $STORM/extlib;
cp $PULSING/shared/target/pulsing-shared-0.1-SNAPSHOT.jar $STORM/extlib;
cp $PULSING/storm/target/pulsing-storm-0.1-SNAPSHOT.jar /usr/local/lib/storm;
cp $PULSING/hadoop/pail/target/dfs-datastores-1.3.6.jar $STORM/extlib;
