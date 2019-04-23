package ecdh;

import org.openqa.selenium.WebDriverException;

public class AddNewCarEventGapInsurance {
	public static void main(String[] args) throws Throwable {
		
		TestBase.url = "https://ecdh.hu";
		TestBase.main("AddNewCarEventGapInsurance", 0);
		try {
		  TestBase.login("vorosborisz@gmail.com", "letstest");
		  TestBase.oneStepInner();
		  TestBase.addNewCarEventGapInsurance();
		  
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
