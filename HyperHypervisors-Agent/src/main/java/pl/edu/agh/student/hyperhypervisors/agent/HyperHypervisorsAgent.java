package pl.edu.agh.student.hyperhypervisors.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtualbox_4_3.VirtualBoxManager;

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

public class HyperHypervisorsAgent {

    private static MBeanServer mBeanServer;
    private static JMXConnectorServer connectorServer;
    private static List<ObjectInstance> registeredMBeans = new ArrayList<>();
    private static Logger logger = LoggerFactory.getLogger(HyperHypervisorsAgent.class);

    public static void main(String[] args) throws Exception {

        Runtime.getRuntime().addShutdownHook(new ShutdownThread());

        mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ServerAgent serverAgentMBean = new ServerAgent();
        registeredMBeans.add(mBeanServer.registerMBean(serverAgentMBean, new ObjectName("server:type=ServerAgent")));

        ConnectionDetails connectionDetails = new ConnectionDetails();
        //TODO to properties file
        connectionDetails.setUrl("http://localhost:18083");
        connectionDetails.setUser("");
        connectionDetails.setPassword("");
        VirtualBoxAgent vboxMbean = new VirtualBoxAgent(VirtualBoxManager.createInstance(null), connectionDetails);
        registeredMBeans.add(mBeanServer.registerMBean(vboxMbean, new ObjectName("vbox:type=VirtualBoxAgent")));

        LocateRegistry.createRegistry(9999);
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9999/server");
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
