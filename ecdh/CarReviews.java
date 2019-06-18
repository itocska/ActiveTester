package ecdh;

import org.openqa.selenium.WebDriverException;

import ecdh.TestBase;

public class CarReviews extends TestBase {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("CarReviews", 0);
		try {
		  TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  TestBase.goToPage(TestBase.url + "/hu/jarmuertekeles");
		  Thread.sleep(3000);
		  TestBase.passShepherd();
		  TestBase.CarReviews();
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
