package ecdh;

import org.openqa.selenium.WebDriverException;

public class AdminThings {
	public static void main(String[] args) throws Throwable {
		
		//VideoRecord.startRecording("AdminThings");

		
		
		TestBase.main("AdminThings", 1);
		try {
		  TestBase.adminLogin();
		  //TestBase.deleteCompany("Teszt Ceg 32");
		  //TestBase.deleteUser(Testbase.personalUser);

		} 
		catch (AssertionError|WebDriverException e) {
			Log.error = true;
			Log.log(e.getMessage().toString());
			System.out.println("Exception occurred");
			
			throw e;
		}
		
		TestBase.close();
		//VideoRecord.stopRecording();
		
	}
}
