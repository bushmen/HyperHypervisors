package pl.edu.agh.student.hyperhypervisors.web.dto;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

public class ChangeIpAndPortData implements Serializable {

    @NotEmpty(message = "{field.nonempty}")
    @Pattern(regexp = "(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])", message = "{ip.invalid}")
    private String ipAddress;

    @NotNull(message = "{field.nonempty}")
    @Min(value = 1, message = "{port.range}")
    @Max(value = 65536, message = "{port.range}")
    private int port;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
