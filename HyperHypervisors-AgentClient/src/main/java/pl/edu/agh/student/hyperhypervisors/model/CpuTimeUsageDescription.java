package pl.edu.agh.student.hyperhypervisors.model;

import java.beans.ConstructorProperties;
import java.io.Serializable;

public class CpuTimeUsageDescription implements Serializable {

    private CpuPercentageTimeUsageDescription percentageTimeUsage;
    private CpuAbsoluteTimeUsageDescription absoluteTimeUsage;

    public CpuTimeUsageDescription() {
    }

    @ConstructorProperties(value = {"percentageTimeUsage", "absoluteTimeUsage"})
    public CpuTimeUsageDescription(CpuPercentageTimeUsageDescription percentageTimeUsage, CpuAbsoluteTimeUsageDescription absoluteTimeUsage) {
        this.percentageTimeUsage = percentageTimeUsage;
        this.absoluteTimeUsage = absoluteTimeUsage;
    }

    public CpuPercentageTimeUsageDescription getPercentageTimeUsage() {
        return percentageTimeUsage;
    }

    public void setPercentageTimeUsage(CpuPercentageTimeUsageDescription percentageTimeUsage) {
        this.percentageTimeUsage = percentageTimeUsage;
    }

    public CpuAbsoluteTimeUsageDescription getAbsoluteTimeUsage() {
        return absoluteTimeUsage;
    }

    public void setAbsoluteTimeUsage(CpuAbsoluteTimeUsageDescription absoluteTimeUsage) {
        this.absoluteTimeUsage = absoluteTimeUsage;
    }
}
