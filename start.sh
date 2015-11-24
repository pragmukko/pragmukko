#!/usr/bin/env bash
echo "Hardware emulator..."
nohup java -cp target/scala-2.11/Swarmakka-assembly-1.0.jar -Dtag_swarmakka SimpleTcpServer > tcp_server.out &

echo "Cluster gate..."
nohup java -cp target/scala-2.11/Swarmakka-assembly-1.0.jar -Dconfig.resource=local/manager.conf -Droot-level=OFF -Dtag_swarmakka GCMain > manager.out &

#sleep 5
echo "Embedded node"
nohup java -cp target/scala-2.11/Swarmakka-assembly-1.0.jar -Dconfig.resource=local/embedded.conf -Droot-level=DEBUG  -Dmavlink.type-filter.0=32 -Dtag_swarmakka EmbeddedMain > embedded.out &

echo "Done"
