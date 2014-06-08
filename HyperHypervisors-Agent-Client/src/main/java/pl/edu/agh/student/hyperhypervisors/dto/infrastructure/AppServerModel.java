package pl.edu.agh.student.hyperhypervisors.dto.infrastructure;

import java.beans.ConstructorProperties;
import java.io.Serializable;

public class AppServerModel implements Serializable {

    public AppServerModel() {
    }

    @ConstructorProperties(value = {"jmxPort", "type"})
    public AppServerModel(int jmxPort, AppServerType type) {
        this.jmxPort = jmxPort;
        this.type = type;
    }

    private int jmxPort;
    private AppServerType type;

    public int getJmxPort() {
        return jmxPort;
    }

    public void setJmxPort(int jmxPort) {
        this.jmxPort = jmxPort;
    }

    public AppServerType getType() {
        return type;
    }

    public void setType(AppServerType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppServerModel that = (AppServerModel) o;

        return jmxPort == that.jmxPort && type == that.type;
    }

    @Override
    public int hashCode() {
        int result = jmxPort;
        result = 31 * result + type.hashCode();
        return result;
    }
}
