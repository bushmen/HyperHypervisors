package pl.edu.agh.student.hyperhypervisors.agent;

import org.virtualbox_4_3.IMachine;
import org.virtualbox_4_3.IVirtualBox;
import org.virtualbox_4_3.VirtualBoxManager;
import pl.edu.agh.student.hyperhypervisors.model.MachineDescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class VirtualBoxAgent {
    private VirtualBoxManager virtualBoxManager;
    private ConnectionDetails connectionDetails;
    private IVirtualBox virtualBox;
    private Map<String, IMachine> machinesMap = new HashMap<String, IMachine>();

    public VirtualBoxAgent(VirtualBoxManager virtualBoxManager, ConnectionDetails connectionDetails) {
        this.virtualBoxManager = virtualBoxManager;
        this.connectionDetails = connectionDetails;
    }

    public List<String> getMachinesNamesList() {
        return execute(new Task<List<String>>() {
            @Override
            public List<String> run() {
                return new ArrayList<String>(machinesMap.keySet());
            }
        });
    }

    public MachineDescription getMachineDescription(final String machineName) {
        return execute(new Task<MachineDescription>() {
            @Override
            public MachineDescription run() {
                return new MachineDescription(machinesMap.get(machineName));
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
