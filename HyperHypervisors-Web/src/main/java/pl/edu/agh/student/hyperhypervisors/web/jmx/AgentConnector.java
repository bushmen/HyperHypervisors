package pl.edu.agh.student.hyperhypervisors.web.jmx;

import pl.edu.agh.student.hyperhypervisors.agent.ServerAgentMXBean;
import pl.edu.agh.student.hyperhypervisors.agent.VirtualBoxAgentMXBean;
import pl.edu.agh.student.hyperhypervisors.model.ServerDescription;
import pl.edu.agh.student.hyperhypervisors.model.VirtualMachineDescription;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.Hypervisor;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.ServerNode;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.List;

public class AgentConnector {

    private ServerNode machine;

    public AgentConnector(ServerNode machine) {
        this.machine = machine;
    }

    public ServerDescription getServerDescription() throws Exception {
        return execute(new MBeanOperation<ServerDescription>() {
            @Override
            public ServerDescription run(MBeanServerConnection mBeanServerConnection) throws Exception {
                return createMBeanProxy(mBeanServerConnection, "server:type=ServerAgent", ServerAgentMXBean.class)
                        .getServerDescription();
            }
        });
    }

    public List<String> getVirtualMachinesNames(final Hypervisor hypervisor) throws Exception {
        return execute(new MBeanOperation<List<String>>() {
            @Override
            public List<String> run(MBeanServerConnection mBeanServerConnection) throws Exception {
                return createMBeanProxy(mBeanServerConnection, "vbox:type=VirtualBoxAgent", VirtualBoxAgentMXBean.class)
                        .getMachinesNamesList(getVMUrl(hypervisor), hypervisor.getLogin(), hypervisor.getPassword());
            }
        });
    }

    public VirtualMachineDescription getVirtualMachineDescription(final Hypervisor hypervisor, final String machineName) throws Exception {
        return execute(new MBeanOperation<VirtualMachineDescription>() {
            @Override
            public VirtualMachineDescription run(MBeanServerConnection mBeanServerConnection) throws Exception {
                return createMBeanProxy(mBeanServerConnection, "vbox:type=VirtualBoxAgent", VirtualBoxAgentMXBean.class)
                        .getMachineDescription(getVMUrl(hypervisor), hypervisor.getLogin(), hypervisor.getPassword(), machineName);
            }
        });
    }

    private String getVMUrl(Hypervisor hypervisor) {
        return "http://" + machine.getIpAddress() + ":" + hypervisor.getPort();
    }

    private <T> T createMBeanProxy(MBeanServerConnection mBeanServerConnection,
                                   String objectName, Class<T> mBeanClass) throws Exception {
        return JMX.newMXBeanProxy(mBeanServerConnection, new ObjectName(objectName), mBeanClass, true);
    }

    private <T extends MBeanOperation<R>, R> R execute(T operation) throws Exception {
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"
                + machine.getIpAddress() + ":" + machine.getAgentPort() + "/server");
        try (JMXConnector jmxConnector = JMXConnectorFactory.connect(url, null)) {
            MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();
            return operation.run(mBeanServerConnection);
        }
    }
}
