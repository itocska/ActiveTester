package ecdh;

import org.openqa.selenium.WebDriverException;

import ecdh.TestBase;

public class AddNewCar extends TestBase {
	public static void main(String[] args) throws Throwable {
		
		TestBase.url = "https://rc.ecdh.hu";
		TestBase.main("AddNewCar", 0);
		try {
		  TestBase.login("vorosborisz@gmail.com", "letstest");
		  TestBase.goToPage(TestBase.url + "/hu/sajat-autom-felvitel");
		  Thread.sleep(3000);
		  TestBase.passShepherd();
		  TestBase.addNewCar();
		  
		  
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
