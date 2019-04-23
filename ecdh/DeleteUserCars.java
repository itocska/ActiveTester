package ecdh;

import org.openqa.selenium.WebDriverException;

public class DeleteUserCars {
	public static void main(String[] args) throws Throwable {
		
		TestBase.url = "https://rc.ecdh.hu";
		TestBase.main("DeleteUserCars", 0);
		try {
		  TestBase.login("vorosborisz@gmail.com", "letstest");
		  TestBase.deleteUserCars();
		  
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
