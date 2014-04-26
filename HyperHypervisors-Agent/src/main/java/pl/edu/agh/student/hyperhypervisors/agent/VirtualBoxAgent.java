package pl.edu.agh.student.hyperhypervisors.agent;

import org.virtualbox_4_3.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.virtualbox_4_3.DeviceType.HardDisk;

public class VirtualBoxAgent implements VirtualBoxAgentMBean {
    private static final int EMPTY = 0;

    private VirtualBoxManager virtualBoxManager;
    private ConnectionDetails connectionDetails;
    private IVirtualBox virtualBox;
    private Map<String, IMachine> machinesMap = new HashMap<>();

    public VirtualBoxAgent(VirtualBoxManager virtualBoxManager, ConnectionDetails connectionDetails) {
        this.virtualBoxManager = virtualBoxManager;
        this.connectionDetails = connectionDetails;
    }

    @Override
    public List<String> getMachinesNamesList() {
        return execute(new Task<List<String>>() {
            @Override
            public List<String> run() {
                return new ArrayList<>(machinesMap.keySet());
            }
        });
    }

    @Override
    public String getMachineName(final String machineName){
        return execute(new Task<String>(){
            @Override
            public String run(){
                return machinesMap.get(machineName).getName();
            }
        });
    }

    @Override
    public String getMachineOperationSystem(final String machineName){
        return execute(new Task<String>(){
            @Override
            public String run(){
                return machinesMap.get(machineName).getOSTypeId();
            }
        });
    }

    @Override
    public long getMachineMemorySize(final String machineName){
        return execute(new Task<Long>(){
            @Override
            public Long run(){
                return machinesMap.get(machineName).getMemorySize();
            }
        });
    }

    @Override
    public long getMachineCPUCount(final String machineName){
        return execute(new Task<Long>(){
            @Override
            public Long run(){
                return machinesMap.get(machineName).getCPUCount();
            }
        });
    }

    @Override
    public long getMachineDiskSpace(final String machineName){
        return execute(new Task<Long>(){
            @Override
            public Long run(){
                return getDiskSpace(machinesMap.get(machineName));
            }
        });
    }

    private long getDiskSpace(IMachine machine) {
        for (IMediumAttachment attachment : machine.getMediumAttachments()) {
            if (isHardDisk(attachment)) {
                IMedium medium = attachment.getMedium();
                if (medium != null) {
                    return medium.getSize();
                }
            }
        }

        return EMPTY;
    }

    private boolean isHardDisk(IMediumAttachment attachment) {
        return attachment.getType() == HardDisk;
    }

    private void initialize() {
        virtualBox = virtualBoxManager.getVBox();
        createMachinesMap();
    }

    private void createMachinesMap() {
        List<IMachine> machines = virtualBox.getMachines();

        for (IMachine machine : machines) {
            machinesMap.put(machine.getName(), machine);
        }
    }

    private <T extends Task<R>, R> R execute(T task) {
        try {
            connect();
            return task.run();
        } finally {
            close();
        }
    }

    private void connect() {
        virtualBoxManager.connect(connectionDetails.getUrl(), connectionDetails.getUser(), connectionDetails.getPassword());
        initialize();
    }

    private void close() {
        virtualBoxManager.disconnect();
        virtualBoxManager.cleanup();
    }
}
