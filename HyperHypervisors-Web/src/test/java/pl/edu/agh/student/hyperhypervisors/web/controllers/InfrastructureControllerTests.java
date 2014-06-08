package pl.edu.agh.student.hyperhypervisors.web.controllers;

import org.junit.Before;
import org.junit.Test;
import pl.edu.agh.student.hyperhypervisors.web.dto.ChangeIpAndPortData;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.Hypervisor;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.ServerNode;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.VirtualMachine;
import pl.edu.agh.student.hyperhypervisors.web.services.ServerService;
import pl.edu.agh.student.hyperhypervisors.web.services.VirtualMachineService;

import java.security.Principal;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class InfrastructureControllerTests {

    private static final String USER_NAME = "USER_NAME";
    private static final Long ID = 1L;

    private ServerService serverServiceMock;
    private VirtualMachineService vmServiceMock;
    private Principal principalMock;
    private ServerNode serverMock;
    private ChangeIpAndPortData changeIpAndPortDataMock;
    private Hypervisor hypervisorMock;
    private VirtualMachine vmMock;
    private InfrastructureController testInstance;

    @Before
    public void setUp() throws Exception {
        serverServiceMock = createMock(ServerService.class);
        vmServiceMock = createMock(VirtualMachineService.class);
        principalMock = createMock(Principal.class);
        serverMock = createMock(ServerNode.class);
        changeIpAndPortDataMock = createMock(ChangeIpAndPortData.class);
        hypervisorMock = createMock(Hypervisor.class);
        vmMock = createMock(VirtualMachine.class);

        testInstance = new InfrastructureController();
        testInstance.serverService = serverServiceMock;
        testInstance.virtualMachineService = vmServiceMock;
    }

    @Test
    public void testInfrastructureView() throws Exception {
        assertEquals("infrastructure/view", testInstance.infrastructureView());
    }

    @Test
    public void testCreateServerView() {
        replay(serverMock);
        assertEquals("infrastructure/create-server", testInstance.createServerView(serverMock));
        verify(serverMock);
    }

    @Test
    public void testSetServerIPAndPortView() {
        expect(principalMock.getName()).andReturn(USER_NAME);
        expect(serverServiceMock.getServerNodeIfAllowed(USER_NAME, ID)).andReturn(serverMock);
        replay(changeIpAndPortDataMock, principalMock);
        assertEquals("infrastructure/set-ip-and-port",
                testInstance.setServerIPAndPortView(changeIpAndPortDataMock, ID, principalMock));
        verify(changeIpAndPortDataMock, principalMock);
    }

    @Test
    public void testSetVMIPView() {
        expect(principalMock.getName()).andReturn(USER_NAME);
        expect(vmServiceMock.getVirtualMachineIfAllowed(USER_NAME, ID)).andReturn(vmMock);
        replay(hypervisorMock, principalMock, changeIpAndPortDataMock);
        assertEquals("infrastructure/set-vm-ip-and-port",
                testInstance.setVMIPAddressAndPortView(changeIpAndPortDataMock, ID, principalMock));
        verify(hypervisorMock, principalMock, changeIpAndPortDataMock);
    }
}
