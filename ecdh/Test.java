package ecdh;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.client.utils.DateUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class Test extends TestBase {
	public static void main(String[] args) throws Throwable {
		System.setProperty("webdriver.chrome.driver", "/www/webdrivers/chromedriver");
		driver = new ChromeDriver();
		driver.get("http://testcenter.vr/selenium-test-surface.php");
		
		List<WebElement> links = driver.findElements(By.cssSelector("#dealerResults li"));
		for (WebElement we : links) {
		    
			
			String classs = we.getAttribute("class");
			System.out.println("|" + classs + "|");
			if (!classs.equals("pagination")) {
				
			    List<WebElement> types = we.findElements(By.cssSelector(".dealerTypeIcons div"));
			    String typesString = "";
			    for (WebElement type : types) {
			    	typesString += type.getAttribute("class");
			    }
			    
			    System.out.println("Type" + typesString);
			    
			    String company = we.findElement(By.cssSelector(".dealer-link a.infoLink")).getAttribute("href");
			    String compName = company.substring(company.lastIndexOf("/"));
				
				System.out.println("Company" + compName);
			}
		}
		
		/*
		String number = "1234";
		double amount = Double.parseDouble(number);
		DecimalFormat formatter = new DecimalFormat("###,###,###");
		
		String output = formatter.format(amount).replaceAll(",", "&nbsp;");

		System.out.println(output);
        
		String string = "118\u00a0438";
		try {
			driver.findElement(By.xpath("//*[contains(text(), \"" + string + "\")]")).click();
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		//System.out.println(driver.findElement(By.cssSelector("input[name=\"hey\"]")).isDisplayed());
		
		//String window = driver.getWindowHandle();
		//((JavascriptExecutor) driver).executeScript("alert('Test')");
		//driver.switchTo().alert().accept();
		//driver.switchTo().window(window);
		
		
		WebElement mySelectElement = driver.findElement(By.cssSelector("select[name='select']"));
		Select dropdown = new Select(mySelectElement);
		dropdown.selectByVisibleText("fff");
		
		Select sel = new Select(driver.findElement(By.id("select")));
		String val = sel.getFirstSelectedOption().getText();
	
		System.out.println(val);
		
		LocalDate futureDate = LocalDate.now().plusMonths(3);
		System.out.println(futureDate);
		
		LocalDate dueDate = LocalDate.now().plusMonths(5);
	      
	    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-LL-dd");
	    String strDueDate = dueDate.format(dateFormat);
	    System.out.println("plus 3 " + strDueDate);
	    
	    LocalDate dueDate2 = LocalDate.now().plusYears(1);
	    
	    DateTimeFormatter dateFormat2 = DateTimeFormatter.ofPattern("yyyy. MMMM dd.");
	    DateTimeFormatter yearF = DateTimeFormatter.ofPattern("yyyy.");
	    DateTimeFormatter monthF = DateTimeFormatter.ofPattern("MMMM");
	    DateTimeFormatter dayF = DateTimeFormatter.ofPattern("dd.");
	    
	    String year = dueDate.format(yearF);
	    String month = dueDate.format(monthF);
	    String day = dueDate.format(dayF);
	    
		String strDueDate2 = dueDate.format(dateFormat2);
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
		System.out.println(year + " " + month + " " + day);
	    */  
		 
	}
	
}
