package ecdh;

import org.openqa.selenium.WebDriverException;

public class AddNewCarEventCompulsoryInsurance {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("AddNewCarEventCompulsoryInsurance", 0);
		try {
		  TestBase.login(TestBase.userX);
		  TestBase.oneStepInner();
		  TestBase.addNewCarEventCompulsoryInsurance();
		  
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