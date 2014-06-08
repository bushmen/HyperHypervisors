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
import java.util.ArrayList;

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

    @Test
    public void testGetServerData() throws Exception {
        expect(principalMock.getName()).andReturn(USER_NAME);
        expect(serverServiceMock.getServersData(USER_NAME)).andReturn(new ArrayList<ServerData>());

        replay(principalMock, serverServiceMock);

        testInstance.getServersData(principalMock);
        verify(principalMock, serverServiceMock);
    }

    @Test
    public void testCreateServerSuccess() throws Exception {
        expect(bindingResultMock.hasErrors()).andReturn(false);
        expect(principalMock.getName()).andReturn(USER_NAME);
        expect(serverServiceMock.createServer(serverMock, USER_NAME)).andReturn(serverMock);

        replay(bindingResultMock, principalMock, serverServiceMock, serverMock);

        String result = testInstance.createServer(serverMock, bindingResultMock, principalMock);
        verify(bindingResultMock, principalMock, serverServiceMock, serverMock);

        assertEquals("redirect:/infrastructure", result);
    }

    @Test
    public void testCreateServerFormErrors() throws Exception {
        expect(bindingResultMock.hasErrors()).andReturn(true);

        replay(bindingResultMock, principalMock, serverServiceMock, serverMock);

        String result = testInstance.createServer(serverMock, bindingResultMock, principalMock);
        verify(bindingResultMock, principalMock, serverServiceMock, serverMock);

        assertEquals("infrastructure/create-server", result);
    }

    @Test
    public void testSetServerIPAndPortSuccess() throws Exception {
        expect(bindingResultMock.hasErrors()).andReturn(false);
        expect(principalMock.getName()).andReturn(USER_NAME);
        serverServiceMock.setIPAndPort(changeIpAndPortDataMock, ID, USER_NAME);
        expectLastCall();

        replay(bindingResultMock, principalMock, serverServiceMock, changeIpAndPortDataMock);

        String result = testInstance.setServerIPAndPort(changeIpAndPortDataMock, bindingResultMock, ID, principalMock);
        verify(bindingResultMock, principalMock, serverServiceMock, changeIpAndPortDataMock);

        assertEquals("redirect:/infrastructure", result);
    }

    @Test
    public void testSetServerIPAndPortErrors() throws Exception {
        expect(bindingResultMock.hasErrors()).andReturn(true);

        replay(bindingResultMock, principalMock, serverServiceMock, changeIpAndPortDataMock);

        String result = testInstance.setServerIPAndPort(changeIpAndPortDataMock, bindingResultMock, ID, principalMock);
        verify(bindingResultMock, principalMock, serverServiceMock, changeIpAndPortDataMock);

        assertEquals("infrastructure/set-ip-and-port", result);
    }

    @Test
    public void testRemoveServer() throws Exception {
        expect(principalMock.getName()).andReturn(USER_NAME);
        serverServiceMock.removeServer(ID, USER_NAME);
        expectLastCall();

        replay(principalMock, serverServiceMock);

        String result = testInstance.removeServer(ID, principalMock);
        verify(principalMock, serverServiceMock);

        assertEquals("redirect:/infrastructure", result);
    }

    @Test
    public void testCreateHypervisorSuccess() throws Exception {
        expect(bindingResultMock.hasErrors()).andReturn(false);
        expect(principalMock.getName()).andReturn(USER_NAME);
        expect(serverServiceMock.getServerNodeIfAllowed(USER_NAME, ID)).andReturn(serverMock);
        expect(hypervisorServiceMock.createHypervisor(hypervisorMock, serverMock)).andReturn(hypervisorMock);

        replay(bindingResultMock, principalMock, serverServiceMock, hypervisorServiceMock, hypervisorMock, serverMock);

        String result = testInstance.createHypervisor(hypervisorMock, bindingResultMock, ID, principalMock);
        verify(bindingResultMock, principalMock, serverServiceMock, hypervisorServiceMock, hypervisorMock, serverMock);

        assertEquals("redirect:/infrastructure", result);
    }

    @Test
    public void testCreateHypervisorErrors() throws Exception {
        expect(bindingResultMock.hasErrors()).andReturn(true);

        replay(bindingResultMock, principalMock, serverServiceMock, hypervisorServiceMock, hypervisorMock, serverMock);

        String result = testInstance.createHypervisor(hypervisorMock, bindingResultMock, ID, principalMock);
        verify(bindingResultMock, principalMock, serverServiceMock, hypervisorServiceMock, hypervisorMock, serverMock);

        assertEquals("infrastructure/create-hypervisor", result);
    }

    @Test
    public void testSetHypervisorPortSuccess() throws Exception {
        expect(bindingResultMock.hasErrors()).andReturn(false);
        expect(principalMock.getName()).andReturn(USER_NAME);
        hypervisorServiceMock.setPort(changePortDataMock, ID, USER_NAME);
        expectLastCall();

        replay(bindingResultMock, principalMock, serverServiceMock, hypervisorServiceMock, changePortDataMock);

        String result = testInstance.setHypervisorPort(changePortDataMock, bindingResultMock, ID, principalMock);
        verify(bindingResultMock, principalMock, serverServiceMock, hypervisorServiceMock, changePortDataMock);

        assertEquals("redirect:/infrastructure", result);
    }

    @Test
    public void testSetHypervisorPortErrors() throws Exception {
        expect(bindingResultMock.hasErrors()).andReturn(true);

        replay(bindingResultMock, principalMock, hypervisorServiceMock, changePortDataMock);

        String result = testInstance.setHypervisorPort(changePortDataMock, bindingResultMock, ID, principalMock);
        verify(bindingResultMock, principalMock, hypervisorServiceMock, changePortDataMock);

        assertEquals("infrastructure/set-port-hypervisor", result);
    }

    @Test
    public void testSetVMIPSuccess() throws Exception {
        expect(bindingResultMock.hasErrors()).andReturn(false);
        expect(principalMock.getName()).andReturn(USER_NAME);
        vmServiceMock.setIPAddress(changeIPDataMock, ID, USER_NAME);
        expectLastCall();

        replay(bindingResultMock, principalMock, vmServiceMock, changeIPDataMock);

        String result = testInstance.setVMIPAddress(changeIPDataMock, bindingResultMock, ID, principalMock);
        verify(bindingResultMock, principalMock, vmServiceMock, changeIPDataMock);

        assertEquals("redirect:/infrastructure", result);
    }

    @Test
    public void testSetVMIPErrors() throws Exception {
        expect(bindingResultMock.hasErrors()).andReturn(true);

        replay(bindingResultMock, principalMock, vmServiceMock, changeIPDataMock);

        String result = testInstance.setVMIPAddress(changeIPDataMock, bindingResultMock, ID, principalMock);
        verify(bindingResultMock, principalMock, vmServiceMock, changeIPDataMock);

        assertEquals("infrastructure/set-ip", result);
    }

    @Test
    public void testCreateAppServerSuccess() throws Exception {
        expect(bindingResultMock.hasErrors()).andReturn(false);
        expect(principalMock.getName()).andReturn(USER_NAME);
        expect(appServerServiceMock.createApplicationServer(appServerMock, ID, USER_NAME)).andReturn(appServerMock);

        replay(bindingResultMock, principalMock, appServerServiceMock, appServerMock);

        String result = testInstance.createApplicationServer(appServerMock, bindingResultMock, ID, principalMock, modelMock);
        verify(bindingResultMock, principalMock, appServerServiceMock, appServerMock);

        assertEquals("redirect:/infrastructure", result);
    }

    @Test
    public void testCreateAppServerErrors() throws Exception {
        expect(bindingResultMock.hasErrors()).andReturn(true);
        expect(modelMock.addAttribute(anyString(), anyObject())).andReturn(modelMock);

        replay(bindingResultMock, principalMock, appServerServiceMock, appServerMock, modelMock);

        String result = testInstance.createApplicationServer(appServerMock, bindingResultMock, ID, principalMock, modelMock);
        verify(bindingResultMock, principalMock, appServerServiceMock, appServerMock, modelMock);

        assertEquals("infrastructure/create-appServer", result);
    }

    @Test
    public void testSetAppServerPortSuccess() throws Exception {
        expect(bindingResultMock.hasErrors()).andReturn(false);
        expect(principalMock.getName()).andReturn(USER_NAME);
        appServerServiceMock.setJmxPort(changePortDataMock, ID, USER_NAME);
        ;
        expectLastCall();

        replay(bindingResultMock, principalMock, appServerServiceMock, changePortDataMock);

        String result = testInstance.setAppServerPort(changePortDataMock, bindingResultMock, ID, principalMock);
        verify(bindingResultMock, principalMock, appServerServiceMock, changePortDataMock);

        assertEquals("redirect:/infrastructure", result);
    }

    @Test
    public void testSetAppServerPortErrors() throws Exception {
        expect(bindingResultMock.hasErrors()).andReturn(true);

        replay(bindingResultMock, principalMock, appServerServiceMock, changePortDataMock);

        String result = testInstance.setAppServerPort(changePortDataMock, bindingResultMock, ID, principalMock);
        verify(bindingResultMock, principalMock, appServerServiceMock, changePortDataMock);

        assertEquals("infrastructure/set-port-appserver", result);
    }
}
