package by.nikiter.model.db.service;

import by.nikiter.model.UserState;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class ServicesTest {

    private final ServiceManager manager = new ServiceManager();

    private final static String testUsername = "TestUser";
    private final static String testTrackingNumber = "TE123456789ST";
    private final static String testTrackingName = "TestName";

    @BeforeClass
    void beforeClass() {
        manager.openSession();
    }

    @AfterClass
    void afterClass() {
        manager.closeSession();
    }

    @Test(groups = {"UserService"}, priority = 1)
    public void testAddUser() {
        manager.getUserService().addUser(testUsername,12);
        Assert.assertTrue(manager.getUserService().hasUser(testUsername), "Can't add user");
    }

    @Test(groups = {"UserService"}, dependsOnMethods = {"testAddUser"}, priority = 1)
    public void testHasUser() {
        Assert.assertTrue(manager.getUserService().hasUser(testUsername), "Can't find existing user");
        Assert.assertFalse(manager.getUserService().hasUser(new StringBuilder(testUsername).reverse().toString()),
                "Finds a user that doesn't exist");
    }

    @Test(groups = {"UserService"}, dependsOnMethods = {"testAddUser"}, priority = 1)
    public void testGetUserState() {
        Assert.assertEquals(
                manager.getUserService().getUserState(testUsername).getId(),
                UserState.USING_BOT.getCode(),
                "Finds wrong user state");
    }

    @Test(groups = {"UserService"}, dependsOnMethods = {"testGetUserState"}, priority = 1)
    public void testChangeUserState() {
        manager.getUserService().changeUserState(testUsername, UserState.ENTERING_TRACKING);
        Assert.assertEquals(
                manager.getUserService().getUserState(testUsername).getId(),
                UserState.ENTERING_TRACKING.getCode(),
                "Can't change user state"
        );
    }

    @Test(groups = {"UserService"}, dependsOnMethods = {"testAddUser"}, priority = 2)
    public void testAddTracking() {
        manager.getUserService().addTracking(testUsername,testTrackingNumber,testTrackingName);
        Assert.assertTrue(manager.getUserService().hasTracking(testUsername,testTrackingNumber),
                "Can't add tracking");
    }

    @Test(groups = {"UserService"}, dependsOnMethods = {"testAddTracking"}, priority = 2)
    public void testHasTracking() {
        Assert.assertTrue(manager.getUserService().hasTracking(testUsername,testTrackingNumber),"Can't find tracking");
        Assert.assertFalse(manager.getUserService().hasTracking(
                testUsername,
                new StringBuilder(testTrackingNumber).reverse().toString()
        ), "Finds tracking that doesn't exist");
    }

    @Test(groups = {"UserService"}, dependsOnMethods = {"testAddTracking"}, priority = 2)
    public void testHasTrackings() {
        Assert.assertTrue(manager.getUserService().hasTrackings(testUsername), "Can't find trackings");
        Assert.assertFalse(
                manager.getUserService().hasTrackings(new StringBuilder(testUsername).reverse().toString()),
                "Finds trackings that doesn't exist"
        );
    }

    @Test(groups = {"UserService"}, dependsOnMethods = {"testAddTracking"}, priority = 2)
    public void testGetTrackingName() {
        Assert.assertEquals(
                manager.getUserService().getTrackingName(testUsername,testTrackingNumber),
                testTrackingName,
                "Finds wrong tracking name"
        );
        Assert.assertNull(manager.getUserService().getTrackingName(
                testUsername,
                new StringBuilder(testTrackingNumber).reverse().toString()
        ), "Finds name of tracking that doesn't exist");
    }

    @Test(groups = {"UserService"}, dependsOnMethods = {"testAddTracking"}, priority = 2)
    public void testGetAllTrackingsNumbers() {
        List<String> expected = new ArrayList<>();
        expected.add(testTrackingNumber);

        Assert.assertEquals(manager.getUserService().getAllTrackingsNumbers(testUsername),expected,
                "Finds wrong trackings number");
    }

    @Test(groups = {"UserService"}, dependsOnMethods = {"testAddTracking"}, priority = 2)
    public void testGetAllTrackingsNumbersAndNames() {
        String[] pair = new String[2];
        pair[0] = testTrackingNumber;
        pair[1] = testTrackingName;
        List<String[]> expected = new ArrayList<>();
        expected.add(pair);
        List<String[]> actual = manager.getUserService().getAllTrackingsNumbersAndNames(testUsername);

        Assert.assertEquals(actual.get(0)[0], expected.get(0)[0], "Finds wrong trackings numbers");
        Assert.assertEquals(actual.get(0)[1], expected.get(0)[1], "Finds wrong trackings names");
    }

    @Test(groups = {"TrackingService"}, dependsOnMethods = {"testAddTracking"}, priority = 2)
    public void testGetAllTrackingsNumbersAndUpdatedAt() {
        Assert.assertTrue(manager.getTrackingService().getAllTrackingsNumbersAndUpdatedAt().size() > 0,
                "Can't find trackings numbers and updated dates");
    }

    @Test(groups = {"TrackingService"}, dependsOnMethods = {"testAddTracking"}, priority = 2)
    public void testUpdateTrackingInfo() {
        Assert.assertTrue(
                manager.getTrackingService().updateTrackingInfo(testTrackingNumber,"First Event", testUsername),
                "Can't update tracking last event"
        );
        Assert.assertFalse(
                manager.getTrackingService().updateTrackingInfo(testTrackingNumber,"First Event", testUsername),
                "Updates same tracking last event"
        );
        Assert.assertTrue(
                manager.getTrackingService().updateTrackingInfo(testTrackingNumber,"Second Event",testUsername),
                "Can't update tracking last event"
        );
    }

    @Test(groups = {"UserService"}, dependsOnMethods = {"testAddTracking"}, priority = 3)
    public void testRemoveTracking() {
        Assert.assertTrue(manager.getUserService().removeTracking(testUsername,testTrackingNumber),
                "Can't remove tracking");
        Assert.assertFalse(manager.getUserService().hasTracking(testUsername,testTrackingNumber),
                "Doesn't actually remove tracking");
    }

    @Test(groups = {"TrackingService"}, dependsOnMethods = {"testRemoveTracking"}, priority = 3)
    public void testTryToDeleteTracking() {
        Assert.assertTrue(manager.getTrackingService().tryToDeleteTracking(testTrackingNumber),
                "Can't delete unrelated");
        Assert.assertFalse(manager.getTrackingService().tryToDeleteTracking(testTrackingNumber),
                "Deletes doesn't existing tracking");
    }

    @Test(groups = {"UserService"}, dependsOnMethods = {"testAddUser"}, priority = 4)
    public void testDeleteUser() {
        manager.getUserService().deleteUser(testUsername);
        Assert.assertFalse(manager.getUserService().hasUser(testUsername), "Can't delete user");
    }
}
