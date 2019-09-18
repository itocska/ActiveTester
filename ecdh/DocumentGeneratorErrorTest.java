package ecdh;

import org.openqa.selenium.WebDriverException;

public class DocumentGeneratorErrorTest {
	public static void main(String[] args) throws Throwable {
		
		TestBase.main("DocumentGenerator", 0);
		try {
		  TestBase.login(TestBase.companyUser, TestBase.companyPassword);
		  TestBase.oneStepInner();
		  TestBase.documentGeneratorErrorTest();
		  
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
