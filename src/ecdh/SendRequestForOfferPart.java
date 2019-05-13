package ecdh;

import org.openqa.selenium.WebDriverException;

public class SendRequestForOfferPart {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("SendRequestForOfferPart", 0);
		try {
		  TestBase.login(TestBase.userX);
		  TestBase.oneStepInner();
		  String requestId = TestBase.SendRequestPart();
		  System.out.println("REQID" + requestId);
		  TestBase.userLogout();
		  TestBase.login(TestBase.userX);
		  TestBase.checkRequestPart(requestId);
		  String companyName = TestBase.GetCompanyName();
		  TestBase.userLogout();
		  TestBase.login(TestBase.userX);
		  TestBase.checkRequestOffer(companyName, "2000");
		  
		  
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