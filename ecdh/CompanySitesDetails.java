package ecdh;

import org.openqa.selenium.WebDriverException;

import ecdh.TestBase;

public class CompanySitesDetails extends TestBase {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("CompanySitesDetails", 0);
		try {
			
		  TestBase.login(TestBase.companyUser, TestBase.companyPassword);
		  Thread.sleep(3000);
		  
		  TestBase.companySitesDetails();
		  
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
