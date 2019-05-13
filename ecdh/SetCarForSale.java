package ecdh;

import org.openqa.selenium.WebDriverException;

public class SetCarForSale {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("SetCarForSale", 1);
		try {
		  TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  TestBase.oneStepInner();
		  TestBase.setCarForSale();
		  
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
