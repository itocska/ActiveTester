package ecdh;

import org.openqa.selenium.WebDriverException;

public class ServiceSearch {
	public static void main(String[] args) throws Throwable {
		
		TestBase.main("serviceSearch", 0);
		try {
		  TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  TestBase.serviceSearch();
		  
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
