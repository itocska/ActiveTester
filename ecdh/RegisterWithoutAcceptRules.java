package ecdh;

import org.openqa.selenium.WebDriverException;

public class RegisterWithoutAcceptRules extends TestBase {
	
	public static void main(String[] args) throws Throwable {
		
		
		
		TestBase.main("RegisterWithoutAcceptRules", 1);
		try {
		  TestBase.registerUser(TestBase.personalUser, TestBase.personalPassword, false);
		  TestBase.goToPage(TestBase.url);
		  TestBase.registerUserWrongEmail();
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
