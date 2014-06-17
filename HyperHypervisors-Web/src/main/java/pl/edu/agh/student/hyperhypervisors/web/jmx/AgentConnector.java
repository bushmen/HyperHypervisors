package pl.edu.agh.student.hyperhypervisors.web.jmx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.student.hyperhypervisors.agent.AppServerAgentMXBean;
import pl.edu.agh.student.hyperhypervisors.agent.ServerAgentMXBean;
import pl.edu.agh.student.hyperhypervisors.agent.VirtualBoxAgentMXBean;
import pl.edu.agh.student.hyperhypervisors.agent.vm.VirtualMachineAgentMXBean;
import pl.edu.agh.student.hyperhypervisors.dto.ServerDescription;
import pl.edu.agh.student.hyperhypervisors.dto.VirtualMachineDescription;
import pl.edu.agh.student.hyperhypervisors.dto.infrastructure.AppServerModel;
import pl.edu.agh.student.hyperhypervisors.dto.infrastructure.AppServerType;
import pl.edu.agh.student.hyperhypervisors.dto.infrastructure.HypervisorModel;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.ApplicationServer;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.Hypervisor;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.ServerNode;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.VirtualMachine;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class is used to connect to any known JMX service and retrieve appropriate data.
 */

public class AgentConnector {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private ServerNode machine;

    public AgentConnector(ServerNode machine) {
        this.machine = machine;
    }

    public List<Hypervisor> getHypervisors() throws Exception {
        return execute(new MBeanOperation<List<Hypervisor>>() {
            @Override
            public List<Hypervisor> run(MBeanServerConnection mBeanServerConnection) throws Exception {
                List<Hypervisor> hypervisors = new ArrayList<>();
                List<HypervisorModel> runningHypervisors = createMBeanProxy(mBeanServerConnection,
                        "server:type=ServerAgent", ServerAgentMXBean.class).getRunningHypervisors();
                for (HypervisorModel model : runningHypervisors) {
                    Hypervisor hypervisor = new Hypervisor();
                    hypervisor.setName(model.getName());
                    hypervisor.setPort(model.getPort());
                    hypervisors.add(hypervisor);
                }
                return hypervisors;
            }
        });
    }

    public List<ApplicationServer> getApplicationServers(VirtualMachine vm) throws Exception {
        return execute(vm, new MBeanOperation<List<ApplicationServer>>() {
            @Override
            public List<ApplicationServer> run(MBeanServerConnection mBeanServerConnection) throws Exception {
                List<ApplicationServer> appServers = new ArrayList<>();
                Set<AppServerModel> appServerModels = createMBeanProxy(mBeanServerConnection,
                        "vm:type=VirtualMachineAgent", VirtualMachineAgentMXBean.class).getApplicationServers();
                for (AppServerModel model : appServerModels) {
                    ApplicationServer server = new ApplicationServer();
                    server.setName(model.getType().name());
                    server.setType(model.getType());
                    server.setJmxPort(model.getJmxPort());
                    appServers.add(server);
                }
                return appServers;
            }
        });
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
                        .getMachinesNamesList(getHypervisorUrl(hypervisor), hypervisor.getLogin(), hypervisor.getPassword());
            }
        });
    }

    public VirtualMachineDescription getVirtualMachineDescription(final Hypervisor hypervisor, final String machineName) throws Exception {
        return execute(new MBeanOperation<VirtualMachineDescription>() {
            @Override
            public VirtualMachineDescription run(MBeanServerConnection mBeanServerConnection) throws Exception {
                return createMBeanProxy(mBeanServerConnection, "vbox:type=VirtualBoxAgent", VirtualBoxAgentMXBean.class)
                        .getMachineDescription(getHypervisorUrl(hypervisor), hypervisor.getLogin(), hypervisor.getPassword(), machineName);
            }
        });
    }

    private String getHypervisorUrl(Hypervisor hypervisor) {
        return "http://" + machine.getIpAddress() + ":" + hypervisor.getPort();
    }

    public List<String> getApplicationsNames(final VirtualMachine vm, final ApplicationServer server) throws Exception {
        if (vm.getIpAddress() == null || vm.getIpAddress().isEmpty()) {
            return new ArrayList<>();
        }

        return execute(new MBeanOperation<List<String>>() {
            @Override
            public List<String> run(MBeanServerConnection mBeanServerConnection) throws Exception {
                AppServerAgentMXBean mBeanProxy = createMBeanProxy(mBeanServerConnection, "appServer:type=AppServerAgent", AppServerAgentMXBean.class);
                String url = getAppServerJmxUrl(vm, server);
                String login = server.getJmxLogin();
                String password = server.getJmxPassword();
                if (server.getType() == AppServerType.Tomcat) {
                    return mBeanProxy.getTomcatAppsNamesList(url, login, password);
                } else {
                    return mBeanProxy.getJboss6AppsNamesList(url, login, password);
                }
            }
        });
    }

    private String getAppServerJmxUrl(VirtualMachine vm, ApplicationServer server) {
        return "service:jmx:rmi:///jndi/rmi://" + vm.getIpAddress() + ":" + server.getJmxPort() + "/jmxrmi";
    }

    private <T> T createMBeanProxy(MBeanServerConnection mBeanServerConnection,
                                   String objectName, Class<T> mBeanClass) throws Exception {
        return JMX.newMXBeanProxy(mBeanServerConnection, new ObjectName(objectName), mBeanClass, true);
    }

    private <T extends MBeanOperation<R>, R> R execute(T operation) throws Exception {
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"
                + machine.getIpAddress() + ":" + machine.getAgentPort() + "/jmxrmi");
        try (JMXConnector jmxConnector = JMXConnectorFactory.connect(url, null)) {
            MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();
            return operation.run(mBeanServerConnection);
        } catch (Exception e) {
            logger.error("Connection problem: ", e);
            return null;
        }
    }

    private <T extends MBeanOperation<R>, R> R execute(VirtualMachine vm, T operation) throws Exception {
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"
                + vm.getIpAddress() + ":" + vm.getAgentPort() + "/jmxrmi");
        try (JMXConnector jmxConnector = JMXConnectorFactory.connect(url, null)) {
            MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();
            return operation.run(mBeanServerConnection);
        } catch (Exception e) {
            logger.error("Connection problem: ", e);
            return null;
        }
    }
}
