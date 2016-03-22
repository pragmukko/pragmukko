#!/usr/bin/env bash

#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

echo "Hardware emulator..."
nohup java -cp target/scala-2.11/Swarmakka-assembly-1.1.jar -Dtag_swarmakka SimpleTcpServer > tcp_server.out &

echo "Cluster gate..."
nohup java -cp target/scala-2.11/Swarmakka-assembly-1.1.jar -Dconfig.resource=local/manager.conf -Droot-level=OFF -Dtag_swarmakka GCMain > manager.out &

sleep 5
echo "Embedded node"
nohup java -cp target/scala-2.11/Swarmakka-assembly-1.1.jar -Dconfig.resource=local/embedded.conf -Droot-level=DEBUG  -Dmavlink.type-filter.0=32 -Dtag_swarmakka EmbeddedMain > embedded.out &

echo "Done"
