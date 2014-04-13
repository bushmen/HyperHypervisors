package pl.edu.agh.student.hyperhypervisors.model;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class MachineDescriptionTest {

    private MachineDescription machineDescription;

    @Before
    public void setup(){
        machineDescription = new MachineDescription("test", "testOS", 1024, 2, 1024);
    }

    @Test
    public void testGetName(){
        String expect = "test";
        Assert.assertEquals(expect, machineDescription.getName());
    }

    @Test
    public void testGetOperationSystem(){
        String expect = "testOS";
        Assert.assertEquals(expect, machineDescription.getOperationSystem());
    }

    @Test
    public void testGetMemorySize(){
        long expect = 1024;
        Assert.assertEquals(expect, machineDescription.getMemorySize());
    }

    @Test
    public void testGetCpuCount(){
        long expect = 2;
        Assert.assertEquals(expect, machineDescription.getCpuCount());
    }

    @Test
    public void testGetDiskSpace(){
        long expect = 1024;
        Assert.assertEquals(expect, machineDescription.getDiskSpace());
    }
}
