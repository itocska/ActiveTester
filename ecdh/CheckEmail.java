package ecdh;

public class CheckEmail {
	public static void main(String[] args) throws Throwable {
		
		Gmail.getMails("vorosborisz@gmail.com", "vivaretina", "ECDH", "href=\"(.*?)\">Addig is tekintsd meg");
		
	}
}
