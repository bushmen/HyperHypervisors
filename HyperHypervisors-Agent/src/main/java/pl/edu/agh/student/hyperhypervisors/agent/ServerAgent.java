package pl.edu.agh.student.hyperhypervisors.agent;

import org.hyperic.sigar.*;
import pl.edu.agh.student.hyperhypervisors.model.*;

import java.util.ArrayList;
import java.util.List;

public class ServerAgent implements ServerAgentMXBean {

    Sigar sigar = new Sigar();

    @Override
    public ServerDescription getServerDescription() throws Exception {
        ServerDescription description = new ServerDescription();
        description.setMemory(getMemoryDescription());
        description.setCpus(getCpuDescriptions());
        return description;
    }

    private MemoryDescription getMemoryDescription() throws Exception {
        Mem sigarMemory = sigar.getMem();
        MemoryDescription memory = new MemoryDescription();
        memory.setTotal(sigarMemory.getTotal());
        memory.setFree(sigarMemory.getFree());
        memory.setUsed(sigarMemory.getUsed());
        memory.setActualFree(sigarMemory.getActualFree());
        memory.setActualUsed(sigarMemory.getActualUsed());
        return memory;
    }

    private List<CpuDescription> getCpuDescriptions() throws Exception {
        CpuInfo[] cpuInfoList = sigar.getCpuInfoList();
        Cpu[] cpuList = sigar.getCpuList();
        CpuPerc[] cpuPercentageList = sigar.getCpuPercList();

        List<CpuDescription> cpus = new ArrayList<>();
        for (int i = 0, n = cpuInfoList.length; i < n; ++i) {
            CpuDescription cpuDescription = getCpuDescription(cpuInfoList[i], cpuList[i], cpuPercentageList[i], i);
            cpus.add(cpuDescription);
        }
        return cpus;
    }

    private CpuDescription getCpuDescription(CpuInfo cpuInfo, Cpu absoluteTimeUsage, CpuPerc percentageTimeUsage, int cpuIndex) {
        CpuDescription cpuDescription = new CpuDescription();
        cpuDescription.setCpuIndex(cpuIndex);
        cpuDescription.setCacheSize(cpuInfo.getCacheSize());
        cpuDescription.setClockSpeed(cpuInfo.getMhz());
        cpuDescription.setModel(cpuInfo.getModel());
        cpuDescription.setVendor(cpuInfo.getVendor());
        cpuDescription.setTimeUsage(getTimeUsage(absoluteTimeUsage, percentageTimeUsage));
        return cpuDescription;
    }

    private CpuTimeUsageDescription getTimeUsage(Cpu absoluteTimeUsage, CpuPerc percentageTimeUsage) {
        CpuTimeUsageDescription timeUsage = new CpuTimeUsageDescription();
        timeUsage.setAbsoluteTimeUsage(getAbsoluteTimeUsage(absoluteTimeUsage));
        timeUsage.setPercentageTimeUsage(getPercentageTimeUsage(percentageTimeUsage));
        return timeUsage;
    }

    private CpuAbsoluteTimeUsageDescription getAbsoluteTimeUsage(Cpu absoluteTimeUsage) {
        CpuAbsoluteTimeUsageDescription timeUsage = new CpuAbsoluteTimeUsageDescription();
        timeUsage.setIdleTime(absoluteTimeUsage.getIdle());
        timeUsage.setNiceTime(absoluteTimeUsage.getNice());
        timeUsage.setSystemTime(absoluteTimeUsage.getSys());
        timeUsage.setUserTime(absoluteTimeUsage.getUser());
        timeUsage.setWaitTime(absoluteTimeUsage.getWait());
        return timeUsage;
    }

    private CpuPercentageTimeUsageDescription getPercentageTimeUsage(CpuPerc percentageTimeUsage) {
        CpuPercentageTimeUsageDescription timeUsage = new CpuPercentageTimeUsageDescription();
        timeUsage.setIdleTime(percentageTimeUsage.getIdle());
        timeUsage.setNiceTime(percentageTimeUsage.getNice());
        timeUsage.setSystemTime(percentageTimeUsage.getSys());
        timeUsage.setUserTime(percentageTimeUsage.getUser());
        timeUsage.setWaitTime(percentageTimeUsage.getWait());
        return timeUsage;
    }

}
