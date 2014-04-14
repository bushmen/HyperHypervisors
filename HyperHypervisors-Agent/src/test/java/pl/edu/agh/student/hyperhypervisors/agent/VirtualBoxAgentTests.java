package pl.edu.agh.student.hyperhypervisors.agent;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.virtualbox_4_3.*;

import java.util.ArrayList;

import static org.easymock.EasyMock.*;

public class VirtualBoxAgentTests {

    private VirtualBoxAgent vboxAgent = new VirtualBoxAgent();
    private IVirtualBox virtualBoxMock;
    private VirtualBoxManager vboxManagerMock;
    private IMachine machineMock;
    private IMediumAttachment mediumAttachmentMock;
    private IMedium mediumMock;
    private ArrayList<IMachine> machinesList;
    private ArrayList<IMediumAttachment> attachmentsList;


    @Before
    public void setup(){
        vboxManagerMock = createMock(VirtualBoxManager.class);
        virtualBoxMock = createMock(IVirtualBox.class);
        machineMock = createMock(IMachine.class);
        mediumAttachmentMock = createMock(IMediumAttachment.class);
        mediumMock = createMock(IMedium.class);
        machinesList = new ArrayList<IMachine>();
        machinesList.add(machineMock);
        attachmentsList = new ArrayList<>();
        attachmentsList.add(mediumAttachmentMock);
        vboxAgent.setVirtualBoxManager(vboxManagerMock);
    }

    @Test
    public void testGetMachinesNamesList(){
        expect(vboxManagerMock.getVBox()).andReturn(virtualBoxMock);
        vboxManagerMock.connect(anyString(), anyString(), anyString());

        expect(machineMock.getName()).andReturn("testMachine");
        expect(virtualBoxMock.getMachines()).andReturn(machinesList);
        replay(vboxManagerMock, machineMock, virtualBoxMock);

        vboxAgent.connectVBoxManager("test", "test", "test");
        System.out.println(vboxAgent.getMachinesNamesList().size());
        Assert.assertEquals("testMachine", vboxAgent.getMachinesNamesList().get(0));
    }
}
