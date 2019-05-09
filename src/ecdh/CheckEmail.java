package ecdh;

public class CheckEmail {
	public static void main(String[] args) throws Throwable {
		
		Gmail.getMails(TestBase.user1, "vivaretina", "ECDH", "href=\"(.*?)\">Addig is tekintsd meg");
		
	}
}
