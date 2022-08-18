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
import in.bloomington.incident.service.UserService;
import in.bloomington.incident.service.IncidentService;

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
    IncidentService incidentService;   
    @Autowired
    ActionLogService actionLogService;
    @Autowired 
    private HttpSession session;		    
    @Value("${incident.email.sender}")
    private String sender;
    @Value("${server.servlet.context-path}")
    private String hostPath; // incidents in production
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
    
    @GetMapping("/incompleteOptions")
    public String incompleteOptions(Model model
				    ) {
	model.addAttribute("app_url",app_url);
	User user = findUserFromSession(session);
	if(user == null){
	    return "redirect:/login";
	}
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
    @GetMapping("/incomplete/resume/{id}/{dt}")
    public String resume(@PathVariable("id") int id,
			 @PathVariable("dt") String dt,
			 Model model
			 ) {
	model.addAttribute("app_url",app_url);
	Incident incident = incidentService.findById(id);
	if(incident != null && !dt.isEmpty()){
	    String receivedDt = incident.getReceivedNoSep();
	    if(!receivedDt.equals(dt)){
		addMessage("Invalid incident reference "+id);
		addMessagesAndErrorsToSession(session);
	    }
	    else if(incident.canBeChanged()){
		List<String> ids = null;
		int sid = 0;
		try{
		    ids = (List<String>) session.getAttribute("incident_ids");
		}catch(Exception ex){
		    System.err.println(ex);
		}
		if(ids != null && ids.size() > 0){
		    String str =  ids.get(ids.size() - 1);
		    if(str != null){
			try{
			    sid = Integer.parseInt(str);
			}catch(Exception ex){}
		    }
		}
		if(ids == null){
		    ids = new ArrayList<>();
		    ids.add(""+id);
		    session.setAttribute("incident_ids", ids);								
		}
		if(incident.isBusinessRelated()){
		    return "redirect:/businessIncident/"+id;
		}
		else{
		    return "redirect:/incident/"+id;
		}								
	    }
	    addMessage("No more changes can be made to this incident");
	    addMessagesAndErrorsToSession(session);
	    if(incident.isBusinessRelated()){
		return "redirect:/forBusiness";
	    }
	    return "redirect:/";
	}
	addMessage("invalid incident "+id);
	addMessagesAndErrorsToSession(session);
	return "redirect:/";
				
    }
    @GetMapping("/incomplete/inform/{id}")
    public String incompleteInform(@PathVariable("id") int id,
				   Model model,
				   HttpServletRequest req
				   ) {
	model.addAttribute("app_url",app_url);
	User user = findUserFromSession(session);
	if(user == null){
	    return "redirect:/login";
	}
	Incident incident = incidentService.findById(id);
	if(incident != null){
	    if(incident.canBeChanged()){
		String email = incident.getEmail();
		String url = prepareUrl(req);								
		String message = sendResumeEmail(incident, url);
		if(message.isEmpty()){
		    ActionLog actionLog = new ActionLog();
		    actionLog.setIncident(incident);
		    Action action = actionService.findById(1); // emailed
		    actionLog.setAction(action);
		    actionLog.setDateNow();
		    actionLog.setUser(user);
		    actionLogService.save(actionLog);
		}
	    }
	}
	return "redirect:/search/incomplete";
				
    }
    // not used for now
    @GetMapping("/incompleteAction")
    public String incompleteAction(@RequestParam String action,
				   Model model,
				   HttpServletRequest req
				   ){
	boolean schedule_flag = false, run_flag=false;
	model.addAttribute("app_url",app_url);
	User user = findUserFromSession(session);
	if(user == null){
	    return "redirect:/login";
	}	
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
		String url = prepareUrl(req);
		String back = sendResumeEmails(all, url);
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
        return "staff/incompleteOptions";
    }
    private String prepareUrl(HttpServletRequest req){
	String host_forward = req.getHeader("X-Forwarded-Host");
	String host = req.getServerName();
	String uri = req.getRequestURI();
	String scheme = req.getScheme();
	int port = req.getServerPort();
	String url = scheme+"://";
	if(host_forward != null){
	    url += host_forward;
	}
	else{
	    url += host;
	}
	if(port == 8080){ // for localhost
	    url += ":"+port;
	}	    
	if(hostPath != null)
	    url += hostPath;
	url += "/incomplete/resume/";
	return url;
    }
    private String sendResumeEmails(List<Incident> all, String url){
	String messages = "";
	if(all != null && all.size() > 0){
	    for(Incident one: all){
		messages += sendResumeEmail(one, url);
	    }
	}
	return messages;
    }
    private String sendResumeEmail(Incident one, String url){
	String message = "";
	if(one != null){
	    String subject = "Incident reporting submission request";
	    if(one.hasEmail()){
		String body = "We noticed that you haven't completed your report. Please click <a href='"+url+one.getId()+"/"+one.getReceivedNoSep()+"'>here</a> to resume and submit your report. If not, your report will not be seen or processed by a representative of the Bloomington Police Department.<br />\n\n Thank you<br />\n\n";
		body += "Please do not reply to this email as this is an automated system.";
		body += "<br />\n\n";
		body += "Bloomington Police Department (BPD)<br />\n";
		body += "220 E 3rd St, Bloomington, IN 47401<br />\n";
		body += "(812) 339-4477<br />\n";
		body += "https://bloomington.in.gov/police<br />";
		body += "\n";
								
		String toEmail = one.getEmail();
		EmailHelper emailHelper = new EmailHelper(mailSender, sender, toEmail, subject, body);
		message = emailHelper.send();
		if(!message.isEmpty()){
		    addError(message);
		    logger.error(message);
		}
	    }
	    else{
		message = "No email address available in the incident";
	    }
	}
	return message;
    }		
		
}
