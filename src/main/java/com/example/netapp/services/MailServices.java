package com.example.netapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServices {

	@Autowired
	private JavaMailSender mailSender; 

	@Value("${spring.mail.username}")
	private static String sender_email;
	
	
	public void sendEMail(String emailTo,
						  String subject,
						  String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(emailTo);
		message.setFrom(sender_email);
		message.setSubject(subject);
		message.setText(body);
		mailSender.send(message);
		
		System.out.println("Sent an email to : " + emailTo);
	}
	
}
