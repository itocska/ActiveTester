package ecdh;

import java.util.Random;

import org.openqa.selenium.WebDriverException;

import ecdh.TestBase;

public class PurgeProtocoll {
	
	public static void main(String[] args) throws Throwable {
	
		
		TestBase.main("PurgeProtocoll", 0);
		try {
			
			//User registration and activation
			TestBase.registerUser(TestBase.personalUser, TestBase.personalPassword);
			TestBase.activateUser();
			
			//Add new car for purge
			TestBase.addNewCar();
			String currentCarId = TestBase.getCarId();
			TestBase.logout();
			
			TestBase.purgeCar(currentCarId);
			TestBase.logout();
			
			//Purge actual "personalUser"
			TestBase.purgeUser();
			
			//Company registration than purge
			Random rand = new Random();
			Integer randomNum = rand.nextInt(3000000 - 1) + 1;
			TestBase.registerCompany(TestBase.companyUser, "Test Company " + randomNum);
			
			TestBase.purgeCompany();
				
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
