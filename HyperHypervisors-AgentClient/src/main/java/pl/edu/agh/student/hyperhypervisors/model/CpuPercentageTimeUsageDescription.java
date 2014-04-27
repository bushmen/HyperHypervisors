package pl.edu.agh.student.hyperhypervisors.model;

import java.beans.ConstructorProperties;
import java.io.Serializable;

public class CpuPercentageTimeUsageDescription implements Serializable {

    private double idleTime;
    private double systemTime;
    private double userTime;
    private double niceTime;
    private double waitTime;

    public CpuPercentageTimeUsageDescription() {
    }

    @ConstructorProperties(value = {"idleTime", "systemTime", "userTime", "niceTime", "waitTime"})
    public CpuPercentageTimeUsageDescription(double idleTime, double systemTime, double userTime, double niceTime, double waitTime) {
        this.idleTime = idleTime;
        this.systemTime = systemTime;
        this.userTime = userTime;
        this.niceTime = niceTime;
        this.waitTime = waitTime;
    }

    public double getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(double idleTime) {
        this.idleTime = idleTime;
    }

    public double getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(double systemTime) {
        this.systemTime = systemTime;
    }

    public double getUserTime() {
        return userTime;
    }

    public void setUserTime(double userTime) {
        this.userTime = userTime;
    }

    public double getNiceTime() {
        return niceTime;
    }

    public void setNiceTime(double niceTime) {
        this.niceTime = niceTime;
    }

    public double getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(double waitTime) {
        this.waitTime = waitTime;
    }
}
