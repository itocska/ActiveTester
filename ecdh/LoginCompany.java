package ecdh;

import org.openqa.selenium.WebDriverException;

public class LoginCompany {
	
	public static final String USER_DIR = "user.dir";
    public static final String DOWNLOADED_FILES_FOLDER = "downloadFiles";

	
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.url = "https://rc.ecdh.hu";
		TestBase.main("LoginCompany", 0);
		try {
		  String companyEmail = "ecdhtest@gmail.com";
		  TestBase.login(companyEmail, "letstest");
		  //TestBase.activateCompany(false, companyEmail);
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
