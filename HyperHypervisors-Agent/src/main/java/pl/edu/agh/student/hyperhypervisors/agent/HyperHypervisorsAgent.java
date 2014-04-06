package pl.edu.agh.student.hyperhypervisors.agent;

import org.hyperic.sigar.jmx.SigarRegistry;

import javax.management.*;
import java.lang.management.ManagementFactory;

public class HyperHypervisorsAgent {

    public static void main(String[] args) throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException, InterruptedException {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        SigarRegistry mbean = new SigarRegistry();
        mBeanServer.registerMBean(mbean, new ObjectName(mbean.getObjectName()));
        Thread.sleep(Long.MAX_VALUE);
    }
}
