package com.code.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
public boolean sendEmail(String subject, String message, String to) {
		
		boolean f=false;
		
		String from="-----";
		String host="smtp.gmail.com";
		
		//system properties
		Properties props=System.getProperties();
		System.out.println(props);
		
		//host set
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.auth", "true");
		
		//getting session object
		Session session=Session.getInstance(props, new Authenticator(){
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("-----","-----");
			}
		});
		
		session.setDebug(true);
		
		//compose message
		MimeMessage m=new MimeMessage(session);
		
		try {
			//from
			m.setFrom(from);
			
			//to
			m.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
			
			//subject
			m.setSubject(subject);
			
			//message
//			m.setText(message);
			m.setContent(message, "text/html" );
			
			//sending msg
			Transport.send(m);
			System.out.println("successfully send...");
			f=true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}

}
