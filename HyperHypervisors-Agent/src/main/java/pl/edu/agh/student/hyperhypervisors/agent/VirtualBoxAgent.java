package pl.edu.agh.student.hyperhypervisors.agent;

import org.virtualbox_4_3.IMachine;
import org.virtualbox_4_3.VirtualBoxManager;
import pl.edu.agh.student.hyperhypervisors.model.VirtualMachineDescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VirtualBoxAgent implements VirtualBoxAgentMXBean {
    private VirtualBoxManager virtualBoxManager;
    private Map<String, IMachine> machinesMap = new HashMap<>();

    public VirtualBoxAgent(VirtualBoxManager virtualBoxManager) {
        this.virtualBoxManager = virtualBoxManager;
    }

    @Override
    public List<String> getMachinesNamesList(String url, String user, String password) {
        return execute(url, user, password, new Task<List<String>>() {
            @Override
            public List<String> run() {
                return new ArrayList<>(machinesMap.keySet());
            }
        });
    }

    @Override
    public VirtualMachineDescription getMachineDescription(String url, String user, String password, final String machineName) {
        return execute(url, user, password, new Task<VirtualMachineDescription>() {
            @Override
            public VirtualMachineDescription run() {
                if (machinesMap.containsKey(machineName)) {
                    return new VirtualMachineDescription(machinesMap.get(machineName));
                }
                return new VirtualMachineDescription();
            }
        });
    }

    private <T extends Task<R>, R> R execute(String url, String user, String password, T task) {
        try {
            connect(url, user, password);
            return task.run();
        } finally {
            close();
        }
    }

    private void connect(String url, String user, String password) {
        virtualBoxManager.connect(url, user, password);
        createMachinesMap();
    }

    private void createMachinesMap() {
        List<IMachine> machines = virtualBoxManager.getVBox().getMachines();

        for (IMachine machine : machines) {
            machinesMap.put(machine.getName(), machine);
        }
    }

    private void close() {
        virtualBoxManager.disconnect();
        virtualBoxManager.cleanup();
    }
}
