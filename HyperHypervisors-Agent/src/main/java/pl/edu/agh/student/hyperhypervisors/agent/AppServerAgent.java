package pl.edu.agh.student.hyperhypervisors.agent;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AppServerAgent implements AppServerAgentMXBean {

    @Override
    public List<String> getTomcatAppsNamesList(String url, String user, String password) {
        return getAppsNamesList(url, "Catalina", "context");
    }

    @Override
    public List<String> getJboss6AppsNamesList(String url, String user, String password) {
        return getAppsNamesList(url, "jboss.web", "path");
    }

    private List<String> getAppsNamesList(String url, final String jmxName, String property) {
        try {
            JMXServiceURL jmxServiceURL = new JMXServiceURL(url);
            try (JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServiceURL, null)) {
                MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();
                Set<ObjectName> objectNames = mBeanServerConnection.queryNames(
                        null, new ObjectName(jmxName + ":type=Manager,*"));
                List<String> appsNames = new ArrayList<>();
                for (ObjectName objectName : objectNames) {
                    appsNames.add(objectName.getKeyProperty(property));
                }
                return appsNames;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
