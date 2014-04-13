package pl.edu.agh.student.hyperhypervisors.agent;

import org.virtualbox_4_3.*;
import pl.edu.agh.student.hyperhypervisors.model.MachineDescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VirtualBoxAgent {
    private VirtualBoxManager virtualBoxManager;
    private IVirtualBox virtualBox;
    private HashMap<String, IMachine> machinesMap;

    public VirtualBoxAgent(String url, String user, String passwd){
        machinesMap = new HashMap<>();
        virtualBoxManager = VirtualBoxManager.createInstance(null);
        virtualBoxManager.connect(url, user, passwd);

        virtualBox = virtualBoxManager.getVBox();
        createMachinesMap();
    }

    private void createMachinesMap() {
        List<IMachine> machines = virtualBox.getMachines();
        for(IMachine machine : machines){
            machinesMap.put(machine.getName(), machine);
        }
    }

    public List<String> getMachinesNamesList(){
        return new ArrayList<>(machinesMap.keySet());
    }

    public MachineDescription getMachineDescription(String machineName){
        IMachine machine = machinesMap.get(machineName);
        return new MachineDescription(machine.getName(), machine.getOSTypeId(), machine.getMemorySize(), machine.getCPUCount(), getDiskSpace(machineName));
    }

    public long getNumberOfCores(String machineName){
        IMachine machine = machinesMap.get(machineName);
        return machine.getCPUCount();
    }

    public long getMemorySize(String machineName){
        IMachine machine = machinesMap.get(machineName);
        return machine.getMemorySize();
    }

    public long getDiskSpace(String machineName){
        IMachine machine = machinesMap.get(machineName);
        for(IMediumAttachment attachment : machine.getMediumAttachments()){
            if(attachment.getType().equals(DeviceType.HardDisk)){
                IMedium medium = attachment.getMedium();
                if(medium != null){
                    return medium.getSize();
                }
            }
        }
        return 0;
    }

    public void closeVirtualBoxAgent(){
        virtualBoxManager.disconnect();
        virtualBoxManager.cleanup();
    }
}
