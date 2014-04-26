package pl.edu.agh.student.hyperhypervisors.agent;

import org.hyperic.sigar.jmx.SigarRegistry;
import org.virtualbox_4_3.VirtualBoxManager;

import javax.management.*;
import java.lang.management.ManagementFactory;

public class HyperHypervisorsAgent {

    public static void main(String[] args) throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException, InterruptedException {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        SigarRegistry mbean = new SigarRegistry();
        mBeanServer.registerMBean(mbean, new ObjectName(mbean.getObjectName()));
        ConnectionDetails connectionDetails = new ConnectionDetails();
        connectionDetails.setUrl("http://localhost:18083");
        connectionDetails.setUser("");
        connectionDetails.setPassword("");
        VirtualBoxAgent vboxMbean = new VirtualBoxAgent(VirtualBoxManager.createInstance(null),connectionDetails);
        mBeanServer.registerMBean(vboxMbean, new ObjectName("vbox:type=virtualBoxManager"));
        Thread.sleep(Long.MAX_VALUE);
    }
}
