package pl.edu.agh.student.hyperhypervisors.agent;

import org.junit.Before;
import org.junit.Test;
import org.virtualbox_4_3.IMachine;
import org.virtualbox_4_3.IVirtualBox;
import org.virtualbox_4_3.VirtualBoxManager;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.*;

public class VirtualBoxAgentTests {

    private VirtualBoxManager virtualBoxManagerMock;
    private IVirtualBox virtualBoxMock;
    private IMachine machineAMock;
    private IMachine machineBMock;

    private String testUrl = "url";
    private String testUser = "user";
    private String testPassword = "password";

    private VirtualBoxAgent virtualBoxAgent;

    @Before
    public void setUp() throws Exception {
        virtualBoxManagerMock = createMock(VirtualBoxManager.class);
        virtualBoxMock = createMock(IVirtualBox.class);
        machineAMock = createMock(IMachine.class);
        machineBMock = createMock(IMachine.class);

        virtualBoxAgent = new VirtualBoxAgent(virtualBoxManagerMock, getConnectionDetails());
    }

    @Test
    public void testShouldGetMachineNames() throws Exception {
        virtualBoxManagerMock.connect(testUrl, testUser, testPassword);
        expectLastCall();
        expect(virtualBoxManagerMock.getVBox()).andReturn(virtualBoxMock);
        expect(virtualBoxMock.getMachines()).andReturn(Arrays.asList(machineAMock, machineBMock));
        expect(machineAMock.getName()).andReturn("A");
        expect(machineBMock.getName()).andReturn("B");
        virtualBoxManagerMock.cleanup();
        expectLastCall();
        virtualBoxManagerMock.disconnect();
        expectLastCall();

        replay(virtualBoxManagerMock, virtualBoxMock, machineAMock, machineBMock);

        List<String> machineNames = virtualBoxAgent.getMachinesNamesList();

        verify(virtualBoxManagerMock, virtualBoxMock, machineAMock, machineBMock);

        assertEquals(2, machineNames.size());
        assertEquals("A", machineNames.get(0));
        assertEquals("B", machineNames.get(1));
    }

    @Test
    public void testShouldGetMachineDescritpion(){
        virtualBoxManagerMock.connect(testUrl, testUser, testPassword);
        expectLastCall();
        expect(virtualBoxManagerMock.getVBox()).andReturn(virtualBoxMock);
        expect(virtualBoxMock.getMachines()).andReturn(Arrays.asList(machineAMock, machineBMock));
    }

    private ConnectionDetails getConnectionDetails() {
        ConnectionDetails connectionDetails = new ConnectionDetails();

        connectionDetails.setUrl(testUrl);
        connectionDetails.setUser(testUser);
        connectionDetails.setPassword(testPassword);

        return connectionDetails;
    }
}
