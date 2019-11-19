package ecdh;

import org.openqa.selenium.WebDriverException;

import ecdh.TestBase;

public class ImportCarSearch extends TestBase {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("ImportCarSearch", 0);
		try {

		  TestBase.goToPage(TestBase.url + "/hu/importautok");
		  Thread.sleep(3000);
		  TestBase.passShepherd();
		  TestBase.importCarSearch();
		  //test
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
