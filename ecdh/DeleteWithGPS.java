package ecdh;

import org.openqa.selenium.WebDriverException;

import ecdh.TestBase;

public class DeleteWithGPS extends TestBase {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("DeleteWithGPS", 0);
		try {
			
			TestBase.registerUser(TestBase.personalUser, TestBase.personalPassword);
			TestBase.activateUser();
			Thread.sleep(3000);
			TestBase.passShepherd();
			TestBase.addNewCar();
			Thread.sleep(3000);
			goToPage(TestBase.url + "/hu/garazs");
			Thread.sleep(3000);
			TestBase.buyGPS();
			Thread.sleep(3000);
			TestBase.deleteUser();
			Thread.sleep(3000);
			TestBase.checkGPSAlert();
		  
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
