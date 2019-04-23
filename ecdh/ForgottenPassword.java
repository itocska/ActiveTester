package ecdh;

import org.openqa.selenium.WebDriverException;

public class ForgottenPassword extends TestBase {
	public static void main(String[] args) throws Throwable {
		
		TestBase.url = "https://rc.ecdh.hu";
		TestBase.main("ForgottenPassword", 1);
		try {
		  TestBase.forgottenPassword("vorosborisz@gmail.com", "vivaretina", "letstest");
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
