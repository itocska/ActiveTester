package ecdh;

import java.util.Random;

import org.openqa.selenium.WebDriverException;

public class LeadCapturePagesTest extends TestBase {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("LeadCapturePagesTest", 1);
		//VideoRecord.startRecording("RegisterActivateDeleteCompany");
		
		try {
		  
			TestBase.registrationFirst();
			TestBase.deleteUser();
			
			TestBase.registrationSecond();
			TestBase.deleteUser();
			
			TestBase.registrationThird();
			TestBase.deleteUser();
			
		} 
		catch (AssertionError|WebDriverException e) {
			Log.error = true;
			Log.log(e.getMessage().toString());
			System.out.println("Exception occurred");
			
			throw e;
		}
		//VideoRecord.stopRecording();
		TestBase.close();
		
	}
}
