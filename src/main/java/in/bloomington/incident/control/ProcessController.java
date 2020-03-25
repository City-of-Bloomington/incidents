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

    // @Value("${incident.ldap.host}")    
    // private String ldap_host = "";


    //login staff
    @GetMapping("/staff/{id}")
    public String staffIncident(@PathVariable("id") int id,
				 Model model,
				 HttpSession session
				 ) {
	Incident incident = null;
	User user = userService.findById(5); // need fix
	// User user = getUserFromSession(session);
	ActionLog actionLog = new ActionLog();
	List<Action> actions = null;
	try{
	    incident = incidentService.findById(id);
	    actionLog.setIncident(incident);
	    actionLog.setUser(user);
	    actions = getNextActions(incident);
	    model.addAttribute("incident", incident);
	    model.addAttribute("actionLog", actionLog);
	    model.addAttribute("actions", actions);
	}catch(Exception ex){
	    logger.error("Error no incident "+id+" not found "+ex);
	    addError("Invalid incident ID "+id);
	}
	if(hasMessages()){
	    model.addAttribute("messages", messages);
	}
	else if(hasErrors()){
	    model.addAttribute("errors", errors);
	}
	return "make_decision";

    }
    //login staff
    @GetMapping("/process/{id}")
    public String processIncident(@PathVariable("id") int id,
				 Model model,
				 HttpSession session
				 ) {
	Incident incident = null;
	// User user = userService.findById(5); // need fix
	User user2 = getUserFromSession(session);
	if(user2 == null ){
	    return "redirect:/login";
	}
	User user = userService.findById(user2.getId());
	if(!user.canProcess()){
	    addMessage("You do not have enough privileges");
	    addMessagesToSession(session);
	    return "redirect:/index";
	}

	ActionLog actionLog = new ActionLog();
	List<Action> actions = null;
	try{
	    incident = incidentService.findById(id);
	    actionLog.setIncident(incident);
	    actionLog.setUser(user);
	    actions = getNextActions(incident);
	    model.addAttribute("incident", incident);
	    model.addAttribute("actionLog", actionLog);
	    model.addAttribute("actions", actions);
	}catch(Exception ex){
	    logger.error("Error no incident "+id+" not found "+ex);
	    addError("Invalid incident ID "+id);
	}
	if(hasMessages()){
	    model.addAttribute("messages", messages);
	}
	else if(hasErrors()){
	    model.addAttribute("errors", errors);
	}
	return "process_decision";
    }    
    //login staff
    @PostMapping("/staff/decision")
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
	    return "redirect:/search/preApproved";
	}
	user = getUserFromSession(session);
	if(user != null && user.canApprove()){
	    actionLog.setDateNow();
	    Action action = actionLog.getAction();
	    actionLogService.save(actionLog);
	    String cfsNumber = actionLog.getCfsNumber();
	    if(cfsNumber != null){
		Incident incident = actionLog.getIncident();
		if(incident != null){
		    incident.setCfsNumber(cfsNumber);
		    incidentService.update(incident);
		}
		// we need to add another action log as processed
		// since cfsNumber is provided
		actionLog = new ActionLog();
		actionLog.setIncident(incident);
		// process action
		actionLog.setAction(actionService.findById(5)); 
		actionLog.setDateNow();
		actionLog.setUser(user);
		actionLogService.save(actionLog);	    
	    }
	    addMessage("Saved Successfully");				
	    model.addAttribute("messages", messages);
	    addMessagesToSession(session);
	    //
	    // check if the action is rejection
	    // redirect to rejection form
	    // if approved, send approve email
	    //
	    if(action != null){
		if(action.isApproved()){
		    // send approve email
		
		}
		else if(action.isRejected()){
		    // redirect to rejection email
		    
		}
	    }
	}
	else {
	    addMessage("You do not have enough privileges ");
	    addMessagesToSession(session);
	    return "redirect:/index";
	}
	return "redirect:/search/preApproved";	    
    }
    //login staff
    @PostMapping("/process/decision")
    public String processDecision(@Valid ActionLog action, 
				BindingResult result,
				Model model,
				HttpSession session
				) {
	if (result.hasErrors()) {
	    String error = Helper.extractErrors(result);
	    addError(error);
	    logger.error("Error saving action "+error);
	    return "redirect:/search/approved";
	}
	User user = getUserFromSession(session);
	// check user role
	if(user != null && user.canApprove()){
	    action.setDateNow();
	    action.setUser(user);
	    actionLogService.save(action);
	    String cfsNumber = action.getCfsNumber();
	    if(cfsNumber != null){
		Incident incident = action.getIncident();
		if(incident != null){
		    incident.setCfsNumber(cfsNumber);
		incidentService.update(incident);
		}
	    }
	    addMessage("Saved Successfully");				
	    model.addAttribute("messages", messages);
	    addMessagesToSession(session);
	    return "redirect:/search/approved";
	}
	else{
	    addMessage("You do not have enough privileges ");
	    addMessagesToSession(session);
	    return "redirect:/index";
	}
    }        
    
    // login users
    @GetMapping("/incidentView/{id}")
    public String viewIncident(@PathVariable("id") int id,
				 Model model,
				 HttpSession session
				 ) {
	Incident incident = null;
	User user = getUserFromSession(session);
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
	if(hasMessages()){
	    model.addAttribute("messages", messages);
	}
	else if(hasErrors()){
	    model.addAttribute("errors", errors);
	}
	return "incidentView";

    }    
    
    @GetMapping("/processAction")
    public String processAction(Model model) {
	//
	// here will send approve email with cfs #
        return "";
    }		
    private String sendApproveEmail(Incident incident){
	String ret = "";
	
	if(!incident.hasCfsNumber()){
	    ret = "incident has no CSF number";
	    return ret;
	}
	if(!incident.hasPersonList()){
	    ret = "incident has no person to send email to";
	    return ret;
	}
	List<Person> persons = incident.getPersons();
	Person person = persons.get(0); // we need one
	String email = person.getEmail();
	String subject = "Incident Reporting Approval";
	String from = "\"incident_reporting@bloomington.in.gov\",'-fwebmaster@bloomington.in.gov'";
	String message = "Dear "+person.getFullname()+
	    "\n\n Your report has been approved. "+
	    "The CFS Number of your report is "+incident.getCfsNumber()+
	    
	    " You can use this number in your future contacts with the Bloomington Police Department. \n\n"+
	    "Please do not reply to this email as this is an automated system.";
	//
	// send the email

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
	    // next workflow step is the last  workflow step + 1
	    int next_step = lastAction.getWorkflowStep()+1;
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
