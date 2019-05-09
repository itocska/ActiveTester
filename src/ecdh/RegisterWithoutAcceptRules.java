package ecdh;

import org.openqa.selenium.WebDriverException;

public class RegisterWithoutAcceptRules extends TestBase {
	
	public static void main(String[] args) throws Throwable {
		
		
		
		TestBase.main("RegisterWithoutAcceptRules", 1);
		try {
		  TestBase.registerUser(TestBase.user1, TestBase.password1, false);
		  TestBase.goToPage("https://rc.ecdh.hu");
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
