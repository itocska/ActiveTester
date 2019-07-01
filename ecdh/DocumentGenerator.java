package ecdh;

import org.openqa.selenium.WebDriverException;

public class DocumentGenerator {
	public static void main(String[] args) throws Throwable {
		
		TestBase.main("DocumentGenerator", 0);
		try {
		  TestBase.login(TestBase.companyUser, TestBase.companyPassword);
		  TestBase.oneStepInner();
		  TestBase.documentGenerator();
		  
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
