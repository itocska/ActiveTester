package ecdh;

import org.openqa.selenium.WebDriverException;
import ecdh.TestBase;

public class Notifications extends TestBase {
	public static void main(String[] args) throws Throwable {
		
		try {
			/*
		  TestBase.main("Notifications driver licence 1 day", 0);
		  TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  TestBase.driverLicenceNotifications(1);
		  TestBase.close();
		  
		  TestBase.main("Notifications driver licence 7 day", 0);
		  TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  TestBase.driverLicenceNotifications(7);
		  TestBase.close();
		  
		  */
		  
		  TestBase.main("Notifications driver licence 30 day", 0);
		  TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  TestBase.driverLicenceNotifications(30);
		  TestBase.close();
		  
		  
			/*
		  TestBase.main("Notifications highway fee 1 day", 0);
		  TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  oneStepInner();
		  highwayFeeNotifications(1);
		  TestBase.close();
		  */
		} 
		catch (AssertionError|WebDriverException e) {
			Log.error = true;
			Log.log(e.getMessage().toString());
			System.out.println("Exception occurred");
			
			throw e;
		}
		
		TestBase.close();
		
		
	}
	
}
