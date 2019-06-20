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

import org.openqa.selenium.By;
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

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='Aktiválás']")));
		assertTrue("Registration succeed", driver.getPageSource().contains("befejezte"));
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
		print("FOUND: " + driver.findElements(By.cssSelector("input[name=\"" + name + "\"]")).size());
		if (driver.findElements(By.cssSelector("input[name=\"" + name + "\"]")).size() != 0) {
			driver.findElement(By.cssSelector("input[name=\"" + name + "\"]")).clear();
			if (name == "doors") {
				driver.findElement(By.cssSelector("input[name=\"" + name + "\"]")).click();

				driver.findElement(By.cssSelector("input[name=\"" + name + "\"]")).clear();

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
			String randNumTax = "";
			String randNumReg = "";
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

			fillName("invoice[street]", "Repülőtéri ");
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
		randomSelect("car_year");
		randomSelect("car_month");
		sleep(2000);
		manufacturer = fillCarField("#car-manufacturer-id", "#ui-id-1");
		sleep(2000);
		model = fillCarField("#car-model-id", "#ui-id-2");
		sleep(2000);
		click("#car-type-id");
		sleep(5000);
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

	}

	public static void fillCarDetail() throws IOException, InterruptedException, AWTException {

		clickLinkWithText("Adatok szerkesztése");
		TestBase.select("petrol", "Dízel");
		randomSelect("car_condition");

		Random rand = new Random();
		long leftLimit = 11111111111111111L;
		long rightLimit = 99999999999999999L;
		long randomLong = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));

		fillName("vin", String.valueOf(randomLong));

		Integer randomNum = rand.nextInt(999999999);
		fillName("motor_number", randomNum.toString());

		randomNum = rand.nextInt(199);
		fillName("power", randomNum.toString());

		randomNum = rand.nextInt((899999) + 100000);
		String randomNumSt = String.valueOf(randomNum) + "AB";
		fillName("traffic_license", randomNumSt);

		randomNum = rand.nextInt((899999) + 10000);
		String randomNumStr = String.valueOf(randomNum) + 'A';
		fillName("registration_number", randomNumStr);

		randomNum = rand.nextInt(1999);
		fillName("fuel_capacity", randomNum.toString());

		// randomNum = rand.nextInt(4) + 1;
		// fillName("doors", randomNum.toString());
		fillName("doors", "3");
		sleep(10000);

		randomNum = rand.nextInt(4) + 1;
		// fillName("seats", randomNum.toString());
		fillName("seats", "4");

		randomSelect("make");
		randomSelect("car_offset");
		randomSelect("cylinder");
		randomSelect("warranty");
		randomSelect("enviromental_v9");

		randomNum = rand.nextInt(200) + 100;
		fillName("max_load", randomNum.toString());

		randomNum = rand.nextInt(200) + 100;
		fillName("trunk", randomNum.toString());

		randomNum = rand.nextInt(2000) + 1000;
		fillName("engine_capacity", randomNum.toString());

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

		click(".btn-primary");
		Thread.sleep(3000);

		Log.log("Autó beküldve.");

		clickLinkWithText("1");
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(manufacturer));
		Log.log("Autó mentve. Gyártó: " + manufacturer);
		assertTrue("Szerepel a forrásban", driver.getPageSource().contains(model));
		Log.log("Modell: " + model);

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
		Integer randomNum = 1 + rand.nextInt((30 - 1) + 1);
		String amount = String.valueOf(randomNum);

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		driver.findElement(By.cssSelector("input[name=\"fueling_date\"]")).clear();
		fillName("fueling_date", dateFormat.format(date));
		fillName("liter", amount);
		String fuelType = randomSelect("type");
		fillName("car_gas_station_id_ac", "mészár");
		click("ul li.ui-menu-item:nth-child(2) a");

		submit();

		Log.log("Esemény: tankolás beküldve.");
		onScreen(amount + " l");
		Log.log("Esemény: tankolás elmentve.");

		click(".event.timeline a[href*=\"tankolas\"]");
		onScreen(fuelType);
		onScreen(amount + " l");
		assertTrue("Gas station coordinates false",
				driver.getPageSource().contains("google.maps.LatLng(47.49087143, 19.03070831)"));
		driver.findElement(By.id("map0")).isDisplayed();
		Log.log("Térkép ok");

		clickLinkWithText("Szerkesztés");
		checkField("liter", amount);
		checkField("type", fuelType);
		submit();

		click("i.fa-trash");
		click("a[data-apply=\"confirmation\"]");

		sleep(8000);
		assertTrue("Event deleted", !driver.getPageSource().contains(amount + " l"));
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

		wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Autó vizsgáztatva')]")));

		assertTrue("Mûszaki vizsga elmentve", driver.getPageSource().contains("Autó vizsgáztatva"));
		Log.log("Esemény: mûszaki vizsga elmentve.");

		onScreen("Abc kft.");

		clickLinkWithText("Autó vizsgáztatva");
		onScreen("Abc kft.");
		String now = dateLocale(LocalDate.now());

		onScreen(now);
		onScreen(noteText);

		clickLinkWithText("Szerkesztés");

		now = dateDashes(LocalDate.now());
		checkField("test_date", now);
		checkField("car_company_id_ac", "Abc kft.");
		onScreen(noteText);

		driver.findElement(By.cssSelector(".checkbox-taxi label")).click();
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
		randomprice = randomprice + 10000000;
		sleep(1000);
		fillName("sell_description", getRandomText(40));
		sleep(1000);
		Random rand2 = new Random();
		int randomzip = rand.nextInt(89) + 10;
		fillName("loc_zip_id_ac", ""+randomzip);
		sleep(1000);
		click("#ui-id-1");
		sleep(1000);
		TestBase.select("car_user[mobile_country]", "Magyarország");
		sleep(1000);
		fillName("car_user[mobile]", "301234567");
		sleep(1000);
		driver.findElement(By.id("save-and-back")).click();
		checkPrice(randomprice, " ");
		Log.log("Autó sikeresen meghirdetve");
		clickLinkWithText("Használt autó hirdetések");
		Log.log("Használt Autó kereső");
		clickLinkWithText("Részletes kereső");
		Log.log("Részletes Kereső Kiválasztva");
		fillName("pricefrom",""+randomprice);
		fillName("priceto",""+randomprice);
		Log.log("Ár megadva");
		driver.findElement(By.id("form-button")).click();
		Log.log("Találatok Megjelenítése");
		driver.findElement(By.className("price")).click();
		Log.log("Az autó szerepel a Használt Autó hírdetések között!");
		goToPage(carURL);
		sleep(3000);
		driver.findElement(By.cssSelector(".fas.fa-pencil-alt")).click();
		click(".switch");
		driver.findElement(By.id("save-and-back")).click();
		Log.log("Hirdetés levéve");


	}

	public static void addNewCarEventTires() throws IOException, InterruptedException {
		clickLinkWithText("esemény");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-mot")));
		click(".sprite-tire");

		click("input[name=\"service_date\"]");
		// click("body");
		new Actions(driver).moveByOffset(0, 0).click().build().perform();

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

		click("i.fa-trash");
		click("a[data-apply=\"confirmation\"]");

		sleep(8000);
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

		clickLinkWithText("Az autó megsérült");
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

	public static void addNewCarEventOther() throws IOException {
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

		Log.log("Esemény: egyéb beküldve.");

		wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + eventText + "')]")));
		assertTrue("Autó meghirdetve", driver.getPageSource().contains("Teszt esemény " + randNum));

		Log.log("Esemény: egyéb sikeresen elmentve.");

		clickLinkWithText(eventText);
		driver.findElement(By.cssSelector("a.red-link")).click();
		clickLinkWithText("Esemény törlése");

		assertTrue("Event deleted", !driver.getPageSource().contains(eventText));
		Log.log("Esemény: egyéb sikeresen törölve.");
	}

	public static void adminLogin() throws IOException, InterruptedException {
		goToPage(url + "/hu/bejelentkezes");
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

	public static void oneStepInner() throws IOException {
		List<WebElement> elements = driver.findElements(By.cssSelector("#mycar-block.card .profile-car-item"));
		for (WebElement element : elements) {
			Log.log(element.findElement(By.className("numberplate")).getText());
		}

		element = elements.get(new Random().nextInt(elements.size()));
		Log.log(element.findElement(By.className("numberplate")).getText() + " selected.");
		element.click();
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

		randomSelect("car_tire_company_offer_items[0][season]");
		driver.findElement(By.cssSelector("input[name=\"car_tire_company_offer_items[0][delivery_date]\"]")).click();
		driver.findElement(By.cssSelector("textarea[name=\"note\"]")).sendKeys("test note");

		driver.findElement(By.className("submitBtn")).click();
		Thread.sleep(5000);

		return randNum;

	}

	public static String checkRequestPart(String requestId) throws IOException, InterruptedException {
		TestBase.goToPage(url + "/hu/alkatresz-erdeklodesek");

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

		randomSelect("car_tire_company_offer_items[0][season]");
		driver.findElement(By.cssSelector("input[name=\"car_tire_company_offer_items[0][delivery_date]\"]")).click();
		driver.findElement(By.cssSelector("textarea[name=\"note\"]")).sendKeys("test note");

		driver.findElement(By.className("submitBtn")).click();
		Thread.sleep(5000);

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

	public static void userLogout() throws IOException {
		Log.log("Kijelentkezés a fiókból.");
		goToPage(url + "/hu/kijelentkezes");
	}

	public static void registerUserWrongEmail() throws IOException {

		driver.findElement(By.partialLinkText("Regisztráció")).click();
		assertEquals("Go to URL", driver.getCurrentUrl(), url + "/hu/regisztracio");
		Log.log("Click Registraion");

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

	public static void checkRequestOffer(String companyName, String price) throws IOException {
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

		assertEquals("Ár stimmel", savedPrice, formattedPrice + " Ft");

		Log.log("Ár stimmel");
	}

	public static void sendRequestFinalOrder() {
		click(".checkbox");
		click(".btn-lg");
		submit();

	}

	public static void checkRequestFinalOrder(String price) throws IOException {
		click(".bell");
		clickLinkWithText("Gumi rendelés");
		onScreen(price);
		Log.log("Értesítés céges oldalon megérkezett.");
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

	public static String SendRequestPart() throws IOException {
		clickLinkWithText("Ajánlatkérés");
		click(".sprite-technical");
		clickXpath("//span[contains(text(),'Fékrendszer')]/following-sibling::i");
		clickXpath("//span[contains(text(),'Fékcső')]/following-sibling::a");
		driver.findElement(By.xpath("//span[contains(text(),'Fékcső')]/following-sibling::a")).click();
		clickLinkWithText("Ajánlatkérés");

		fillName("loc_zip_id_ac", "10");
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

	private static void clickXpath(String string) {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(string)));
		driver.findElement(By.xpath(string)).click();
	}

	private static void clickLinkWithText(String string) {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
				"//a[not(contains(@class,'d-sm-none'))]/descendant-or-self::*[contains(text(),\"" + string + "\")]")));
		driver.findElement(By.xpath(
				"//a[not(contains(@class,'d-sm-none'))]/descendant-or-self::*[contains(text(),\"" + string + "\")]"))
				.click();
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
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), \"" + string + "\")]")));
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
		List<WebElement> items = driver.findElements(By.cssSelector("ul.tree-browser > li"));
		List<String> list = new ArrayList<String>();
		String oneItem;

		int randPrice = new Random().nextInt(123456);
		fillName("price_work", "" + randPrice);

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
		clickLinkWithText("Időszakos szerviz");
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
		Log.log("Sikeresen mentve");
		clickLinkWithText("Egyéb szerviz");
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
		submit();
		Log.log("Sikeres módosítás");

		click(".fas.fa-trash.circle");
		click(".btn.btn-sm.h-100.d-flex.align-items-center.btn-secondary");
		sleep(1000);
		assertTrue("Event deleted", !driver.getPageSource().contains(noteText));
		Log.log("Esemény: egyéb sikeresen törölve.");

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
		onScreen(penaltyType);
		checkPrice(price, "\u00a0");

		clickLinkWithText("Büntetés");
		onScreen(penaltyType);
		onScreen("Nem");

		clickLinkWithText("Szerkesztés");
		checkField("penalty_type", penaltyType);
		checkField("price", "" + price);
		onScreen(noteText);
		driver.findElement(By.xpath("//*[contains(text(),'Fizetve')]")).click();
		submit();
		clickLinkWithText(penaltyType);
		onScreen("Igen");

		click("i.fa-trash");
		clickLinkWithText("Esemény törlése");

		sleep(8000);
		assertTrue("Event deleted", !driver.getPageSource().contains(penaltyType));
		Log.log("Esemény: Bírság sikeresen törölve.");

	}

	public static void addNewCarEventHighwayFee() throws IOException, InterruptedException {
		// goToPage(url+"/hu/autopalya-matrica-hozzadasa/" + getCarId());
		clickLinkWithText("esemény hozzáadása");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sprite-mycar_highway_ticket")));
		click(".sprite-mycar_highway_ticket");

		driver.findElement(By.cssSelector("input[name=\"start_date\"]")).click();
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

		sleep(4000);

		String pattern = "//dt[contains(text(),' Autópálya-matrica érvényessége')]//following-sibling::dd[1]";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pattern)));
		WebElement insuranceParent = driver.findElement(By.xpath(pattern));
		String insurance = insuranceParent.findElement(By.tagName("a")).getText();

		// assertTrue("Autópályamatrica lejárat OK.", insurance.contains(expiration));
		// Log.log("Autópályamatrica lejárat OK.");

		assertTrue("Autópályamatrica adatok OK.", insurance.contains(expiration));
		assertTrue("Autópályamatrica adatok OK.", insurance.contains(name));
		Log.log("Autópályamatrica adatok OK.");

		onScreen("Új autópálya matrica");
		clickLinkWithText("Új autópálya matrica");
		onScreen(expiration);
		onScreen(name);
		onScreen(price);

		clickLinkWithText("Szerkesztés");

		list = driver.findElements(By.cssSelector("input[type=\"radio\"]"));
		randNumber = new Random().nextInt(size - 1) + 1;
		id = list.get(randNumber).getAttribute("id");
		driver.findElement(By.xpath("//label[@for='" + id + "']")).click();
		name = driver.findElement(By.cssSelector("label[for=\"" + id + "\"] .ticket-name i")).getText();
		expiration = driver.findElement(By.cssSelector("label[for=\"" + id + "\"] .ticket-expiration")).getText();
		price = driver.findElement(By.cssSelector("label[for=\"" + id + "\"] .ticket-price")).getText();

		Log.log(name + " autópálya matrica kiválasztva.");
		Log.log(expiration + " lejárattal.");
		Log.log(price + " áron.");
		submit();

		onScreen(expiration);
		onScreen(name);
		onScreen(price);

	}

	private static void submit() {
		click(".submitBtn");
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

		click("i.fa-trash");
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
				By.xpath("//*[contains(text(), \"" + parts[0] + "\") and contains(text(), \"" + parts[1] + "\")]")));
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

		submit();

		click("i.fa-trash");
		click("a[data-apply=\"confirmation\"]");

		sleep(8000);
		assertTrue("Event deleted", !driver.getPageSource().contains("GAP biztosítás"));
		Log.log("Esemény: GAP biztosítás sikeresen törölve.");
	}

	private static void sleep(int i) throws InterruptedException {
		System.out.println("wait " + i + " millisconds");
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

		click(".switch"); 
		Random rand = new Random();
		Integer randomNum = rand.nextInt(40000) + 50000;
		String amount = String.valueOf(randomNum);
		fillName("rent_price_1", amount);
		randomNum = rand.nextInt(30000) + 40000;
		amount = String.valueOf(randomNum);
		fillName("rent_price_2", amount);
		randomNum =rand.nextInt(20000)+ 30000;
		amount = String.valueOf(randomNum);
		fillName("rent_price_3", amount);
		randomNum = rand.nextInt(10000) + 20000;
		amount = String.valueOf(randomNum);
		fillName("rent_price_4", amount);
		randomNum =rand.nextInt(10000) + 10000;
		amount = String.valueOf(randomNum);
		fillName("rent_bail", amount);
		sleep(1000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/main/section[3]/div/div[2]/form/div[3]/div[2]/div[2]/div/label"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("rent-penalty-days"))).click();
		int randomday = rand.nextInt(28);
		fillName("rent_penalty_days", ""+randomday);
		int randompenalty = rand.nextInt(9)+1;
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
		
		driver.findElement(By.id("rent-description")).clear();
		driver.findElement(By.id("rent-description")).sendKeys(getRandomText(50));
		submit();
		sleep(5000);
		Log.log("Autó bérlésre sikeresen meghirdetve");
		clickLinkWithText("Bérautó hirdetések");
		Log.log("Bérelhető Autók megjelenítése");
		fillName("loc_zip_id_ac",""+randomzip);
		click(".ui-menu-item");
		sleep(2000);
		Log.log("Irsz megadása");
		driver.findElement(By.id("form-button")).click();
		Log.log("Keresés Indítása");
		driver.findElement(By.cssSelector(".col.btn.btn-secondary")).click();
	
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
		//driver.findElement(By.id("start-date")).sendKeys(startDate);
		fillName("start_date",startDate);
		sleep(1000);
		//driver.findElement(By.id("end-date")).sendKeys(endDate);
		driver.findElement(By.name("end_date")).click();
		driver.findElement(By.name("end_date")).clear();
		fillName("end_date",endDate);
		sleep(1000);
		driver.findElement(By.id("form-button")).click();
		Log.log("Bérlési időszak megadása!");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pickup-location"))).click();
		randomNum = rand.nextInt(1) + 1;
		Select pick = new Select(driver.findElement(By.id("pickup-location")));
		pick.selectByIndex(randomNum);
		Log.log("SIKERÜLT?");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dropdown-location"))).click();
		Select drop = new Select(driver.findElement(By.name("dropdown_location")));
		sleep(500);
		randomNum = rand.nextInt(1) + 1;
		drop.selectByIndex(randomNum);

		
		driver.findElement(By.id("notes")).sendKeys(getRandomText(50));
        
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
 		 randomNum = rand.nextInt(1) + 1;
 		 st.selectByIndex(randomNum);
         fillName("car_address[street_num]","11");
         fillName("car_address[building]","A");
         fillName("car_address[floor]","1");
         fillName("car_address[door]","1");
         driver.findElement(By.cssSelector(".mb-3.col.btn.btn-primary")).click(); 
         Log.log("Bérlés kérelem kitöltve");
         driver.findElement(By.id("profileMiniImg")).click();
         clickLinkWithText("Garázs");
         Log.log("Visszalépés a Garázsba");
         goToPage(TestBase.url+"/hu/foglalasaim");
         click("i.fa-trash");
         click("a[data-apply=\"confirmation\"]");
         Log.log("Foglalás Sikeresen Törölve");
         Log.log("Ugrás az autóbérlés modosításhoz");
         goToPage(carURL);
         sleep(3000);
         driver.findElement(By.cssSelector(".fas.fa-pencil-alt")).click();
         wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/main/section[3]/div/div[2]/form/div[3]/div[2]/div[2]/div/label"))).click();
         click(".switch");
         driver.findElement(By.id("form-button")).click();
         sleep(2000);
         wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Vissza az adatlapra')]"))).click();
         Log.log("Hirdetés levéve");


	}

	public static void advancedSearchRent() throws IOException, InterruptedException {
		click(".user-menu .nav-menu a");
		click(".sprite-rent-cars");
		clickXpath("//label[contains(text(),'Gyártó')]/following-sibling::a");
		// TODO
		// String manufacturer = fillCarField(GYARTO);
		sleep(5000);
		submit();

		// Log.log("Keresés a meghirdetett " + manufacturer + " autóra.");

		// wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),
		// '2 194 562 Ft')]")));
		assertTrue("Car found", driver.getPageSource().contains("BMW 116"));
		Log.log("Autó szerepel a használtautó keresőben.");

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
		fillName("logo_text", getRandomText(5));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name=\"logo_slogan\"]")));
		fillName("logo_slogan", getRandomText(5));
		sleep(2000);  
		driver.findElement(By.cssSelector(".btn.btn-primary.submitBtn.tsLoadingIcon")).click();
		Log.log("Fejléc mentése"); 
		sleep(2000);
		
		
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
			
			Random rand = new Random();
			
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name=\"car_company_page_menus[2][title]\"]")));
			fillName("car_company_page_menus[0][title]", getRandomText(4).substring(0, 1 + rand.nextInt(10)));
			randomSelect("car_company_page_menus[0][menu_modul]");
			
			fillName("car_company_page_menus[1][title]", getRandomText(4).substring(0, 1 + rand.nextInt(10)));
			randomSelect("car_company_page_menus[1][menu_modul]");
			
			fillName("car_company_page_menus[2][title]", getRandomText(4).substring(0, 1 + rand.nextInt(10)));
			randomSelect("car_company_page_menus[2][menu_modul]");
			Log.log("3 elem részletezés"); 
			
			driver.findElement(By.cssSelector(".btn.btn-primary.submitBtn.tsLoadingIcon")).click();
			Log.log("Menü mentése");  
			 
			}else {
				
				Log.log("Már ki van töltve"); 
				
			}

		sleep(1000);  
		driver.findElement(By.xpath("/html/body/main/div/div/div[3]/h4/a")).click();
		Log.log("Bemutatkozás szerkesztése"); 
		sleep(2000);
		fillName("about_us_title",getRandomText(5));
		fillName("about_us",getRandomText(20));
		driver.findElement(By.cssSelector(".btn.btn-primary.submitBtn.tsLoadingIcon")).click();
		Log.log("Bemutatkozás mentve");
		sleep(2000);
		
		clickLinkWithText("új oldal hozzáadása");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div/div/form//div/div/div/div/div/input"))).sendKeys("Teszt cím");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("content_ifr"))).sendKeys(getRandomText(60));
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
		goToPage(url+"/hu/ceg-oldal-szerkesztes");
		sleep(3000);
		Log.log("munkatárs mentve");
		
		companyPageNewArticle();
		companyPageNewArticle();
		companyPageNewArticle();
		companyPageNewArticle();

		CompanyWebpage();

	}

	public static String companyPageNewArticle() throws IOException, InterruptedException {
		wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//a/descendant-or-self::*[contains(text(),\"új hír hozzáadása\")]")));
		clickLinkWithText("új hír hozzáadása");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("textarea[name=\"lead\"]")));
		String title = getRandomText(5);
		fillName("title", title);
		fillName("lead", getRandomText(10));
		fillName("content", getRandomText(50));
		submit();
		sleep(4000);
		Log.log("Céges oldal hír beküldve: " + title);
		return title;  

	}

	public static void CompanyWebpage() throws IOException, InterruptedException {
		goToPage(url + "/hu/ceg-oldal-szerkesztes");
		clickLinkWithText("Menü szerkesztése");
		sleep(6000);
		driver.findElement(By.id("car-company-page-menus-add")).click();
		fillName("car_company_page_menus[0][title]", "Rólunk");
		select("car_company_page_menus[0][menu_modul]", "Nyitó oldal");
		driver.findElement(By.id("car-company-page-menus-add")).click();
		fillName("car_company_page_menus[1][title]", "Híreink");
		select("car_company_page_menus[1][menu_modul]", "Hírek");
		submit();
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

		sleep(500);
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
			sleep(500);
			randomNum = rand.nextInt(25) + 1;
			brand.selectByIndex(randomNum);
		} catch (org.openqa.selenium.StaleElementReferenceException ex) {
			Select brand = new Select(driver.findElement(By.id("MainContent_control_BrandList")));
			brand.selectByIndex(randomNum);
		}
		Log.log("Márka megadása");

		try {
			Select brand = new Select(driver.findElement(By.id("MainContent_control_BrandList")));
			sleep(500);
			randomNum = rand.nextInt(1) + 1;
			brand.selectByIndex(randomNum);
		} catch (org.openqa.selenium.StaleElementReferenceException ex) {
			Select brand = new Select(driver.findElement(By.id("MainContent_control_EngineType")));
			brand.selectByIndex(randomNum);
		}
		Log.log("Motor típus megadása");

		try {
			Select brand = new Select(driver.findElement(By.id("MainContent_control_ModelRange1List")));
			sleep(500);
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
			sleep(500);
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
		   sleep(1000);
		   driver.findElement(By.id("MainContent_controlCheckBoxInvoiceRequired")).click();
		   
		   driver.switchTo().defaultContent();
		   driver.switchTo().frame(driver.findElement(By.tagName("iframe")));
		 
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

		// driver.findElement(By.xpath("/html/body/main/section[2]/div/div[2]/div[2]/div[3]/a[3]/i")).click();
		// driver.findElement(By.cssSelector("i.fas.fa-trash.circle")).click();
		click("i.fa-trash");
		sleep(2000);
		driver.findElement(By.className("btn-secondary")).click();

		Log.log("Esemény: Teljesítményadó második részlet sikeresen törölve.");
		sleep(2000);
		// driver.findElement(By.xpath("/html/body/main/section[2]/div/div[2]/div[2]/div[3]/a[3]/i")).click();
		click("i.fa-trash");
		sleep(2000);
		driver.findElement(By.className("btn-secondary")).click();

		Log.log("Esemény: Teljesítményadó első részlet sikeresen törölve.");
		sleep(2000);

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

		// driver.findElement(By.xpath("/html/body/main/section[2]/div/div[2]/div[2]/div[3]/a[3]/i")).click();
		click("i.fa-trash");
		driver.findElement(By.className("btn-secondary")).click();

		Log.log("Esemény: Egész éves teljesítményadó sikeresen törölve.");

	}

	public static void inviteActivateFriend() throws IOException, InterruptedException {

		driver.findElement(By.xpath("/html/body/header/div/div/div[2]/div[4]/a")).click();
		driver.findElement(By.className("sprite-invite")).click();
		// clickLinkWithText("Új meghívó link");
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
		int randIndex = new Random().nextInt(3)+1;
		Select type = new Select(driver.findElement(By.id("type")));
		type.selectByIndex(randIndex);
		Log.log("Típus választás");
		
		fillName("price","200000");
		Log.log("Gumi ára");
		
		randIndex = new Random().nextInt(28)+1;
		Select mufacturer = new Select(driver.findElement(By.id("mufacturer")));
		mufacturer.selectByIndex(randIndex);
		Log.log("Márka választás");
		
		fillName("item_description","test model");
		Log.log("Gumi modell");
		
		randIndex = new Random().nextInt(2)+1;
		Select number = new Select(driver.findElement(By.id("number")));
		number.selectByIndex(randIndex);
		Log.log("darabszám választás");
		int kerekszam = 0;
		if(randIndex == 1) {
			kerekszam = 2;
		}else {
			kerekszam = 4;
		}
		
		randIndex = new Random().nextInt(2)+1;
		Select worn = new Select(driver.findElement(By.id("worn")));
		worn.selectByIndex(randIndex);
		Log.log("állapot választás");
		
		randIndex = new Random().nextInt(52)+1;
		Select dot_week = new Select(driver.findElement(By.id("dot_week")));
		dot_week.selectByIndex(randIndex);
		Log.log("DOT hét");
		
		randIndex = new Random().nextInt(30)+1;
		Select dot_year = new Select(driver.findElement(By.id("dot_year")));
		dot_year.selectByIndex(randIndex);
		Log.log("Dot év");
		
		if(kerekszam == 4) {
			randIndex = new Random().nextInt(10)+1;
			Select thread_depth_1 = new Select(driver.findElement(By.id("thread-depth-1")));
			thread_depth_1.selectByIndex(randIndex);
			Log.log("Bal első");
			
			randIndex = new Random().nextInt(10)+1;
			Select thread_depth_2 = new Select(driver.findElement(By.id("thread-depth-2")));
			thread_depth_2.selectByIndex(randIndex);
			Log.log("Bal hátsó");
			
			randIndex = new Random().nextInt(10)+1;
			Select thread_depth_3 = new Select(driver.findElement(By.id("thread-depth-3")));
			thread_depth_3.selectByIndex(randIndex);
			Log.log("Jobb első");
			
			randIndex = new Random().nextInt(10)+1;
			Select thread_depth_4 = new Select(driver.findElement(By.id("thread-depth-4")));
			thread_depth_4.selectByIndex(randIndex);
			Log.log("Jobb hátsó");
			
		}else {
			
			randIndex = new Random().nextInt(10)+1;
			Select thread_depth_1 = new Select(driver.findElement(By.id("thread-depth-1")));
			thread_depth_1.selectByIndex(randIndex);
			Log.log("Bal első");
			
			randIndex = new Random().nextInt(10)+1;
			Select thread_depth_2 = new Select(driver.findElement(By.id("thread-depth-2")));
			thread_depth_2.selectByIndex(randIndex);
			Log.log("Bal hátsó");
		}
		
		randIndex = new Random().nextInt(500)+100;
		fillName("tire_storage","test text"+randIndex);
		Log.log("Tárolás megjegyzés");
		
		driver.findElement(By.cssSelector(".btn.btn-primary.submitBtn.tsLoadingIcon")).click();
		Log.log("Gumi sikeresen hozzáadva");
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
		driver.findElement(By.xpath("/html/body/main/section[2]/div/div[1]/div[2]/div/div/div/div/div/div/div/table/tbody/tr/td/div/div/div[3]/div[2]/table/thead/tr/td[7]")).click();
		sleep(1000);
		Log.log("Nap kiválasztása");
		clickLinkWithText("Esemény hozzáadása");
		sleep(1000);
		int rand = new Random().nextInt(500)+500;
		fillName("title","Test esemény "+ rand);
		rand = new Random().nextInt(500)+500;
		fillName("description","Test megjegyzés "+ rand);
		rand = new Random().nextInt(89)+10;
		fillName("cal_location",""+ rand);
		sleep(1000);
		
		driver.findElement(By.id("cal_location")).sendKeys(Keys.ARROW_DOWN);
		driver.findElement(By.id("cal_location")).sendKeys(Keys.ENTER);
		Log.log("Autocomplete mező kitöltése");
		
		driver.findElement(By.cssSelector(".btn.btn-primary.w-100")).click();
		
		Log.log("Naptári esemény sikeresen felvive");

	}
	
}
