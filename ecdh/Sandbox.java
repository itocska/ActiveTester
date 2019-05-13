package ecdh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Sandbox {
	public static void main(String[] args) throws Throwable {

		WebDriver driver;
		System.setProperty("webdriver.chrome.driver", "/www/webdrivers/chromedriver");
		driver = new ChromeDriver();
		
		Log.testname = "Register";
		Log.driver = driver;
		
		WebElement element;
		
		try {
			
			driver.get(TestBase.url);
			
			driver.findElement(By.name("pass")).sendKeys("kecskesajt");
			driver.findElement(By.className("btn-success")).click();
			
			driver.get(TestBase.url+"/hu/car-users/activate/success/7dfb2791-dac2-4140-adcb-e24b1ef1ed9e");
			
			driver.findElement(By.className("ok")).click();
			Log.log("Accept cookies");
			
			String body = driver.findElement(By.tagName("body")).getText();
			assertTrue("Registration succeed", body.contains("Sikeres"));
			
			String actualUrl = driver.getCurrentUrl();
			assertTrue("Registration succeed", actualUrl.contains("success"));
			
			
			
			
			
		} catch (AssertionError|WebDriverException e) {
			System.out.println(e.getMessage().toString());
			
			throw e;
		}
		catch(Exception e) {		
			System.out.println(e.getMessage().toString());
			
			throw e;
		}
		
		
		driver.close();
	}
}
