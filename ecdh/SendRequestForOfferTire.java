package ecdh;

import org.openqa.selenium.WebDriverException;

public class SendRequestForOfferTire {
	public static void main(String[] args) throws Throwable {
		
		TestBase.url = "https://rc.ecdh.hu";
		TestBase.main("SendRequestForOfferTire", 0);
		try {
			TestBase.login("vorosborisz@gmail.com", "letstest");
		  TestBase.oneStepInner();
		  String requestId = TestBase.SendRequestTire();
		  System.out.println("REQID" + requestId);
		  TestBase.userLogout();
		  TestBase.login("ecdhtest@gmail.com", "letstest");
		  String price = TestBase.checkRequest(requestId);
		  String companyName = TestBase.GetCompanyName();
		  Log.log("Cég:" + companyName);
		  Log.log("Ajánlott ár:" + price);
		  TestBase.userLogout();
		  TestBase.login("vorosborisz@gmail.com", "letstest");
		  TestBase.checkRequestOffer(companyName, price);
		  TestBase.sendRequestFinalOrder();
		  TestBase.userLogout();
		  TestBase.login("ecdhtest@gmail.com", "letstest");
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
