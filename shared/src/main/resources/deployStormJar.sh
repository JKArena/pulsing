cd $STORM;
./bin/storm jar ../pulsing-storm-0.1-SNAPSHOT.jar org.jhk.pulsing.storm.topologies.UserTopologyRunner remote;
./bin/storm jar ../pulsing-storm-0.1-SNAPSHOT.jar org.jhk.pulsing.storm.topologies.PulseSubscribeTopologyRunner remote;
./bin/storm jar ../pulsing-storm-0.1-SNAPSHOT.jar org.jhk.pulsing.storm.topologies.PulseTopologyRunner remote;
