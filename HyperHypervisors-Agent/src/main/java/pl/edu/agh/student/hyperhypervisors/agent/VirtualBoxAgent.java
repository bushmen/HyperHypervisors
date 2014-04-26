package pl.edu.agh.student.hyperhypervisors.agent;

import org.virtualbox_4_3.IMachine;
import org.virtualbox_4_3.IVirtualBox;
import org.virtualbox_4_3.VirtualBoxManager;
import pl.edu.agh.student.hyperhypervisors.model.VirtualMachineDescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VirtualBoxAgent implements VirtualBoxAgentMXBean {
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
    public VirtualMachineDescription getMachineDescription(final String machineName) {
        return execute(new Task<VirtualMachineDescription>() {
            @Override
            public VirtualMachineDescription run() {
                return new VirtualMachineDescription(machinesMap.get(machineName));
            }
        });
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
