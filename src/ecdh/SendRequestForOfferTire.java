package ecdh;

import org.openqa.selenium.WebDriverException;

public class SendRequestForOfferTire {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("SendRequestForOfferTire", 0);
		try {
			TestBase.login(TestBase.userX);
		  TestBase.oneStepInner();
		  String requestId = TestBase.SendRequestTire();
		  System.out.println("REQID" + requestId);
		  TestBase.userLogout();
		  TestBase.login(TestBase.userX);
		  String price = TestBase.checkRequest(requestId);
		  String companyName = TestBase.GetCompanyName();
		  Log.log("C�g:" + companyName);
		  Log.log("Aj�nlott �r:" + price);
		  TestBase.userLogout();
		  TestBase.login(TestBase.userX);
		  TestBase.checkRequestOffer(companyName, price);
		  TestBase.sendRequestFinalOrder();
		  TestBase.userLogout();
		  TestBase.login(TestBase.userX);
		  TestBase.checkRequestFinalOrder(price);

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
