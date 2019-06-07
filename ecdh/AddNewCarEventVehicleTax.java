package ecdh;

import org.openqa.selenium.WebDriverException;

public class AddNewCarEventVehicleTax {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("AddNewCarEventVehicleTax", 0);
		try {
		  TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  TestBase.oneStepInner();
		  TestBase.addNewCarEventVehicleTax();
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
