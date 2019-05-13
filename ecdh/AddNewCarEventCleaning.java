package ecdh;

import org.openqa.selenium.WebDriverException;

public class AddNewCarEventCleaning {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("AddNewCarEventCleaning", 0);
		try {
		  TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  TestBase.oneStepInner();
		  TestBase.addNewCarEventCleaning();
		  
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
