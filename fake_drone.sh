#!/usr/bin/env bash
nohup \
java -cp target/scala-2.11/Swarmakka-assembly-1.1.jar \
-Dtag_fake_drone -Dmember-id=fake_drone1 -Dvx=-0.1 -Dvy=0.2 \
-Dconfig.resource=local/embedded.conf -Dakka.remote.netty.tcp.port=2556 FakeDrone > drone1.out &

nohup \
java -cp target/scala-2.11/Swarmakka-assembly-1.1.jar \
-Dtag_fake_drone -Dmember-id=fake_drone2 -Dvx=0.1 -Dvy=-0.2 \
-Dconfig.resource=local/embedded.conf -Dakka.remote.netty.tcp.port=2557 FakeDrone > drone2.out &

nohup \
java -cp target/scala-2.11/Swarmakka-assembly-1.1.jar \
-Dtag_fake_drone -Dmember-id=fake_drone3 -Dvx=0.1 -Dvy=0.2 \
-Dconfig.resource=local/embedded.conf -Dakka.remote.netty.tcp.port=2558 FakeDrone > drone3.out &
