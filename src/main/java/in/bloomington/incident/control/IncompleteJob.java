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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Value;
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
import in.bloomington.incident.utils.EmailHelper;

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
	    String url = "https//"+host_path;
	    if(host_path.isEmpty()){
		url = "http://localhost:8080";
	    }
	    for(Incident incident:all){
		if(incident.hasEmail()){
		    String toEmail = incident.getEmail();
		    if(!toEmail.startsWith("blah@")){
			String body = "We noticed that you haven't completed your report. Please click <a href='"+url+"/incident/"+incident.getId()+"'>here</a> to finish and submit your report. If not, your report will not be seen or processed by a representative of the Bloomington Police Department.";

			EmailHelper emailHelper = new EmailHelper(mailSender,sender, toEmail, subject, body);
			String back = emailHelper.send();
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
	}
	else{
	    System.err.println("Incomplete incidents: No records found, no email sent");
	}
    }

}






















































