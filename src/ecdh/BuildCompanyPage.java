package ecdh;

import org.openqa.selenium.WebDriverException;

public class BuildCompanyPage extends TestBase {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("BuildCompanyPage", 0);
		try {
		  LoginCompany.main(args);
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
