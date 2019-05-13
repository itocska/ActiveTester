package ecdh;

import org.openqa.selenium.WebDriverException;

public class LoginCompany {
	
	public static final String USER_DIR = "user.dir";
    public static final String DOWNLOADED_FILES_FOLDER = "downloadFiles";

	
	public static void main(String[] args) throws Throwable {
		
		
		
		TestBase.main("LoginCompany", 0);
		try {
		  TestBase.login(TestBase.companyUser, TestBase.companyPassword);
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
