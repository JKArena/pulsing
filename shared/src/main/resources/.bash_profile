export MAVEN=/usr/local/lib/maven/apache-maven-3.3.9/bin
export KAFKA=/usr/local/lib/kafka/kafka_2.10-0.10.0.0
export STORM=/usr/local/lib/storm/apache-storm-1.0.1
export HADOOP_HOME=/usr/local/lib/hadoop/hadoop-2.7.2
export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
export REDIS=/usr/local/lib/redis/redis-3.2.1
export BISON=/usr/local/lib/bison/bison-3.0.4/src
export MYSQL=/usr/local/mysql/bin
export CQLSH_NO_BUNDLED=true
export PULSING=/Users/jikim/repository/pulsing/
export CASSANDRA=/usr/local/lib/cassandra/apache-cassandra-3.9
export PATH=$MAVEN:$KAFKA:$STORM:$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$PATH
export SPARK_HOME=/usr/local/lib/spark/spark-2.0.0-bin-hadoop2.7
export SPARK_HADOOP_VERSION=2.7.2
export ES_HOME=/usr/local/lib/elasticsearch/elasticsearch-5.2.2

SPARK_JAR=""
for jar in "$SPARK_HOME"/extra/*
  do
    SPARK_JAR="$jar,$SPARK_JAR"
done

export SPARK_JARS=$SPARK_JAR


export HLJ=/hadoop/libjars
export LIBJARS_CASSANDRA=$HLJ/cascading-cassandra-2.0.6.jar,$HLJ/cassandra-all-3.7.jar,$HLJ/cassandra-driver-core-3.1.0.jar,$HLJ/cassandra-driver-extras-3.1.0.jar,$HLJ/cassandra-driver-mapping-3.1.0.jar

export LIBJARS_CASCALOG=$HLJ/cascalog-core-2.1.1.jar,$HLJ/midje-cascalog-2.1.1.jar,$HLJ/cascalog-checkpoint-2.1.1.jar,$HLJ/cascalog-math-2.1.1.jar,$HLJ/cascalog-more-taps-2.1.1.jar,$HLJ/jvyaml-1.0.0.jar,$HLJ/carbonite-1.5.0.jar,$HLJ/minlog-1.3.0.jar,$HLJ/kryo-4.0.0.jar,$HLJ/hadoop-util-0.3.0.jar,$HLJ/maple-0.2.2.jar,$HLJ/chill-java-0.8.0.jar,$HLJ/chill-hadoop-0.8.0.jar,$HLJ/tools.macro-0.1.2.jar,$HLJ/dfs-datastores-cascading-1.3.6.jar,$HLJ/math.combinatorics-0.0.4.jar,$HLJ/jackknife-0.1.7.jar,$HLJ/clojure-1.8.0.jar,$HLJ/jgrapht-core-0.9.2.jar,$HLJ/jgrapht-ext-0.9.2.jar,$HLJ/cascading-core-2.5.3.jar,$HLJ/cascading-hadoop-2.5.3.jar,$HLJ/dfs-datastores-1.3.6.jar

export LIB_OTHER=$HLJ/libthrift-0.9.3.jar,$HLJ/pulsing-hadoop-pail-0.1-SNAPSHOT.jar,$HLJ/pulsing-shared-0.1-SNAPSHOT.jar,$HLJ/pulsing-thrift-0.1-SNAPSHOT.jar

export LIBJARS=$LIBJARS_CASSANDRA,$LIBJARS_CASCALOG,$LIB_OTHER
export HADOOP_CLASSPATH=`echo $LIBJARS | tr ',' ':'`
export HIVE_HOME=/usr/local/lib/hive/apache-hive-2.1.0-bin

export PATH=$CASSANDRA:$REDIS:$MYSQL:$BISON:$HIVE_HOME/bin:$PATH
# Setting PATH for Python 2.7
# The original version is saved in .bash_profile.pysave
PATH="/Library/Frameworks/Python.framework/Versions/2.7/bin:${PATH}"
export PATH

# Setting PATH for Python 3.6
# The original version is saved in .bash_profile.pysave
PATH="/Library/Frameworks/Python.framework/Versions/3.6/bin:${PATH}"
export PATH
