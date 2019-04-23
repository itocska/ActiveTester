package ecdh;

import org.openqa.selenium.WebDriverException;

public class RegisterWithoutAcceptRules extends TestBase {
	
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.url = "https://rc.ecdh.hu";
		TestBase.main("RegisterWithoutAcceptRules", 1);
		try {
		  TestBase.registerUser("vorosborisz@gmail.com", "letstest", false);
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
