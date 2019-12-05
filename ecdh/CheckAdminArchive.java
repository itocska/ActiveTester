package ecdh;

import org.openqa.selenium.WebDriverException;

public class CheckAdminArchive {
	public static void main(String[] args) throws Throwable {
		
		TestBase.main("CheckAdminArchive", 0);
		try {
		  TestBase.adminLogin();
		  TestBase.oneStepInner();
		  TestBase.checkAdminArchive();
		  
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
