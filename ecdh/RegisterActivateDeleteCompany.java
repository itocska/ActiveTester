package ecdh;

import java.util.Random;

import org.openqa.selenium.WebDriverException;

public class RegisterActivateDeleteCompany extends TestBase {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("RegisterActivateDeleteCompany", 1);
		//VideoRecord.startRecording("RegisterActivateDeleteCompany");
		
		try {
		  Random rand = new Random();
		  Integer randomNum = rand.nextInt(3000000 - 1) + 1;
			
		  String companyEmail = TestBase.companyUser;
		  String companyName = "Teszt Ceg " + randomNum;
		  TestBase.registerCompany(companyName, companyEmail);
		  TestBase.activateCompany(true, companyEmail);
		  TestBase.adminLogin();
		  TestBase.adminActivatecompany(companyName);
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
