package in.bloomington.incident.control;
/**
 * @copyright Copyright (C) 2014-2016 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 */

import java.util.*;
import java.sql.*;
import java.io.*;
import java.text.*;
import javax.sql.*;
import org.quartz.*;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Value;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.Action;
import in.bloomington.incident.model.ActionLog;
import in.bloomington.incident.model.IncidentIncomplete;
import in.bloomington.incident.service.IncidentIncompleteService;
import in.bloomington.incident.service.ActionService;
import in.bloomington.incident.service.ActionLogService;


@Component
public class IncompleteJob extends QuartzJobBean{

    boolean debug = true;
    static final long serialVersionUID = 55L;		
    static Logger logger = LoggerFactory.getLogger(IncompleteJob.class);
    String subject = "Incident reporting submission request";
    String fromEmail = "incident_reporting";
    //    @Autowired
    //    private JavaMailSender mailSender;

    @Autowired
    private JavaMailSender mailSender;
    
    // @Autowired
    // private MailProperties mailProperties;

    @Autowired
    IncidentIncompleteService incompleteService;

    @Autowired
    ActionService actionService;
    
    @Autowired
    ActionLogService actionLogService;
    
    @Autowired
    private Environment env;
    @Value("${server.servlet.context-path}")
    private String host_path;
    @Value("${incident.email.sender}")
    private String sender;
    
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Executing Job ");
	checkRecordsAndSendEmail();
	
    }
    private void checkRecordsAndSendEmail(){
	List<Incident> all = null;
	List<IncidentIncomplete> plist = incompleteService.getAll();
	if(plist != null){
	    all = new ArrayList<>();
	    for(IncidentIncomplete one:plist){
		Incident incident = one.getIncident();
		if(incident != null)
		    all.add(incident);
	    }
	}
	if(all != null && all.size() > 0){
	    String url = host_path;
	    if(host_path.isEmpty()){
		url = "localhost:8080";
	    }
	    for(Incident incident:all){
		if(incident.hasEmail()){
		    String body = "We noticed that you haven't completed your report. Please click <a href='"+url+"/incident/"+incident.getId()+"'>here</a> to finish and submit your report. If not, your report will not be seen or processed by a representative of the Bloomington Police Department.";
		    String toEmail = incident.getEmail();
		    String back = sendMail(sender, toEmail, subject, body);
		    if(back.isEmpty()){
			// success
			// action log
			ActionLog actionLog = new ActionLog();
			actionLog.setIncident(incident);
			Action action = actionService.findById(1); // emailed
			actionLog.setAction(action);
			actionLog.setDateNow();
			actionLogService.save(actionLog);			
		    }
		    else{
			System.err.println(" error sending email "+back);
			
			// failure
			// add email log
			
		    }
		}
	    }
	}
	else{
	    System.err.println("Incomplete incidents: No records found, no email sent");
	}
    }
    /**
    private String sendMail(String fromEmail, String toEmail, String subject, String body) {
	String back = "";
        try {
            logger.info("Sending Email to {}", toEmail);
            // MimeMessage message = mailSender.createMimeMessage();
	    SimpleMailMessage message = new SimpleMailMessage(); 
            
            message.setSubject(subject);
            message.setText(body);
            message.setFrom(fromEmail);
            message.setTo(toEmail);
	    System.err.println(" Sending email by mailSender");
            mailSender.send(message);
	    System.err.println(" Email sent successfully");	    
        } catch (Exception ex) {
            logger.error("Failed to send email to ", toEmail);
	    back = "Failed to send email "+toEmail;
        }
	return back;
     }
    */
    private String sendMail(String fromEmail, String toEmail, String subject, String body) {
	String back = "";
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
            logger.error("Failed to send email to ", toEmail);
	    back = "Failed to send email "+toEmail;
        }
	return back;
     }    

}






















































