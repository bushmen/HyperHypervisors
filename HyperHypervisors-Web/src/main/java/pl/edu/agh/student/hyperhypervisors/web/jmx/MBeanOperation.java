package pl.edu.agh.student.hyperhypervisors.web.jmx;

import javax.management.MBeanServerConnection;

public interface MBeanOperation<R> {

    R run(MBeanServerConnection mBeanServerConnection) throws Exception;
}
