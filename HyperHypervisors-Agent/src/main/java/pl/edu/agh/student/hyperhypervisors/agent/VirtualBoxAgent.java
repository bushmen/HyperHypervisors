package pl.edu.agh.student.hyperhypervisors.agent;

import org.virtualbox_4_3.IMachine;
import org.virtualbox_4_3.IVirtualBox;
import org.virtualbox_4_3.VirtualBoxManager;

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

    public String getMachineDescription(String machineName){
        IMachine machine = machinesMap.get(machineName);
        return "Name: " + machine.getName() + "\nOS: " + machine.getOSTypeId() + "\nMemory size:" + machine.getMemorySize() + "\nCPU's: " + machine.getCPUCount();
    }

    public long getNumberOfCores(String machineName){
        IMachine machine = machinesMap.get(machineName);
        return machine.getCPUCount();
    }

    public long getMemorySize(String machineName){
        IMachine machine = machinesMap.get(machineName);
        return machine.getMemorySize();
    }

//    public int getDiskSpace(String machineName){
//        IMachine machine = machinesMap.get(machineName);
//        List<IStorageController> storageControllers = machine.getStorageControllers();
//        for(IStorageController controller : storageControllers){
//            System.out.println(controller.getName());
//        }
//        return 0;
//    }

    public void closeVirtualBoxAgent(){
        virtualBoxManager.disconnect();
        virtualBoxManager.cleanup();
    }
}
