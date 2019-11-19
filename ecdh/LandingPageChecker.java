package ecdh;

import org.openqa.selenium.WebDriverException;

import ecdh.TestBase;

public class LandingPageChecker extends TestBase {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("LandingPageChecker", 0);
		try {

		  TestBase.landingPageUsedCarSell();
		  TestBase.landingPageVehicleAnalysis();
		  TestBase.landingPageServiceLog();
		  TestBase.landingPageCarSellForDealer();

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