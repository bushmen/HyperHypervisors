package pl.edu.agh.student.hyperhypervisors.agent;

import java.util.List;

/**
 * This MBean is used to retrieve information about applications known for specific application server. Currently, only Tomcat 7 and Jboss 6 are supported
 */

public interface AppServerAgentMXBean {

    List<String> getTomcatAppsNamesList(String url, String user, String password);

    List<String> getJboss6AppsNamesList(String url, String user, String password);
}
