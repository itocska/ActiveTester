package ecdh;

import org.openqa.selenium.WebDriverException;

public class AddNewCarEventMontlySurvey {
	public static void main(String[] args) throws Throwable {
		
		TestBase.main("MontlySurvey", 0);
		try {
		  TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  TestBase.oneStepInner();
		  TestBase.addNewCarEventMontlySurvey();
		  
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
