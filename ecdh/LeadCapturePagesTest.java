package ecdh;

import java.util.Random;

import org.openqa.selenium.WebDriverException;

public class LeadCapturePagesTest extends TestBase {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("LeadCapturePagesTest", 1);
		
		try {
			
			TestBase.registrationFirst(TestBase.personalUser, TestBase.personalPassword);
			//szerviznaplo-alkalmazas-autosoknak	
			TestBase.deleteUser();
			
			TestBase.registrationSecond(TestBase.personalUser, TestBase.personalPassword);
			//hasznalt-auto-eladas
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
