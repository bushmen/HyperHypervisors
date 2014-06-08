package pl.edu.agh.student.hyperhypervisors.dto;

import java.beans.ConstructorProperties;
import java.io.Serializable;

public class CpuDescription implements Serializable {

    private long cpuIndex;
    private String vendor;
    private String model;
    private long cacheSize;
    private int clockSpeed;
    private CpuTimeUsageDescription timeUsage;

    public CpuDescription() {
    }

    @ConstructorProperties(value = {"cpuIndex", "vendor", "model", "cacheSize", "clockSpeed", "timeUsage"})
    public CpuDescription(long cpuIndex, String vendor, String model, long cacheSize, int clockSpeed, CpuTimeUsageDescription timeUsage) {
        this.cpuIndex = cpuIndex;
        this.vendor = vendor;
        this.model = model;
        this.cacheSize = cacheSize;
        this.clockSpeed = clockSpeed;
        this.timeUsage = timeUsage;
    }

    public long getCpuIndex() {
        return cpuIndex;
    }

    public void setCpuIndex(long cpuIndex) {
        this.cpuIndex = cpuIndex;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public int getClockSpeed() {
        return clockSpeed;
    }

    public void setClockSpeed(int clockSpeed) {
        this.clockSpeed = clockSpeed;
    }

    public CpuTimeUsageDescription getTimeUsage() {
        return timeUsage;
    }

    public void setTimeUsage(CpuTimeUsageDescription timeUsage) {
        this.timeUsage = timeUsage;
    }
}
