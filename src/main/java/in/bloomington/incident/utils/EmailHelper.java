package in.bloomington.incident.utils;
import java.util.*;
import java.io.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.SimpleMailMessage;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

import in.bloomington.incident.model.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EmailHelper{

    final static long serialVersionUID = 580L;
    static Logger logger = LoggerFactory.getLogger(EmailHelper.class);
    JavaMailSender mailSender;
    String fromEmail;
    String toEmail;
    String subject;
    String body;
    public EmailHelper(JavaMailSender mailSender,
		String fromEmail,
		String toEmail,
		String subject,
		String body){
	setMailSender(mailSender);
	setFromEmail(fromEmail);
	setToEmail(toEmail);
	setSubject(subject);
	setBody(body);
    }
    public EmailHelper(JavaMailSender mailSender,
		Email email){
	setMailSender(mailSender);
	setEmail(email);
    }    
    
    void setMailSender(JavaMailSender mailSender){
	if(mailSender != null)
	    this.mailSender = mailSender;
    }
    void setFromEmail(String fromEmail){
	if(fromEmail != null)
	    this.fromEmail = fromEmail;
    }
    void setToEmail(String toEmail){
	if(toEmail != null)
	    this.toEmail = toEmail;
    }
    void setSubject(String subject){
	if(subject != null)
	    this.subject = subject;
    }
    void setBody(String body){
	if(body != null)
	    this.body = body;
    }
    void setEmail(Email email){
	if(email != null){
	    setFromEmail(email.getSender());
	    setToEmail(email.getReceiver());
	    setSubject(email.getSubject());
	    setBody(email.getMessage());
	}
    }
    private String checkValidity(){
	String back = "";
	if(fromEmail == null || fromEmail.isEmpty()){
	    back = "from email is not set";
	}
	if(toEmail == null || toEmail.isEmpty()){
	    if(!back.isEmpty()) back += ", ";
	    back += "to email is not set";
	}
	if(subject == null || subject.isEmpty()){
	    if(!back.isEmpty()) back += ", ";
	    back += "subject is not set";
	}
	if(body == null || body.isEmpty()){
	    if(!back.isEmpty()) back += ", ";
	    back += "email message is not set";
	}
	if(mailSender == null){
	    if(!back.isEmpty()) back += ", ";
	    back += "mail sender object not set";
	}
	return back;
    }
    public String send(){
	String back = "";
	back = checkValidity();
	if(!back.isEmpty()){
	    return back;
	}
        try {
            logger.info("Sending Email to {}", toEmail);
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper messageHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.toString());
            messageHelper.setSubject(subject);
            messageHelper.setText(body, true);
            messageHelper.setFrom(fromEmail);
            messageHelper.setTo(toEmail);
	    System.err.println(" Sending email by mailSender");
            mailSender.send(message);
	    System.err.println(" Email sent successfully");	    
        } catch (MessagingException ex) {
            logger.error("Failed to send email to {}", toEmail);
	    back = "Failed to send email "+toEmail;
        }
	return back;
     } 

}
