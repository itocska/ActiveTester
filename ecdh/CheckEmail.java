package ecdh;

public class CheckEmail {
	public static void main(String[] args) throws Throwable {
		
		Gmail.getMails(TestBase.personalUser, TestBase.testerPassword, TestBase.personalPassword, "href=\"(.*?)\">Addig is tekintsd meg");
		
	}
}
