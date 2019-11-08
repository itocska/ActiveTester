package ecdh;

import org.openqa.selenium.WebDriverException;

public class DocumentGenerator {
	public static void main(String[] args) throws Throwable {
		
		TestBase.main("DocumentGenerator", 0);
		try {
			
			//required FillUserPersonalData
		  TestBase.login(TestBase.companyUser, TestBase.companyPassword);
		  TestBase.oneStepInner();
		  //minden dokumentum
		  TestBase.dgSelectAllBuyDocument();
		  //1. Átadás-átvételi: Autó
		  TestBase.dgSellHandoverReceiptCar();
		  //2. Átadás-átvételi: Kellékek
		  TestBase.dgSellHandoverReceiptAccessories();
		  //3. Átadás-átvételi: Vételár
		  TestBase.dgSellHandoverReceiptPrice();
		  //4. Bizományból kiadás
		  TestBase.dgSellHandoverConsignerEject();
		  //5. Bizományosi szerződés
		  TestBase.dgSellHandoverConsignerContract();
		  //6. Foglaló
/*		  TestBase.dgSellBooking();
		  //7. Meghatalmazás: Átírás
		  TestBase.dgSellAuthorizationRewriting();
		  //8. Meghatalmazás: Műszaki vizsga
		  TestBase.dgSellAuthorizationTechnicalExam();
		  //9. Meghatalmazás: Regisztrációs adó
		  TestBase.dgSellAuthorizationRegistrationTax();
		  //10. Átadás-átvételi: Átírási ktg
		  TestBase.dgSellHandoverTranscriptionCost();
		  //11. Adásvételi szerződés
		  TestBase.dgSellSalesContract();
		  //12. Állapotlap
		  TestBase.dgSellStatusSheet();
*/
		  
		  
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
