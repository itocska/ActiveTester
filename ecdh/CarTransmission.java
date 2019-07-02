package ecdh;

import org.openqa.selenium.WebDriverException;

public class CarTransmission  extends TestBase {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("CarTransmission", 0);
		try {
	      
		  TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  TestBase.oneStepInner();
		  TestBase.CarTransmission();
		  
		  
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
