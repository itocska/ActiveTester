package ecdh;

import java.util.Random;

import org.openqa.selenium.WebDriverException;

import ecdh.TestBase;

public class PurgeProtocoll {
	
	public static void main(String[] args) throws Throwable {
	
		
		TestBase.main("PurgeProtocoll", 0);
		try {
			
			//User registration and activaiton
			/*TestBase.registerUser(TestBase.personalUser, TestBase.personalPassword);
			TestBase.activateUser();*/
			
			//Add new car for purge
			//TestBase.addNewCar();
			//TestBase.purgeCar();
			
			//Purge actual "personalUser"
			//TestBase.registerUser(TestBase.personalUser, TestBase.personalPassword);
			TestBase.purgeUser();
			
			//Company registration and activaiton
			/*Random rand = new Random();
			Integer randomNum = rand.nextInt(3000000 - 1) + 1;
			String companyEmail = TestBase.companyUser;
			String companyName = "Teszt Ceg " + randomNum;
			TestBase.registerCompany(companyName, companyEmail);
			TestBase.activateCompany(true, companyEmail);
			TestBase.adminLogin();
			TestBase.adminActivatecompany(companyName);*/
			//TestBase.purgeCompany();
				
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
