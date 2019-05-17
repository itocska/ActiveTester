package ecdh;

import org.openqa.selenium.WebDriverException;

import com.thoughtworks.selenium.webdriven.commands.Click;

public class AddNewCarEventBodyRepair {
  public static void main(String[] args) throws Throwable {
		
		TestBase.main("AddNewCarEventBodyRepair", 0);
		try {
		  TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		  TestBase.oneStepInner();
		  TestBase.addNewCarEventBodyRepair();
		  
		  
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
