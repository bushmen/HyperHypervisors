package pl.edu.agh.student.hyperhypervisors.web.controllers;

import org.junit.Assert;
import org.junit.Test;

public class MainControllerTest {
    @Test
    public void testIndex() throws Exception {
        MainController testInstance = new MainController();
        Assert.assertEquals("index", testInstance.index());
    }
}