package ecdh;

import org.openqa.selenium.WebDriverException;

public class SendRequestForOfferTire {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("SendRequestForOfferTire", 0);
		try {
			TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  TestBase.oneStepInner();
		  String requestId = TestBase.SendRequestTire();
		  System.out.println("REQID" + requestId);
		  TestBase.userLogout();
		  TestBase.login(TestBase.companyUser, TestBase.companyPassword);
		  String price = TestBase.checkRequest(requestId);
		  String companyName = TestBase.GetCompanyName();
		  Log.log("Cég:" + companyName);
		  Log.log("Ajánlott ár:" + price);
		  TestBase.userLogout();
		  TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  TestBase.checkRequestOfferTire(companyName, price);
		  TestBase.sendRequestFinalOrder();
		  TestBase.userLogout();
		  TestBase.login(TestBase.companyUser, TestBase.companyPassword);
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
