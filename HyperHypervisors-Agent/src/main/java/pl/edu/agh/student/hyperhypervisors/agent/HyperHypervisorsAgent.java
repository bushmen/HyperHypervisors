package pl.edu.agh.student.hyperhypervisors.agent;

import org.virtualbox_4_3.VirtualBoxManager;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class HyperHypervisorsAgent {

    public static void main(String[] args) throws Exception {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ServerAgent serverAgentMBean = new ServerAgent();
        mBeanServer.registerMBean(serverAgentMBean, new ObjectName("server:type=ServerAgent"));

        ConnectionDetails connectionDetails = new ConnectionDetails();
        connectionDetails.setUrl("http://localhost:18083");
        connectionDetails.setUser("");
        connectionDetails.setPassword("");
        VirtualBoxAgent vboxMbean = new VirtualBoxAgent(VirtualBoxManager.createInstance(null), connectionDetails);
        mBeanServer.registerMBean(vboxMbean, new ObjectName("vbox:type=VirtualBoxAgent"));
        Thread.sleep(Long.MAX_VALUE);
    }
}
