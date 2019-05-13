package ecdh;

import org.openqa.selenium.WebDriverException;

public class LoginUser {
	
	public static void main(String[] args) throws Throwable {
		
		TestBase.main("LoginUser", 0);
		try {		
		  //TestBase.login(TestBase.user1,TestBase.password1);
			TestBase.login(TestBase.personalUser, TestBase.personalPassword);
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