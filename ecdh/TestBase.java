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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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
	public static String urlLive;
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
	public static String publicPart;
	public static String fullClearUser;

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
		urlLive = prop.getProperty("url2");
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

	protected static void deleteUser() throws Exception, IOException, InterruptedException, TimeoutException {
		sleep(5000);
		passShepherd();

		click(".user-img");
		clickLinkWithText("Adatmódosítás");
		clickLinkWithText("Fiók törlése");

		try {

			click(".btn-red");

		} catch (TimeoutException e) {

			driver.findElement(By.xpath("//div[@id='popup-content']//a[contains(text(), 'Garázs')]")).click();
			sleep(3000);
			deleteUserCars();
			sleep(2000);
			click(".user-img");
			clickLinkWithText("Adatmódosítás");
			clickLinkWithText("Fiók törlése");
			click(".btn-red");

		}

		Log.log("Felhasználó törlése.");
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//p[contains(text(), 'Sikeres törlés email küldés!')]")));
		Log.log("Törlés email elküldve");
		deleteUserInEmail();

	}

	protected static void deleteUserInEmail() throws Exception {

		sleep(2000);
		driver.get("https://mail.ecdh.hu/hpronto/");
		Log.log("Open Pronto");
		sleep(4000);
		driver.findElement(By.cssSelector("input[type=text]")).sendKeys(testerMail);
		Log.log("Fill username");
		driver.findElement(By.cssSelector("input[type=password]")).sendKeys(testerPassword);
		Log.log("Fill password");
		sleep(2000);
		clickXpath("//input[@type='submit']");
		Log.log("Login Pronto");

		sleep(6000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@title='ECDH test']")));
		
		sleep(3000);
		
		driver.switchTo().frame(driver.findElements(By.tagName("iframe")).get(1));
		new WebDriverWait(driver, 20).until( ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Fiók végleges törlése')]"))).click();

		sleep(3000);

		for (String winHandle : driver.getWindowHandles()) {
			System.out.println(winHandle);
			driver.switchTo().window(winHandle);
		}

		onScreenAlert("Felhasználó törölve");

		Log.log("Fiók végleges törlése");

	}

	protected static void activateUser() throws Exception {

		sleep(2000);
		driver.get("https://mail.ecdh.hu/hpronto/");
		Log.log("Open Pronto");
		sleep(4000);
		driver.findElement(By.cssSelector("input[type=text]")).sendKeys(testerMail);
		Log.log("Fill username");
		driver.findElement(By.cssSelector("input[type=password]")).sendKeys(testerPassword);
		Log.log("Fill password");
		sleep(2000);
		clickXpath("//input[@type='submit']");
		Log.log("Login Pronto");

		sleep(6000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@title='ECDH test']")));
		
		sleep(3000);
		
		driver.switchTo().frame(driver.findElements(By.tagName("iframe")).get(1));
		new WebDriverWait(driver, 20).until( ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Személyes fiók aktiválása')]"))).click();
		
		Log.log("New user account activation");
		sleep(5000);

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

	public static void fillName(String name, String text) throws IOException, InterruptedException {
		
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
		sleep(1000);
	}

	protected static void registerUser(String user, String password, Boolean obligatory)
			throws IOException, InterruptedException {
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

	protected static void registerUser(String username, String password) throws IOException, InterruptedException {
		
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

	public static void login(String username, String password) throws IOException, InterruptedException {
		
		clickLinkWithText("Belépés");
		Log.log("Click login");
		sleep(3000);

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
	
	public static void selectValue(String inputName, String value) throws IOException, InterruptedException {
		
		Select selectInput = new Select(driver.findElement(By.name(inputName)));
		sleep(3000);
		selectInput.selectByValue(value);
		
	}

	public static void selectIndex(String string, int i) throws IOException {
		WebDriverWait wait = new WebDriverWait(driver, 15);
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("select[name='" + string + "']")));

		WebElement mySelectElement = driver.findElement(By.cssSelector("select[name='" + string + "']"));
		Select dropdown = new Select(mySelectElement);
		dropdown.selectByIndex(i);
		Log.log("Index " + i + " selected from " + string);

	}
	
	public static void multiSelectByXpath(String xpath) throws IOException, InterruptedException {
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		driver.findElement(By.xpath(xpath)).click();
		sleep(3000);
		List<WebElement> options = driver.findElements(By.xpath("//ul[@class='multiselect-container dropdown-menu show']/li"));
		Random rand = new Random();
		int list = rand.nextInt(options.size());
		//Log.log(""+options.size());
		//Log.log(""+list);
		
		options.get(list).click();

		sleep(2000);
		
	}
	
	public static void clickCheckboxById(String id) throws IOException, InterruptedException {
		
		String xpath = "//input[@type='checkbox' and @id='" + id + "']";
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		driver.findElement(By.xpath(xpath)).click();
		sleep(3000);
		
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

		/*
		 * wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
		 * "//*[@value='" + "HU" + "']"))); System.out.println("Magyarország");
		 * assertTrue("Szerepel a forrásban", driver.getPageSource().contains("HU"));
		 * Log.log("Képernyőn: " + "Magyarország");
		 */

		try {

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

			String mail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='email']")))
					.getText();

			System.out.println(mail);
			assertTrue("Szerepel a forrásban", driver.getPageSource().contains(mail));
			Log.log("Képernyőn: " + mail);
		}

		catch (NoSuchElementException e) {

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

			String mail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='email']")))
					.getText();

			System.out.println(mail);
			assertTrue("Szerepel a forrásban", driver.getPageSource().contains(mail));
			Log.log("Képernyőn: " + mail);

		}

		submit();
		sleep(2000);
		Log.log("Tovább a fizetéshez");

		driver.findElement(By.id("actionPay")).click();
		sleep(2000);
		Log.log("Fizetés");
		
		onScreenWS("A kártyás fizetést fogadtuk!");
		
		String orderNumTrash = driver.findElement(By.xpath("//a[contains(text(), 'megrendeléseid')]")).getText();

		sleep(5000);
		driver.findElement(By.cssSelector(".btn.btn-lg.btn-primary.btn-block.btn-success")).click();
		sleep(2000);
		Log.log("Siker");
		
		String orderNum = orderNumTrash.replaceAll("[^0-9]", "");
		
		goToPage(url + "/hu/car-service-orders/ipn-success-test/" + orderNum);
		sleep(3000);
		
		goToPage(url + "/hu/vasarlasok");
		sleep(3000);
		
		onScreenWS("Autó limit 2-5");
		
		Log.log("Sikeres autólimit bővítés!");

	}

	public static void AddCarSync() throws IOException, InterruptedException, ParserConfigurationException,
			SAXException, XPathExpressionException {

		int rand = new Random().nextInt(500) + 500;
		String randUser = "Felhasználó " + rand;
		rand = new Random().nextInt(50) + 10;
		int randLimit = rand;

		try {

			driver.findElement(By.xpath("(//a[contains(text(), 'Beállít')])[1]")).click();

			try {

				Log.log("Adatmezők kitöltése nélküli mentés ellenőrzés...");
				sleep(2000);
				driver.findElement(By.id("form-button")).click();
				sleep(2000);
				driver.findElement(By.className("error-message"));

			} catch (NoSuchElementException e) {

				Log.log("Nem jelenik meg a kötelező mező hibaüzenet!");
				driver.close();
				System.exit(0);

			}

			Log.log("Adatmezők kitöltése...");
			sleep(2000);
			driver.findElement(By.name("username")).sendKeys(randUser);
			fillName("car_limit", "" + randLimit);
			driver.findElement(By.id("form-button")).click();
			sleep(2000);
			Log.log("Adatmezők kitöltve, szinkron elindítva!");

		} catch (NoSuchElementException e) {

			Log.log("A szinkron már folyamatban");
			sleep(3000);
			driver.findElement(
					By.xpath("(//div[@id='toggle-partner-sync-block']//a[contains(text(), 'Megtekint')])[1]")).click();
			sleep(2000);
			clickLinkWithText("Módosítás");
			sleep(2000);
			fillName("username", randUser);
			fillName("car_limit", "" + randLimit);
			driver.findElement(By.id("form-button")).click();
			sleep(2000);
			Log.log("Átírva megfogható adatokra");

		}

		driver.findElement(By.xpath("(//div[@id='toggle-partner-sync-block']//a[contains(text(), 'Megtekint')])[1]"))
				.click();
		sleep(2000);
		Log.log("Megtekint ellenőrzése...");

		sleep(3000);

		// wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='blue-bg
		// p-3']//text()[contains(.,'"+randLimit+"')]")));

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document doc = documentBuilder.parse("src/cricketTeam_info.xml");

		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();

		XPathExpression expr = xpath
				.compile("substring(//div[@class='blue-bg p-3']//text()[contains(.,'" + randLimit + "')], 26, 2)");
		String substr = (String) expr.evaluate(doc, XPathConstants.STRING);

		String strLimit = "" + randLimit;

		Log.log(strLimit);
		Log.log(substr);

		if (substr.equals(randLimit)) {
			Log.log("Képernyőn: " + randLimit);
		} else {
			Log.log("nem található a limit");
		}

		clickLinkWithText("Módosítás");
		sleep(2000);
		Log.log("Módosítás ellenőrzése...");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//section//input[@value='" + randUser + "']")));
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("" + randUser));
		Log.log("Képernyőn: " + randUser);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//section//input[@value='" + randLimit + "']")));
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("" + randLimit));
		Log.log("Képernyőn: " + randLimit);

		rand = new Random().nextInt(500) + 500;
		randUser = "Felhasználó " + rand;
		rand = new Random().nextInt(50) + 1;
		randLimit = rand;

		fillName("username", randUser);
		fillName("car_limit", "" + randLimit);
		driver.findElement(By.id("form-button")).click();
		sleep(2000);
		Log.log("Adatok módosítva");

		driver.findElement(By.xpath("(//div[@id='toggle-partner-sync-block']//a[contains(text(), 'Megtekint')])[1]"))
				.click();
		sleep(2000);
		Log.log("Szerkesztett megtekint ellenőrzése...");

		// wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//section/div/div/div/div/div[contains(text(),'"+randLimit+"')]")));

		expr = xpath.compile("substring(//div[@class='blue-bg p-3']//text()[contains(.,'" + randLimit + "')], 26, 2)");
		substr = (String) expr.evaluate(doc, XPathConstants.STRING);

		strLimit = "" + randLimit;

		if (substr.equals(randLimit)) {
			Log.log("Képernyőn: " + randLimit);
		} else {
			Log.log("nem található a limit");
		}

		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("" + randLimit));
		Log.log("Képernyőn: " + randLimit);

		clickLinkWithText("Módosítás");
		sleep(2000);
		Log.log("Szerkesztett módosítás ellenőrzése...");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//section//input[@value='" + randUser + "']")));
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("" + randUser));
		Log.log("Képernyőn: " + randUser);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//section//input[@value='" + randLimit + "']")));

		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("" + randLimit));
		Log.log("Képernyőn: " + randLimit);

		driver.findElement(By.id("form-button")).click();
		sleep(2000);
		Log.log("Sikeres teszt");

		// admin interakció kell majd, ha működő képes, akkor írható tovább a teszt

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
		driver.findElement(By.cssSelector("input[name='main_company[last_name]']")).sendKeys("Mr");
		driver.findElement(By.cssSelector("input[name='main_company[first_name]']")).sendKeys("Tester");
		Log.log("cégvezető neve kitöltés");
		driver.findElement(By.className("register")).click();
		Log.log("Regisztráció mentése");

	}

	public static void activateCompany(Boolean realActivation, String companyEmail) throws Exception {
		
			sleep(2000);
			driver.get("https://mail.ecdh.hu/hpronto/");
			Log.log("Open Pronto");
			sleep(4000);
			driver.findElement(By.cssSelector("input[type=text]")).sendKeys(testerMail);
			Log.log("Fill username");
			driver.findElement(By.cssSelector("input[type=password]")).sendKeys(testerPassword);
			Log.log("Fill password");
			sleep(2000);
			clickXpath("//input[@type='submit']");
			Log.log("Login Pronto");

			sleep(6000);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@title='ECDH test']")));
			
			sleep(3000);
			
			driver.switchTo().frame(driver.findElements(By.tagName("iframe")).get(1));
			new WebDriverWait(driver, 20).until( ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Céges fiók létrehozása')]"))).click();
			

			Log.log("New user account activation");

			System.out.println(driver.getTitle());

			for (String winHandle : driver.getWindowHandles()) {
				System.out.println(winHandle);
				driver.switchTo().window(winHandle);
			}

			System.out.println(driver.getTitle());
			sleep(2000);
			passShepherd();
			sleep(2000);
			passShepherd();
			sleep(2000);
			passShepherd();
			sleep(2000);
			passShepherd();
			sleep(2000);
			Log.log("Activation succeed");
			
			goToPage(url + "/hu/kijelentkezes");
			sleep(3000);
			onScreenAlert("Sikeres kijelentkezés!");
			
			Log.log("Kijelentkezés");
			
	}


	public static void forgottenPassword() throws Exception {
		
		driver.findElement(By.partialLinkText("Elfelejtetted")).click();
		driver.findElement(By.cssSelector("input[name='email_check']")).sendKeys(personalUser);
		sleep(1000);
		driver.findElement(By.className("btn-success")).click();
		sleep(1000);
		
		sleep(2000);
		driver.get("https://mail.ecdh.hu/hpronto/");
		Log.log("Open Pronto");
		sleep(4000);
		driver.findElement(By.cssSelector("input[type=text]")).sendKeys(testerMail);
		Log.log("Fill username");
		driver.findElement(By.cssSelector("input[type=password]")).sendKeys(testerPassword);
		Log.log("Fill password");
		sleep(2000);
		clickXpath("//input[@type='submit']");
		Log.log("Login Pronto");

		sleep(6000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@title='ECDH test']")));
		
		sleep(3000);
		
		driver.switchTo().frame(driver.findElements(By.tagName("iframe")).get(1));
		new WebDriverWait(driver, 20).until( ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Jelszóváltás')]"))).click();

		sleep(3000);
		
		Log.log("Jelszóváltás");
		sleep(5000);

		System.out.println(driver.getTitle());

		for (String winHandle : driver.getWindowHandles()) {
			System.out.println(winHandle);
			driver.switchTo().window(winHandle);
		}

		System.out.println(driver.getTitle());

		fillName("password", personalPassword);
		fillName("confirm_password", personalPassword);
		sleep(1000);
		submit();
		sleep(3000);
		driver.findElement(By.xpath("//a[@class='btn btn-lg btn-primary btn-block btn-success']")).click();
		sleep(2000);
		
		goToPage(url);
		sleep(2000);

		login(personalUser, personalPassword);
		Log.log("Sikeres jelszó módosítás");

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

	public static String generatePlateNumberForeignCountry() {
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

		String licensePlate = letters + digits;
		return licensePlate;
	}

	public static void addNewCar() throws IOException, InterruptedException, AWTException {
		TestBase.goToPage(TestBase.url + "/hu/sajat-autom-felvitel");
		sleep(3000);

		Random rand = new Random();
		String carYear;

		try {

			carYear = randomSelect("car_year");

		} catch (TimeoutException e) {

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(), 'Saját autó limit')]")));
			// String text = driver.findElement(By.xpath("//h1[contains(text(), 'Saját autó
			// limit')]")).getText();
			goToPage(TestBase.url + "/hu/garazs");
			sleep(3000);
			deleteUserCars();
			sleep(5000);
			goToPage(TestBase.url + "/hu/sajat-autom-felvitel");
			sleep(5000);
			rand = new Random();
			carYear = randomSelect("car_year");

		}
		String carMonth = randomSelect("car_month");
		sleep(2000);

		// Random rand = new Random();
		// String carYear = randomSelect("car_year");

		/*
		 * manufacturer = fillCarField("#car-manufacturer-id", "#ui-id-1"); sleep(2000);
		 */

		try {

			// driver.findElement(By.xpath("//*[contains(text(), 'Egyedi
			// gyártó')]")).click();

			manufacturer = fillCarField("#car-manufacturer-id", "#ui-id-1");
			sleep(2000);

			try {

				model = fillCarField("#car-model-id", "#ui-id-2");
				sleep(2000);

				// driver.findElement(By.xpath("//*[contains(text(), 'Egyedi
				// modell')]")).click();

			} catch (ElementNotVisibleException e) {

				driver.findElement(By.xpath("(//a[@class='custom-combobox-toggle'])[2]")).click();
				driver.findElement(By.xpath("//div[@id='carModelBlock']//a[@id='ui-id-extra ui-menu-item']")).click();
				sleep(2000);

				fillName("car_model_custom", "Test Modell");
				fillName("car_type_custom", "Test Típus");

				model = "Test Modell";

			}

		} catch (ElementNotVisibleException e) {

			driver.findElement(By.xpath("(//a[@class='custom-combobox-toggle'])[1]")).click();
			driver.findElement(By.xpath("//div[@id='carManufacturerBlock']//a[@id='ui-id-extra ui-menu-item']"))
					.click();
			sleep(2000);

			fillName("car_manufacturer_custom", "Test Gyártó");
			fillName("car_model_custom", "Test Modell");
			fillName("car_type_custom", "Test Típus");
			manufacturer = "Test Gyártó";
			model = "Test Modell";

		}

		fillName("numberplate", "abc123");
		int kmNumberInt = rand.nextInt(998999) + 10000;
		fillName("km", "" + kmNumberInt);

		click(".btn-secondary");
		sleep(3000);
		onScreenAlert("Az autót sikeresen elmentette");
		passShepherd();
		sleep(1000);
		passShepherd();
		sleep(1000);
		passShepherd();
		sleep(1000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@class='breadcrumb-item'][2]/span")));
		// wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(),
		// '" + manufacturer + "')]")));
		System.out.println(manufacturer);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(manufacturer));
		Log.log("Képernyőn: " + manufacturer);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@class='breadcrumb-item'][2]/span")));
		// wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(),
		// '" + model + "')]")));
		System.out.println(model);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(model));
		Log.log("Képernyőn: " + model);

		onScreen(carYear);
		onScreen(carMonth);
		onScreen("ABC-123");
		checkPrice(kmNumberInt, " ");
		passShepherd();

	}

	public static void addNewCarOtherCountryTest()
			throws IOException, InterruptedException, AWTException, TimeoutException, ElementNotVisibleException {

		TestBase.goToPage(TestBase.url + "/hu/sajat-autom-felvitel");
		sleep(3000);

		Random rand = new Random();
		String carYear;

		try {

			carYear = randomSelect("car_year");

		} catch (TimeoutException e) {

			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//h1[contains(text(), 'Saját autó limit')]")));
			// String text = driver.findElement(By.xpath("//h1[contains(text(), 'Saját autó
			// limit')]")).getText();
			goToPage(TestBase.url + "/hu/garazs");
			sleep(3000);
			deleteUserCars();
			sleep(5000);
			goToPage(TestBase.url + "/hu/sajat-autom-felvitel");
			sleep(5000);
			rand = new Random();
			carYear = randomSelect("car_year");

		}
		String carMonth = randomSelect("car_month");
		sleep(2000);

		// Random rand = new Random();
		// String carYear = randomSelect("car_year");

		/*
		 * manufacturer = fillCarField("#car-manufacturer-id", "#ui-id-1"); sleep(2000);
		 */

		try {

			// driver.findElement(By.xpath("//*[contains(text(), 'Egyedi
			// gyártó')]")).click();

			manufacturer = fillCarField("#car-manufacturer-id", "#ui-id-1");
			sleep(2000);

			try {

				model = fillCarField("#car-model-id", "#ui-id-2");
				sleep(2000);

				// driver.findElement(By.xpath("//*[contains(text(), 'Egyedi
				// modell')]")).click();

			} catch (ElementNotVisibleException e) {

				driver.findElement(By.xpath("(//a[@class='custom-combobox-toggle'])[2]")).click();
				driver.findElement(By.xpath("//div[@id='carModelBlock']//a[@id='ui-id-extra ui-menu-item']")).click();
				sleep(2000);

				fillName("car_model_custom", "Test Modell");
				fillName("car_type_custom", "Test Típus");

				model = "Test Modell";

			}

		} catch (ElementNotVisibleException e) {

			driver.findElement(By.xpath("(//a[@class='custom-combobox-toggle'])[1]")).click();
			driver.findElement(By.xpath("//div[@id='carManufacturerBlock']//a[@id='ui-id-extra ui-menu-item']"))
					.click();
			sleep(2000);

			fillName("car_manufacturer_custom", "Test Gyártó");
			fillName("car_model_custom", "Test Modell");
			fillName("car_type_custom", "Test Típus");
			manufacturer = "Test Gyártó";
			model = "Test Modell";

		}

		/*
		 * click("#car-type-id"); sleep(5000);
		 */

		/*
		 * try {
		 * 
		 * //driver.findElement(By.xpath("//*[contains(text(), 'Egyedi típus')]")).click
		 * ();
		 * 
		 * click("#car-type-id"); sleep(5000);
		 * 
		 * //fillName("car_type_custom","Test Típus");
		 * 
		 * }catch(ElementNotVisibleException e) {
		 * 
		 * driver.findElement(By.xpath("(//a[@class='custom-combobox-toggle'])[3]")).
		 * click();
		 * driver.findElement(By.cssSelector("#car-type-id #ui-id-extra")).click();
		 * sleep(2000);
		 * 
		 * fillName("car_type_custom","Test Típus"); sleep(2000);
		 * 
		 * }
		 */

		String NumberPlate = generatePlateNumberForeignCountry();
		fillName("numberplate", "" + NumberPlate);
		driver.findElement(By.name("numberplate_country")).click();
		Select orszag = new Select(driver.findElement(By.name("numberplate_country")));
		orszag.selectByVisibleText("A - Ausztria");
		int kmNumberInt = rand.nextInt(998999) + 10000;
		fillName("km", "" + kmNumberInt);

		click(".btn-secondary");
		sleep(3000);
		passShepherd();
		sleep(1000);
		passShepherd();
		sleep(1000);
		passShepherd();
		sleep(1000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@class='breadcrumb-item'][2]/span")));
		// wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(),
		// '" + manufacturer + "')]")));
		System.out.println(manufacturer);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(manufacturer));
		Log.log("Képernyőn: " + manufacturer);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@class='breadcrumb-item'][2]/span")));
		// wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(),
		// '" + model + "')]")));
		System.out.println(model);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(model));
		Log.log("Képernyőn: " + model);

		onScreen(carYear);
		onScreen(carMonth);
		onScreen(NumberPlate);
		checkPrice(kmNumberInt, " ");

	}

	public static void addNewCarError() throws IOException, InterruptedException, AWTException {

		TestBase.goToPage(TestBase.url + "/hu/sajat-autom-felvitel");
		sleep(3000);

		Random rand = new Random();
		String carYear;

		try {

			carYear = randomSelect("car_year");

		} catch (TimeoutException e) {

			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//h1[contains(text(), 'Saját autó limit')]")));
			// String text = driver.findElement(By.xpath("//h1[contains(text(), 'Saját autó
			// limit')]")).getText();
			goToPage(TestBase.url + "/hu/garazs");
			sleep(3000);
			deleteUserCars();
			sleep(5000);
			goToPage(TestBase.url + "/hu/sajat-autom-felvitel");
			sleep(5000);
			rand = new Random();
			carYear = randomSelect("car_year");

		}
		String carMonth = randomSelect("car_month");
		sleep(2000);

		try {

			manufacturer = fillCarField("#car-manufacturer-id", "#ui-id-1");
			sleep(2000);

			try {

				model = fillCarField("#car-model-id", "#ui-id-2");
				sleep(2000);

				// driver.findElement(By.xpath("//*[contains(text(), 'Egyedi
				// modell')]")).click();

			} catch (ElementNotVisibleException e) {

				driver.findElement(By.xpath("(//a[@class='custom-combobox-toggle'])[2]")).click();
				driver.findElement(By.xpath("//div[@id='carModelBlock']//a[@id='ui-id-extra ui-menu-item']")).click();
				sleep(2000);

				fillName("car_model_custom", "Test Modell");
				fillName("car_type_custom", "Test Típus");

				model = "Test Modell";

			}

		} catch (ElementNotVisibleException e) {

			driver.findElement(By.xpath("(//a[@class='custom-combobox-toggle'])[1]")).click();
			driver.findElement(By.xpath("//div[@id='carManufacturerBlock']//a[@id='ui-id-extra ui-menu-item']"))
					.click();
			sleep(2000);

			fillName("car_manufacturer_custom", "Test Gyártó");
			fillName("car_model_custom", "Test Modell");
			fillName("car_type_custom", "Test Típus");
			manufacturer = "Test Gyártó";
			model = "Test Modell";

		}

		sleep(5000);

		String NumberPlate = generatePlateNumber();
		fillName("numberplate", "111111");
		int kmNumberInt = rand.nextInt(998999) + 10000;
		fillName("km", "" + kmNumberInt);

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

		String engineNumber = "";
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

		String randomNumStr2 = "";
		randomNum = rand.nextInt((899999) + 100000);
		String registrationNumber = randomNum.toString();
		randomNumStr2 = String.valueOf(registrationNumber) + 'A';
		fillName("registration_number", randomNumStr2);

		randomNum = rand.nextInt(70) + 10;
		String fuelCapacity = randomNum.toString();
		fillName("fuel_capacity", fuelCapacity);

		sleep(3000);
		driver.findElement(By.name("doors")).click();
		sleep(3000);
		// driver.findElement(By.name("doors")).clear();
		// sleep(3000);
		randomNum = rand.nextInt(3) + 2;
		String doors = randomNum.toString();
		Log.log(doors + " ajtó szám");
		// Log.log(randomNum+" ajtó szám");
		sleep(3000);
		// fillName("doors", doors);
		// fillName("doors", ""+randomNum);
		driver.findElement(By.cssSelector("input[name='doors']")).clear();
		driver.findElement(By.cssSelector("input[name='doors']")).sendKeys(Keys.BACK_SPACE);
		driver.findElement(By.cssSelector("input[name='doors']")).sendKeys(doors);
		sleep(3000);

		randomNum = rand.nextInt(4) + 1;
		String seats = randomNum.toString();
		fillName("seats", seats);
		sleep(3000);

		String Cylinder = "";
		String make = "";
		String gearType = "";
		String carOffset = "";
		String warranty = "";
		String enviromental_V9 = "";
		make = randomSelect("make");
		gearType = randomSelect("gear_type");
		carOffset = randomSelect("car_offset");
		Cylinder = randomSelect("cylinder");
		warranty = randomSelect("warranty");
		enviromental_V9 = randomSelect("enviromental_v9");

		String max_Load = "";
		randomNum = rand.nextInt(200) + 100;
		max_Load = randomNum.toString();
		fillName("max_load", max_Load);

		String trunk = "";
		randomNum = rand.nextInt(200) + 100;
		trunk = randomNum.toString();
		fillName("trunk", trunk);

		int engine_capacity = 0;
		randomNum = rand.nextInt(2000) + 1000;
		engine_capacity = rand.nextInt(999) + 1000;
		fillName("engine_capacity", "" + engine_capacity);

		String netWeight = "";
		randomNum = rand.nextInt(3200) + 100;
		netWeight = randomNum.toString();
		fillName("net_weight", netWeight);

		String weight = "";
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
		String currentValue1 = "";
		String currentValue2 = "";
		String currentValue3 = "";
		String currentValue4 = "";
		String currentValue5 = "";
		String currentValue6 = "";

		elements = driver.findElements(By.tagName("select"));
		for (WebElement element : elements) {
			String name = element.getAttribute("name");
			randomSelect(name);

			Select test = new Select(driver.findElement(By.name(name)));

			selectors = "" + selInt;

			switch (selectors) {

			case "1":
				currentValue1 = test.getFirstSelectedOption().getText();
				break;
			case "2":
				currentValue2 = test.getFirstSelectedOption().getText();
				break;
			case "3":
				currentValue3 = test.getFirstSelectedOption().getText();
				break;
			case "4":
				currentValue4 = test.getFirstSelectedOption().getText();
				break;
			case "5":
				currentValue5 = test.getFirstSelectedOption().getText();
				break;
			case "6":
				currentValue6 = test.getFirstSelectedOption().getText();
				break;

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

		wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//div[@class = 'half-box'][1]/dd[contains(text(), '" + seats + "')]")));
		System.out.println(seats);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(seats));
		Log.log("Képernyőn: " + seats + "Ülés");
		sleep(4000);

		wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("(//div[@class = 'half-box'][2]/dd[contains(text(),'" + doors + "')])[1]")));
		System.out.println(doors);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(doors));
		Log.log("Képernyőn: " + doors + "Ajtó");
		sleep(4000);

		onScreen("Dízel");
		checkPrice(engine_capacity, " ");
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
		onScreenValue("" + engine_capacity);
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

		/*
		 * 
		 * 
		 * clickLinkWithText("Adatok szerkesztése"); TestBase.select("petrol", "Dízel");
		 * randomSelect("car_condition");
		 * 
		 * rand = new Random(); leftLimit = 11111111111111111L; rightLimit =
		 * 99999999999999999L; randomLong = leftLimit + (long) (Math.random() *
		 * (rightLimit - leftLimit));
		 * 
		 * vin = String.valueOf(randomLong); fillName("vin", vin);
		 * 
		 * engineNumber =""; randomNum = rand.nextInt(999999999); engineNumber =
		 * randomNum.toString(); fillName("motor_number", engineNumber);
		 * 
		 * randomNum = rand.nextInt(199); power = randomNum.toString();
		 * fillName("power", power);
		 * 
		 * randomNum = rand.nextInt((899999) + 100000); trafficLicense =
		 * randomNum.toString(); randomNumSt = String.valueOf(trafficLicense) + "AB";
		 * fillName("traffic_license", randomNumSt);
		 * 
		 * 
		 * randomNumStr2 =""; randomNum = rand.nextInt((899999) + 10000);
		 * registrationNumber = randomNum.toString(); randomNumStr2 =
		 * String.valueOf(registrationNumber) + 'A'; fillName("registration_number",
		 * randomNumStr2);
		 * 
		 * randomNum = rand.nextInt(70)+10; fuelCapacity = randomNum.toString();
		 * fillName("fuel_capacity", fuelCapacity);
		 * 
		 * 
		 * driver.findElement(By.name("doors")).click();
		 * driver.findElement(By.name("doors")).clear(); sleep(2000); randomNum =
		 * rand.nextInt(3)+3; doors = randomNum.toString(); fillName("doors",doors);
		 * 
		 * 
		 * randomNum = rand.nextInt(4) + 1; seats = randomNum.toString();
		 * fillName("seats",seats);
		 * 
		 * Cylinder=""; make =""; gearType=""; carOffset=""; warranty="";
		 * enviromental_V9=""; make = randomSelect("make"); gearType =
		 * randomSelect("gear_type"); carOffset =randomSelect("car_offset"); Cylinder
		 * =randomSelect("cylinder"); warranty = randomSelect("warranty");
		 * enviromental_V9 = randomSelect("enviromental_v9");
		 * 
		 * 
		 * max_Load=""; randomNum = rand.nextInt(200) + 100; max_Load =
		 * randomNum.toString(); fillName("max_load", max_Load);
		 * 
		 * trunk =""; randomNum = rand.nextInt(200) + 100; trunk = randomNum.toString();
		 * fillName("trunk", trunk);
		 * 
		 * engine_capacity =0; randomNum = rand.nextInt(2000) + 1000; engine_capacity =
		 * rand.nextInt(9989)+1000; fillName("engine_capacity",""+engine_capacity);
		 * 
		 * netWeight =""; randomNum = rand.nextInt(3200) + 100; netWeight =
		 * randomNum.toString(); fillName("net_weight", netWeight);
		 * 
		 * weight=""; randomNum += 100; weight = randomNum.toString();
		 * fillName("weight", weight);
		 * 
		 * click(".btn-secondary"); Thread.sleep(3000); click(".btn-secondary");
		 * Thread.sleep(3000);
		 * 
		 * elements = driver.findElements(By.className("collapsed")); for (WebElement
		 * element : elements) { String name = element.getAttribute("data-target");
		 * Log.log(name + " collapsed"); element.click(); Thread.sleep(1500); }
		 * 
		 * selectors = "1"; selInt = 1; currentValue1=""; currentValue2="";
		 * currentValue3=""; currentValue4=""; currentValue5=""; currentValue6="";
		 * 
		 * 
		 * elements = driver.findElements(By.tagName("select")); for (WebElement element
		 * : elements ) { String name = element.getAttribute("name");
		 * randomSelect(name);
		 * 
		 * Select test = new Select(driver.findElement(By.name(name)));
		 * 
		 * selectors = "" + selInt;
		 * 
		 * switch(selectors) {
		 * 
		 * case "1" : currentValue1 = test.getFirstSelectedOption().getText();break;
		 * case "2" : currentValue2 = test.getFirstSelectedOption().getText();break;
		 * case "3" : currentValue3 = test.getFirstSelectedOption().getText();break;
		 * case "4" : currentValue4 = test.getFirstSelectedOption().getText();break;
		 * case "5" : currentValue5 = test.getFirstSelectedOption().getText();break;
		 * case "6" : currentValue6 = test.getFirstSelectedOption().getText();break;
		 * 
		 * }
		 * 
		 * selInt++;
		 * 
		 * }
		 * 
		 * Log.log(currentValue1); Log.log(currentValue2); Log.log(currentValue3);
		 * Log.log(currentValue4); Log.log(currentValue5); Log.log(currentValue6);
		 * 
		 * elements = driver.findElements(By.cssSelector(".checkbox label")); for
		 * (WebElement element : elements) { rand = new Random(); randomNum =
		 * rand.nextInt(2); if (randomNum == 0) { element.click(); } }
		 * 
		 * driver.findElement(By.id("save-and-back")).click(); Thread.sleep(3000);
		 * 
		 * Log.log("Autó beküldve."); onScreen(vin); onScreen(engineNumber);
		 * onScreen(registrationNumber); onScreen(trafficLicense);
		 * 
		 * wait.until( ExpectedConditions.visibilityOfElementLocated(By.
		 * xpath("//div[@class = 'half-box'][1]/dd[contains(text(), '"+ seats +"')]")));
		 * System.out.println(seats); assertTrue("Szerepel a forrásban",
		 * driver.getPageSource().contains(seats)); Log.log("Képernyőn: " + seats);
		 * 
		 * wait.until(ExpectedConditions.visibilityOfElementLocated(By.
		 * xpath("(//div[@class = 'half-box'][2]/dd[contains(text(),'" + doors +
		 * "')])[1]"))); System.out.println(doors); assertTrue("Szerepel a forrásban",
		 * driver.getPageSource().contains(doors)); Log.log("Képernyőn: " + doors);
		 * 
		 * onScreen("Dízel"); checkPrice(engine_capacity," "); onScreen(make);
		 * onScreen(gearType);
		 * 
		 * 
		 * driver.findElement(By.cssSelector(".btn.btn-primary.btn-block.d-sm-block")).
		 * click();
		 * 
		 * 
		 * 
		 * 
		 * onScreenValue(vin); onScreenValue(engineNumber); onScreenValue(power);
		 * onScreenValue(randomNumSt); onScreenValue(randomNumStr2);
		 * onScreenValue(fuelCapacity); onScreen(make); onScreen(gearType);
		 * onScreen(carOffset); onScreen(Cylinder); onScreen(warranty);
		 * onScreen(enviromental_V9); onScreenValue(max_Load); onScreenValue(trunk);
		 * onScreenValue(""+engine_capacity); onScreenValue(netWeight);
		 * onScreenValue(weight); driver.findElement(By.id("form-button")).click();
		 * sleep(2000); driver.findElement(By.id("form-button")).click();
		 * onScreen(currentValue1); onScreen(currentValue2); onScreen(currentValue3);
		 * onScreen(currentValue4); onScreen(currentValue5);
		 */

	}

	public static void addNewCarEventFuel() throws IOException, InterruptedException {
		clickLinkWithText("esemény");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-fueling")));
		click(".sprite-fueling");
		sleep(3000);
		submit();
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'A mező nem lehet üres')]")));
		assertTrue("Kötelező mezők validálása", driver.getPageSource().contains("A mező nem lehet üres"));
		Log.log("Kötelező mezők validálása.");

		Random rand = new Random();

		Integer randomNum = rand.nextInt(50) + 10;
		String liter = String.valueOf(randomNum);

		fillName("liter", liter);

		randomNum = rand.nextInt(10000) + 10000;
		Integer cost = randomNum;

		fillName("fueling_cost", "" + cost);

		driver.findElement(By.id("fueling-date")).click();
		driver.findElement(By.id("fueling-date")).sendKeys(Keys.ENTER);

		String fuelType = randomSelect("type");

		fillName("car_gas_station_id_ac", "mészá");
		sleep(1000);
		click("ul li.ui-menu-item:nth-child(2) a");

		submit();

		sleep(2000);
		driver.findElement(By.xpath("//a[contains(text(), 'adatlapja')]")).click();
		sleep(3000);

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
		checkField("fueling_cost", "" + cost);

		randomNum = rand.nextInt(50) + 10;
		liter = String.valueOf(randomNum);
		fillName("liter", liter);

		randomNum = rand.nextInt(10000) + 10000;
		cost = randomNum;
		fillName("fueling_cost", "" + cost);

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

		sleep(2000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("test-date"))).click();
		driver.findElement(By.cssSelector(".logo-title.d-none.d-md-inline-block.ml-3")).click();

		Random rand = new Random();
		int randomNum = 1000 + rand.nextInt((50000 - 1) + 1);
		String noteText = "Note " + String.valueOf(randomNum);
		fillName("note", noteText);

		fillName("car_company_id_ac", "Abc kft.");
		submit();

		sleep(2000);
		driver.findElement(By.xpath("//a[contains(text(), 'adatlapja')]")).click();
		sleep(3000);

		wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Autó vizsgáztatva')]")));

		assertTrue("Műszaki vizsga elmentve", driver.getPageSource().contains("Autó vizsgáztatva"));
		Log.log("Esemény: műszaki vizsga elmentve.");

		onScreen("Abc kft.");

		sleep(3000);

		driver.findElement(By.cssSelector("a[href*='muszaki-vizsga-megtekintese']")).click();

		sleep(3000);
		onScreen("Abc kft.");
		String now = dateLocale(LocalDate.now());

		onScreen(now);
		onScreen(noteText);

		now = dateLocale(LocalDate.now().plusYears(2));
		onScreen(now);

		clickLinkWithText("Szerkesztés");

		now = dateDashes(LocalDate.now());
		checkField("test_date", now);
		checkField("car_company_id_ac", "Abc kft.");
		onScreen(noteText);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("label[for=is-taxi]"))).click();
		sleep(2000);
		submit();
		sleep(3000);

		clickLinkWithText("Műszaki vizsga");
		sleep(2000);

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
		fillName("sell_price", "" + randomprice);
		sleep(1000);
		driver.findElement(By.name("sell_description")).clear();
		String randomText = UUID.randomUUID().toString();
		fillName("sell_description", randomText);

		sleep(1000);
		rand = new Random();
		int randomzip = rand.nextInt(89) + 10;

		while (13 <= randomzip && randomzip <= 19) {
			randomzip = rand.nextInt(89) + 10;
		}

		fillName("loc_zip_id_ac", "" + randomzip);

		try {
			driver.findElement(By.cssSelector(".ui-menu-item")).click();
		} catch (NoSuchElementException e) {

			randomzip = rand.nextInt(89) + 10;
			while (13 <= randomzip && randomzip <= 19) {
				randomzip = rand.nextInt(89) + 10;
			}
			fillName("loc_zip_id_ac", "" + randomzip);
			click(".ui-menu-item");

		}

		fillName("loc_zip_id_ac", "" + randomzip);

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
		sleep(2000);
		driver.findElement(By.cssSelector(".fas.fa-eye")).click();
		sleep(2000);
		checkPrice(randomprice, " ");
		onScreen(randomText);
		WebElement nameText = driver.findElement(By.className("name"));
		String nametextValue = nameText.getText();
		onScreen("" + nametextValue);
		sleep(10000);
		Log.log("Autó sikeresen meghirdetve");
		clickLinkWithText("Használt autó hirdetések");
		Log.log("Használt Autó kereső");
		clickLinkWithText("Részletes kereső");
		Log.log("Részletes Kereső Kiválasztva");
		fillName("pricefrom", "" + randomprice);
		fillName("priceto", "" + randomprice);
		fillName("loc_zip_id_ac", "" + randomzip);
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

		sleep(2000);
		String myCarId = getCarId();
		// Esemény felvitel
		sleep(3000);
		clickLinkWithText("esemény");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-mot")));
		click(".sprite-tire");

		click("input[name=\"service_date\"]");
		sleep(2000);
		driver.findElement(By.id("service-date")).sendKeys(Keys.ENTER);

		// Új gumi felvitel
		clickLinkWithText("+ Új gumi felvitel");

		String width = randomSelect("width");
		String height = randomSelect("height");
		String diameter = randomSelect("diameter");
		String type = randomSelect("type");
		String mufacturer = randomSelect("mufacturer");
		fillName("item_description", "test model");
		select("number", "4");
		String worn = randomSelect("worn");
		randomSelect("dot_week");
		randomSelect("dot_year");
		String td1 = randomSelect("thread_depth_1");
		String td2 = randomSelect("thread_depth_2");
		String td3 = randomSelect("thread_depth_3");
		String td4 = randomSelect("thread_depth_4");

		td1 = td1.substring(0, td1.length() - 3);
		td2 = td2.substring(0, td2.length() - 3);
		td3 = td3.substring(0, td3.length() - 3);
		td4 = td4.substring(0, td4.length() - 3);

		Random rand = new Random();
		int randomNum = 1000 + rand.nextInt((50000 - 1) + 1);
		String noteText = "Note " + String.valueOf(randomNum);
		fillName("tire_storage", noteText);

		int price = 1000 + rand.nextInt((50000 - 1) + 1);
		String priceString = "" + price;

		price = 1000 + rand.nextInt((50000 - 1) + 1);
		priceString = "" + price;
		fillName("price", priceString);
		driver.findElement(By.cssSelector(".btn.btn-primary.submitBtn.tsLoadingIcon")).click();
		// Gumi felvitel vége

		sleep(5000);
		// Gumi felhelyezése az autóra
		Actions builder = new Actions(driver);
		WebElement from = driver.findElement(By.cssSelector(".tire-link.draggable.ui-draggable.ui-draggable-handle"));
		WebElement to = driver.findElement(By.cssSelector(".row.axis-draggable"));
		builder.dragAndDrop(from, to).perform();
		// Felhelyezés vége

		Log.log("A Gumi hozzáadása Sikeres, felhelyezve az autóra");
		click("input[name=\"service_date\"]");
		fillName("car_company_id_ac", "Teszt");
		fillName("price_work", "50000");

		int randomNum2 = rand.nextInt(50000) + 1000;
		String noteText2 = "Note " + String.valueOf(randomNum2);
		fillName("note", noteText2);

		// Gumi CSERE ESEMÉNY VÉGE
		submit();

		sleep(3000);

		onScreenAlert("Sikeres szerviz esemény mentés");
		sleep(2000);

		// Esemény adatok ellenőrzés az esemény adatlapján
		wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//div[@class='row event-view-row-sm']//*[contains(text(), 'Gumicsere')]")));
		System.out.println("Gumicsere");
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("Gumicsere"));
		Log.log("Képernyőn: Gumicsere");

		onScreen("50 000");
		onScreen(type);
		onScreen(mufacturer);
		onScreen("4 db");
		onScreen(width + "/" + height + "/" + diameter);

		// Szerkesztés
		sleep(2000);
		clickLinkWithText("Szerkesztés");
		sleep(4000);

		// Első Gumi levétele
		builder = new Actions(driver);
		from = driver.findElement(By.xpath("//div[@class='row'][1]/div[@class='axis col-12']"));
		to = driver.findElement(By.xpath("//div[@class='col-12 col-lg-6']"));
		sleep(2000);
		builder.dragAndDrop(from, to).perform();

		sleep(3000);
		// második gumi felvitel
		clickLinkWithText("+ Új gumi felvitel");

		width = randomSelect("width");
		height = randomSelect("height");
		diameter = randomSelect("diameter");
		type = randomSelect("type");
		mufacturer = randomSelect("mufacturer");
		fillName("item_description", "test model");
		select("number", "4");
		randomSelect("worn");
		randomSelect("dot_week");
		randomSelect("dot_year");
		td1 = randomSelect("thread_depth_1");
		td2 = randomSelect("thread_depth_2");
		td3 = randomSelect("thread_depth_3");
		td4 = randomSelect("thread_depth_4");

		td1 = td1.substring(0, td1.length() - 3);
		td2 = td2.substring(0, td2.length() - 3);
		td3 = td3.substring(0, td3.length() - 3);
		td4 = td4.substring(0, td4.length() - 3);

		randomNum = rand.nextInt(50000) + 1000;
		noteText = "Note " + String.valueOf(randomNum);
		fillName("tire_storage", noteText);

		price = rand.nextInt(50000) + 1000;
		priceString = "" + price;
		fillName("price", priceString);
		driver.findElement(By.cssSelector(".btn.btn-primary.submitBtn.tsLoadingIcon")).click();
		// második gumi vége

		Log.log("Új gumi hozzáadása");

		// második gumi feltétel
		sleep(2000);
		builder = new Actions(driver);
		from = driver.findElement(By.xpath("//div[@class='tire-link draggable ui-draggable ui-draggable-handle'][2]"));
		to = driver.findElement(By.xpath("//div[@class='col-4 offset-4 wheelson-box']/div[@class='info-box']"));
		builder.dragAndDrop(from, to).perform();
		Log.log("Az új Gumi az autóra felhelyezve.");
		// második gumi feltétel vége

		onScreen(noteText2);

		int randprice = rand.nextInt(500) + 100000;
		fillName("price_work", "" + randprice);

		randomNum2 = rand.nextInt(50000) + 1000;
		noteText2 = "Note " + String.valueOf(randomNum2);
		fillName("note", noteText2);
		sleep(1000);
		driver.findElement(By.xpath("//div[@class='logo-title d-none d-md-inline-block ml-3']")).click();
		sleep(3000);

		sleep(1000);
		submit();
		// Szerkesztés vége

		sleep(1000);
		onScreenAlert("Sikeres szerviz esemény mentés");

		// esemény adatok ellenőrzése az esemény listában
		onScreen(noteText2);
		checkPrice(randprice, " ");
		onScreen("Teszt");
		sleep(2000);

		// Felvitt adatok ellenőrzése az esemény adatlapján
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@class='fas fa-eye circle']"))).click();
		onScreen("Teszt");
		checkPrice(randprice, " ");

		wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//div[@class='row event-view-row-sm']//*[contains(text(), 'Gumicsere')]")));
		System.out.println("Gumicsere");
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("Gumicsere"));
		Log.log("Képernyőn: Gumicsere");

		onScreen(type);
		onScreen(mufacturer);
		onScreen("4 db");
		onScreen(width + "/" + height + "/" + diameter);

		// Garázsban ellenőrzés
		goToPage(url + "/hu/sajat-auto/" + myCarId);
		sleep(4000);
		clickLinkWithText("Gumicsere");
		sleep(2000);

		// Esemény törlés
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@class='fas fa-trash circle']")))
				.click();
		sleep(2000);
		clickLinkWithText("Esemény törlése");
		sleep(2000);
		onScreenAlert("A szerviz esemény sikeresen törölve.");

		// Gumik törlése
		goToPage(url + "/hu/sajat-auto/" + myCarId);
		sleep(3000);
		driver.findElement(By.xpath("//*[text()[contains(.,'" + type + "')]]")).click();
		sleep(2000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@class='fas fa-trash circle']")))
				.click();
		sleep(1000);
		clickLinkWithText("Igen");
		sleep(1000);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@class='fas fa-trash circle']")))
				.click();
		sleep(1000);
		clickLinkWithText("Igen");
		sleep(1000);
		onScreenAlert("A gumi törölve lett");

		Log.log("SIKERES GUMICSERE TESZT");

	}

	public static void addNewCarEventCleaning() throws IOException, InterruptedException {
		// driver.findElement(By.xpath("//span[contains(text(),
		// \"esemény\")]")).click();
		// wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-cleaning")));
		// driver.findElement(By.className("sprite-cleaning")).click();

		clickLinkWithText("esemény hozzáadása");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-cleaning")));
		click(".sprite-cleaning");
		sleep(3000);

		String cleaningType = randomSelect("cleaning_type");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cleaning-date"))).click();
		sleep(1000);
		click(".logo-title");

		Random rand = new Random();
		Integer randomNum = 1000 + rand.nextInt((50000 - 1) + 1);
		int price = randomNum;
		String priceString = String.valueOf(randomNum);
		fillName("price", priceString);

		fillName("car_company_id_ac", "a");
		sleep(4000);
		click("ul#ui-id-1 li:nth-child(1)");

		rand = new Random();
		randomNum = 1000 + rand.nextInt((50000 - 1) + 1);
		String noteText = "Note " + String.valueOf(randomNum);
		fillName("note", noteText);

		submit();

		sleep(2000);
		driver.findElement(By.xpath("//a[contains(text(), 'adatlapja')]")).click();
		sleep(3000);

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
		sleep(2000);
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
		sleep(2000);
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
		fillName("note", noteText);
		// driver.findElement(By.cssSelector("textarea[name=\"note\"]")).sendKeys(noteText);

		submit();
		
		onScreenAlert("Sikeres mentés");

		Log.log("Esemény: baleset beküldve.");
		sleep(2000);

		String now = dateLocale(LocalDate.now());
		System.out.println(now);
		onScreen(now);
		// spec onScreen
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("(//*[contains(text(), 'Sérülés megtekintése')])[2]")));
		System.out.println("Sérülés megtekintése");
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("Sérülés megtekintése"));
		Log.log("Képernyőn: Sérülés megtekintése");

		Log.log("Esemény: baleset elmentve.");

		sleep(2000);
		// driver.findElement(By.cssSelector("a[href*='baleset-esemeny-megtekintese']")).click();
		// sleep(2000);

		onScreen(noteText);

		clickLinkWithText("Szerkesztés");
		onScreen(noteText);
		submit();
		sleep(3000);
		onScreenAlert("Sikeres módosítás");

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("event-types")));
		element = driver.findElement(By.className("event-types"));
		((JavascriptExecutor) driver).executeScript("arguments[0].style.display='none'", element);

		click("i.fa-trash");
		click("a[data-apply=\"confirmation\"]");
		sleep(1000);
		onScreenAlert("Sikeresen törölve!");

		sleep(3000);
		assertTrue("Event deleted", !driver.getPageSource().contains(noteText));
		Log.log("Esemény: baleset sikeresen törölve.");

	}

	public static void addNewCarEventOther() throws IOException, InterruptedException {
		// driver.findElement(By.xpath("//span[contains(text(),
		// \"esemény\")]")).click();
		clickLinkWithText("esemény");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-other")));
		driver.findElement(By.className("sprite-other")).click();
		sleep(2000);

		Random rand = new Random();
		Integer randomNum = 1 + rand.nextInt((3000000 - 1) + 1);
		String randNum = String.valueOf(randomNum);
		String eventText = "Teszt esemény " + randNum;

		fillName("title", "Teszt esemény " + randNum);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("event-date"))).click();
		driver.findElement(By.cssSelector(".logo-title.d-none.d-md-inline-block.ml-3")).click();

		int randNumber = new Random().nextInt(123456);
		String noteText = "Test note " + randNumber;
		fillName("note", noteText);

		driver.findElement(By.className("submitBtn")).click();
		sleep(2000);

		sleep(2000);
		driver.findElement(By.xpath("//a[contains(text(), 'adatlapja')]")).click();
		sleep(3000);

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

	public static void adminActivatecompany(String companyName) throws IOException, InterruptedException {
		goToPage(url + "/hu/admin/car/car-companies");
		Log.log("Admin cégek");
		clickLinkWithText(companyName);

		driver.findElement(By.xpath("/html/body/section/section/div/div[1]/ul/li/a")).click();
		driver.findElement(By.xpath("/html/body/section/section/div/div[1]/ul/li/ul/li[1]/a")).click();

		sleep(3000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//i[@class='zmdi zmdi-thumb-up']"))).click();

		Log.log("Cég jóváhagyva");
		
		goToPage(url + "/hu/kijelentkezes");
		sleep(3000);
		onScreenAlert("Sikeres kijelentkezés!");
		
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

		sleep(3000);
		try {

			List<WebElement> elements = driver.findElements(By.cssSelector("#mycar-block.card .profile-car-item"));
			for (WebElement element : elements) {
				Log.log(element.findElement(By.className("numberplate")).getText());
			}
			element = elements.get(new Random().nextInt(elements.size()));
			Log.log(element.findElement(By.className("numberplate")).getText() + " selected.");
			element.click();

		} catch (Exception e) {

			driver.navigate().refresh();
			sleep(5000);
			List<WebElement> elements = driver.findElements(By.cssSelector("#mycar-block.card .profile-car-item"));
			for (WebElement element : elements) {
				Log.log(element.findElement(By.className("numberplate")).getText());
			}
			element = elements.get(new Random().nextInt(elements.size()));
			Log.log(element.findElement(By.className("numberplate")).getText() + " selected.");
			element.click();

		}

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

		// randomSelect("car_part_company_offer_items[0][season]");
		driver.findElement(By.cssSelector("input[name=\"car_piece_part_company_offer_items[0][delivery_date]\"]"))
				.click();
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

	public static void logout() throws IOException, InterruptedException {
		sleep(3000);
		Log.log("Kijelentkezés a fiókból.");

		goToPage(url + "/hu/kijelentkezes");
		sleep(3000);
		onScreenAlert("Sikeres kijelentkezés!");
		
		sleep(3000);
	}

	public static void registerUserWrongEmail() throws IOException {

		driver.findElement(By.partialLinkText("Regisztráció")).click();
		/*
		 * assertEquals("Go to URL", driver.getCurrentUrl(), url + "/hu/regisztracio");
		 * Log.log("Click Registraion");
		 */

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

	public static void addNewCarNotes() throws IOException, InterruptedException {
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
		//check all note
		sleep(2000);
		driver.findElement(By.cssSelector(".car-mycar-notes .moreBtn")).click();
		sleep(3000);
		//write new
		driver.findElement(By.cssSelector(".car-mycar-notes .card-header a")).click();
		sleep(2000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note")));

		//fill note
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
	
	public static void deleteAllCarNote() throws IOException, InterruptedException {
		
		boolean isThereAnyNote = true;
		
		do {
			
			sleep(3000);
			
			try {
				
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//a[@class='note-delete'])[1]")));
				driver.findElement(By.xpath("(//a[@class='note-delete'])[1]")).click();
				
			}catch(TimeoutException e) {
				
				Log.log("Nincs több jegyzet");
				isThereAnyNote = false;
				
			}
			
		}while(isThereAnyNote == true);

	}

	public static void advancedSearch() throws IOException, InterruptedException {
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

	public static void checkRequestOfferTire(String companyName, String price)
			throws IOException, InterruptedException {
		sleep(3000);
		driver.findElement(By.cssSelector("#active-tire-requests .list-item:nth-child(1) a")).click();
		sleep(3000);
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

		if (savedPrice.equals(formattedPrice + " Ft")) {

			Log.log("Ár stimmel");

		} else {

			Log.log("Ár nem stimmel!!!!!!!!!!");
			Log.log("Itt szerepel: " + savedPrice);
			Log.log("Korábban megadott: " + formattedPrice + " Ft");

		}

	}

	public static void checkRequestOfferPart(String companyName, String price)
			throws IOException, InterruptedException {

		sleep(3000);
		driver.findElement(By.cssSelector("#active-piecepart-requests .list-item:nth-child(1) a")).click();
		sleep(3000);
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

		if (savedPrice.equals(formattedPrice + " Ft")) {

			Log.log("Ár stimmel");

		} else {

			Log.log("Ár nem stimmel!!!!!!!!!!");
			Log.log("Itt szerepel: " + savedPrice);
			Log.log("Korábban megadott: " + formattedPrice + " Ft");

		}
	}

	public static void sendRequestFinalOrder() throws InterruptedException, IOException {

		click(".checkbox");
		click(".btn-lg");
		sleep(3000);

		try {

			driver.findElement(By.id("car-address-loc-zip-id")).click();

		} catch (ElementNotVisibleException e) {

			driver.findElement(By.xpath("//span[@class='switch']")).click();
			sleep(3000);

		}

		fillName("car_address[loc_zip_id_ac]", "1052");
		sleep(1000);
		driver.findElement(By.id("car-address-loc-zip-id")).sendKeys(Keys.ENTER);
		fillName("car_address[street]", "Sas");
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

	public static void checkRequestFinalOrderTire(String price) throws IOException, InterruptedException {
		click(".bell");
		clickLinkWithText("Gumi rendelés");
		onScreen(price);

		// onScreen("1052 Budapest, Sas utca 25.");

		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[text()[contains(.,'1052 Budapest, Sas utca 25.')]]")));
		System.out.println("1052 Budapest, Sas utca 25.");
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("1052 Budapest, Sas utca 25."));
		Log.log("Képernyőn: " + "1052 Budapest, Sas utca 25.");

		onScreen("test note");

		Log.log("Értesítés céges oldalon megérkezett.");
		clickLinkWithText("Teljesítve");
		Log.log("Archiválva");
	}

	public static void checkRequestFinalOrderPart(String price) throws IOException, InterruptedException {

		click(".bell");
		clickLinkWithText("Alkatrész rendelés");
		int priceInt = Integer.parseInt(price);
		checkPrice(priceInt, " ");

		// onScreen("1052 Budapest, Sas utca 25.");

		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[text()[contains(.,'1052 Budapest, Sas utca 25.')]]")));
		System.out.println("1052 Budapest, Sas utca 25.");
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("1052 Budapest, Sas utca 25."));
		Log.log("Képernyőn: " + "1052 Budapest, Sas utca 25.");

		// onScreen("hey!");

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()[contains(.,'hey!')]]")));
		System.out.println("hey!");
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("hey!"));
		Log.log("Képernyőn: " + "hey!");

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

	private static void clickLinkWithText(String string) throws InterruptedException {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
				"//a[not(contains(@class,'d-sm-none'))]/descendant-or-self::*[contains(text(),'" + string + "')]")));
		driver.findElement(By.xpath(
				"//a[not(contains(@class,'d-sm-none'))]/descendant-or-self::*[contains(text(),'" + string + "')]"))
				.click();

		sleep(3000);
	}
	
	private static void clickText(String string) throws InterruptedException {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'" + string + "')]")));
		driver.findElement(By.xpath("//*[contains(text(),'" + string + "')]")).click();

		sleep(3000);
	}

	private static void clickButton(String string) throws InterruptedException {
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//button[contains(text(), '" + string + "')]")));
		driver.findElement(By.xpath("//button[contains(text(), '" + string + "')]")).click();

		sleep(3000);
	}

	private static void clickXpath(String string) {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(string)));
		driver.findElement(By.xpath(string)).click();
	}

	public static void FillUserPersonalData() throws IOException, InterruptedException {
		click(".user-img");
		clickLinkWithText("Adatmódosítás");
		sleep(3000);
		driver.findElement(By.id("mobile")).clear();
		sleep(2000);
		fillName("mobile", "11111111");
		fillName("user[last_name]", "Teszt");
		fillName("user[first_name]", "Eszter");
		fillName("mothers_name", "Kovács Hilda Géza");
		fillName("birthdate", "1970-12-12");
		fillName("personal_ident", "AE12345678");
		fillName("driving_licence_number", "fdsfdsAE12345678");
		driver.findElement(By.id("form-button")).click();
		Thread.sleep(5000);
		click(".logos");
		Log.log("Vissza a főoldalra.");
		clickLinkWithText("profil szerkesztése");
		checkField("mobile", "3611111111");
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
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + string + "')]")));
		System.out.println(string);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(string));
		Log.log("Képernyőn: " + string);
	}

	private static void onScreenWS(String string) throws IOException {
		wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()[contains(.,'" + string + "')]]")));
		System.out.println(string);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(string));
		Log.log("Képernyőn: " + string);
	}

	private static void onScreenValue(String string) throws IOException {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + string + "']")));
		System.out.println(string);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(string));
		Log.log("Képernyőn: " + string);
	}

	private static void onScreenSelected(String string) throws IOException {
		wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//option[@selected='selected' and contains(text(), '" + string + "')]")));
		System.out.println(string);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(string));
		Log.log("Képernyőn: " + string);
	}

	private static void onScreenAlert(String string) throws IOException {
		wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), '" + string + "')]")));
		System.out.println(string);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(string));
		Log.log("Képernyőn: " + string);
	}

	public static void test() {
		driver.get("https://testcenter.vr/selenium-test-surface.php");

	}

	public static void checkCarProperties() throws IOException, InterruptedException {
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

		String pattern = "//div[@id='mycar-block']//a['numberplate' and contains(translate(.,'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), '"+ string + "')]";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pattern)));
		WebElement myElement = driver.findElement(By.xpath(pattern));
		WebElement parent = myElement.findElement(By.xpath("../.."));
		parent.findElement(By.className("item-image")).click();
		Log.log("Autó kiválasztva: " + string);

	}

	public static void deleteCar(String numberPlate) throws IOException, InterruptedException {
		selectCar(numberPlate);
		sleep(2000);
		clickLinkWithText("Autó törlése");
		sleep(2000);
		click(".deleteAttachedItem");
	}

	public static void deleteUserCars() throws IOException, InterruptedException {
		List<WebElement> elements = driver.findElements(By.cssSelector("#mycar-block .numberplate"));
		List<String> list = new ArrayList<String>();
		for (WebElement element : elements) {
			String numberplate = element.getText();
			list.add(numberplate);
		}
		for (String oneItem : list) {
			deleteCar(oneItem);
			Log.log(oneItem + " rendszámú autó törölve.");
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
		
		//Create event
		clickLinkWithText("esemény hozzáadása");
		sleep(5000);

		// wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-mycar_service_log-body"))).click();
		/*
		 * List<WebElement> sprites =
		 * driver.findElements(By.cssSelector(".sprite-mycar_service_log-body")); for
		 * (WebElement sprite : sprites) {
		 * 
		 * if (sprite.isDisplayed()) { sprite.click();
		 * 
		 * } }
		 */

		WebElement element = driver.findElement(By.cssSelector(".sprite.sprite-mycar_service_log-body"));
		Actions actions = new Actions(driver);
		actions.moveToElement(element).click().build().perform();

		//fill details
		sleep(3000);
		click(".ts-date-picker");
		click("h2");
		fillName("car_company_id_ac", "a");
		sleep(3000);
		click("ul#ui-id-1 li:nth-child(1)");
		// driver.findElement(By.id("car-company-id")).sendKeys(Keys.ENTER);
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
		sleep(3000);

		publicPart = driver.findElement(By.id("car-mycar-service-log-items-0-text")).getAttribute("value");
		Log.log(publicPart + " mentve");

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
		
		//onScreenAlert("Sikeres szerviz esemény hozzáadás");
		
		//Check details
		String now = dateLocale(LocalDate.now());
		System.out.println(now);
		onScreen(now);
		onScreen("Karosszéria javítás");
		onScreen(noteText);
		int p1 = 12435;
		int p2 = 20000;
		checkPrice(p1, " ");
		checkPrice(p2, " ");

		onScreen(publicPart);
		onScreen("Bal hátsó oldalsó ajtó üveg");
		checkPrice(randPrice, " ");

		//check in garage
		clickXpath("//a[contains(text(), 'adatlapja')]");
		sleep(3000);

		clickLinkWithText("Karosszéria javítás");

		clickLinkWithText("Szerkesztés");

		//modify
		clickXpath("(//div[@class='col-xs-1']/span)[1]");
		fillName("car_mycar_service_log_items[1][price]", "4444");
		p1 = 4444;
		fillName("car_mycar_service_log_items[1][item_description]", "test text");
		randPrice = new Random().nextInt(123456);
		fillName("price_work", "" + randPrice);
		RN = new Random().nextInt(123456);
		noteText = "Test note " + RN;
		fillName("note", noteText);

		submit();
		
		//onScreenAlert("Sikeres szerviz esemény hozzáadás");
		
		//check values in service list
		onScreen(now);
		onScreen(noteText);
		checkPrice(randPrice, " ");
		onScreen("Karosszéria javítás");
		
		driver.findElement(By.xpath("//a/b[contains(text(),'Karosszéria javítás')]")).click();

		//check modded values
		onScreen(now);
		onScreen("Karosszéria javítás");
		onScreen(noteText);

		checkPrice(p1, " ");

		onScreen("Bal hátsó oldalsó ajtó üveg");
		checkPrice(randPrice, " ");

		//delete event
		click("i.fa-trash");
		clickLinkWithText("Esemény törlése");
		sleep(3000);
		
		onScreenAlert("A szerviz esemény sikeresen törölve.");
		
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

		sleep(2000);
		driver.findElement(By.xpath("//a[contains(text(), 'adatlapja')]")).click();
		sleep(3000);

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
		
		//event upload
		goToPage(url + "/hu/egyeb-szerviz-esemeny-letrehozasa/" + getCarId());
		sleep(1000);
		int randNumber = new Random().nextInt(500) + 1;

		click(".ts-date-picker");
		driver.findElement(By.xpath("/html/body/header/div/div/div[1]/div")).click();
		
		LocalDate today = LocalDate.now();
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy. MM dd.");
		String strToday = today.format(dateFormat);

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
		
		//save
		
		submit();
		onScreenAlert("Sikeres szerviz esemény mentés");

		//check in timeline
		sleep(2000);
		driver.findElement(By.xpath("//a[contains(text(), 'adatlapja')]")).click();
		sleep(3000);

		onScreen("Egyéb szerviz");

		sleep(2000);
		Log.log("Sikeresen mentve");

		//check in datasheet
		driver.findElement(By.cssSelector("a[href*='szerviz-esemeny-megtekintese']")).click();
		sleep(1000);
		onScreen(partName);
		onScreen(noteText);
		checkPrice(randPrice, " ");
		
		//modify
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

		//save modded values
		submit();
		onScreenAlert("Sikeres szerviz esemény mentés");
		
		sleep(2000);
		Log.log("Sikeres módosítás");

		//check modded values in datasheet
		Log.log("Újra ellenőrzés...");
		clickLinkWithText("Egyéb szerviz");
		onScreen(partName);
		onScreen(noteText);
		checkPrice(randPrice, " ");
		
		//check modded values in modify page
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

		//delete event
		submit();
		onScreenAlert("Sikeres szerviz esemény mentés");
		sleep(2000);
		Log.log("Törlés...");

		driver.findElement(By.cssSelector(".fas.fa-trash.circle")).click();
		driver.findElement(By.cssSelector(".btn.btn-sm.h-100.d-flex.align-items-center.btn-secondary")).click();
		sleep(1000);
		onScreenAlert("A szerviz esemény sikeresen törölve");
		assertTrue("Event deleted", !driver.getPageSource().contains(noteText));
		Log.log("Esemény: egyéb sikeresen törölve.");

	}

	public static void importCarSearch() throws IOException, InterruptedException, AWTException {
		
		sleep(3000);
		driver.findElement(By.xpath("//a[@class='col-12 col-sm-6 col-md-4 col-lg-3 col-xl landing-sell-item-wrapper'][1]")).click();
		
		String carName = driver.findElement(By.xpath("//h1[@class='container']")).getText();
		String carPrice = driver.findElement(By.cssSelector(".price.py-2.mt-3")).getText();
		String carKm = driver.findElement(By.xpath("//dl[@class='row'][1]/dd[@class='col-12 col-md-4 col-md-8']")).getText();
		String carDate = driver.findElement(By.xpath("//dl[@class='row'][2]/dd[@class='col-12 col-md-4 col-md-8']")).getText();
		String carMake = driver.findElement(By.xpath("//dl[@class='row'][3]/dd[@class='col-12 col-md-4 col-md-8']")).getText();
		String carFuel = driver.findElement(By.xpath("//dl[@class='row'][4]/dd[@class='col-12 col-md-8']")).getText();
		String carEngCap = driver.findElement(By.xpath("//dl[@class='row'][5]/dd[@class='col-12 col-md-8']")).getText();
		String carGearShift = driver.findElement(By.xpath("//dl[@class='row'][7]/dd[@class='col-12 col-md-8']")).getText();
		String carkW = driver.findElement(By.xpath("//dl[@class='row'][8]/dd[@class='col-12 col-md-8']")).getText();

		String[] fullCarName = carName.split(" ");
		String[] fullCarDate = carDate.split(" ");
		String[] fullCarEngCap = carEngCap.split(" ");
		String[] fullCarkW = carkW.split("kW");
		
		Log.log(fullCarName[0]);
		Log.log(carPrice);
		Log.log(carKm);
		Log.log(carDate);
		Log.log(carMake);
		Log.log(carFuel);
		Log.log(fullCarEngCap[0]);
		Log.log(carGearShift);
		Log.log(fullCarkW[0]);
		
		sleep(3000);
		
		goToPage(url + "/hu/importautok");
		
		clickLinkWithText("Részletes kereső");
		sleep(3000);
		
		fillName("kmfrom", carKm);
		fillName("kmto", carKm);
		selectValue("yearfrom", fullCarDate[0]);
		selectValue("yearto", fullCarDate[0]);
		fillName("powerfrom", fullCarkW[0]);
		fillName("powerto", fullCarkW[0]);
		fillName("ccmfrom", fullCarEngCap[0]);
		fillName("ccmto", fullCarEngCap[0]);
		clickText(carMake);
		clickText(carFuel);
		clickText(carGearShift);
		
		clickButton("Találatok megtekintése");
		
	}

	public static void addNewCarEventPenalty() throws IOException, InterruptedException {
		// goToPage(url+"/hu/birsag-esemeny-letrehozasa/" + getCarId());
		clickLinkWithText("esemény hozzáadása");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-penalty")));
		click(".sprite-penalty");

		sleep(2000);
		String penaltyType = randomSelect("penalty_type");

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("penalty-date"))).click();
		driver.findElement(By.cssSelector(".logo-title.d-none.d-md-inline-block.ml-3")).click();

		LocalDate dueDate = LocalDate.now().plusMonths(3);

		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-LL-dd");
		String strDueDate = dueDate.format(dateFormat);
		Log.log(strDueDate + " tesztre");

		driver.findElement(By.id("pay-due-date")).sendKeys(strDueDate);
		driver.findElement(By.id("pay-due-date")).sendKeys(Keys.ENTER);

		Random rand = new Random();
		Integer price = 1000 + rand.nextInt((50000 - 1) + 1);
		fillName("price", "" + price);
		int note = 1000 + rand.nextInt((50000 - 1) + 1);
		String noteText = "Note " + note;
		fillName("note", noteText);

		submit();

		sleep(2000);
		driver.findElement(By.xpath("//a[contains(text(), 'adatlapja')]")).click();
		sleep(3000);

		sleep(1000);
		onScreen(penaltyType);
		checkPrice(price, " ");

		sleep(3000);
		driver.findElement(By.cssSelector("a[href*='birsag-esemeny-megtekintese']")).click();
		sleep(3000);
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

		sleep(3000);

		clickLinkWithText("Szerkesztés");
		checkField("penalty_type", penaltyType);
		checkField("price", "" + price);
		onScreen(noteText);

		sleep(1000);
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
		clickXpath("//h3[@class='title float-left']");
		sleep(2000);
		// wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".sprite-mycar_highway_ticket"))).click();
		click(".icon-inner .sprite-mycar_highway_ticket");
		// clickLinkWithText("Pályamatrica");

		driver.findElement(By.cssSelector("input[name=\"start_date\"]")).click();
		sleep(3000);
		driver.findElement(By.id("start-date")).sendKeys(Keys.ENTER);
		driver.findElement(By.cssSelector(".logo-title.d-none.d-md-inline-block.ml-3")).click();
		List<WebElement> list = driver.findElements(By.cssSelector("input[type=\"radio\"]:not([id=\"ticket1\"])"));
		int size = list.size();
		int randNumber = new Random().nextInt(size - 1) + 1;
		String id = list.get(randNumber).getAttribute("id");
		// list.get(randNumber).click();
		sleep(3000);
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

		sleep(2000);
		driver.findElement(By.xpath("//a[contains(text(), 'adatlapja')]")).click();
		sleep(3000);

		String pattern = "//dt[contains(text(),' Autópálya-matrica érvényessége')]//following-sibling::dd[1]";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pattern)));
		WebElement insuranceParent = driver.findElement(By.xpath(pattern));
		String insurance = insuranceParent.findElement(By.tagName("a")).getText();

		// assertTrue("Autópályamatrica lejárat OK.", insurance.contains(expiration));
		// Log.log("Autópályamatrica lejárat OK.");

		Log.log(expiration);
		Log.log(name);
		// assertTrue("Autópályamatrica adatok OK.", insurance.contains(expiration));
		assertTrue("Autópályamatrica adatok OK.", insurance.contains(name));
		Log.log("Autópályamatrica adatok OK.");

		onScreen("Új autópálya matrica");
		clickLinkWithText("Új autópálya matrica");
		sleep(1000);
		onScreen(expiration);
		onScreen(name);

		String priceView = driver.findElement(By.xpath("(//dl[@class='col-12 col-sm'])[6]//dd")).getText();

		if (priceView.equals(price)) {

			Log.log("Az ár egyezik");

		} else {

			Log.log("Az ár nem stimmel!");
			driver.close();
			System.exit(0);

		}

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

		priceView = driver.findElement(By.xpath("(//dl[@class='col-12 col-sm'])[6]//dd")).getText();

		if (priceView.equals(price)) {

			Log.log("Az ár egyezik");

		} else {

			Log.log("Az ár nem stimmel!");
			driver.close();
			System.exit(0);

		}

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
		sleep(4000);
		// wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".sprite.sprite-mycar_insurance")));
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

		sleep(2000);
		driver.findElement(By.xpath("//a[contains(text(), 'adatlapja')]")).click();
		sleep(3000);

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
		checkSelect("type", "Kötelező gépjármű biztosítás");
		checkSelect("company", company);

		checkField("ident", ident);
		checkField("price", "" + price);
		checkField("period", period);

		submit();
		sleep(3000);

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

		sleep(2000);
		driver.findElement(By.xpath("//a[contains(text(), 'adatlapja')]")).click();
		sleep(3000);

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
		onScreenAlert("Sikeres módosítás");
		sleep(3000);
		driver.findElement(By.cssSelector(".fas.fa-trash.circle")).click();
		click("a[data-apply=\"confirmation\"]");

		sleep(2000);
		
		onScreenAlert("Sikeresen törölve!");
		
		sleep(4000);
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

		// FELVITEL
		clickLinkWithText("esemény hozzáadása");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-mycar_insurance")));
		clickLinkWithText("GAP");
		sleep(2000);

		String company = randomSelect("company");

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("start-date"))).click();
		sleep(1000);
		// driver.findElement(By.cssSelector("input[name=\"start_date\"]")).click();
		// String period = randomSelect("period");

		int randNumber = new Random().nextInt(123456);
		String ident = "" + randNumber;
		fillName("ident", ident);

		int price = new Random().nextInt(123456);
		String stringPrice = "" + price;
		fillName("price", stringPrice);
		submit();

		// FELVITEL VÉGE

		sleep(2000);
		driver.findElement(By.xpath("//a[contains(text(), 'adatlapja')]")).click();
		sleep(3000);

		// MEGTEKINTÉS ADATLAPON

		String pattern = "//dt[contains(text(),'GAP biztosítás')]//following-sibling::dd[1]";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pattern)));
		WebElement insuranceParent = driver.findElement(By.xpath(pattern));
		String insurance = insuranceParent.findElement(By.tagName("a")).getText();
		LocalDate dueDate = LocalDate.now().plusYears(1);

		onScreen("Új GAP biztosítás");

		Log.log("Elvárt lejárati dátum: " + dateLocale(dueDate));
		assertTrue("GAP biztosítás lejárat OK.", insurance.contains("Lejár: " + dateLocale(dueDate)));
		Log.log("GAP biztosítás lejárat OK.");

		// MEGTEKINTÉS ESEMÉNY

		clickLinkWithText("biztosítás");
		sleep(4000);
		onScreen(company);
		// onScreen(period);
		onScreen(ident);

		checkPrice(price, " ");

		// MÓDOSÍTÁS + FELVITEL ELLENŐRZÉS

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

		// MÓDOSÍTOTT ELLENŐRZÉSE

		sleep(2000);

		clickLinkWithText("biztosítás");
		onScreen(company);
		// onScreen(period);
		onScreen(ident);

		checkPrice(price, " ");

		// MÓDOSÍTOTT FELVITEL ELLENŐRZÉSE

		sleep(3000);
		clickLinkWithText("Szerkesztés");
		sleep(2000);
		checkSelect("type", "GAP biztosítás");
		checkSelect("company", company);

		checkField("ident", ident);
		checkField("price", "" + price);

		submit();
		sleep(2000);

		// TÖRÖL

		driver.findElement(By.cssSelector(".fas.fa-trash.circle")).click();
		click("a[data-apply=\"confirmation\"]");

		sleep(3000);
		assertTrue("Event deleted", !driver.getPageSource().contains("GAP biztosítás"));
		Log.log("Esemény: GAP biztosítás sikeresen törölve.");
	}

	private static void sleep(int i) throws InterruptedException {
		System.out.println("\t" + "\t" + "\t" + "\t" + "\t" + "\t" + "\t" + "\t" + "\t" + "\t" + "\t" + "\t" + "wait "
				+ i + " millisconds");
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
	
	public static String dateTimeLocaleMinusOneHour(LocalDateTime date) {
		DateTimeFormatter yearF = DateTimeFormatter.ofPattern("yyyy.");
		DateTimeFormatter monthF = DateTimeFormatter.ofPattern("MMMM");
		DateTimeFormatter dayF = DateTimeFormatter.ofPattern("d.");
		DateTimeFormatter timeF = DateTimeFormatter.ofPattern("HH:mm");
		LocalDateTime dateMinusOneHour = date.minus(1, ChronoUnit.HOURS);
		
		String year = dateMinusOneHour.format(yearF);
		String month = dateMinusOneHour.format(monthF);
		String day = dateMinusOneHour.format(dayF);
		String time = dateMinusOneHour.format(timeF);

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

		return year + " " + month + " " + day + " " + time;
	}
	
	public static String dateTimeLocale(LocalDateTime date) {
		DateTimeFormatter yearF = DateTimeFormatter.ofPattern("yyyy.");
		DateTimeFormatter monthF = DateTimeFormatter.ofPattern("MMMM");
		DateTimeFormatter dayF = DateTimeFormatter.ofPattern("d.");
		DateTimeFormatter timeF = DateTimeFormatter.ofPattern("HH:mm");

		
		String year = date.format(yearF);
		String month = date.format(monthF);
		String day = date.format(dayF);
		String time = date.format(timeF);

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

		return year + " " + month + " " + day + " " + time;
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

		String carName = wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".col-12.col-md-8.col-lg-9.text-right")))
				.getText();
		click(".switch");
		Random rand = new Random();
		Integer randomNum1 = rand.nextInt(10000) + 100000;
		fillName("rent_price_1", "" + randomNum1);
		Integer randomNum2 = rand.nextInt(10000) + 80000;
		fillName("rent_price_2", "" + randomNum2);
		Integer randomNum3 = rand.nextInt(10000) + 60000;
		fillName("rent_price_3", "" + randomNum3);
		Random rand2 = new Random();
		Integer randomNum4 = rand2.nextInt(10000) + 40000;
		fillName("rent_price_4", "" + randomNum4);
		Integer randomNum5 = rand.nextInt(10000) + 20000;
		fillName("rent_bail", "" + randomNum5);
		sleep(1000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("/html/body/main/section[3]/div/div[2]/form/div[3]/div[2]/div[2]/div/label"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("rent-penalty-days"))).click();
		int randomday = rand.nextInt(10);
		fillName("rent_penalty_days", "" + randomday);
		/*
		 * int randompenalty = rand.nextInt(90)+10; fillName("rent_penalty_percentage",
		 * ""+randompenalty);
		 */
		fillName("fuel_combined", "8.5");
		int randomzip = rand.nextInt(89) + 10;

		while (13 <= randomzip && randomzip <= 19) {
			randomzip = rand.nextInt(89) + 10;
		}

		fillName("loc_zip_id_ac", "" + randomzip);

		try {

			driver.findElement(By.cssSelector(".ui-menu-item")).click();

		} catch (NoSuchElementException e) {

			randomzip = rand.nextInt(89) + 10;
			while (13 <= randomzip && randomzip <= 19) {
				randomzip = rand.nextInt(89) + 10;
			}
			fillName("loc_zip_id_ac", "" + randomzip);
			click(".ui-menu-item");

		}

		sleep(2000);
		driver.findElement(By.id("rent-description")).clear();
		driver.findElement(By.id("rent-description")).sendKeys(getRandomText(50));

		fillName("car_user[mobile]", "12345678");

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

		} catch (NoSuchElementException e) {

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
		fillName("rent_price_to", "" + randomNum1);
		fillName("loc_zip_id_ac", "" + randomzip);
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
		// onScreen(""+randompenalty);
		onScreen("8.5");
		String rentURLfromrp = driver.getCurrentUrl();
		Log.log("Az autó szerepel a Bérautó listában");
		// driver.findElement(By.xpath("//div[@class='col-lg adverter
		// px-2']/div[@class='col pl-0 item-footer subscript']/a[@class='col btn
		// btn-secondary']")).click();
		sleep(2000);
		clickLinkWithText("Érdekel");
		sleep(3000);

		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
		Date systemDate = new Date();

		Calendar c = Calendar.getInstance();
		c.setTime(systemDate);
		c.add(Calendar.DATE, 1);
		Date currendDatePlusOne = c.getTime();

		Calendar c2 = Calendar.getInstance();
		c2.setTime(systemDate);
		c2.add(Calendar.DATE, 8);
		Date currentDatePlusEight = c2.getTime();

		String startDate = formatDate.format(currendDatePlusOne);
		String endDate = formatDate.format(currentDatePlusEight);

		sleep(3000);

		fillName("start_date", startDate);
		sleep(1000);

		driver.findElement(By.name("end_date")).click();
		driver.findElement(By.name("end_date")).clear();
		fillName("end_date", endDate);
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

		fillName("last_name", "Vezeték");
		fillName("first_name", "Kereszt");
		fillName("mothers_name", "Anyja");
		fillName("driving_licence_number", "123456789");
		fillName("personal_ident", "123456AB");
		driver.findElement(By.id("birth")).sendKeys("1970-10-10");
		driver.findElement(By.id("birth")).sendKeys(Keys.ENTER);

		try {

			driver.findElement(By.xpath("//*[contains(text(),'Vezetéknév')]"));

		} catch (NoSuchElementException e) {

			driver.findElement(By.xpath("(//span[@class='switch'])[2]")).click();
			sleep(3000);

		}

		try {

			fillName("car_address[loc_zip_id_ac]", "" + randomzip);
			click(".ui-menu-item");

		} catch (NoSuchElementException e) {

			randomzip = rand.nextInt(89) + 10;
			fillName("car_address[loc_zip_id_ac]", "" + randomzip);
			click(".ui-menu-item");

		}

		driver.findElement(By.name("car_address[street]")).sendKeys("Teszt");
		driver.findElement(By.name("car_address[street_type]")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("car_address[street_type]"))).click();
		Select st = new Select(driver.findElement(By.name("car_address[street_type]")));
		sleep(1000);
		randomNum9 = rand.nextInt(1) + 1;
		st.selectByIndex(randomNum9);
		fillName("car_address[street_num]", "11");
		fillName("car_address[building]", "A");
		fillName("car_address[floor]", "1");
		fillName("car_address[door]", "1");
		driver.findElement(By.cssSelector(".mb-3.col.btn.btn-primary")).click();
		Log.log("Bérlés kérelem kitöltve");
		sleep(3000);

		onScreenAlert("Sikeres foglalás létrehozás!");
		sleep(2000);

		goToPage(carURL);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".fas.fa-pencil-alt"))).click();
		sleep(2000);

		sleep(5000);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + randomNum1 + "']")));
		System.out.println(randomNum1);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("" + randomNum1));
		Log.log("Képernyőn: " + randomNum1);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + randomNum2 + "']")));
		System.out.println(randomNum2);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("" + randomNum2));
		Log.log("Képernyőn: " + randomNum2);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + randomNum3 + "']")));
		System.out.println(randomNum3);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("" + randomNum3));
		Log.log("Képernyőn: " + randomNum3);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + randomNum4 + "']")));
		System.out.println(randomNum4);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("" + randomNum4));
		Log.log("Képernyőn: " + randomNum4);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + randomNum5 + "']")));
		System.out.println(randomNum5);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("" + randomNum5));
		Log.log("Képernyőn: " + randomNum5);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + zipValue + "']")));
		System.out.println(zipValue);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("" + zipValue));
		Log.log("Képernyőn: " + zipValue);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + 8.5 + "']")));
		System.out.println(8.5);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("" + 8.5));
		Log.log("Képernyőn: " + 8.5);

		goToPage(TestBase.url + "/hu/foglalasaim");
		click("i.fa-eye");
		onScreen("8 nap");

		Log.log("Adatok leellenőrízve");

//1--------

//2------
		Log.log("Feltőltás Új Adatokkal");

		goToPage(carURL);
		driver.findElement(By.cssSelector(".fas.fa-pencil-alt")).click();
		randomNum1 = rand.nextInt(10000) + 100000;
		fillName("rent_price_1", "" + randomNum1);
		randomNum2 = rand.nextInt(10000) + 80000;
		fillName("rent_price_2", "" + randomNum2);
		randomNum3 = rand.nextInt(10000) + 60000;
		fillName("rent_price_3", "" + randomNum3);
		rand2 = new Random();
		randomNum4 = rand2.nextInt(10000) + 40000;
		fillName("rent_price_4", "" + randomNum4);
		randomNum5 = rand.nextInt(10000) + 20000;
		fillName("rent_bail", "" + randomNum5);
		sleep(1000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("/html/body/main/section[3]/div/div[2]/form/div[3]/div[2]/div[2]/div/label"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("/html/body/main/section[3]/div/div[2]/form/div[3]/div[2]/div[1]/div/label"))).click();
		fillName("fuel_combined", "8.5");
		randomzip = rand.nextInt(89) + 10;

		while (13 <= randomzip && randomzip <= 19) {
			randomzip = rand.nextInt(89) + 10;
		}

		fillName("loc_zip_id_ac", "" + randomzip);

		try {

			driver.findElement(By.cssSelector(".ui-menu-item")).click();

		} catch (NoSuchElementException e) {

			randomzip = rand.nextInt(89) + 10;
			while (13 <= randomzip && randomzip <= 19) {
				randomzip = rand.nextInt(89) + 10;
			}
			fillName("loc_zip_id_ac", "" + randomzip);
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

		} catch (NoSuchElementException e) {

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
		fillName("rent_price_to", "" + randomNum1);
		fillName("loc_zip_id_ac", "" + randomzip);
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
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("" + randomNum1));
		Log.log("Képernyőn: " + randomNum1);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + randomNum2 + "']")));
		System.out.println(randomNum2);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("" + randomNum2));
		Log.log("Képernyőn: " + randomNum2);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + randomNum3 + "']")));
		System.out.println(randomNum3);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("" + randomNum3));
		Log.log("Képernyőn: " + randomNum3);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + randomNum4 + "']")));
		System.out.println(randomNum4);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("" + randomNum4));
		Log.log("Képernyőn: " + randomNum4);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + randomNum5 + "']")));
		System.out.println(randomNum5);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("" + randomNum5));
		Log.log("Képernyőn: " + randomNum5);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + zipValue + "']")));
		System.out.println(zipValue);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("" + zipValue));
		Log.log("Képernyőn: " + zipValue);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='" + 8.5 + "']")));
		System.out.println(8.5);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("" + 8.5));
		Log.log("Képernyőn: " + 8.5);

		Log.log("Adatok leellenőrízve");

		sleep(1000);
		clickLinkWithText("Garázs");

		Log.log("Visszalépés a Garázsba");

		goToPage(TestBase.url + "/hu/foglalasaim");

		driver.findElement(By.cssSelector(".fas.fa-long-arrow-alt-left")).click();
		goToPage(TestBase.url + "/hu/foglalasaim");
		click("i.fa-trash");
		click("a[data-apply=\"confirmation\"]");
		Log.log("Foglalás Sikeresen Törölve");
		Log.log("Ugrás az autóbérlés modosításhoz");
		goToPage(carURL);
		sleep(3000);
		driver.findElement(By.cssSelector(".fas.fa-pencil-alt")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("/html/body/main/section[3]/div/div[2]/form/div[3]/div[2]/div[1]/div/label"))).click();
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

		goToPage(url + "/hu/ceg-oldal-szerkesztes");

		if (driver
				.findElements(By.xpath(
						"//a/descendant-or-self::*[contains(text(),\"kattintson ide cégoldala létrehozásához\")]"))
				.size() != 0) {
			clickLinkWithText("kattintson ide cégoldala létrehozásához");
		}

		clickLinkWithText("Fejléc szerkesztése");
		Log.log("Fejléc szerkesztése");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name=\"logo_text\"]")));
		int rand = new Random().nextInt(500) + 500;
		String logoText = "Logo TExt " + rand;
		fillName("logo_text", logoText);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name=\"logo_slogan\"]")));
		rand = new Random().nextInt(500) + 600;
		String logoSlogen = "Logo Slogan " + rand;
		fillName("logo_slogan", logoSlogen);
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
		if (menupontok == 0) {
			click("#car-company-page-menus-add");
			sleep(1000);
			click("#car-company-page-menus-add");
			sleep(1000);
			click("#car-company-page-menus-add");
			sleep(1000);
			Log.log("3 elem hozzáadása");

			rand = new Random().nextInt(10);

			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.cssSelector("input[name=\"car_company_page_menus[2][title]\"]")));
			String firstMenu = "Elso menu" + rand;
			fillName("car_company_page_menus[0][title]", firstMenu);
			randomSelect("car_company_page_menus[0][menu_modul]");

			String secondMenu = "Masodik menu" + rand;
			fillName("car_company_page_menus[1][title]", secondMenu);
			randomSelect("car_company_page_menus[1][menu_modul]");

			String thirdMenu = "Harmadik menu" + rand;
			fillName("car_company_page_menus[2][title]", thirdMenu);
			randomSelect("car_company_page_menus[2][menu_modul]");
			Log.log("3 elem részletezés");

			driver.findElement(By.cssSelector(".btn.btn-primary.submitBtn.tsLoadingIcon")).click();
			Log.log("Menü mentése");
			onScreen(firstMenu);
			onScreen(secondMenu);
			onScreen(thirdMenu);

		} else {

			Log.log("Már ki van töltve");
			sleep(2000);
			driver.findElement(By.xpath("html/body/div/div/div/div/form/div/div/div/div")).click();

		}

		sleep(2000);
		driver.findElement(By.xpath("/html/body/main/div/div/div[3]/h4/a")).click();
		Log.log("Bemutatkozás szerkesztése");
		sleep(2000);
		rand = new Random().nextInt(30);
		String aboutUstitle = "AboutUs" + rand;
		fillName("about_us_title", aboutUstitle);
		String aboutUs = "AboutUs" + rand;
		fillName("about_us", aboutUs);
		driver.findElement(By.cssSelector(".btn.btn-primary.submitBtn.tsLoadingIcon")).click();
		Log.log("Bemutatkozás mentve");
		sleep(2000);

		clickLinkWithText("új oldal hozzáadása");
		rand = new Random().nextInt(30);
		String Title = "Title Example" + rand;
		sleep(2000);
		// wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div/div/form//div/div/div/div/div/input"))).sendKeys(Title);
		fillName("title", Title);
		String ContentIfr = "Ez egy Példa tartalom :)" + rand;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("content_ifr"))).sendKeys(ContentIfr);
		driver.findElement(By.cssSelector(".btn.btn-primary.submitBtn.tsLoadingIcon")).click();
		sleep(3000);
		goToPage(url + "/hu/ceg-oldal-szerkesztes");
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
		fillName("phone", "701234567");
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
		fillName("phone", "709876543");
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

		fillName("ctl00$MainContent$control_mileage", "12030");

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

		driver.findElement(
				By.name("ctl00$MainContent$myEquipmentControl$PaneOptional_content$controlOptionalEquipmentList$0"))
				.click();
		Log.log("Kategória Választása");

		driver.findElement(By.name("ctl00$MainContent$controlForwardToValuation")).click();
		Log.log("Kategória Választása");

		driver.findElement(By.id("MainContent_controlForwardToValuation")).click();
		Log.log("Tovább a Értékelés Típusának kiválasztásához / Fizetéséhez.");
		sleep(1000);
		fillName("ctl00$MainContent$PaneValuation_content$controlEmailAddress", "tesz@teszt.hu");

		Log.log("Email cím megadása");
		driver.findElement(By.name("ctl00$MainContent$PaneValuation_content$chkUserConsent")).click();

		Log.log("A felhasználói feltételeket és az adatvédelmi nyilatkozatot elolvastam megértettem és elfogadom");
		driver.findElement(By.id("MainContent_ControlButtonCCbasic")).click();
		sleep(3000);

		driver.switchTo().defaultContent();
		driver.switchTo().frame(driver.findElement(By.tagName("iframe")));

		sleep(3000);

		fillName("ctl00$MainContent$txtBankcardOwnerName", "Teszt Ember");
		fillName("ctl00$MainContent$txtBankcardOwnerEmail", "teszt@gmail.com");
		fillName("ctl00$MainContent$controlTextBoxCegnev", "Kovács Józsi");
		fillName("ctl00$MainContent$controlTextBoxAdoszam", "123456789");
		fillName("ctl00$MainContent$controlTextBoxCimTelepules", "Budapest");
		fillName("ctl00$MainContent$controlTextBoxCimIranyitoszam", "1052");
		fillName("ctl00$MainContent$controlTextBoxCimKozteruletNeve", "Táncsics Mihály Utca 76");
		driver.findElement(By.name("ctl00$MainContent$ControlForwardToCCPayment")).click();
		Log.log("Fizetés elindítása");
		sleep(2000);
		goToPage(url + "/hu/garazs");
		Log.log("Fizetés Megszakítva Vissza a kezdőlapra!");
	}

	public static void addGPS() throws IOException, InterruptedException {

		List<WebElement> elements = driver.findElements(By.cssSelector(".card .profile-car-item"));
		List<String> numberPlates = new ArrayList<String>();
		List<String> gpsCodes = new ArrayList<String>();

		gpsCodes.add("864893031557658");
		gpsCodes.add("864893031571469");
		gpsCodes.add("864895030889255");

		Log.log("Element: " + elements);
		
		for (WebElement element : elements) {
			
			Log.log("Element: " + element);
			numberPlates.add(element.findElement(By.className("numberplate")).getText());
		}

		goToPage(url + "/hu/kijelentkezes");
		sleep(3000);
		onScreenAlert("Sikeres kijelentkezés!");
		
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
		sleep(3000);
		onScreenAlert("Sikeres kijelentkezés!");
		
		TestBase.login(TestBase.personalUser, TestBase.personalPassword);
		clickLinkWithText(numberPlates.get(0));

	}

	public static void passShepherd() {
		try {
			driver.findElement(By.className("shepherd-button")).click();
		} catch (NoSuchElementException e) {

		}
	}

	public static void cronRun() throws IOException, InterruptedException {
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
		WebElement lastNotification = driver
				.findElement(By.cssSelector(".nav-notifications .dropdown-menu a:nth-child(1) .notification-title"));
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
		goToPage(url + "/hu/autopalya-matrica-hozzadasa/" + getCarId());

		LocalDate dueDate = LocalDate.now().plusDays(days);

		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-LL-dd");
		String strDueDate = dueDate.format(dateFormat);
		fillName("start_date", strDueDate);

		List<WebElement> list = driver.findElements(By.cssSelector("input[type=\"radio\"]"));
		int size = list.size();
		int randNumber = new Random().nextInt(size - 1) + 1;
		String id = list.get(randNumber).getAttribute("id");
		// list.get(randNumber).click();
		driver.findElement(By.xpath("//label[@for='" + id + "']")).click();
		String name = driver.findElement(By.cssSelector("label[for=\"" + id + "\"] .ticket-name i")).getText();
		String expiration = driver.findElement(By.cssSelector("label[for=\"" + id + "\"] .ticket-expiration"))
				.getText();
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
		wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("(//*[text()='ECDH Esemény értesítő To:" + personalUser + "'])[2]")));
		driver.findElement(By.xpath("(//*[text()='ECDH Esemény értesítő To:" + personalUser + "'])[2]")).click();

		int found1 = driver.findElements(By.xpath("//*[contains(text(), 'jogosítványod lejárata')]")).size();
		String string = day + " nap múlva esedékes lesz";
		int found2 = driver.findElements(By.xpath("//*[contains(text(), '" + string + "')]")).size();
		assertTrue("Email kiment a jogosítvány lejártáról " + day + " nap", found1 > 0 & found2 > 0);
		Log.log("Email kiment jogosítvány lejártáról +" + day + " nap");
		driver.get("https://accounts.google.com/Logout");
	}

	public static void addNewCarEventOdometerReading() throws IOException, InterruptedException {

		clickLinkWithText("esemény hozzáadása");

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-mycar_other_event")));

		click(".sprite-mycar_other_event");

		sleep(1000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("date"))).click();
		driver.findElement(By.cssSelector(".logo-title.d-none.d-md-inline-block.ml-3")).click();

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
		 */
		sleep(3000);
		driver.findElement(By.cssSelector(".fas.fa-trash.circle")).click();
		clickLinkWithText("Esemény törlése");

		Log.log("Esemény: Km óra állás sikeresen törölve.");

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
		sleep(4000);
		driver.findElement(By.xpath(
				"/html/body/main/section[2]/div/div/div[2]/div/form/div[2]/div[1]/div[3]/div/div/div[1]/ul/li[1]/div/div[1]/table/thead/tr[1]/th[1]"))
				.click();
		sleep(4000);
		/*driver.findElement(By.xpath(
				"/html/body/main/section[2]/div/div/div[2]/div/form/div[2]/div[1]/div[3]/div/div/div[1]/ul/li[1]/div/div[2]/table/tbody/tr/td/span[3]"))
				.click();*/
		driver.findElement(By.xpath(
				"/html/body/main/section[2]/div/div/div[2]/div/form/div[2]/div[1]/div[3]/div/div/div[1]/ul/li[1]/div/div[1]/table/tbody/tr[2]/td[3]"))
				.click();
		//driver.findElement(By.id("hp-tax-date")).sendKeys(Keys.ENTER);

		int randPrice = new Random().nextInt(123456) + 1100;
		int randNumber = new Random().nextInt(500) + 1;

		sleep(1000);

		String taxPrice = "" + randPrice;
		fillName("price", taxPrice);
		String noteText = "Test note " + randNumber;
		fillName("note", noteText);
		sleep(3000);
		submit();

		sleep(2000);
		driver.findElement(By.xpath("//a[contains(text(), 'adatlapja')]")).click();
		sleep(3000);

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

		sleep(2000);
		driver.findElement(By.xpath("//a[contains(text(), 'adatlapja')]")).click();
		sleep(3000);

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
				"/html/body/main/section[2]/div/div/div[2]/div/form/div[2]/div[1]/div[3]/div/div/div[1]/ul/li[1]/div/div[1]/table/thead/tr[1]/th[1]"))
				.click();
		sleep(2000);
		/*driver.findElement(By.xpath(
				"/html/body/main/section[2]/div/div/div[2]/div/form/div[2]/div[1]/div[3]/div/div/div[1]/ul/li[1]/div/div[2]/table/tbody/tr/td/span[3]"))
				.click();*/
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

		sleep(2000);
		driver.findElement(By.xpath("//a[contains(text(), 'adatlapja')]")).click();
		sleep(3000);

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
		
		sleep(2000);
		driver.findElement(By.xpath("/html/body/header/div/div/div[2]/div[4]/a")).click();
		sleep(2000);
		driver.findElement(By.className("sprite-invite")).click();
		sleep(3000);
		// clickLinkWithText("Új meghívó link");
		try {

			driver.findElement(By.xpath("//*[contains(text(),'Új meghívó link')]")).click();

		} catch (NoSuchElementException e) {

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
		
		
		sleep(2000);
		driver.get("https://mail.ecdh.hu/hpronto/");
		Log.log("Open Pronto");
		sleep(4000);
		driver.findElement(By.cssSelector("input[type=text]")).sendKeys(testerMail);
		Log.log("Fill username");
		driver.findElement(By.cssSelector("input[type=password]")).sendKeys(testerPassword);
		Log.log("Fill password");
		sleep(2000);
		clickXpath("//input[@type='submit']");
		Log.log("Login Pronto");

		sleep(6000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@title='ECDH test']")));
		
		sleep(3000);
		
		driver.switchTo().frame(driver.findElements(By.tagName("iframe")).get(1));
		new WebDriverWait(driver, 20).until( ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Jól hangzik, kezdjük!')]"))).click();
		
		//wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//*[text()='Teljeskörű autókezelő alkalmazás (meghívó az ECDH.hu-ra) To:" + testerMail + "'])[2]")));
		//driver.findElement(By.xpath("(//*[text()='Teljeskörű autókezelő alkalmazás (meghívó az ECDH.hu-ra) To:" + testerMail + "'])[2]")).click();
		//wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(), 'Jól hangzik, kezdjük!')]")));
		//driver.findElement(By.xpath("//a[contains(text(), 'Jól hangzik, kezdjük!')]")).click();
		
		Log.log("Meghívás elfogadása");
		goToPage(inviteLink);

		/*try {
			driver.switchTo().alert().accept();
			goToPage(inviteLink);
			sleep(3000);
		} catch (NoSuchElementException e) {

		}
		try {
			driver.switchTo().alert().accept();
			goToPage(inviteLink);
			sleep(3000);
		} catch (NoSuchElementException e) {

		}

		try {
			element = driver.findElement(By.className("ok"));
			element.click();
		} catch (NoSuchElementException e) {

		}*/
		
		
		
		Log.log("Accept cookies");

		try {
			
		fillName("user[username]", testerMail);
		
		}catch(NoSuchElementException e) {
			
			for (String winHandle : driver.getWindowHandles()) {
				System.out.println(winHandle);
				driver.switchTo().window(winHandle);
			}

			sleep(3000);
			fillName("user[username]", testerMail);
			
		}
		
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

		driver.get("https://mail.ecdh.hu/hpronto/");
		Log.log("Open Pronto");
		sleep(4000);
		driver.findElement(By.cssSelector("input[type=text]")).sendKeys(testerMail);
		Log.log("Fill username");
		driver.findElement(By.cssSelector("input[type=password]")).sendKeys(testerPassword);
		Log.log("Fill password");
		sleep(2000);
		clickXpath("//input[@type='submit']");
		Log.log("Login Pronto");

		sleep(6000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@title='ECDH test']")));
		
		sleep(3000);
		
		driver.switchTo().frame(driver.findElements(By.tagName("iframe")).get(1));
		new WebDriverWait(driver, 20).until( ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Személyes fiók aktiválása')]"))).click();
		
		Log.log("New user account activation");
		sleep(5000);

		System.out.println(driver.getTitle());

		for (String winHandle : driver.getWindowHandles()) {
			System.out.println(winHandle);
			driver.switchTo().window(winHandle);
		}

		System.out.println(driver.getTitle());

		passShepherd();
		Log.log("Activation succeed");

	}

	public static void addNewTire() throws IOException, InterruptedException {

		sleep(4000);
		clickLinkWithText("Új gumi hozzáadása");

		// Gumi felvitel
		String width = randomSelect("width");
		String height = randomSelect("height");
		String diameter = randomSelect("diameter");
		String type = randomSelect("type");

		Random rand = new Random();
		int randomPrice = rand.nextInt(40000) + 20000;
		String price = "" + randomPrice;
		fillName("price", price);
		String mufacturer = randomSelect("mufacturer");
		String modelName = "test model";
		fillName("item_description", modelName);
		select("number", "4");
		String worn = randomSelect("worn");
		String dotWeek = randomSelect("dot_week");
		String dotYear = randomSelect("dot_year");
		String td1full = randomSelect("thread_depth_1");
		String td2full = randomSelect("thread_depth_2");
		String td3full = randomSelect("thread_depth_3");
		String td4full = randomSelect("thread_depth_4");

		String td1 = td1full.substring(0, td1full.length() - 3);
		String td2 = td2full.substring(0, td2full.length() - 3);
		String td3 = td3full.substring(0, td3full.length() - 3);
		String td4 = td4full.substring(0, td4full.length() - 3);

		int randomNum = rand.nextInt(40000) + 500;

		String noteText = "Note " + randomNum;
		fillName("tire_storage", noteText);

		// felvitel vége
		// driver.findElement(By.cssSelector(".btn.btn-primary.submitBtn.tsLoadingIcon")).click();
		clickXpath("//div[@class='card-body']//button[@type='submit']");

		sleep(3000);

		Log.log("Gumi sikeresen hozzáadva");
		sleep(3000);

		// megtekintés a garázsban

		onScreenWS(type);
		onScreenWS(mufacturer);
		onScreenWS("4 db");

		goToPage(TestBase.url + "/hu/gumik/" + getCarId());
		sleep(3000);

		// megtekintés a gumi tárban

		onScreen(type);
		onScreen(mufacturer);
		onScreen(td1 + "/" + td2 + "/" + td3 + "/" + td4 + " mm");

		sleep(2000);
		driver.findElement(By.xpath("//i[@class='fas fa-pencil-alt circle']")).click();
		sleep(3000);

		// megtekintés az űrlapot

		onScreenSelected(width);
		onScreenSelected(height);
		onScreenSelected(diameter);
		onScreenSelected(type);
		onScreenValue(price);
		onScreenSelected(mufacturer);
		onScreenValue(modelName);
		onScreenSelected("4");
		onScreenSelected(worn);
		onScreenSelected(dotWeek);
		onScreenSelected(dotYear);
		onScreenSelected(td1full);
		onScreenSelected(td2full);
		onScreenSelected(td3full);
		onScreenSelected(td4full);
		onScreen(noteText);

		sleep(3000);

		// űrlap adatok módosítása

		width = randomSelect("width");
		height = randomSelect("height");
		diameter = randomSelect("diameter");
		type = randomSelect("type");

		randomPrice = rand.nextInt(40000) + 20000;
		price = "" + randomPrice;
		fillName("price", price);
		mufacturer = randomSelect("mufacturer");
		modelName = "test model";
		fillName("item_description", modelName);
		select("number", "4");
		worn = randomSelect("worn");
		dotWeek = randomSelect("dot_week");
		dotYear = randomSelect("dot_year");
		td1full = randomSelect("thread_depth_1");
		td2full = randomSelect("thread_depth_2");
		td3full = randomSelect("thread_depth_3");
		td4full = randomSelect("thread_depth_4");

		td1 = td1full.substring(0, td1full.length() - 3);
		td2 = td2full.substring(0, td2full.length() - 3);
		td3 = td3full.substring(0, td3full.length() - 3);
		td4 = td4full.substring(0, td4full.length() - 3);

		randomNum = rand.nextInt(40000) + 500;

		noteText = "Note " + randomNum;
		fillName("tire_storage", noteText);

		// módosítás vége

		sleep(2000);
		submit();
		sleep(2000);

		onScreenAlert("Sikeres Gumi módosítás");

		// módosított értékek megtekintés a garázsban

		onScreenWS(type);
		onScreenWS(mufacturer);
		onScreenWS("4 db");

		goToPage(TestBase.url + "/hu/gumik/" + getCarId());
		sleep(3000);

		// módosított értékek megtekintés a gumi tárban

		onScreen(type);
		onScreen(mufacturer);
		onScreen(td1 + "/" + td2 + "/" + td3 + "/" + td4 + " mm");

		sleep(2000);
		driver.findElement(By.xpath("//i[@class='fas fa-pencil-alt circle']")).click();
		sleep(3000);

		// módosított értékek megtekintés az űrlapot

		onScreenSelected(width);
		onScreenSelected(height);
		onScreenSelected(diameter);
		onScreenSelected(type);
		onScreenValue(price);
		onScreenSelected(mufacturer);
		onScreenValue(modelName);
		onScreenSelected("4");
		onScreenSelected(worn);
		onScreenSelected(dotWeek);
		onScreenSelected(dotYear);
		onScreenSelected(td1full);
		onScreenSelected(td2full);
		onScreenSelected(td3full);
		onScreenSelected(td4full);
		onScreen(noteText);

		sleep(3000);

		// Űrlap bezárás

		submit();

		sleep(3000);

		goToPage(TestBase.url + "/hu/gumik/" + getCarId());
		sleep(3000);

		// Gumi Törlés

		driver.findElement(By.xpath("//i[@class='fas fa-trash circle']")).click();
		sleep(3000);

		clickLinkWithText("Igen");

		onScreenAlert("A gumi törölve lett");

		Log.log("GUMIFELVITEL SIKERES TESZT!");

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
		driver.findElement(By.xpath("//*[@class='fc-view-container']//*[contains(text(),'20')]")).click();
		sleep(1000);
		Log.log("Nap kiválasztása");
		clickLinkWithText("Esemény hozzáadása");
		sleep(1000);
		int rand = new Random().nextInt(500) + 500;
		fillName("title", "Test esemény " + rand);
		String titleText = "Test esemény " + rand;
		fillName("description", "Test megjegyzés " + rand);
		String descText = "Test megjegyzés " + rand;

		fillName("cal_location", "" + "repülőtéri út 6");
		sleep(1000);

		driver.findElement(By.id("cal_location")).sendKeys(Keys.ARROW_DOWN);
		driver.findElement(By.id("cal_location")).sendKeys(Keys.ENTER);
		Log.log("Autocomplete mező kitöltése");

		driver.findElement(By.cssSelector(".btn.btn-primary.w-100")).click();

		Log.log("Naptári esemény sikeresen felvive");

		sleep(4000);
		driver.findElement(By.xpath("//*[@class='fc-view-container']//*[contains(text(),'20')]")).click();
		sleep(1000);
		driver.findElement(By.xpath("//*[contains(text(),'" + titleText + "')]")).click();
		onScreen(titleText);
		onScreen("20.");
		onScreen(descText);
		onScreen("Budapest, Repülőtéri út 6, Magyarország");
		Log.log("Ismétlődik?");
		onScreen("Nem");
		sleep(3000);
		//driver.findElement(By.cssSelector(".text-uppercase.btn.btn-secondary.popup")).click();
		clickLinkWithText("Szerkesztés");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("title")));

		onScreen(titleText);
		onScreen(descText);
		onScreen("20.");
		onScreen("Budapest, Repülőtéri út 6, Magyarország");
		driver.findElement(By.xpath("//label[contains(text(),'Egész napos')]")).click();
		rand = new Random().nextInt(500) + 500;
		fillName("title", "Test esemény " + rand);
		titleText = "Test esemény " + rand;
		fillName("description", "Test megjegyzés " + rand);
		descText = "Test megjegyzés " + rand;
		sleep(3000);
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[@id='popup-content']//*[contains(text(), 'Ismétlődik')]")))
				.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("recurring-data-interval")));
		fillName("recurring_data[interval]", "30");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("recurring-data-frequency"))).click();
		driver.findElement(By.id("recurring-data-frequency")).sendKeys(Keys.ARROW_DOWN);
		driver.findElement(By.id("recurring-data-frequency")).sendKeys(Keys.ARROW_DOWN);
		driver.findElement(By.id("recurring-data-frequency")).sendKeys(Keys.ARROW_DOWN);
		driver.findElement(By.id("recurring-data-frequency")).sendKeys(Keys.ENTER);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("recurring-data-end-condition"))).click();
		driver.findElement(By.id("recurring-data-end-condition")).sendKeys(Keys.ARROW_DOWN);
		driver.findElement(By.id("recurring-data-end-condition")).sendKeys(Keys.ENTER);
		fillName("recurring_data[count]", "1");

		sleep(3000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".btn.btn-primary.w-100"))).click();
		onScreen(titleText);
		onScreen("20.");
		onScreen(descText);
		onScreen("Budapest, Repülőtéri út 6, Magyarország");
		Log.log("Ismétlődik?");
		onScreen("Igen");

		driver.findElement(By.cssSelector(".fas.fa-trash.circle")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".btn.grayBtn.deleteAttachedItem")))
				.click();
		Log.log("A naptár esemény sikeresen törölve");

	}

	public static void gasStation() throws IOException, InterruptedException {

		driver.findElement(By.xpath("//div[@class='user-nav nav-menu']/a")).click();
		sleep(1000);
		driver.findElement(By.className("sprite-gas-stations")).click();
		sleep(2000);

		try {

			driver.findElement(By.xpath("/html/body/main/section/div[3]/div[2]/div/div[2]/a[1]")).click();

		} catch (NoSuchElementException e) {

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

		} catch (NoSuchElementException e) {

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

		} catch (NoSuchElementException e) {

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
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[contains(text(), 'SHELL')]")))
				.click();
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

			} catch (NoSuchElementException e) {

				Log.log("Márka szűrő Működik!");

			}

			driver.findElement(By.xpath("//a[div[contains(text(), 'SHELL')]][1]")).click();

		} catch (NoSuchElementException e) {

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

		String firstResult = "TestText";

		sleep(2000);
		clickLinkWithText("Cégkereső");
		sleep(2000);
		clickButton("Keresés");
		sleep(2000);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));

		try {

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//*[@class='result-name'])[1]")));

		} catch (NoSuchElementException e) {

			Log.log("Nincs cég az oldalon!");
			driver.close();
			System.exit(0);

		}

		String firstCompany = driver.findElement(By.xpath("(//*[@class='result-name'])[1]")).getText();
		String secondCompany = driver.findElement(By.xpath("(//*[@class='result-name'])[2]")).getText();

		fillName("name", firstCompany);

		submit();
		sleep(3000);

		try {

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//*[@class='result-name'])[1]")));
			String record1 = driver.findElement(By.xpath("(//*[@class='result-name'])[1]")).getText();

			if (firstCompany.equals(record1)) {

				Log.log("Név szerinti kereső teszt1: jó");

			} else {

				Log.log("Név szerinti kereső teszt1: HIBA!");
				driver.close();
				System.exit(0);

			}

		} catch (NoSuchElementException e) {

			Log.log("Kereső nem ad találatokat!");
			driver.close();
			System.exit(0);

		}

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
		fillName("name", secondCompany);
		submit();
		sleep(3000);

		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//*[@class='result-name'])[1]")));
			String record2 = driver.findElement(By.xpath("(//*[@class='result-name'])[1]")).getText();

			if (secondCompany.equals(record2)) {

				Log.log("Név szerinti kereső teszt2: jó");

			} else {

				Log.log("Név szerinti kereső teszt2: HIBA!");
				driver.close();
				System.exit(0);

			}

		} catch (NoSuchElementException e) {

			Log.log("Kereső nem ad találatokat!");
			driver.close();
			System.exit(0);

		}

		Log.log("SIKERES CÉGKERESŐ TESZT");

	}

	public static void routePlanner() throws InterruptedException, IOException {

		goToPage(url + "/hu/cegkereso");

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));

		fillName("name", "teszt");
		submit();
		sleep(3000);

		Log.log("Cég keresés");

		driver.findElement(By.xpath("(//a/div[@class='result-name'])[1]")).click();

		try {

			driver.findElement(By.xpath("(//div[@class='col font-weight-bold border-right'])[1]")).click();

		} catch (NoSuchElementException e) {

			Log.log("Nincs céges weboldal");

		}

		// driver.findElement(By.cssSelector(".btn.btn-red-border.w-100.mt-3.font-weight-bold.text-uppercase")).click();
		sleep(2000);
		clickButton("Útvonaltervezés");
		// clickLinkWithText("Útvonaltervezés");
		sleep(3000);

		Random rand = new Random();
		int randomnum = rand.nextInt(89) + 10;

		fillName("to", "" + randomnum);
		sleep(1000);
		driver.findElement(By.id("to")).sendKeys(Keys.ARROW_DOWN);
		driver.findElement(By.id("to")).sendKeys(Keys.ENTER);
		sleep(2000);
		// driver.findElement(By.xpath("//button[@class='btn btn-primary
		// w-100']")).sendKeys(Keys.ENTER);
		clickButton("Indulás");
		sleep(2000);
		driver.findElement(By.xpath("//button[@class='gm-control-active gm-fullscreen-control']")).click();
		sleep(3000);
		// String Bpoint = driver.findElement(By.name("to")).getAttribute("value");
		driver.findElement(By.xpath("//map[@id='gmimap0']/area")).click();
		String Apoint = driver.findElement(By.xpath("//div[@class='gm-iw']")).getText();
		sleep(2000);
		driver.findElement(By.xpath("//map[@id='gmimap1']/area")).click();
		String Bpoint = driver.findElement(By.xpath("//div[@class='gm-iw']")).getText();
		sleep(2000);
		Log.log("Képernyőn A pont: " + Apoint);
		sleep(1000);
		Log.log("Képernyőn B pont: " + Bpoint);

		// POI_PONT_EXIT
		driver.findElement(By.xpath("(//button[@class='gm-ui-hover-effect'])[1]"));

		sleep(2000);
		Log.log("A pontok kattinthatóak Útvonal terv leellenőrízve");

		goToPage(url + "/hu/garazs");
		sleep(2000);

		Log.log("SIKERES ÚTVONALTERVEZŐ TESZT");

	}

	public static void CarTransmission() throws IOException, InterruptedException {

		String carPot = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='half-box'][2]/dd")))
				.getText();
		String make = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='half-box'][1]/dd")))
				.getText();
		String Cm3 = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='half-box'][2]/dd")))
				.getText();
		String km = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//dd[4]/a[@class='d-block mb-2']")))
				.getText();
		sleep(2000);
		Log.log("Autó Átadás elindítása!");
		clickLinkWithText("Autóm eladása");
		fillName("buyer_email", companyUser);
		submit();

		sleep(2000);

		goToPage(url + "/hu/eladas-kerelmek-atadasok");
		String carName = wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='col-12 col-sm-6 col-lg'][1]/a")))
				.getText();
		driver.findElement(By.id("userMenu")).click();
		clickLinkWithText("Kijelentkezés");
		Log.log("Kijelentkezés!");
		sleep(2000);
		Log.log("Bejelentkezés!");
		TestBase.login(TestBase.companyUser, TestBase.companyPassword);
		click(".fas.fa-bell");
		sleep(2000);

		clickXpath("//*[text()[contains(.,'Autó átvétel')]]");

		onScreen(carName);
		Log.log("Autó Átvétel Elfogadása!");
		click(".fa.fa-check.circle");
		click(".btn-secondary");

		sleep(2000);
		
		clickXpath("//div[@class='col-12 col-sm-6 col-lg'][1]/a");
		sleep(2000);

		Log.log("Autó Adatainak ellenőrzése!");
		onScreen(carPot);
		onScreen(Cm3);
		onScreen(km);
		click(".fas.fa-long-arrow-alt-left");
		Log.log("Sikeres Autó Átvétel!");

	}

	public static void companyRate() throws IOException, InterruptedException {

		goToPage(url + "/hu/szolgaltatas-kereso");
		sleep(3000);

		/*
		 * wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
		 * Boolean goodFilter = false; Select profileSelect = new
		 * Select(driver.findElement(By.id("profiles"))); Log.log(""+goodFilter);
		 * 
		 * while(goodFilter == false) {
		 * 
		 * int currentIndex = 1; profileSelect.selectByIndex(currentIndex); String
		 * currentProfile = profileSelect.getFirstSelectedOption().getText(); submit();
		 * sleep(2000);
		 * 
		 * try{
		 * 
		 * wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
		 * "(//section//span/a)[1]"))); clickLinkWithText("Tovább az adatlapra");
		 * goodFilter = true; sleep(4000);
		 * 
		 * }catch(NoSuchElementException e) {
		 * 
		 * Log.log("Nincs találat erre a tevékenységi körre: "+currentProfile);
		 * currentIndex++;
		 * 
		 * }
		 * 
		 * }
		 */

		driver.findElement(By.xpath("//div[@class='row result-list company-result-item align-items-center'][1]//div[@class='result-name']"))
				.click();
		sleep(4000);

		driver.findElement(By.xpath("//*[contains(text(), 'Részletes értékelés')]")).click();

		sleep(3000);

		driver.findElement(By
				.xpath("(//span[@class='empty-stars']/span[@class='star'][3])[2]"))
				.click();
		driver.findElement(By
				.xpath("(//span[@class='empty-stars']/span[@class='star'][3])[3]"))
				.click();
		driver.findElement(By
				.xpath("(//span[@class='empty-stars']/span[@class='star'][3])[4]"))
				.click();
		driver.findElement(By
				.xpath("(//span[@class='empty-stars']/span[@class='star'][3])[5]"))
				.click();
		driver.findElement(By
				.xpath("(//span[@class='empty-stars']/span[@class='star'][3])[6]"))
				.click();
		sleep(1000);
		int rateTextNum = new Random().nextInt(500) + 100;
		driver.findElement(By.xpath("//textarea")).sendKeys("Teszt értékelő szöveg " + rateTextNum);
		sleep(2000);

		clickButton("Értékelés elküldése");
		//onScreenAlert("Sikeres értékelés mentés");
		
		sleep(2000);

		try {

			wait.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath("(//div[@class='rating-stars'])[1]//span[@class='filled-stars' and contains(@style,'width: 60%')]")));
			Log.log("Értékelés összegző helyes eredmény");

		} catch (NoSuchElementException e) {

			Log.log("Értékelés összegző hiba!");
			driver.close();
			System.exit(0);

		}

	}

	public static void dgNewPartner() throws IOException, InterruptedException {

		clickLinkWithText("Új partner felvétele");
		sleep(2000);
		Log.log("Partner felvétel...");
		fillName("last_name", "TesztCsalád");
		fillName("first_name", "TesztVezeték");
		fillName("personal_ident", "123456AB");
		fillName("mothers_name", "Partner Anyu");
		fillName("birth_date", "1956-03-11");
		fillName("birth_place", "Budapest");
		fillName("nationality", "Magyar");
		fillName("email", "test@email.com");
		fillName("phone", "12345678");
		fillName("car_address[loc_zip_id_ac]", "1052");
		sleep(3000);
		driver.findElement(By.id("car-address-loc-zip-id")).sendKeys(Keys.ENTER);
		sleep(3000);
		fillName("car_address[street]", "Sas");
		driver.findElement(By.id("car-address-street-type")).click();
		sleep(1000);
		driver.findElement(By.id("car-address-street-type")).sendKeys(Keys.ARROW_DOWN);
		sleep(1000);
		driver.findElement(By.id("car-address-street-type")).sendKeys(Keys.ENTER);
		sleep(1000);
		fillName("car_address[street_num]", "25");
		fillName("car_address[building]", "A");
		fillName("car_address[floor]", "2");
		fillName("car_address[door]", "204");
		driver.findElement(By.xpath("//section//button[@type='submit']")).click();
		sleep(2000);

	}

	public static String[] Tanu1 = { "Tanú 1", "234567CD", "Repülőtéri út 6/a" };
	public static String[] Tanu2 = { "Tanú 2", "345678EF", "Igazából ez bármi lehet" };

	public static void dgFillWitness() throws IOException, InterruptedException {

		fillName("witness1_name", Tanu1[0]);
		fillName("witness1_personal_ident", Tanu1[1]);
		fillName("witness1_address", Tanu1[2]);
		fillName("witness2_name", Tanu2[0]);
		fillName("witness2_personal_ident", Tanu2[1]);
		fillName("witness2_address", Tanu2[2]);

	}
	
	public static void giveDocumentGeneratorAvailability(String myCar) throws IOException, InterruptedException{

		goToPage(url + "/hu/kijelentkezes");
			
		adminLogin();
			
		goToPage(url + "/hu/admin/car/car-companies");
			
		fillName("quick_search", companyUser);
			
		clickButton("Keres");
		sleep(2000);
			
		clickXpath("(//a[@class='btn btn-default btn-link command command-edit'])[1]");
		sleep(2000);
			
		clickXpath("//*[contains(text(), 'Dokumentum kitöltő')]");
		sleep(1000);
			
		clickButton("Mentés");
		sleep(2000);
			
		onScreenWS("Sikeres cég módosítás");
			
		goToPage(url + "/hu/kijelentkezes");
		
		login(TestBase.companyUser, TestBase.companyPassword);
		sleep(2000);
		
		goToPage(url + "/hu/sajat-auto/" + myCar);
		
	}

	public static void dgSelectAllBuyDocument() throws IOException, InterruptedException {
		
		String myCar = getCarId();
		
		try {

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(), 'Dokumentum generáló')]")));

		} catch (NoSuchElementException e) {
		
			giveDocumentGeneratorAvailability(myCar);
			
		}
		
		Log.log("Dokumentum generáló elérhető");

		clickLinkWithText("Dokumentum generáló");
		sleep(3000);

		// vétel dokumentumok kiválasztása------------------------------------
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
		// vétel dokumentumok kiválasztása-------------------------------------

	}

	public static void dgBuyHandoverReceiptCar() throws IOException, InterruptedException {

		// goToPage(TestBase.url + "/hu/uj-dokumentum-kitoltese/" + getCarId() + "/1");

		// Átadás-átvételi:
		// Autó----------------------------------------------------------------------------
		String mFacturer = driver.findElement(By.xpath("//div[@class='car-manufacturer']")).getText();
		String plateNum = driver.findElement(By.id("car-plate-number")).getAttribute("value");
		String carVin = driver.findElement(By.id("car-vin")).getAttribute("value");

		if (carVin.length() < 17) {

			driver.findElement(By.id("car-vin")).sendKeys("12345678911234567");
			carVin = "12345678911234567";

		}

		int randKeys = new Random().nextInt(5) + 1000;
		fillName("car_keys", "" + randKeys);

		int randKomment = new Random().nextInt(500) + 1;
		fillName("note", "Teszt megjegyzés " + randKomment);

		// partner----------------------------------------------------
		sleep(2000);
		clickLinkWithText("Partner Kiválasztása");
		sleep(2000);

		try {

			driver.findElement(By.xpath("(//i[@class='fas fa-plus circle'])[1]")).click();

		} catch (NoSuchElementException e) {

			dgNewPartner();

		}

		// partner vége----------------------------------------------------

		String buyerName = driver.findElement(By.id("partner1-name")).getAttribute("value");
		String taxNum = driver.findElement(By.id("partner1-tax-no")).getAttribute("value");
		String regNum = driver.findElement(By.id("partner1-reg-no")).getAttribute("value");
		String address = driver.findElement(By.id("partner1-address")).getAttribute("value");
		sleep(3000);

		fillName("sign_city_id_ac", "Budapest");
		sleep(4000);
		driver.findElement(By.id("sign-city-id")).sendKeys(Keys.ENTER);
		sleep(1000);
		driver.findElement(By.id("sign-date")).click();
		driver.findElement(By.xpath("//form/div[4]/div[1]")).click();

		dgFillWitness();

		submit();

		try {

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='btn btn-lg btn-secondary w-100']")));
			clickText("Mentés és tovább");

		} catch (TimeoutException e) {

		}

		sleep(3000);

		onScreenWS(mFacturer);
		onScreenWS(plateNum);
		onScreenWS(carVin);
		onScreenWS("" + randKeys);
		onScreenWS("Teszt megjegyzés " + randKomment);
		onScreenWS(buyerName);
		onScreenWS(taxNum);
		onScreenWS(regNum);
		onScreenWS(address);
		onScreenWS("Budapest");
		onScreenWS(Tanu1[0]);
		onScreenWS(Tanu1[1]);
		onScreenWS(Tanu1[2]);
		onScreenWS(Tanu2[0]);
		onScreenWS(Tanu2[1]);
		onScreenWS(Tanu2[2]);

		driver.findElement(By.xpath("//div[contains(text(),'Következő')]")).click();
		sleep(2000);

	}

	public static void dgBuyHandoverReceiptAccessories() throws IOException, InterruptedException {

		// goToPage(TestBase.url + "/hu/uj-dokumentum-kitoltese/" + getCarId() + "/2");

		// Átadás-átvételi: Kellék
		// ----------------------------------------------------------------------------
		String mFacturer = driver.findElement(By.xpath("//div[@class='car-manufacturer']")).getText();
		String plateNum = driver.findElement(By.id("car-plate-number")).getAttribute("value");
		String carVin = driver.findElement(By.id("car-vin")).getAttribute("value");

		if (carVin.length() < 17) {

			driver.findElement(By.id("car-vin")).sendKeys("12345678911234567");
			carVin = "12345678911234567";

		}

		int randAcc = new Random().nextInt(5) + 1;
		fillName("acc_list", "Random kellék " + randAcc);

		int randDoc = new Random().nextInt(500) + 1;
		fillName("doc_list", "Még átvett: " + randDoc);

		// partner----------------------------------------------------
		sleep(2000);
		clickLinkWithText("Partner Kiválasztása");
		sleep(2000);

		try {

			driver.findElement(By.xpath("(//i[@class='fas fa-plus circle'])[1]")).click();

		} catch (NoSuchElementException e) {

			dgNewPartner();

		}

		// partner vége----------------------------------------------------

		String buyerName = driver.findElement(By.id("partner1-name")).getAttribute("value");
		String taxNum = driver.findElement(By.id("partner1-tax-no")).getAttribute("value");
		String regNum = driver.findElement(By.id("partner1-reg-no")).getAttribute("value");
		String address = driver.findElement(By.id("partner1-address")).getAttribute("value");
		sleep(3000);

		fillName("sign_city_id_ac", "Budapest");
		sleep(4000);
		driver.findElement(By.id("sign-city-id")).sendKeys(Keys.ENTER);
		sleep(1000);
		driver.findElement(By.id("sign-date")).click();
		driver.findElement(By.xpath("//form/div[4]/div[1]")).click();

		dgFillWitness();

		submit();

		try {

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='btn btn-lg btn-secondary w-100']")));
			clickText("Mentés és tovább");

		} catch (TimeoutException e) {

		}

		sleep(3000);

		onScreenWS(mFacturer);
		onScreenWS(plateNum);
		onScreenWS(carVin);
		onScreenWS("Random kellék " + randAcc);
		onScreenWS("Még átvett: " + randDoc);
		onScreenWS(buyerName);
		onScreenWS(taxNum);
		onScreenWS(regNum);
		onScreenWS(address);
		onScreenWS("Budapest");
		onScreenWS(Tanu1[0]);
		onScreenWS(Tanu1[1]);
		onScreenWS(Tanu1[2]);
		onScreenWS(Tanu2[0]);
		onScreenWS(Tanu2[1]);
		onScreenWS(Tanu2[2]);

		driver.findElement(By.xpath("//div[contains(text(),'Következő')]")).click();
		sleep(2000);

	}

	public static void dgBuyHandoverReceiptPrice() throws IOException, InterruptedException {

		// goToPage(TestBase.url + "/hu/uj-dokumentum-kitoltese/" + getCarId() + "/3");

		// Átadás-átvételi: Vételár
		// ----------------------------------------------------------------------------
		String mFacturer = driver.findElement(By.xpath("//div[@class='car-manufacturer']")).getText();
		String plateNum = driver.findElement(By.id("car-plate-number")).getAttribute("value");
		String carVin = driver.findElement(By.id("car-vin")).getAttribute("value");

		if (carVin.length() < 17) {

			driver.findElement(By.id("car-vin")).sendKeys("12345678911234567");
			carVin = "12345678911234567";

		}

		fillName("price", "" + 1500000);
		int price = 1500000;

		/*
		 * int randAcc = new Random().nextInt(5)+1;
		 * fillName("acc_list","Random kellék "+randAcc);
		 */

		int randDoc = new Random().nextInt(500) + 1;
		fillName("doc_list", "Még átvett: " + randDoc);

		// partner----------------------------------------------------
		sleep(2000);
		clickLinkWithText("Partner Kiválasztása");
		sleep(2000);

		try {

			driver.findElement(By.xpath("(//i[@class='fas fa-plus circle'])[1]")).click();

		} catch (NoSuchElementException e) {

			dgNewPartner();

		}

		// partner vége----------------------------------------------------

		String buyerName = driver.findElement(By.id("partner1-name")).getAttribute("value");
		String taxNum = driver.findElement(By.id("partner1-tax-no")).getAttribute("value");
		String regNum = driver.findElement(By.id("partner1-reg-no")).getAttribute("value");
		String address = driver.findElement(By.id("partner1-address")).getAttribute("value");
		sleep(3000);

		fillName("sign_city_id_ac", "Budapest");
		sleep(4000);
		driver.findElement(By.id("sign-city-id")).sendKeys(Keys.ENTER);
		sleep(1000);
		driver.findElement(By.id("sign-date")).click();
		driver.findElement(By.xpath("//form/div[4]/div[1]")).click();

		dgFillWitness();

		submit();

		try {

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='btn btn-lg btn-secondary w-100']")));
			clickText("Mentés és tovább");

		} catch (TimeoutException e) {

		}

		sleep(3000);

		onScreenWS(mFacturer);
		onScreenWS(plateNum);
		onScreenWS(carVin);
		// másfél millió ellenőrzés
		onScreenWS("500");
		// onScreenWS("Random kellék "+ randAcc);
		onScreenWS("Még átvett: " + randDoc);
		onScreenWS(buyerName);
		onScreenWS(taxNum);
		onScreenWS(regNum);
		onScreenWS(address);
		onScreenWS("Budapest");
		onScreenWS(Tanu1[0]);
		onScreenWS(Tanu1[1]);
		onScreenWS(Tanu1[2]);
		onScreenWS(Tanu2[0]);
		onScreenWS(Tanu2[1]);
		onScreenWS(Tanu2[2]);

		driver.findElement(By.xpath("//div[contains(text(),'Következő')]")).click();
		sleep(2000);

	}

	public static void dgBuyHandoverConsignerEject() throws IOException, InterruptedException {

		// goToPage(TestBase.url + "/hu/uj-dokumentum-kitoltese/" + getCarId() + "/4");

		// Bizományból kiadás
		// ----------------------------------------------------------------------------
		String mFacturer = driver.findElement(By.xpath("//div[@class='car-manufacturer']")).getText();
		String plateNum = driver.findElement(By.id("car-plate-number")).getAttribute("value");
		String carVin = driver.findElement(By.id("car-vin")).getAttribute("value");
		String carMileage = driver.findElement(By.id("car-mileage")).getAttribute("value");

		if (carVin.length() < 17) {

			driver.findElement(By.id("car-vin")).sendKeys("12345678911234567");
			carVin = "12345678911234567";

		}

		/*
		 * fillName("price",""+1500000); int price = 1500000;
		 */

		int randAcc = new Random().nextInt(5) + 1;
		fillName("acc_list", "Random kellék " + randAcc);

		int randDoc = new Random().nextInt(500) + 1;
		fillName("doc_list", "Még átvett: " + randDoc);

		int randKomment = new Random().nextInt(500) + 1;
		fillName("note", "Teszt megjegyzés " + randKomment);

		// partner----------------------------------------------------
		sleep(2000);
		clickLinkWithText("Partner Kiválasztása");
		sleep(2000);

		try {

			driver.findElement(By.xpath("(//i[@class='fas fa-plus circle'])[1]")).click();

		} catch (NoSuchElementException e) {

			dgNewPartner();

		}

		// partner vége----------------------------------------------------

		String buyerName = driver.findElement(By.id("partner1-name")).getAttribute("value");
		String taxNum = driver.findElement(By.id("partner1-tax-no")).getAttribute("value");
		String regNum = driver.findElement(By.id("partner1-reg-no")).getAttribute("value");
		String address = driver.findElement(By.id("partner1-address")).getAttribute("value");
		
		driver.findElement(By.id("partner1-phone")).click();
		
		fillName("partner1_phone", "55555555");
		sleep(3000);

		fillName("sign_city_id_ac", "Budapest");
		sleep(4000);
		driver.findElement(By.id("sign-city-id")).sendKeys(Keys.ENTER);
		sleep(1000);
		driver.findElement(By.id("sign-date")).click();
		driver.findElement(By.xpath("//form/div[4]/div[1]")).click();

		dgFillWitness();

		submit();

		try {

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='btn btn-lg btn-secondary w-100']")));
			clickText("Mentés és tovább");

		} catch (TimeoutException e) {

		}

		sleep(3000);

		onScreenWS(mFacturer);
		onScreenWS(plateNum);
		onScreenWS(carVin);
		onScreenWS(carMileage);
		// onScreenWS(""+price);
		onScreenWS("Random kellék " + randAcc);
		onScreenWS("Teszt megjegyzés " + randKomment);
		onScreenWS("Még átvett: " + randDoc);
		onScreenWS(buyerName);
		onScreenWS(taxNum);
		onScreenWS(regNum);
		onScreenWS(address);
		onScreenWS("555-555");
		onScreenWS("Budapest");
		onScreenWS(Tanu1[0]);
		onScreenWS(Tanu1[1]);
		onScreenWS(Tanu1[2]);
		onScreenWS(Tanu2[0]);
		onScreenWS(Tanu2[1]);
		onScreenWS(Tanu2[2]);

		driver.findElement(By.xpath("//div[contains(text(),'Következő')]")).click();
		sleep(2000);

	}

	public static void dgBuyHandoverConsignerContract() throws IOException, InterruptedException {

		// goToPage(TestBase.url + "/hu/uj-dokumentum-kitoltese/" + getCarId() + "/5");

		// Bizományi szerződés
		// ----------------------------------------------------------------------------
		String mFacturer = driver.findElement(By.xpath("//div[@class='car-manufacturer']")).getText();
		String plateNum = driver.findElement(By.id("car-plate-number")).getAttribute("value");
		String carVin = driver.findElement(By.id("car-vin")).getAttribute("value");
		String carMotorNumber = driver.findElement(By.id("car-motor-number")).getAttribute("value");
		String carMileage = driver.findElement(By.id("car-mileage")).getAttribute("value");
		String gearShift = randomSelect("car_gear_type");
		String color = randomSelect("car_color");

		if (carVin.length() < 17) {

			driver.findElement(By.id("car-vin")).sendKeys("12345678911234567");
			carVin = "12345678911234567";

		}

		if (carMotorNumber.length() < 17) {

			driver.findElement(By.id("car-motor-number")).sendKeys("12345678911234567");
			carMotorNumber = "12345678911234567";

		}

		/*
		 * fillName("price",""+1500000); int price = 1500000;
		 */

		/*
		 * int randAcc = new Random().nextInt(5)+1;
		 * fillName("acc_list","Random kellék "+randAcc);
		 */

		/*
		 * int randDoc = new Random().nextInt(500)+1;
		 * fillName("doc_list","Még átvett: "+randDoc);
		 */

		/*
		 * int randKomment = new Random().nextInt(500)+1;
		 * fillName("note","Teszt megjegyzés "+randKomment);
		 */

		int randOther = new Random().nextInt(500) + 1;
		fillName("car_other", "Teszt egyéb " + randOther);

		// szerződési feltételek----------------------------------------------------

		fillName("deadline-days", "3999");
		fillName("min_sell_price", "1500000");
		fillName("percentage", "69");
		fillName("penalty", "90000");

		// partner----------------------------------------------------
		sleep(2000);
		clickLinkWithText("Partner Kiválasztása");
		sleep(2000);

		try {

			driver.findElement(By.xpath("(//i[@class='fas fa-plus circle'])[1]")).click();

		} catch (NoSuchElementException e) {

			dgNewPartner();

		}

		// partner vége----------------------------------------------------

		String buyerName = driver.findElement(By.id("partner1-name")).getAttribute("value");
		String taxNum = driver.findElement(By.id("partner1-tax-no")).getAttribute("value");
		String regNum = driver.findElement(By.id("partner1-reg-no")).getAttribute("value");
		String address = driver.findElement(By.id("partner1-address")).getAttribute("value");
		
		driver.findElement(By.id("partner1-phone")).click();
		fillName("partner1_phone", "55555555");
		
		String email = driver.findElement(By.id("partner1-email")).getAttribute("value");
		sleep(3000);

		fillName("sign_city_id_ac", "Budapest");
		sleep(4000);
		driver.findElement(By.id("sign-city-id")).sendKeys(Keys.ENTER);
		sleep(1000);
		driver.findElement(By.id("sign-date")).click();
		driver.findElement(By.xpath("//form/div[4]/div[1]")).click();

		dgFillWitness();

		submit();

		try {

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='btn btn-lg btn-secondary w-100']")));
			clickText("Mentés és tovább");

		} catch (TimeoutException e) {

		}

		sleep(3000);

		onScreenWS(mFacturer);
		onScreenWS(plateNum);
		onScreenWS(carVin);
		onScreenWS(carMileage);
		onScreenWS(carMotorNumber);
		onScreenWS(gearShift);
		onScreenWS(color);
		onScreenWS("3999");
		// másfél millió
		onScreenWS("500");
		onScreenWS("69%");
		onScreenWS("90000");
		// onScreenWS(""+price);
		// onScreenWS("Random kellék "+ randAcc);
		// onScreenWS("Teszt megjegyzés "+ randKomment);
		// onScreenWS("Még átvett: "+ randDoc);
		onScreenWS("Teszt egyéb: " + randOther);
		onScreenWS(buyerName);
		onScreenWS(taxNum);
		onScreenWS(regNum);
		onScreenWS(address);
		onScreenWS(email);
		onScreenWS("555-555");
		onScreenWS("Budapest");
		onScreenWS(Tanu1[0]);
		onScreenWS(Tanu1[1]);
		onScreenWS(Tanu1[2]);
		onScreenWS(Tanu2[0]);
		onScreenWS(Tanu2[1]);
		onScreenWS(Tanu2[2]);

		driver.findElement(By.xpath("//div[contains(text(),'Következő')]")).click();
		sleep(2000);

	}
	
	public static void dgBuyBooking() throws IOException, InterruptedException {

		// goToPage(TestBase.url + "/hu/uj-dokumentum-kitoltese/" + getCarId() + "/6");

		// Foglaló
		// ----------------------------------------------------------------------------
		String mFacturer = driver.findElement(By.xpath("//div[@class='car-manufacturer']")).getText();
		String plateNum = driver.findElement(By.id("car-plate-number")).getAttribute("value");
		String carVin = driver.findElement(By.id("car-vin")).getAttribute("value");
		//String carMotorNumber = driver.findElement(By.id("car-motor-number")).getAttribute("value");
		//String carMileage = driver.findElement(By.id("car-mileage")).getAttribute("value");
		//String gearShift = randomSelect("car_gear_type");
		//String color = randomSelect("car_color");

		if (carVin.length() < 17) {

			driver.findElement(By.id("car-vin")).sendKeys("12345678911234567");
			carVin = "12345678911234567";

		}

		/*if (carMotorNumber.length() < 17) {

			driver.findElement(By.id("car-motor-number")).sendKeys("12345678911234567");
			carMotorNumber = "12345678911234567";

		}*/

		
		fillName("price",""+1500000);
		int price = 1500000;
		
		fillName("deposit",""+490000);
		int deposit = 490;
		
		sleep(1000);
		driver.findElement(By.cssSelector(".form-control.ts-date-picker")).click();
		sleep(2000);
		
		driver.findElement(By.xpath("(//div[@class='card-header'])[1]")).click();
		
		/*
		 * int randAcc = new Random().nextInt(5)+1;
		 * fillName("acc_list","Random kellék "+randAcc);
		 */

		/*
		 * int randDoc = new Random().nextInt(500)+1;
		 * fillName("doc_list","Még átvett: "+randDoc);
		 */

		/*
		 * int randKomment = new Random().nextInt(500)+1;
		 * fillName("note","Teszt megjegyzés "+randKomment);
		 */

		/*int randOther = new Random().nextInt(500) + 1;
		fillName("car_other", "Teszt egyéb " + randOther);*/

		// partner----------------------------------------------------
		sleep(2000);
		clickLinkWithText("Partner Kiválasztása");
		sleep(2000);

		try {

			driver.findElement(By.xpath("(//i[@class='fas fa-plus circle'])[1]")).click();

		} catch (NoSuchElementException e) {

			dgNewPartner();

		}

		// partner vége----------------------------------------------------

		String buyerName = driver.findElement(By.id("partner1-name")).getAttribute("value");
		String taxNum = driver.findElement(By.id("partner1-tax-no")).getAttribute("value");
		String regNum = driver.findElement(By.id("partner1-reg-no")).getAttribute("value");
		String address = driver.findElement(By.id("partner1-address")).getAttribute("value");
		
		driver.findElement(By.id("partner1-phone")).click();
		fillName("partner1_phone", "55555555");
		
		String email = driver.findElement(By.id("partner1-email")).getAttribute("value");
		sleep(3000);

		//Keltezési adatok -----------------------------------
		fillName("sign_city_id_ac", "Budapest");
		sleep(4000);
		driver.findElement(By.id("sign-city-id")).sendKeys(Keys.ENTER);
		sleep(1000);
		driver.findElement(By.id("sign-date")).click();
		driver.findElement(By.xpath("//form/div[4]/div[1]")).click();

		dgFillWitness();

		submit();
		
		//Mentés ellenőrzés

		try {

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='btn btn-lg btn-secondary w-100']")));
			clickText("Mentés és tovább");

		} catch (TimeoutException e) {

		}

		sleep(3000);
		
		//Ellenőrzés

		onScreenWS(mFacturer);
		onScreenWS(plateNum);
		onScreenWS(carVin);
		onScreenWS("3999");
		// másfél millió
		onScreenWS("500");
		onScreenWS(""+deposit);
		// onScreenWS("Random kellék "+ randAcc);
		// onScreenWS("Teszt megjegyzés "+ randKomment);
		// onScreenWS("Még átvett: "+ randDoc);
		//onScreenWS("Teszt egyéb: " + randOther);
		onScreenWS(buyerName);
		onScreenWS(taxNum);
		onScreenWS(regNum);
		onScreenWS(address);
		onScreenWS(email);
		onScreenWS("555-555");
		onScreenWS("Budapest");
		onScreenWS(Tanu1[0]);
		onScreenWS(Tanu1[1]);
		onScreenWS(Tanu1[2]);
		onScreenWS(Tanu2[0]);
		onScreenWS(Tanu2[1]);
		onScreenWS(Tanu2[2]);

		driver.findElement(By.xpath("//div[contains(text(),'Következő')]")).click();
		sleep(2000);

	}
	
	public static void dgBuyAuthorizationRewriting() throws IOException, InterruptedException {

		// goToPage(TestBase.url + "/hu/uj-dokumentum-kitoltese/" + getCarId() + "/7");

		// Meghatalmazás: Átírás
		// ----------------------------------------------------------------------------
		String mFacturer = driver.findElement(By.xpath("//div[@class='car-manufacturer']")).getText();
		String plateNum = driver.findElement(By.id("car-plate-number")).getAttribute("value");
		String carVin = driver.findElement(By.id("car-vin")).getAttribute("value");
		//String carMotorNumber = driver.findElement(By.id("car-motor-number")).getAttribute("value");
		//String carMileage = driver.findElement(By.id("car-mileage")).getAttribute("value");
		//String gearShift = randomSelect("car_gear_type");
		//String color = randomSelect("car_color");

		if (carVin.length() < 17) {

			driver.findElement(By.id("car-vin")).sendKeys("12345678911234567");
			carVin = "12345678911234567";

		}

		/*if (carMotorNumber.length() < 17) {

			driver.findElement(By.id("car-motor-number")).sendKeys("12345678911234567");
			carMotorNumber = "12345678911234567";

		}*/

		/*
		 * fillName("price",""+1500000); int price = 1500000;
		 */

		/*
		 * int randAcc = new Random().nextInt(5)+1;
		 * fillName("acc_list","Random kellék "+randAcc);
		 */

		/*
		 * int randDoc = new Random().nextInt(500)+1;
		 * fillName("doc_list","Még átvett: "+randDoc);
		 */

		/*
		 * int randKomment = new Random().nextInt(500)+1;
		 * fillName("note","Teszt megjegyzés "+randKomment);
		 */

		//int randOther = new Random().nextInt(500) + 1;
		//fillName("car_other", "Teszt egyéb " + randOther);


		// partner----------------------------------------------------
		sleep(2000);
		clickLinkWithText("Partner Kiválasztása");
		sleep(2000);

		try {

			driver.findElement(By.xpath("(//i[@class='fas fa-plus circle'])[1]")).click();

		} catch (NoSuchElementException e) {

			dgNewPartner();

		}

		// partner vége----------------------------------------------------

		String myName = driver.findElement(By.id("partner1-name")).getAttribute("value");
		
		if (myName.length() < 2) {

			myName = "12345678911234567";
			fillName("partner1_name", myName);

		}
		
		String personalIdent = driver.findElement(By.id("partner1-personal-ident")).getAttribute("value");
		
		if (personalIdent.length() < 2) {

			personalIdent = "123456XY";
			fillName("partner1_personal_ident", personalIdent);

		}
		
		String motherName = driver.findElement(By.id("partner1-mothers-name")).getAttribute("value");
		
		if (motherName.length() < 2) {

			motherName = "Teszt Anyuka";
			fillName("partner1_mothers_name", motherName);

		}
		
		String address = driver.findElement(By.id("partner1-address")).getAttribute("value");
		
		if (address.length() < 2) {

			address = "1051 Budapest, Sas utca 25. A ép. 2. em./204 aj.";
			fillName("partner1_address", address);

		}

		//Keltezés
		
		fillName("sign_city_id_ac", "Budapest");
		sleep(4000);
		driver.findElement(By.id("sign-city-id")).sendKeys(Keys.ENTER);
		sleep(1000);
		driver.findElement(By.id("sign-date")).click();
		driver.findElement(By.xpath("//form/div[4]/div[1]")).click();

		dgFillWitness();

		submit();

		try {

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='btn btn-lg btn-secondary w-100']")));
			clickText("Mentés és tovább");

		} catch (TimeoutException e) {

		}

		sleep(3000);

		onScreenWS(mFacturer);
		onScreenWS(plateNum);
		onScreenWS(carVin);
		// onScreenWS(""+price);
		// onScreenWS("Random kellék "+ randAcc);
		// onScreenWS("Teszt megjegyzés "+ randKomment);
		// onScreenWS("Még átvett: "+ randDoc);
		//onScreenWS("Teszt egyéb: " + randOther);
		onScreenWS(myName);
		onScreenWS(motherName);
		onScreenWS(personalIdent);
		onScreenWS(address);
		//onScreenWS("555-555");
		onScreenWS("Budapest");
		onScreenWS(Tanu1[0]);
		onScreenWS(Tanu1[1]);
		onScreenWS(Tanu1[2]);
		onScreenWS(Tanu2[0]);
		onScreenWS(Tanu2[1]);
		onScreenWS(Tanu2[2]);

		driver.findElement(By.xpath("//div[contains(text(),'Következő')]")).click();
		sleep(2000);

	}
	
	public static void dgBuyAuthorizationTechnicalExam() throws IOException, InterruptedException {

		// goToPage(TestBase.url + "/hu/uj-dokumentum-kitoltese/" + getCarId() + "/8");

		// Meghatalmazás: Műszaki vizsga
		// ----------------------------------------------------------------------------
		String mFacturer = driver.findElement(By.xpath("//div[@class='car-manufacturer']")).getText();
		String plateNum = driver.findElement(By.id("car-plate-number")).getAttribute("value");
		String carVin = driver.findElement(By.id("car-vin")).getAttribute("value");
		//String carMotorNumber = driver.findElement(By.id("car-motor-number")).getAttribute("value");
		//String carMileage = driver.findElement(By.id("car-mileage")).getAttribute("value");
		//String gearShift = randomSelect("car_gear_type");
		//String color = randomSelect("car_color");

		if (carVin.length() < 17) {

			driver.findElement(By.id("car-vin")).sendKeys("12345678911234567");
			carVin = "12345678911234567";

		}

		/*if (carMotorNumber.length() < 17) {

			driver.findElement(By.id("car-motor-number")).sendKeys("12345678911234567");
			carMotorNumber = "12345678911234567";

		}*/

		/*
		 * fillName("price",""+1500000); int price = 1500000;
		 */

		/*
		 * int randAcc = new Random().nextInt(5)+1;
		 * fillName("acc_list","Random kellék "+randAcc);
		 */

		/*
		 * int randDoc = new Random().nextInt(500)+1;
		 * fillName("doc_list","Még átvett: "+randDoc);
		 */

		/*
		 * int randKomment = new Random().nextInt(500)+1;
		 * fillName("note","Teszt megjegyzés "+randKomment);
		 */

		//int randOther = new Random().nextInt(500) + 1;
		//fillName("car_other", "Teszt egyéb " + randOther);


		// partner----------------------------------------------------
		sleep(2000);
		clickLinkWithText("Partner Kiválasztása");
		sleep(2000);

		try {

			driver.findElement(By.xpath("(//i[@class='fas fa-plus circle'])[1]")).click();

		} catch (NoSuchElementException e) {

			dgNewPartner();

		}

		// partner vége----------------------------------------------------

		String myName = driver.findElement(By.id("partner1-name")).getAttribute("value");
		
		if (myName.length() < 2) {

			myName = "12345678911234567";
			fillName("partner1_name", myName);

		}
		
		String personalIdent = driver.findElement(By.id("partner1-personal-ident")).getAttribute("value");
		
		if (personalIdent.length() < 2) {

			personalIdent = "123456XY";
			fillName("partner1_personal_ident", personalIdent);

		}
		
		String motherName = driver.findElement(By.id("partner1-mothers-name")).getAttribute("value");
		
		if (motherName.length() < 2) {

			motherName = "Teszt Anyuka";
			fillName("partner1_mothers_name", motherName);

		}
		
		String address = driver.findElement(By.id("partner1-address")).getAttribute("value");
		
		if (address.length() < 2) {

			address = "1051 Budapest, Sas utca 25. A ép. 2. em./204 aj.";
			fillName("partner1_address", address);

		}

		//Keltezés
		
		fillName("sign_city_id_ac", "Budapest");
		sleep(4000);
		driver.findElement(By.id("sign-city-id")).sendKeys(Keys.ENTER);
		sleep(1000);
		driver.findElement(By.id("sign-date")).click();
		driver.findElement(By.xpath("//form/div[4]/div[1]")).click();

		dgFillWitness();

		submit();

		try {

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='btn btn-lg btn-secondary w-100']")));
			clickText("Mentés és tovább");

		} catch (TimeoutException e) {

		}

		sleep(3000);

		onScreenWS(mFacturer);
		onScreenWS(plateNum);
		onScreenWS(carVin);
		// onScreenWS(""+price);
		// onScreenWS("Random kellék "+ randAcc);
		// onScreenWS("Teszt megjegyzés "+ randKomment);
		// onScreenWS("Még átvett: "+ randDoc);
		//onScreenWS("Teszt egyéb: " + randOther);
		onScreenWS(myName);
		onScreenWS(motherName);
		onScreenWS(personalIdent);
		onScreenWS(address);
		//onScreenWS("555-555");
		onScreenWS("Budapest");
		onScreenWS(Tanu1[0]);
		onScreenWS(Tanu1[1]);
		onScreenWS(Tanu1[2]);
		onScreenWS(Tanu2[0]);
		onScreenWS(Tanu2[1]);
		onScreenWS(Tanu2[2]);

		driver.findElement(By.xpath("//div[contains(text(),'Következő')]")).click();
		sleep(2000);

	}
	
	public static void dgBuyAuthorizationRegistrationTax() throws IOException, InterruptedException {

		// goToPage(TestBase.url + "/hu/uj-dokumentum-kitoltese/" + getCarId() + "/9");

		// Meghatalmazás: Regisztrációs adó
		// ----------------------------------------------------------------------------
		String mFacturer = driver.findElement(By.xpath("//div[@class='car-manufacturer']")).getText();
		String plateNum = driver.findElement(By.id("car-plate-number")).getAttribute("value");
		String carVin = driver.findElement(By.id("car-vin")).getAttribute("value");
		//String carMotorNumber = driver.findElement(By.id("car-motor-number")).getAttribute("value");
		//String carMileage = driver.findElement(By.id("car-mileage")).getAttribute("value");
		//String gearShift = randomSelect("car_gear_type");
		//String color = randomSelect("car_color");

		if (carVin.length() < 17) {

			driver.findElement(By.id("car-vin")).sendKeys("12345678911234567");
			carVin = "12345678911234567";

		}

		/*if (carMotorNumber.length() < 17) {

			driver.findElement(By.id("car-motor-number")).sendKeys("12345678911234567");
			carMotorNumber = "12345678911234567";

		}*/

		/*
		 * fillName("price",""+1500000); int price = 1500000;
		 */

		/*
		 * int randAcc = new Random().nextInt(5)+1;
		 * fillName("acc_list","Random kellék "+randAcc);
		 */

		/*
		 * int randDoc = new Random().nextInt(500)+1;
		 * fillName("doc_list","Még átvett: "+randDoc);
		 */

		/*
		 * int randKomment = new Random().nextInt(500)+1;
		 * fillName("note","Teszt megjegyzés "+randKomment);
		 */

		//int randOther = new Random().nextInt(500) + 1;
		//fillName("car_other", "Teszt egyéb " + randOther);


		// partner----------------------------------------------------
		sleep(2000);
		clickLinkWithText("Partner Kiválasztása");
		sleep(2000);

		try {

			driver.findElement(By.xpath("(//i[@class='fas fa-plus circle'])[1]")).click();

		} catch (NoSuchElementException e) {

			dgNewPartner();

		}

		// partner vége----------------------------------------------------

		String myName = driver.findElement(By.id("partner1-name")).getAttribute("value");
		
		if (myName.length() < 2) {

			myName = "12345678911234567";
			fillName("partner1_name", myName);

		}
		
		String personalIdent = driver.findElement(By.id("partner1-personal-ident")).getAttribute("value");
		
		if (personalIdent.length() < 2) {

			personalIdent = "123456XY";
			fillName("partner1_personal_ident", personalIdent);

		}
		
		String motherName = driver.findElement(By.id("partner1-mothers-name")).getAttribute("value");
		
		if (motherName.length() < 2) {

			motherName = "Teszt Anyuka";
			fillName("partner1_mothers_name", motherName);

		}
		
		String address = driver.findElement(By.id("partner1-address")).getAttribute("value");
		
		if (address.length() < 2) {

			address = "1051 Budapest, Sas utca 25. A ép. 2. em./204 aj.";
			fillName("partner1_address", address);

		}

		//Keltezés
		
		fillName("sign_city_id_ac", "Budapest");
		sleep(4000);
		driver.findElement(By.id("sign-city-id")).sendKeys(Keys.ENTER);
		sleep(1000);
		driver.findElement(By.id("sign-date")).click();
		driver.findElement(By.xpath("//form/div[4]/div[1]")).click();

		dgFillWitness();

		submit();

		try {

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='btn btn-lg btn-secondary w-100']")));
			clickText("Mentés és tovább");

		} catch (TimeoutException e) {

		}

		sleep(3000);

		onScreenWS(mFacturer);
		onScreenWS(plateNum);
		onScreenWS(carVin);
		// onScreenWS(""+price);
		// onScreenWS("Random kellék "+ randAcc);
		// onScreenWS("Teszt megjegyzés "+ randKomment);
		// onScreenWS("Még átvett: "+ randDoc);
		//onScreenWS("Teszt egyéb: " + randOther);
		onScreenWS(myName);
		onScreenWS(motherName);
		onScreenWS(personalIdent);
		onScreenWS(address);
		//onScreenWS("555-555");
		onScreenWS("Budapest");
		onScreenWS(Tanu1[0]);
		onScreenWS(Tanu1[1]);
		onScreenWS(Tanu1[2]);
		onScreenWS(Tanu2[0]);
		onScreenWS(Tanu2[1]);
		onScreenWS(Tanu2[2]);

		driver.findElement(By.xpath("//div[contains(text(),'Következő')]")).click();
		sleep(2000);

	}
	
	public static void dgBuyHandoverTranscriptionCost() throws IOException, InterruptedException {

		// goToPage(TestBase.url + "/hu/uj-dokumentum-kitoltese/" + getCarId() + "/10");

		// Átadás-átvételi: átírási ktg
		// ----------------------------------------------------------------------------
		String mFacturer = driver.findElement(By.xpath("//div[@class='car-manufacturer']")).getText();
		String plateNum = driver.findElement(By.id("car-plate-number")).getAttribute("value");
		String carVin = driver.findElement(By.id("car-vin")).getAttribute("value");
		//String carMotorNumber = driver.findElement(By.id("car-motor-number")).getAttribute("value");
		//String carMileage = driver.findElement(By.id("car-mileage")).getAttribute("value");
		//String gearShift = randomSelect("car_gear_type");
		//String color = randomSelect("car_color");

		if (carVin.length() < 17) {

			driver.findElement(By.id("car-vin")).sendKeys("12345678911234567");
			carVin = "12345678911234567";

		}

		/*if (carMotorNumber.length() < 17) {

			driver.findElement(By.id("car-motor-number")).sendKeys("12345678911234567");
			carMotorNumber = "12345678911234567";

		}*/

		/*
		 * fillName("price",""+1500000); int price = 1500000;
		 */

		/*
		 * int randAcc = new Random().nextInt(5)+1;
		 * fillName("acc_list","Random kellék "+randAcc);
		 */

		
		 int randPropTax = new Random().nextInt(10000)+20000;
		 fillName("property_tax","" + randPropTax);
		
		 int randOriginCheck = new Random().nextInt(5000)+30000;
		 fillName("origin_check",""+randOriginCheck);

		 int randAdminCost = new Random().nextInt(5000) + 20000;
		 fillName("administration", "" + randAdminCost);
		
		


		// partner----------------------------------------------------
		sleep(2000);
		clickLinkWithText("Partner Kiválasztása");
		sleep(2000);

		try {

			driver.findElement(By.xpath("(//i[@class='fas fa-plus circle'])[1]")).click();

		} catch (NoSuchElementException e) {

			dgNewPartner();

		}

		// partner vége----------------------------------------------------

		String myName = driver.findElement(By.id("partner1-name")).getAttribute("value");
		
		if (myName.length() < 2) {

			myName = "12345678911234567";
			fillName("partner1_name", myName);

		}
		
		String personalTaxNum = driver.findElement(By.id("partner1-tax-no")).getAttribute("value");
		
		if (personalTaxNum.length() < 2) {

			personalTaxNum = "12345678912";
			fillName("partner1_tax_no", personalTaxNum);

		}
		
		String personalRegNo = driver.findElement(By.id("partner1-reg-no")).getAttribute("value");
		
		if (personalRegNo.length() < 2) {

			personalRegNo = "1234567891";
			fillName("partner1_reg_no", personalRegNo);

		}
		
		String address = driver.findElement(By.id("partner1-address")).getAttribute("value");
		
		if (address.length() < 2) {

			address = "1051 Budapest, Sas utca 25. A ép. 2. em./204 aj.";
			fillName("partner1_address", address);

		}

		//Keltezés
		
		fillName("sign_city_id_ac", "Budapest");
		sleep(4000);
		driver.findElement(By.id("sign-city-id")).sendKeys(Keys.ENTER);
		sleep(1000);
		driver.findElement(By.id("sign-date")).click();
		driver.findElement(By.xpath("//form/div[4]/div[1]")).click();

		dgFillWitness();

		submit();

		try {

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='btn btn-lg btn-secondary w-100']")));
			clickText("Mentés és tovább");

		} catch (TimeoutException e) {

		}

		sleep(3000);

		onScreenWS(mFacturer);
		onScreenWS(plateNum);
		onScreenWS(carVin);
		// onScreenWS(""+price);
		// onScreenWS("Random kellék "+ randAcc);
		// onScreenWS("Teszt megjegyzés "+ randKomment);
		// onScreenWS("Még átvett: "+ randDoc);
		//onScreenWS("Teszt egyéb: " + randOther);
		onScreenWS(myName);
		onScreenWS(personalTaxNum);
		onScreenWS(personalRegNo);
		onScreenWS(address);
		onScreenWS("" + randPropTax);
		onScreenWS("" + randOriginCheck);
		onScreenWS("" + randAdminCost);
		onScreenWS("Budapest");
		onScreenWS(Tanu1[0]);
		onScreenWS(Tanu1[1]);
		onScreenWS(Tanu1[2]);
		onScreenWS(Tanu2[0]);
		onScreenWS(Tanu2[1]);
		onScreenWS(Tanu2[2]);

		driver.findElement(By.xpath("//div[contains(text(),'Következő')]")).click();
		sleep(2000);

	}
	
	public static void dgBuySalesContract() throws IOException, InterruptedException {

		// goToPage(TestBase.url + "/hu/uj-dokumentum-kitoltese/" + getCarId() + "/11");

		// Adásvételi szerződés
		// ----------------------------------------------------------------------------
		String mFacturer = driver.findElement(By.xpath("//div[@class='car-manufacturer']")).getText();
		String plateNum = driver.findElement(By.id("car-plate-number")).getAttribute("value");
		String carVin = driver.findElement(By.id("car-vin")).getAttribute("value");
		String carMotorNumber = driver.findElement(By.id("car-motor-number")).getAttribute("value");
		String carMileage = driver.findElement(By.id("car-mileage")).getAttribute("value");
		String carRegNumBook = driver.findElement(By.id("car-registry-number")).getAttribute("value");
		String carRegistrationNum = driver.findElement(By.id("registration-number")).getAttribute("value");
		//String gearShift = randomSelect("car_gear_type");
		//String color = randomSelect("car_color");
		String paymentType = randomSelect("payment_type");

		if (carVin.length() < 17) {

			driver.findElement(By.id("car-vin")).sendKeys("12345678911234567");
			carVin = "12345678911234567";

		}

		if (carMotorNumber.length() < 17) {

			driver.findElement(By.id("car-motor-number")).sendKeys("12345678911234567");
			carMotorNumber = "12345678911234567";

		}
		
		if (carRegNumBook.length() < 7) {

			driver.findElement(By.id("car-registry-number")).sendKeys("123456A");
			carVin = "123456A";

		}

		if (carRegistrationNum.length() < 15) {

			driver.findElement(By.id("registration-number")).sendKeys("123456789123456");
			carMotorNumber = "123456789123456";

		}

		
		fillName("price",""+15000000); 
		int price = 15000000;
		 
		
		driver.findElement(By.id("car-mileage-date")).click();
		sleep(3000);
		driver.findElement(By.id("payment-date")).click();
		sleep(3000);

		/*
		 int randAcc = new Random().nextInt(5)+1;
		 fillName("acc_list","Random kellék "+randAcc);
		*/

		// partner----------------------------------------------------
		sleep(2000);
		clickLinkWithText("Partner Kiválasztása");
		sleep(2000);

		try {

			driver.findElement(By.xpath("(//i[@class='fas fa-plus circle'])[1]")).click();

		} catch (NoSuchElementException e) {

			dgNewPartner();

		}

		// partner vége----------------------------------------------------

		String myName = driver.findElement(By.id("partner1-name")).getAttribute("value");
		
		if (myName.length() < 2) {

			myName = "Kovács Béla";
			fillName("partner1_name", myName);

		}
		
		String personalTaxNum = driver.findElement(By.id("partner1-tax-no")).getAttribute("value");
		
		if (personalTaxNum.length() < 2) {

			personalTaxNum = "12345678912";
			fillName("partner1_tax_no", personalTaxNum);

		}
		
		String personalRegNo = driver.findElement(By.id("partner1-reg-no")).getAttribute("value");
		
		if (personalRegNo.length() < 2) {

			personalRegNo = "1234567891";
			fillName("partner1_reg_no", personalRegNo);

		}
		
		String address = driver.findElement(By.id("partner1-address")).getAttribute("value");
		
		if (address.length() < 2) {

			address = "1051 Budapest, Sas utca 25. A ép. 2. em./204 aj.";
			fillName("partner1_address", address);

		}
		
		String represent = driver.findElement(By.id("partner1-representative")).getAttribute("value");
		
		if (represent.length() < 2) {

			represent = "Apuci";
			fillName("partner1_representative", represent);

		}

		//Dátumok
		
		sleep(2000);
		clickXpath("//label[contains(text(), 'Az okmányok átadása')]");
		sleep(3000);
		
		//Keltezés
		
		fillName("sign_city_id_ac", "Budapest");
		sleep(4000);
		driver.findElement(By.id("sign-city-id")).sendKeys(Keys.ENTER);
		sleep(1000);
		driver.findElement(By.id("sign-date")).click();
		driver.findElement(By.xpath("//form/div[4]/div[1]")).click();

		dgFillWitness();

		submit();

		try {

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='btn btn-lg btn-secondary w-100']")));
			clickText("Mentés és tovább");

		} catch (TimeoutException e) {
			
			Log.log("No modded data");
			
		}

		sleep(3000);
		
		onScreenWS(mFacturer);
		onScreenWS(plateNum);
		onScreenWS(carVin);
		onScreenWS("tizenötmillió");
		
		onScreenWS(carMotorNumber);
		onScreenWS(carMileage);
		onScreenWS(carRegNumBook);
		onScreenWS(carRegistrationNum);
		onScreenWS(paymentType);
		// onScreenWS("Random kellék "+ randAcc);
		// onScreenWS("Teszt megjegyzés "+ randKomment);
		// onScreenWS("Még átvett: "+ randDoc);
		//onScreenWS("Teszt egyéb: " + randOther);
		onScreenWS(myName);
		onScreenWS(personalTaxNum);
		onScreenWS(personalRegNo);
		onScreenWS(address);
		onScreenWS("Bizalmi vagyonkezelés ténye nem áll fenn.");
		onScreenWS("Budapest");
		onScreenWS(Tanu1[0]);
		onScreenWS(Tanu1[1]);
		onScreenWS(Tanu1[2]);
		onScreenWS(Tanu2[0]);
		onScreenWS(Tanu2[1]);
		onScreenWS(Tanu2[2]);

		driver.findElement(By.xpath("//div[contains(text(),'Következő')]")).click();
		sleep(2000);

	}
	
	public static void dgBuyStatusSheet() throws IOException, InterruptedException {

		// goToPage(TestBase.url + "/hu/uj-dokumentum-kitoltese/" + getCarId() + "/12");

		// Állapotlap
		// ----------------------------------------------------------------------------
		String mFacturer = driver.findElement(By.xpath("//div[@class='car-manufacturer']")).getText();
		String plateNum = driver.findElement(By.id("car-plate-number")).getAttribute("value");
		String carVin = driver.findElement(By.id("car-vin")).getAttribute("value");
		String carMotorNumber = driver.findElement(By.id("car-motor-number")).getAttribute("value");
		String carRegistrationNum = driver.findElement(By.id("registration-number")).getAttribute("value");
		String carKeyNum = driver.findElement(By.id("car-keys")).getAttribute("value");
		String carOwnerCount = driver.findElement(By.id("owner-count")).getAttribute("value");
		String gearShift = randomSelect("car_gear_type");
		String color = randomSelect("car_color");

		if (carVin.length() < 17) {

			driver.findElement(By.id("car-vin")).sendKeys("12345678911234567");
			carVin = "12345678911234567";

		}

		if (carMotorNumber.length() < 17) {

			driver.findElement(By.id("car-motor-number")).sendKeys("12345678911234567");
			carMotorNumber = "12345678911234567";

		}

		if (carRegistrationNum.length() < 15) {

			driver.findElement(By.id("registration-number")).sendKeys("123456789123456");
			carMotorNumber = "123456789123456";

		}
		 
		if (carKeyNum.length() < 2) {

			driver.findElement(By.id("car-keys")).sendKeys("13");
			carKeyNum = "13";

		}

		if (carOwnerCount.length() < 2) {

			driver.findElement(By.id("owner-count")).sendKeys("10");
			carOwnerCount = "10";

		}

		/*
		 int randAcc = new Random().nextInt(5)+1;
		 fillName("acc_list","Random kellék "+randAcc);
		*/

		// partner----------------------------------------------------
		sleep(2000);
		clickLinkWithText("Partner Kiválasztása");
		sleep(2000);

		try {

			driver.findElement(By.xpath("(//i[@class='fas fa-plus circle'])[1]")).click();

		} catch (NoSuchElementException e) {

			dgNewPartner();

		}

		// partner vége----------------------------------------------------

		String myName = driver.findElement(By.id("partner1-name")).getAttribute("value");
		
		if (myName.length() < 2) {

			myName = "Kovács Béla";
			fillName("partner1_name", myName);

		}
		
		String personalTaxNum = driver.findElement(By.id("partner1-tax-no")).getAttribute("value");
		
		if (personalTaxNum.length() < 2) {

			personalTaxNum = "12345678912";
			fillName("partner1_tax_no", personalTaxNum);

		}
		
		String personalRegNo = driver.findElement(By.id("partner1-reg-no")).getAttribute("value");
		
		if (personalRegNo.length() < 2) {

			personalRegNo = "1234567891";
			fillName("partner1_reg_no", personalRegNo);

		}
		
		String address = driver.findElement(By.id("partner1-address")).getAttribute("value");
		
		if (address.length() < 2) {

			address = "1051 Budapest, Sas utca 25. A ép. 2. em./204 aj.";
			fillName("partner1_address", address);

		}
		

		//Felszereltség
		
		String roof = randomSelect("param[car_params_technicals][roof]");
		String airCond = randomSelect("param[car_params_technicals][airconditioning]");
		String interior = randomSelect("param[car_params_comforts][interior]");
		fillName("status_descriptor[other][summary]","Segítség!");
		
		//Keltezés
		
		fillName("sign_city_id_ac", "Budapest");
		sleep(4000);
		driver.findElement(By.id("sign-city-id")).sendKeys(Keys.ENTER);
		sleep(1000);
		driver.findElement(By.id("sign-date")).click();
		driver.findElement(By.xpath("//form/div[4]/div[1]")).click();

		dgFillWitness();

		submit();

		try {

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='btn btn-lg btn-secondary w-100']")));
			clickText("Mentés és tovább");

		} catch (TimeoutException e) {
			
			Log.log("No modded data");
			
		}

		sleep(3000);
		
		onScreenWS(mFacturer);
		onScreenWS(plateNum);
		onScreenWS(carVin);
		
		onScreenWS(carMotorNumber);
		onScreenWS(gearShift);
		onScreenWS(carKeyNum);
		onScreenWS(carRegistrationNum);
		onScreenWS(carOwnerCount);
		onScreenWS(color);
		// onScreenWS("Teszt megjegyzés "+ randKomment);
		// onScreenWS("Még átvett: "+ randDoc);
		//onScreenWS("Teszt egyéb: " + randOther);
		onScreenWS(myName);
		onScreenWS(personalTaxNum);
		onScreenWS(personalRegNo);
		onScreenWS(address);
		onScreenWS("Segítség!");
		onScreenWS("Budapest");
		onScreenWS(Tanu1[0]);
		onScreenWS(Tanu1[1]);
		onScreenWS(Tanu1[2]);
		onScreenWS(Tanu2[0]);
		onScreenWS(Tanu2[1]);
		onScreenWS(Tanu2[2]);

		driver.findElement(By.xpath("//div[contains(text(),'Összes')]")).click();
		sleep(2000);

	}
	
	public static void dbCheclAllBuyDoc() throws IOException, InterruptedException {

		onScreen("Átadás-átvételi: Autó");
		onScreen("Átadás-átvételi: Kellék");
		onScreen("Átadás-átvételi: Vételár");
		onScreen("Bizományból kiadás");
		onScreen("Bizományosi szerződés");
		onScreen("Foglaló");
		onScreen("Meghatalmazás: Átírás");
		onScreen("Meghatalmazás: Műszaki vizsga");
		onScreen("Meghatalmazás: Regisztrációs adó");
		onScreen("Átadás-átvételi: átírási ktg");
		onScreen("Adásvételi szerződés");
		onScreen("Állapotlap");

		Log.log("All Buy document created successfully!");

	}
	

	public static void documentGeneratorErrorTest() throws IOException, InterruptedException {

		clickLinkWithText("Dokumentum generáló");
		sleep(3000);

		// vétel dokumentumok kiválasztása------------------------------------
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
		// vétel dokumentumok kiválasztása-------------------------------------

		submit();
		Log.log("Hiba üzenetek ellenőrze");
		onScreen("Kötelező mező");
		onScreen("A mező nem lehet üres.");
		sleep(3000);
		// Átadás-átvételi
		// nyilatkozat----------------------------------------------------------------------------
		String mFacturer = driver.findElement(By.xpath("//div[@class='car-manufacturer']")).getText();
		String plateNum = driver.findElement(By.id("car-plate-number")).getAttribute("value");
		String carVin = driver.findElement(By.id("car-vin")).getAttribute("value");
		clickLinkWithText("Partner Kiválasztása");
		sleep(2000);
		clickLinkWithText("Új partner felvétele");
		sleep(2000);

		// partner----------------------------------------------------
		Log.log("Partner felvétel...");
		driver.findElement(By.xpath("//section//button[@type='submit']")).click();
		onScreen("A mező nem lehet üres.");
		Log.log("Hiba Üzenetek Ellenőrzése!");
		sleep(10000);
		fillName("last_name", "TesztCsalád");
		fillName("first_name", "TesztVezeték");
		fillName("personal_ident", "123456AB");
		fillName("mothers_name", "Partner Anyu");
		fillName("birth_date", "1956-03-11");
		fillName("birth_place", "Budapest");
		fillName("nationality", "Magyar");
		fillName("email", "test@email.com");
		fillName("phone", "12345678");
		fillName("car_address[loc_zip_id_ac]", "1052");
		sleep(3000);
		driver.findElement(By.id("car-address-loc-zip-id")).sendKeys(Keys.ENTER);
		sleep(3000);
		fillName("car_address[street]", "Sas");
		driver.findElement(By.id("car-address-street-type")).click();
		sleep(1000);
		driver.findElement(By.id("car-address-street-type")).sendKeys(Keys.ARROW_DOWN);
		sleep(1000);
		driver.findElement(By.id("car-address-street-type")).sendKeys(Keys.ENTER);
		sleep(1000);
		fillName("car_address[street_num]", "25");
		fillName("car_address[building]", "A");
		fillName("car_address[floor]", "2");
		fillName("car_address[door]", "204");
		driver.findElement(By.xpath("//section//button[@type='submit']")).click();
		sleep(2000);
		// partner vége----------------------------------------------------

		String buyerName = driver.findElement(By.id("partner1-name")).getAttribute("value");
		String taxNum = driver.findElement(By.id("partner1-tax-no")).getAttribute("value");
		String regNum = driver.findElement(By.id("partner1-reg-no")).getAttribute("value");
		String address = driver.findElement(By.id("partner1-address")).getAttribute("value");

		fillName("sign_city_id_ac", "Budapest");
		sleep(1000);
		driver.findElement(By.id("sign-city-id")).sendKeys(Keys.ENTER);
		sleep(1000);
		driver.findElement(By.id("sign-date")).click();
		driver.findElement(By.xpath("//form/div[4]/div[1]")).click();

		fillName("witness1_name", "Tanú 1");
		fillName("witness1_personal_ident", "234567CD");
		fillName("witness1_address", "Repülőtéri út 6/a");
		fillName("witness2_name", "Tanú 2");
		fillName("witness2_personal_ident", "345678EF");
		fillName("witness2_address", "Igazából ez bármi lehet");
		submit();

	}

	public static void setNewRSSChannel() throws IOException, InterruptedException {

		driver.findElement(By.id("userMenu")).click();
		// driver.findElement(By.cssSelector("")).click();
		clickLinkWithText("Admin");
		sleep(5000);

		try {

			WebElement isVisibleElement = driver.findElement(By.xpath("//*[contains(text(), 'RSS hírcsatornák')]"));
			boolean visible = isVisibleElement.isDisplayed();
			Log.log("" + visible);
			// onScreen("RSS hírcsatornák");
			// clickLinkWithText("RSS hírcsatornák");

			if (visible == true) {

				clickLinkWithText("RSS hírcsatornák");

			} else {

				Log.log("RSS Admin keresése");
				isVisibleElement = driver.findElement(By.xpath("//*[contains(text(), 'RSS Admin')]"));
				visible = isVisibleElement.isDisplayed();

				if (visible == true) {

					clickLinkWithText("RSS Admin");
					clickLinkWithText("RSS hírcsatornák");

				} else {

					Log.log("oldal menü kinyitása");
					driver.findElement(By.cssSelector(".hi-trigger.ma-trigger")).click();
					sleep(2000);
					clickLinkWithText("RSS Admin");
					clickLinkWithText("RSS hírcsatornák");
				}
			}

		} catch (NoSuchElementException e) {

			goToPage(url + "/hu/admin/car/rss-heads");

		}

		try {

			driver.findElement(By.cssSelector(".zmdi.zmdi-plus")).click();

		} catch (NoSuchElementException e) {

			goToPage(url + "/hu/admin/car/rss-heads/add");

		}

		/*
		 * negatív teszteset, nincs elvárt validáció a mezőkre
		 * 
		 * submit();
		 * onScreen("Sikeres RssHeads hozzáadás");
		 * Log.log("Adatok nélkül mentve");
		 */

		sleep(3000);
		fillName("title", "HVG_itocska_test");
		sleep(1000);
		fillName("url", "https://hvg.hu/rss/cegauto");
		sleep(2000);
		submit();
		// TÖRIK A TESZT Bug #12297
		sleep(2000);
		// onScreen("Sikeres RssHeads hozzáadás");

		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[text()[contains(.,'Sikeres RssHeads hozzáadás')]]")));
		System.out.println("Sikeres RssHeads hozzáadás");
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("Sikeres RssHeads hozzáadás"));
		Log.log("Képernyőn: " + "Sikeres RssHeads hozzáadás");

	}

	public static void checkRSSChannel() throws IOException, InterruptedException {

		// RSS megnevezése
		onScreenValue("HVG_itocska_test");
		// link
		onScreenValue("http://hvg.hu/");
		// onScreenValue("hvg.hu RSS");
		// megjegyzés
		onScreen("hvg.hu RSS");
		// Copyright
		onScreenValue("hvg@hvg.hu");
		// Forrás
		onScreenValue("https://hvg.hu/rss/cegauto");
		sleep(3000);
		/*
		 * goToPage(url+"/hu/admin/car/rss-items"); sleep(2000);
		 * onScreen("HVG_itocska_test"); goToPage(url+"/hu/mediafigyelo");
		 * onScreen("HVG_itocska_test");
		 * 
		 * első HVG mentése, majd ellenőrzése goToPage(url+"/hu/admin/car/rss-items");
		 * sleep(2000); clickLinkWithText("RSS Csatorna"); sleep(2000);
		 * clickLinkWithText("RSS Csatorna");
		 */
		goToPage(url + "/hu/admin/car/rss-heads");
		sleep(2000);
		onScreen("HVG_itocska_test");
		onScreen("https://hvg.hu/rss/cegauto");
		/*
		 * driver.findElement(By.cssSelector(".zmdi.zmdi-check")).click();
		 * goToPage(url+"/hu/mediafigyelo"); onScreen("HVG_itocska_test");
		 * goToPage(url+"/hu/admin/car/rss-heads");
		 */
		driver.findElement(By.cssSelector(".zmdi.zmdi-edit")).click();
		sleep(2000);
		fillName("title", "HVG_itocska_test2");
		submit();
		sleep(2000);
		goToPage(url + "/hu/admin/car/rss-heads");
		sleep(2000);
		onScreen("HVG_itocska_test2");

	}

	public static void deleteTestRSSChannel() throws IOException, InterruptedException {

		goToPage(url + "/hu/admin/car/rss-heads");
		sleep(2000);
		driver.findElement(By.cssSelector(".zmdi.zmdi-delete")).click();
		driver.findElement(By.cssSelector(".btn.bgm-lightblue")).click();
		Log.log("Sikeres teszt");

	}

	public static void addNewCarEventMonthlySurvey() throws IOException, InterruptedException {

		Log.log("Havi szemle esemény felvitel!");
		driver.findElement(By.xpath("//a[@class='add-link popup']")).click();
		sleep(2000);
		driver.findElement(By.xpath("//span[@class='sprite sprite-mycar_monthly_inspection']")).click();
		// submit();
		// Minden Rendben állapot mentése
		sleep(2000);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("date"))).click();
		driver.findElement(By.cssSelector(".logo-title.d-none.d-md-inline-block.ml-3")).click();

		sleep(2000);
		driver.findElement(By.xpath(
				"//div[@class='col-md-6']/div[@class='form-group toggle ']/div/label[@class='input-toggle']/span[@class='switch']"))
				.click();
		submit();
		sleep(2000);
		Log.log("A havi szemle állapot sikersen mentve!");
		onScreen("Minden rendben");
		driver.findElement(By.cssSelector(".fas.fa-long-arrow-alt-left")).click();
		Log.log("Mentés ellenőrzése");
		sleep(2000);
		Log.log("Esemény Szerkesztés");
		driver.findElement(By.xpath(
				"//div[2]/div[@class='event timeline'][1]/div[@class='event-body mr-3']/div[@class='lv-small']/a/b"))
				.click();
		sleep(3000);
		clickLinkWithText("Szerkesztés");
		// click(".ts-date-picker");
		sleep(3000);
		driver.findElement(By.xpath(
				"//div[@class='col-md-6']/div[@class='form-group toggle ']/div/label[@class='input-toggle']/span[@class='switch']"))
				.click();
		fillName("exterior_condition", "Karosszéria külső állapota");
		fillName("interior_condition", "Utastér állapota");
		driver.findElement(By.id("tire-condition-fine-0")).click();
		fillName("tire_condition", "Gumik állapota Sérült");

		Random rand = new Random();
		Integer randRPD = rand.nextInt(10) + 2;
		Select depth = new Select(driver.findElement(By.name("thread_depth_front_left")));
		depth.selectByVisibleText(randRPD + " mm");

		Integer randRPD1 = rand.nextInt(10) + 2;
		depth = new Select(driver.findElement(By.name("thread_depth_front_right")));
		depth.selectByVisibleText(randRPD1 + " mm");

		Integer randRPD2 = rand.nextInt(10) + 2;
		depth = new Select(driver.findElement(By.name("thread_depth_rear_left")));
		depth.selectByVisibleText(randRPD2 + " mm");

		Integer randRPD3 = rand.nextInt(10) + 2;
		depth = new Select(driver.findElement(By.name("thread_depth_rear_right")));
		depth.selectByVisibleText(randRPD3 + " mm");

		sleep(2000);

		int eo = rand.nextInt(4) + 1;
		List<WebElement> radios = driver.findElements(By.name("engine_oil"));
		if (eo > 0 && eo <= radios.size()) {
			radios.get(eo).click();
		} else {
			throw new NotFoundException("option " + eo + " not found");
		}

		int bf = rand.nextInt(2) + 1;
		radios = driver.findElements(By.name("break_fluid"));
		if (bf > 0 && bf <= radios.size()) {
			radios.get(bf + 1).click();
		} else {
			throw new NotFoundException("option " + bf + " not found");
		}

		int cl = rand.nextInt(2) + 1;
		radios = driver.findElements(By.name("cooling_liquid"));
		if (cl > 0 && cl <= radios.size()) {
			radios.get(cl + 1).click();
		} else {
			throw new NotFoundException("Ez a gomb" + cl + "nem található");
		}

		int lg = rand.nextInt(1) + 1;
		radios = driver.findElements(By.name("landing_gear"));
		if (lg > 0 && lg <= radios.size()) {
			radios.get(lg).click();
		} else {
			throw new NotFoundException("Ez a gomb" + lg + "nem található");

		}
		int sl = rand.nextInt(2) + 1;
		radios = driver.findElements(By.name("straight_line"));
		if (sl > 0 && sl <= radios.size()) {
			radios.get(sl + 1).click();
		} else {
			throw new NotFoundException("Ez a gomb" + sl + "nem található");

		}
		int ep = rand.nextInt(2) + 1;
		radios = driver.findElements(By.name("engine_power"));
		if (ep > 0 && ep <= radios.size()) {
			radios.get(ep + 1).click();
		} else {
			throw new NotFoundException("Ez a gomb" + ep + "nem található");
		}

		int l = rand.nextInt(1) + 1;
		radios = driver.findElements(By.name("lightning"));
		if (l > 0 && l <= radios.size()) {
			radios.get(l + 1).click();
		} else {
			throw new NotFoundException("Ez a gomb" + l + "nem található");
		}

		List<WebElement> elements = driver.findElements(By.cssSelector(".form-group.checkbox.fg-line"));
		for (WebElement element : elements) {
			rand = new Random();
			int randomNum = rand.nextInt(3) + 1;
			if (randomNum == 3) {
				element.click();

			}

		}

		elements = driver.findElements(By.cssSelector(".form-group.checkbox.fg-line"));
		for (WebElement element : elements) {
			rand = new Random();
			int randomNum = rand.nextInt(3) + 1;
			if (randomNum == 1 || randomNum == 2 || randomNum == 3) {
				element.click();

			}

		}

		fillName("opinion", "Szubjektív Vélemény");
		sleep(2000);
		
		submit();
		onScreenAlert("Sikeres módosítás");
		
		sleep(2000);
		driver.findElement(By.cssSelector(".fas.fa-eye.circle")).click();
		onScreen("Karosszéria külső állapota");
		onScreen("Utastér állapota");
		onScreen("Gumik állapota Sérült");
		onScreen("Szubjektív Vélemény");
		onScreen(randRPD + " mm");
		onScreen(randRPD1 + " mm");
		onScreen(randRPD2 + " mm");
		onScreen(randRPD3 + " mm");
		clickLinkWithText("Szerkesztés");

		click(".ts-date-picker");

		fillName("exterior_condition", "Karosszéria külső állapota új ");
		fillName("interior_condition", "Utastér állapota új");
		driver.findElement(By.id("tire-condition-fine-0")).click();
		fillName("tire_condition", "Gumik állapota Sérült új");

		rand = new Random();
		randRPD = rand.nextInt(10) + 2;
		depth = new Select(driver.findElement(By.name("thread_depth_front_left")));
		depth.selectByVisibleText(randRPD + (1) + " mm");

		randRPD1 = rand.nextInt(10) + 2;
		depth = new Select(driver.findElement(By.name("thread_depth_front_right")));
		depth.selectByVisibleText(randRPD1 + " mm");

		randRPD2 = rand.nextInt(10) + 2;
		depth = new Select(driver.findElement(By.name("thread_depth_rear_left")));
		depth.selectByVisibleText(randRPD2 + " mm");

		randRPD3 = rand.nextInt(10) + 2;
		depth = new Select(driver.findElement(By.name("thread_depth_rear_right")));
		depth.selectByVisibleText(randRPD3 + " mm");

		sleep(2000);

		eo = rand.nextInt(4) + 1;
		radios = driver.findElements(By.name("engine_oil"));
		if (eo > 0 && eo <= radios.size()) {
			radios.get(eo).click();
		} else {
			throw new NotFoundException("option " + eo + " not found");
		}

		bf = rand.nextInt(2) + 1;
		radios = driver.findElements(By.name("break_fluid"));
		if (bf > 0 && bf <= radios.size()) {
			radios.get(bf + 1).click();
		} else {
			throw new NotFoundException("option " + bf + " not found");
		}

		cl = rand.nextInt(2) + 1;
		radios = driver.findElements(By.name("cooling_liquid"));
		if (cl > 0 && cl <= radios.size()) {
			radios.get(cl + 1).click();
		} else {
			throw new NotFoundException("Ez a gomb" + cl + "nem található");
		}

		lg = rand.nextInt(1) + 1;
		radios = driver.findElements(By.name("landing_gear"));
		if (lg > 0 && lg <= radios.size()) {
			radios.get(lg).click();
		} else {
			throw new NotFoundException("Ez a gomb" + lg + "nem található");

		}
		sl = rand.nextInt(2) + 1;
		radios = driver.findElements(By.name("straight_line"));
		if (sl > 0 && sl <= radios.size()) {
			radios.get(sl + 1).click();
		} else {
			throw new NotFoundException("Ez a gomb" + sl + "nem található");

		}
		ep = rand.nextInt(2) + 1;
		radios = driver.findElements(By.name("engine_power"));
		if (ep > 0 && ep <= radios.size()) {
			radios.get(ep + 1).click();
		} else {
			throw new NotFoundException("Ez a gomb" + ep + "nem található");
		}

		l = rand.nextInt(1) + 1;
		radios = driver.findElements(By.name("lightning"));
		if (l > 0 && l <= radios.size()) {
			radios.get(l + 1).click();
		} else {
			throw new NotFoundException("Ez a gomb" + l + "nem található");
		}

		elements = driver.findElements(By.cssSelector(".form-group.checkbox.fg-line"));
		for (WebElement element : elements) {
			rand = new Random();
			int randomNum = rand.nextInt(3) + 1;
			if (randomNum == 3) {
				element.click();

			}

		}

		elements = driver.findElements(By.cssSelector(".form-group.checkbox.fg-line"));
		for (WebElement element : elements) {
			rand = new Random();
			int randomNum = rand.nextInt(3) + 1;
			if (randomNum == 1 || randomNum == 2 || randomNum == 3) {
				element.click();

			}

		}

		fillName("opinion", "Szubjektív Vélemény");
		sleep(2000);
		
		submit();
		onScreenAlert("Sikeres módosítás");
		
		sleep(2000);
		driver.findElement(By.cssSelector(".fas.fa-eye.circle")).click();
		onScreen("Karosszéria külső állapota új");
		onScreen("Utastér állapota új");
		onScreen("Gumik állapota Sérült új");
		onScreen("Szubjektív Vélemény");
		driver.findElement(By.cssSelector(".fas.fa-trash.circle")).click();
		sleep(1000);
		driver.findElement(By.cssSelector(".btn.grayBtn.deleteAttachedItem")).click();
		sleep(2000);
		onScreenAlert("Sikeresen törölve!");
		
		Log.log("Sikeresen Törölve");

	}

	public static void serviceSearch() throws IOException, InterruptedException {

		// szolgaltatas kereso inditasa
		sleep(3000);
		clickLinkWithText("Cégkereső");
		sleep(1000);
		driver.findElement(By.cssSelector(".fas.fa-search.mr-0")).click();
		sleep(1000);

		String firstre = driver.findElement(By.xpath(
				"//div[@class='row result-list company-result-item align-items-center' and .//span[contains(@class, 'service-btn')]]"))
				.getText();
		String[] allData = firstre.split("\n");
		System.out.println(allData[0]);
		sleep(2000);
		driver.findElement(By.xpath("//*[contains(text(), '" + allData[0] + "')]")).click();

		sleep(3000);
		String companyTitle = driver.findElement(By.cssSelector(".company-profile.company-profile-title")).getText();

		String cAdress = driver.findElement(By.xpath("//div[@class='col-12 text-darker mb-2']/span")).getText();

		String[] tadress = cAdress.split(" ");
		// cAdress.indexOf(0, 1);
		Log.log(companyTitle);
		System.out.println(tadress[0]);
		System.out.println(tadress[1]);

		clickLinkWithText("Cég és szolgáltatás kereső");

		sleep(2000);
		driver.findElement(By.xpath("//button[@class='multiselect dropdown-toggle btn btn-default']")).click();
		driver.findElement(By.xpath("//label[contains(text(),'" + companyTitle + "')]")).click();
		fillName("location", tadress[0] + " " + tadress[1]);
		sleep(2000);
		driver.findElement(By.cssSelector(".fas.fa-search.mr-0")).click();
		sleep(2000);
		onScreen(companyTitle);
		onScreen(allData[0]);

		Log.log("Sikeres szolgáltatás kereső teszt");

	}
	
	public static void purgeUser() throws IOException, InterruptedException {
		
		
		adminLogin();
		
		//Admin page
		clickXpath("//a[@id='profileMiniImg']");
		sleep(2000);
		clickLinkWithText("Admin");
		
		//Desktop size errors treatment
		try {

			WebElement isVisibleElement = driver.findElement(By.xpath("//*[contains(text(), 'Felhasználók')]"));
			boolean visible = isVisibleElement.isDisplayed();
			Log.log("" + visible);

			if (visible == true) {

				clickLinkWithText("Felhasználók");

			} else {

				Log.log("oldal menü kinyitása");
				driver.findElement(By.cssSelector(".hi-trigger.ma-trigger")).click();
				sleep(2000);
				clickLinkWithText("Felhasználók");
				
			}

		} catch (NoSuchElementException e) {

			Log.log("Valami hiba van a felbontással, sidebarral!");
			goToPage(url + "/hu/admin/car/car-users");

		}

		//search the actual user
		sleep(3000);
		fillName("key", personalUser);
		sleep(2000);
		clickButton("Keres");
		sleep(3000);
		
		//view user page
		clickXpath("(//span[@class='zmdi zmdi-eye'])[1]");
		sleep(3000);
		
		//get token
		String url = driver.getCurrentUrl();
		String userId = url.replaceFirst(".*\\/([^\\/?]+).*", "$1");
		
		//go to user purge page
		sleep(3000);
		driver.navigate().back();
		sleep(1000);
		driver.navigate().back();
		sleep(2000);
		clickXpath("(//a[@data-toggle='dropdown'])[2]");
		sleep(2000);
		clickLinkWithText("Felhasználó kiégetése");
		sleep(3000);
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Car')]")));
		
		//purge user
		fillName("uid", userId);
		sleep(2000);
		clickButton("Vizsgálat");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(), 'Törlés')]")));
		clickButton("Törlés");
		sleep(1000);
		driver.switchTo().alert().accept();
		
		sleep(3000);
		onScreenWS("objektum törlés megtörtént");
		Log.log("Purge User test successful!");
		
	}
	
	public static void purgeCompany(String mail) throws IOException, InterruptedException {
		
		
		adminLogin();
		
		//Admin page
		clickXpath("//a[@id='profileMiniImg']");
		sleep(2000);
		clickLinkWithText("Admin");
		
		//Desktop size errors treatment

		try {

			WebElement isVisibleElement = driver.findElement(By.xpath("//*[contains(text(), 'Cégjegyzék')]"));
			boolean visible = isVisibleElement.isDisplayed();
			Log.log("" + visible);

			if (visible == true) {

				clickLinkWithText("Cégek");

			} else {

				Log.log("Cégjegyzék keresése sidebarban");
				isVisibleElement = driver.findElement(By.xpath("//*[contains(text(), 'Cégjegyzék')]"));
				visible = isVisibleElement.isDisplayed();

				if (visible == true) {

					clickLinkWithText("Cégjegyzék");
					clickLinkWithText("Cégek");

				} else {

					Log.log("oldal menü kinyitása");
					driver.findElement(By.cssSelector(".hi-trigger.ma-trigger")).click();
					sleep(2000);
					//clickLinkWithText("Cégjegyzék");
					clickLinkWithText("Cégek");
				}
			}

		} catch (NoSuchElementException e) {

			Log.log("Valami hiba van a felbontással, sidebarral!");
			goToPage(url + "/hu/admin/car/car-companies");

		}

		//search the actual user
		sleep(3000);
		fillName("quick_search", mail);
		sleep(2000);
		clickButton("Keres");
		sleep(3000);
		
		//view user page
		clickXpath("(//span[@class='zmdi zmdi-eye'])[1]");
		sleep(3000);
		
		//get token
		String currenturl = driver.getCurrentUrl();
		String companyId = currenturl.replaceFirst(".*\\/([^\\/?]+).*", "$1");
		
		//go to user purge page
		sleep(2000);
		goToPage(url + "/hu/admin/car/car-user-purge-protocoll");
		
		//purge company
		fillName("company", companyId);
		sleep(2000);
		clickButton("Vizsgálat");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(), 'Törlés')]")));
		clickButton("Törlés");
		sleep(1000);
		driver.switchTo().alert().accept();
		
		sleep(3000);
		onScreenWS("objektum törlés megtörtént");
		Log.log("Purge Company test successful!");
		
	}

	public static void purgeCar(String currentCarId) throws IOException, InterruptedException {
	
		adminLogin();
		
		//go to user purge page
		sleep(3000);
		goToPage(url + "/hu/admin/car/car-user-purge-protocoll");
		
		//purge company
		fillName("cid", currentCarId);
		sleep(2000);
		clickButton("Vizsgálat");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(), 'Törlés')]")));
		clickButton("Törlés");
		sleep(1000);
		driver.switchTo().alert().accept();
		
		sleep(3000);
		onScreenWS("objektum törlés megtörtént");
		Log.log("Purge Company test successful!");
		
	}

	public static void landingPageUsedCarSell() throws IOException, InterruptedException {
	
		goToPage(urlLive + "/hu/hasznalt-auto-eladas");
		sleep(3000);
		
		onScreen("Add el használt gépjárműved profin összeállított használt autó hirdetéssel!");
		onScreen("Add fel hirdetésed!");
		onScreen("Felhasznált cikkek");
	 
	}

	public static void landingPageServiceLog() throws IOException, InterruptedException {
	
		goToPage(urlLive + "/hu/szerviznaplo-alkalmazas-autosoknak");
		sleep(3000);
		
		onScreen("Tartalomjegyzék");
		onScreen("Eljött az idő, hogy Te is egyetlen rendszerbe rögzítsd az autóddal kapcsolatos adatokat, szervizeseményeket, fontosabb időpontokat és határidőket. Állíts be értesítéseket, kapj ajánlatokat egyetlen autós alkalmazás segítségével. Hoppá: és mindezt teljesen ingyen!");
		onScreen("Regisztráld autód!");
		onScreen("Show must go on! Mire számíthatsz tőlünk a jövőben?");
	 
	}

	public static void landingPageVehicleAnalysis() throws IOException, InterruptedException {
	
		goToPage(urlLive + "/hu/jarmuelemzes");
		sleep(3000);
		
		onScreen("Tartalomjegyzék");
		onScreen("Egy professzionális járműelemzés során gyorsan kiderül a vizsgált autó valós állapota és értéke. Akár eladsz, akár venni szeretnél, egy ilyen autó vizsgálat szuper hivatkozási alap lesz!");
		onScreen("Rendeld meg a járműelemzést!");
		onScreen("Személyes adatok");
	 
	}

	public static void landingPageCarSellForDealer() throws IOException, InterruptedException {
	
		goToPage(urlLive + "/hu/hasznaltauto-eladas-kereskedoknek");
		sleep(3000);
		
		onScreen("Tartalomjegyzék");
		onScreen("Automatikus hirdetésáttöltés a legnagyobb használt autó portálokra - 5-ször nagyobb esély használtautóid értékesítésére!");
		onScreen("Értékesíts több autót!");
		onScreen("Rendeld meg a számodra legkedvezőbb autós csomagot!");
	 
	}

	public static void companySitesDetails() throws IOException, InterruptedException {
		
		boolean isSitesCorrect = false;
		
		do {
		
			sleep(10000);
			
			try {
				
				driver.findElement(By.xpath("(//a[@class='shepherd-button sheperd-inline-link'])[1]")).click();
				
			}catch(NoSuchElementException e) {
				
				Log.log("Nem kínál fel bővítési lehetőséget az oldal.");
				isSitesCorrect = true;
				break;
				
			}
			
			sleep(3000);
			
			List<WebElement> elements = driver.findElements(By.xpath("//label[@class='control-label']"));
			for ( WebElement element : elements ) {
				
				 if ( !element.isSelected() ) {
					 	element.click();
				    }
			    
			}
			
			sleep(3000);
			
			fillName("open_mon_from", "08:00");
			fillName("open_mon_to", "20:00");
			fillName("open_tue_from", "08:00");
			fillName("open_tue_to", "20:00");
			fillName("open_wed_from", "08:00");
			fillName("open_wed_to", "20:00");
			fillName("open_thu_from", "08:00");
			fillName("open_thu_to", "20:00");
			fillName("open_fri_from", "08:00");
			fillName("open_fri_to", "20:00");
			fillName("open_sat_from", "10:00");
			fillName("open_sat_to", "18:00");
			fillName("open_sun_from", "10:00");
			fillName("open_sun_to", "18:00");
			
			sleep(2000);
			
			clickButton("Módosítások mentése");
			
			sleep(2000);
			
			onScreenAlert("Sikeres módosítás");
			
			sleep(2000);
			
			goToPage(url + "/hu/garazs");
			
		}while(isSitesCorrect == false);
		
		Log.log("Teszt vége, ha nem futott, próbáld újra frissen regelt fiókkal");
		
	}

	public static void addNewCarEventDriverService() throws IOException, InterruptedException {

		//Create event sofőrszolgálat-------------------------------------------------------------
		clickLinkWithText("esemény hozzáadása");
		sleep(5000);
	
		WebElement element = driver.findElement(By.cssSelector(".sprite.sprite-driver_service"));
		Actions actions = new Actions(driver);
		actions.moveToElement(element).click().build().perform();
	
		//Fill details----------------------------------------------------------------------------
		sleep(2000);
		Log.log("Sofőrszolgálat esemény felvitel");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("event-date")));
		driver.findElement(By.id("event-date")).click();
		sleep(2000);
		driver.findElement(By.id("event-date")).sendKeys(Keys.ENTER);
		sleep(3000);
		//String nowDateTime = driver.findElement(By.id("event-date")).getText();
		
		/*LocalDate today = LocalDate.now();
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy. MM dd.");
		String strToday = today.format(dateFormat);*/

		
		String strToday = dateTimeLocaleMinusOneHour(LocalDateTime.now());
		LocalDateTime today = LocalDateTime.now();
		LocalDateTime TodayMinusOneHour = today.minus(1, ChronoUnit.HOURS);
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy. MM. dd. HH:mm");
		String strTodayMinusOneWithDot = TodayMinusOneHour.format(dateFormat);
		
		driver.findElement(By.id("mileage")).click();
		sleep(2000);
		
		String serviceName = "Sofőrszolgálatocska";
		fillName("car_company_id_ac", serviceName);
		sleep(2000);
		
		int workPrice = 15123;
		fillName("fee", "" + workPrice);
		sleep(2000);
		
		String testText = "Megjegyzés elsőre";
		fillName("note", testText);
		
		submit();
		
		//Check in garage--------------------------------------------------------------------------
		onScreenAlert("Sikeres esemény hozzáadás");
		sleep(3000);
		driver.findElement(By.xpath("//a[contains(text(), 'adatlapja')]")).click();
		sleep(4000);
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//b[contains(text(), 'Sofőrszolgálat')]")));
		System.out.println("Sofőrszolgálat");
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains("Sofőrszolgálat"));
		Log.log("Képernyőn: Sofőrszolgálat");
		
		
		Log.log("Esemény sikeresen elmentve!");
		
		sleep(5000);
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
				"//a[not(contains(@class,'d-sm-none'))]/descendant-or-self::b[contains(text(),'Sofőrszolgálat')]")));
		driver.findElement(By.xpath(
				"//a[not(contains(@class,'d-sm-none'))]/descendant-or-self::b[contains(text(),'Sofőrszolgálat')]"))
				.click();

		sleep(3000);
		
		//Check in data sheet----------------------------------------------------------------------
		Log.log("Adatok ellenőrzése");
		onScreen(serviceName);
		onScreen(strToday);
		checkPrice(workPrice,"");
		onScreen(testText);
		
		clickText("Szerkesztés");
		
		//Check inputs-----------------------------------------------------------------------------
		Log.log("Adatok ellenőrzése szerkesztés űrlapon");
		onScreenValue(serviceName);
		onScreenValue(strTodayMinusOneWithDot);
		onScreenValue("" + workPrice);
		onScreen(testText);
		
		//Modify values----------------------------------------------------------------------------
		serviceName = "Valami másik szolgáltató";
		fillName("car_company_id_ac", serviceName);
		sleep(2000);
		
		workPrice = 23456;
		fillName("fee", "" + workPrice);
		sleep(2000);
		
		testText = "Megjegyzés másodikra";
		fillName("note", testText);
		
		submit();
		
		//Check modified values in data sheet------------------------------------------------------
		onScreenAlert("Sikeres esemény módosítás");
		Log.log("Szerkesztett adatok ellenőrzése");
		onScreen(serviceName);
		onScreen(strToday);
		checkPrice(workPrice,"");
		onScreen(testText);
		
		clickText(serviceName);
		driver.findElement(By.cssSelector(".fas.fa-trash.circle")).click();
		sleep(3000);
		clickLinkWithText("Esemény törlése");
		
		onScreenAlert("Az esemény sikeresen törölve!");
		
		Log.log("Sikeres sofőrszolgálat teszt!");
		
	}
	
	public static void addNewCarEventCarRescueAndTransport() throws IOException, InterruptedException {

		//Create event autómentés-------------------------------------------------------------
		clickLinkWithText("esemény hozzáadása");
		sleep(5000);
	
		WebElement element = driver.findElement(By.cssSelector(".sprite.sprite-car_transport"));
		Actions actions = new Actions(driver);
		actions.moveToElement(element).click().build().perform();
	
		//Fill details----------------------------------------------------------------------------
		sleep(2000);
		Log.log("Autómentés és autószállítás esemény felvitel");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("event-date")));
		driver.findElement(By.id("event-date")).click();
		sleep(2000);
		driver.findElement(By.id("event-date")).sendKeys(Keys.ENTER);
		sleep(3000);
		//String nowDateTime = driver.findElement(By.id("event-date")).getText();
		
		LocalDate today = LocalDate.now();
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String nowDateTime = today.format(dateFormat);
		
		LocalDateTime secondToday = LocalDateTime.now();
		LocalDateTime secondTodayMinusOneHour = secondToday.minus(1, ChronoUnit.HOURS);
		
		//secondToday = secondToday.setTime(LocalDateTime.getTime() - 3600 * 1000));
		//LocalDateTime secondTodayMinusOneHour = today.minus(1);
		
		dateFormat = DateTimeFormatter.ofPattern("yyyy. MM. dd. HH:mm");
		String nowDateTimeValueDot = secondTodayMinusOneHour.format(dateFormat);
		
		driver.findElement(By.id("mileage")).click();
		sleep(2000);
		
		String serviceName = "Aki hazaszállít";
		fillName("car_company_id_ac", serviceName);
		sleep(2000);
		
		int workPrice = 15123;
		fillName("fee", "" + workPrice);
		sleep(2000);
		
		String testText = "Megjegyzés elsőre";
		fillName("note", testText);
		
		submit();
		
		//Check in garage--------------------------------------------------------------------------
		onScreenAlert("Sikeres esemény hozzáadás");
		sleep(4000);
		driver.findElement(By.xpath("//a[contains(text(), 'adatlapja')]")).click();
		
		onScreen("Autómentés és autószállítás");
		
		Log.log("Esemény sikeresen elmentve!");
		
		sleep(3000);
		clickText("Autómentés és autószállítás");
		
		//Check in data sheet----------------------------------------------------------------------
		Log.log("Adatok ellenőrzése");
		onScreen(serviceName);
		onScreen(nowDateTime);
		checkPrice(workPrice,"");
		onScreen(testText);
		
		clickText("Szerkesztés");
		
		//Check inputs-----------------------------------------------------------------------------
		Log.log("Adatok ellenőrzése szerkesztés űrlapon");
		onScreenValue(serviceName);
		onScreenValue(nowDateTimeValueDot);
		onScreenValue("" + workPrice);
		onScreen(testText);
		
		//Modify values----------------------------------------------------------------------------
		serviceName = "Valami másik szolgáltató";
		fillName("car_company_id_ac", serviceName);
		sleep(2000);
		
		workPrice = 23456;
		fillName("fee", "" + workPrice);
		sleep(2000);
		
		testText = "Megjegyzés másodikra";
		fillName("note", testText);
		
		submit();
		
		//Check modified values in data sheet------------------------------------------------------
		onScreenAlert("Sikeres esemény módosítás");
		Log.log("Szerkesztett adatok ellenőrzése");
		onScreen(serviceName);
		onScreen(nowDateTime);
		checkPrice(workPrice,"");
		onScreen(testText);
		
		driver.findElement(By.cssSelector(".fas.fa-trash.circle")).click();
		sleep(2000);
		clickLinkWithText("Esemény törlése");
		
		onScreenAlert("Az esemény sikeresen törölve!");
		
		Log.log("Sikeres autómentés és autószállítás teszt!");
		
	}
	
	public static void checkAdminArchive() throws IOException, InterruptedException, AWTException {
		
		addNewCar();
		String carID = getCarId();
		clickLinkWithText("Autó törlése");
		sleep(2000);
		
		clickButton("Autó törlése");
		sleep(2000);
		
		onScreenAlert("Autó törölve!");
		goToPage(url + "/hu/admin/car/archive");
		sleep(3000);
		
		onScreen(adminUser);
		onScreen(carID);
		onScreen("30 napig");
		
		Log.log("Sikeres archívum ellenőrzés");
		
	}
	
	public static void checkGPSAlert() throws IOException, InterruptedException, AWTException {
		
		//Szolgáltatást kell venni ahhoz hogy ne engedjen törölni
		//onScreenAlert("");
		
	}
	
	public static void buyGPS() throws IOException, InterruptedException {

		clickLinkWithText("GPS követő vásárlása");
		sleep(2000);
		driver.findElement(By.xpath("(//a[@class='btn btn-block btn-white'])[2]")).click();
		sleep(3000);
		fillName("name","test name");
		fillName("phone","12345678");
		selectValue("customer_type","1");
		
		fillName("invoice[loc_zip_id_ac]", "1052");
		sleep(1000);
		driver.findElement(By.id("invoice-loc-zip-id")).sendKeys(Keys.ENTER);
		fillName("invoice[street]", "Sas");
		driver.findElement(By.id("invoice-street-type")).click();
		sleep(1000);
		driver.findElement(By.id("invoice-street-type")).sendKeys(Keys.ARROW_DOWN);
		driver.findElement(By.id("invoice-street-type")).sendKeys(Keys.ENTER);
		sleep(1000);
		fillName("invoice[street_num]", "25");
		fillName("invoice[building]", "a");
		fillName("invoice[floor]", "2");
		fillName("invoice[door]", "204");
		
		randomSelect("car_gps_ident[requested_place]");
		driver.findElement(By.id("car-gps-ident-requested-date-from")).click();
		sleep(1000);
		driver.findElement(By.id("car-gps-ident-requested-date-from")).sendKeys(Keys.ENTER);
		sleep(1000);
		driver.findElement(By.id("car-gps-ident-requested-date-to")).click();
		sleep(1000);
		driver.findElement(By.id("car-gps-ident-requested-date-to")).sendKeys(Keys.ENTER);
		sleep(1000);
		
		clickCheckboxById("accept-rules");
		clickCheckboxById("accept-rules2");
		
		clickButton("Tovább a fizetéshez");

	}
	
	public static void giveDocuGen(String email) throws IOException, InterruptedException {
		
		goToPage(url + "/hu/admin/car/car-companies?quick_search=" + email);
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".btn.btn-default.btn-link.command.command-edit"))).click();
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[contains(text(), 'Dokumentum kitöltő')]")));
		clickText("Dokumentum kitöltő");
		
		submit();
		
	}
	
	public static void justForPrivate() throws IOException, InterruptedException {
		
		goToPage(url + "/hu/kijelentkezes");
		
		Random rand = new Random();
		int randNum = rand.nextInt(5000);
		fullClearUser = "fullCleanUser" + randNum;
		
		try {
			
			login(fullClearUser, "ecdh1");
			deleteUser();
			
		}catch(Exception e) {}
		
			try {
				
				registerUser("fullCleanUser", "ecdh1");
				activateUser();
				
			}catch(Exception e) {}
		
	}
	
	static void registrationFirst(String username, String password) throws IOException, InterruptedException {
		
		goToPage(url + "/hu/regisztracio/szerviznaplo-alkalmazas-autosoknak");

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
	
	static void registrationSecond(String username, String password) throws IOException, InterruptedException {
		
		goToPage(url + "/hu/regisztracio/hasznalt-auto-eladas");

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

}
