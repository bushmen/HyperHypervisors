package pl.edu.agh.student.hyperhypervisors.model;

import java.beans.ConstructorProperties;
import java.io.Serializable;

public class CpuAbsoluteTimeUsageDescription implements Serializable {

    private long idleTime;
    private long systemTime;
    private long userTime;
    private long niceTime;
    private long waitTime;

    public CpuAbsoluteTimeUsageDescription() {
    }

    @ConstructorProperties(value = {"idleTime", "systemTime", "userTime", "niceTime", "waitTime"})
    public CpuAbsoluteTimeUsageDescription(long idleTime, long systemTime, long userTime, long niceTime, long waitTime) {
        this.idleTime = idleTime;
        this.systemTime = systemTime;
        this.userTime = userTime;
        this.niceTime = niceTime;
        this.waitTime = waitTime;
    }

    public long getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(long idleTime) {
        this.idleTime = idleTime;
    }

    public long getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(long systemTime) {
        this.systemTime = systemTime;
    }

    public long getUserTime() {
        return userTime;
    }

    public void setUserTime(long userTime) {
        this.userTime = userTime;
    }

    public long getNiceTime() {
        return niceTime;
    }

    public void setNiceTime(long niceTime) {
        this.niceTime = niceTime;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }
}
