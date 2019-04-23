package ecdh;

import org.openqa.selenium.WebDriverException;

public class CompanyWebpage {
	public static void main(String[] args) throws Throwable {
		
		TestBase.url = "https://rc.ecdh.hu";
		TestBase.main("CompanyWebpage", 0);
		try {
		  TestBase.login("ecdhtest@gmail.com", "letstest");
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
