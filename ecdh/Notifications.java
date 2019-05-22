package ecdh;

import org.openqa.selenium.WebDriverException;
import ecdh.TestBase;

public class Notifications extends TestBase {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("Notifications", 0);
		try {
		  TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  TestBase.driverLicenceNotifications(1);
		  TestBase.driverLicenceNotifications(7);
		  TestBase.driverLicenceNotifications(30);
		  
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
