package ecdh;

import org.openqa.selenium.WebDriverException;

import ecdh.TestBase;

public class AddNewCarError extends TestBase {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("AddNewCarError", 0);
		try {
		  TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  Thread.sleep(3000);
		  TestBase.passShepherd();
		  TestBase.addNewCarError();
		  //test
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
