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
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.mail.javamail.JavaMailSender;
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
import in.bloomington.incident.model.Business;
import in.bloomington.incident.utils.Helper;
import in.bloomington.incident.utils.EmailHelper;

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
    @Autowired
    private JavaMailSender mailSender;    

    @Autowired
    UserService userService;    
    @Autowired
    private Environment env;
    @Value("${incident.email.host}")
    private String email_host;
    @Value("${incident.email.sender}")
    private String email_sender;
    @Autowired 
    private HttpSession session;

    //
    @GetMapping("/staff")
    public String staff(HttpSession session,
			Model model) {
	model.addAttribute("app_url", app_url);
	User user = findUserFromSession(session);
	if(user == null ){
	    return "redirect:/login";
	}
	getMessagesAndErrorsFromSession(session, model);
	
	return "staff/staff_intro";
    }
    @GetMapping("/staff/{id}")
    public String staffIncident(@PathVariable("id") int id,
				Model model
				) {
	Incident incident = null;
	model.addAttribute("app_url", app_url);
	User user = findUserFromSession(session);
	if(user == null ){
	    return "redirect:/login";
	}	
	//
	// adding the discard action
	Action discardAction = actionService.findById(7);// discard action = 7
	List<Action> actions = null;
	try{
	    incident = incidentService.findById(id);
	    actions = getNextActions(incident);
	    if(actions != null && actions.size() > 0){
		if(discardAction != null)
		    actions.add(discardAction);
		ActionLog actionLog = new ActionLog();
		actionLog.setIncident(incident);								
		model.addAttribute("actionLog", actionLog);
		model.addAttribute("actions", actions);
		if(incident.isBusinessRelated()){
		    Business business = incident.getBusiness();
		    if(business != null){
			model.addAttribute("business", business);
		    }
		}
	    }						
	    model.addAttribute("incident", incident);
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
				  Model model
				  ) {
	Incident incident = null;
	model.addAttribute("app_url", app_url);
	User user = findUserFromSession(session);
	if(user == null ){
	    return "redirect:/login";
	}
	if(!user.canProcess()){
	    addMessage("You do not have enough privileges");
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/staff/staff_intro";
	}
	// adding the discard action
	Action discardAction = actionService.findById(7);// discard action = 7
	//
	ActionLog actionLog = new ActionLog();
	List<Action> actions = null;
	try{
	    incident = incidentService.findById(id);
	    actionLog.setIncident(incident);
	    actionLog.setUser(user);
	    actions = getNextActions(incident);
	    if(user.canApprove()){
		actions.add(discardAction);
	    }
	    model.addAttribute("incident", incident);
	    model.addAttribute("actionLog", actionLog);
	    model.addAttribute("actions", actions);
	    if(incident.isBusinessRelated()){
		Business business = incident.getBusiness();
		if(business != null){
		    model.addAttribute("business", business);
		}
	    }
	}catch(Exception ex){
	    logger.error("Error no incident "+id+" not found "+ex);
	    addError("Invalid incident ID "+id);
	}
	getMessagesAndErrorsFromSession(session, model);
	return "staff/process_decision";
    }
    @GetMapping("/discard/{id}")
    public String discardIncident(@PathVariable("id") int id,
				  Model model
				  ) {
	Incident incident = null;
	model.addAttribute("app_url", app_url);
	User user = findUserFromSession(session);
	if(user == null ){
	    return "redirect:/login";
	}
	if(!user.canApprove()){
	    addMessage("You do not have enough privileges");
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/staff/staff_intro";
	}
	incident = incidentService.findById(id);
	/**
	   if(!incident.canBeChanged()){ // if can not be changed
	   addMessage("This incident can not be discarded");
	   addMessagesAndErrorsToSession(session);
	   return "redirect:/staff/staff_intro";
	   }
	*/
	ActionLog actionLog = new ActionLog();
	List<Action> actions = new ArrayList<>();
	try{

	    actionLog.setIncident(incident);
	    actionLog.setUser(user);
	    Action action = actionService.findById(7); // discard action
	    if(action != null) actions.add(action);
	    model.addAttribute("incident", incident);
	    model.addAttribute("actionLog", actionLog);
	    model.addAttribute("actions", actions);
	    if(incident.isBusinessRelated()){
		Business business = incident.getBusiness();
		if(business != null){
		    model.addAttribute("business", business);
		}
	    }
	}catch(Exception ex){
	    logger.error("Error no incident "+id+" not found "+ex);
	    addError("Invalid incident ID "+id);
	}
	getMessagesAndErrorsFromSession(session, model);
	return "staff/discard_decision";
    }
		
    //
    @PostMapping("/staff/decision") // approve or reject
    public String staffDecision(@Valid ActionLog actionLog, 
				BindingResult result,
				Model model
				) {
	User user = null;
	model.addAttribute("app_url", app_url);
	if (result.hasErrors()) {
	    String error = Helper.extractErrors(result);
	    addError(error);
	    logger.error("Error saving action "+error);
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/search/received";
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
		else{
		    System.err.println(" incident is null ");
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
		    sendApproveEmail(email);
		    addMessage("Email sent successfully ");
		    getMessagesAndErrorsFromSession(session, model);
		    return "redirect:/search/confirmed";
		}
		else if(action.isRejected()){
		    addMessagesAndErrorsToSession(session);
		    return "redirect:/rejectForm/"+incident.getId();
		}
	    }
	}
	else {
	    addMessage("You do not have approve or reject role ");
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/staff";
	}
	addMessagesAndErrorsToSession(session);
	return "redirect:/search/confirmed";	    
    }
    //cancel action
    @GetMapping("/cancelAction/{id}")
    public String cancelAction(@PathVariable("id") int id,
			       Model model
			       ) {
	model.addAttribute("app_url", app_url);
	User user = findUserFromSession(session);
	if(user == null ){
	    return "redirect:/login";
	}
	Incident incident = null;
	ActionLog actionLog = actionLogService.findById(id);
	incident = actionLog.getIncident();				
	if(user.canApprove()){
	    actionLog.setId(id);
	    actionLog.setCancelled(true);
	    actionLog.setCancelledByUser(user);
	    actionLogService.update(actionLog);
	    if(incident != null){
		addMessage("Cancelled successfully");
	    }
	    addMessagesAndErrorsToSession(session);
	    incident.setCaseNumber(null);
	    incidentService.update(incident);
	    // check if there were actions after the cancelled one
	    // if so we need to cancel as well
	    List<ActionLog> logs = incident.getActionLogs();
	    List<ActionLog> tmpLogs = null;
	    if(logs != null && logs.size() > 1){ 
		tmpLogs = logs.stream().
		    filter(o1->o1.getId() > id).
		    collect(Collectors.toList());
	    }
	    if(tmpLogs != null && tmpLogs.size() > 0){
		for(ActionLog log:tmpLogs){
		    log.setCancelled(true);
		    log.setCancelledByUser(user);
		    actionLogService.update(log);
		}
	    }
	    return "redirect:/staff/"+incident.getId();						
	}
	addMessage("You can not cancell incident");
	addMessagesAndErrorsToSession(session);								
	return "redirect:/staff";		
    }
    //reject email form
    @GetMapping("/rejectForm/{id}")
    public String rejectForm(@PathVariable("id") int id,
			     Model model
			     ) {
	model.addAttribute("app_url", app_url);
	Incident incident = incidentService.findById(id);
	User user = null;
	user = findUserFromSession(session);
	if(user == null){
	    return "redirect:/login";
	}
	if(user.canApprove()){				
	    Email email = new Email();
	    email.populateEmail(incident, "reject");
	    model.addAttribute("email", email);
	    getMessagesAndErrorsFromSession(session, model);
	    return "staff/rejectForm";
	}
	else{
	    addMessage("You do not have approve or reject role ");
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/staff";	   
	}
    }
		
    //login staff
    @PostMapping("/rejectEmail")
    public String sendRejectEmail(Email email, 
				  BindingResult result,
				  Model model
				  ) {
	model.addAttribute("app_url", app_url);
	if (result.hasErrors()) {
	    String error = Helper.extractErrors(result);
	    addError(error);
	    logger.error("Error reject email form "+error);
	}
	User user = findUserFromSession(session);
	if(user == null){
	    return "redirect:/login";
	}
	if(user.canApprove()){
	    email.setSender(email_sender);
	    String message = email.getMessage();
	    message += "<br />";
	    message += "Please do not reply to this email as this is an automated system.<br />\n\n";
	    email.setMessage(message);
	    //
	    // send the email
	    //
	    EmailHelper emailer = new EmailHelper(mailSender, email);
	    String back = emailer.send();
	    addMessage("Email Sent Successfully");
	    addMessagesAndErrorsToSession(session);
	    //
	    // we may need add email to email logs
	    //
	    // back to main staff page
	    return "redirect:/search/confirmed";
	}
	else{
	    addMessage("You do not have approve or reject role ");
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/staff";
	}	
    }
    // process of adding case number to complete incident actions
    @PostMapping("/process/final")
    public String processDecision(@Valid ActionLog actionLog, 
				  BindingResult result,
				  Model model
				  ) {
	model.addAttribute("app_url", app_url);
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
		String back = sendApproveEmail(email);
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
	    return "redirect:/staff";
	}
    }
    // discard an incident
    @PostMapping("/discard/final")
    public String discardFinal(@Valid ActionLog actionLog, 
			       BindingResult result,
			       Model model
			       ) {
	model.addAttribute("app_url", app_url);
	if (result.hasErrors()) {
	    String error = extractErrors(result);
	    addError(error);
	    logger.error("Error saving action "+error);
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/search/incomplete";
	}
	User user = findUserFromSession(session);
	if(user == null){
	    return "redirect:/login";
	}
	if(user.canApprove()){
	    actionLog.setDateNow();
	    actionLog.setUser(user);
	    actionLogService.save(actionLog);
	    addMessage("Saved Successfully");				
	    model.addAttribute("messages", messages);
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/search/incomplete";
	}
	else{
	    addMessage("You do not have enough privileges ");
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/staff";
	}
    }
    // discard batch of incidents
    @PostMapping("/discard/batch")
    public String discardBatch(@RequestParam(required=true) Integer[] incident_ids,
			       @RequestParam(required=true) String comments,
			       @RequestParam String target,
			       Model model
			       ) {
	model.addAttribute("app_url", app_url);
	User user = findUserFromSession(session);
	if(user == null){
	    return "redirect:/login";
	}
	if(user.canApprove()){
	    if(incident_ids != null && incident_ids.length > 0){
		Action action = actionService.findById(7); 								
		for(Integer id:incident_ids){
		    Incident incident = incidentService.findById(id);
		    if(incident != null){
			ActionLog actionLog = new ActionLog();
			actionLog.setAction(action);
			actionLog.setIncident(incident);
			actionLog.setDateNow();
			actionLog.setUser(user);
			actionLog.setComments(comments);
			actionLogService.save(actionLog);
		    }
		}
		addMessage("Saved Successfully");				
		model.addAttribute("messages", messages);
	    }
	    addMessagesAndErrorsToSession(session);
	    if(target != null)
		return "redirect:/search/"+target;
	    else
		return "redirect:/search/incomplete";
	}
	else{
	    addMessage("You do not have enough privileges ");
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/staff";
	}
    }        				
    
    // 
    @GetMapping("/incidentView/{id}")
    public String viewIncident(@PathVariable("id") int id,
			       Model model
			       ) {
	Incident incident = null;
	model.addAttribute("app_url", app_url);
	User user = findUserFromSession(session);
	if(user == null){
	    return "redirect:/login";
	}
	try{
	    incident = incidentService.findById(id);
	    if(incident.hasBusinessRecord()){
		Business business = incident.getBusiness();
		if(business != null){
		    model.addAttribute("business", business);
		}
	    }
	    model.addAttribute("incident", incident);
	    if(incident.hasBusinessRecord()){
		model.addAttribute("business", incident.getBusiness());
	    }
	}catch(Exception ex){
	    logger.error("Error incident "+id+" not found "+ex);
	    addError("Invalid incident ID "+id);
	    return "redirect:/search";
	}
	getMessagesAndErrorsFromSession(session, model);
	return "incidentView";

    }    

    private String sendApproveEmail(Email email){
	String ret = "";
	email.setSender(email_sender);
	EmailHelper emailer = new EmailHelper(mailSender, email);
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
