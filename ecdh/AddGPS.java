package ecdh;

import org.openqa.selenium.WebDriverException;

public class AddGPS  extends TestBase {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("AddNewCar", 0);
		try {
		  TestBase.addGPS();
		  
		  
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
