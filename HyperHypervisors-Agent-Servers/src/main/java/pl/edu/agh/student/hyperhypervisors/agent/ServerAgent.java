package pl.edu.agh.student.hyperhypervisors.agent;

import org.hyperic.sigar.*;
import pl.edu.agh.student.hyperhypervisors.dto.*;
import pl.edu.agh.student.hyperhypervisors.dto.infrastructure.HypervisorModel;

import java.util.ArrayList;
import java.util.List;

public class ServerAgent implements ServerAgentMXBean {

    Sigar sigar = new Sigar();

    @Override
    public List<HypervisorModel> getRunningHypervisors() throws Exception {
        List<HypervisorModel> hypervisors = new ArrayList<>();

        NetConnection[] connections = sigar.getNetConnectionList(getFlags());
        for (NetConnection conn : connections) {
            String processName = getProcessName(conn);
            if("vboxwebsrv".equals(processName)) {
                HypervisorModel hypervisorModel = new HypervisorModel();
                hypervisorModel.setName(processName);
                hypervisorModel.setPort((int) conn.getLocalPort());
                hypervisors.add(hypervisorModel);
            }
        }

        return hypervisors;
    }

    public static int getFlags() {
        int flags = NetFlags.CONN_CLIENT | NetFlags.CONN_PROTOCOLS;
        flags |= NetFlags.CONN_SERVER | NetFlags.CONN_CLIENT;
        flags &= ~NetFlags.CONN_PROTOCOLS;
        flags |= NetFlags.CONN_TCP;
        return flags;
    }

    private String getProcessName(NetConnection connection) {
        if (connection.getState() == NetFlags.TCP_LISTEN) {
            try {
                long pid = this.sigar.getProcPort(connection.getType(), connection.getLocalPort());
                if (pid != 0) {
                    return this.sigar.getProcState(pid).getName();
                }
            } catch (SigarException ignored) {
                //nothing can be done
            }
        }
        return null;
    }

    @Override
    public ServerDescription getServerDescription() throws Exception {
        return new ServerDescription(getMemoryDescription(), getCpuDescriptions());
    }

    private MemoryDescription getMemoryDescription() throws Exception {
        Mem sigarMemory = sigar.getMem();
        return new MemoryDescription(sigarMemory.getTotal(), sigarMemory.getFree(),
                sigarMemory.getUsed(), sigarMemory.getActualFree(), sigarMemory.getActualUsed());
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
        return new CpuDescription(cpuIndex, cpuInfo.getVendor(), cpuInfo.getModel(),
                cpuInfo.getCacheSize(), cpuInfo.getMhz(), getTimeUsage(absoluteTimeUsage, percentageTimeUsage));
    }

    private CpuTimeUsageDescription getTimeUsage(Cpu absoluteTimeUsage, CpuPerc percentageTimeUsage) {
        return new CpuTimeUsageDescription(getPercentageTimeUsage(percentageTimeUsage),
                getAbsoluteTimeUsage(absoluteTimeUsage));
    }

    private CpuAbsoluteTimeUsageDescription getAbsoluteTimeUsage(Cpu absoluteTimeUsage) {
        return new CpuAbsoluteTimeUsageDescription(absoluteTimeUsage.getIdle(), absoluteTimeUsage.getNice(),
                absoluteTimeUsage.getSys(), absoluteTimeUsage.getUser(), absoluteTimeUsage.getWait());
    }

    private CpuPercentageTimeUsageDescription getPercentageTimeUsage(CpuPerc percentageTimeUsage) {
        return new CpuPercentageTimeUsageDescription(percentageTimeUsage.getIdle(), percentageTimeUsage.getNice(),
                percentageTimeUsage.getSys(), percentageTimeUsage.getUser(), percentageTimeUsage.getWait());
    }

}
