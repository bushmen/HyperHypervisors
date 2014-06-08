#!/bin/bash

IP_ADDRESS=127.0.0.1
PORT=9999

java -jar ./HyperHypervisors-Agent-VMs.jar ${IP_ADDRESS} ${PORT} &> agent.out &
echo $! > agent.pid