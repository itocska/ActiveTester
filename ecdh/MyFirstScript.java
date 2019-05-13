package ecdh;

import static org.junit.Assert.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class MyFirstScript {
		
	public static void main(String[] args) throws Throwable {

		WebDriver driver;
		System.setProperty("webdriver.chrome.driver", "/www/webdrivers/chromedriver");
		driver = new ChromeDriver();
		
		Log.testname = "MyFirstScript";
		
		System.out.println("HEY");
		
		try {
			
			driver.get(TestBase.url);
			Log.log("Go to URL");
			
			String actualUrl = driver.getCurrentUrl();
			Log.log("Check URL");

			driver.findElement(By.name("pass")).sendKeys("kecskesajt");
			driver.findElement(By.className("btn-success")).click();
			Log.log("Log in password protection");
			
			WebElement element = driver.findElement(By.className("fuel"));
			Actions actions = new Actions(driver);
			actions.moveToElement(element);
			actions.perform();
			Log.log("Scroll to FUEL");
	
			driver.close();
			
			
		} catch (AssertionError e) {
			Log.error = true;
			Log.log(e.getMessage().toString());
			System.out.println("Exception occurred");
		} catch(Exception e) {
			
			System.out.println(e.getMessage().toString());
			
			throw e;
            
		}
		
	}

}
