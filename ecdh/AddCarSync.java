package ecdh;

import org.openqa.selenium.WebDriverException;

public class AddCarSync  extends TestBase {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("AddCarSync", 0);
		try {
	      
		  TestBase.login(TestBase.companyUser, TestBase.companyPassword);
		  TestBase.AddCarSync();
		  
		  
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
