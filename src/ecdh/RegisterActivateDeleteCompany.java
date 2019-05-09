package ecdh;

import java.util.Random;

import org.openqa.selenium.WebDriverException;

public class RegisterActivateDeleteCompany extends TestBase {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("RegisterActivateDeleteCompany", 1);
		//VideoRecord.startRecording("RegisterActivateDeleteCompany");
		
		try {
		  Random rand = new Random();
		  Integer randomNum = 1 + rand.nextInt((3000000 - 1) + 1);
		  String randNum = String.valueOf(randomNum);
			
		  String companyEmail = TestBase.cuser;
		  String companyName = "Teszt Ceg" + randNum;
		  TestBase.registerCompany(companyName, companyEmail);
		  TestBase.activateCompany(true, companyEmail);
		  TestBase.adminLogin();
		  TestBase.adminActivatecompany(companyEmail);
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
