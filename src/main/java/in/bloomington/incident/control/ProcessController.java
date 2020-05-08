package in.bloomington.incident.control;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Value;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import in.bloomington.incident.service.IncidentService;
import in.bloomington.incident.service.IncidentTypeService;
import in.bloomington.incident.service.ActionService;
import in.bloomington.incident.service.ActionLogService;
import in.bloomington.incident.service.RequestService;
import in.bloomington.incident.service.UserService;

import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.IncidentType;
import in.bloomington.incident.model.Action;
import in.bloomington.incident.model.ActionLog;
import in.bloomington.incident.model.Person;
import in.bloomington.incident.model.Request;
import in.bloomington.incident.model.User;
import in.bloomington.incident.model.Email;
import in.bloomington.incident.utils.Helper;
import in.bloomington.incident.utils.EmailHandle;

@Controller
public class ProcessController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(ProcessController.class);	
    @Autowired
    IncidentService incidentService;		
    @Autowired
    IncidentTypeService incidentTypeService;
    @Autowired
    ActionService actionService;
    @Autowired
    ActionLogService actionLogService;
    @Autowired
    RequestService requestService;
    // remove later
    @Autowired
    UserService userService;    
    @Autowired
    private Environment env;
    @Value("${incident.email.host}")
    private String email_host;
    @Value("${incident.email.sender}")
    private String email_sender;

    //
    @GetMapping("/staff/{id}")
    public String staffIncident(@PathVariable("id") int id,
				 Model model,
				 HttpSession session
				 ) {
	Incident incident = null;
	User user = findUserFromSession(session);
	if(user == null ){
	    return "redirect:/login";
	}	
	ActionLog actionLog = new ActionLog();
	List<Action> actions = null;
	try{
	    incident = incidentService.findById(id);
	    actionLog.setIncident(incident);
	    actions = getNextActions(incident);
	    model.addAttribute("incident", incident);
	    model.addAttribute("actionLog", actionLog);
	    model.addAttribute("actions", actions);
	}catch(Exception ex){
	    logger.error("Error no incident "+id+" not found "+ex);
	    addError("Invalid incident ID "+id);
	}
	handleErrorsAndMessages(model);
	return "staff/make_decision";

    }
    //
    @GetMapping("/process/{id}")
    public String processIncident(@PathVariable("id") int id,
				 Model model,
				 HttpSession session
				 ) {
	Incident incident = null;
	User user = findUserFromSession(session);
	if(user == null ){
	    return "redirect:/login";
	}
	if(!user.canProcess()){
	    addMessage("You do not have enough privileges");
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/staff/staff_intro";
	}

	ActionLog actionLog = new ActionLog();
	List<Action> actions = null;
	try{
	    incident = incidentService.findById(id);
	    actionLog.setIncident(incident);
	    actions = getNextActions(incident);
	    model.addAttribute("incident", incident);
	    model.addAttribute("actionLog", actionLog);
	    model.addAttribute("actions", actions);
	    
	}catch(Exception ex){
	    logger.error("Error no incident "+id+" not found "+ex);
	    addError("Invalid incident ID "+id);
	}
	getMessagesAndErrorsFromSession(session, model);
	return "staff/process_decision";
    }    
    //
    @PostMapping("/staff/decision") // approve or reject
    public String staffDecision(@Valid ActionLog actionLog, 
				BindingResult result,
				Model model,
				HttpSession session
				) {
	User user = null;
	if (result.hasErrors()) {
	    String error = Helper.extractErrors(result);
	    addError(error);
	    logger.error("Error saving action "+error);
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/search/confirmed";
	}
	user = findUserFromSession(session);
	if(user == null ){
	    return "redirect:/login";
	}	
	if(user.canApprove()){
	    actionLog.setUser(user);
	    actionLog.setDateNow();
	    Action action = actionLog.getAction();
	    actionLogService.save(actionLog);
	    String caseNumber = actionLog.getCaseNumber();
	    Incident incident = actionLog.getIncident();	    
	    if(caseNumber != null && !caseNumber.isEmpty()){
		if(incident != null){
		    incident.setCaseNumber(caseNumber);
		    incidentService.update(incident);
		}
		// we need to add another action log as processed
		// since caseNumber is provided
		actionLog = new ActionLog();
		actionLog.setIncident(incident);
		// process action
		actionLog.setAction(actionService.findById(6)); 
		actionLog.setDateNow();
		actionLog.setUser(user);
		actionLogService.save(actionLog);	    
	    }
	    addMessage("Saved Successfully");				
	    model.addAttribute("messages", messages);
	    //
	    // check if the action is rejection
	    // redirect to rejection form
	    // if approved, send approve email
	    //
	    if(action != null){
		if(action.isApproved() && incident.hasCaseNumber()){
		    Email email = new Email();
		    email.populateEmail(incident, "approve");
		    sendApproveEmail(email, user);
		    addMessage("Email sent successfully ");
		    // handleErrorsAndMessages(model);
		    getMessagesAndErrorsFromSession(session, model);
		    return "staff/staff_intro";
		}
		else if(action.isRejected()){
		    addMessagesAndErrorsToSession(session);
		    return "redirect:/rejectForm/"+incident.getId();
		}
	    }
	}
	else {
	    addMessage("You do not have enough privileges ");
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/staff/staff_intro";
	}
	addMessagesAndErrorsToSession(session);
	return "redirect:/search/confirmed";	    
    }
    //reject email form
    @GetMapping("/rejectForm/{id}")
    public String rejectForm(@PathVariable("id") int id,
			     Model model,
			     HttpSession session
			     ) {
	Incident incident = incidentService.findById(id);
	User user = null;
	user = findUserFromSession(session);
	if(user == null){
	    return "redirect:login";
	}
	Email email = new Email();
	email.populateEmail(incident, "reject");
	model.addAttribute("email", email);
	getMessagesAndErrorsFromSession(session, model);
	return "staff/rejectForm";
    }
    //login staff
    @PostMapping("/rejectEmail")
    public String sendRejectEmail(Email email, 
				  BindingResult result,
				  Model model,
				  HttpSession session
				  ) {
	if (result.hasErrors()) {
	    String error = Helper.extractErrors(result);
	    addError(error);
	    logger.error("Error reject email form "+error);
	}
	User user = findUserFromSession(session);
	if(user == null){
	    return "redirect:login";
	}
	else{
	    email.setUser_id(user.getId());
	}
	email.setSender(email_sender);// +",'-fwebmaster@bloomington.in.gov'");
	//
	// send the email
	//
	EmailHandle emailer = new EmailHandle(email, email_host);
	String back = emailer.send();
	//
	// we may need add email to email logs
	//
	// back to main staff page
	return "staff/staff_intro";
	
    }
    // process of adding case number to complete incident actions
    @PostMapping("/process/final")
    public String processDecision(@Valid ActionLog actionLog, 
				BindingResult result,
				Model model,
				HttpSession session
				) {
	if (result.hasErrors()) {
	    String error = extractErrors(result);
	    addError(error);
	    logger.error("Error saving action "+error);
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/search/approved";
	}
	User user = findUserFromSession(session);
	if(user == null){
	    return "redirect:/login";
	}
	if(!actionLog.hasCaseNumber()){
	    addError("Case number is required");
	    addMessagesAndErrorsToSession(session);
	    Incident incident = actionLog.getIncident();
	    return "redirect:/process/"+incident.getId();
	}
	else if(user.canProcess()){
	    actionLog.setDateNow();
	    actionLog.setUser(user);
	    actionLogService.save(actionLog);
	    String caseNumber = actionLog.getCaseNumber();
	    if(caseNumber != null && !caseNumber.isEmpty()){
		Incident incident = actionLog.getIncident();
		if(incident != null){
		    incident.setCaseNumber(caseNumber);
		    incidentService.update(incident);
		}
		System.err.println(" **** sending approve email ");
		Email email = new Email();
		email.populateEmail(incident, "approve");
		String back = sendApproveEmail(email, user);
		if(back.isEmpty())
		    addMessage("Email sent successfully ");
		else
		    addError(back);
	    }
	    addMessage("Saved Successfully");				
	    model.addAttribute("messages", messages);
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/search/approved";
	}
	else{
	    addMessage("You do not have enough privileges ");
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/staff/staff_intro";
	}
    }        
    
    // 
    @GetMapping("/incidentView/{id}")
    public String viewIncident(@PathVariable("id") int id,
				 Model model,
				 HttpSession session
				 ) {
	Incident incident = null;
	User user = findUserFromSession(session);
	if(user == null){
	    return "redirect:/login";
	}
	try{
	    incident = incidentService.findById(id);
	    model.addAttribute("incident", incident);
	}catch(Exception ex){
	    logger.error("Error no incident "+id+" not found "+ex);
	    addError("Invalid incident ID "+id);
	}
	getMessagesAndErrorsFromSession(session, model);
	return "incidentView";

    }    
    private User findUserFromSession(HttpSession session){
	User user = null;
    	User user2 = getUserFromSession(session);
	if(user2 != null){
	    user = userService.findById(user2.getId());
	}
	return user;
    }
    private String sendApproveEmail(Email email,
				    User user
				    ){
	String ret = "";
	EmailHandle emailer = new EmailHandle(email, email_host);
	email.setSender(email_sender);
	ret = emailer.send();
	return ret;
    }

    /**
     * find next action in workflow steps compare to the last
     * action (if any)
     */
    public List<Action> getNextActions(Incident incident){
	List<Action> nextActions = null;
	if(incident.hasNextAction()){
	    Action lastAction = incident.getLastAction();
	    int next_step = lastAction.getNextstep();// lastAction.getWorkflowStep()+1;
	    List<Action> actions = actionService.getAll();
	    nextActions = new ArrayList<>();
	    for(Action action:actions){
		if(action.getWorkflowStep() == next_step){
		    nextActions.add(action);
		}
	    }
	}
	return nextActions;
    }
		
}
