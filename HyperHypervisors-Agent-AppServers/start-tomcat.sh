#!/bin/bash

SERVER_TYPE=Tomcat
PORT=8050
VM_AGENT_PORT=9999
CATALINA_HOME=./apache-tomcat-7.0.53

export CATALINA_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=$PORT -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -javaagent:HyperHypervisors-Agent-AppServers.jar=\"$SERVER_TYPE $PORT $VM_AGENT_PORT\""

${CATALINA_HOME}/bin/catalina.sh run &> tomcat.out &
echo $! > tomcat.pid