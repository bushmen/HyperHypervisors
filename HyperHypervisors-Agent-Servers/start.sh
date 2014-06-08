#!/bin/bash

IP_ADDRESS=127.0.0.1
PORT=9999

vboxwebsrv -v &> vbox.out &
VBOX_PID=`echo $!`

java -Djava.library.path="./sigar/lib/" -jar ./HyperHypervisors-Agent-Servers.jar ${IP_ADDRESS} ${PORT} &> agent.out &
AGENT_PID=`echo $!`

echo ${VBOX_PID} ${AGENT_PID} > agent.pid