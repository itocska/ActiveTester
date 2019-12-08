package ecdh;

import org.openqa.selenium.WebDriverException;

import ecdh.TestBase;

public class AddNewCar extends TestBase {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("AddNewCar", 0);
		try {
		  TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  //TestBase.login(TestBase.companyUser, TestBase.companyPassword);
		  Thread.sleep(3000);
		  TestBase.passShepherd();
		  TestBase.addNewCar();
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
