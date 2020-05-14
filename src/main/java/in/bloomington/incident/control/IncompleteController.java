package in.bloomington.incident.control;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */
import java.util.UUID;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.mail.javamail.JavaMailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import in.bloomington.incident.model.User;
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.IncidentIncomplete;
import in.bloomington.incident.model.Action;
import in.bloomington.incident.model.ActionLog;
import in.bloomington.incident.control.IncompleteJob;
import in.bloomington.incident.utils.EmailHelper;
import in.bloomington.incident.service.IncidentIncompleteService;
import in.bloomington.incident.service.ActionService;
import in.bloomington.incident.service.ActionLogService;

@Controller
public class IncompleteController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(IncompleteController.class);
		
    @Autowired
    private Scheduler scheduler;
    @Autowired
    IncidentIncompleteService incompleteService;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    ActionService actionService;
    @Autowired
    ActionLogService actionLogService;
    
    @Value("${incident.email.sender}")
    private String sender;
    @Value("${server.servlet.context-path}")
    private String host_path;
    String jobName = "incomplete_job";
    String groupName = "incomplete_group";


    private JobDetail buildJobDetail(){
	return JobBuilder.newJob(IncompleteJob.class)
	    .withIdentity(jobName, groupName)
	    .withDescription("Send Email Job")
	    //.usingJobData(jobDataMap)
	    .storeDurably()
	    .build();
    }
    private Trigger buildJobTrigger(JobDetail jobDetail) {
	java.util.Calendar cal = Calendar.getInstance();
	cal.set(Calendar.HOUR_OF_DAY, 7);//to run at 7am of the day
	cal.set(Calendar.MINUTE, 0);
	Date startTime = cal.getTime();
	System.err.println(" scheduling "+startTime);
	Trigger myTrigger = null;
	try{
	    myTrigger = TriggerBuilder.newTrigger()
		.withIdentity(jobName, groupName)
		.startAt(startTime)
		.withSchedule(
			      SimpleScheduleBuilder.simpleSchedule()
			      // .withIntervalInMinutes(3)
			      .withIntervalInHours(24) // every day
			      .repeatForever()
			      // .withRepeatCount(2) 
			      // .withMisfireHandlingInstructionFireNow())
			      .withMisfireHandlingInstructionIgnoreMisfires())
		// .endAt(endDate)
		.build();
	}catch(Exception ex){
	    System.err.println(" "+ex);
	}
	return myTrigger;
    }
    private String startSchedule(){
	String back = "";
	try{
	    JobDetail jobDetail = buildJobDetail();
	    Trigger trigger = buildJobTrigger(jobDetail);
	    scheduler.scheduleJob(jobDetail, trigger);
	}
	catch(Exception ex){
	    back += ex;
	}
	return back;
    }
    
    @GetMapping("/staff/incompleteOptions")
    public String incompleteOptions(Model model,
				    HttpSession session
				    ) {
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
	    // 
	    model.addAttribute("incidents", all);
	}
	else{
	    addMessage("No incident found");
	    model.addAttribute("messages", messages);
	}
        return "staff/incompleteOptions";
    }    
    @GetMapping("/staff/incompleteAction")
    public String incompleteAction(@RequestParam String action,
				   Model model,
				   HttpSession session
			      ){
	boolean schedule_flag = false, run_flag=false;
	if(action !=null){
	    if(action.equals("Schedule")){
		schedule_flag = true;
	    }
	    else if(action.equals("Run")){
		run_flag = true;
	    }
	}
	if(run_flag){
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
		String back = sendSubmissionEmails(all);
		if(back.isEmpty()){
		    addMessage("Emails sent successfully");
		}
	    }
	}
	else if(schedule_flag){
	    String back = startSchedule();
	    if(!back.isEmpty()){
		addError(back);
	    }
	}
        return "redirect:/search/incomplete";
    }
    private String sendSubmissionEmails(List<Incident> all){
	String messages = "";
	if(all != null && all.size() > 0){
	    String subject = "Incident reporting submission request";
	    String url = "https://"+host_path;
	    if(host_path.isEmpty()){
		url = "http://localhost:8080";
	    }
	    Incident one = all.get(0);
	    if(one.hasEmail()){
		String body = "We noticed that you haven't completed your report. Please click <a href='"+url+"/incident/"+one.getId()+"'>here</a> to finish and submit your report. If not, your report will not be seen or processed by a representative of the Bloomington Police Department.";
		String toEmail = one.getEmail();
		EmailHelper emailHelper = new EmailHelper(mailSender, sender, toEmail, subject, body);
		String back = emailHelper.send();
		if(back.isEmpty()){
		    // success
		    // action log
		    ActionLog actionLog = new ActionLog();
		    actionLog.setIncident(one);
		    Action action = actionService.findById(1); // emailed
		    actionLog.setAction(action);
		    actionLog.setDateNow();
		    actionLogService.save(actionLog);
		}
		else{
		    addError(back);
		    messages += back;
		    logger.error(back);
		    // failure
		    // add email log
		}
	    }
	}
	return messages;
    }
		
}
