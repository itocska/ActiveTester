package ecdh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.sun.glass.events.KeyEvent;

import de.svenjacobs.loremipsum.LoremIpsum;


public class TestBase {
	
	public static WebDriver driver;
	public static WebElement element;
	public static WebDriverWait wait;
	private static int close;
	public static String url;

	public static void main(String arg, int close) throws Throwable {

		//System.setProperty("webdriver.chrome.driver", "/www/webdrivers/chromedriver");
		String path = (new File("")).getAbsolutePath();
		System.setProperty("webdriver.chrome.driver", path + "/webdriver/chromedriver.exe");
		driver = new ChromeDriver();
		
		wait = new WebDriverWait(driver, 10);
		
		String window = driver.getWindowHandle();
		((JavascriptExecutor) driver).executeScript("alert('Test')");
		driver.switchTo().alert().accept();
		driver.switchTo().window(window);
		
		Log.testname = arg;
		System.out.println("start" + Log.testname);
		Log.driver = driver;
		
		
		TestBase.close = close;
		
		if (url.isEmpty() ) {
			url = "http://rc.ecdh.hu";
		}
		
		try {
			goToPage(url);
			unlockPage();
			acceptCookies();
			
		} catch (AssertionError|WebDriverException e) {
			Log.error = true;
			Log.log(e.getMessage().toString());
			System.out.println("Exception occurred");
			
			throw e;
		}
		
		
		
	}

	protected static void deleteUser() throws IOException, InterruptedException {
		click(".user-img");
		clickLinkWithText("Adatm�dos�t�s");
		clickLinkWithText("Fi�k t�rl�se");	
		click(".btn-red");
		Log.log("Felhaszn�l� t�rl�se.");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-success")));
		Log.log("Felhaszn�l� t�r�lve.");
	}

	protected static void activateUser() throws Exception {
		/*
		driver.get("https://gmail.com");
		
		driver.findElement(By.cssSelector("input[type=\"email\"]")).sendKeys("vorosborisz@gmail.com");
		driver.findElement(By.xpath("//*[text()='Next']")).click();
		
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type=password]")));

		driver.findElement(By.cssSelector("input[type=password]")).sendKeys("vivaretina");
		driver.findElement(By.xpath("//*[text()='Next']")).click();
		Log.log("Login Gmail");
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='ECDH regisztr�ci� meger�s�t�se']")));
		driver.findElement(By.xpath("//*[text()='ECDH regisztr�ci� meger�s�t�se']")).click();
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText("Szem�lyes fi�k aktiv�l�sa")));
		driver.findElement(By.partialLinkText("Szem�lyes fi�k aktiv�l�sa")).click();
		Log.log("New user account activation");
		
		System.out.println(driver.getTitle());
		
		for (String winHandle : driver.getWindowHandles()) { 
	        System.out.println(winHandle);
	        driver.switchTo().window(winHandle);       
	    }
		
		System.out.println(driver.getTitle());
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='Aktiv�l�s']")));
		assertTrue("Registration succeed", driver.getPageSource().contains("befejezte"));
		Log.log("Activation succeed");
		*/
		driver.get(Gmail.getMails("{email}", "{password}", "ECDH", "href=\"(.*?)\">Szem�lyes fi�k aktiv�l�sa"));
		
		
	}

	private static void acceptCookies() throws IOException, InterruptedException {
		Log.log("Accept cookies");
		try {
		  driver.findElement(By.className("cc-btn")).click();
		  sleep(2000);
		} catch (NoSuchElementException e){
		  Log.log("ERROR - No cookie acception message.");
		}
	}

	private static void unlockPage() throws IOException {
		//driver.findElement(By.name("pass")).sendKeys("kecskesajt");
		//driver.findElement(By.className("btn-success")).click();
		//Log.log("Password protection unlocked.");
	}

	public static void goToPage(String url) throws IOException {
		driver.get(url);
		Log.log("Start");
	}
	
	public static void fillName(String name, String text) throws IOException {
      if (driver.findElements(By.cssSelector("input[name=\"" + name + "\"]")).size() != 0) {
	    driver.findElement(By.cssSelector("input[name=\"" + name + "\"]")).clear();
	    if (name == "doors") {
	      driver.findElement(By.cssSelector("input[name=\"" + name + "\"]")).sendKeys("3");
	    } else {
	      driver.findElement(By.cssSelector("input[name=\"" + name + "\"]")).sendKeys(text);
	    }
      } else {
    	driver.findElement(By.cssSelector("textarea[name=\"" + name + "\"]")).clear();
  	    driver.findElement(By.cssSelector("textarea[name=\"" + name + "\"]")).sendKeys(text); 
      }
      
      Log.log(name + " field filled with: " + text);
	}
	
	protected static void registerUser(String user, String password, Boolean obligatory) throws IOException {
		// obligatory checkboxes not checked test
		clickLinkWithText("Regisztr�ci�");
		Log.log("Click Registraion");
		
		try 
		{
		  element = driver.findElement(By.className("ok"));
		  element.click();
		} catch (NoSuchElementException e) {
			
		}
		Log.log("Accept cookies");
			
		fillName("user[username]", user);
		fillName("user[password]", password);
		fillName("user[confirm_password]", password);

		Log.log("K�telez� mez�k mell�z�se (felhaszn�l�si, adatv�delmi felt�telek).");
		
		click(".register");
		Log.log("Regisztr�ci� gomb megnyom�sa.");	
	
		assertTrue("Regisztr�l�s a k�telez�k n�lk�l blokkolva", !driver.getPageSource().contains("A regisztr�ci�d sikeres"));
		Log.log("Regisztr�l�s blokkolva"); 
	}

	protected static void registerUser(String username, String password) throws IOException {
		clickLinkWithText("Regisztr�ci�");
		Log.log("Click Registraion");
		
		try 
		{
		  element = driver.findElement(By.className("ok"));
		  element.click();
		} catch (NoSuchElementException e) {
			
		}
		Log.log("Accept cookies");
			
		fillName("user[username]", username);
		fillName("user[password]", password);
		fillName("user[confirm_password]", password);
		
		Actions actions = new Actions(driver);
		
		WebElement myElement = driver.findElement(By.xpath("//label[@for=\"user-accept-rules\"]"));
		WebElement parent = myElement.findElement(By.xpath(".."));
		actions.moveToElement(parent, 5, 5).click().build().perform();
		Log.log("Accept rules");
		
		myElement = driver.findElement(By.xpath("//label[@for=\"user-accept-rules2\"]"));
		parent = myElement.findElement(By.xpath(".."));
		actions.moveToElement(parent, 5, 5).click().build().perform();
		Log.log("Accept privacy terms");
		
		click(".register");
		Log.log("Click on Regisztr�ci�");
		
		
		//wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='A regisztr�ci�d sikeres']")));
		wait.until(ExpectedConditions.textToBePresentInElementLocated(By.className("feedback-page"), "regisztr�ci�d sikeres"));
		
		assertTrue("Registration succeed", driver.getPageSource().contains("A regisztr�ci�d sikeres"));
		Log.log("Register succeed"); 
		
	}

	public static void close() throws IOException {
		
		Log.log("Finished.");
		System.out.println("Inside" + Log.testname);
		Log.close();
		if (TestBase.close == 1) {
		  driver.close();
		}
	}

	public static void login(String username, String password) throws IOException {
		clickLinkWithText("Bel�p�s");
		Log.log("Click login");
		
		try 
		{
		  element = driver.findElement(By.className("ok"));
		  element.click();
		} catch (NoSuchElementException e) {
			
		}
		Log.log("Accept cookies");
			
		fillName("username", username);
		fillName("password", password);
		
		click(".btn-secondary");
		Log.log("Click on Login");
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("user-img")));
		//assertTrue("Login succeed", driver.getPageSource().contains("Bejelentkezve"));
		Log.log("Login succeed");
	}

	public static void select(String string, String string2) throws IOException {
		WebDriverWait wait = new WebDriverWait(driver, 15);
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("select[name='" + string + "']")));
		
		WebElement mySelectElement = driver.findElement(By.cssSelector("select[name='" + string + "']"));
		Select dropdown = new Select(mySelectElement);
		dropdown.selectByVisibleText(string2);
		Log.log(string2 + " selected from " + string);
	}

	public static void selectIndex(String string, int i) throws IOException {
		WebDriverWait wait = new WebDriverWait(driver, 15);
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("select[name='" + string + "']")));
		
		WebElement mySelectElement = driver.findElement(By.cssSelector("select[name='" + string + "']"));
		Select dropdown= new Select(mySelectElement);
		dropdown.selectByIndex(i);
		Log.log("Index " + i + " selected from " + string);
		
	}

	public static void registerCompany(String string, String email) throws IOException, AWTException, InterruptedException {
		WebElement element = driver.findElement(By.xpath("//a[contains(text(), \"Kattints ide\")]"));
		Actions actions = new Actions(driver);
		actions.moveToElement(element);
		actions.perform();
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(), \"Kattints ide\")]")));

		driver.findElement(By.xpath("//a[contains(text(), \"Kattints ide\")]")).click();
		Log.log("C�ges regisztr�ci� link");
		
		
		driver.findElement(By.cssSelector("input[name='user[username]']")).sendKeys(email);
		driver.findElement(By.cssSelector("input[name='main_company[name]']")).sendKeys(string);

		driver.findElement(By.className("multiselect")).click();
		
		/*org.openqa.selenium.Point coordinates = driver.findElement(By.className("multiselect")).getLocation();
		Robot robot = new Robot();
		robot.mouseMove(coordinates.getX(),coordinates.getY()+100);*/
		
		element = driver.findElement(By.className("multiselect-container"));
		WebElement element2 = driver.findElement(By.cssSelector("li:nth-child(8)"));
		actions = new Actions(driver);
		actions.moveToElement(element);
		actions.moveToElement(element2).click();
		actions.perform();
		sleep(3000);
		
		driver.findElement(By.cssSelector(".multiselect")).click();

		driver.findElement(By.id("user-password")).sendKeys("letstest");
		driver.findElement(By.id("user-confirm-password")).sendKeys("letstest");
		
		WebElement myElement = driver.findElement(By.xpath("//label[@for=\"user-accept-rules2\"]"));
		WebElement parent = myElement.findElement(By.xpath(".."));
		actions.moveToElement(parent, 5, 5).click().build().perform();
		Log.log("Accept privacy terms");
		
		myElement = driver.findElement(By.xpath("//label[@for=\"user-accept-rules\"]"));
		parent = myElement.findElement(By.xpath(".."));
		actions.moveToElement(parent, 5, 5).click().build().perform();
		Log.log("Accept rules");
		
		
		
		
		driver.findElement(By.className("register")).click();

		
		
		

		
		
		
	}

	public static void activateCompany(Boolean realActivation, String companyEmail) throws Exception {
		if (realActivation) {
		  driver.get(Gmail.getMails("ecdhtest@gmail.com", "letstest", "ECDH", "href=\"(.*?)\">Addig is tekintsd meg"));
		}
		
		Log.log("Email aktiv�l�s sikeres.");
		
		TestBase.goToPage("https://rc.ecdh.hu/hu/ceg-adat-modositas");
		driver.findElement(By.cssSelector("textarea[name='description']")).sendKeys("R�vid le�r�s teszt");
		
		Random rand = new Random();
		Integer randomNum = 1000000000 + rand.nextInt((999999999 - 1) + 1);
		String amount = String.valueOf(randomNum);
		
		driver.findElement(By.cssSelector("input[name='reg_no']")).sendKeys(amount);
		driver.findElement(By.cssSelector("input[name='tax_no']")).sendKeys(amount + "9");
		driver.findElement(By.cssSelector("input[name='acc_number_eu']")).sendKeys(amount + "9");
		driver.findElement(By.cssSelector("input[name='acc_number_hu']")).sendKeys(amount + "9");
		
		TestBase.select("lang", "Magyar");
		
		driver.findElement(By.cssSelector("input[name='car_address[loc_zip_id_ac]']")).clear();
		driver.findElement(By.cssSelector("input[name='car_address[loc_zip_id_ac]']")).sendKeys("1016");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(), \"1016\")]")));
		
		driver.findElement(By.xpath("//a[contains(text(), \"1016\")]")).click();
		driver.findElement(By.cssSelector("input[name='car_address[street]']")).sendKeys("M�sz�ros");
		TestBase.select("car_address[street_type]", "utca");
		driver.findElement(By.cssSelector("input[name='car_address[street_num]']")).sendKeys("25");
		driver.findElement(By.cssSelector("input[name='webpage']")).sendKeys("http://test.com");
		driver.findElement(By.cssSelector("input[name='company_owner[car_user][user][last_name]']")).sendKeys("Teszt");
		driver.findElement(By.cssSelector("input[name='company_owner[car_user][user][first_name]']")).sendKeys("Eszter");
		
		rand = new Random();
		randomNum = 1000 + rand.nextInt((999 - 1) + 1);
		amount = String.valueOf(randomNum);
		driver.findElement(By.cssSelector("input[name='email']")).sendKeys("vorosborisz" + amount + "@gmail.com");
		
		TestBase.select("phone_country", "V�lasszon");
		
		driver.findElement(By.className("btn-primary")).click();
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), \"Sikeres\")]")));

		Log.log("Narancs mez�k kit�ltve.");
		goToPage("https://rc.ecdh.hu/hu/kijelentkezes");		
		
	}
	
	public static void forgottenPassword(String email, String emailpassword, String password) throws Exception {
		driver.findElement(By.partialLinkText("Elfelejtetted")).click();
		driver.findElement(By.cssSelector("input[name='email_check']")).sendKeys(email);
		driver.findElement(By.className("btn-success")).click();
		
		driver.get(Gmail.getMails(email, emailpassword, "Elfelejtett", "href=\"(.*?)\">Jelsz�v�lt�s"));
		driver.findElement(By.id("password")).sendKeys(password);
		driver.findElement(By.id("confirm-password")).sendKeys(password);
		
		driver.findElement(By.id("form-button")).click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), \"Vissza\")]")));
		driver.findElement(By.xpath("//span[contains(text(), \"Vissza\")]")).click();

		
		driver.findElement(By.id("username")).sendKeys(email);
		driver.findElement(By.id("password")).sendKeys(password);
		
		driver.findElement(By.className("btn-secondary")).click();
		Log.log("Click on Login");
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("user-img")));
		Log.log("Login succeed");

	}
	
	public static final int GYARTO = 1;
	public static final int MODELL = 2;
	//public static final int EVJARAT = 2;
	//public static final int HONAP = 3;
	
	public static String fillCarField(String inputField, String listField) throws IOException, InterruptedException {
		
		
		click(inputField);
		List<WebElement> elementWrappers = driver.findElements(By.cssSelector(listField + " li"));
		System.out.println(listField);
		System.out.println(elementWrappers);
		List<String> elements = new ArrayList<String>();
		
		for (WebElement element : elementWrappers) {
		  elements.add(element.findElement(By.tagName("a")).getText());
		}

		int size = elements.size();
		int randnMumber = new Random().nextInt(size);
		String randListElement = elements.get(randnMumber);
		
		driver.findElement(By.cssSelector(inputField)).sendKeys(randListElement);
		sleep(1000);
		driver.findElement(By.cssSelector(listField + " li")).click();
		sleep(1000);
		return randListElement;
		
		
	}
	
	public static String generatePlateNumber() {
		String letters = "";
        int n = 'Z' - 'A' + 1;
        for (int i = 0; i < 3; i++) {
            char c = (char) ('A' + Math.random() * n);
            letters += c;
        }
        
        String digits = "";
        int x = '9' - '0' + 1;
        for (int i = 0; i < 3; i++) {
            char c = (char) ('0' + Math.random() * x);
            digits += c;
        }
        
        String licensePlate = letters + "-" + digits;
        return licensePlate;
	}

	public static void addNewCar() throws IOException, InterruptedException, AWTException {
		randomSelect("car_year");
		randomSelect("car_month");
		sleep(2000);
		String manufacturer = fillCarField("#car-manufacturer-id", "#ui-id-1");
		sleep(2000);
		String model = fillCarField("#car-model-id", "#ui-id-2");
		sleep(2000);
		//click("#car-type-id");
		//click("#ui-id-3 li:first-child a");
		
		fillName("numberplate", generatePlateNumber());
		fillName("km", "120000");
		click(".btn-secondary");
		sleep(3000);
		passShepherd();
		sleep(1000);
		passShepherd();
		sleep(1000);
		passShepherd();
		sleep(1000);
		clickLinkWithText("Adatok szerkeszt�se");
		TestBase.select("petrol", "D�zel");
		randomSelect("car_condition");
		Random rand = new Random();
		long leftLimit = 11111111111111111L;
	    long rightLimit = 99999999999999999L;
	    long randomLong = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));
		fillName("vin", String.valueOf(randomLong));
		
		rand = new Random();
		Integer randomNum = rand.nextInt(999999999);
		fillName("motor_number", randomNum.toString());
		
		rand = new Random();
		randomNum = rand.nextInt(1999);
		fillName("power", randomNum.toString());
		
		rand = new Random();
		randomNum = rand.nextInt(4) + 1;
		//fillName("doors", randomNum.toString());
		fillName("doors", "3");
		sleep(25000);
		
		rand = new Random();
		randomNum = rand.nextInt(4) + 1;
		//fillName("seats", randomNum.toString());
		fillName("seats", "4");
		
		sleep(5000);
		
		randomSelect("make");
		randomSelect("car_offset");
		randomSelect("cylinder");
		randomSelect("warranty");
		randomSelect("enviromental_v9");
		
		rand = new Random();
		randomNum = rand.nextInt(200) + 100;
		fillName("trunk", randomNum.toString());
		
		rand = new Random();
		randomNum = rand.nextInt(2000) + 1000;
		fillName("engine_capacity", randomNum.toString());
		
		rand = new Random();
		randomNum = rand.nextInt(3200) + 100;
		fillName("net_weight", randomNum.toString());
		randomNum += 100;
		fillName("weight", randomNum.toString());
		
		click(".btn-secondary");
		Thread.sleep(3000);
		click(".btn-secondary");
		Thread.sleep(3000);
		
		List<WebElement> elements = driver.findElements(By.className("collapsed"));
		for (WebElement element : elements) {
			String name = element.getAttribute("data-target");
			Log.log(name + " collapsed");
			element.click();
			Thread.sleep(1500);
		} 
		
		elements = driver.findElements(By.tagName("select"));
		for (WebElement element : elements) {
		  String name = element.getAttribute("name");
		  randomSelect(name);
		}
		
		elements = driver.findElements(By.cssSelector(".checkbox label"));
		for (WebElement element : elements) {
		  rand = new Random();
		  randomNum = rand.nextInt(2);
		  if (randomNum == 0) {
		    element.click();
		  }
		}
		
		click(".btn-secondary");
		Thread.sleep(3000);
		
		Log.log("Aut� bek�ldve.");

		clickLinkWithText("1");
		assertTrue("Szerepel a forr�sban", driver.getPageSource().contains(manufacturer));
		Log.log("Aut� mentve. Gy�rt�: " + manufacturer);
		assertTrue("Szerepel a forr�sban", driver.getPageSource().contains(model));
		Log.log("Modell: " + model);

	}
	
	public static void addNewCarEventFuel() throws IOException, InterruptedException {
		clickLinkWithText("esem�ny");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-fueling")));
		click(".sprite-fueling");
		
		submit();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'A mez� nem lehet �res')]")));
		assertTrue("K�telez� mez�k valid�l�sa", driver.getPageSource().contains("A mez� nem lehet �res"));
		Log.log("K�telez� mez�k valid�l�sa."); 
		
		Random rand = new Random();
		Integer randomNum = 1 + rand.nextInt((30 - 1) + 1);
		String amount = String.valueOf(randomNum);
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		driver.findElement(By.cssSelector("input[name=\"fueling_date\"]")).clear();
		fillName("fueling_date", dateFormat.format(date));
		fillName("liter", amount);
		String fuelType = randomSelect("type");
		fillName("car_gas_station_id_ac", "m�sz�r");
		click("ul li.ui-menu-item:nth-child(2) a");
		
		
		submit();
		
		Log.log("Esem�ny: tankol�s bek�ldve.");
        onScreen(amount + " l");
		Log.log("Esem�ny: tankol�s elmentve."); 
		
		click(".event.timeline a[href*=\"tankolas\"]");
		onScreen(fuelType);
		onScreen(amount + " l");
		assertTrue("Gas station coordinates false", driver.getPageSource().contains("google.maps.LatLng(47.49087143, 19.03070831)"));
		driver.findElement(By.id("map0")).isDisplayed();
		Log.log("T�rk�p ok");
		
		
		clickLinkWithText("Szerkeszt�s");
		checkField("liter", amount);
		checkField("type", fuelType);
		submit();
		
		click("i.fa-trash");
		click("a[data-apply=\"confirmation\"]");
		
		sleep(8000);
		assertTrue("Event deleted", !driver.getPageSource().contains(amount + " l"));
		Log.log("Esem�ny: Tankol�s sikeresen t�r�lve."); 	
		
	}
	
	

	public static void addNewCarEventTechspec() throws IOException, InterruptedException {
		clickLinkWithText("esem�ny");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-mot")));
		click(".sprite-mot");
		
		click("input[name=\"test_date\"]");
		click("body");
		
		Random rand = new Random();
		int randomNum = 1000 + rand.nextInt((50000 - 1) + 1);
		String noteText = "Note " + String.valueOf(randomNum);
		fillName("note", noteText);
		fillName("car_company_id_ac", "Abc kft.");
		submit();
		
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Aut� vizsg�ztatva')]")));

		assertTrue("M�szaki vizsga elmentve", driver.getPageSource().contains("Aut� vizsg�ztatva"));
		Log.log("Esem�ny: m�szaki vizsga elmentve.");
		
		onScreen("Abc kft.");
		
		clickLinkWithText("Aut� vizsg�ztatva");
		onScreen("Abc kft.");
		String now = dateLocale(LocalDate.now());
		
		onScreen(now);
		onScreen(noteText);
		
		clickLinkWithText("Szerkeszt�s");
		
		now = dateDashes(LocalDate.now());
		checkField("test_date", now);
		checkField("car_company_id_ac", "Abc kft.");
		onScreen(noteText);
		submit();
		
		click("i.fa-trash");
		click("a[data-apply=\"confirmation\"]");
		
		sleep(10000);
		assertTrue("Event deleted", !driver.getPageSource().contains(noteText));
		Log.log("Esem�ny: m�szaki vizsga sikeresen t�r�lve.");
		
	}
	
	public static void setCarForSale() throws IOException, InterruptedException {
		//driver.findElement(By.xpath("//a[contains(text(), \"Elad�sra k�n�lom\")]")).click();
		clickLinkWithText("Elad�sra k�n�lom");
		//driver.findElement(By.className("switch")).click();
		click(".switch");
		
		//wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name=\"sell_price\"]")));
		//driver.findElement(By.cssSelector("input[name=\"sell_price\"]")).sendKeys("2194562");
		fillName("sell_price", "2194562");
		
		fillName("sell_description", getRandomText(40));
		
		//driver.findElement(By.cssSelector("input[name=\"loc_zip_id_ac\"]")).sendKeys("10");
		fillName("loc_zip_id_ac", "10");
		//wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ui-id-6")));
		//driver.findElement(By.id("ui-id-6")).click();
		click("#ui-id-6");

		
		TestBase.select("car_user[mobile_country]", "Magyarorsz�g");
		//driver.findElement(By.cssSelector("input[name=\"car_user[mobile]\"]")).sendKeys("1234567");
		fillName("car_user[mobile]", "1234567");
		
		driver.findElement(By.id("save-and-back")).click();
        //Thread.sleep(5000);
		//driver.findElement(By.id("save-and-back")).click();
		
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '2.194.562 Ft')]")));

		assertTrue("Aut� meghirdetve", driver.getPageSource().contains("2.194.562 Ft"));
		Log.log("Aut� sikeresen meghirdetve"); 

	}

	public static void addNewCarEventTires() throws IOException, InterruptedException {
		clickLinkWithText("esem�ny");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-mot")));
		click(".sprite-tire");
		
		click("input[name=\"service_date\"]");
		//click("body");
		
		clickLinkWithText("�j gumi felv�tele");
		
		randomSelect("width");
		randomSelect("height");
		randomSelect("diameter");
		randomSelect("type");
		randomSelect("mufacturer");
		fillName("item_description", "test model");
		select("number", "4");
		randomSelect("worn");
		randomSelect("dot_week");
		randomSelect("dot_year");
		randomSelect("thread_depth_1");
		randomSelect("thread_depth_2");
		randomSelect("thread_depth_3");
		randomSelect("thread_depth_4");
		
		Random rand = new Random();
		int randomNum = 1000 + rand.nextInt((50000 - 1) + 1);
		String noteText = "Note " + String.valueOf(randomNum);
		fillName("tire_storage", noteText);
		
		driver.findElement(By.cssSelector("#add-wheel .submitBtn")).click();
		
		sleep(10000);
		
		int price = 1000 + rand.nextInt((50000 - 1) + 1);
		String priceString = "" + price;
		fillName("car_mycar_service_log_items[0][price]", priceString);
		randomSelect("car_mycar_service_log_items[0][tire_position]");
		fillName("car_company_id_ac", "Abc Kft.");
		
	    price = 1000 + rand.nextInt((50000 - 1) + 1);
		priceString = "" + price;
		fillName("price_work", priceString);
		
		randomNum = 1000 + rand.nextInt((50000 - 1) + 1);
		noteText = "Note " + String.valueOf(randomNum);
		fillName("note", noteText);
		
		driver.findElement(By.cssSelector("#form .submitBtn")).click();
		
		Log.log("Esem�ny: gumicsere elmentve.");

		
	}

	public static void addNewCarEventCleaning() throws IOException, InterruptedException {
		//driver.findElement(By.xpath("//span[contains(text(), \"esem�ny\")]")).click();
		//wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-cleaning")));
		//driver.findElement(By.className("sprite-cleaning")).click();
		
		clickLinkWithText("esem�ny hozz�ad�sa");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-cleaning")));
		click(".sprite-cleaning");
		
		
		String cleaningType = randomSelect("cleaning_type");
		click("input[name=\"cleaning_date\"]");
		click("body");
		
		Random rand = new Random();
		Integer randomNum = 1000 + rand.nextInt((50000 - 1) + 1);
		int price = randomNum; 
		String priceString = String.valueOf(randomNum);
		fillName("price", priceString);
		
		rand = new Random();
		randomNum = 1000 + rand.nextInt((50000 - 1) + 1);
		String noteText = "Note " + String.valueOf(randomNum);
		fillName("note", noteText);
		
		submit();
		
		Log.log("Esem�ny: tiszt�t�s bek�ldve.");
		
		String now = dateLocale(LocalDate.now());
		System.out.println(now);
		onScreen(now);
		onScreen("Aut� tiszt�tva");
		onScreen(cleaningType);
		Log.log("Esem�ny: tiszt�t�s elmentve.");
		
		clickLinkWithText("Aut� tiszt�tva");
		onScreen(now);
		onScreen(cleaningType);
		checkPrice(price, " ");
		onScreen(noteText);
		
		clickLinkWithText("Szerkeszt�s");
		checkField("cleaning_type", cleaningType);
		checkField("price", priceString);
		checkField("note", noteText);
		submit();
		
		click("i.fa-trash");
		click("a[data-apply=\"confirmation\"]");
		
		sleep(8000);
		assertTrue("Event deleted", !driver.getPageSource().contains(noteText));
		Log.log("Esem�ny: tiszt�t�s sikeresen t�r�lve."); 	
	}

	public static void addNewCarEventAccident() throws IOException, InterruptedException {
	
			WebElement element = driver.findElement(By.className("event-types"));       
	    	((JavascriptExecutor)driver).executeScript("arguments[0].style.display='none'", element);
			driver.findElement(By.cssSelector(".events .add-link")).click();
			
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-accident")));
			click(".sprite-accident");
			
			
			click("input[name=\"accident_date\"]");
			//click(".blue-heading");
			driver.findElement(By.cssSelector("input[name=\"accident_date\"]")).sendKeys();
			
			int randNumber = new Random().nextInt(123456);
			String noteText = "Test note " + randNumber;
			//fillName("note", noteText);
			driver.findElement(By.cssSelector("textarea[name=\"note\"]")).sendKeys(noteText);
			
			submit();
			
			Log.log("Esem�ny: baleset bek�ldve.");
			
			String now = dateLocale(LocalDate.now());
			System.out.println(now);
			onScreen(now);
			onScreen("Az aut� megs�r�lt");
	
			Log.log("Esem�ny: baleset elmentve.");
			
			clickLinkWithText("Az aut� megs�r�lt");
			onScreen(noteText);
			
			clickLinkWithText("Szerkeszt�s");
			onScreen(noteText);
			submit();
			
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("event-types")));
			element = driver.findElement(By.className("event-types"));       
		    ((JavascriptExecutor)driver).executeScript("arguments[0].style.display='none'", element);
			

			click("i.fa-trash");
			click("a[data-apply=\"confirmation\"]");
			
			sleep(10000);
			assertTrue("Event deleted", !driver.getPageSource().contains(noteText));
			Log.log("Esem�ny: baleset sikeresen t�r�lve.");

	}

	public static void addNewCarEventOther() throws IOException {
		//driver.findElement(By.xpath("//span[contains(text(), \"esem�ny\")]")).click();
		clickLinkWithText("esem�ny");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-other")));
		driver.findElement(By.className("sprite-other")).click();
		
		Random rand = new Random();
		Integer randomNum = 1 + rand.nextInt((3000000 - 1) + 1);
		String randNum = String.valueOf(randomNum);
		String eventText = "Teszt esem�ny " + randNum;
		driver.findElement(By.cssSelector("input[name=\"title\"]")).sendKeys("Teszt esem�ny " + randNum);
		driver.findElement(By.cssSelector("input[name=\"event_date\"]")).sendKeys("2018-01-06");
		
		driver.findElement(By.className("submitBtn")).click();
		
		
		Log.log("Esem�ny: egy�b bek�ldve.");
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + eventText + "')]")));
		assertTrue("Aut� meghirdetve", driver.getPageSource().contains("Teszt esem�ny " + randNum));
		
		Log.log("Esem�ny: egy�b sikeresen elmentve.");
		
		clickLinkWithText(eventText);
		driver.findElement(By.cssSelector("a.red-link")).click();
		
		assertTrue("Event deleted", !driver.getPageSource().contains(eventText));
		Log.log("Esem�ny: egy�b sikeresen t�r�lve."); 	
	}
	
	public static void adminLogin() throws IOException, InterruptedException {
		goToPage("https://rc.ecdh.hu/hu/bejelentkezes");
		fillName("username", "info@ecdh.hu");
		fillName("password", "M1ntam�ku?");
		driver.findElement(By.className("btn-secondary")).click();
		Thread.sleep(5000);
	}

	public static void adminActivatecompany(String companyEmail) throws IOException {
		goToPage("https://rc.ecdh.hu/hu/admin/car/car-companies");
		driver.findElement(By.cssSelector("tr:nth-child(1) a:nth-child(2)")).click();
		goToPage("https://rc.ecdh.hu/hu/admin/car/car-users");
		driver.findElement(By.cssSelector("tr:nth-child(1) a:nth-child(2)")).click();
		goToPage("https://rc.ecdh.hu/hu/kijelentkezes");
	}

	public static void deleteCompany(String companyName) throws IOException {
		goToPage("https://rc.ecdh.hu/hu/admin/car/car-companies");
		driver.findElement(By.xpath("//*[contains(text(), '" + companyName + "')]/following::a[4]")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".confirmation .popover-content .bgm-lightblue")));
		driver.findElement(By.cssSelector(".confirmation .popover-content .bgm-lightblue")).click();
	}
	
	public static void deleteUser(String userName) throws IOException {
		goToPage("https://rc.ecdh.hu/hu/admin/car/car-users");
		driver.findElement(By.xpath("//*[contains(text(), '" + userName + "')]/following::a[4]")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".confirmation .popover-content .bgm-lightblue")));
		driver.findElement(By.cssSelector(".confirmation .popover-content .bgm-lightblue")).click();
	}

	public static void oneStepInner() throws IOException {
		List<WebElement> elements = driver.findElements(By.cssSelector(".card .profile-car-item"));
		for (WebElement element : elements) {
		  Log.log(element.findElement(By.className("numberplate")).getText());
		}
		
		Random rand = new Random();
		Log.log(elements.get(rand.nextInt(elements.size())).findElement(By.className("numberplate")).getText() + " selected.");
		elements.get(rand.nextInt(elements.size())).click();
		
	}

	public static String SendRequestTire() throws IOException, InterruptedException {
		
		driver.findElement(By.xpath("//a[contains(text(), \"Aj�nlatk�r�s\")]")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-tire")));
		driver.findElement(By.className("sprite-tire")).click();
		
		//TestBase.select("car_tire_request_items[0][width]", "165");
		Thread.sleep(1000);
		TestBase.randomSelect("car_tire_request_items[0][width]");
		//TestBase.select("car_tire_request_items[0][height]", "50");
		Thread.sleep(1000);
		TestBase.randomSelect("car_tire_request_items[0][height]");
		//TestBase.select("car_tire_request_items[0][diameter]", "r17");
		Thread.sleep(1000);
		TestBase.randomSelect("car_tire_request_items[0][diameter]");
		Thread.sleep(1000);
		driver.findElement(By.cssSelector("input[name=\"car_tire_request_items[0][qty]\"]")).sendKeys("2");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//label[@for=\"car-tire-request-items-0-summer\"]")).click();
		
		driver.findElement(By.cssSelector("input[name='loc_zip_id_ac']")).clear();
		driver.findElement(By.cssSelector("input[name='loc_zip_id_ac']")).sendKeys("1016");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(), \"1016\")]")));
		driver.findElement(By.xpath("//a[contains(text(), \"1016\")]")).click();
		driver.findElement(By.className("submitBtn")).click();
		
		//wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Sikeres')]")));
		//assertTrue("Tire request succeed", driver.getPageSource().contains("Sikeres"));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".order-1 a")));
		String requestId = driver.findElement(By.cssSelector(".order-1 a")).getText();
		System.out.println("ID" + requestId);
		Log.log("Gumi aj�nlatk�r�s elk�ldve.");
		
		return requestId;
		
	}

	public static String checkRequest(String requestId) throws IOException, InterruptedException {
		TestBase.goToPage("https://rc.ecdh.hu/hu/gumi-erdeklodesek");
		
		assertTrue("Tire request succeed", driver.getPageSource().contains(requestId));
		Log.log("Gumi aj�nlatk�r�s meg�rkezett.");
		
		click(".bell");
		clickLinkWithText("Gumi aj�nlatk�r�s");
		onScreen(requestId);
		Log.log("�rtes�t�s c�ges oldalon meg�rkezett.");
		
		
		//driver.findElement(By.cssSelector("a[data-original-title=\"Aj�nlat ad�sa\"]")).click();
		
		randomSelect("car_tire_company_offer_items[0][manufacturer]");
		fillName("car_tire_company_offer_items[0][item_description]", "test");
		
		Random rand = new Random();
		Integer randomNum = 2000 + rand.nextInt((30000 - 1) + 1);
		String randNum = String.valueOf(randomNum);
		fillName("car_tire_company_offer_items[0][price]", randNum);
		

		randomSelect("car_tire_company_offer_items[0][season]");
		driver.findElement(By.cssSelector("input[name=\"car_tire_company_offer_items[0][delivery_date]\"]")).click();		
		driver.findElement(By.cssSelector("textarea[name=\"note\"]")).sendKeys("test note");

		driver.findElement(By.className("submitBtn")).click();
        Thread.sleep(5000);
        
        return randNum;

	}
	
	public static String checkRequestPart(String requestId) throws IOException, InterruptedException {
		TestBase.goToPage("https://rc.ecdh.hu/hu/alkatresz-erdeklodesek");
		
		assertTrue("Tire request succeed", driver.getPageSource().contains(requestId));
		Log.log("Gumi aj�nlatk�r�s meg�rkezett.");
		
		click(".bell");
		clickLinkWithText("Gumi aj�nlatk�r�s");
		onScreen(requestId);
		Log.log("�rtes�t�s c�ges oldalon meg�rkezett.");
		
		
		//driver.findElement(By.cssSelector("a[data-original-title=\"Aj�nlat ad�sa\"]")).click();
		
		randomSelect("car_tire_company_offer_items[0][manufacturer]");
		fillName("car_tire_company_offer_items[0][item_description]", "test");
		
		Random rand = new Random();
		Integer randomNum = 2000 + rand.nextInt((30000 - 1) + 1);
		String randNum = String.valueOf(randomNum);
		fillName("car_tire_company_offer_items[0][price]", randNum);
		

		randomSelect("car_tire_company_offer_items[0][season]");
		driver.findElement(By.cssSelector("input[name=\"car_tire_company_offer_items[0][delivery_date]\"]")).click();		
		driver.findElement(By.cssSelector("textarea[name=\"note\"]")).sendKeys("test note");

		driver.findElement(By.className("submitBtn")).click();
        Thread.sleep(5000);
        
        return randNum;

	}
	
	public static void checkSelect(String name, String text) throws IOException {		
		String defaultItem = new Select(driver.findElement(By.cssSelector("select[name='" + name + "']"))).getFirstSelectedOption().getText();		
		
		if (defaultItem == text) {
		  Log.log(text + " selected - its ok");
		} else {
		  Log.log('|' + defaultItem + "| selected - but expected: |" + text + "|");
		}
		assertEquals(defaultItem, text);

	}

	public static String randomSelect(String name) throws IOException {
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("select[name='" + name + "']")));
		WebElement mySelectElement = driver.findElement(By.cssSelector("select[name='" + name + "']"));
		Select dropdown = new Select(mySelectElement);
		List<WebElement> options = dropdown.getOptions();
		int size = options.size();
		int randnMumber = new Random().nextInt(size - 1) + 1;
		options.get(randnMumber).click();
		Log.log(options.get(randnMumber).getText() + " selected from " + name);
		
		return options.get(randnMumber).getText();
	}

	public static void userLogout() throws IOException {
		Log.log("Kijelentkez�s a fi�kb�l.");
		goToPage("https://rc.ecdh.hu/hu/kijelentkezes");
	}

	public static void registerUserWrongEmail() throws IOException {
		
		driver.findElement(By.partialLinkText("Regisztr�ci�")).click();
		assertEquals("Go to URL", driver.getCurrentUrl(), url + "/hu/regisztracio");
		Log.log("Click Registraion");
		
		try 
		{
		  element = driver.findElement(By.className("ok"));
		  element.click();
		} catch (NoSuchElementException e) {
			
		}
		Log.log("Accept cookies");
			
		driver.findElement(By.id("user-username")).sendKeys("abcde12345");
		driver.findElement(By.id("user-password")).sendKeys("abcdeQWE123");
		driver.findElement(By.id("user-confirm-password")).sendKeys("eeeFDSFDS456");
		
		element = driver.findElement(By.className("register"));
		Actions actions = new Actions(driver);
		actions.moveToElement(element);
		actions.perform();
		Log.log("Szab�lytalan e-mail c�m.");
		Log.log("Jelszavak nem egyeznek.");
		
		driver.findElement(By.className("register")).click();
		Log.log("Regisztr�ci� gomb megnyom�sa.");	
	
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '�gy t�nik el�rta az e-mail c�m�t.')]")));
		assertTrue("Wrong email format", driver.getPageSource().contains("�gy t�nik el�rta az e-mail c�m�t."));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'A k�t jelsz� nem egyezik')]")));		
		assertTrue("Different passwords", driver.getPageSource().contains("A k�t jelsz� nem egyezik"));
		Log.log("Regisztr�l�s blokkolva"); 
		
	}

	public static void addNewCarNotes() throws IOException {
		driver.findElement(By.cssSelector(".car-mycar-notes a")).click();
		
		Random rand = new Random();
		Integer randomNum = 1 + rand.nextInt((3000000 - 1) + 1);
		String randNum = String.valueOf(randomNum);
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note")));
		fillName("note", "Note-" + randNum);
		click(".submitBtn");
		Log.log("Jegyzet \"Note-" + randNum +"\" bek�ldve.");
		
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + randNum + "')]")));
		assertTrue("Note appeared", driver.getPageSource().contains(randNum));
		Log.log("Jegyzet elmentve."); 	
		
	}

	public static void deleteCarNote() throws IOException, InterruptedException {
		String note = driver.findElement(By.cssSelector(".car-mycar-notes .note-item:nth-child(3)")).getText();
		Log.log("T�rlend� jegyzet: " + note);
		driver.findElement(By.cssSelector(".car-mycar-notes .note-item:nth-child(3) .note-delete")).click();
		sleep(10000);
		
		assertTrue("Note deleted", !driver.getPageSource().contains(note));
		Log.log("Jegyzet: " + note + " t�r�lve.");

		
	}

	public static void allCarNote() throws IOException, InterruptedException {
		driver.findElement(By.cssSelector(".car-mycar-notes .moreBtn")).click();
		driver.findElement(By.cssSelector(".car-mycar-notes .card-header a")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note")));
		
		Random rand = new Random();
		Integer randomNum = 1 + rand.nextInt((3000000 - 1) + 1);
		String randNum = String.valueOf(randomNum);
		fillName("note", "note-" + randNum);
		submit();
		Log.log("Jegyzet \"note-" + randNum + "\" bek�ldve.");
		
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + randNum + "')]")));
		assertTrue("Note appeared", driver.getPageSource().contains("note-" + randNum));
		Log.log("Jegyzet elmentve."); 	
		
		String note = driver.findElement(By.cssSelector(".car-mycar-notes .note-item:nth-child(2)")).getText();
		System.out.println(note);
		driver.findElement(By.cssSelector(".car-mycar-notes .note-item:nth-child(2) .note-delete")).click();
		Thread.sleep(10000);
		
		assertTrue("Note deleted", !driver.getPageSource().contains(note));
		Log.log("Jegyzet: " + note + " t�r�lve.");
		
	}

	public static void advancedSearch() throws IOException {
		click(".user-menu .nav-menu a");
		click(".sprite-used-cars");
		click(".detail-search-left a");
		fillName("pricefrom", "2194560");
		fillName("priceto", "2194564");
		submit();
		Log.log("Keres�s a meghirdetett aut�ra.");
		
        //wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '2 194 562 Ft')]")));
		assertTrue("Car found", driver.getPageSource().contains("BMW 116"));
		Log.log("Aut� szerepel a haszn�ltaut� keres�ben.");
	}

	private static void click(String css) {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(css)));
		driver.findElement(By.cssSelector(css)).click();
	}

	public static void checkRequestOffer(String companyName, String price) throws IOException {
		driver.findElement(By.cssSelector("#active-tire-requests .list-item:nth-child(1) a")).click();
		onScreen(companyName);
		Log.log("Aj�nlat meg�rkezett.");
		
		click(".bell");
		clickLinkWithText("Gumi aj�nlat");
		onScreen(companyName);
		Log.log("�rtes�t�s user oldalon meg�rkezett (" + companyName + ").");
		String savedPrice = driver.findElement(By.className("price")).getText();
		
		double amount = Double.parseDouble(price);
		DecimalFormat formatter = new DecimalFormat("###,###,###");
		
		String formattedPrice = formatter.format(amount).replaceAll(",", " ");
		
		assertEquals("�r stimmel", savedPrice, formattedPrice + " Ft");
		
		Log.log("�r stimmel");
	}
	
	public static void sendRequestFinalOrder() {
		click(".checkbox");
		click(".btn-lg");
		submit();
		
	}
	
	public static void checkRequestFinalOrder(String price) throws IOException {
		click(".bell");
		clickLinkWithText("Gumi rendel�s");
		onScreen(price);
		Log.log("�rtes�t�s c�ges oldalon meg�rkezett.");
	}

	public static String GetCompanyName() throws IOException {
		goToPage("https://rc.ecdh.hu/hu/car-companies/edit");
		return driver.findElement(By.id("name")).getAttribute("value");
	}
	
	public static boolean exists(String selector) throws IOException {
		if (driver.findElements(By.cssSelector(selector)).size() != 0) {
			return true;
		}
		
		return false;
	}
	
	public static String SendRequestPart() throws IOException {
		clickLinkWithText("Aj�nlatk�r�s");
		click(".sprite-technical");
		clickXpath("//span[contains(text(),'F�krendszer')]/following-sibling::i");
		clickXpath("//span[contains(text(),'F�kcs�')]/following-sibling::a");
		driver.findElement(By.xpath("//span[contains(text(),'F�kcs�')]/following-sibling::a")).click();
		clickLinkWithText("Aj�nlatk�r�s");
		
		fillName("loc_zip_id_ac", "10");
		clickLinkWithText("1014");
		if (driver.findElement(By.cssSelector("input[name=\"vin\"]")).isDisplayed()) {
		  fillName("vin", "12345678901234567");
		} else {
			Log.log("Alv�zsz�m kor�bbr�l elmentve.");
		}
		if (driver.findElement(By.cssSelector("input[name=\"motor_number\"]")).isDisplayed()) {
		  fillName("motor_number", "12345678901234567");
		} else {
		  Log.log("Motorsz�m kor�bbr�l elmentve.");
		}
		
        LocalDate dueDate = LocalDate.now().plusDays(3);
      
	    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-LL-dd");
	    String strDueDate = dueDate.format(dateFormat);
        fillName("end_date", strDueDate);
		
		
		clickXpath("//td[contains(text(),'18')]");
		fillName("note", "hey!");
		submit();
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".order-1")));
		return driver.findElement(By.cssSelector(".order-1 a")).getText();
	}

	private static void clickXpath(String string) {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(string)));
		driver.findElement(By.xpath(string)).click();		
	}

	private static void clickLinkWithText(String string) {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[not(contains(@class,'d-sm-none'))]/descendant-or-self::*[contains(text(),\"" + string + "\")]")));
		driver.findElement(By.xpath("//a[not(contains(@class,'d-sm-none'))]/descendant-or-self::*[contains(text(),\"" + string + "\")]")).click();
	}

	public static void FillUserPersonalData() throws IOException, InterruptedException {
		click(".user-img");
		clickLinkWithText("Adatm�dos�t�s");
		fillName("mobile", "12345678");
		fillName("user[last_name]", "Teszt");
		fillName("user[first_name]", "Eszter");
		fillName("mothers_name", "Kov�cs Hilda G�za");
		fillName("birthdate", "1970-12-12");
		fillName("personal_ident", "AE12345678");
		fillName("driving_licence_number", "fdsfdsAE12345678");
		clickLinkWithText("M�dos�t�sok ment�se");
		Thread.sleep(5000);
		click(".logo");
		Log.log("Vissza a f�oldalra.");
		clickLinkWithText("profil szerkeszt�se");
		checkField("mobile", "3636123456");
		checkField("user[last_name]", "Teszt");
		checkField("user[first_name]", "Eszter");
		checkField("mothers_name", "Kov�cs Hilda G�za");
		checkField("birthdate", "1970-12-12");
		checkField("personal_ident", "AE12345678");
		checkField("driving_licence_number", "fdsfdsAE12345678");

	}

	private static void checkField(String name, String expectedValue) throws IOException {
		String data = "";
		if (driver.findElements(By.cssSelector("input[name=\""+ name +"\"]")).size() != 0) {
		  data = driver.findElement(By.cssSelector("input[name=\""+ name +"\"]")).getAttribute("value");
		} 
		if (driver.findElements(By.cssSelector("select[name=\""+ name +"\"]")).size() != 0) {
			Select select = new Select(driver.findElement(By.cssSelector("select[name=\""+ name +"\"]")));
			data = select.getFirstSelectedOption().getText();
		}
		if (driver.findElements(By.cssSelector("textarea[name=\""+ name +"\"]")).size() != 0) {
			WebElement textarea = driver.findElement(By.cssSelector("textarea[name=\""+ name +"\"]"));
			data = textarea.getText();
		}
		try {
		  assertEquals(data, expectedValue);
		} catch (Exception e) {
		    System.out.println("Mez�: " + name + " - nem az elv�rt �rt�k");
		    Log.log("Mez�: " + name + " - nem az elv�rt �rt�k");
		    throw e;
		}
		System.out.println("Mez�: " + name + " - OK " + expectedValue);
		Log.log("Mez�: " + name + " - OK " + expectedValue);
		
	}

	private static void onScreen(String string) throws IOException {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), \"" + string + "\")]")));
        
		assertTrue("Szerepel a forr�sban", driver.getPageSource().contains(string));
		Log.log("K�perny�n: " + string);
	}

	public static void test() {
		driver.get("https://testcenter.vr/selenium-test-surface.php"); 
		
	}

	public static void checkCarProperties() throws IOException {
		clickLinkWithText("Adatok szerkeszt�se");
		checkField("car_manufacturer_id", "BMW");
		checkField("car_model_id", "116");
		checkField("car_year", "2012");
		checkField("car_month", "m�rcius");
		checkField("numberplate", "ABC-123");
		checkField("petrol", "D�zel");
		checkField("km", "120000");
	}

	public static void selectCar(String string) throws IOException {
		
		String pattern = "//div[@class=\"numberplate\" and contains(translate(.,'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), \"" + string + "\")]";
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pattern)));
		WebElement myElement = driver.findElement(By.xpath(pattern));
		WebElement parent = myElement.findElement(By.xpath("../.."));
		parent.findElement(By.className("item-image")).click();
		Log.log("Aut� kiv�lasztva: " + string);
		
	}

	public static void deleteCar(String numberPlate) throws IOException {
		selectCar(numberPlate);
		clickLinkWithText("Aut� t�rl�se");
		click(".deleteAttachedItem");
	}
	
	public static void deleteUserCars() throws IOException {
		List<WebElement> elements = driver.findElements(By.cssSelector(".numberplate"));
		List<String> list = new ArrayList<String>();
		for (WebElement element : elements) {
		  String numberplate = element.getText();
		  list.add(numberplate);
		} 
		for( String oneItem : list ) {
			Log.log(oneItem + " rendsz�m� aut� t�r�lve.");
	        deleteCar(oneItem);
		}
		 
		
	}
	
	public static void selectCarPartItem(String part) throws IOException {
		Log.log("Try to select:" + part);
		
		String pattern = "//span[contains(text(), \"" + part + "\")]";
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pattern)));
		WebElement myElement = driver.findElement(By.xpath(pattern));
		WebElement parent = myElement.findElement(By.xpath(".."));
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("ul li i")));
		parent.findElement(By.tagName("i")).click();
		
		Log.log(part + " kiv�lasztva.");
	}

	public static void addNewCarEventBodyRepair() throws IOException, InterruptedException {
		clickLinkWithText("esem�ny hozz�ad�sa");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-cleaning")));
		click(".sprite-mycar_service_log-body");
		
		//goToPage(TestBase.url + "/hu/szerviz-esemeny-letrehozasa/4/" + getCarId());
		click(".ts-date-picker");
		click("h2");
		clickXpath("//div[contains(text(), \"Kiv�laszt�s\")]");
		Log.log("Kiv�laszt�s clicked");
		
		List<WebElement> items = driver.findElements(By.cssSelector("ul.tree-browser > li"));
		List<String> list = new ArrayList<String>();
		String oneItem;
		
		for (WebElement item : items) {
		  oneItem = item.findElement(By.tagName("span")).getText();
		  if (!oneItem.isEmpty()) {
		    list.add(oneItem);
		  }
		}
		
		int size = list.size();
		int randomNumber = new Random().nextInt(size - 1) + 1;
		Log.log("Elem kiv�laszt�sa: " + list.get(randomNumber));
		selectCarPartItem(list.get(randomNumber));
	
		if (driver.findElements(By.cssSelector("ul.tree-browser li.active ul > li")).size() != 0) {

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("ul.tree-browser li.active ul > li")));
			items = driver.findElements(By.cssSelector("ul.tree-browser li.active ul > li"));
			list = new ArrayList<String>();
			String itemName;
			for (WebElement item : items) {
			  itemName = item.findElement(By.tagName("span")).getText();
			  if (!itemName.isEmpty()) {
			    list.add(itemName);
			  }
			} 
			size = list.size();
			System.out.println("list size" + size);
			System.out.println("list" + list);
			if (size == 1) {
				selectCarPartItem(list.get(0));
				Log.log("Try to select:" + list.get(0));
			} else {
		      randomNumber = new Random().nextInt(size - 1) + 1;
			  selectCarPartItem(list.get(randomNumber));
			}
		}
		
		if (driver.findElements(By.cssSelector("ul.tree-browser li.active ul li.active ul > li")).size() != 0) {

			items = driver.findElements(By.cssSelector("ul.tree-browser li.active ul li.active ul > li"));
			list = new ArrayList<String>();
			for (WebElement item : items) {
			  list.add(item.findElement(By.tagName("span")).getText());
			} 
			size = list.size();
		    randomNumber = new Random().nextInt(size - 1) + 1;
			selectCarPartItem(list.get(randomNumber));
			
			
		}
		sleep(1000);
		fillName("car_mycar_service_log_items[0][price]", "20000");
		submit();
		
	}

	public static void addNewCarEventRecurringService() throws IOException, InterruptedException {
		goToPage("https://rc.ecdh.hu/hu/szerviz-esemeny-letrehozasa/2/" + getCarId());
		fillName("service_interval_month", "48");
		fillName("service_interval_km", "20000");
		click(".ts-date-picker");
		
		List<WebElement> list = driver.findElements(By.className("changeMainPart"));
		int size = list.size();
		int randNumber = new Random().nextInt(size - 1) + 1;
		String partName = list.get(randNumber).getText();
		list.get(randNumber).click();
		
		Log.log(partName + " alkatr�sz kiv�lasztva.");
		int randPrice = new Random().nextInt(123456);
		fillName("car_mycar_service_log_items[0][price]", "" + randPrice);
		fillName("car_mycar_service_log_items[0][item_description]", "part " + randNumber);
		String noteText = "Test note " + randNumber;
		fillName("note", noteText);
		submit();
		clickLinkWithText("Id�szakos szerviz");
		onScreen(partName);
		onScreen(noteText);
		String pattern = "###,###";
		DecimalFormat format = new DecimalFormat(pattern); 
		String stringPrice = format.format(randPrice);
		//String stringPriceSpace = stringPrice.replaceAll(",", "\u00a0");
		String stringPriceSpace = stringPrice.replaceAll(",", " ");
		onScreen(stringPriceSpace);
		clickLinkWithText("Szerkeszt�s");
		onScreen(partName);
		onScreen(noteText);
		checkField("car_mycar_service_log_items[0][price]", randPrice + "");
		submit();
		
		click("i.fa-trash");
		click("a[data-apply=\"confirmation\"]");
		
		sleep(10000);
		assertTrue("Event deleted", !driver.getPageSource().contains(noteText));
		Log.log("Esem�ny: egy�b sikeresen t�r�lve."); 	
		
			
		
	}

	public static void addNewCarEventPenalty() throws IOException, InterruptedException {
      goToPage("https://rc.ecdh.hu/hu/birsag-esemeny-letrehozasa/" + getCarId());
      String penaltyType = randomSelect("penalty_type");
      driver.findElement(By.cssSelector("input[name=\"penalty_date\"]")).click();
      LocalDate dueDate = LocalDate.now().plusMonths(3);
      
	  DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-LL-dd");
	  String strDueDate = dueDate.format(dateFormat);
      fillName("pay_due_date", strDueDate);
      Random rand = new Random();
	  Integer price = 1000 + rand.nextInt((50000 - 1) + 1);
      fillName("price", "" + price);
      int note = 1000 + rand.nextInt((50000 - 1) + 1);
      String noteText = "Note " + note;
      fillName("note", noteText);
      
      submit();
      onScreen(penaltyType);
      checkPrice(price, "\u00a0");
      
      clickLinkWithText("B�ntet�s");
      onScreen(penaltyType);
      onScreen("Nem");
      
      clickLinkWithText("Szerkeszt�s");
      checkField("penalty_type", penaltyType);
      checkField("price", "" + price);
      onScreen(noteText);
      driver.findElement(By.xpath("//*[contains(text(),'Fizetve')]")).click();
      submit();
      clickLinkWithText(penaltyType);
      onScreen("Igen");
      
      click("i.fa-trash-alt");
	  //click("a[data-apply=\"confirmation\"]");
	
	  sleep(8000);
	  assertTrue("Event deleted", !driver.getPageSource().contains(penaltyType));
	  Log.log("Esem�ny: B�rs�g sikeresen t�r�lve."); 
      
      
	}
	
	

	public static void addNewCarEventHighwayFee() throws IOException, InterruptedException {
		//goToPage("https://rc.ecdh.hu/hu/autopalya-matrica-hozzadasa/" + getCarId());
		clickLinkWithText("esem�ny hozz�ad�sa");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-mycar_highway_ticket")));
		clickLinkWithText("P�lyamatrica");
		
		driver.findElement(By.cssSelector("input[name=\"start_date\"]")).click();
		List<WebElement> list = driver.findElements(By.cssSelector("input[type=\"radio\"]"));
		int size = list.size();
		int randNumber = new Random().nextInt(size - 1) + 1;
		String id = list.get(randNumber).getAttribute("id");
		//list.get(randNumber).click();
		driver.findElement(By.xpath("//label[@for='" + id + "']")).click();
		String name = driver.findElement(By.cssSelector("label[for=\"" + id + "\"] .ticket-name i")).getText();
		String expiration = driver.findElement(By.cssSelector("label[for=\"" + id + "\"] .ticket-expiration")).getText();
		String price = driver.findElement(By.cssSelector("label[for=\"" + id + "\"] .ticket-price")).getText();
		
		Log.log(name + " aut�p�lya matrica kiv�lasztva.");
		Log.log(expiration + " lej�rattal.");
		Log.log(price + " �ron.");
		submit();
		
		sleep(4000);
		
		String pattern = "//dt[contains(text(),' Aut�p�lya-matrica �rv�nyess�ge')]//following-sibling::dd[1]";
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pattern)));
		WebElement insuranceParent = driver.findElement(By.xpath(pattern));
		String insurance = insuranceParent.findElement(By.tagName("a")).getText();
		
		//assertTrue("Aut�p�lyamatrica lej�rat OK.", insurance.contains(expiration));
		//Log.log("Aut�p�lyamatrica lej�rat OK.");
		
		assertTrue("Aut�p�lyamatrica adatok OK.", insurance.contains(expiration));
		assertTrue("Aut�p�lyamatrica adatok OK.", insurance.contains(name));
		Log.log("Aut�p�lyamatrica adatok OK.");
		
		
		
		onScreen("�j aut�p�lya matrica");
		clickLinkWithText("�j aut�p�lya matrica");
		onScreen(expiration);
		onScreen(name);
		onScreen(price);
		
		clickLinkWithText("Szerkeszt�s");
		
		list = driver.findElements(By.cssSelector("input[type=\"radio\"]"));
		randNumber = new Random().nextInt(size - 1) + 1;
		id = list.get(randNumber).getAttribute("id");
		driver.findElement(By.xpath("//label[@for='" + id + "']")).click();
		name = driver.findElement(By.cssSelector("label[for=\"" + id + "\"] .ticket-name i")).getText();
		expiration = driver.findElement(By.cssSelector("label[for=\"" + id + "\"] .ticket-expiration")).getText();
		price = driver.findElement(By.cssSelector("label[for=\"" + id + "\"] .ticket-price")).getText();
		
		Log.log(name + " aut�p�lya matrica kiv�lasztva.");
		Log.log(expiration + " lej�rattal.");
		Log.log(price + " �ron.");
		submit();
		
		onScreen(expiration);
		onScreen(name);
		onScreen(price);	
		
	}

	private static void submit() {
		click(".submitBtn");
	}

	public static void addNewCarEventCompulsoryInsurance() throws IOException, InterruptedException {
		//goToPage("https://rc.ecdh.hu/hu/biztositas-hozzadasa/" + getCarId() + "/1");
		clickLinkWithText("esem�ny hozz�ad�sa");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-mycar_insurance")));
		clickLinkWithText("K�telez�");
		
		String company = randomSelect("company");
		driver.findElement(By.cssSelector("input[name=\"start_date\"]")).click();
		String period = randomSelect("period");
		
		int randNumber = new Random().nextInt(123456);
		String ident =  "" + randNumber;
		fillName("ident", ident);
		
		int price = new Random().nextInt(123456);
		String stringPrice = "" + price;
		fillName("price", stringPrice);
		submit();
		
		String pattern = "//dt[contains(text(),'K�telez� g�pj�rm� biztos�t�s')]//following-sibling::dd[1]";
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pattern)));
		WebElement insuranceParent = driver.findElement(By.xpath(pattern));
		String insurance = insuranceParent.findElement(By.tagName("a")).getText();
		LocalDate dueDate = LocalDate.now().plusYears(1);
		
		assertTrue("CASCO biztos�t�s list�zva.", insurance.contains(dateLocale(LocalDate.now())));
		if (insurance.contains(dateLocale(LocalDate.now()))) {
		  Log.log("CASCO biztos�t�s list�zva.");
		}
		
		assertTrue("CASCO biztos�t�s lej�rat OK.", insurance.contains("Lej�r: " + dateLocale(dueDate)));
		Log.log("CASCO biztos�t�s lej�rat OK.");
		
		clickLinkWithText("biztos�t�s");
		onScreen(company);
		onScreen(period);
		onScreen(ident);
		
		checkPrice(price, " ");

		clickLinkWithText("Szerkeszt�s");
		checkSelect("type", "K�telez� g�pj�rm� biztos�t�s");
		checkSelect("company", company);
		
		checkField("ident", ident);
		checkField("price", "" + price);
		checkField("period", period);
		
		submit();
		
		click("i.fa-trash");
		click("a[data-apply=\"confirmation\"]");
		
		sleep(8000);
		assertTrue("Event deleted", !driver.getPageSource().contains("CASCO biztos�t�s"));
		Log.log("Esem�ny: K�telez� g�pj�rm� biztos�t�s sikeresen t�r�lve."); 	
	}

	public static void addNewCarEventCascoInsurance() throws IOException, InterruptedException {
		//goToPage("https://rc.ecdh.hu/hu/biztositas-hozzadasa/" + getCarId() + "/2");
		clickLinkWithText("esem�ny hozz�ad�sa");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-mycar_insurance")));
		clickLinkWithText("Casco");
		
		String company = randomSelect("company");
		driver.findElement(By.cssSelector("input[name=\"start_date\"]")).click();
		String period = randomSelect("period");
		
		int randNumber = new Random().nextInt(123456);
		String ident =  "" + randNumber;
		fillName("ident", ident);
		
		int price = new Random().nextInt(123456);
		String stringPrice = "" + price;
		fillName("price", stringPrice);
		submit();
		
		String pattern = "//dt[contains(text(),'CASCO biztos�t�s')]//following-sibling::dd[1]";
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pattern)));
		WebElement insuranceParent = driver.findElement(By.xpath(pattern));
		String insurance = insuranceParent.findElement(By.tagName("a")).getText();
		LocalDate dueDate = LocalDate.now().plusYears(1);
		
		assertTrue("CASCO biztos�t�s list�zva.", insurance.contains(dateLocale(LocalDate.now())));
		if (insurance.contains(dateLocale(LocalDate.now()))) {
		  Log.log("CASCO biztos�t�s list�zva.");
		}
		
		assertTrue("CASCO biztos�t�s lej�rat OK.", insurance.contains("Lej�r: " + dateLocale(dueDate)));
		Log.log("CASCO biztos�t�s lej�rat OK.");
		
		clickLinkWithText("biztos�t�s");
		onScreen(company);
		onScreen(period);
		onScreen(ident);
		
		checkPrice(price, " ");

		clickLinkWithText("Szerkeszt�s");
		checkSelect("type", "CASCO biztos�t�s");
		checkSelect("company", company);
		
		checkField("ident", ident);
		checkField("price", "" + price);
		checkField("period", period);
		
		submit();
		
		click("i.fa-trash");
		click("a[data-apply=\"confirmation\"]");
		
		sleep(8000);
		assertTrue("Event deleted", !driver.getPageSource().contains("CASCO biztos�t�s"));
		Log.log("Esem�ny: CASCO sikeresen t�r�lve."); 	
		
	}
	
	public static void checkPrice(int num, String delimiter) throws IOException {
		String pattern = "###,###";
		DecimalFormat format = new DecimalFormat(pattern); 
		String stringPrice = format.format(num);
		String stringPriceSpace = stringPrice.replaceAll(",", delimiter);
		onScreen(stringPriceSpace);
	}

	public static void addNewCarEventGapInsurance() throws IOException, InterruptedException {

		clickLinkWithText("esem�ny hozz�ad�sa");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-mycar_insurance")));
		clickLinkWithText("GAP");
		
		String company = randomSelect("company");
		driver.findElement(By.cssSelector("input[name=\"start_date\"]")).click();
		//String period = randomSelect("period");
		
		int randNumber = new Random().nextInt(123456);
		String ident =  "" + randNumber;
		fillName("ident", ident);
		
		int price = new Random().nextInt(123456);
		String stringPrice = "" + price;
		fillName("price", stringPrice);
		submit();
		
		String pattern = "//dt[contains(text(),'GAP biztos�t�s')]//following-sibling::dd[1]";
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pattern)));
		WebElement insuranceParent = driver.findElement(By.xpath(pattern));
		String insurance = insuranceParent.findElement(By.tagName("a")).getText();
		LocalDate dueDate = LocalDate.now().plusYears(1);
		
		onScreen("�j GAP biztos�t�s");
		
		Log.log("Elv�rt lej�rati d�tum: " + dateLocale(dueDate));
		assertTrue("GAP biztos�t�s lej�rat OK.", insurance.contains("Lej�r: " + dateLocale(dueDate)));
		Log.log("GAP biztos�t�s lej�rat OK.");
		
		clickLinkWithText("biztos�t�s");
		onScreen(company);
		//onScreen(period);
		onScreen(ident);
		
		checkPrice(price, " ");

		clickLinkWithText("Szerkeszt�s");
		checkSelect("type", "GAP biztos�t�s");
		checkSelect("company", company);
		
		checkField("ident", ident);
		checkField("price", "" + price);
		//checkField("period", period);
		
		submit();
		
		click("i.fa-trash");
		click("a[data-apply=\"confirmation\"]");
		
		sleep(8000);
		assertTrue("Event deleted", !driver.getPageSource().contains("GAP biztos�t�s"));
		Log.log("Esem�ny: GAP biztos�t�s sikeresen t�r�lve."); 	
	}

	private static void sleep(int i) throws InterruptedException {
		System.out.println("wait " + i + " millisconds");
		Thread.sleep(i);
		
	}
	
	public static String getCarId(){
		String url = driver.getCurrentUrl();
	    return url.replaceFirst(".*/([^/?]+).*", "$1");
	}

	public static String dateLocale(LocalDate date) {
	    DateTimeFormatter yearF = DateTimeFormatter.ofPattern("yyyy.");
	    DateTimeFormatter monthF = DateTimeFormatter.ofPattern("MMMM");
	    DateTimeFormatter dayF = DateTimeFormatter.ofPattern("d.");
	    
	    String year = date.format(yearF);
	    String month = date.format(monthF);
	    String day = date.format(dayF);
	    
		month = month.replace("January", "janu�r");
		month = month.replace("February", "febru�r");
		month = month.replace("March", "m�rcius");
		month = month.replace("April", "�prilis");
		month = month.replace("May", "m�jus");
		month = month.replace("June", "j�nius");
		month = month.replace("July", "j�lius");
		month = month.replace("August", "augusztus");
		month = month.replace("September", "szeptember");
		month = month.replace("October", "okt�ber");
		month = month.replace("November", "november");
		month = month.replace("December", "december");
		
		return year + " " + month + " " + day;
	}
	
	public static String dateDots(LocalDate date) {
	    DateTimeFormatter yearF = DateTimeFormatter.ofPattern("yyyy.");
	    DateTimeFormatter monthF = DateTimeFormatter.ofPattern("MM.");
	    DateTimeFormatter dayF = DateTimeFormatter.ofPattern("dd.");
	    
	    String year = date.format(yearF);
	    String month = date.format(monthF);
	    String day = date.format(dayF);

		return year + " " + month + " " + day;
	}
	
	public static String dateDashes(LocalDate date) {
	    DateTimeFormatter yearF = DateTimeFormatter.ofPattern("yyyy-");
	    DateTimeFormatter monthF = DateTimeFormatter.ofPattern("MM-");
	    DateTimeFormatter dayF = DateTimeFormatter.ofPattern("dd");
	    
	    String year = date.format(yearF);
	    String month = date.format(monthF);
	    String day = date.format(dayF);

		return year + month + day;
	}

	public static void setCarForRent() throws IOException, InterruptedException {
		clickLinkWithText("B�rl�sre k�n�lom");
		click(".switch");
		
		
		fillName("rent_price_1", "7432");
		
		Random rand = new Random();
		Integer randomNum = 2000 + rand.nextInt((10000 - 1) + 1);
		String amount = String.valueOf(randomNum);
		fillName("rent_price_2", amount);
		
		randomNum = 20000 + rand.nextInt((100000 - 1) + 1);
		amount = String.valueOf(randomNum);
		fillName("rent_price_3", amount);
		
		randomNum = 30000 + rand.nextInt((100000 - 1) + 1);
		amount = String.valueOf(randomNum);
		fillName("rent_price_4", amount);	
		
		randomNum = 30000 + rand.nextInt((100000 - 1) + 1);
		amount = String.valueOf(randomNum);
		fillName("rent_bail", amount);	

		fillName("loc_zip_id_ac", "10");
		click("#ui-id-6");

		fillName("rent_description", "Lorem ipsum dolor sit amet!");
		
		submit();
		sleep(5000);
		
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '7.432 Ft')]")));

		assertTrue("Aut� meghirdetve", driver.getPageSource().contains("7.432 Ft"));
		Log.log("Aut� b�rl�sre sikeresen meghirdetve"); 
		
	}

	public static void advancedSearchRent() throws IOException, InterruptedException {
		click(".user-menu .nav-menu a");
		click(".sprite-rent-cars");
		clickXpath("//label[contains(text(),'Gy�rt�')]/following-sibling::a");
		// TODO
		//String manufacturer = fillCarField(GYARTO);
		sleep(5000);
		submit();

		//Log.log("Keres�s a meghirdetett " + manufacturer + " aut�ra.");
		
        //wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '2 194 562 Ft')]")));
		assertTrue("Car found", driver.getPageSource().contains("BMW 116"));
		Log.log("Aut� szerepel a haszn�ltaut� keres�ben.");
		
	}
	
	public static String getRandomText(int i) {
		Random rand = new Random();
		Integer randomNum = rand.nextInt(10);
		
		LoremIpsum ipsum = new LoremIpsum();
		return ipsum.getWords(i, randomNum);
	}

	public static void buildCompanyPage() throws IOException, InterruptedException {
		goToPage("https://rc.ecdh.hu/hu/ceg-oldal-szerkesztes");
		
		
		if (driver.findElements(By.xpath("//a/descendant-or-self::*[contains(text(),\"kattintson ide c�goldala l�trehoz�s�hoz\")]")).size() != 0) {
		  clickLinkWithText("kattintson ide c�goldala l�trehoz�s�hoz");
		}
		
		
		clickLinkWithText("Fejl�c szerkeszt�se");

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name=\"logo_text\"]")));
		fillName("logo_text", getRandomText(5));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name=\"logo_slogan\"]")));
		fillName("logo_slogan", getRandomText(5));
		
		submit();
		sleep(2000);
		clickLinkWithText("Men� szerkeszt�se");
		
		click("#car-company-page-menus-add");
		sleep(1000);
		click("#car-company-page-menus-add");
		sleep(1000);
		click("#car-company-page-menus-add");
		sleep(1000);
		
		Random rand = new Random();
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name=\"car_company_page_menus[2][title]\"]")));
		fillName("car_company_page_menus[0][title]", getRandomText(4).substring(0, 1 + rand.nextInt(10)));
		randomSelect("car_company_page_menus[0][menu_modul]");
		
		fillName("car_company_page_menus[1][title]", getRandomText(4).substring(0, 1 + rand.nextInt(10)));
		randomSelect("car_company_page_menus[1][menu_modul]");
		
		fillName("car_company_page_menus[2][title]", getRandomText(4).substring(0, 1 + rand.nextInt(10)));
		randomSelect("car_company_page_menus[2][menu_modul]");
		
		submit();
		

		companyPageNewArticle();
		companyPageNewArticle();
		companyPageNewArticle();
		companyPageNewArticle();

		CompanyWebpage();
		
	}
	
	public static String companyPageNewArticle() throws IOException, InterruptedException {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a/descendant-or-self::*[contains(text(),\"�j h�r hozz�ad�sa\")]")));
		clickLinkWithText("�j h�r hozz�ad�sa");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("textarea[name=\"lead\"]")));
		String title = getRandomText(5);
		fillName("title", title);
		fillName("lead", getRandomText(10));
		fillName("content", getRandomText(50));
		submit();
		sleep(4000);
		Log.log("C�ges oldal h�r bek�ldve: " + title);
		return title;
	
	}

	public static void CompanyWebpage() throws IOException, InterruptedException {
		goToPage("https://rc.ecdh.hu/hu/ceg-oldal-szerkesztes");
		clickLinkWithText("Men� szerkeszt�se");
		sleep(6000);
		driver.findElement(By.id("car-company-page-menus-add")).click();
		fillName("car_company_page_menus[0][title]", "R�lunk");
		select("car_company_page_menus[0][menu_modul]", "Nyit� oldal");
		driver.findElement(By.id("car-company-page-menus-add")).click();
		fillName("car_company_page_menus[1][title]", "H�reink");
		select("car_company_page_menus[1][menu_modul]", "H�rek");
		submit();
	}

	public static void addGPS() throws IOException, InterruptedException {
		TestBase.login("vorosborisz@gmail.com", "letstest");
		List<WebElement> elements = driver.findElements(By.cssSelector(".card .profile-car-item"));
		List<String> numberPlates = new ArrayList<String>();
		List<String> gpsCodes = new ArrayList<String>();
		
		gpsCodes.add("864893031557658");
		gpsCodes.add("864893031571469");
		gpsCodes.add("864895030889255");

		for (WebElement element : elements) {
		  numberPlates.add(element.findElement(By.className("numberplate")).getText());
		}
		
		goToPage("https://rc.ecdh.hu/hu/kijelentkezes");
		TestBase.adminLogin();
		
		int c = 0;
		for (String numberPlate : numberPlates) {
		  goToPage("https://rc.ecdh.hu/hu/admin/car/car-mycars");
		  fillName("quick_search", numberPlate);
		  driver.findElement(By.className("btn-primary")).click();
		  sleep(1000);
		  driver.findElement(By.className("command-edit")).click();
		  fillName("gps_ident", gpsCodes.get(c));
		  submit();
		  c++;
		}
		
		goToPage("https://rc.ecdh.hu/hu/kijelentkezes");
		TestBase.login("vorosborisz@gmail.com", "letstest");
		clickLinkWithText(numberPlates.get(0));
		
		
	}

	public static void passShepherd() {
		try {
		  driver.findElement(By.className("shepherd-button")).click();
		} catch (NoSuchElementException e){
		  
		}
	}
	
}
