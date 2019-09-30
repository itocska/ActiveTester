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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import de.svenjacobs.loremipsum.LoremIpsum;

//byITO
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
//end

public class TestBase {

	public static WebDriver driver;
	public static WebElement element;
	public static WebDriverWait wait;
	private static int close;
	public static String url;
	public static String personalUser;
	public static String personalPassword;
	public static String companyUser;
	public static String companyPassword;
	public static String testerMail;
	public static String testerPassword;
	public static String adminUser;
	public static String adminPassword;
	public static String dbUser;
	public static String dbPass;
	public static String myUrl;
	public static String manufacturer;
	public static String model;
	public static String yearfrom;

	// byITOtest
	public static Properties prop = new Properties();

	// end

	public static void main(String arg, int close) throws Throwable {

		// byITOtest
		String path = System.getProperty("user.dir");
		InputStream input = new FileInputStream(path + "/src/config/config.properties");
		prop.load(input);

		if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0) {
			System.setProperty("webdriver.chrome.driver", "/www/webdrivers/chromedriver");
		} else {
			String pathcr = (new File("")).getAbsolutePath();
			System.setProperty("webdriver.chrome.driver", pathcr + "/webdriver/chromedriver.exe");
		}
		String activePUser = prop.getProperty("pUser");
		String activeCUser = prop.getProperty("cUser");
		String activeAUser = prop.getProperty("aUser");
		String activeTMail = prop.getProperty("tMail");

		personalUser = prop.getProperty(activePUser);
		personalPassword = prop.getProperty(activePUser + "Pass");
		companyUser = prop.getProperty(activeCUser);
		companyPassword = prop.getProperty(activeCUser + "Pass");
		adminUser = prop.getProperty(activeAUser);
		adminPassword = prop.getProperty(activeAUser + "Pass");
		// csak a mailer privát adatai
		testerMail = prop.getProperty(activeTMail);
		testerPassword = prop.getProperty(activeTMail + "Pass");
		dbUser = prop.getProperty("dbUser");
		dbPass = prop.getProperty("dbPass");
		myUrl = prop.getProperty("dbURL");

		url = prop.getProperty("url");
		// end

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

		try {
			goToPage(url);
			unlockPage();
			acceptCookies();

		} catch (AssertionError | WebDriverException e) {
			Log.error = true;
			Log.log(e.getMessage().toString());
			System.out.println("Exception occurred");

			throw e;
		}

	}

	private static void print(String string) {
		System.out.println(string);
	}

	protected static void deleteUser() throws IOException, InterruptedException {
		click(".user-img");
		clickLinkWithText("Adatmódosítás");
		clickLinkWithText("Fiók törlése");
		click(".btn-red");
		Log.log("Felhasználó törlése.");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-success")));
		Log.log("Felhasználó törölve.");
	}

	protected static void activateUser() throws Exception {
		driver.get("https://gmail.com");

		driver.findElement(By.cssSelector("input[type=\"email\"]")).sendKeys(testerMail);
		driver.findElement(By.xpath("//*[text()='Következő']")).click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type=password]")));

		driver.findElement(By.cssSelector("input[type=password]")).sendKeys(testerPassword);
		driver.findElement(By.xpath("//*[text()='Következő']")).click();
		Log.log("Login Gmail");

		sleep(6000);
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("(//*[text()='Regisztráció megerősítése (ECDH)'])[2]")));
		driver.findElement(By.xpath("(//*[text()='Regisztráció megerősítése (ECDH)'])[2]")).click();

		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//a[contains(text(), 'Személyes fiók aktiválása')]")));
		driver.findElement(By.xpath("//a[contains(text(), 'Személyes fiók aktiválása')]")).click();
		Log.log("New user account activation");

		System.out.println(driver.getTitle());

		for (String winHandle : driver.getWindowHandles()) {
			System.out.println(winHandle);
			driver.switchTo().window(winHandle);
		}

		System.out.println(driver.getTitle());

		passShepherd();
		Log.log("Activation succeed");

		// driver.get(Gmail.getMails("{email}", "{password}", "ECDH",
		// "href=\"(.*?)\">Személyes fiók aktiválása"));

	}

	private static void acceptCookies() throws IOException, InterruptedException {
		Log.log("Accept cookies");
		try {
			driver.findElement(By.className("cc-btn")).click();
			sleep(2000);
		} catch (NoSuchElementException e) {
			Log.log("ERROR - No cookie acception message.");
		}
	}

	private static void unlockPage() throws IOException {
		// driver.findElement(By.name("pass")).sendKeys("kecskesajt");
		// driver.findElement(By.className("btn-success")).click();
		// Log.log("Password protection unlocked.");
	}

	public static void goToPage(String url) throws IOException {
		driver.get(url);
	}

	public static void fillName(String name, String text) throws IOException {
		print("FOUND: " + driver.findElements(By.cssSelector("input[name='" + name + "']")).size());
		if (driver.findElements(By.cssSelector("input[name='" + name + "']")).size() != 0) {
			driver.findElement(By.cssSelector("input[name='" + name + "']")).clear();
			driver.findElement(By.cssSelector("input[name='" + name + "']")).sendKeys(Keys.BACK_SPACE);
			if (name == "doors") {
				driver.findElement(By.cssSelector("input[name='" + name + "']")).click();

				driver.findElement(By.cssSelector("input[name='" + name + "']")).clear();
				driver.findElement(By.cssSelector("input[name='" + name + "']")).sendKeys(Keys.BACK_SPACE);

				driver.findElement(By.cssSelector("input[name='" + name + "']")).sendKeys("3");
			} else {
				driver.findElement(By.cssSelector("input[name='" + name + "']")).sendKeys(text);
			}
		} else {
			driver.findElement(By.cssSelector("textarea[name='" + name + "']")).clear();
			driver.findElement(By.cssSelector("textarea[name='" + name + "']")).sendKeys(Keys.BACK_SPACE);
			driver.findElement(By.cssSelector("textarea[name='" + name + "']")).sendKeys(text);
		}

		Log.log(name + " field filled with: " + text);
	}

	protected static void registerUser(String user, String password, Boolean obligatory) throws IOException {
		// obligatory checkboxes not checked test
		clickLinkWithText("Regisztráció");
		Log.log("Click Registraion");

		try {
			element = driver.findElement(By.className("ok"));
			element.click();
		} catch (NoSuchElementException e) {

		}
		Log.log("Accept cookies");

		fillName("user[username]", user);
		fillName("user[password]", password);
		fillName("user[confirm_password]", password);

		Log.log("Kötelező mezők mellőzése (felhasználási, adatvédelmi feltételek).");

		click(".register");
		Log.log("Regisztráció gomb megnyomása.");

		assertTrue("Regisztrálás a kötelezők nélkül blokkolva",
				!driver.getPageSource().contains("A regisztrációd sikeres"));
		Log.log("Regisztrálás blokkolva");
	}

	protected static void registerUser(String username, String password) throws IOException {
		clickLinkWithText("Regisztráció");
		Log.log("Click Registraion");

		try {
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
		Log.log("Click on Regisztráció");

		// wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='A
		// regisztrációd sikeres']")));
		wait.until(ExpectedConditions.textToBePresentInElementLocated(By.className("feedback-page"),
				"regisztrációd sikeres"));

		assertTrue("Registration succeed", driver.getPageSource().contains("A regisztrációd sikeres"));
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
		clickLinkWithText("Belépés");
		Log.log("Click login");

		try {
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
		// assertTrue("Login succeed",
		// driver.getPageSource().contains("Bejelentkezve"));
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
		Select dropdown = new Select(mySelectElement);
		dropdown.selectByIndex(i);
		Log.log("Index " + i + " selected from " + string);

	}

	public static void CarLimit() throws IOException, InterruptedException, AWTException {

		clickLinkWithText("Előfizetek");
		fillName("name", "Teszt Ember");
		fillName("email", personalUser);
		Select orszag = new Select(driver.findElement(By.name("phone_country")));
		orszag.selectByVisibleText("Magyarország");
		fillName("phone", "709874512");
		String randNumTax = "";
		String randNumReg = "";
		Select cusType = new Select(driver.findElement(By.name("customer_type")));
		Random rand = new Random();
		Integer randomNumif = rand.nextInt(2);

		if (randomNumif == 1) {
			cusType.selectByVisibleText("magánszemély");
			driver.findElement(By.cssSelector("input[name='invoice[loc_zip_id_ac]']")).sendKeys("1112");
			sleep(2000);
			driver.findElement(By.cssSelector(".ui-menu-item:first-child")).click();
			fillName("invoice[street]", "Repülőtéri");
			Select streetType = new Select(driver.findElement(By.name("invoice[street_type]")));
			streetType.selectByVisibleText("út");
			fillName("invoice[street_num]", "6");
			fillName("invoice[building]", "A");
			fillName("invoice[floor]", "1");
			fillName("invoice[door]", "1");

		} else

		{

			 cusType.selectByVisibleText("céges");
			 
			driver.findElement(By.cssSelector("input[name='invoice[loc_zip_id_ac]']")).sendKeys("1112");
			sleep(2000);
			driver.findElement(By.cssSelector(".ui-menu-item:first-child")).click();
			Integer cegRnd = rand.nextInt(500) + 1;
			fillName("company_name", "TesztCég" + cegRnd);

			Random rand2 = new Random();
			for (int i = 0; i < 11; i++) {
				Integer randomNumTax = rand2.nextInt((9) + 1);
				randNumTax = randNumTax + String.valueOf(randomNumTax);
			}
			fillName("tax_no", randNumTax);
			Log.log("Adószám kitöltés");

			for (int i = 0; i < 10; i++) {
				Integer randomNumReg = rand2.nextInt((9) + 1);
				randNumReg = randNumReg + String.valueOf(randomNumReg);
			}
			fillName("reg_no", randNumReg);
			Log.log("Cégjegyzékszám kitöltés");
			fillName("invoice[street]", "Repülőtéri");
			Select streetType = new Select(driver.findElement(By.name("invoice[street_type]")));
			streetType.selectByVisibleText("út");
			fillName("invoice[street_num]", "6");
			fillName("invoice[building]", "A");
			fillName("invoice[floor]", "1");
			fillName("invoice[door]", "1");

		}

		Actions actions = new Actions(driver);

		WebElement myElement = driver.findElement(By.xpath("//label[@for=\"accept-rules2\"]"));
		WebElement parent = myElement.findElement(By.xpath(".."));
		actions.moveToElement(parent, 5, 5).click().build().perform();
		Log.log("Accept privacy terms");

		myElement = driver.findElement(By.xpath("//label[@for=\"accept-rules\"]"));
		parent = myElement.findElement(By.xpath(".."));
		actions.moveToElement(parent, 5, 5).click().build().perform();
		Log.log("Accept rules");
		
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + personalUser + "']")));
		System.out.println(personalUser);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(personalUser));
		Log.log("Képernyőn: " + personalUser);
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + "HU" + "']")));
		System.out.println("Magyarország");
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("HU"));
		Log.log("Képernyőn: " + "Magyarország");

		
	
		
		try{
			
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + "1" + "']")));
			System.out.println("magánszemély");
			assertTrue("Szerepel a forrásban", driver.getPageSource().contains("magánszemély"));
			Log.log("Képernyőn: " + "magánszemély");
		 
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='invoice-street']")));
			System.out.println("Repülőtéri");
			Log.log("Képernyőn: " + "Repülőtéri");
			
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@id='invoice-street-type']")));
			System.out.println("út");
			assertTrue("Szerepel a forrásban", driver.getPageSource().contains("út"));
			Log.log("Képernyőn: " + "út");
			
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='invoice-loc-zip-id']")));
			System.out.println("1112");
			assertTrue("Szerepel a forrásban", driver.getPageSource().contains("1112"));
			Log.log("Képernyőn: " + "1112");
	
			onScreenValue("6");
			
			
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='invoice-building']")));
			System.out.println("A");
			assertTrue("Szerepel a forrásban", driver.getPageSource().contains("A"));
			Log.log("Képernyőn: " + "A");
			
			String mail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='email']"))).getText();
			
			System.out.println(mail);
			assertTrue("Szerepel a forrásban", driver.getPageSource().contains(mail));
			Log.log("Képernyőn: " + mail);
		}
		
			
		catch(NoSuchElementException e)	{
		
			
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//section//input[@value='" + "2" + "']")));
			assertTrue("Szerepel a forrásban", driver.getPageSource().contains("céges"));
			Log.log("Képernyőn: " + "céges");
			
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='invoice-street']")));
			System.out.println("Repülőtéri");
			Log.log("Képernyőn: " + "Repülőtéri");
			
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@id='invoice-street-type']")));
			System.out.println("út");
			assertTrue("Szerepel a forrásban", driver.getPageSource().contains("út"));
			Log.log("Képernyőn: " + "út");
			
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='invoice-loc-zip-id']")));
			System.out.println("1112");
			assertTrue("Szerepel a forrásban", driver.getPageSource().contains("1112"));
			Log.log("Képernyőn: " + "1112");
			
			onScreenValue("TesztCég");
			onScreenValue(randNumReg);
			onScreenValue(randNumTax);
			onScreenValue("6");
			
			String mail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='email']"))).getText();
			
			System.out.println(mail);
			assertTrue("Szerepel a forrásban", driver.getPageSource().contains(mail));
			Log.log("Képernyőn: " + mail);
		
		
		}
			
		
		
	

		submit();
		sleep(2000);
		Log.log("Tovább a fizetéshez");

		driver.findElement(By.cssSelector(".bg-blue.btnClass.uppercase.paymentButton")).click();
		sleep(2000);
		Log.log("Fizetés");

		driver.findElement(By.cssSelector(".btn.btn-lg.btn-primary.btn-block.btn-success")).click();
		sleep(2000);
		Log.log("Siker");
		

		}


	
	public static void AddCarSync()throws IOException, InterruptedException {
		
		int rand = new Random().nextInt(500)+500;
		String randUser = "Felhasználó "+rand;
		rand = new Random().nextInt(50)+1;
		int randLimit = rand;
		
		try {
			
				driver.findElement(By.xpath("(//a[contains(text(), 'Beállít')])[1]")).click();
				
				try {
					
					Log.log("Adatmezők kitöltése nélküli mentés ellenőrzés...");
					sleep(2000);
					driver.findElement(By.id("form-button")).click();
					sleep(2000);
					driver.findElement(By.className("error-message"));
					
				}catch(NoSuchElementException e) {
				
					Log.log("Nem jelenik meg a kötelező mező hibaüzenet!");
					driver.close();
					System.exit(0);
				
				}
				
				Log.log("Adatmezők kitöltése...");
				sleep(2000);
				driver.findElement(By.name("username")).sendKeys(randUser);
				fillName("car_limit",""+randLimit);
				driver.findElement(By.id("form-button")).click();
				sleep(2000); 
				Log.log("Adatmezők kitöltve, szinkron elindítva!");
		
			}catch(NoSuchElementException e) {

				Log.log("A szinkron már folyamatban");
				driver.findElement(By.xpath("(//a[contains(text(), 'Megtekint')])[1]")).click();
				sleep(2000);
				clickLinkWithText("Módosítás");
		      	sleep(2000);
		      	fillName("username", randUser);
		    	fillName("car_limit",""+randLimit);
				driver.findElement(By.id("form-button")).click();
				sleep(2000);
				Log.log("Átírva megfogható adatokra");

			}
		
				driver.findElement(By.xpath("(//a[contains(text(), 'Megtekint')])[1]")).click();
				sleep(2000);
				Log.log("Megtekint ellenőrzése...");
				
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//section/div/div/div/div/div[contains(text(),'"+randLimit+"')]")));
				assertTrue("Szerepel a forrásban", driver.getPageSource().contains(""+randLimit));
				Log.log("Képernyőn: " + randLimit);
					
				clickLinkWithText("Módosítás");
				sleep(2000);
				Log.log("Módosítás ellenőrzése...");
				
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//section//input[@value='" + randUser + "']")));
				assertTrue("Szerepel a forrásban", driver.getPageSource().contains(""+randUser));
				Log.log("Képernyőn: " + randUser);
				
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//section//input[@value='" + randLimit + "']")));
				assertTrue("Szerepel a forrásban", driver.getPageSource().contains(""+randLimit));
				Log.log("Képernyőn: " + randLimit);
				
				rand = new Random().nextInt(500)+500;
				randUser = "Felhasználó "+rand;
				rand = new Random().nextInt(50)+1;
				randLimit = rand;
				
				fillName("username", randUser);
		    	fillName("car_limit",""+randLimit);
				driver.findElement(By.id("form-button")).click();
				sleep(2000);
				Log.log("Adatok módosítva");
				
				
				
				driver.findElement(By.xpath("(//a[contains(text(), 'Megtekint')])[1]")).click();
				sleep(2000);
				Log.log("Szerkesztett megtekint ellenőrzése...");
				
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//section/div/div/div/div/div[contains(text(),'"+randLimit+"')]")));
				assertTrue("Szerepel a forrásban", driver.getPageSource().contains(""+randLimit));
				Log.log("Képernyőn: " + randLimit);
				
				clickLinkWithText("Módosítás");
				sleep(2000);
				Log.log("Szerkesztett módosítás ellenőrzése...");
				
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//section//input[@value='" + randUser + "']")));
				assertTrue("Szerepel a forrásban", driver.getPageSource().contains(""+randUser));
				Log.log("Képernyőn: " + randUser);
				
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//section//input[@value='" + randLimit + "']")));
				assertTrue("Szerepel a forrásban", driver.getPageSource().contains(""+randLimit));
				Log.log("Képernyőn: " + randLimit);
				
				driver.findElement(By.id("form-button")).click();
				sleep(2000);
				Log.log("Sikeres teszt");
				
				//admin interakció kell majd, ha működő képes, akkor írható tovább a teszt

}

	public static void registerCompany(String string, String email)
			throws IOException, AWTException, InterruptedException {
		WebElement element = driver.findElement(By.xpath("//a[contains(text(), \"Kattints ide\")]"));
		Actions actions = new Actions(driver);
		actions.moveToElement(element);
		actions.perform();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(), \"Kattints ide\")]")));

		driver.findElement(By.xpath("//a[contains(text(), \"Kattints ide\")]")).click();
		Log.log("Céges regisztráció link");

		driver.findElement(By.cssSelector("input[name='user[username]']")).sendKeys(email);
		driver.findElement(By.cssSelector("input[name='main_company[name]']")).sendKeys(string);
		Log.log("cégnév, cég email kitöltés");
		driver.findElement(By.className("multiselect")).click();

		/*
		 * org.openqa.selenium.Point coordinates =
		 * driver.findElement(By.className("multiselect")).getLocation(); Robot robot =
		 * new Robot(); robot.mouseMove(coordinates.getX(),coordinates.getY()+100);
		 */

		element = driver.findElement(By.className("multiselect-container"));
		WebElement element2 = driver.findElement(By.cssSelector("li:nth-child(8)"));
		actions = new Actions(driver);
		actions.moveToElement(element);
		actions.moveToElement(element2).click();
		actions.perform();
		sleep(3000);

		driver.findElement(By.cssSelector(".multiselect")).click();
		Log.log("Tevékenységi kör kitöltés");
		driver.findElement(By.id("user-password")).sendKeys(companyPassword);
		driver.findElement(By.id("user-confirm-password")).sendKeys(companyPassword);
		Log.log("Jelszó kitöltés");

		WebElement myElement = driver.findElement(By.xpath("//label[@for=\"user-accept-rules2\"]"));
		WebElement parent = myElement.findElement(By.xpath(".."));
		actions.moveToElement(parent, 5, 5).click().build().perform();
		Log.log("Accept privacy terms");

		myElement = driver.findElement(By.xpath("//label[@for=\"user-accept-rules\"]"));
		parent = myElement.findElement(By.xpath(".."));
		actions.moveToElement(parent, 5, 5).click().build().perform();
		Log.log("Accept rules");

		Random rand = new Random();
		String randNumTax = "";
		String randNumReg = "";
		for (int i = 0; i < 11; i++) {
			Integer randomNumTax = rand.nextInt((9) + 1);
			randNumTax = randNumTax + String.valueOf(randomNumTax);
		}
		driver.findElement(By.cssSelector("input[name='main_company[tax_no]']")).sendKeys(randNumTax);
		Log.log("Adószám kitöltés");

		for (int i = 0; i < 10; i++) {
			Integer randomNumReg = rand.nextInt((9) + 1);
			randNumReg = randNumReg + String.valueOf(randomNumReg);
		}
		driver.findElement(By.cssSelector("input[name='main_company[reg_no]']")).sendKeys(randNumReg);
		Log.log("Cégjegyzékszám kitöltés");
		driver.findElement(By.cssSelector("input[name='main_company[email]']")).sendKeys(email);
		Log.log("céges email kitöltés");
		driver.findElement(By.cssSelector("input[name='main_company[car_address][loc_zip_id_ac]']")).sendKeys("1051");
		sleep(2000);
		driver.findElement(By.cssSelector(".ui-menu-item:first-child")).click();
		Log.log("irsz kitöltés");
		// driver.findElement(By.cssSelector("main_company[car_address][street]']")).sendKeys("TestArea");
		driver.findElement(By.cssSelector("input[name='main_company[car_address][street]']")).sendKeys("Sas");
		Log.log("utca kitöltés");
		Select areaType = new Select(driver.findElement(By.id("main-company-car-address-street-type")));
		// Integer randomArea = rand.nextInt((187) + 1);
		// areaType.selectByValue("randomArea");
		areaType.selectByValue("1");
		Log.log("utca típus kitöltés");
		driver.findElement(By.cssSelector("input[name='main_company[car_address][street_num]']")).sendKeys("25");
		driver.findElement(By.cssSelector("input[name='main_company[car_address][building]']")).sendKeys("A");
		driver.findElement(By.cssSelector("input[name='main_company[car_address][floor]']")).sendKeys("2");
		driver.findElement(By.cssSelector("input[name='main_company[car_address][door]']")).sendKeys("204");
		Log.log("hsz, épület, emelet, ajtó kitöltés");
		driver.findElement(By.cssSelector("input[name='user[last_name]']")).sendKeys("Mr");
		driver.findElement(By.cssSelector("input[name='user[first_name]']")).sendKeys("Tester");
		Log.log("cégvezető neve kitöltés");
		driver.findElement(By.className("register")).click();
		Log.log("Regisztráció mentése");

	}

	public static void activateCompany(Boolean realActivation, String companyEmail) throws Exception {
		sleep(2000);
		driver.get("https://gmail.com");
		sleep(2000);
		Log.log("Gmail nyiáts");
		driver.findElement(By.cssSelector("input[type=\"email\"]")).sendKeys(testerMail);
		driver.findElement(By.xpath("//*[text()='Következő']")).click();
		Log.log("felhasználónév kitöltés");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type=password]")));
		Log.log("Jelszó kitöltés");
		driver.findElement(By.cssSelector("input[type=password]")).sendKeys(testerPassword);
		driver.findElement(By.xpath("//*[text()='Következő']")).click();
		Log.log("Login Gmail");

		sleep(6000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("(//*[text()='Céges fiók létrehozása - adatellenőrzés (ECDH)'])[2]")));
		driver.findElement(By.xpath("(//*[text()='Céges fiók létrehozása - adatellenőrzés (ECDH)'])[2]")).click();

		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//a[contains(text(), 'Céges fiók létrehozása')]")));

		boolean staleElement = true;
		while (staleElement) {
			try {
				driver.findElement(By.xpath("//a[contains(text(), 'Céges fiók létrehozása')]")).click();
				staleElement = false;

			} catch (StaleElementReferenceException e) {
				staleElement = true;
			}
		}

		Log.log("New user account activation");

		System.out.println(driver.getTitle());

		for (String winHandle : driver.getWindowHandles()) {
			System.out.println(winHandle);
			driver.switchTo().window(winHandle);
		}

		System.out.println(driver.getTitle());
		passShepherd();
		Log.log("Activation succeed");

	}

	public static void forgottenPassword(String email, String emailpassword, String password) throws Exception {
		driver.findElement(By.partialLinkText("Elfelejtetted")).click();
		driver.findElement(By.cssSelector("input[name='email_check']")).sendKeys(email);
		driver.findElement(By.className("btn-success")).click();

		driver.get(Gmail.getMails(email, emailpassword, "Elfelejtett", "href=\"(.*?)\">Jelszóváltás"));
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
	// public static final int EVJARAT = 2;
	// public static final int HONAP = 3;

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
		Random rand = new Random();
		String carYear = randomSelect("car_year");
		String carMonth = randomSelect("car_month");
		sleep(2000);
		manufacturer = fillCarField("#car-manufacturer-id", "#ui-id-1");
		sleep(2000);
		model = fillCarField("#car-model-id", "#ui-id-2");
		sleep(2000);
		click("#car-type-id");
		sleep(5000);
		
		String NumberPlate = generatePlateNumber();
        fillName("numberplate","111111");
        int kmNumberInt = rand.nextInt(998999)+10000;
        fillName("km",""+kmNumberInt);
       
		
		click(".btn-secondary");
		sleep(3000);
		passShepherd();
		sleep(1000);
		passShepherd();
		sleep(1000);
		passShepherd();
		sleep(1000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@class='breadcrumb-item'][2]/span")));
		//wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(), '" + manufacturer + "')]")));
		System.out.println(manufacturer);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(manufacturer));
		Log.log("Képernyőn: " + manufacturer);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@class='breadcrumb-item'][2]/span")));
		//wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(), '" + model + "')]")));
		System.out.println(model);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(model));
		Log.log("Képernyőn: " + model);
		
		onScreen(carYear);
		onScreen(carMonth);
		onScreen(NumberPlate);
		checkPrice(kmNumberInt," ");

	}
	public static void addNewCarOtherCountryTest() throws IOException, InterruptedException, AWTException {
		
		Random rand = new Random();
		String carYear = randomSelect("car_year");
		String carMonth = randomSelect("car_month");
		sleep(2000);
		manufacturer = fillCarField("#car-manufacturer-id", "#ui-id-1");
		sleep(2000);
		model = fillCarField("#car-model-id", "#ui-id-2");
		sleep(2000);
		click("#car-type-id");
		sleep(5000);
		String NumberPlate = generatePlateNumber();
        fillName("numberplate",""+NumberPlate);
		driver.findElement(By.name("numberplate_country")).click();
		Select orszag = new Select(driver.findElement(By.name("numberplate_country")));
		orszag.selectByVisibleText("A - Ausztria");
        int kmNumberInt = rand.nextInt(998999)+10000;
        fillName("km",""+kmNumberInt);
       
		
		click(".btn-secondary");
		sleep(3000);
		passShepherd();
		sleep(1000);
		passShepherd();
		sleep(1000);
		passShepherd();
		sleep(1000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@class='breadcrumb-item'][2]/span")));
		//wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(), '" + manufacturer + "')]")));
		System.out.println(manufacturer);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(manufacturer));
		Log.log("Képernyőn: " + manufacturer);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@class='breadcrumb-item'][2]/span")));
		//wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(), '" + model + "')]")));
		System.out.println(model);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(model));
		Log.log("Képernyőn: " + model);
		
		onScreen(carYear);
		onScreen(carMonth);
		onScreen(NumberPlate);
		checkPrice(kmNumberInt," ");
		
		
		

	}
	
	public static void addNewCarError() throws IOException, InterruptedException, AWTException {
		
		Random rand = new Random();
		String carYear = randomSelect("car_year");
		String carMonth = randomSelect("car_month");
		sleep(2000);
		manufacturer = fillCarField("#car-manufacturer-id", "#ui-id-1");
		sleep(2000);
		model = fillCarField("#car-model-id", "#ui-id-2");
		sleep(2000);
		click("#car-type-id");
		sleep(5000);
		
		String NumberPlate = generatePlateNumber();
        fillName("numberplate","111111");
        int kmNumberInt = rand.nextInt(998999)+10000;
        fillName("km",""+kmNumberInt);
       
		
		click(".btn-secondary");
		sleep(3000);
		passShepherd();
		sleep(1000);
		passShepherd();
		sleep(1000);
		passShepherd();
		sleep(1000);
		onScreen("Nem megfelelő formátum.");
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(model));
		Log.log("Hiba teszt sikeres Log mentve!");
		
		
		
	
	}
	
	public static void fillCarDetail() throws IOException, InterruptedException, AWTException {


		  sleep(2000);
		  clickLinkWithText("Adatok szerkesztése");
		  TestBase.select("petrol", "Dízel");
		  randomSelect("car_condition");
	 
			Random rand = new Random();
			long leftLimit = 11111111111111111L;
			long rightLimit = 99999999999999999L;
			long randomLong = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));

		    String vin = String.valueOf(randomLong);
			fillName("vin", vin);

			String engineNumber ="";
			Integer randomNum = rand.nextInt(999999999);
			engineNumber = randomNum.toString();
			fillName("motor_number", engineNumber);

			randomNum = rand.nextInt(199);
			String power = randomNum.toString(); 
			fillName("power", power);

			randomNum = rand.nextInt((899999) + 100000);
			String trafficLicense = randomNum.toString();
			String randomNumSt = String.valueOf(trafficLicense) + "AB";
			fillName("traffic_license", randomNumSt);

			
			String randomNumStr2 ="";
			randomNum = rand.nextInt((899999) + 10000);
			String registrationNumber = randomNum.toString();
			randomNumStr2 = String.valueOf(registrationNumber) + 'A';
			fillName("registration_number", randomNumStr2);

			randomNum = rand.nextInt(70)+10;
			String fuelCapacity = randomNum.toString();
			fillName("fuel_capacity", fuelCapacity);

			
			driver.findElement(By.name("doors")).click();
			driver.findElement(By.name("doors")).clear();
			sleep(2000);
			randomNum = rand.nextInt(3)+3;
			String doors = randomNum.toString();
			fillName("doors",doors);


			randomNum = rand.nextInt(4) + 1;
			String seats = randomNum.toString();
			fillName("seats",seats);
			
			String Cylinder="";
			String make ="";
			String gearType="";
			String carOffset="";
			String warranty="";
			String enviromental_V9="";
			make = randomSelect("make");
			gearType = randomSelect("gear_type");
			carOffset =randomSelect("car_offset");
			Cylinder =randomSelect("cylinder");
			warranty = randomSelect("warranty");
			enviromental_V9 = randomSelect("enviromental_v9");

			
			String max_Load="";
			randomNum = rand.nextInt(200) + 100;
			max_Load = randomNum.toString();
			fillName("max_load", max_Load);

			String trunk ="";
			randomNum = rand.nextInt(200) + 100;
			trunk = randomNum.toString();
			fillName("trunk", trunk);
	        
			int engine_capacity =0;
			randomNum = rand.nextInt(2000) + 1000;
			engine_capacity = rand.nextInt(9989)+1000;
			fillName("engine_capacity",""+engine_capacity);

			String netWeight ="";
			randomNum = rand.nextInt(3200) + 100;
			netWeight = randomNum.toString();
			fillName("net_weight", netWeight);
			
			String weight="";
			randomNum += 100;
			weight = randomNum.toString();
			fillName("weight", weight);

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

			String selectors = "1";
			int selInt = 1;
			String currentValue1="";
			String currentValue2="";
			String currentValue3="";
			String currentValue4="";
			String currentValue5="";
			String currentValue6="";
			
			
			elements = driver.findElements(By.tagName("select"));
			for (WebElement element : elements ) {
				String name = element.getAttribute("name");
				randomSelect(name);
				
				Select test = new Select(driver.findElement(By.name(name)));
				
				selectors = "" + selInt;
				
				switch(selectors) {
				
				case "1" : currentValue1 = test.getFirstSelectedOption().getText();break;
				case "2" : currentValue2 = test.getFirstSelectedOption().getText();break;
				case "3" : currentValue3 = test.getFirstSelectedOption().getText();break;
				case "4" : currentValue4 = test.getFirstSelectedOption().getText();break;
				case "5" : currentValue5 = test.getFirstSelectedOption().getText();break;
				case "6" : currentValue6 = test.getFirstSelectedOption().getText();break;
						   
				}
				
				selInt++;
					
			}
			
			Log.log(currentValue1);
			Log.log(currentValue2);
			Log.log(currentValue3);
			Log.log(currentValue4);
			Log.log(currentValue5);
			Log.log(currentValue6);

			elements = driver.findElements(By.cssSelector(".checkbox-label"));
			for (WebElement element : elements) {
				rand = new Random();
				randomNum = rand.nextInt(2);
				if (randomNum == 0) {
					element.click();
					Thread.sleep(1500);
				}
			}
			
            sleep(2000);
			driver.findElement(By.id("save-and-back")).click();
			Thread.sleep(3000);

			Log.log("Autó beküldve.");
			onScreen(vin);
			onScreen(engineNumber);
			onScreen(registrationNumber);
			onScreen(trafficLicense);
			
			wait.until(
			ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'half-box'][1]/dd[contains(text(), '"+ seats +"')]")));
			System.out.println(seats);
			assertTrue("Szerepel a forrásban", driver.getPageSource().contains(seats));
			Log.log("Képernyőn: " + seats);
			
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[@class = 'half-box'][2]/dd[contains(text(),'" + doors + "')])[1]")));
			System.out.println(doors);
			assertTrue("Szerepel a forrásban", driver.getPageSource().contains(doors));
			Log.log("Képernyőn: " + doors);
					
			onScreen("Dízel");
			checkPrice(engine_capacity," ");
			onScreen(make);
			onScreen(gearType);
			
			
			driver.findElement(By.cssSelector(".btn.btn-primary.btn-block.d-sm-block")).click();
			
			
			
					
			onScreenValue(vin);
			onScreenValue(engineNumber);
			onScreenValue(power);
			onScreenValue(randomNumSt);
			onScreenValue(randomNumStr2);
			onScreenValue(fuelCapacity);
			onScreen(make);
			onScreen(gearType);
			onScreen(carOffset);
			onScreen(Cylinder);
			onScreen(warranty);
			onScreen(enviromental_V9);
			onScreenValue(max_Load);
			onScreenValue(trunk);
			onScreenValue(""+engine_capacity);
			onScreenValue(netWeight);
			onScreenValue(weight);
			
			
		    driver.findElement(By.id("form-button")).click();
		    sleep(2000);
		    driver.findElement(By.id("form-button")).click();
		    onScreen(currentValue1);
		    onScreen(currentValue2);
		    onScreen(currentValue3);
		    onScreen(currentValue4);
		    onScreen(currentValue5);
		    
		    driver.findElement(By.id("save-and-back")).click();
		   
		    

		    
		    
		    clickLinkWithText("Adatok szerkesztése");
			TestBase.select("petrol", "Dízel");
			randomSelect("car_condition");
	 
	          rand = new Random();
			 leftLimit = 11111111111111111L;
			 rightLimit = 99999999999999999L;
			 randomLong = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));

		     vin = String.valueOf(randomLong);
			fillName("vin", vin);

			 engineNumber ="";
			 randomNum = rand.nextInt(999999999);
			engineNumber = randomNum.toString();
			fillName("motor_number", engineNumber);

			randomNum = rand.nextInt(199);
			 power = randomNum.toString(); 
			fillName("power", power);

			randomNum = rand.nextInt((899999) + 100000);
			 trafficLicense = randomNum.toString();
			 randomNumSt = String.valueOf(trafficLicense) + "AB";
			fillName("traffic_license", randomNumSt);

			
			 randomNumStr2 ="";
			randomNum = rand.nextInt((899999) + 10000);
			 registrationNumber = randomNum.toString();
			randomNumStr2 = String.valueOf(registrationNumber) + 'A';
			fillName("registration_number", randomNumStr2);

			randomNum = rand.nextInt(70)+10;
			fuelCapacity = randomNum.toString();
			fillName("fuel_capacity", fuelCapacity);

			
			driver.findElement(By.name("doors")).click();
			driver.findElement(By.name("doors")).clear();
			sleep(2000);
			randomNum = rand.nextInt(3)+3;
			 doors = randomNum.toString();
			fillName("doors",doors);


			randomNum = rand.nextInt(4) + 1;
			 seats = randomNum.toString();
			fillName("seats",seats);
			
			 Cylinder="";
			 make ="";
			 gearType="";
			 carOffset="";
			 warranty="";
			 enviromental_V9="";
			make = randomSelect("make");
			gearType = randomSelect("gear_type");
			carOffset =randomSelect("car_offset");
			Cylinder =randomSelect("cylinder");
			warranty = randomSelect("warranty");
			enviromental_V9 = randomSelect("enviromental_v9");

			
			 max_Load="";
			randomNum = rand.nextInt(200) + 100;
			max_Load = randomNum.toString();
			fillName("max_load", max_Load);

			trunk ="";
			randomNum = rand.nextInt(200) + 100;
			trunk = randomNum.toString();
			fillName("trunk", trunk);
	        
			 engine_capacity =0;
			randomNum = rand.nextInt(2000) + 1000;
			engine_capacity = rand.nextInt(9989)+1000;
			fillName("engine_capacity",""+engine_capacity);

			netWeight ="";
			randomNum = rand.nextInt(3200) + 100;
			netWeight = randomNum.toString();
			fillName("net_weight", netWeight);
			
			weight="";
			randomNum += 100;
			weight = randomNum.toString();
			fillName("weight", weight);

			click(".btn-secondary");
			Thread.sleep(3000);
			click(".btn-secondary");
			Thread.sleep(3000);

			elements = driver.findElements(By.className("collapsed"));
			for (WebElement element : elements) {
				String name = element.getAttribute("data-target");
				Log.log(name + " collapsed");
				element.click();
				Thread.sleep(1500);
			}

			 selectors = "1";
			 selInt = 1;
			 currentValue1="";
			 currentValue2="";
			 currentValue3="";
			 currentValue4="";
			 currentValue5="";
			 currentValue6="";
			
			
			elements = driver.findElements(By.tagName("select"));
			for (WebElement element : elements ) {
				String name = element.getAttribute("name");
				randomSelect(name);
				
				Select test = new Select(driver.findElement(By.name(name)));
				
				selectors = "" + selInt;
				
				switch(selectors) {
				
				case "1" : currentValue1 = test.getFirstSelectedOption().getText();break;
				case "2" : currentValue2 = test.getFirstSelectedOption().getText();break;
				case "3" : currentValue3 = test.getFirstSelectedOption().getText();break;
				case "4" : currentValue4 = test.getFirstSelectedOption().getText();break;
				case "5" : currentValue5 = test.getFirstSelectedOption().getText();break;
				case "6" : currentValue6 = test.getFirstSelectedOption().getText();break;
						   
				}
				
				selInt++;
					
			}
			
			Log.log(currentValue1);
			Log.log(currentValue2);
			Log.log(currentValue3);
			Log.log(currentValue4);
			Log.log(currentValue5);
			Log.log(currentValue6);

			elements = driver.findElements(By.cssSelector(".checkbox label"));
			for (WebElement element : elements) {
				rand = new Random();
				randomNum = rand.nextInt(2);
				if (randomNum == 0) {
					element.click();
				}
			}

			driver.findElement(By.id("save-and-back")).click();
			Thread.sleep(3000);

			Log.log("Autó beküldve.");
			onScreen(vin);
			onScreen(engineNumber);
			onScreen(registrationNumber);
			onScreen(trafficLicense);
			
			wait.until(
			ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'half-box'][1]/dd[contains(text(), '"+ seats +"')]")));
			System.out.println(seats);
			assertTrue("Szerepel a forrásban", driver.getPageSource().contains(seats));
			Log.log("Képernyőn: " + seats);
			
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[@class = 'half-box'][2]/dd[contains(text(),'" + doors + "')])[1]")));
			System.out.println(doors);
			assertTrue("Szerepel a forrásban", driver.getPageSource().contains(doors));
			Log.log("Képernyőn: " + doors);
					
			onScreen("Dízel");
			checkPrice(engine_capacity," ");
			onScreen(make);
			onScreen(gearType);
			
			
			driver.findElement(By.cssSelector(".btn.btn-primary.btn-block.d-sm-block")).click();
			
			
			
					
			onScreenValue(vin);
			onScreenValue(engineNumber);
			onScreenValue(power);
			onScreenValue(randomNumSt);
			onScreenValue(randomNumStr2);
			onScreenValue(fuelCapacity);
			onScreen(make);
			onScreen(gearType);
			onScreen(carOffset);
			onScreen(Cylinder);
			onScreen(warranty);
			onScreen(enviromental_V9);
			onScreenValue(max_Load);
			onScreenValue(trunk);
			onScreenValue(""+engine_capacity);
			onScreenValue(netWeight);
			onScreenValue(weight);
		    driver.findElement(By.id("form-button")).click();
		    sleep(2000);
		    driver.findElement(By.id("form-button")).click();
		    onScreen(currentValue1);
		    onScreen(currentValue2);
		    onScreen(currentValue3);
		    onScreen(currentValue4);
		    onScreen(currentValue5);

	}

	public static void addNewCarEventFuel() throws IOException, InterruptedException {
		clickLinkWithText("esemény");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-fueling")));
		click(".sprite-fueling");

		submit();
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'A mező nem lehet üres')]")));
		assertTrue("Kötelező mezők validálása", driver.getPageSource().contains("A mező nem lehet üres"));
		Log.log("Kötelező mezők validálása.");

		Random rand = new Random();
		
		Integer randomNum = rand.nextInt(50) + 10;
		String liter = String.valueOf(randomNum);
		
		fillName("liter", liter);
		
		randomNum = rand.nextInt(10000)+10000;
		Integer cost = randomNum;
		
		fillName("fueling_cost", ""+cost);
		
		driver.findElement(By.id("fueling-date")).click();
		driver.findElement(By.id("fueling-date")).sendKeys(Keys.ENTER);
		
		String fuelType = randomSelect("type");
		
		fillName("car_gas_station_id_ac", "mészá");
		sleep(1000);
		click("ul li.ui-menu-item:nth-child(2) a");

		submit();
		sleep(1000);

		Log.log("Esemény: tankolás beküldve.");
		onScreen(liter + " l");
		Log.log("Esemény: tankolás elmentve.");
		sleep(2000);

		click("a[href*=\"tankolas-megtekintese\"]");
		sleep(1000);
		
		onScreen(fuelType);
		onScreen(liter + " l");
		assertTrue("Gas station coordinates false",
				driver.getPageSource().contains("google.maps.LatLng(47.49087143, 19.03070831)"));
		driver.findElement(By.id("map0")).isDisplayed();
		Log.log("Térkép ok");
		
		checkPrice(cost, " ");

		clickLinkWithText("Szerkesztés");
		checkField("liter", liter);
		checkField("type", fuelType);
		checkField("fueling_cost", ""+cost);
		
		randomNum = rand.nextInt(50) + 10;
		liter = String.valueOf(randomNum);
		fillName("liter", liter);
		
		randomNum = rand.nextInt(10000)+10000;
		cost = randomNum;
		fillName("fueling_cost", ""+cost);
		
		submit();
		sleep(1000);
		
		Log.log("Módosítva");
		onScreen(liter + " l");
		onScreen(fuelType);
		checkPrice(cost, " ");
		sleep(1000);
		
		click("a[href*=\"tankolas-megtekintese\"]");
		sleep(1000);
		
		onScreen(fuelType);
		onScreen(liter + " l");
		assertTrue("Gas station coordinates false",
				driver.getPageSource().contains("google.maps.LatLng(47.49087143, 19.03070831)"));
		driver.findElement(By.id("map0")).isDisplayed();
		Log.log("Térkép ok");
		
		checkPrice(cost, " ");
		

		driver.findElement(By.cssSelector(".fas.fa-trash.circle")).click();
		sleep(1000);
		driver.findElement(By.cssSelector(".btn.grayBtn.deleteAttachedItem")).click();

		sleep(4000);
		assertTrue("Event deleted", !driver.getPageSource().contains(liter + " l"));
		Log.log("Esemény: Tankolás sikeresen törölve.");

	}

	public static void addNewCarEventTechspec() throws IOException, InterruptedException {
		clickLinkWithText("esemény");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-mot")));
		click(".sprite-mot");

		click("input[name=\"test_date\"]");
		click(".logo-title");

		Random rand = new Random();
		int randomNum = 1000 + rand.nextInt((50000 - 1) + 1);
		String noteText = "Note " + String.valueOf(randomNum);
		fillName("note", noteText);

		fillName("car_company_id_ac", "Abc kft.");
		submit();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Autó vizsgáztatva')]")));

		assertTrue("Mûszaki vizsga elmentve", driver.getPageSource().contains("Autó vizsgáztatva"));
		Log.log("Esemény: mûszaki vizsga elmentve.");

		onScreen("Abc kft.");
		
		sleep(3000);

		driver.findElement(By.cssSelector("a[href*='muszaki-vizsga-megtekintese']")).click();
		
		sleep(3000);
		onScreen("Abc kft.");
		String now = dateLocale(LocalDate.now());

		onScreen(now);
		onScreen(noteText);

		clickLinkWithText("Szerkesztés");

		now = dateDashes(LocalDate.now());
		checkField("test_date", now);
		checkField("car_company_id_ac", "Abc kft.");
		onScreen(noteText);

		driver.findElement(By.xpath("//*[contains(text(), 'Taxi')]")).click();
		submit();

		clickLinkWithText("Műszaki vizsga");
		
		now = dateLocale(LocalDate.now().plusYears(1));
		onScreen(now);

		click("i.fa-trash");
		clickLinkWithText("Esemény törlése");

		sleep(6000);
		assertTrue("Event deleted", !driver.getPageSource().contains(noteText));
		Log.log("Esemény: mûszaki vizsga sikeresen törölve.");

	}

	public static void setCarForSale() throws IOException, InterruptedException {
        
		String carURL = driver.getCurrentUrl();
		
		Random rand = new Random();
		int randomprice = rand.nextInt(5000000) + 1000000;
		clickLinkWithText("Eladásra kínálom");
		click(".switch");
		sleep(1000);
		fillName("sell_price", ""+randomprice);
		sleep(1000);
		driver.findElement(By.name("sell_description")).clear();
		String randomText = UUID.randomUUID().toString();
		fillName("sell_description", randomText);
		
	
		sleep(1000);
		rand = new Random();
		int randomzip = rand.nextInt(89) + 10;
        
		while(13<= randomzip && randomzip <=19) {randomzip = rand.nextInt(89) + 10;}
		
        fillName("loc_zip_id_ac",""+randomzip);
		
		try {
			driver.findElement(By.cssSelector(".ui-menu-item")).click();
		}catch(NoSuchElementException e){
			
			randomzip=rand.nextInt(89)+10;
			while(13<= randomzip && randomzip <=19) {randomzip = rand.nextInt(89) + 10;}
			fillName("loc_zip_id_ac",""+randomzip);
			click(".ui-menu-item");
			
		}
		
		
		fillName("loc_zip_id_ac", ""+randomzip);
		
		sleep(1000);
		click("#ui-id-1");
		sleep(1000);
		TestBase.select("car_user[mobile_country]", "Magyarország");
		sleep(1000);
		fillName("car_user[mobile]", "301234567");
		sleep(1000);
		driver.findElement(By.id("save-and-back")).click();
		sleep(2000);
		checkPrice(randomprice, " ");
		driver.findElement(By.cssSelector(".fas.fa-eye")).click();
		checkPrice(randomprice, " ");
		onScreen(randomText);
		WebElement nameText = driver.findElement(By.className("name"));
		String nametextValue = nameText.getText();
		onScreen(""+nametextValue);
		sleep(10000);
		Log.log("Autó sikeresen meghirdetve");
		clickLinkWithText("Használt autó hirdetések");
		Log.log("Használt Autó kereső");
		clickLinkWithText("Részletes kereső");
		Log.log("Részletes Kereső Kiválasztva");
		fillName("pricefrom",""+randomprice);
		fillName("priceto",""+randomprice);
		fillName("loc_zip_id_ac",""+randomzip);
		Log.log("Ár megadva");
		Log.log("IRSZ megadva!");
		driver.findElement(By.id("form-button")).click();
		Log.log("Találatok Megjelenítése");
		driver.findElement(By.className("price")).click();
		checkPrice(randomprice, " ");
		onScreen(randomText);
		onScreen("");
		Log.log("Az autó szerepel a Használt Autó hírdetések között!");
		goToPage(carURL);
		sleep(3000);
		driver.findElement(By.cssSelector(".fas.fa-pencil-alt")).click();
		click(".switch");
		clickLinkWithText("Vissza az adatlapra");
		Log.log("Hirdetés levéve");

	}

	public static void addNewCarEventTires() throws IOException, InterruptedException {
		clickLinkWithText("esemény");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-mot")));
		click(".sprite-tire");

		click("input[name=\"service_date\"]");
		// click("body");
		//new Actions(driver).moveByOffset(0, 0).click().build().perform();
		driver.findElement(By.id("service-date")).sendKeys(Keys.ENTER);

		clickLinkWithText("Új felvétele");

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
		// fillName("car_mycar_service_log_items[0][price]", priceString);
		randomSelect("car_mycar_service_log_items[0][tire_position]");
		fillName("car_company_id_ac", "Abc Kft.");

		price = 1000 + rand.nextInt((50000 - 1) + 1);
		priceString = "" + price;
		fillName("price_work", priceString);

		randomNum = 1000 + rand.nextInt((50000 - 1) + 1);
		noteText = "Note " + String.valueOf(randomNum);
		fillName("note", noteText);

		driver.findElement(By.cssSelector("#form .submitBtn")).click();

		Log.log("Esemény: gumicsere elmentve.");

	}

	public static void addNewCarEventCleaning() throws IOException, InterruptedException {
		// driver.findElement(By.xpath("//span[contains(text(),
		// \"esemény\")]")).click();
		// wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-cleaning")));
		// driver.findElement(By.className("sprite-cleaning")).click();

		clickLinkWithText("esemény hozzáadása");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-cleaning")));
		click(".sprite-cleaning");

		String cleaningType = randomSelect("cleaning_type");
		click("input[name=\"cleaning_date\"]");
		click(".logo-title");

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

		Log.log("Esemény: tisztítás beküldve.");

		String now = dateLocale(LocalDate.now());
		System.out.println(now);
		onScreen(now);
		onScreen("Autó tisztítva");
		onScreen(cleaningType);
		Log.log("Esemény: tisztítás elmentve.");

		clickLinkWithText("Autó tisztítva");
		onScreen(now);
		onScreen(cleaningType);
		checkPrice(price, " ");
		onScreen(noteText);

		clickLinkWithText("Szerkesztés");
		checkField("cleaning_type", cleaningType);
		checkField("price", priceString);
		checkField("note", noteText);
		submit();
		sleep(3000);
		
		onScreen(cleaningType);
		clickLinkWithText(cleaningType);
		
		sleep(2000);
		onScreen(now);
		onScreen(cleaningType);
		checkPrice(price, " ");
		onScreen(noteText);
		
		clickLinkWithText("Szerkesztés");
		checkField("cleaning_type", cleaningType);
		checkField("price", priceString);
		checkField("note", noteText);
		submit();
		sleep(3000);
		

		driver.findElement(By.cssSelector(".fas.fa-trash.circle")).click();
		sleep(2000);
		click("a[data-apply=\"confirmation\"]");

		sleep(4000);
		assertTrue("Event deleted", !driver.getPageSource().contains(noteText));
		Log.log("Esemény: tisztítás sikeresen törölve.");
	}

	public static void addNewCarEventAccident() throws IOException, InterruptedException {

		WebElement element = driver.findElement(By.className("event-types"));
		((JavascriptExecutor) driver).executeScript("arguments[0].style.display='none'", element);
		driver.findElement(By.cssSelector(".events .add-link")).click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-accident")));
		click(".sprite-accident");

		click("input[name=\"accident_date\"]");
		// click(".blue-heading");
		driver.findElement(By.cssSelector("input[name=\"accident_date\"]")).sendKeys();

		int randNumber = new Random().nextInt(123456);
		String noteText = "Test note " + randNumber;
		// fillName("note", noteText);
		driver.findElement(By.cssSelector("textarea[name=\"note\"]")).sendKeys(noteText);

		submit();

		Log.log("Esemény: baleset beküldve.");

		String now = dateLocale(LocalDate.now());
		System.out.println(now);
		onScreen(now);
		onScreen("Az autó megsérült");

		Log.log("Esemény: baleset elmentve.");

		sleep(2000);
		driver.findElement(By.cssSelector("a[href*='baleset-esemeny-megtekintese']")).click();
		sleep(2000);
		
		onScreen(noteText);

		clickLinkWithText("Szerkesztés");
		onScreen(noteText);
		submit();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("event-types")));
		element = driver.findElement(By.className("event-types"));
		((JavascriptExecutor) driver).executeScript("arguments[0].style.display='none'", element);

		click("i.fa-trash");
		click("a[data-apply=\"confirmation\"]");

		sleep(10000);
		assertTrue("Event deleted", !driver.getPageSource().contains(noteText));
		Log.log("Esemény: baleset sikeresen törölve.");

	}

	public static void addNewCarEventOther() throws IOException, InterruptedException {
		// driver.findElement(By.xpath("//span[contains(text(),
		// \"esemény\")]")).click();
		clickLinkWithText("esemény");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-other")));
		driver.findElement(By.className("sprite-other")).click();

		Random rand = new Random();
		Integer randomNum = 1 + rand.nextInt((3000000 - 1) + 1);
		String randNum = String.valueOf(randomNum);
		String eventText = "Teszt esemény " + randNum;
		driver.findElement(By.cssSelector("input[name=\"title\"]")).sendKeys("Teszt esemény " + randNum);

		click("input[name=\"event_date\"]");
		click(".logo-title");

		int randNumber = new Random().nextInt(123456);
		String noteText = "Test note " + randNumber;
		fillName("note", noteText);

		driver.findElement(By.className("submitBtn")).click();
		sleep(2000);

		Log.log("Esemény: egyéb beküldve.");

		wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + eventText + "')]")));
		assertTrue("Autó meghirdetve", driver.getPageSource().contains("Teszt esemény " + randNum));

		Log.log("Esemény: egyéb sikeresen elmentve.");
		sleep(2000);

		clickLinkWithText(eventText);
		driver.findElement(By.cssSelector("a.red-link")).click();
		clickLinkWithText("Esemény törlése");

		assertTrue("Event deleted", !driver.getPageSource().contains(eventText));
		Log.log("Esemény: egyéb sikeresen törölve.");
	}

	public static void adminLogin() throws IOException, InterruptedException {
		sleep(5000);
		goToPage(url + "/hu/bejelentkezes");
		sleep(5000);
		fillName("username", adminUser);
		fillName("password", adminPassword);
		driver.findElement(By.className("btn-secondary")).click();
		Log.log("Admin bejelentkezve");
		Thread.sleep(5000);
	}

	public static void adminActivatecompany(String companyName) throws IOException {
		goToPage(url + "/hu/admin/car/car-companies");
		Log.log("Admin cégek");
		clickLinkWithText(companyName);

		driver.findElement(By.xpath("/html/body/section/section/div/div[1]/ul/li/a")).click();
		driver.findElement(By.xpath("/html/body/section/section/div/div[1]/ul/li/ul/li[1]/a")).click();
		driver.findElement(By.xpath("/html/body/section/section/div/form/div[3]/div[2]/div[1]/div[2]/a[2]/i")).click();
		Log.log("Cég jóváhagyva");
		goToPage(url + "/hu/kijelentkezes");
		Log.log("Admin kijelentkezés");
	}

	public static void deleteCompany(String companyName) throws IOException {
		goToPage(url + "/hu/admin/car/car-companies");
		driver.findElement(By.xpath("//*[contains(text(), '" + companyName + "')]/following::a[4]")).click();
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.cssSelector(".confirmation .popover-content .bgm-lightblue")));
		driver.findElement(By.cssSelector(".confirmation .popover-content .bgm-lightblue")).click();
	}

	public static void deleteUser(String userName) throws IOException {
		goToPage(url + "/hu/admin/car/car-users");
		driver.findElement(By.xpath("//*[contains(text(), '" + userName + "')]/following::a[4]")).click();
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.cssSelector(".confirmation .popover-content .bgm-lightblue")));
		driver.findElement(By.cssSelector(".confirmation .popover-content .bgm-lightblue")).click();
	}

	public static void oneStepInner() throws IOException, InterruptedException {
		List<WebElement> elements = driver.findElements(By.cssSelector("#mycar-block.card .profile-car-item"));
		for (WebElement element : elements) {
			Log.log(element.findElement(By.className("numberplate")).getText());
		}

		element = elements.get(new Random().nextInt(elements.size()));
		Log.log(element.findElement(By.className("numberplate")).getText() + " selected.");
		sleep(3000);
		element.click();
		sleep(3000);
	}

	public static String SendRequestTire() throws IOException, InterruptedException {

		driver.findElement(By.xpath("//a[contains(text(), \"Ajánlatkérés\")]")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-tire")));
		driver.findElement(By.className("sprite-tire")).click();

		// TestBase.select("car_tire_request_items[0][width]", "165");
		Thread.sleep(1000);
		TestBase.randomSelect("car_tire_request_items[0][width]");
		// TestBase.select("car_tire_request_items[0][height]", "50");
		Thread.sleep(1000);
		TestBase.randomSelect("car_tire_request_items[0][height]");
		// TestBase.select("car_tire_request_items[0][diameter]", "r17");
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
		driver.findElement(By.id("end-date")).click();
		sleep(1000);
		driver.findElement(By.id("end-date")).sendKeys(Keys.ARROW_RIGHT);
		driver.findElement(By.id("end-date")).sendKeys(Keys.ARROW_RIGHT);
		driver.findElement(By.id("end-date")).sendKeys(Keys.ARROW_RIGHT);
		driver.findElement(By.id("end-date")).sendKeys(Keys.ENTER);
		driver.findElement(By.className("submitBtn")).click();

		// wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),
		// 'Sikeres')]")));
		// assertTrue("Tire request succeed",
		// driver.getPageSource().contains("Sikeres"));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".order-1 a")));
		String requestId = driver.findElement(By.cssSelector(".order-1 a")).getText();
		System.out.println("ID" + requestId);
		Log.log("Gumi ajánlatkérés elküldve.");

		return requestId;

	}
	public static String haKell;
	public static String checkRequest(String requestId) throws IOException, InterruptedException {
		TestBase.goToPage(url + "/hu/gumi-erdeklodesek");

		assertTrue("Tire request succeed", driver.getPageSource().contains(requestId));
		Log.log("Gumi ajánlatkérés megérkezett.");

		click(".bell");
		clickLinkWithText("Gumi ajánlatkérés");
		onScreen(requestId);
		Log.log("Értesítés céges oldalon megérkezett.");

		// driver.findElement(By.cssSelector("a[data-original-title=\"Ajánlat
		// adása\"]")).click();

		randomSelect("car_tire_company_offer_items[0][manufacturer]");
		fillName("car_tire_company_offer_items[0][item_description]", "test");

		Random rand = new Random();
		Integer randomNum = 2000 + rand.nextInt((30000 - 1) + 1);
		String randNum = String.valueOf(randomNum);
		fillName("car_tire_company_offer_items[0][price]", randNum);
		haKell = randNum;

		randomSelect("car_tire_company_offer_items[0][season]");
		driver.findElement(By.cssSelector("input[name=\"car_tire_company_offer_items[0][delivery_date]\"]")).click();
		driver.findElement(By.cssSelector("textarea[name=\"note\"]")).sendKeys("test note");

		driver.findElement(By.className("submitBtn")).click();
		Thread.sleep(5000);
		
		Log.log("Ajánlat adása");

		return randNum;

	}

	public static String checkRequestPart(String requestId) throws IOException, InterruptedException {
		TestBase.goToPage(url + "/hu/alkatresz-erdeklodesek");

		assertTrue("Part request succeed", driver.getPageSource().contains(requestId));
		Log.log("Alkatrész ajánlatkérés megérkezett.");

		click(".bell");
		clickLinkWithText("Alkatrész ajánlatkérés");
		onScreen(requestId);
		Log.log("Értesítés céges oldalon megérkezett.");

		
		fillName("car_piece_part_company_offer_items[0][item_description]", "test");

		Random rand = new Random();
		Integer randomNum = 2000 + rand.nextInt((30000 - 1) + 1);
		String randNum = String.valueOf(randomNum);
		fillName("car_piece_part_company_offer_items[0][price]", randNum);
		haKell = randNum;

		//randomSelect("car_part_company_offer_items[0][season]");
		driver.findElement(By.cssSelector("input[name=\"car_piece_part_company_offer_items[0][delivery_date]\"]")).click();
		driver.findElement(By.id("car-piece-part-company-offer-items-0-delivery-date")).sendKeys(Keys.ARROW_RIGHT);
		driver.findElement(By.id("car-piece-part-company-offer-items-0-delivery-date")).sendKeys(Keys.ENTER);
		sleep(2000);
		
		driver.findElement(By.id("valid-to")).click();
		driver.findElement(By.id("valid-to")).sendKeys(Keys.ARROW_RIGHT);
		driver.findElement(By.id("valid-to")).sendKeys(Keys.ARROW_RIGHT);
		driver.findElement(By.id("valid-to")).sendKeys(Keys.ENTER);
		sleep(2000);
		
		fillName("delivery_price_from", "20000");
		fillName("delivery_price_to", "40000");
		
		
		driver.findElement(By.cssSelector("textarea[name=\"note\"]")).sendKeys("test note");

		driver.findElement(By.cssSelector(".btn.btn-secondary.btn-lg")).click();
		Thread.sleep(5000);
		
		Log.log("Ajánlat adás");
		
		return randNum;

	}

	public static void checkSelect(String name, String text) throws IOException {
		String defaultItem = new Select(driver.findElement(By.cssSelector("select[name='" + name + "']")))
				.getFirstSelectedOption().getText();

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

	public static void userLogout() throws IOException, InterruptedException {
		sleep(3000);
		Log.log("Kijelentkezés a fiókból.");
		goToPage(url + "/hu/kijelentkezes");
		sleep(3000);
	}

	public static void registerUserWrongEmail() throws IOException {

		driver.findElement(By.partialLinkText("Regisztráció")).click();
		/*assertEquals("Go to URL", driver.getCurrentUrl(), url + "/hu/regisztracio");
		Log.log("Click Registraion");*/

		try {
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
		Log.log("Szabálytalan e-mail cím.");
		Log.log("Jelszavak nem egyeznek.");

		driver.findElement(By.className("register")).click();
		Log.log("Regisztráció gomb megnyomása.");

		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Úgy tűnik elírta az e-mail címét.')]")));
		assertTrue("Wrong email format", driver.getPageSource().contains("Úgy tűnik elírta az e-mail címét."));
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'A két jelszó nem egyezik')]")));
		assertTrue("Different passwords", driver.getPageSource().contains("A két jelszó nem egyezik"));
		Log.log("Regisztrálás blokkolva");

	}

	public static void addNewCarNotes() throws IOException {
		// driver.findElement(By.cssSelector(".car-mycar-notes a")).click();
		driver.findElement(By.cssSelector("a.float-right.popup.btn-icon.btn-primary.small")).click();

		Random rand = new Random();
		Integer randomNum = 1 + rand.nextInt((3000000 - 1) + 1);
		String randNum = String.valueOf(randomNum);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note")));
		fillName("note", "Note-" + randNum);
		click(".submitButton");
		Log.log("Jegyzet \"Note-" + randNum + "\" beküldve.");

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + randNum + "')]")));
		assertTrue("Note appeared", driver.getPageSource().contains(randNum));
		Log.log("Jegyzet elmentve.");

	}

	public static void deleteCarNote() throws IOException, InterruptedException {
		String note = driver.findElement(By.cssSelector(".car-mycar-notes .note-item:nth-child(3)")).getText();
		Log.log("Törlendő jegyzet: " + note);
		driver.findElement(By.cssSelector(".car-mycar-notes .note-item:nth-child(3) .note-delete")).click();
		sleep(10000);

		assertTrue("Note deleted", !driver.getPageSource().contains(note));
		Log.log("Jegyzet: " + note + " törölve.");

	}

	public static void allCarNote() throws IOException, InterruptedException {
		driver.findElement(By.cssSelector(".car-mycar-notes .moreBtn")).click();
		driver.findElement(By.cssSelector(".car-mycar-notes .card-header a")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note")));

		Random rand = new Random();
		Integer randomNum = 1 + rand.nextInt((3000000 - 1) + 1);
		String randNum = String.valueOf(randomNum);
		fillName("note", "note-" + randNum);
		click(".submitButton");
		Log.log("Jegyzet \"note-" + randNum + "\" beküldve.");

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + randNum + "')]")));
		assertTrue("Note appeared", driver.getPageSource().contains("note-" + randNum));
		Log.log("Jegyzet elmentve.");

		String note = driver.findElement(By.cssSelector(".car-mycar-notes .note-item:nth-child(2)")).getText();
		System.out.println(note);
		driver.findElement(By.cssSelector(".car-mycar-notes .note-item:nth-child(2) .note-delete")).click();
		Thread.sleep(10000);

		assertTrue("Note deleted", !driver.getPageSource().contains(note));
		Log.log("Jegyzet: " + note + " törölve.");

	}

	public static void advancedSearch() throws IOException {
		click(".user-menu .nav-menu a");
		click(".sprite-used-cars");
		click(".detail-search-left a");
		fillName("pricefrom", "2194560");
		fillName("priceto", "2194564");
		submit();
		Log.log("Keresés a meghirdetett autóra.");

		// wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),
		// '2 194 562 Ft')]")));
		assertTrue("Car found", driver.getPageSource().contains("BMW 116"));
		Log.log("Autó szerepel a használtautó keresőben.");
	}

	private static void click(String css) {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(css)));
		driver.findElement(By.cssSelector(css)).click();
	}

	public static void checkRequestOfferTire(String companyName, String price) throws IOException {
		driver.findElement(By.cssSelector("#active-tire-requests .list-item:nth-child(1) a")).click();
		onScreen(companyName);
		Log.log("Ajánlat megérkezett.");

		click(".bell");
		clickLinkWithText("Gumi ajánlat");
		onScreen(companyName);
		Log.log("Értesítés user oldalon megérkezett (" + companyName + ").");
		String savedPrice = driver.findElement(By.className("price")).getText();

		double amount = Double.parseDouble(price);
		DecimalFormat formatter = new DecimalFormat("###,###,###");

		String formattedPrice = formatter.format(amount).replaceAll(",", " ");

		if(savedPrice.equals(formattedPrice+" Ft")) {
			
			Log.log("Ár stimmel");
			
		}else {
		
		Log.log("Ár nem stimmel!!!!!!!!!!");
		Log.log("Itt szerepel: "+savedPrice);
		Log.log("Korábban megadott: "+formattedPrice+" Ft");
		
		}
		
	}
	
	public static void checkRequestOfferPart(String companyName, String price) throws IOException {
		
		driver.findElement(By.cssSelector("#active-piecepart-requests .list-item:nth-child(1) a")).click();
		onScreen(companyName);
		Log.log("Ajánlat megérkezett.");

		click(".bell");
		clickLinkWithText("Alkatrész ajánlat");
		onScreen(companyName);
		Log.log("Értesítés user oldalon megérkezett (" + companyName + ").");
		String savedPrice = driver.findElement(By.className("price")).getText();

		double amount = Double.parseDouble(price);
		DecimalFormat formatter = new DecimalFormat("###,###,###");

		String formattedPrice = formatter.format(amount).replaceAll(",", "");

		if(savedPrice.equals(formattedPrice+" Ft")) {
			
			Log.log("Ár stimmel");
			
		}else {
		
		Log.log("Ár nem stimmel!!!!!!!!!!");
		Log.log("Itt szerepel: "+savedPrice);
		Log.log("Korábban megadott: "+formattedPrice+" Ft");


		}
	}
		

	public static void sendRequestFinalOrder() throws InterruptedException, IOException {
		
		click(".checkbox");
		click(".btn-lg");
		sleep(3000);
		
		try {
			
			driver.findElement(By.id("car-address-loc-zip-id")).click();
			
		}catch(ElementNotVisibleException e){
			
			driver.findElement(By.xpath("//span[@class='switch']")).click();
			sleep(3000);
			
		}
		
		
		fillName("car_address[loc_zip_id_ac]", "1052");
		sleep(1000);
		driver.findElement(By.id("car-address-loc-zip-id")).sendKeys(Keys.ENTER);
		fillName("car_address[street]","Sas");
		driver.findElement(By.id("car-address-street-type")).click();
		sleep(1000);
		driver.findElement(By.id("car-address-street-type")).sendKeys(Keys.ARROW_DOWN);
		driver.findElement(By.id("car-address-street-type")).sendKeys(Keys.ENTER);
		sleep(1000);
		fillName("car_address[street_num]", "25");
		fillName("car_address[building]", "a");
		fillName("car_address[floor]", "2");
		fillName("car_address[door]", "204");
		
		Log.log("Megrendelés");
		
		submit();
		sleep(4000);

	}

	public static void checkRequestFinalOrderTire(String price) throws IOException {
		click(".bell");
		clickLinkWithText("Gumi rendelés");
		onScreen(price);
		
		onScreen("1052 Budapest, Sas utca 25.");
		onScreen("test note");
		
		Log.log("Értesítés céges oldalon megérkezett.");
		clickLinkWithText("Teljesítve");
		Log.log("Archiválva");
	}
	public static void checkRequestFinalOrderPart(String price) throws IOException {
		
		click(".bell");
		clickLinkWithText("Alkatrész rendelés");
		int priceInt = Integer.parseInt(price);
		checkPrice(priceInt, " ");
		
		onScreen("1052 Budapest, Sas utca 25.");
		onScreen("hey!");
		onScreen("Fékcső");
		onScreen("test");
		
		Log.log("Értesítés céges oldalon megérkezett.");
		clickLinkWithText("Teljesítve");
		Log.log("Archiválva");
		
	}

	public static String GetCompanyName() throws IOException {
		goToPage(url + "/hu/car-companies/edit");
		return driver.findElement(By.id("name")).getAttribute("value");
	}

	public static boolean exists(String selector) throws IOException {
		if (driver.findElements(By.cssSelector(selector)).size() != 0) {
			return true;
		}

		return false;
	}

	public static String SendRequestPart() throws IOException, InterruptedException {
		
		clickLinkWithText("Ajánlatkérés");
		click(".sprite-technical");
		sleep(1000);
		clickXpath("//span[contains(text(),'Fékrendszer')]/following-sibling::i");
		sleep(1000);
		clickXpath("//span[contains(text(),'Fékcső')]/following-sibling::a");
		sleep(1000);
		driver.findElement(By.xpath("//span[contains(text(),'Fékcső')]/following-sibling::a")).click();
		sleep(1000);
		clickLinkWithText("Ajánlatkérés");

		fillName("loc_zip_id_ac", "10");
		sleep(2000);
		clickLinkWithText("1014");
		if (driver.findElement(By.cssSelector("input[name=\"vin\"]")).isDisplayed()) {
			fillName("vin", "12345678901234567");
		} else {
			Log.log("Alvázszám korábbról elmentve.");
		}
		if (driver.findElement(By.cssSelector("input[name=\"motor_number\"]")).isDisplayed()) {
			fillName("motor_number", "12345678901234567");
		} else {
			Log.log("Motorszám korábbról elmentve.");
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

	private static void clickLinkWithText(String string) {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
				"//a[not(contains(@class,'d-sm-none'))]/descendant-or-self::*[contains(text(),'" + string + "')]")));
		driver.findElement(By.xpath(
				"//a[not(contains(@class,'d-sm-none'))]/descendant-or-self::*[contains(text(),'" + string + "')]"))
				.click();
	}
	
	private static void clickXpath(String string) {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(string)));
		driver.findElement(By.xpath(string)).click();
	}

	public static void FillUserPersonalData() throws IOException, InterruptedException {
		click(".user-img");
		clickLinkWithText("Adatmódosítás");
		fillName("mobile", "12345678");
		fillName("user[last_name]", "Teszt");
		fillName("user[first_name]", "Eszter");
		fillName("mothers_name", "Kovács Hilda Géza");
		fillName("birthdate", "1970-12-12");
		fillName("personal_ident", "AE12345678");
		fillName("driving_licence_number", "fdsfdsAE12345678");
		clickLinkWithText("Módosítások mentése");
		Thread.sleep(5000);
		click(".logo");
		Log.log("Vissza a főoldalra.");
		clickLinkWithText("profil szerkesztése");
		checkField("mobile", "3636123456");
		checkField("user[last_name]", "Teszt");
		checkField("user[first_name]", "Eszter");
		checkField("mothers_name", "Kovács Hilda Géza");
		checkField("birthdate", "1970-12-12");
		checkField("personal_ident", "AE12345678");
		checkField("driving_licence_number", "fdsfdsAE12345678");

	}

	private static void checkField(String name, String expectedValue) throws IOException {
		String data = "";
		if (driver.findElements(By.cssSelector("input[name=\"" + name + "\"]")).size() != 0) {
			data = driver.findElement(By.cssSelector("input[name=\"" + name + "\"]")).getAttribute("value");
		}
		if (driver.findElements(By.cssSelector("select[name=\"" + name + "\"]")).size() != 0) {
			Select select = new Select(driver.findElement(By.cssSelector("select[name=\"" + name + "\"]")));
			data = select.getFirstSelectedOption().getText();
		}
		if (driver.findElements(By.cssSelector("textarea[name=\"" + name + "\"]")).size() != 0) {
			WebElement textarea = driver.findElement(By.cssSelector("textarea[name=\"" + name + "\"]"));
			data = textarea.getText();
		}
		try {
			assertEquals(data, expectedValue);
		} catch (Exception e) {
			System.out.println("Mező: " + name + " - nem az elvárt érték");
			Log.log("Mező: " + name + " - nem az elvárt érték");
			throw e;
		}

		Log.log("Mező: " + name + " - OK " + expectedValue);

	}

	private static void onScreen(String string) throws IOException {
		wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + string + "')]")));
		System.out.println(string);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(string));
		Log.log("Képernyőn: " + string);
	}
	
	private static void onScreenValue(String string) throws IOException {
		wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + string + "']")));
		System.out.println(string);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(string));
		Log.log("Képernyőn: " + string);
	}

	public static void test() {
		driver.get("https://testcenter.vr/selenium-test-surface.php");

	}

	public static void checkCarProperties() throws IOException {
		clickLinkWithText("Adatok szerkesztése");
		checkField("car_manufacturer_id", "BMW");
		checkField("car_model_id", "116");
		checkField("car_year", "2012");
		checkField("car_month", "március");
		checkField("numberplate", "ABC-123");
		checkField("petrol", "Dízel");
		checkField("km", "120000");
	}

	public static void selectCar(String string) throws IOException {

		String pattern = "//a[\"numberplate\" and contains(translate(.,'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), \""
				+ string + "\")]";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pattern)));
		WebElement myElement = driver.findElement(By.xpath(pattern));
		WebElement parent = myElement.findElement(By.xpath("../.."));
		parent.findElement(By.className("item-image")).click();
		Log.log("Autó kiválasztva: " + string);

	}

	public static void deleteCar(String numberPlate) throws IOException {
		selectCar(numberPlate);
		clickLinkWithText("Autó törlése");
		click(".deleteAttachedItem");
	}

	public static void deleteUserCars() throws IOException {
		List<WebElement> elements = driver.findElements(By.cssSelector(".numberplate"));
		List<String> list = new ArrayList<String>();
		for (WebElement element : elements) {
			String numberplate = element.getText();
			list.add(numberplate);
		}
		for (String oneItem : list) {
			Log.log(oneItem + " rendszámú autó törölve.");
			deleteCar(oneItem);
		}

	}

	public static void selectCarPartItem(String part, int depth) throws IOException {
		Log.log("Try to select:" + part);

		String pattern = "(//li/span[contains(text(),\"" + part + "\")]/parent::li//i)[1]";
		if (depth == 2) {
			pattern = "(//li[@class=\"active\"]/ul/li/span[contains(text(),\"" + part + "\")]/parent::li//i)[1]";
		}
		if (depth == 3) {
			pattern = "//li[@class=\"active\"]/ul/li[@class=\"active\"]/ul/li/span[contains(text(),\"" + part
					+ "\")]/parent::li//i";
		}

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pattern)));
		WebElement myElement = driver.findElement(By.xpath(pattern));
		WebElement parent = myElement.findElement(By.xpath(".."));

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("ul li i")));
		parent.findElement(By.tagName("i")).click();

		Log.log(part + " kiválasztva.");
	}

	public static void addNewCarEventBodyRepair() throws IOException, InterruptedException {
		clickLinkWithText("esemény hozzáadása");
		sleep(2000);

		List<WebElement> sprites = driver.findElements(By.cssSelector(".sprite-mycar_service_log-body"));
		for (WebElement sprite : sprites) {

			if (sprite.isDisplayed()) {
				sprite.click();

			}
		}

		driver.findElement(By.cssSelector("input[name=\"service_date\"]")).sendKeys();
		sleep(2000);
		goToPage(TestBase.url + "/hu/szerviz-esemeny-letrehozasa/4/" + getCarId());
		click(".ts-date-picker");
		click("h2");
		fillName("car_company_id_ac", "a");
		sleep(3000);
		click("ul#ui-id-1 li:nth-child(1)");
		sleep(2000);
		clickXpath("//div[contains(text(), \"Kiválasztás\")]");
		Log.log("Kiválasztás clicked");
		sleep(2000);
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
		Log.log("Elem kiválasztása: " + list.get(randomNumber));
		selectCarPartItem(list.get(randomNumber), 1);

		if (driver.findElements(By.cssSelector("ul.tree-browser li.active ul > li")).size() != 0) {

			wait.until(
					ExpectedConditions.visibilityOfElementLocated(By.cssSelector("ul.tree-browser li.active ul > li")));
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
				selectCarPartItem(list.get(0), 2);
				Log.log("Try to select:" + list.get(0));
				sleep(2000);
			} else {
				randomNumber = new Random().nextInt(size - 1) + 1;
				selectCarPartItem(list.get(randomNumber), 2);
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
			selectCarPartItem(list.get(randomNumber), 3);

		}
		sleep(1000);
		
		int randPrice = new Random().nextInt(123456);
		fillName("price_work", "" + randPrice);
		sleep(1000);
		fillName("car_mycar_service_log_items[0][price]", "20000");

		// Test third level accessibility
		clickXpath("//div[contains(text(), \"Kiválasztás\")]");
		Log.log("Kiválasztás clicked");
		clickXpath("//span[contains(text(), \"Szélvédő és egyéb üvegek\")]");
		clickXpath("//li[@class=\"active\"]/ul/li/span[contains(text(),\"Oldalsó ajtó üveg\")]");
		clickXpath(
				"//li[@class=\"active\"]/ul/li[@class=\"active\"]//li/span[contains(text(),\"Bal hátsó\")]/parent::li//i");
		sleep(3000);
		fillName("car_mycar_service_log_items[1][price]", "12435");

		int RN = new Random().nextInt(123456);
		String noteText = "Test note " + RN;
		driver.findElement(By.cssSelector("textarea[name=\"note\"]")).sendKeys(noteText);
		submit();
		clickLinkWithText("Karosszéria javítás");
		click("i.fa-trash");
		clickLinkWithText("Esemény törlése");
		sleep(3000);
		Log.log("A szerviz esemény sikeresen törölve.");

	}

	public static void addNewCarEventRecurringService() throws IOException, InterruptedException {
		goToPage(url + "/hu/szerviz-esemeny-letrehozasa/2/" + getCarId());
		fillName("service_interval_month", "48");
		fillName("service_interval_km", "20000");
		click(".ts-date-picker");
		driver.findElement(By.xpath("/html/body/header/div/div/div[1]/div")).click();

		List<WebElement> list = driver.findElements(By.className("changeMainPart"));
		int size = list.size();
		int randNumber = new Random().nextInt(size - 1) + 1;
		String partName = list.get(randNumber).getText();
		list.get(randNumber).click();

		Log.log(partName + " alkatrész kiválasztva.");
		int randPrice = new Random().nextInt(123456);
		sleep(5000);
		fillName("car_mycar_service_log_items[0][price]", "" + randPrice);
		fillName("car_mycar_service_log_items[0][item_description]", "part " + randNumber);
		String noteText = "Test note " + randNumber;
		fillName("note", noteText);
		submit();
		sleep(2000);
		
		driver.findElement(By.cssSelector("a[href*='szerviz-esemeny-megtekintese']")).click();
		
		onScreen(partName);
		onScreen(noteText);
		checkPrice(randPrice, " ");
		clickLinkWithText("Szerkesztés");
		onScreen(partName);
		onScreen(noteText);
		checkField("car_mycar_service_log_items[0][price]", randPrice + "");

		click(".removeOfferItem");

		list = driver.findElements(By.className("changeMainPart"));
		size = list.size();
		randNumber = new Random().nextInt(size - 1) + 1;
		partName = list.get(randNumber).getText();
		list.get(randNumber).click();
		Log.log(partName + " alkatrész kiválasztva.");
		randPrice = new Random().nextInt(123456);
		sleep(5000);
		fillName("car_mycar_service_log_items[1][price]", "" + randPrice);
		fillName("car_mycar_service_log_items[1][item_description]", "part " + randNumber);
		int randPrice2 = new Random().nextInt(123456);
		fillName("price_work", "" + randPrice2);
		submit();

		clickLinkWithText("Időszakos szerviz");
		onScreen(partName);
		onScreen(noteText);
		checkPrice(randPrice, " ");
		checkPrice(randPrice2, " ");

		click("i.fa-trash");
		clickLinkWithText("Esemény törlése");

		sleep(1000);
		assertTrue("Event deleted", !driver.getPageSource().contains(noteText));
		Log.log("Esemény: egyéb sikeresen törölve.");

	}

	public static void addNewCarEventOtherService() throws IOException, InterruptedException {
		goToPage(url + "/hu/egyeb-szerviz-esemeny-letrehozasa/" + getCarId());
		sleep(1000);
		int randNumber = new Random().nextInt(500) + 1;

		click(".ts-date-picker");
		driver.findElement(By.xpath("/html/body/header/div/div/div[1]/div")).click();

		driver.findElement(By.cssSelector(".btn.btn-primary.col-12")).click();
		sleep(800);
		driver.findElement(By.cssSelector(".tree-browser li")).click();
		driver.findElement(By.cssSelector(".tree-browser li a")).click();

		String partName = driver.findElement(By.id("car-mycar-service-log-items-0-text")).getAttribute("value");

		Log.log(partName + " alkatrész kiválasztva.");
		int randPrice = new Random().nextInt(123456);
		sleep(1000);
		fillName("car_mycar_service_log_items[0][price]", "" + randPrice);
		fillName("car_mycar_service_log_items[0][item_description]", "part " + randNumber);
		fillName("car_company_id_ac", "Test Kft.");
		int randWorkPrice = new Random().nextInt(123456);
		fillName("price_work", "" + randWorkPrice);
		String noteText = "Test note " + randNumber;
		fillName("note", noteText);
		submit();
		sleep(2000);
		Log.log("Sikeresen mentve");
		
		driver.findElement(By.cssSelector("a[href*='szerviz-esemeny-megtekintese']")).click();
		sleep(1000);
		onScreen(partName);
		onScreen(noteText);
		checkPrice(randPrice, " ");
		clickLinkWithText("Szerkesztés");
		Log.log("Módosítás");
		sleep(2000);
		String oldalonAlkatresz = driver.findElement(By.id("car-mycar-service-log-items-0-text")).getAttribute("value");
		if (oldalonAlkatresz.equals(partName)) {
			driver.findElement(By.xpath("//*[contains(text(), \"" + partName + "\")]"));
			System.out.println(partName);
			assertTrue("Szerepel a forrásban", driver.getPageSource().contains(partName));
			Log.log("Képernyőn: " + partName);
		} else {
			Log.log("Alkatrész hiba");
		}
		onScreen(noteText);
		checkField("car_mycar_service_log_items[0][price]", randPrice + "");
		
		randNumber = new Random().nextInt(500) + 1;
		randPrice = new Random().nextInt(123456);
		sleep(1000);
		fillName("car_mycar_service_log_items[0][price]", "" + randPrice);
		fillName("car_mycar_service_log_items[0][item_description]", "part " + randNumber);
		randWorkPrice = new Random().nextInt(123456);
		fillName("price_work", "" + randWorkPrice);
		noteText = "Test note " + randNumber;
		fillName("note", noteText);
		
		submit();
		sleep(2000);
		Log.log("Sikeres módosítás");
		
		Log.log("Újra ellenőrzés...");
		clickLinkWithText("Egyéb szerviz");
		onScreen(partName);
		onScreen(noteText);
		checkPrice(randPrice, " ");
		clickLinkWithText("Szerkesztés");
		Log.log("Módosítás");
		sleep(2000);
		oldalonAlkatresz = driver.findElement(By.id("car-mycar-service-log-items-0-text")).getAttribute("value");
		if (oldalonAlkatresz.equals(partName)) {
			driver.findElement(By.xpath("//*[contains(text(), \"" + partName + "\")]"));
			System.out.println(partName);
			assertTrue("Szerepel a forrásban", driver.getPageSource().contains(partName));
			Log.log("Képernyőn: " + partName);
		} else {
			Log.log("Alkatrész hiba");
		}
		onScreen(noteText);
		checkField("car_mycar_service_log_items[0][price]", randPrice + "");
		
		submit();
		sleep(2000);
		Log.log("Törlés...");

		driver.findElement(By.cssSelector(".fas.fa-trash.circle")).click();
		driver.findElement(By.cssSelector(".btn.btn-sm.h-100.d-flex.align-items-center.btn-secondary")).click();
		sleep(1000);
		assertTrue("Event deleted", !driver.getPageSource().contains(noteText));
		Log.log("Esemény: egyéb sikeresen törölve.");

	}
	
	public static void importCarSearch() throws IOException, InterruptedException, AWTException {
		
		
	driver.findElement(By.name("mf_ac")).click();
	
	manufacturer = fillCarField("#mf", "#ui-id-1");
		try {	
		model = fillCarField("#mu", "#ui-id-2");
		
		}catch(IllegalArgumentException e){
		
			driver.findElement(By.id("mf")).clear();
			manufacturer = fillCarField("#mf", "#ui-id-1");	
			model = fillCarField("#mu", "#ui-id-2");
		}
		
		driver.findElement(By.xpath("//div[@class='col-sm-6'][3]/div[@class='form-group  select  fg-line']/div[@class='multiple-select-holder  ']/span[@class='multiselect-native-select']/div[@class='btn-group']/button[@class='multiselect dropdown-toggle btn btn-default']")).click();
		sleep(2000);
		driver.findElement(By.xpath("//div[@class='btn-group show']/ul[@class='multiselect-container dropdown-menu show']/li[2]/a/label[@class='checkbox']")).click();
		driver.findElement(By.xpath("//div[@class='col-sm-6'][4]/div[@class='row']/div[@class='col-10 pl-1 petrolHolder']/div[@class='form-group  select  fg-line']/div[@class='multiple-select-holder  ']/span[@class='multiselect-native-select']/div[@class='btn-group']/button[@class='multiselect dropdown-toggle btn btn-default']")).click();
		driver.findElement(By.xpath("//ul[@class='multiselect-container dropdown-menu show']/li[2]/a/label[@class='checkbox']")).click();
		Select yearfrom = new Select(driver.findElement(By.name("yearfrom")));
		yearfrom.selectByVisibleText("2012");
		fillName("kmto","1200");
		fillName("priceto","12");
		sleep(2000);
		submit();
		
		
		
		
	}

	public static void addNewCarEventPenalty() throws IOException, InterruptedException {
		// goToPage(url+"/hu/birsag-esemeny-letrehozasa/" + getCarId());
		clickLinkWithText("esemény hozzáadása");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-penalty")));
		click(".sprite-penalty");

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
		sleep(1000);
		onScreen(penaltyType);
		checkPrice(price, " ");
		
		sleep(3000);
		driver.findElement(By.cssSelector("a[href*='birsag-esemeny-megtekintese']")).click();
		onScreen(penaltyType);
		onScreen("Nem");

		clickLinkWithText("Szerkesztés");
		checkField("penalty_type", penaltyType);
		checkField("price", "" + price);
		onScreen(noteText);
		driver.findElement(By.xpath("//*[contains(text(),'Fizetve')]")).click();
		
		price = 1000 + rand.nextInt((50000 - 1) + 1);
		fillName("price", "" + price);
		note = 1000 + rand.nextInt((50000 - 1) + 1);
		noteText = "Note " + note;
		fillName("note", noteText);
		
		submit();
		sleep(2000);
		
		clickLinkWithText(penaltyType);
		sleep(2000);
		onScreen(penaltyType);
		onScreen("Igen");
		
		clickLinkWithText("Szerkesztés");
		checkField("penalty_type", penaltyType);
		checkField("price", "" + price);
		onScreen(noteText);
		
		submit();
		sleep(2000);

		driver.findElement(By.cssSelector(".fas.fa-trash.circle")).click();
		sleep(1000);
		clickLinkWithText("Igen");

		sleep(5000);
		assertTrue("Event deleted", !driver.getPageSource().contains(penaltyType));
		Log.log("Esemény: Bírság sikeresen törölve.");

	}

	public static void addNewCarEventHighwayFee() throws IOException, InterruptedException {
		// goToPage(url+"/hu/autopalya-matrica-hozzadasa/" + getCarId());
		clickLinkWithText("esemény hozzáadása");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-mycar_highway_ticket")));
		click(".sprite-mycar_highway_ticket");

		driver.findElement(By.cssSelector("input[name=\"start_date\"]")).click();
		driver.findElement(By.id("start-date")).sendKeys(Keys.ENTER);
		List<WebElement> list = driver.findElements(By.cssSelector("input[type=\"radio\"]:not([id=\"ticket1\"])"));
		int size = list.size();
		int randNumber = new Random().nextInt(size - 1) + 1;
		String id = list.get(randNumber).getAttribute("id");
		// list.get(randNumber).click();
		driver.findElement(By.xpath("//label[@for='" + id + "']")).click();
		String name = driver.findElement(By.cssSelector("label[for=\"" + id + "\"] .ticket-name")).getText();
		String expiration = driver.findElement(By.cssSelector("label[for=\"" + id + "\"] .ticket-expiration"))
				.getText();
		String price = driver.findElement(By.cssSelector("label[for=\"" + id + "\"] .ticket-price")).getText();

		Log.log(name + " autópálya matrica kiválasztva.");
		Log.log(expiration + " lejárattal.");
		Log.log(price + " áron.");
		submit();

		sleep(4000);

		String pattern = "//dt[contains(text(),' Autópálya-matrica érvényessége')]//following-sibling::dd[1]";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pattern)));
		WebElement insuranceParent = driver.findElement(By.xpath(pattern));
		String insurance = insuranceParent.findElement(By.tagName("a")).getText();

		// assertTrue("Autópályamatrica lejárat OK.", insurance.contains(expiration));
		// Log.log("Autópályamatrica lejárat OK.");

		Log.log(expiration);
		Log.log(name);
		//assertTrue("Autópályamatrica adatok OK.", insurance.contains(expiration));
		assertTrue("Autópályamatrica adatok OK.", insurance.contains(name));
		Log.log("Autópályamatrica adatok OK.");

		onScreen("Új autópálya matrica");
		clickLinkWithText("Új autópálya matrica");
		sleep(1000);
		onScreen(expiration);
		onScreen(name);
		onScreen(price);

		clickLinkWithText("Szerkesztés");

		list = driver.findElements(By.cssSelector("input[type=\"radio\"]:not([id=\"ticket1\"])"));
		size = list.size();
		randNumber = new Random().nextInt(size - 1) + 1;
		id = list.get(randNumber).getAttribute("id");
		// list.get(randNumber).click();
		driver.findElement(By.xpath("//label[@for='" + id + "']")).click();
		name = driver.findElement(By.cssSelector("label[for=\"" + id + "\"] .ticket-name")).getText();
		expiration = driver.findElement(By.cssSelector("label[for=\"" + id + "\"] .ticket-expiration")).getText();
		price = driver.findElement(By.cssSelector("label[for=\"" + id + "\"] .ticket-price")).getText();

		Log.log(name + " autópálya matrica kiválasztva.");
		Log.log(expiration + " lejárattal.");
		Log.log(price + " áron.");
		
		submit();
		sleep(1000);
		driver.findElement(By.cssSelector(".fas.fa-long-arrow-alt-left")).click();
		sleep(1000);

		pattern = "//dt[contains(text(),' Autópálya-matrica érvényessége')]//following-sibling::dd[1]";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pattern)));
		insuranceParent = driver.findElement(By.xpath(pattern));
		insurance = insuranceParent.findElement(By.tagName("a")).getText();

		Log.log(expiration);
		Log.log(name);
		
		assertTrue("Autópályamatrica adatok OK.", insurance.contains(name));
		Log.log("Autópályamatrica adatok OK.");

		onScreen("Új autópálya matrica");
		clickLinkWithText("Új autópálya matrica");
		sleep(1000);
		onScreen(expiration);
		onScreen(name);
		onScreen(price);
		
		driver.findElement(By.cssSelector(".fas.fa-trash.circle")).click();
		sleep(2000);
		driver.findElement(By.cssSelector(".btn.grayBtn.deleteAttachedItem")).click();
		
		Log.log("Autópályamatrica sikeresen törölve");

	}

	private static void submit() {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@type='submit']"))).click();
	}

	public static void addNewCarEventCompulsoryInsurance() throws IOException, InterruptedException {
		// goToPage(url+"/hu/biztositas-hozzadasa/" + getCarId() + "/1");
		clickLinkWithText("esemény hozzáadása");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-mycar_insurance")));
		clickLinkWithText("Kötelező");

		String company = randomSelect("company");
		driver.findElement(By.cssSelector("input[name=\"start_date\"]")).click();
		String period = randomSelect("period");

		int randNumber = new Random().nextInt(123456);
		String ident = "" + randNumber;
		fillName("ident", ident);

		int price = new Random().nextInt(123456);
		String stringPrice = "" + price;
		fillName("price", stringPrice);
		submit();

		String pattern = "//dt[contains(text(),'Kötelező gépjármű biztosítás')]//following-sibling::dd[1]";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pattern)));
		WebElement insuranceParent = driver.findElement(By.xpath(pattern));
		String insurance = insuranceParent.findElement(By.tagName("a")).getText();
		LocalDate dueDate = LocalDate.now().plusYears(1);

		assertTrue("CASCO biztosítás listázva.", insurance.contains(dateLocale(LocalDate.now())));
		if (insurance.contains(dateLocale(LocalDate.now()))) {
			Log.log("CASCO biztosítás listázva.");
		}

		assertTrue("CASCO biztosítás lejárat OK.", insurance.contains("Lejár: " + dateLocale(dueDate)));
		Log.log("CASCO biztosítás lejárat OK.");

		clickLinkWithText("biztosítás");
		onScreen(company);
		onScreen(period);
		onScreen(ident);

		checkPrice(price, " ");

		clickLinkWithText("Szerkesztés");
		checkSelect("type", "Kötelező gépjármû biztosítás");
		checkSelect("company", company);

		checkField("ident", ident);
		checkField("price", "" + price);
		checkField("period", period);

		submit();

		click("i.fa-trash");
		click("a[data-apply=\"confirmation\"]");

		sleep(8000);
		assertTrue("Event deleted", !driver.getPageSource().contains("CASCO biztosítás"));
		Log.log("Esemény: Kötelező gépjármû biztosítás sikeresen törölve.");
	}

	public static void addNewCarEventCascoInsurance() throws IOException, InterruptedException {
		// goToPage(url+"/hu/biztositas-hozzadasa/" + getCarId() + "/2");
		clickLinkWithText("esemény hozzáadása");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-mycar_insurance")));
		clickLinkWithText("Casco");

		String company = randomSelect("company");
		driver.findElement(By.cssSelector("input[name=\"start_date\"]")).click();
		String period = randomSelect("period");

		int randNumber = new Random().nextInt(123456);
		String ident = "" + randNumber;
		fillName("ident", ident);

		int price = new Random().nextInt(123456);
		String stringPrice = "" + price;
		fillName("price", stringPrice);
		submit();

		String pattern = "//dt[contains(text(),'CASCO biztosítás')]//following-sibling::dd[1]";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pattern)));
		WebElement insuranceParent = driver.findElement(By.xpath(pattern));
		String insurance = insuranceParent.findElement(By.tagName("a")).getText();
		LocalDate dueDate = LocalDate.now().plusYears(1);

		assertTrue("CASCO biztosítás listázva.", insurance.contains(dateLocale(LocalDate.now())));
		if (insurance.contains(dateLocale(LocalDate.now()))) {
			Log.log("CASCO biztosítás listázva.");
		}

		assertTrue("CASCO biztosítás lejárat OK.", insurance.contains("Lejár: " + dateLocale(dueDate)));
		Log.log("CASCO biztosítás lejárat OK.");
		
		sleep(3000);
		clickLinkWithText("biztosítás");
		onScreen(company);
		onScreen(period);
		onScreen(ident);

		checkPrice(price, " ");

		clickLinkWithText("Szerkesztés");
		checkSelect("type", "CASCO biztosítás");
		checkSelect("company", company);

		checkField("ident", ident);
		checkField("price", "" + price);
		checkField("period", period);
	

		submit();
		sleep(3000);
		driver.findElement(By.cssSelector(".fas.fa-trash.circle")).click();
		click("a[data-apply=\"confirmation\"]");

		sleep(8000);
		assertTrue("Event deleted", !driver.getPageSource().contains("CASCO biztosítás"));
		Log.log("Esemény: CASCO sikeresen törölve.");

	}

	public static void checkPrice(int num, String delimiter) throws IOException {
		String pattern = "###,###";
		DecimalFormat format = new DecimalFormat(pattern);
		String stringPrice = format.format(num);
		String commaStringPrice = stringPrice.replaceAll("[^0-9]", delimiter);
		String[] parts = commaStringPrice.split(delimiter);
		wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//*[contains(text(), '" + parts[0] + "') and contains(text(), '" + parts[1] + "')]")));
		Log.log("Képernyőn: " + parts[0] + " " + parts[1]);
	}

	public static void addNewCarEventGapInsurance() throws IOException, InterruptedException {

		clickLinkWithText("esemény hozzáadása");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-mycar_insurance")));
		clickLinkWithText("GAP");

		String company = randomSelect("company");
		driver.findElement(By.cssSelector("input[name=\"start_date\"]")).click();
		// String period = randomSelect("period");

		int randNumber = new Random().nextInt(123456);
		String ident = "" + randNumber;
		fillName("ident", ident);

		int price = new Random().nextInt(123456);
		String stringPrice = "" + price;
		fillName("price", stringPrice);
		submit();

		String pattern = "//dt[contains(text(),'GAP biztosítás')]//following-sibling::dd[1]";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pattern)));
		WebElement insuranceParent = driver.findElement(By.xpath(pattern));
		String insurance = insuranceParent.findElement(By.tagName("a")).getText();
		LocalDate dueDate = LocalDate.now().plusYears(1);

		onScreen("Új GAP biztosítás");

		Log.log("Elvárt lejárati dátum: " + dateLocale(dueDate));
		assertTrue("GAP biztosítás lejárat OK.", insurance.contains("Lejár: " + dateLocale(dueDate)));
		Log.log("GAP biztosítás lejárat OK.");

		clickLinkWithText("biztosítás");
		onScreen(company);
		// onScreen(period);
		onScreen(ident);

		checkPrice(price, " ");

		clickLinkWithText("Szerkesztés");
		checkSelect("type", "GAP biztosítás");
		checkSelect("company", company);

		checkField("ident", ident);
		checkField("price", "" + price);
		// checkField("period", period);
		
		randNumber = new Random().nextInt(123456);
		ident = "" + randNumber;
		fillName("ident", ident);

		price = new Random().nextInt(123456);
		stringPrice = "" + price;
		fillName("price", stringPrice);

		submit();
		sleep(2000);
			
		clickLinkWithText("biztosítás");
		onScreen(company);
		// onScreen(period);
		onScreen(ident);

		checkPrice(price, " ");

		clickLinkWithText("Szerkesztés");
		sleep(20000);
		checkSelect("type", "GAP biztosítás");
		checkSelect("company", company);

		checkField("ident", ident);
		checkField("price", "" + price);

		submit();
		sleep(2000);
		
		driver.findElement(By.cssSelector(".fas.fa-trash.circle")).click();
		click("a[data-apply=\"confirmation\"]");

		sleep(3000);
		assertTrue("Event deleted", !driver.getPageSource().contains("GAP biztosítás"));
		Log.log("Esemény: GAP biztosítás sikeresen törölve.");
	}

	private static void sleep(int i) throws InterruptedException {
		System.out.println("\t"+"\t"+"wait " + i + " millisconds");
		Thread.sleep(i);

	}

	public static String getCarId() {
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

		month = month.replace("January", "január");
		month = month.replace("February", "február");
		month = month.replace("March", "március");
		month = month.replace("April", "április");
		month = month.replace("May", "május");
		month = month.replace("June", "június");
		month = month.replace("July", "július");
		month = month.replace("August", "augusztus");
		month = month.replace("September", "szeptember");
		month = month.replace("October", "október");
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
		
		String carURL = driver.getCurrentUrl();
		
		driver.findElement(By.xpath("//*[contains(text(), 'Bérlésre kínálom')]")).click();
		
		String carName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".col-12.col-md-8.col-lg-9.text-right"))).getText();
		click(".switch"); 
		Random rand = new Random();
		Integer randomNum1 = rand.nextInt(10000) + 100000;
		fillName("rent_price_1", ""+randomNum1);
		Integer randomNum2 = rand.nextInt(10000) + 80000;
		fillName("rent_price_2", ""+randomNum2);
		Integer randomNum3 =rand.nextInt(10000)+ 60000;
		fillName("rent_price_3", ""+randomNum3);
		Random rand2 = new Random();
		Integer randomNum4 =rand2.nextInt(10000)+ 40000;
		fillName("rent_price_4", ""+randomNum4);
		Integer randomNum5 =rand.nextInt(10000) + 20000;
		fillName("rent_bail", ""+randomNum5);
		sleep(1000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/main/section[3]/div/div[2]/form/div[3]/div[2]/div[2]/div/label"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("rent-penalty-days"))).click();
		int randomday = rand.nextInt(10);
		fillName("rent_penalty_days", ""+randomday);
		int randompenalty = rand.nextInt(90)+10;
		fillName("rent_penalty_percentage", ""+randompenalty);
		fillName("fuel_combined","8.5");
		int randomzip = rand.nextInt(89) + 10;
		
		
		while(13<= randomzip && randomzip <=19) {randomzip = rand.nextInt(89) + 10;}
		
		fillName("loc_zip_id_ac",""+randomzip);
		
		try {
			
			driver.findElement(By.cssSelector(".ui-menu-item")).click();
		
		}catch(NoSuchElementException e){
			
			randomzip=rand.nextInt(89)+10;
			while(13<= randomzip && randomzip <=19) {randomzip = rand.nextInt(89) + 10;}
			fillName("loc_zip_id_ac",""+randomzip);
			click(".ui-menu-item");
			
		}
		
		sleep(2000);
		driver.findElement(By.id("rent-description")).clear();
		driver.findElement(By.id("rent-description")).sendKeys(getRandomText(50));
		submit();
		sleep(5000);
		
 		sleep(500);
 		String zipValue = driver.findElement(By.id("loc-zip-id")).getAttribute("value");
 		Log.log(zipValue);
 		sleep(500);
 		
		Log.log("Autó bérlésre sikeresen meghirdetve");
		
		
		goToPage(carURL);
		sleep(2000);
		
		checkPrice(randomNum4, " ");
		Log.log("Autó adatlapon ár alapján ellenőrizve");
		
		clickLinkWithText("Bérautóként meghirdetve");
		
		
		
		checkPrice(randomNum4, " ");
		String RentURLfromtl = driver.getCurrentUrl();
		Log.log("Idővonalról ellenőrizve a bérlap");
		

		driver.findElement(By.className("logos")).click();
		sleep(3000);
		try {
			
		driver.findElement(By.xpath("//*[@id='myrentcar-block']//div[@class='overflow-hidden']/a")).click();
		
		}catch(NoSuchElementException e) {
			
			Log.log("Főoldalon nem jelent meg a hirdetés a saját autók között!");
			driver.close();
			System.exit(0);
		
		}
		

		driver.findElement(By.cssSelector(".fas.fa-eye")).click();
		
		checkPrice(randomNum4, " ");
		String rentURLfromcdp = driver.getCurrentUrl();
		Log.log("Autó adatlapról ellenőrizve a bérlap");
		
		 
		sleep(2000);
		
		
		clickLinkWithText("Bérautó hirdetések");
		Log.log("Bérelhető Autók megjelenítése");
		fillName("rent_price_to",""+randomNum1);
		fillName("loc_zip_id_ac",""+randomzip);
	    click(".ui-menu-item");
		sleep(2000);
		Log.log("Irsz megadása");
		sleep(2000);
		driver.findElement(By.id("form-button")).click();
		Log.log("Keresés Indítása");
		driver.findElement(By.cssSelector(".col.btn.btn-secondary")).click();
	    sleep(4000);
	    checkPrice(randomNum1, " ");
		checkPrice(randomNum2, " ");
		checkPrice(randomNum3, " ");
		checkPrice(randomNum4, " ");
	    onScreen(""+randompenalty);
	    onScreen("8.5");
	    String rentURLfromrp = driver.getCurrentUrl();
	    Log.log("Az autó szerepel a Bérautó listában");
	    
	    
		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
		Date systemDate = new Date();
		
		Calendar c  = Calendar.getInstance();
		c.setTime(systemDate);
		c.add(Calendar.DATE, 1);
		Date currendDatePlusOne = c.getTime();
		
		Calendar c2  = Calendar.getInstance();
		c2.setTime(systemDate);
		c2.add(Calendar.DATE, 8);
		Date currentDatePlusEight = c2.getTime();
		
		String startDate = formatDate.format(currendDatePlusOne);
		String endDate = formatDate.format(currentDatePlusEight);
		
		sleep(3000);
		
		fillName("start_date",startDate);
		sleep(1000);

		driver.findElement(By.name("end_date")).click();
		driver.findElement(By.name("end_date")).clear();
		fillName("end_date",endDate);
		sleep(1000);
		driver.findElement(By.xpath("//h1")).click();
		sleep(1000);
		driver.findElement(By.id("form-button")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pickup-location"))).click();
		Random rand0 = new Random();
		Integer randomNum9 = rand0.nextInt(1) + 1;
		Select pick = new Select(driver.findElement(By.id("pickup-location")));
		pick.selectByIndex(randomNum9);
		Log.log("bérlési Időszak megadása");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dropdown-location"))).click();
		Select drop = new Select(driver.findElement(By.name("dropdown_location")));
		sleep(500);
		randomNum9 = rand.nextInt(1) + 1;
		drop.selectByIndex(randomNum9);

		
		driver.findElement(By.id("notes")).sendKeys(getRandomText(50));
        
		try {
			
			driver.findElement(By.xpath("//*[contains(text(),'Vezetéknév')]"));
			
		}catch(NoSuchElementException e){
			
			driver.findElement(By.xpath("(//span[@class='switch'])[2]")).click();
			sleep(3000);
			
		}
		
		try {
			
			fillName("car_address[loc_zip_id_ac]",""+randomzip);
			click(".ui-menu-item");
		
		}catch(NoSuchElementException e){
			
			randomzip=rand.nextInt(89)+10;
			fillName("car_address[loc_zip_id_ac]",""+randomzip);
			click(".ui-menu-item");
			
		}
		
         driver.findElement(By.name("car_address[street]")).sendKeys("Teszt");
         driver.findElement(By.name("car_address[street_type]")).click();
         wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("car_address[street_type]"))).click();
 		 Select st = new Select(driver.findElement(By.name("car_address[street_type]")));
 		 sleep(1000);
 		 randomNum9 = rand.nextInt(1) + 1;
 		 st.selectByIndex(randomNum9);
         fillName("car_address[street_num]","11");
         fillName("car_address[building]","A");
         fillName("car_address[floor]","1");
         fillName("car_address[door]","1");
         driver.findElement(By.cssSelector(".mb-3.col.btn.btn-primary")).click(); 
         Log.log("Bérlés kérelem kitöltve");
         sleep(2000);
         goToPage(carURL);
         
         wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".fas.fa-pencil-alt"))).click();
         sleep(2000);
     
    	  
    	 sleep(5000); 
    	 
    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + randomNum1 + "']")));
		System.out.println(randomNum1);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(""+randomNum1));
		Log.log("Képernyőn: " + randomNum1);
      
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + randomNum2 + "']")));
		System.out.println(randomNum2);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(""+randomNum2));
		Log.log("Képernyőn: " + randomNum2);
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + randomNum3 + "']")));
		System.out.println(randomNum3);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(""+randomNum3));
		Log.log("Képernyőn: " + randomNum3);
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + randomNum4 + "']")));
		System.out.println(randomNum4);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(""+randomNum4));
		Log.log("Képernyőn: " + randomNum4);
		
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + randomNum5 + "']")));
		System.out.println(randomNum5);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(""+randomNum5));
		Log.log("Képernyőn: " + randomNum5);
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + zipValue + "']")));
		System.out.println(zipValue);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(""+zipValue));
		Log.log("Képernyőn: " + zipValue);
		
		
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + 8.5 + "']")));
		System.out.println(8.5);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(""+8.5));
		Log.log("Képernyőn: " + 8.5);
	
		

		 goToPage(TestBase.url+"/hu/foglalasaim");
		 click("i.fa-eye");
         onScreen("8 nap");
  		
         
 
 		
		
       
        Log.log("Adatok leellenőrízve");
      
   
//1--------
                  
         
         
//2------
        Log.log("Feltőltás Új Adatokkal");
     
        goToPage(carURL);
 	    driver.findElement(By.cssSelector(".fas.fa-pencil-alt")).click();
 		randomNum1 = rand.nextInt(10000) + 100000;
 		fillName("rent_price_1", ""+randomNum1);
 	    randomNum2 = rand.nextInt(10000) + 80000;
 		fillName("rent_price_2", ""+randomNum2);
 		randomNum3 =rand.nextInt(10000)+ 60000;
 		fillName("rent_price_3", ""+randomNum3);
 	    rand2 = new Random();
 	    randomNum4 =rand2.nextInt(10000)+ 40000;
 		fillName("rent_price_4", ""+randomNum4);
 	    randomNum5 =rand.nextInt(10000) + 20000;
 		fillName("rent_bail", ""+randomNum5);
 		sleep(1000);
 		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/main/section[3]/div/div[2]/form/div[3]/div[2]/div[2]/div/label"))).click();
 		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/main/section[3]/div/div[2]/form/div[3]/div[2]/div[1]/div/label"))).click();
 		fillName("fuel_combined","8.5");
 		randomzip = rand.nextInt(89) + 10;
 		
 		
 		while(13<= randomzip && randomzip <=19) {randomzip = rand.nextInt(89) + 10;}
 		
 		fillName("loc_zip_id_ac",""+randomzip);
 		
 		try {
 			
 			driver.findElement(By.cssSelector(".ui-menu-item")).click();
 		
 		}catch(NoSuchElementException e){
 			
 			randomzip=rand.nextInt(89)+10;
 			while(13<= randomzip && randomzip <=19) {randomzip = rand.nextInt(89) + 10;}
 			fillName("loc_zip_id_ac",""+randomzip);
 			click(".ui-menu-item");
 			
 		}
 		
 		sleep(2000);
 		driver.findElement(By.id("rent-description")).clear();
 		driver.findElement(By.id("rent-description")).sendKeys(getRandomText(50));
 		submit();
 		sleep(5000);
 		
  		sleep(500);
  		zipValue = driver.findElement(By.id("loc-zip-id")).getAttribute("value");
  		Log.log(zipValue);
  		sleep(500);
  		
 		Log.log("Autó bérlésre sikeresen meghirdetve");
 		
 		
 		goToPage(carURL);
 		sleep(2000);
 		
 		checkPrice(randomNum4, " ");
 		Log.log("Autó adatlapon ár alapján ellenőrizve");
 		
 		clickLinkWithText("Bérautóként meghirdetve");
 		
 		
 		
 		checkPrice(randomNum4, " ");
 		RentURLfromtl = driver.getCurrentUrl();
 		Log.log("Idővonalról ellenőrizve a bérlap");
 		

 		driver.findElement(By.className("logos")).click();
 		sleep(3000);
 		try {
 			
 		driver.findElement(By.xpath("//*[@id='myrentcar-block']//div[@class='overflow-hidden']/a")).click();
 		
 		}catch(NoSuchElementException e) {
 			
 			Log.log("Főoldalon nem jelent meg a hirdetés a saját autók között!");
 			driver.close();
 			System.exit(0);
 		
 		}
 		

 		driver.findElement(By.cssSelector(".fas.fa-eye")).click();
 		
 		checkPrice(randomNum4, " ");
 		rentURLfromcdp = driver.getCurrentUrl();
 		Log.log("Autó adatlapról ellenőrizve a bérlap");
 		
 		 
 		sleep(2000);
 		
 		
 		clickLinkWithText("Bérautó hirdetések");
 		Log.log("Bérelhető Autók megjelenítése");
 		fillName("rent_price_to",""+randomNum1);
 		fillName("loc_zip_id_ac",""+randomzip);
 	    click(".ui-menu-item");
 		sleep(2000);
 		Log.log("Irsz megadása");
 		sleep(2000);
 		driver.findElement(By.id("form-button")).click();
 		Log.log("Keresés Indítása");
 		driver.findElement(By.cssSelector(".col.btn.btn-secondary")).click();
 	    sleep(4000);
 	    checkPrice(randomNum1, " ");
 		checkPrice(randomNum2, " ");
 		checkPrice(randomNum3, " ");
 		checkPrice(randomNum4, " ");
 	    onScreen("8.5");
 	    rentURLfromrp = driver.getCurrentUrl();
 	    Log.log("Az autó szerepel a Bérautó listában");
 	    
        
        sleep(2000);
        goToPage(carURL);
          
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".fas.fa-pencil-alt"))).click();
        sleep(2000);
        
     	  
     	sleep(5000); 
     	 
     	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + randomNum1 + "']")));
 		System.out.println(randomNum1);
 		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(""+randomNum1));
 		Log.log("Képernyőn: " + randomNum1);
       
 		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + randomNum2 + "']")));
 		System.out.println(randomNum2);
 		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(""+randomNum2));
 		Log.log("Képernyőn: " + randomNum2);
 		
 		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + randomNum3 + "']")));
 		System.out.println(randomNum3);
 		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(""+randomNum3));
 		Log.log("Képernyőn: " + randomNum3);
 		
 		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + randomNum4 + "']")));
 		System.out.println(randomNum4);
 		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(""+randomNum4));
 		Log.log("Képernyőn: " + randomNum4);
 		
 		
 		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + randomNum5 + "']")));
 		System.out.println(randomNum5);
 		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(""+randomNum5));
 		Log.log("Képernyőn: " + randomNum5);
 		
 		
 		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + zipValue + "']")));
		System.out.println(zipValue);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(""+zipValue));
		Log.log("Képernyőn: " + zipValue);
		
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + 8.5 + "']")));
		System.out.println(8.5);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(""+8.5));
		Log.log("Képernyőn: " + 8.5);
          
        Log.log("Adatok leellenőrízve");
 	    
  		 sleep(1000);
  		 clickLinkWithText("Garázs");
         
         Log.log("Visszalépés a Garázsba");
         
         goToPage(TestBase.url+"/hu/foglalasaim");
         
      
  		 driver.findElement(By.cssSelector(".fas.fa-long-arrow-alt-left")).click();
  		 goToPage(TestBase.url+"/hu/foglalasaim");
         click("i.fa-trash");
         click("a[data-apply=\"confirmation\"]");
         Log.log("Foglalás Sikeresen Törölve");
         Log.log("Ugrás az autóbérlés modosításhoz");
         goToPage(carURL);
         sleep(3000);
         driver.findElement(By.cssSelector(".fas.fa-pencil-alt")).click();
         wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/main/section[3]/div/div[2]/form/div[3]/div[2]/div[1]/div/label"))).click();
         click(".switch");
         driver.findElement(By.id("form-button")).click();
         sleep(2000);
         Log.log("Hirdetés levéve");
         Log.log("Sikeres Autóbérlés Teszt!");


	}

	public static String getRandomText(int i) {
		Random rand = new Random();
		Integer randomNum = rand.nextInt(10);

		LoremIpsum ipsum = new LoremIpsum();
		return ipsum.getWords(i, randomNum);
	}

	public static void buildCompanyPage() throws IOException, InterruptedException {
      
		goToPage(url+"/hu/ceg-oldal-szerkesztes");
		
		
		if (driver.findElements(By.xpath("//a/descendant-or-self::*[contains(text(),\"kattintson ide cégoldala létrehozásához\")]")).size() != 0) {
		  clickLinkWithText("kattintson ide cégoldala létrehozásához");
		}
		
		clickLinkWithText("Fejléc szerkesztése");
		Log.log("Fejléc szerkesztése"); 	
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name=\"logo_text\"]")));
		int rand = new Random().nextInt(500)+500;
		String logoText = "Logo TExt "+rand;
		fillName("logo_text",logoText);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name=\"logo_slogan\"]")));
		rand = new Random().nextInt(500)+600;
		String logoSlogen = "Logo Slogan "+rand;
		fillName("logo_slogan",logoSlogen);
		sleep(2000);  
		driver.findElement(By.cssSelector(".btn.btn-primary.submitBtn.tsLoadingIcon")).click();
		Log.log("Fejléc mentése"); 
		sleep(2000);
		onScreen(logoText);
		onScreen(logoSlogen);
		
		clickLinkWithText("Menü szerkesztése");
		Log.log("Menü szerkesztése");
		sleep(5000);
		int menupontok = driver.findElements(By.xpath("//*[contains(text(), \"Menüpont neve\")]")).size();
		System.out.println(menupontok);
		if (menupontok==0) {
			click("#car-company-page-menus-add");
			sleep(1000);
			click("#car-company-page-menus-add");  
			sleep(1000);
			click("#car-company-page-menus-add");
			sleep(1000);
			Log.log("3 elem hozzáadása"); 
			
			rand = new Random().nextInt(10);
			
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name=\"car_company_page_menus[2][title]\"]")));
			 String firstMenu = "Elso menu"+rand;
			fillName("car_company_page_menus[0][title]", firstMenu);
			randomSelect("car_company_page_menus[0][menu_modul]");
			
			 String secondMenu = "Masodik menu"+rand;
			fillName("car_company_page_menus[1][title]", secondMenu);
			randomSelect("car_company_page_menus[1][menu_modul]");
			
			String thirdMenu = "Harmadik menu"+rand;
			fillName("car_company_page_menus[2][title]", thirdMenu);
			randomSelect("car_company_page_menus[2][menu_modul]");
			Log.log("3 elem részletezés"); 

			driver.findElement(By.cssSelector(".btn.btn-primary.submitBtn.tsLoadingIcon")).click();
			Log.log("Menü mentése");  
		    onScreen(firstMenu);
		    onScreen(secondMenu);
		    onScreen(thirdMenu);
			
			
			}else {
				
				Log.log("Már ki van töltve"); 
				sleep(2000);
				driver.findElement(By.xpath("html/body/div/div/div/div/form/div/div/div/div")).click();
				
			}

		
		
		sleep(2000);
		driver.findElement(By.xpath("/html/body/main/div/div/div[3]/h4/a")).click();
		Log.log("Bemutatkozás szerkesztése"); 
		sleep(2000);
		rand = new Random().nextInt(30);
		String aboutUstitle = "AboutUs"+rand;
		fillName("about_us_title",aboutUstitle);
		String aboutUs = "AboutUs"+rand;
		fillName("about_us",aboutUs);
		driver.findElement(By.cssSelector(".btn.btn-primary.submitBtn.tsLoadingIcon")).click();
		Log.log("Bemutatkozás mentve");
		sleep(2000);
		
		clickLinkWithText("új oldal hozzáadása");
		rand = new Random().nextInt(30);
		String Title = "Title Example"+rand;
		sleep(2000);
		//wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div/div/form//div/div/div/div/div/input"))).sendKeys(Title);
		fillName("title", Title);
		String ContentIfr = "Ez egy Példa tartalom :)"+rand;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("content_ifr"))).sendKeys(ContentIfr);
		driver.findElement(By.cssSelector(".btn.btn-primary.submitBtn.tsLoadingIcon")).click();	
		sleep(3000);
		goToPage(url+"/hu/ceg-oldal-szerkesztes");
		sleep(3000);
		Log.log("Tartalom mentve");
		
		clickLinkWithText("Munkatársak kezelése");
		sleep(1000);
		clickLinkWithText("Új munkatárs");
		sleep(2000);
		fillName("name", "Józsi");
		fillName("titulus", "R1");
		Select orszag = new Select(driver.findElement(By.name("phone_country")));
		orszag.selectByVisibleText("Magyarország");
		fillName("phone","701234567");
		fillName("email", "kovacs@jozsef.hu");
		driver.findElement(By.xpath("html/body/div/div/div/div/form/div/button")).click();
		sleep(3000);
		Log.log("munkatárs mentve");
		onScreen("Józsi");
		onScreen("R1");
		onScreen("kovacs@jozsef.hu");

		driver.findElement(By.cssSelector(".fas.fa-edit.circle")).click();
		sleep(2000);
		driver.findElement(By.name("name")).clear();
		fillName("name", "Béla");
		driver.findElement(By.name("titulus")).clear();
		fillName("titulus", "R2");
		driver.findElement(By.name("phone")).clear();
		orszag = new Select(driver.findElement(By.name("phone_country")));
		orszag.selectByVisibleText("Románia");
		fillName("phone","709876543");
		fillName("email", "kovacs@bela.hu");
		
		driver.findElement(By.xpath("html/body/div/div/div/div/form/div/button")).click();
		
		clickCss(".fas.fa-long-arrow-alt-left");
		
		onScreen(logoText);
		onScreen(logoSlogen);
		onScreen(aboutUs);
		onScreen(aboutUstitle);
		onScreen(Title);
		onScreen("Béla");
		onScreen("R2");
		onScreen("kovacs@bela.hu");
	

	}


	
	
	
	
	public static void CarReviews() throws IOException, InterruptedException {
		
		
		Random rand = new Random();
		Integer randomNum = rand.nextInt(10) + 2;

		driver.switchTo().defaultContent();
		driver.switchTo().frame(driver.findElement(By.tagName("iframe")));
		Select year = new Select(driver.findElement(By.id("MainContent_control_RegistrationYear")));
		year.selectByIndex(randomNum);
		Log.log("Év megadása");
		sleep(500);

		Select month = new Select(driver.findElement(By.id("MainContent_control_RegistrationMonth")));
		sleep(500);
		month.selectByIndex(randomNum);
		Log.log("Hónap megadása");

		driver.switchTo().defaultContent();
		driver.switchTo().frame(driver.findElement(By.tagName("iframe")));
		// Select category = new
		// Select(driver.findElement(By.id("MainContent_control_VehicleCategoryList")));

		sleep(1500);
		randomNum = rand.nextInt(1) + 2;

		try {
			Select category = new Select(driver.findElement(By.id("MainContent_control_VehicleCategoryList")));
			category.selectByIndex(randomNum);
		} catch (org.openqa.selenium.StaleElementReferenceException ex) {
			Select category = new Select(driver.findElement(By.id("MainContent_control_VehicleCategoryList")));
			category.selectByIndex(randomNum);
		} 

		// category.selectByIndex(randomNum);
		Log.log("Kategória megadása");

		try {
			Select brand = new Select(driver.findElement(By.id("MainContent_control_BrandList")));
			sleep(1500);
			randomNum = rand.nextInt(25) + 1;
			brand.selectByIndex(randomNum);
		} catch (org.openqa.selenium.StaleElementReferenceException ex) {
			Select brand = new Select(driver.findElement(By.id("MainContent_control_BrandList")));
			brand.selectByIndex(randomNum);
		}
		Log.log("Márka megadása");

		try {
			Select brand = new Select(driver.findElement(By.id("MainContent_control_BrandList")));
			sleep(1500);
			randomNum = rand.nextInt(1) + 1;
			brand.selectByIndex(randomNum);
		} catch (org.openqa.selenium.StaleElementReferenceException ex) {
			Select brand = new Select(driver.findElement(By.id("MainContent_control_EngineType")));
			brand.selectByIndex(randomNum);
		}
		Log.log("Motor típus megadása");

		try {
			Select brand = new Select(driver.findElement(By.id("MainContent_control_ModelRange1List")));
			sleep(1500);
			randomNum = rand.nextInt(1) + 1;
			brand.selectByIndex(randomNum);
			
		} catch (org.openqa.selenium.StaleElementReferenceException ex) {
			Select brand = new Select(driver.findElement(By.id("MainContent_control_ModelRange1List")));
			randomNum = rand.nextInt(1) + 1;
			brand.selectByIndex(randomNum);
		}
		Log.log("Modell Sorozat megadása");
		
		try {
			Select brand = new Select(driver.findElement(By.id("MainContent_control_ModelRange2List")));
			sleep(1500);
			randomNum = rand.nextInt(1) + 1;
			brand.selectByIndex(randomNum);
			
		} catch (org.openqa.selenium.StaleElementReferenceException ex) {
			Select brand = new Select(driver.findElement(By.id("MainContent_control_ModelRange2List")));
			randomNum = rand.nextInt(1) + 1;
			brand.selectByIndex(randomNum);
		}
		Log.log("Modell megadása");

		sleep(500);
		
		fillName("ctl00$MainContent$control_mileage","12030");
		
		driver.findElement(By.name("ctl00$MainContent$controlButtonProceedToModelSelection")).click();
		Log.log("tovább");
		
		sleep(1000);
		driver.switchTo().defaultContent();
		driver.switchTo().frame(driver.findElement(By.tagName("iframe")));
		sleep(1000);
	   
	   driver.findElement(By.id("MainContent_myModelControl_controlModelList_controlModelSelectorButton_0")).click();
	   sleep(1000);
	   
		
		   driver.findElement(By.id("MainContent_ControlCondition_controlLabelCategory3HeaderText")).click();
		   sleep(1000);
		   
		   driver.findElement(By.id("MainContent_ControlCondition_ForwardToEquipmentFromCondition3")).click();
		   sleep(1000);
		   
		   driver.findElement(By.name("ctl00$MainContent$myEquipmentControl$PaneOptional_content$controlOptionalEquipmentList$0")).click();
		   Log.log("Kategória Választása");
		   
		   driver.findElement(By.name("ctl00$MainContent$controlForwardToValuation")).click();
		   Log.log("Kategória Választása");
		   
		   driver.findElement(By.id("MainContent_controlForwardToValuation")).click();
		   Log.log("Tovább a Értékelés Típusának kiválasztásához / Fizetéséhez.");
		   sleep(1000);
		   fillName("ctl00$MainContent$PaneValuation_content$controlEmailAddress","tesz@teszt.hu");
		   
		   Log.log("Email cím megadása");
		   driver.findElement(By.name("ctl00$MainContent$PaneValuation_content$chkUserConsent")).click();
		   
		   Log.log("A felhasználói feltételeket és az adatvédelmi nyilatkozatot elolvastam megértettem és elfogadom");
		   driver.findElement(By.id("MainContent_ControlButtonCCbasic")).click();
		   sleep(3000);
		   
		   driver.switchTo().defaultContent();
		   driver.switchTo().frame(driver.findElement(By.tagName("iframe")));
		   sleep(2000);
		   driver.findElement(By.id("MainContent_controlCheckBoxInvoiceRequired")).click();
		   sleep(3000);
		 
		   fillName("ctl00$MainContent$controlTextBoxCegnev","Teszt Ember");
		   fillName("ctl00$MainContent$controlTextBoxAdoszam","1035678942");
		   fillName("ctl00$MainContent$controlTextBoxCimTelepules","Soroksár");
		   fillName("ctl00$MainContent$controlTextBoxCimIranyitoszam","1238");
		   fillName("ctl00$MainContent$controlTextBoxCimKozteruletNeve","Táncsics Mihály Utca 76");
		   driver.findElement(By.name("ctl00$MainContent$ControlForwardToCCPayment")).click();
		   Log.log("Fizetés elindítása");
		   sleep(2000);
		   goToPage(url+"/hu/garazs");
		   Log.log("Fizetés Megszakítva Vissza a kezdőlapra!");
		}
	


	public static void addGPS() throws IOException, InterruptedException {
		TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		List<WebElement> elements = driver.findElements(By.cssSelector(".card .profile-car-item"));
		List<String> numberPlates = new ArrayList<String>();
		List<String> gpsCodes = new ArrayList<String>();

		gpsCodes.add("864893031557658");
		gpsCodes.add("864893031571469");
		gpsCodes.add("864895030889255");

		for (WebElement element : elements) {
			numberPlates.add(element.findElement(By.className("numberplate")).getText());
		}

		goToPage(url + "/hu/kijelentkezes");
		TestBase.adminLogin();

		int c = 0;
		for (String numberPlate : numberPlates) {
			goToPage(url + "/hu/admin/car/car-mycars");
			fillName("quick_search", numberPlate);
			driver.findElement(By.className("btn-primary")).click();
			sleep(1000);
			driver.findElement(By.className("command-edit")).click();
			fillName("gps_ident", gpsCodes.get(c));
			submit();
			c++;
		}

		goToPage(url + "/hu/kijelentkezes");
		TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		clickLinkWithText(numberPlates.get(0));

	}

	public static void passShepherd() {
		try {
			driver.findElement(By.className("shepherd-button")).click();
		} catch (NoSuchElementException e) {

		}
	}

	public static void cronRun() throws IOException {
		goToPage(url + "/hu/admin/car/pages/run-cron/event");
		clickLinkWithText("Események cron futtatása");
	}
	
	public static void driverLicenceNotifications(int days) throws Exception {
		goToPage(url);
		click("a.user-img");
		clickLinkWithText("Adatmódosítás");
		LocalDate dueDate = LocalDate.now().plusDays(days);

		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-LL-dd");
		String strDueDate = dueDate.format(dateFormat);

	    fillName("driving_licence_expiration", strDueDate);
	    submit();
	    
	    sleep(2000);
	    cronRun();
	    
	    goToPage(url);
	    click(".nav-notifications");
	    WebElement lastNotification = driver.findElement(By.cssSelector(".nav-notifications .dropdown-menu a:nth-child(1) .notification-title"));
	    String lastNotificationText = lastNotification.getText();
	    assertEquals("Jogosítványod lejár", lastNotificationText);
	    
		Log.log("Jogosítvány lejártáról értesítés +" + days + " nap");
		lastNotification.click();
		String URL = driver.getCurrentUrl();
		assertEquals(url + "/hu/profil-modositas", URL);
		Log.log("Ugrás a profil oldalra");

		notificationEmail(days);

	}

	public static void highwayFeeNotifications(int days) throws IOException, InterruptedException {
		goToPage(url+"/hu/autopalya-matrica-hozzadasa/" + getCarId());
		
		LocalDate dueDate = LocalDate.now().plusDays(days);
	      
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-LL-dd");
		String strDueDate = dueDate.format(dateFormat);
	    fillName("start_date", strDueDate);
	    
		List<WebElement> list = driver.findElements(By.cssSelector("input[type=\"radio\"]"));
		int size = list.size();
		int randNumber = new Random().nextInt(size - 1) + 1;
		String id = list.get(randNumber).getAttribute("id");
		//list.get(randNumber).click();
		driver.findElement(By.xpath("//label[@for='" + id + "']")).click();
		String name = driver.findElement(By.cssSelector("label[for=\"" + id + "\"] .ticket-name i")).getText();
		String expiration = driver.findElement(By.cssSelector("label[for=\"" + id + "\"] .ticket-expiration")).getText();
		String price = driver.findElement(By.cssSelector("label[for=\"" + id + "\"] .ticket-price")).getText();
		
		Log.log(name + " autópálya matrica kiválasztva.");
		Log.log(expiration + " lejárattal.");
		Log.log(price + " áron.");
		submit();
		
		sleep(2000);
	    cronRun();
	}
	
	protected static void notificationEmail(int day) throws Exception {
		driver.get("https://gmail.com");
		try {
			driver.findElement(By.cssSelector("input[type=\"email\"]")).sendKeys(testerMail);
			driver.findElement(By.xpath("//*[text()='Következő']")).click();

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type=password]")));

			driver.findElement(By.cssSelector("input[type=password]")).sendKeys(testerPassword);
			driver.findElement(By.xpath("//*[text()='Következő']")).click();
			Log.log("Login Gmail");
		} catch (NoSuchElementException e) {

		}

		sleep(6000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//*[text()='ECDH Esemény értesítő'])[2]")));
		driver.findElement(By.xpath("(//*[text()='ECDH Esemény értesítő'])[2]")).click();

		int found1 = driver.findElements(By.xpath("//*[contains(text(), \"jogosítványod lejárata\")]")).size();
		String string = day + " nap múlva esedékes lesz";
		int found2 = driver.findElements(By.xpath("//*[contains(text(), \"" + string + "\")]")).size();
		assertTrue("Email kiment a jogosítvány lejártáról " + day + " nap", found1 > 0 & found2 > 0);
		Log.log("Email kiment jogosítvány lejártáról +" + day + " nap");
		driver.get("https://accounts.google.com/Logout");
	}

	public static void addNewCarEventOdometerReading() throws IOException, InterruptedException {

		clickLinkWithText("esemény hozzáadása");

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-mycar_other_event")));

		click(".sprite-mycar_other_event");

		click(".ts-date-picker");

		driver.findElement(By.xpath("/html/body/header/div/div/div[1]/div")).click();

		submit();

		Log.log("Esemény: Km óra állás sikeresen rögzítve");
		/*
		 * Törléshez:
		 * 
		 * clickLinkWithText("Km óra állás");
		 * 
		 * clickLinkWithText("Szerkesztés");
		 * 
		 * click(".ts-date-picker");
		 * driver.findElement(By.xpath("/html/body/header/div/div/div[1]/div")).click();
		 * 
		 * submit();
		 * 
		 * driver.findElement(By.cssSelector(".fas.fa-trash circle")).click();
		 * driver.findElement(By.className("btn-secondary")).click();
		 * 
		 * Log.log("Esemény: Km óra állás sikeresen törölve.");
		 */
	}

	public static void addNewCarEventVehicleTax() throws IOException, InterruptedException {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		String yearS = "" + year;

		goToPage(TestBase.url + "/hu/teljesitmenyado-befizetes/" + getCarId());
		sleep(5000);

		// wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("installment-type")));
		Select taxType = new Select(driver.findElement(By.id("installment-type")));
		taxType.selectByVisibleText("Első részlet");

		click(".ts-date-picker");
		driver.findElement(By.xpath(
				"/html/body/main/section[2]/div/div/div[2]/div/form/div[2]/div[1]/div[3]/div/div/div[1]/ul/li[1]/div/div[1]/table/thead/tr[1]/th[2]"))
				.click();
		driver.findElement(By.xpath(
				"/html/body/main/section[2]/div/div/div[2]/div/form/div[2]/div[1]/div[3]/div/div/div[1]/ul/li[1]/div/div[2]/table/tbody/tr/td/span[3]"))
				.click();
		driver.findElement(By.xpath(
				"/html/body/main/section[2]/div/div/div[2]/div/form/div[2]/div[1]/div[3]/div/div/div[1]/ul/li[1]/div/div[1]/table/tbody/tr[2]/td[3]"))
				.click();
		// driver.findElement(By.xpath("/html/body/header/div/div/div[1]/div")).click();

		int randPrice = new Random().nextInt(123456) + 1100;
		int randNumber = new Random().nextInt(500) + 1;

		sleep(1000);

		String taxPrice = "" + randPrice;
		fillName("price", taxPrice);
		String noteText = "Test note " + randNumber;
		fillName("note", noteText);
		submit();

		Log.log("Sikeresen mentve");
		sleep(2000);
		goToPage(TestBase.url + "/hu/teljesitmenyado-befizetesek/" + getCarId());
		sleep(2000);
		clickLinkWithText("Első részlet");
		onScreen("Első részlet");
		checkPrice(randPrice, " ");

		if (yearS.equals(driver.findElement(By.xpath("/html/body/main/section[2]/div/div/div[2]/div/div[2]/dl[4]/dd"))
				.getText())) {
			System.out.println("Helyes befizetett év");
		} else {
			assertTrue("Befizetett év hiba", driver.getPageSource().contains("2019"));
		}

		clickLinkWithText("Szerkesztés");
		Log.log("Módosítás");
		sleep(2000);

		onScreen("Első részlet");
		onScreen(noteText);
		submit();

		sleep(2000);
		goToPage(TestBase.url + "/hu/teljesitmenyado-befizetes/" + getCarId());
		sleep(5000);

		click(".ts-date-picker");
		driver.findElement(By.xpath("/html/body/header/div/div/div[1]/div")).click();

		randNumber = new Random().nextInt(500) + 1;

		sleep(1000);

		String noteText2 = "Test note " + randNumber;
		fillName("note", noteText2);
		submit();

		Log.log("Sikeresen mentve");
		sleep(2000);

		goToPage(TestBase.url + "/hu/teljesitmenyado-befizetesek/" + getCarId());
		sleep(3000);
		clickLinkWithText("Második részlet");
		onScreen("Második részlet");
		checkPrice(randPrice, " ");

		if (yearS.equals(driver.findElement(By.xpath("/html/body/main/section[2]/div/div/div[2]/div/div[2]/dl[4]/dd"))
				.getText())) {
			System.out.println("Helyes befizetett év");
		} else {
			assertTrue("Befizetett év hiba", driver.getPageSource().contains("2019"));
		}

		clickLinkWithText("Szerkesztés");
		Log.log("Módosítás");
		sleep(2000);

		onScreen("Második részlet");
		onScreen(noteText2);
		submit();
		sleep(3000);

		// driver.findElement(By.xpath("/html/body/main/section[2]/div/div[2]/div[2]/div[3]/a[3]/i")).click();
		// driver.findElement(By.cssSelector("i.fas.fa-trash.circle")).click();
		
		clickCss(".fas.fa-trash.circle");
		
		sleep(2000);
		driver.findElement(By.className("btn-secondary")).click();

		Log.log("Esemény: Teljesítményadó második részlet sikeresen törölve.");
		sleep(4000);
		// driver.findElement(By.xpath("/html/body/main/section[2]/div/div[2]/div[2]/div[3]/a[3]/i")).click();
		clickCss(".fas.fa-trash.circle");
		sleep(2000);
		driver.findElement(By.className("btn-secondary")).click();

		Log.log("Esemény: Teljesítményadó első részlet sikeresen törölve.");
		sleep(4000);

		/*
		 * clickLinkWithText("Új esemény hozzáadása"); sleep(2000);
		 * wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(
		 * "sprite-mycar_hp_tax"))); sleep(2000); click(".sprite-mycar_hp_tax");
		 */

		goToPage(TestBase.url + "/hu/teljesitmenyado-befizetes/" + getCarId());
		sleep(5000);

		Select taxType2 = new Select(driver.findElement(By.id("installment-type")));
		taxType2.selectByVisibleText("Egy összegben");

		click(".ts-date-picker");
		driver.findElement(By.xpath(
				"/html/body/main/section[2]/div/div/div[2]/div/form/div[2]/div[1]/div[3]/div/div/div[1]/ul/li[1]/div/div[1]/table/thead/tr[1]/th[2]"))
				.click();
		driver.findElement(By.xpath(
				"/html/body/main/section[2]/div/div/div[2]/div/form/div[2]/div[1]/div[3]/div/div/div[1]/ul/li[1]/div/div[2]/table/tbody/tr/td/span[3]"))
				.click();
		driver.findElement(By.xpath(
				"/html/body/main/section[2]/div/div/div[2]/div/form/div[2]/div[1]/div[3]/div/div/div[1]/ul/li[1]/div/div[1]/table/tbody/tr[2]/td[3]"))
				.click();

		randNumber = new Random().nextInt(500) + 1;
		int randPrice2 = new Random().nextInt(123456);
		String taxPrice2 = "" + randPrice2;
		fillName("price", taxPrice2);

		sleep(1000);

		String noteText3 = "Test note " + randNumber;
		fillName("note", noteText3);
		submit();

		Log.log("Sikeresen mentve");
		sleep(2000);
		goToPage(TestBase.url + "/hu/teljesitmenyado-befizetesek/" + getCarId());
		sleep(2000);
		clickLinkWithText("Egy összegben");
		onScreen("Egy összegben");
		checkPrice(randPrice2, " ");

		if (yearS.equals(driver.findElement(By.xpath("/html/body/main/section[2]/div/div/div[2]/div/div[2]/dl[4]/dd"))
				.getText())) {
			System.out.println("Helyes befizetett év");
		} else {
			assertTrue("Befizetett év hiba", driver.getPageSource().contains("2019"));
		}

		clickLinkWithText("Szerkesztés");
		Log.log("Módosítás");
		sleep(2000);

		onScreen("Egy összegben");
		onScreen(noteText3);
		submit();
		sleep(3000);

		// driver.findElement(By.xpath("/html/body/main/section[2]/div/div[2]/div[2]/div[3]/a[3]/i")).click();
		clickCss(".fas.fa-trash.circle");
		sleep(1000);
		driver.findElement(By.className("btn-secondary")).click();

		Log.log("Esemény: Egész éves teljesítményadó sikeresen törölve.");

	}

	private static void clickCss(String string) throws IOException, InterruptedException {
		
		sleep(2000);
		WebElement element = driver.findElement(By.cssSelector(string));
		Actions actions = new Actions(driver);
		actions.moveToElement(element).click().perform();
		sleep(3000);
		
	}
	
	public static void inviteActivateFriend() throws IOException, InterruptedException {

		driver.findElement(By.xpath("/html/body/header/div/div/div[2]/div[4]/a")).click();
		driver.findElement(By.className("sprite-invite")).click();
		// clickLinkWithText("Új meghívó link");
		try{

			driver.findElement(By.xpath("//*[contains(text(),'Új meghívó link')]")).click();
			
		}catch(NoSuchElementException e){
			
			Log.log("Már van meghívó link generálva");
			
		}
		
		sleep(2000);
		fillName("invitee_emails[0][invitee_email]", testerMail);
		String inviteLink = driver.findElement(By.xpath("/html/body/main/section[2]/div[1]/div[2]/div[2]/a")).getText();
		submit();
		sleep(1000);
		goToPage(TestBase.url + "/hu/garazs");
		sleep(1000);
		Log.log("Sikeres meghívó küldés");
		goToPage(TestBase.url + "/hu/kijelentkezes");
		sleep(1000);
		Log.log("Sikeres kijelentkezés");
		sleep(1000);
		driver.get("https://gmail.com");

		driver.findElement(By.cssSelector("input[type=\"email\"]")).sendKeys(testerMail);
		driver.findElement(By.xpath("//*[text()='Következő']")).click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type=password]")));

		driver.findElement(By.cssSelector("input[type=password]")).sendKeys(testerPassword);
		driver.findElement(By.xpath("//*[text()='Következő']")).click();
		Log.log("Login Gmail");

		sleep(2000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("(//*[text()='Teljeskörű autókezelő alkalmazás (meghívó az ECDH.hu-ra)'])[2]")));
		driver.findElement(By.xpath("(//*[text()='Teljeskörű autókezelő alkalmazás (meghívó az ECDH.hu-ra)'])[2]"))
				.click();

		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//a[contains(text(), 'Jól hangzik, kezdjük!')]")));
		driver.findElement(By.xpath("//a[contains(text(), 'Jól hangzik, kezdjük!')]")).click();
		Log.log("Meghívás elfogadása");
		goToPage(inviteLink);

		try {
			driver.switchTo().alert().accept();
			goToPage(inviteLink);
			sleep(3000);
		} catch (NoSuchElementException e) {

		}
		driver.switchTo().alert().accept();
		goToPage(inviteLink);
		sleep(3000);

		try {
			element = driver.findElement(By.className("ok"));
			element.click();
		} catch (NoSuchElementException e) {

		}
		Log.log("Accept cookies");

		fillName("user[username]", testerMail);
		fillName("user[password]", personalPassword);
		fillName("user[confirm_password]", personalPassword);

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
		Log.log("Click on Regisztráció");

		// wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='A
		// regisztrációd sikeres']")));
		wait.until(ExpectedConditions.textToBePresentInElementLocated(By.className("feedback-page"),
				"regisztrációd sikeres"));

		assertTrue("Registration succeed", driver.getPageSource().contains("A regisztrációd sikeres"));
		Log.log("Register succeed");
		sleep(15000);

		driver.get("https://gmail.com");

		sleep(4000);
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("(//*[text()='Regisztráció megerősítése (ECDH)'])[2]")));
		driver.findElement(By.xpath("(//*[text()='Regisztráció megerősítése (ECDH)'])[2]")).click();

		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//a[contains(text(), 'Személyes fiók aktiválása')]")));
		driver.findElement(By.xpath("//a[contains(text(), 'Személyes fiók aktiválása')]")).click();
		Log.log("New user account activation");

		System.out.println(driver.getTitle());

		for (String winHandle : driver.getWindowHandles()) {
			System.out.println(winHandle);
			driver.switchTo().window(winHandle);
		}

	}
	
	public static void addNewTire() throws IOException, InterruptedException {
		
		sleep(4000);
		clickLinkWithText("Új gumi hozzáadása");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("summer_tire_change_month")));
		int randType = new Random().nextInt(3)+1;
		Select type = new Select(driver.findElement(By.id("type")));
		type.selectByIndex(randType);
		String typeValue = type.getFirstSelectedOption().getText();
		Log.log(typeValue);
		Log.log("Típus választás");
		
		int randPrice = new Random().nextInt(50000)+200000;
		fillName("price",""+randPrice);
		Log.log("Gumi ára");
		
		int randFacturer = new Random().nextInt(28)+1;
		Select mufacturer = new Select(driver.findElement(By.id("mufacturer")));
		mufacturer.selectByIndex(randFacturer);
		String facturerValue = mufacturer.getFirstSelectedOption().getText();
		Log.log(facturerValue);
		Log.log("Márka választás");
		
		fillName("item_description","test model");
		Log.log("Gumi modell");
		

		Select number = new Select(driver.findElement(By.id("number")));
		number.selectByIndex(2);
		Log.log("darabszám választás");
		
		
		int randWorn = new Random().nextInt(2)+1;
		Select worn = new Select(driver.findElement(By.id("worn")));
		worn.selectByIndex(randWorn);
		String wornValue = worn.getFirstSelectedOption().getText();
		Log.log(wornValue);
		Log.log("állapot választás");
		
		int randDotWeek = new Random().nextInt(52)+1;
		Select dot_week = new Select(driver.findElement(By.id("dot_week")));
		dot_week.selectByIndex(randDotWeek);
		String dwValue = dot_week.getFirstSelectedOption().getText();
		Log.log(dwValue);
		Log.log("DOT hét");
		
		int randDotYear = new Random().nextInt(30)+1;
		Select dot_year = new Select(driver.findElement(By.id("dot_year")));
		dot_year.selectByIndex(randDotYear);
		String dyValue = dot_year.getFirstSelectedOption().getText();
		Log.log(dyValue);
		Log.log("Dot év");
		
			int randDepthLeftFront = new Random().nextInt(10)+1;
			Select thread_depth_1 = new Select(driver.findElement(By.id("thread-depth-1")));
			thread_depth_1.selectByIndex(randDepthLeftFront);
			String dlfValue = thread_depth_1.getFirstSelectedOption().getText();
			Log.log(dlfValue);
			Log.log("Bal első");
			
			int randDepthLeftBack = new Random().nextInt(10)+1;
			Select thread_depth_2 = new Select(driver.findElement(By.id("thread-depth-2")));
			thread_depth_2.selectByIndex(randDepthLeftBack);
			String dlbValue = thread_depth_2.getFirstSelectedOption().getText();
			Log.log(dlbValue);
			Log.log("Bal hátsó");
			
			int randDepthRightFront = new Random().nextInt(10)+1;
			Select thread_depth_3 = new Select(driver.findElement(By.id("thread-depth-3")));
			thread_depth_3.selectByIndex(randDepthRightFront);
			String drfValue = thread_depth_3.getFirstSelectedOption().getText();
			Log.log(drfValue);
			Log.log("Jobb első");
			
			int randDepthRightBack = new Random().nextInt(10)+1;
			Select thread_depth_4 = new Select(driver.findElement(By.id("thread-depth-4")));
			thread_depth_4.selectByIndex(randDepthRightBack);
			String drbValue = thread_depth_4.getFirstSelectedOption().getText();
			Log.log(drbValue);
			Log.log("Jobb hátsó");
			
		
		int randText = new Random().nextInt(500)+100;
		driver.findElement(By.id("tire-storage")).sendKeys("test text "+randText);
		Log.log("Tárolás megjegyzés");
		
		driver.findElement(By.cssSelector(".btn.btn-primary.submitBtn.tsLoadingIcon")).click();
		Log.log("Gumi sikeresen hozzáadva");
		sleep(3000);
		
		onScreen(typeValue);
		onScreen(facturerValue);
		onScreen("4 db");
		clickLinkWithText(typeValue);
		sleep(2000);
		
		onScreen(typeValue);
		onScreen(facturerValue + " ");
		onScreen("test model");
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@class='card']//*[contains(text(), '4')]")));
		System.out.println("4");
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("4"));
		Log.log("Képernyőn: 4");
		
		driver.findElement(By.cssSelector(".fas.fa-pencil-alt.circle")).click();
		sleep(2000);
		
		onScreen(typeValue);
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + randPrice + "']")));
		System.out.println(randPrice);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(""+randPrice));
		Log.log("Képernyőn: " + randPrice);
		
		onScreen(facturerValue);
		onScreenValue("test model");
		onScreen(wornValue);
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='dot_week']//*[contains(text(), '"+dwValue+"')][1]")));
		System.out.println(dwValue);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(""+dwValue));
		Log.log("Képernyőn: " + dwValue);
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='dot_year']//*[contains(text(), '"+dyValue+"')][1]")));
		System.out.println(dyValue);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(""+dyValue));
		Log.log("Képernyőn: " + dyValue);
		
		onScreen(dlfValue);
		onScreen(dlbValue);
		onScreen(drfValue);
		onScreen(drbValue);
		onScreen("test text "+randText);
		
		
		randType = new Random().nextInt(3)+1;
		type = new Select(driver.findElement(By.id("type")));
		type.selectByIndex(randType);
		typeValue = type.getFirstSelectedOption().getText();
		Log.log(typeValue);
		Log.log("Típus választás");
		
		randPrice = new Random().nextInt(50000)+200000;
		fillName("price",""+randPrice);
		Log.log("Gumi ára");
		
		randFacturer = new Random().nextInt(28)+1;
		mufacturer = new Select(driver.findElement(By.id("mufacturer")));
		mufacturer.selectByIndex(randFacturer);
		facturerValue = mufacturer.getFirstSelectedOption().getText();
		Log.log(facturerValue);
		Log.log("Márka választás");
		
		fillName("item_description","test model");
		Log.log("Gumi modell");
		
		
		number = new Select(driver.findElement(By.id("number")));
		number.selectByIndex(1);
		Log.log("darabszám választás");
		
		
		randWorn = new Random().nextInt(2)+1;
		worn = new Select(driver.findElement(By.id("worn")));
		worn.selectByIndex(randWorn);
		wornValue = worn.getFirstSelectedOption().getText();
		Log.log(wornValue);
		Log.log("állapot választás");
		
		randDotWeek = new Random().nextInt(52)+1;
		dot_week = new Select(driver.findElement(By.id("dot_week")));
		dot_week.selectByIndex(randDotWeek);
		dwValue = dot_week.getFirstSelectedOption().getText();
		Log.log(dwValue);
		Log.log("DOT hét");
		
		randDotYear = new Random().nextInt(30)+1;
		dot_year = new Select(driver.findElement(By.id("dot_year")));
		dot_year.selectByIndex(randDotYear);
		dyValue = dot_year.getFirstSelectedOption().getText();
		Log.log(dyValue);
		Log.log("Dot év");
		
		randDepthLeftFront = new Random().nextInt(10)+1;
		thread_depth_1 = new Select(driver.findElement(By.id("thread-depth-1")));
		thread_depth_1.selectByIndex(randDepthLeftFront);
		dlfValue = thread_depth_1.getFirstSelectedOption().getText();
		Log.log(dlfValue);
		Log.log("Bal első");
		
		randDepthLeftBack = new Random().nextInt(10)+1;
		thread_depth_2 = new Select(driver.findElement(By.id("thread-depth-2")));
		thread_depth_2.selectByIndex(randDepthLeftBack);
		dlbValue = thread_depth_2.getFirstSelectedOption().getText();
		Log.log(dlbValue);
		Log.log("Bal hátsó");
		
		
		randText = new Random().nextInt(500)+100;
		driver.findElement(By.id("tire-storage")).sendKeys("test text "+randText);
		Log.log("Tárolás megjegyzés");
		
		driver.findElement(By.cssSelector(".btn.btn-primary.submitBtn.tsLoadingIcon")).click();
		Log.log("Gumi sikeresen módosítva");
		sleep(3000);
		
		onScreen(typeValue);
		onScreen(facturerValue);
		onScreen("2 db");
		clickLinkWithText(typeValue);
		sleep(2000);
		
		onScreen(typeValue);
		onScreen(facturerValue + " ");
		onScreen("test model");

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@class='card']//*[contains(text(), '2')]")));
		System.out.println("2");
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("2"));
		Log.log("Képernyőn: 2");
		
		driver.findElement(By.cssSelector(".fas.fa-trash.circle")).click();
		driver.findElement(By.cssSelector(".btn.btn-sm.h-100.d-flex.align-items-center.btn-secondary")).click();
		Log.log("Gumi sikeresen törölve!");
		
		
	}
	
	public static void documentStorage() throws IOException, InterruptedException {
		
		sleep(4000);
		clickLinkWithText("Dokumentumtár");
		sleep(1000);
		driver.findElement(By.cssSelector(".btn.btn-secondary.popup")).click();
		sleep(1000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("document-type")));
		Log.log("Típus választó stimmel");
		sleep(1000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("browse-file")));
		Log.log("Fájl feltöltés stimmel");
		sleep(1000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note")));
		Log.log("Megjegyzés stimmel");
		sleep(1000);
		driver.findElement(By.xpath("/html/body/div[3]/div/div/div/form/section/div/div[1]/button")).click();
		sleep(1000);
		driver.findElement(By.xpath("/html/body/header/div/div/div[1]/a")).click();
		
	}
	
public static void addNewCalendarEvent() throws IOException, InterruptedException {
		
		sleep(1000);
		driver.findElement(By.xpath("//*[@id='calendar']//*[contains(text(),'20')]")).click();
		sleep(1000);
		Log.log("Nap kiválasztása");
		clickLinkWithText("Esemény hozzáadása");
		sleep(1000);
		int rand = new Random().nextInt(500)+500;
		fillName("title","Test esemény "+ rand);
		String titleText = "Test esemény "+rand;
		fillName("description","Test megjegyzés "+ rand);
		String descText = "Test megjegyzés "+rand;

		fillName("cal_location",""+ "repülőtéri út 6");
		sleep(1000);
		
		driver.findElement(By.id("cal_location")).sendKeys(Keys.ARROW_DOWN);
		driver.findElement(By.id("cal_location")).sendKeys(Keys.ENTER);
		Log.log("Autocomplete mező kitöltése");
		
		driver.findElement(By.cssSelector(".btn.btn-primary.w-100")).click();
		
		Log.log("Naptári esemény sikeresen felvive");
		
		sleep(1000);
		driver.findElement(By.xpath("//*[@id='calendar']//*[contains(text(),'20')]")).click();
		sleep(1000);
		driver.findElement(By.xpath("//*[contains(text(),'"+titleText+"')]")).click();
		onScreen(titleText);
		onScreen("20.");
		onScreen(descText);
		onScreen("Budapest, Repülőtéri út 6, Magyarország");
		Log.log("Ismétlődik?");
		onScreen("Nem");
		driver.findElement(By.cssSelector(".text-uppercase.btn.btn-secondary.popup")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("title")));
		
		onScreen(titleText);
		onScreen(descText);
		onScreen("-20");
		onScreen("Budapest, Repülőtéri út 6, Magyarország");
		driver.findElement(By.xpath("//label[contains(text(),'Egész napos')]")).click();
		rand = new Random().nextInt(500)+500;
		fillName("title","Test esemény "+ rand);
		titleText = "Test esemény "+rand;
		fillName("description","Test megjegyzés "+ rand);
		descText = "Test megjegyzés "+rand;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("recurring"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("recurring-data-interval")));
		fillName("recurring_data[interval]","30");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("recurring-data-frequency"))).click();
		driver.findElement(By.id("recurring-data-frequency")).sendKeys(Keys.ARROW_DOWN);
		driver.findElement(By.id("recurring-data-frequency")).sendKeys(Keys.ARROW_DOWN);
		driver.findElement(By.id("recurring-data-frequency")).sendKeys(Keys.ARROW_DOWN);
		driver.findElement(By.id("recurring-data-frequency")).sendKeys(Keys.ENTER);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("recurring-data-end-condition"))).click();
		driver.findElement(By.id("recurring-data-end-condition")).sendKeys(Keys.ARROW_DOWN);
		driver.findElement(By.id("recurring-data-end-condition")).sendKeys(Keys.ENTER);
		fillName("recurring_data[count]","1");
		driver.findElement(By.cssSelector(".btn.btn-primary.w-100")).click();
		onScreen(titleText);
		onScreen("20.");
		onScreen(descText);
		onScreen("Budapest, Repülőtéri út 6, Magyarország");
		Log.log("Ismétlődik?");
		onScreen("Igen");
		
		driver.findElement(By.cssSelector(".fas.fa-trash.circle")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".btn.grayBtn.deleteAttachedItem"))).click();
		Log.log("Esemény törölve!");
		

	}

public static void gasStation() throws IOException, InterruptedException {
	
		driver.findElement(By.xpath("/html/body/header/div/div/div[2]/div[4]/a")).click();
		sleep(1000);
		driver.findElement(By.className("sprite-gas-stations")).click();
		sleep(2000);
		
		try {
			
			driver.findElement(By.xpath("/html/body/main/section/div[3]/div[2]/div/div[2]/a[1]")).click();
		
		}catch(NoSuchElementException e){
			
			Log.log("Nem kattintható benzinkút!");
			driver.close();
			System.exit(0);
			
		}
		Log.log("Van tölttőállomás az adatbázisban!");
		
		sleep(2000);
		driver.findElement(By.xpath("//button[contains(text(), 'Útvonaltervezés ide')]")).click();
		Log.log("Útvonaltervezés");
		sleep(1000);
		fillName("to", "sas 25");
		sleep(2000);
		driver.findElement(By.id("to")).sendKeys(Keys.ARROW_DOWN);
		sleep(2000);
		driver.findElement(By.id("to")).sendKeys(Keys.ENTER);
		sleep(2000);
		Log.log("Cím választás");
		driver.findElement(By.cssSelector(".btn.btn-primary.w-100")).click();
		Log.log("Tervez");
		sleep(2000);
		
		try {
			
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[3]/img)[1]")));
			Log.log("'A' pont a térképen");
			sleep(2000);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[4]/img)[1]")));
			Log.log("'B' pont a térképen");
			sleep(2000);
		
		}catch(NoSuchElementException e){
			
			Log.log("Útvonal tervezés hiba!");
			driver.close();
			System.exit(0);
			
		}
		Log.log("Sikeres útvonaltervezés");

		driver.findElement(By.xpath("/html/body/header/div/div/div[2]/div[4]/a")).click();
		sleep(1000);
		driver.findElement(By.className("sprite-gas-stations")).click();
		sleep(2000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("multiselect"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[contains(text(), 'MOL')]"))).click();
		sleep(500);
		Log.log("MOL-ra szűrés");
		submit();
		sleep(3000);
		
		try {
			
			driver.findElement(By.xpath("//a[div[contains(text(), 'MOL')]][1]")).click();
		
		}catch(NoSuchElementException e){
			
			Log.log("Márka szűrő hiba!");
			driver.close();
			System.exit(0);
			
		}
		Log.log("MOL találat");
		
		sleep(1000);
		driver.findElement(By.xpath("//*[contains(text(), 'MOL')]"));
		driver.findElement(By.xpath("/html/body/header/div/div/div[2]/div[4]/a")).click();
		sleep(1000);
		driver.findElement(By.className("sprite-gas-stations")).click();
		sleep(2000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("multiselect"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[contains(text(), 'SHELL')]"))).click();
		sleep(500);
		Log.log("SHELL-re szűrés");
		submit();
		sleep(3000);

		try {
			
			try {
				
				driver.findElement(By.xpath("//a[div[contains(text(), 'MOL')]][1]")).click();
				Log.log("Márka szűrő hiba!");
				driver.close();
				System.exit(0);

			}catch(NoSuchElementException e){
				
				Log.log("Márka szűrő Működik!");
				
			}
			
			driver.findElement(By.xpath("//a[div[contains(text(), 'SHELL')]][1]")).click();
		
		}catch(NoSuchElementException e){
			
			Log.log("Márka szűrő hiba!");
			driver.close();
			System.exit(0);
			
		}
		Log.log("SHELL találat");
		
		sleep(1000);
		driver.findElement(By.xpath("//*[contains(text(), 'SHELL')]"));
		Log.log("Sikeres tölttőállomás teszt!");
	
	}

public static void companySearch() throws IOException, InterruptedException {

	String firstResult ="TestText";
	clickLinkWithText("Cégkereső");
	wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
	
	try {
		
	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//section//span/a)[1]")));
	
	}catch(NoSuchElementException e) {
		
		Log.log("Nincs cég az oldalon!");
		driver.close();
		System.exit(0);
		
	}
	
	String firstCompany = driver.findElement(By.xpath("(//section//span/a)[1]")).getText();
	String secondCompany = driver.findElement(By.xpath("(//section//span/a)[2]")).getText();
	
	fillName("name",firstCompany);
	submit();
	sleep(3000);
	
	try {
		
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//section//span/a)[1]")));
			String record1 = driver.findElement(By.xpath("(//section//span/a)[1]")).getText();
			
			if(firstCompany.equals(record1)) {
				
				Log.log("Név szerinti kereső teszt1: jó");
				
			}else {
				
				Log.log("Név szerinti kereső teszt1: HIBA!");
				driver.close();
				System.exit(0);
				
			}
		
		}catch(NoSuchElementException e) {
			
			Log.log("Kereső nem ad találatokat!");
			driver.close();
			System.exit(0);
			
		}
	

	wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
	fillName("name",secondCompany);
	submit();
	sleep(3000);

	try {
		
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//section//span/a)[1]")));
			String record2 = driver.findElement(By.xpath("(//section//span/a)[1]")).getText();
			
			if(secondCompany.equals(record2)) {
				
				Log.log("Név szerinti kereső teszt2: jó");
				
			}else {
				
				Log.log("Név szerinti kereső teszt2: HIBA!");
				driver.close();
				System.exit(0);
				
			}
		
		}catch(NoSuchElementException e) {
			
			Log.log("Kereső nem ad találatokat!");
			driver.close();
			System.exit(0);
			
		}
	
	clickLinkWithText("Cégkereső");
	wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
	driver.findElement(By.id("profiles")).click();
	sleep(2000);
	driver.findElement(By.id("profiles")).sendKeys(Keys.ARROW_DOWN);
	driver.findElement(By.id("profiles")).sendKeys(Keys.ARROW_DOWN);
	driver.findElement(By.id("profiles")).sendKeys(Keys.ARROW_DOWN);
	driver.findElement(By.id("profiles")).sendKeys(Keys.ARROW_DOWN);
	driver.findElement(By.id("profiles")).sendKeys(Keys.ARROW_DOWN);
	driver.findElement(By.id("profiles")).sendKeys(Keys.ARROW_DOWN);
	driver.findElement(By.id("profiles")).sendKeys(Keys.ARROW_DOWN);
	sleep(1000);
	driver.findElement(By.id("profiles")).sendKeys(Keys.ENTER);
	sleep(1000);
	Select tevKor = new Select(driver.findElement(By.id("profiles")));
	String tevKorValue = tevKor.getFirstSelectedOption().getText();
	submit();
	sleep(5000);
	
	try {
		
		firstResult = driver.findElement(By.xpath("(//section//span/a)[1]")).getText();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//*[contains(text(),'Tovább az adatlapra')])[1]"))).click();
	
	}catch(NoSuchElementException e) {
		
		Log.log("Kereső nem ad találatokat!");
		driver.close();
		System.exit(0);
		
	}
	
	onScreen(firstResult);
	onScreen(tevKorValue);
	
	}


    public static void CarTransmission() throws IOException, InterruptedException {
	
	
	String carPot = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='half-box'][2]/dd"))).getText();
	String make = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='half-box'][1]/dd"))).getText();
	String Cm3 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='half-box'][2]/dd"))).getText();
	String km = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//dd[4]/a[@class='d-block mb-2']"))).getText();
	sleep(2000);
	Log.log("Autó Átadás elindítása!");
	clickLinkWithText("Autóm eladása");
	fillName("buyer_email",companyUser);
	submit();
	
	sleep(2000);
	
	goToPage(url+"/hu/eladas-kerelmek-atadasok");
	String carName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='col-12 col-sm-6 col-lg'][1]/a"))).getText();
	driver.findElement(By.id("userMenu")).click();
	clickLinkWithText("Kijelentkezés");
	Log.log("Kijelentkezés!");
	sleep(2000);
	Log.log("Bejelentkezés!");
	TestBase.login(TestBase.companyUser, TestBase.companyPassword);
	click(".fas.fa-bell");
	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@class='notification-title']"))).click();
	onScreen(carName);
	Log.log("Autó Átvétel Elfogadása!");
	click(".fa.fa-check.circle");
	click(".btn-secondary");
	
	sleep(2000);
	
	goToPage(url+"/hu/garazs");
	driver.findElement(By.cssSelector(".overflow-hidden")).click();
	
	sleep(2000);
	
	
	Log.log("Autó Adatainak ellenőrzése!");
	onScreen(carPot);    
	onScreen(Cm3);
	onScreen(km);
	click(".fas.fa-long-arrow-alt-left");
	Log.log("Sikeres Autó Átvétel!");

    }


public static void companyRate() throws IOException, InterruptedException {

	clickLinkWithText("Cégkereső");
	wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
	Boolean goodFilter = false;
	Select profileSelect = new Select(driver.findElement(By.id("profiles")));
	Log.log(""+goodFilter);
	
	while(goodFilter == false) {
		
		int currentIndex = 1;
		profileSelect.selectByIndex(currentIndex);
		String currentProfile = profileSelect.getFirstSelectedOption().getText();
		submit();
		sleep(2000);
		
		try{
			
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//section//span/a)[1]")));
				clickLinkWithText("Tovább az adatlapra");
				goodFilter = true;
				sleep(4000);
				
			}catch(NoSuchElementException e) {
				
				Log.log("Nincs találat erre a tevékenységi körre: "+currentProfile);
				currentIndex++;
				
			}
		
	}
	
	boolean staleElement = true; 
	while(staleElement){
	  try{
	     driver.findElement(By.xpath("//*[contains(text(), 'részletes értékelés')]")).click();
	     staleElement = false;

	  } catch(StaleElementReferenceException e){
	    staleElement = true;
	  }
	}
	
	sleep(1000);
	
	driver.findElement(By.xpath("//div[@class='detailed-ratings row']/div[@class='col-4'][1]/div/div/div/div/span/span[3]")).click();
	driver.findElement(By.xpath("//div[@class='detailed-ratings row']/div[@class='col-4'][2]/div/div/div/div/span/span[3]")).click();
	driver.findElement(By.xpath("//div[@class='detailed-ratings row']/div[@class='col-4'][3]/div/div/div/div/span/span[3]")).click();
	driver.findElement(By.xpath("//div[@class='detailed-ratings row']/div[@class='col-4'][4]/div/div/div/div/span/span[3]")).click();
	driver.findElement(By.xpath("//div[@class='detailed-ratings row']/div[@class='col-4'][5]/div/div/div/div/span/span[3]")).click();
	sleep(1000);
	int rateTextNum = new Random().nextInt(500)+100;
	fillName("car_company_ratings[1][text_rate]","Teszt értékelő szöveg "+rateTextNum);
	submit();
	sleep(1000);
	
	try{
			
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'row mt-2')]//span[contains(@style,'width: 60%')]")));
			Log.log("Értékelés összegző helyes eredmény");
			
		}catch(NoSuchElementException e) {
			
			Log.log("Értékelés összegző hiba!");
			driver.close();
			System.exit(0);
			
		}
	
	}


public static void documentGenerator() throws IOException, InterruptedException {
	
	clickLinkWithText("Dokumentum generáló");
	sleep(3000);
	
	//vétel dokumentumok kiválasztása------------------------------------
	Log.log("vétel dokumentumok kiválasztása...");
	clickLinkWithText("Vétel");
	sleep(2000);
	driver.findElement(By.xpath("(//label[contains(text(),'Átadás-átvételi: Autó')])[2]")).click();
	driver.findElement(By.xpath("(//label[contains(text(),'Átadás-átvételi: Kellék')])[2]")).click();
	driver.findElement(By.xpath("(//label[contains(text(),'Átadás-átvételi: Vételár')])[2]")).click();
	driver.findElement(By.xpath("//label[contains(text(),'Bizományból kiadás')]")).click();
	driver.findElement(By.xpath("//label[contains(text(),'Bizományosi szerződés')]")).click();
	driver.findElement(By.xpath("(//label[contains(text(),'Foglaló')])[2]")).click();
	driver.findElement(By.xpath("(//label[contains(text(),'Meghatalmazás: Átírás')])[2]")).click();
	driver.findElement(By.xpath("(//label[contains(text(),'Meghatalmazás: Műszaki vizsga')])[2]")).click();
	driver.findElement(By.xpath("(//label[contains(text(),'Meghatalmazás: Regisztrációs adó')])[2]")).click();
	driver.findElement(By.xpath("(//label[contains(text(),'Átadás-átvételi: átírási ktg')])[2]")).click();
	driver.findElement(By.xpath("(//label[contains(text(),'Adásvételi szerződés')])[2]")).click();
	driver.findElement(By.xpath("(//label[contains(text(),'Állapotlap')])[2]")).click();
	submit();
	sleep(3000);
	Log.log("Minden vétel dokumentum kitöltése");
	//vétel dokumentumok kiválasztása-------------------------------------
	
	
	//Átadás-átvételi nyilatkozat----------------------------------------------------------------------------
	String mFacturer = driver.findElement(By.xpath("//div[@class='car-manufacturer']")).getText();
	String plateNum = driver.findElement(By.id("car-plate-number")).getAttribute("value");
	String carVin = driver.findElement(By.id("car-vin")).getAttribute("value");
	
	int randKeys = new Random().nextInt(5)+1;
	fillName("car_keys",""+randKeys);
	
	int randKomment = new Random().nextInt(500)+1;
	fillName("note","Teszt megjegyzés "+randKomment);
	
	clickLinkWithText("Partner Kiválasztása");
	sleep(2000);
	clickLinkWithText("Új partner felvétele");
	sleep(2000);
	
	//partner----------------------------------------------------
	Log.log("Partner felvétel...");
	fillName("last_name","TesztCsalád");
	fillName("first_name","TesztVezeték");
	fillName("personal_ident","123456AB");
	fillName("mothers_name","Partner Anyu");
	fillName("birth_date","1956-03-11");
	fillName("birth_place","Budapest");
	fillName("nationality","Magyar");
	fillName("email","test@email.com");
	fillName("phone","12345678");
	fillName("car_address[loc_zip_id_ac]","1052");
	sleep(3000);
	driver.findElement(By.id("car-address-loc-zip-id")).sendKeys(Keys.ENTER);
	sleep(3000);
	fillName("car_address[street]","Sas");
	driver.findElement(By.id("car-address-street-type")).click();
	sleep(1000);
	driver.findElement(By.id("car-address-street-type")).sendKeys(Keys.ARROW_DOWN);
	sleep(1000);
	driver.findElement(By.id("car-address-street-type")).sendKeys(Keys.ENTER);
	sleep(1000);
	fillName("car_address[street_num]","25");
	fillName("car_address[building]","A");
	fillName("car_address[floor]","2");
	fillName("car_address[door]","204");
	driver.findElement(By.xpath("//section//button[@type='submit']")).click();
	sleep(2000);
	//partner vége----------------------------------------------------
	
	String buyerName = driver.findElement(By.id("partner1-name")).getAttribute("value");
	String taxNum = driver.findElement(By.id("partner1-tax-no")).getAttribute("value");
	String regNum = driver.findElement(By.id("partner1-reg-no")).getAttribute("value");
	String address = driver.findElement(By.id("partner1-address")).getAttribute("value");
	
	fillName("sign_city_id_ac","Budapest");
	sleep(1000);
	driver.findElement(By.id("sign-city-id")).sendKeys(Keys.ENTER);
	sleep(1000);
	driver.findElement(By.id("sign-date")).click();
	driver.findElement(By.xpath("//form/div[4]/div[1]")).click();
	
	fillName("witness1_name","Tanú 1");
	fillName("witness1_personal_ident","234567CD");
	fillName("witness1_address","Repülőtéri út 6/a");
	fillName("witness2_name","Tanú 2");
	fillName("witness2_personal_ident","345678EF");
	fillName("witness2_address","Igazából ez bármi lehet");
	submit();
	
	}


public static void documentGeneratorErrorTest() throws IOException, InterruptedException {
	
	clickLinkWithText("Dokumentum generáló");
	sleep(3000);
	
	//vétel dokumentumok kiválasztása------------------------------------
	Log.log("vétel dokumentumok kiválasztása...");
	clickLinkWithText("Vétel");
	sleep(2000);
	driver.findElement(By.xpath("(//label[contains(text(),'Átadás-átvételi: Autó')])[2]")).click();
	driver.findElement(By.xpath("(//label[contains(text(),'Átadás-átvételi: Kellék')])[2]")).click();
	driver.findElement(By.xpath("(//label[contains(text(),'Átadás-átvételi: Vételár')])[2]")).click();
	driver.findElement(By.xpath("//label[contains(text(),'Bizományból kiadás')]")).click();
	driver.findElement(By.xpath("//label[contains(text(),'Bizományosi szerződés')]")).click();
	driver.findElement(By.xpath("(//label[contains(text(),'Foglaló')])[2]")).click();
	driver.findElement(By.xpath("(//label[contains(text(),'Meghatalmazás: Átírás')])[2]")).click();
	driver.findElement(By.xpath("(//label[contains(text(),'Meghatalmazás: Műszaki vizsga')])[2]")).click();
	driver.findElement(By.xpath("(//label[contains(text(),'Meghatalmazás: Regisztrációs adó')])[2]")).click();
	driver.findElement(By.xpath("(//label[contains(text(),'Átadás-átvételi: átírási ktg')])[2]")).click();
	driver.findElement(By.xpath("(//label[contains(text(),'Adásvételi szerződés')])[2]")).click();
	driver.findElement(By.xpath("(//label[contains(text(),'Állapotlap')])[2]")).click();
	submit();
	sleep(3000);
	Log.log("Minden vétel dokumentum kitöltése");
	//vétel dokumentumok kiválasztása-------------------------------------

	submit();
	Log.log("Hiba üzenetek ellenőrze");
	onScreen("Kötelező mező");
	onScreen("A mező nem lehet üres.");
	sleep(3000);
	//Átadás-átvételi nyilatkozat----------------------------------------------------------------------------
	String mFacturer = driver.findElement(By.xpath("//div[@class='car-manufacturer']")).getText();
	String plateNum = driver.findElement(By.id("car-plate-number")).getAttribute("value");
	String carVin = driver.findElement(By.id("car-vin")).getAttribute("value");
	clickLinkWithText("Partner Kiválasztása");
	sleep(2000);
	clickLinkWithText("Új partner felvétele");
	sleep(2000);
	
	//partner----------------------------------------------------
	Log.log("Partner felvétel...");
	driver.findElement(By.xpath("//section//button[@type='submit']")).click();
	onScreen("A mező nem lehet üres.");
	Log.log("Hiba Üzenetek Ellenőrzése!");
	sleep(10000);
	fillName("last_name","TesztCsalád");
	fillName("first_name","TesztVezeték");
	fillName("personal_ident","123456AB");
	fillName("mothers_name","Partner Anyu");
	fillName("birth_date","1956-03-11");
	fillName("birth_place","Budapest");
	fillName("nationality","Magyar");
	fillName("email","test@email.com");
	fillName("phone","12345678");
	fillName("car_address[loc_zip_id_ac]","1052");
	sleep(3000);
	driver.findElement(By.id("car-address-loc-zip-id")).sendKeys(Keys.ENTER);
	sleep(3000);
	fillName("car_address[street]","Sas");
	driver.findElement(By.id("car-address-street-type")).click();
	sleep(1000);
	driver.findElement(By.id("car-address-street-type")).sendKeys(Keys.ARROW_DOWN);
	sleep(1000);
	driver.findElement(By.id("car-address-street-type")).sendKeys(Keys.ENTER);
	sleep(1000);
	fillName("car_address[street_num]","25");
	fillName("car_address[building]","A");
	fillName("car_address[floor]","2");
	fillName("car_address[door]","204");
	driver.findElement(By.xpath("//section//button[@type='submit']")).click();
	sleep(2000);
	//partner vége----------------------------------------------------
	
	String buyerName = driver.findElement(By.id("partner1-name")).getAttribute("value");
	String taxNum = driver.findElement(By.id("partner1-tax-no")).getAttribute("value");
	String regNum = driver.findElement(By.id("partner1-reg-no")).getAttribute("value");
	String address = driver.findElement(By.id("partner1-address")).getAttribute("value");
	
	fillName("sign_city_id_ac","Budapest");
	sleep(1000);
	driver.findElement(By.id("sign-city-id")).sendKeys(Keys.ENTER);
	sleep(1000);
	driver.findElement(By.id("sign-date")).click();
	driver.findElement(By.xpath("//form/div[4]/div[1]")).click();
	
	fillName("witness1_name","Tanú 1");
	fillName("witness1_personal_ident","234567CD");
	fillName("witness1_address","Repülőtéri út 6/a");
	fillName("witness2_name","Tanú 2");
	fillName("witness2_personal_ident","345678EF");
	fillName("witness2_address","Igazából ez bármi lehet");
	submit();
	
	}



public static void setNewRSSChannel()  throws IOException, InterruptedException {
	
	
	
	
	}


public static void checkRSSChannel()  throws IOException, InterruptedException {
	
	
	
	
	}


public static void deleteTestRSSChannel()  throws IOException, InterruptedException {
	
	
	
	
	}


}