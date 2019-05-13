package ecdh;

import org.openqa.selenium.WebDriverException;

public class CheckCarProperties {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("CheckCarProperties", 0);
		try {
		  TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  TestBase.oneStepInner();
		  //TestBase.selectCar("ABC-123");
		  TestBase.checkCarProperties();
		  
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
