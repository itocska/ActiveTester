package ecdh;

import org.openqa.selenium.WebDriverException;

public class AddNewCarNotes {
	public static void main(String[] args) throws Throwable {
		
		
		TestBase.main("AddNewCarNotes", 0);
		try {
		  TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  TestBase.oneStepInner();
		  TestBase.addNewCarNotes();
		  TestBase.addNewCarNotes();
		  TestBase.addNewCarNotes();
		  TestBase.addNewCarNotes();
		  TestBase.addNewCarNotes();
		  TestBase.addNewCarNotes();
		  
		  TestBase.deleteCarNote();
		  TestBase.allCarNote();
		 
		  TestBase.deleteAllCarNote();
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
