package ecdh;

import org.openqa.selenium.WebDriverException;

public class SendRequestForOfferPart {
	public static void main(String[] args) throws Throwable {
		
		//Alkatrész kereskedéssel kell rendelkezzen a cég, és az értesítéseket be kell állítani(default érték hogy ne kapjon)
		
		TestBase.main("SendRequestForOfferPart", 0);
		try {
			TestBase.login(TestBase.personalUser, TestBase.personalPassword);
			TestBase.oneStepInner();
			String requestId = TestBase.SendRequestPart();
			System.out.println("REQID" + requestId);
			TestBase.logout();
			
			TestBase.login(TestBase.companyUser, TestBase.companyPassword);
			
			String price = TestBase.checkRequestPart(requestId);
			String companyName = TestBase.GetCompanyName();
			Log.log("Cég:" + companyName);
			Log.log("Ajánlott ár:" + price);
			TestBase.logout();
			
			TestBase.login(TestBase.personalUser, TestBase.personalPassword);
			TestBase.checkRequestOfferPart(companyName, price);
			
			TestBase.sendRequestFinalOrder();
			TestBase.logout();
			
			TestBase.login(TestBase.companyUser, TestBase.companyPassword);
			TestBase.checkRequestFinalOrderPart(price);
		  
		  
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
