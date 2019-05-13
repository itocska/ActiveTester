package ecdh;

import javax.mail.Flags;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Gmail {

	public static void main(String[] args) throws Exception {
	  //getMails(args);
	}
	
public static String getMails(String email, String password, String content, String linkPattern) throws Exception {
	    String registrationURL = null;
	
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        //props.setProperty("mail.store.protocol", "imaps");

        Thread.sleep(5000);
        
        Session session = Session.getDefaultInstance(props, null);
        Store store = session.getStore("imaps");
        store.connect("imap.gmail.com", email, password);


        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_WRITE);

        System.out.println("Total Message:" + folder.getMessageCount());
        System.out.println("Unread Message:"
                + folder.getUnreadMessageCount());
        
        
        boolean isMailFound = false;
        Message mailFromGod = null;
        
        SearchTerm term = new SubjectTerm(content);
        /*SearchTerm term = new SubjectTerm("Elfelejtett");
        if (args == null) {
        	term = new SubjectTerm("ECDH");
        }*/
        
        Message[] messages = folder.search(term);

        
        for (int i = 0; i < 5; i++) {
        	System.out.println("on search");
            messages = folder.search(term);
            //Wait for 10 seconds
            if (messages.length == 0) {
                Thread.sleep(2000);
                System.out.println(i + ". try");
            }
        }
        
        System.out.println("ML" + messages.length);

        for (Message mail : messages) {
            if (!mail.isSet(Flags.Flag.SEEN)) {
                mailFromGod = mail;
                System.out.println("Message Count is: "
                        + mailFromGod.getMessageNumber());
                isMailFound = true;
            }
        }

        //Test fails if no unread mail was found from God
        if (!isMailFound) {
            throw new Exception(
                    "Mail not found");
        
        //Read the content of mail and launch registration URL                
        } 
        else {
            String line;
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(mailFromGod
                            .getInputStream()));
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            System.out.println(buffer);

            Pattern p = Pattern.compile(linkPattern);
            /*Pattern p = Pattern.compile("href=\"(.*?)\">Jelszóváltás");
            if (args == null) {
              p = Pattern.compile("href=\"(.*?)\">Személyes fiók aktiválása");
            }*/
            
            Matcher m = p.matcher(buffer.toString());
            
            if (m.find()) {
                registrationURL = m.group(1); // this variable should contain the link URL
            }
            
            System.out.println(registrationURL);        
        }
		
		return registrationURL;
        
    }


}        