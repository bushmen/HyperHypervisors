package pl.edu.agh.student.hyperhypervisors.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.student.hyperhypervisors.agent.vm.VirtualMachineAgentMXBean;
import pl.edu.agh.student.hyperhypervisors.dto.infrastructure.AppServerModel;
import pl.edu.agh.student.hyperhypervisors.dto.infrastructure.AppServerType;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class HyperHypervisorsAppServersAgent {

    private static Logger logger = LoggerFactory.getLogger(HyperHypervisorsAppServersAgent.class);

    public static void premain(String agentArgs) {
        String[] args = agentArgs.split(" ");
        try {
            String typeName = args[0];
            int jmxPort = Integer.parseInt(args[1]);
            int vmPort = Integer.parseInt(args[2]);
            registerAppServerInVM(typeName, jmxPort, vmPort);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static void registerAppServerInVM(String typeName, int jmxPort, int vmPort) throws Exception {
        AppServerModel appServer = new AppServerModel();
        appServer.setType(AppServerType.valueOf(typeName));
        appServer.setJmxPort(jmxPort);

        String url = "service:jmx:rmi:///jndi/rmi://localhost:" + vmPort + "/jmxrmi";
        JMXServiceURL jmxServiceURL = new JMXServiceURL(url);
        try (JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServiceURL, null)) {
            MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();
            VirtualMachineAgentMXBean mBeanProxy = JMX.newMXBeanProxy(mBeanServerConnection,
                    new ObjectName("vm:type=VirtualMachineAgent"), VirtualMachineAgentMXBean.class, true);
            mBeanProxy.registerApplicationServer(appServer);
        }
    }
}
