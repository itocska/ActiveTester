package ecdh;

import org.openqa.selenium.WebDriverException;

import ecdh.TestBase;

public class FillCarDetail extends TestBase {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("FillCarDetail", 0);
		try {
		  TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  TestBase.oneStepInner();
		  Thread.sleep(3000);
		  TestBase.passShepherd();
		  TestBase.fillCarDetail();
		  
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
