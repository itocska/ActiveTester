package ecdh;

import org.openqa.selenium.WebDriverException;

public class RegisterActivatefillDetailsDeleteUser {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("RegisterActivatefillDetailsDeleteUser", 1);
		try {
		  TestBase.registerUser(TestBase.cuser, TestBase.password1);
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
