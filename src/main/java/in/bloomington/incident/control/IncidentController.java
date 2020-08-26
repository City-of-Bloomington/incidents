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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.validation.ObjectError;
import org.springframework.validation.FieldError;
import org.springframework.mail.javamail.JavaMailSender;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import in.bloomington.incident.service.IncidentService;
import in.bloomington.incident.service.IncidentTypeService;
import in.bloomington.incident.service.ActionService;
import in.bloomington.incident.service.ActionLogService;
import in.bloomington.incident.service.RequestService;
import in.bloomington.incident.service.UserService;
import in.bloomington.incident.service.AddressService;
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.IncidentType;
import in.bloomington.incident.model.Action;
import in.bloomington.incident.model.Address;
import in.bloomington.incident.model.ActionLog;
import in.bloomington.incident.model.Person;
import in.bloomington.incident.model.Request;
import in.bloomington.incident.model.User;
import in.bloomington.incident.utils.Helper;
import in.bloomington.incident.utils.EmailHelper;
// import in.bloomington.incident.utils.AddressCheck;

@Controller
public class IncidentController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(IncidentController.class);		
    final static List<String> entryTypes =
				new ArrayList<>(Arrays.asList(
																			"Broke window",
																			"Damaged window",
																			"Damaged door",
																			"Pried window",
																			"Pried door",
																			"Unlocked door",
																			"Unlocked vehicle",
																			"Other specify"));
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
    UserService userService;
    @Autowired
		AddressService addressService;		
    @Autowired
    private JavaMailSender mailSender;
    // @Autowired
    // AddressCheck addressCheck;
    
    private Environment env;

    @Value("${incident.email.sender}")
    private String email_sender;
    @Value("${incident.application.name}")
    private String application_name;
    @Value("${incident.address.checkurl}")    
    private String address_check_url;
    @Value("${server.servlet.context-path}")
    private String hostPath; // incidents in production


    @RequestMapping("/initialStart")
    public String initialNext(@RequestParam(required = true) String email,
															@RequestParam(required = true) String email2,
															@RequestParam(required = true) int type_id,
															@RequestParam(required = true) int address_id,
															Model model,
															HttpSession session
															){
				boolean emailProblem = false;
				if(email == null || email.isEmpty() ||
					 email2 == null || email2.isEmpty()){
						addError("Both emails are required");
						addMessagesAndErrorsToSession(session);
						emailProblem = true;
				}
				if(!email.equals(email2)){
						addError("The two emails do not match");
						addMessagesAndErrorsToSession(session);
						emailProblem = true;
				}
				if(!Helper.isValidEmail(email)){
						addError("Invalid Email "+email);
						addMessagesAndErrorsToSession(session);
						emailProblem = true;
				}
				if(emailProblem){
						return "redirect:/introEmail?type_id="+type_id;
				}
				IncidentType type = incidentTypeService.findById(type_id);
				Address address = addressService.findById(address_id);
				Incident incident = new Incident();
				incident.setEmail(email);
				incident.setReceivedNow();
				incident.setIncidentType(type);
				incident.setAddress(address);
				incidentService.save(incident);
				//
				// this is the only place we are adding
				// incident ID in the session
				//

				List<String> ids = null;
				try{
						ids = (List<String>) session.getAttribute("incident_ids");
				}catch(Exception ex){
						System.err.println(ex);
				}
				if(ids == null){
						ids = new ArrayList<>();
				}
				ids.add(""+incident.getId());
				session.setAttribute("incident_ids", ids);
				//
				// return "redirect:/incidentStart/"+incident.getId();
				//
				model.addAttribute("incident", incident);
				model.addAttribute("entryTypes", entryTypes);
				model.addAttribute("hostPath", hostPath);
				handleErrorsAndMessages(model);
        return "incidentAdd";

    }
		/**
    @GetMapping("/incidentStart/{id}")
    public String startIncident(@PathVariable("id") int id,
																Model model,
																RedirectAttributes redirectAttributes,
																HttpSession session
																) {
				if(!verifySession(session, ""+id)){				
						addMessage("No more changes can be made ");
						addMessagesAndErrorsToSession(session);
						return "redirect:/index";
				}
				Incident incident = null;
				try{
						incident = incidentService.findById(id);
				}catch(Exception ex){
						addError("Invalid incident Id: "+id);
						logger.error(errors+" "+ex);
						model.addAttribute("errors", errors);
						redirectAttributes.addFlashAttribute("errors",
																								 "invalid incident " + id + "!");
						return "redirect:/start";
				}
        model.addAttribute("incident", incident);
				model.addAttribute("entryTypes", entryTypes);
				model.addAttribute("hostPath", hostPath);
				handleErrorsAndMessages(model);
        return "incidentAdd";
    }
		*/
    @PostMapping("/incidentNext/{id}")
    public String incidentNext(@PathVariable("id") int id,
															 @Valid Incident incident,
															 BindingResult result,
															 Model model,
															 HttpSession session
															 ) {
				boolean pass = true;
        if (result.hasErrors()) {
						addError(Helper.extractErrors(result));
						pass = false;
        }
				if(!verifySession(session, ""+id)){				
						addMessage("No more changes can be made ");
						addMessagesAndErrorsToSession(session);
						return "redirect:/";
				}
				incident.setId(id);
				if(incident.canBeChanged()){	
						if(!incident.verifyAll()){
								addError(incident.getErrorInfo());
								pass = false;
						}
						if(!pass){
								model.addAttribute("entryTypes", entryTypes);
								model.addAttribute("hostPath", hostPath);
								model.addAttribute("incident", incident);
								handleErrorsAndMessages(model);
								return "incidentAdd";
						}
						incidentService.update(incident);
						//
						// this redirect will decide the next step
						//
						return "redirect:/incident/"+id;
						/**
						if(incident.hasPersonList()){
								model.addAttribute("incident", incident);
								model.addAttribute("hostPath", hostPath);
								return "incident";		
						}
						else{
								return "redirect:/person/add/"+id;
						}
						*/
				}
				else {
						addMessage("no more changes can be made");
						addMessagesAndErrorsToSession(session);
						return "redirect:/introStart";	    
				}
    }
    // view mode
    @GetMapping("/incident/{id}")
    public String showIncident(@PathVariable("id") int id,
															 Model model,
															 HttpServletRequest req,
															 RedirectAttributes redirectAttributes,
															 HttpSession session
															 ) {
				Incident incident = null;
				if(!verifySession(session, ""+id)){
						addMessage("no more changes can be made ");
						addMessagesAndErrorsToSession(session);
						return "redirect:/index";						
				}
				try{
						incident = incidentService.findById(id);
				}catch(Exception ex){
						addError("Invalid incident Id "+id);
						logger.error(""+ex);
						redirectAttributes.addFlashAttribute("errors", errors);
						return "redirect:/error";
				}
				if(incident.canBeChanged()){	
						if(!incident.hasPersonList()){
								addMessage("You need to add a person");
								addMessagesAndErrorsToSession(session);	    
								return "redirect:/person/add/"+id;
						}
						if(incident.isFraudRelated() && !incident.hasFraudList()){
								addMessage("You need to add fraud info");
								addMessagesAndErrorsToSession(session);	    
								return "redirect:/fraud/add/"+id;
						}
						if(!incident.isFraudRelated() && !incident.hasPropertyList()){
								addMessage("You need to add a property");
								addMessagesAndErrorsToSession(session);	    
								return "redirect:/property/add/"+id;
						}
						if(incident.isVehicleRequired() && !incident.hasVehicleList()){
								addMessage("You need to add a vehicle");
								addMessagesAndErrorsToSession(session);
								return "redirect:/vehicle/add/"+id;
						}
						model.addAttribute("incident", incident);
						model.addAttribute("hostPath", hostPath);	    
						getMessagesAndErrorsFromSession(session, model);
						return "incident";	    
				}
				else {
						addError("No more changes can be made to this incident");
						addMessagesAndErrorsToSession(session);
						return "redirect:/introStart";
				}

    }
    //
    @GetMapping("/incident/finalPage/{id}")
    public String incidentFinalPage(@PathVariable("id") int id,
																		Model model,
																		HttpSession session,
																		RedirectAttributes redirectAttributes
																		) {
				Incident incident = null;
				if(!verifySession(session, ""+id)){
						addMessage("no more changes can be made ");
						addMessagesAndErrorsToSession(session);
						return "redirect:/index";
				}
				try{
						incident = incidentService.findById(id);
				}catch(Exception ex){
						addError("Invalid incident Id "+id);
						logger.error(""+ex);
						redirectAttributes.addFlashAttribute("errors", errors);
						return "redirect:/error";
				}
				if(incident.canBeSubmitted()){
						model.addAttribute("incident", incident);
						handleErrorsAndMessages(model);	
						return "finalSubmit";
				}
				else{
						addMessage("incident can be submitted ");
						addMessages(incident.getErrors());
						addMessagesAndErrorsToSession(session);
						return "redirect:/introStart";
				}
    }
    
		
    @GetMapping("/incident/submit/{id}")
    public String submitIncident(@PathVariable("id") int id,
																 Model model,
																 RedirectAttributes redirectAttributes,
																 HttpServletRequest req
																 ) {
				HttpSession session = req.getSession();
				if(!verifySession(session, ""+id)){
						addMessage("no more changes can be made ");
						addMessagesAndErrorsToSession(session);
						return "redirect:/index";
				}
				Incident incident = null;
				incident = incidentService.findById(id);
				if(!incident.canBeSubmitted()){
						addMessage("Incident can not be submitted ");
						addMessages(incident.getErrors());
						addMessagesAndErrorsToSession(session);
						return "redirect:/index";
				}
				try{
						String host = req.getServerName();
						String uri = req.getRequestURI();
						String scheme = req.getScheme();
						int port = req.getServerPort();
						String url = scheme+"://"+host;
						if(port == 8080){ // for localhost
								url += ":"+port;
						}	    
						if(hostPath != null)
								url += hostPath;

						url += "/incident/confirm/";

						ActionLog actionLog = new ActionLog();
						actionLog.setIncident(incident);
						Action action = actionService.findById(2); // received action
						actionLog.setAction(action);
						actionLog.setDateNow();
						actionLogService.save(actionLog);
						//
						// create the request and related hash
						//
						String back = createRequestAndEmail(url, incident);
						// the success submission and what to do next
						// confirmation email
						//
						session.removeAttribute("incident_ids");
						session.invalidate();
				}catch(Exception ex){
						addError("Invalid incident Id "+id);
						logger.error(errors+" "+ex);
						model.addAttribute("errors", errors);
						return "redirect:/index"; 
				}
				handleErrorsAndMessages(model);
				return "successSubmission";
    }		
    @GetMapping("/incident/confirm/{id}/{hash}")
    public String submitIncident(@PathVariable("id") int id,
																 @PathVariable("hash") String hash,
																 Model model
																 ) {
				String error = "";
				Request request = null;
				Incident incident = null;
				try{
						request = requestService.findById(id);
						incident = incidentService.findById(id);
				}catch(Exception ex){}
				System.err.println("hash: "+hash);
				System.err.println("req hash: "+request.getHash());
				if(request == null){
						error = "Incident not found ";
				}
				else if(request.isConfirmed()){
						error = "The incident is already confirmed ";
				}
				else if(!request.getHash().equals(hash)){
						error = "The hash does not match ";
				}
				/*
					else if(request.checkExpired()){ // with current time
					// we may ignore this condition and still
					// let the request to be confirmed
					error = "The confirmation is too late, the request expired";
					}
				*/
				else{
						request.setConfirmed('y');
						requestService.update(request);
						//
						// add action log
						//
						ActionLog actionLog = new ActionLog();
						actionLog.setIncident(incident);
						Action action = actionService.findById(3); // confirmed action
						actionLog.setAction(action);
						actionLog.setDateNow();
						actionLogService.save(actionLog);
				}
				if(error.equals("")){
						return "confirm";
				}
				else{
						model.addAttribute("failure_message", error);
						return "failedconfirm";
				}
    }
    /**
			 @PostMapping("/incident/add")
			 public String addIncident(@Valid Incident incident,
			 BindingResult result,
			 Model model,
			 HttpSession session
			 ) {
			 if (result.hasErrors()) {
			 String error = Helper.extractErrors(result);
			 addError(error);
			 logger.error("Error starting new incident "+error);
			 return "incidentAdd";
			 }
			 incidentService.save(incident);
			 addMessage("Added Successfully");
			 addMessagesAndErrorsToSession(session);
			 resetAll();
			 return "redirect:/incident/"+incident.getId();
			 }
    */
    @GetMapping("/incident/edit/{id}")
    public String showEditForm(@PathVariable("id") int id,
															 Model model,
															 HttpSession session
															 ) {
				if(!verifySession(session, ""+id)){
						addMessage("no more changes can be made ");
						addMessagesAndErrorsToSession(session);
						return "redirect:/index";
				}
				Incident incident = null;
				try{
						incident = incidentService.findById(id);
						
				}catch(Exception ex){
						addError("Invalid incident Id "+id);
						logger.error(""+ex);

						return "start";
				}
				if(incident.canBeChanged()){
						model.addAttribute("incident", incident);
						model.addAttribute("entryTypes", entryTypes);
						// model.addAttribute("allZipCodes", getAllZipCodes());
						// model.addAttribute("allStates", getAllStates());
						// model.addAttribute("allCities", getAllCities());
						model.addAttribute("hostPath",hostPath);
						getMessagesAndErrorsFromSession(session, model);
						// handleErrorsAndMessages(model);
						return "incidentUpdate";
				}
				else {
						addError("No more changes can be made to this incident");
						addMessagesAndErrorsToSession(session);
						return "redirect:/index";
				}
    }
    @PostMapping("/incident/update/{id}")
    public String updateIncident(@PathVariable("id") int id,
																 @Valid Incident incident, 
																 BindingResult result,
																 Model model,
																 HttpSession session
																 ) {
				if (result.hasErrors()) {
						String error = Helper.extractErrors(result);
						addError(error);
						logger.error("Error update incident "+id+" "+error);
						handleErrorsAndMessages(model);
						return "updateIncident";
				}
				if(!verifySession(session, ""+id)){
						addMessage("no more changes can be made ");
						addMessagesAndErrorsToSession(session);
						return "redirect:/index";
				}
				if(incident.canBeChanged()){
						boolean pass = true;
						if(!incident.verifyAll()){
								addError(incident.getErrorInfo());
								addMessagesAndErrorsToSession(session);		
								pass = false;
						}
						/**
						if(pass && incident.isAddressChanged() &&
							 addressCheck.isInIUPDLayer(incident.getLatitude(),
							 incident.getLongitude())){
							 pass = false;
								addError("This address is in IU Police Department district");
						}
						*/
						if(!pass){
								return "redirect:/incident/edit/"+incident.getId();
						}
						incidentService.update(incident);
						addMessage("Updated Successfully");				
						addMessagesAndErrorsToSession(session);
						return "redirect:/incident/"+id;
				}
				else{
						addError("No more changes can be made to this incident");
						addMessagesAndErrorsToSession(session);
						return "redirect:/index";
				}
    }
		
    @GetMapping("/incident/delete/{id}")
    public String deleteIncident(@PathVariable("id") int id,
																 Model model,
																 HttpSession session
																 ) {
				if(!verifySession(session, ""+id)){
						addMessage("no more changes can be made ");
						addMessagesAndErrorsToSession(session);
						return "redirect:/index";
				}
				Incident incident = null;
				try{
						incident = incidentService.findById(id);
						if(incident.canBeChanged()){
								incidentService.delete(id);
								addMessage("Deleted Succefully");
						}
				}catch(Exception ex){
						logger.error("Error delete incident "+id+" "+ex);
						addError("Invalid incident ID "+id);
				}
				addMessagesAndErrorsToSession(session);
				return "redirect:/index";

    }
    private String createRequestAndEmail(String url,
																				 Incident incident){
				String ret = "";
				String lastname = "";
				int id = incident.getId();
				Person person = null;
				List<Person> persons = incident.getPersons();
				if(persons != null){
						for(Person one:persons){
								if(one.isReporter()){ // first reporter
										person = one;
										break;
								}
						}
						// if non is reporter we take the first one
						if(person == null){
								person = persons.get(0);
						}
				}
				if(person != null){
						lastname = person.getLastname();
				}
				String strToHash = ""+id+""+lastname;
				String hash = Helper.createMD5Hash(strToHash);
				Request request = new Request();
				request.setId(id);
				request.setHash(hash);
				request.setExpireDateTime(); // 72 hours from now
				requestService.save(request);
				//
				// now we can send the submission email
				//
				String subject = " Bloomington's Police Department Online Reporting Confirmation ";
				String to = incident.getEmail();
				String body = "Click on the link ";
				body += url+id+"/"+hash+" to confirm.\n\n ";
				body += " Once your report is reviewed it will either be accepted or rejected, at which time you will receive another email explaining the reason for denial or a report reference number.\n\n";
				body += "Please do not reply to this email as this is an automated system.";
				EmailHelper emailHelper = new EmailHelper(mailSender, email_sender, to, subject, body);
				String back = emailHelper.send();
				if(!back.isEmpty()){
						addError(back);
						logger.error(back);
						ret += back;
				}
				/**
					 System.err.println(" url "+url);
					 System.err.println(" host "+email_host);
					 System.err.println(" to "+to);
					 System.err.println(" from "+email_sender);
				*/
				/**
					 EmailHandle emailer = new EmailHandle(email_host,
					 to,
					 email_sender,
					 subject,
					 message);
				*/
				//
				// uncomment in production to send email
				//
				return ret;
    }

}
