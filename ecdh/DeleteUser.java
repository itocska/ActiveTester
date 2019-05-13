package ecdh;

import org.openqa.selenium.WebDriverException;

public class DeleteUser {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("DeleteUser", 1);
		try {
		  if (args.length > 0) {
		    TestBase.login(args[0], args[1]);
		  } else {
			TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  }
		  TestBase.deleteUser();
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
