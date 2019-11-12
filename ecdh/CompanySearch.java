package ecdh;

import org.openqa.selenium.WebDriverException;

public class CompanySearch {
	public static void main(String[] args) throws Throwable {
		
		TestBase.main("CompanySearch", 0);
		try {
			
		  TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  TestBase.companySearch();
		  TestBase.routePlanner();
		  
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
