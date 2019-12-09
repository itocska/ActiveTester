package ecdh;

import org.openqa.selenium.WebDriverException;

public class CarLimit  extends TestBase {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("CarLimit", 0);
		try {
		TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  TestBase.goToPage(TestBase.url + "/hu/csomag-elofizetes");
		  Thread.sleep(3000);
		  TestBase.passShepherd();
		  
		  TestBase.CarLimit();
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
