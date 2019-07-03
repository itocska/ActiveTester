package ecdh;

import org.openqa.selenium.WebDriverException;

public class SendRequestForOfferPart {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("SendRequestForOfferPart", 0);
		try {
		  TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  TestBase.oneStepInner();
		  String requestId = TestBase.SendRequestPart();
		  System.out.println("REQID" + requestId);
		  TestBase.userLogout();
		  TestBase.login(TestBase.companyUser, TestBase.companyPassword);
		  TestBase.checkRequestPart(requestId);
		  String companyName = TestBase.GetCompanyName();
		  TestBase.userLogout();
		  TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  TestBase.checkRequestOfferPart(companyName);
		  
		  
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
