package ecdh;

import org.openqa.selenium.WebDriverException;

public class DocumentGenerator {
	public static void main(String[] args) throws Throwable {
		
		TestBase.main("DocumentGenerator", 0);
		try {
			
			//required FillUserPersonalData
		  TestBase.login(TestBase.companyUser, TestBase.companyPassword);
		  TestBase.oneStepInner();
		  
		  //TestBase.giveDocumentGeneratorAvailability();
		  
		  //minden dokumentum
		  TestBase.dgSelectAllBuyDocument();
		  //1. Átadás-átvételi: Autó
		  TestBase.dgBuyHandoverReceiptCar();
		  //2. Átadás-átvételi: Kellékek
		  TestBase.dgBuyHandoverReceiptAccessories();
		  //3. Átadás-átvételi: Vételár
		  TestBase.dgBuyHandoverReceiptPrice();
		  //4. Bizományból kiadás
		  TestBase.dgBuyHandoverConsignerEject();
		  //5. Bizományosi szerződés
		  TestBase.dgBuyHandoverConsignerContract();
		  //6. Foglaló
		  TestBase.dgBuyBooking();
		  //7. Meghatalmazás: Átírás
		  TestBase.dgBuyAuthorizationRewriting();
		  //8. Meghatalmazás: Műszaki vizsga
		  TestBase.dgBuyAuthorizationTechnicalExam();
		  //9. Meghatalmazás: Regisztrációs adó
		  TestBase.dgBuyAuthorizationRegistrationTax();
		  //10. Átadás-átvételi: Átírási ktg
		  TestBase.dgBuyHandoverTranscriptionCost();
		  //11. Adásvételi szerződés
		  TestBase.dgBuySalesContract();
		  //12. Állapotlap
		  TestBase.dgBuyStatusSheet();
		  TestBase.dbCheclAllBuyDoc();
		  
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
