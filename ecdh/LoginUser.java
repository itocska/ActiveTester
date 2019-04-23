package ecdh;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.openqa.selenium.WebDriverException;





public class LoginUser {
	static Properties prop = new Properties();
	public static void main(String[] args) throws Throwable {
		
		System.out.println(System.getProperty("os.name"));
		
		
		String path = System.getProperty("user.dir");
		
		System.out.println(path);
		InputStream input = new FileInputStream(path + "/src/config/config.properties");
	    prop.load(input);
	    
		TestBase.url = prop.getProperty("url");
		TestBase.main("LoginUser", 0);
		try {
		  TestBase.login((prop.getProperty("usr")),prop.getProperty("pass"));
		 
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

