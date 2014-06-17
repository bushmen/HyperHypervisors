package pl.edu.agh.student.hyperhypervisors.agent.vm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to start agent for virtual machines: registers appropriate MBeans and waits for connections.
 */

public class HyperHypervisorsVMsAgent {

    private static MBeanServer mBeanServer;
    private static JMXConnectorServer connectorServer;
    private static List<ObjectInstance> registeredMBeans = new ArrayList<>();
    private static Logger logger = LoggerFactory.getLogger(HyperHypervisorsVMsAgent.class);

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("IP address and port number not specified");
        }

        Runtime.getRuntime().addShutdownHook(new ShutdownThread());

        mBeanServer = ManagementFactory.getPlatformMBeanServer();
        VirtualMachineAgent vmAgent = new VirtualMachineAgent();
        registeredMBeans.add(mBeanServer.registerMBean(vmAgent, new ObjectName("vm:type=VirtualMachineAgent")));

        String ip = args[0];
        int port = Integer.parseInt(args[1]);
        LocateRegistry.createRegistry(port);
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + ip + ":" + port + "/jmxrmi");
        connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mBeanServer);
        connectorServer.start();

        Thread.sleep(Long.MAX_VALUE);
    }

    private static class ShutdownThread extends Thread {
        @Override
        public void run() {
            if (mBeanServer == null) {
                return;
            }

            stopConnectorServer();

            for (ObjectInstance mbean : registeredMBeans) {
                unregisterMBean(mbean);
            }
        }

        private void stopConnectorServer() {
            if (connectorServer != null) {
                try {
                    connectorServer.stop();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        private void unregisterMBean(ObjectInstance mbean) {
            try {
                mBeanServer.unregisterMBean(mbean.getObjectName());
            } catch (Exception e) {
                LoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
            }
        }
    }
}
