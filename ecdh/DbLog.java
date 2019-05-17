package ecdh;

import java.sql.*;

public class DbLog {
	public static void main(String[] args) {
		
	}

	public static Integer newTest(String testname, String date) {
		try
	    {
		  Class.forName("com.mysql.jdbc.Driver");
	      Connection conn = DriverManager.getConnection(TestBase.myUrl, TestBase.dbUser, TestBase.dbPass);

	      String query = " insert into java_log_test (testname, date)"
	        + " values (?, ?)";
	      
	      PreparedStatement preparedStmt = conn.prepareStatement(query);
	      preparedStmt.setString (1, testname);
	      preparedStmt.setString (2, date);
	      
	      preparedStmt.execute();
	      ResultSet rs=preparedStmt.getGeneratedKeys();
          
	      Integer id = 0;
          if(rs.next()){
              id=rs.getInt(1);
          }
          
	      conn.close();
	      
	      return id;
	    }
	    catch (Exception e)
	    {
	      System.err.println("Got an exception!");
	      System.err.println(e.getMessage());
	    }
		return null;
		
		
	}

	public static void testEvent(Integer testId, String event, String type) {
		try
	    {/*
		  Class.forName("com.mysql.jdbc.Driver");
	      String myUrl = "jdbc:mysql://localhost/testcenter";
	      //Connection conn = DriverManager.getConnection(myUrl, "root", "");

	      String query = " insert into java_log_test_row (testId, event, type)"
	        + " values (?, ?, ?)";
	      
	      PreparedStatement preparedStmt = conn.prepareStatement(query);
	      preparedStmt.setLong (1, testId);
	      preparedStmt.setString (2, event);
	      preparedStmt.setString (3, type);
	      
	      preparedStmt.execute();
	     */
	    }
	    catch (Exception e)
	    {
	      System.err.println("Got an exception!");
	      System.err.println(e.getMessage());
	    }
		
	}
}
