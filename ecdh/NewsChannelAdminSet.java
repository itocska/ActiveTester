package ecdh;

import org.openqa.selenium.WebDriverException;

public class NewsChannelAdminSet{
	
	public static void main(String[] args) throws Throwable{
	
		TestBase.main("NewsChannelAdminSet", 0);
		try {
			
		  TestBase.adminLogin();
		  TestBase.oneStepInner();
		  //Teszt mindig a HVG RSS-el
		  TestBase.setNewRSSChannel();
		  TestBase.checkRSSChannel();
		  TestBase.deleteTestRSSChannel();
		  
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