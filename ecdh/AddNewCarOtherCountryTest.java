package ecdh;

import org.openqa.selenium.WebDriverException;

import ecdh.TestBase;

public class AddNewCarOtherCountryTest extends TestBase {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("AddNewCarOtherCountryTest", 0);
		try {
		  TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  TestBase.goToPage(TestBase.url + "/hu/sajat-autom-felvitel");
		  Thread.sleep(3000);
		  TestBase.passShepherd();
		  TestBase.addNewCarOtherCountryTest();
		  //test
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
