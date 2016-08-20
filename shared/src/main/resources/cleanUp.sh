cd /usr/local/lib/storm;
rm -rf localstorm;
rm -r /usr/local/lib/storm/apache-storm-1.0.1/logs/workers-artifacts/*
cd /usr/local/lib/kafka;
rm -rf kafka-logs;
rm -rf zookeeper;
hadoop fs -rm -r hdfs://localhost/data/pailnewdata/*;
hadoop fs -rm -r hdfs://localhost/data/pailmaster
hadoop fs -rm -r hdfs://localhost/tmp/*;
