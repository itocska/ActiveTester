package ecdh;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import org.openqa.selenium.WebDriver;

public class Log {
	
	static Integer c = 0;
	public static boolean error;
	public static String testname;
	static boolean dateSet = false;
	static Integer testId;
	public static WebDriver driver;
	
	static String logString = "<test>";

	static void log(String string) throws IOException {
		
		if (!dateSet) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			logString += "<name>" + testname + "</name>";
			logString += "<time>" + dateFormat.format(date) + "</time>";
			dateSet = true;
			
			String dateString = dateFormat.format(date);
			testId = DbLog.newTest(testname, dateString);
			System.out.println(testId);
		}
		
		string = string.replace("<", "");
		string = string.replace(">", "");
		if (error) {
			logString += "<error>" + string + "</error>";
			DbLog.testEvent(testId, string, "error");
		}
		else {
			logString += "<testitem>" + string + "</testitem>";
			DbLog.testEvent(testId, string, "testevent");
		}
		
		System.out.println(string);
		
		if (error) {
		  close();
		}
		
	}

	public static void close() throws IOException {
		
		dateSet = false;
		
		 File screenshotFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		 FileUtils.copyFile(screenshotFile, new File("/www/test-center/ECDH/" +testname + "_screenshot.png"));
		  
		logString += "</test>";
		
		BufferedWriter bw = null;
		FileWriter fw = null;		
	
		File file = new File("/www/test-center/ECDH/test-" + testname + ".xml");
		
		System.out.println("close" + testname);
	
		if (!file.exists()) {
			file.createNewFile();
			System.out.println("File created.");
		}
	
		fw = new FileWriter(file.getAbsoluteFile(), true);
		bw = new BufferedWriter(fw);
	
		bw.write(logString);
		bw.newLine();
		
		System.out.println("log file saved.");
		
		bw.close();
		fw.close();
		
	}
}
