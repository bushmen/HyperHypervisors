package pl.edu.agh.student.hyperhypervisors.web.controllers;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.Model;
import pl.edu.agh.student.hyperhypervisors.web.dto.ChangeIpAddressData;
import pl.edu.agh.student.hyperhypervisors.web.dto.ChangeIpAndPortData;
import pl.edu.agh.student.hyperhypervisors.web.dto.ChangePortData;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.ApplicationServer;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.Hypervisor;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.ServerNode;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.VirtualMachine;
import pl.edu.agh.student.hyperhypervisors.web.services.ApplicationServerService;
import pl.edu.agh.student.hyperhypervisors.web.services.HypervisorService;
import pl.edu.agh.student.hyperhypervisors.web.services.ServerService;
import pl.edu.agh.student.hyperhypervisors.web.services.VirtualMachineService;

import java.security.Principal;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class InfrastructureControllerTests {

    private static final String USER_NAME = "USER_NAME";
    private static final Long ID = 1L;

    private ServerService serverServiceMock;
    private HypervisorService hypervisorServiceMock;
    private VirtualMachineService vmServiceMock;
    private ApplicationServerService appServerServiceMock;
    private Principal principalMock;
    private ServerNode serverMock;
    private ChangeIpAndPortData changeIpAndPortDataMock;
    private Hypervisor hypervisorMock;
    private ChangePortData changePortDataMock;
    private VirtualMachine vmMock;
    private ChangeIpAddressData changeIPDataMock;
    private InfrastructureController testInstance;
    private ApplicationServer appServerMock;
    private Model modelMock;

    @Before
    public void setUp() throws Exception {
        serverServiceMock = createMock(ServerService.class);
        hypervisorServiceMock = createMock(HypervisorService.class);
        vmServiceMock = createMock(VirtualMachineService.class);
        appServerServiceMock = createMock(ApplicationServerService.class);
        principalMock = createMock(Principal.class);
        serverMock = createMock(ServerNode.class);
        changeIpAndPortDataMock = createMock(ChangeIpAndPortData.class);
        hypervisorMock = createMock(Hypervisor.class);
        changePortDataMock = createMock(ChangePortData.class);
        vmMock = createMock(VirtualMachine.class);
        changeIPDataMock = createMock(ChangeIpAddressData.class);
        appServerMock = createMock(ApplicationServer.class);
        modelMock = createMock(Model.class);

        testInstance = new InfrastructureController();
        testInstance.serverService = serverServiceMock;
        testInstance.hypervisorService = hypervisorServiceMock;
        testInstance.virtualMachineService = vmServiceMock;
        testInstance.applicationServerService = appServerServiceMock;
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
    public void testCreateHypervisorView() {
        expect(principalMock.getName()).andReturn(USER_NAME);
        expect(serverServiceMock.getServerNodeIfAllowed(USER_NAME, ID)).andReturn(serverMock);
        replay(hypervisorMock, principalMock);
        assertEquals("infrastructure/create-hypervisor",
                testInstance.createHypervisorView(hypervisorMock, ID, principalMock));
        verify(hypervisorMock, principalMock);
    }

    @Test
    public void testSetHypervisorPortView() {
        expect(principalMock.getName()).andReturn(USER_NAME);
        expect(hypervisorServiceMock.getHypervisorIfAllowed(USER_NAME, ID)).andReturn(hypervisorMock);
        replay(hypervisorMock, principalMock, changePortDataMock);
        assertEquals("infrastructure/set-port-hypervisor",
                testInstance.setHypervisorPortView(changePortDataMock, ID, principalMock));
        verify(hypervisorMock, principalMock, changePortDataMock);
    }

    @Test
    public void testRemoveHypervisor() {
        expect(principalMock.getName()).andReturn(USER_NAME);
        hypervisorServiceMock.removeHypervisor(ID, USER_NAME);
        expectLastCall();
        replay(hypervisorMock, principalMock);
        assertEquals("redirect:/infrastructure", testInstance.removeHypervisor(ID, principalMock));
        verify(hypervisorMock, principalMock);
    }

    @Test
    public void testSetVMIPView() {
        expect(principalMock.getName()).andReturn(USER_NAME);
        expect(vmServiceMock.getVirtualMachineIfAllowed(USER_NAME, ID)).andReturn(vmMock);
        replay(hypervisorMock, principalMock, changeIPDataMock);
        assertEquals("infrastructure/set-ip",
                testInstance.setVMIPAddressView(changeIPDataMock, ID, principalMock));
        verify(hypervisorMock, principalMock, changeIPDataMock);
    }

    @Test
    public void testCreateAppServerView() {
        expect(principalMock.getName()).andReturn(USER_NAME);
        expect(serverServiceMock.getServerNodeIfAllowed(USER_NAME, ID)).andReturn(serverMock);
        expect(modelMock.addAttribute(anyString(), anyObject())).andReturn(modelMock);
        replay(hypervisorMock, principalMock, modelMock, appServerMock);
        assertEquals("infrastructure/create-appServer",
                testInstance.createApplicationServerView(appServerMock, ID, principalMock, modelMock));
        verify(hypervisorMock, principalMock, modelMock, appServerMock);
    }

    @Test
    public void testSetAppServerPortView() {
        expect(principalMock.getName()).andReturn(USER_NAME);
        expect(appServerServiceMock.getApplicationServerIfAllowed(USER_NAME, ID)).andReturn(appServerMock);
        replay(hypervisorMock, principalMock, changePortDataMock);
        assertEquals("infrastructure/set-port-appserver",
                testInstance.setAppServerPortView(changePortDataMock, ID, principalMock));
        verify(hypervisorMock, principalMock, changePortDataMock);
    }

    @Test
    public void testRemoveAppServer() {
        expect(principalMock.getName()).andReturn(USER_NAME);
        appServerServiceMock.removeApplicationServer(ID, USER_NAME);
        expectLastCall();
        replay(hypervisorMock, principalMock);
        assertEquals("redirect:/infrastructure", testInstance.removeApplicationServer(ID, principalMock));
        verify(hypervisorMock, principalMock);
    }
}
