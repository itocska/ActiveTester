package ecdh;

import org.openqa.selenium.WebDriverException;

import ecdh.TestBase;

public class RegisterActivateDeleteUser {
	
	public static void main(String[] args) throws Throwable {
	
		
		TestBase.main("RegisterActivateDelete", 0);
		try {
			TestBase.registerUser(TestBase.personalUser, TestBase.personalPassword);
			TestBase.activateUser();
		  //TestBase.deleteUser();
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
