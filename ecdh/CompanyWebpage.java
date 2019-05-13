package ecdh;

import org.openqa.selenium.WebDriverException;

public class CompanyWebpage {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("CompanyWebpage", 0);
		try {
		  TestBase.login(TestBase.companyUser, TestBase.companyPassword);
		  TestBase.buildCompanyPage();
		  
		  
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
