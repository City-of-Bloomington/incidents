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
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.IncidentType;
import in.bloomington.incident.model.Action;
import in.bloomington.incident.model.ActionLog;
import in.bloomington.incident.model.Person;
import in.bloomington.incident.model.Request;
import in.bloomington.incident.utils.Helper;
import in.bloomington.incident.utils.EmailHandle;

@Controller
public class IncidentController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(IncidentController.class);		
    final static List<String> entryTypes =
				new ArrayList<>(Arrays.asList("Unlocked vehicle", "Broke window","Pried window","Pried door","Other specify"));
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
    private Environment env;
    @Value( "${incident.defaultcity}" )
    private String defaultCity;
    @Value( "${incident.defaultstate}" )
    private String defaultState;		
    @Value( "${incident.zipcodes}" )
    private List<String> zipCodes;
    @Value("${incident.email.host}")
    private String email_host;
    @Value("${incident.email.sender}")
    private String email_sender;
    @Value("${incident.application.name}")
    private String application_name;    

    public List<String> getAllZipCodes(){
				return zipCodes;
    }
    public List<String> getAllCities(){
				List<String> allCities = new ArrayList<>();
				if(defaultCity != null){
						allCities.add(defaultCity);
				}
				return allCities;
    }
    public List<String> getAllStates(){
				List<String> allStates = new ArrayList<>();
				if(defaultState != null){
						allStates.add(defaultState);
				}
				return allStates;
    }
    @GetMapping("/incidentStart/{id}")
    public String startIncident(@PathVariable("id") int id,
																Model model,
																RedirectAttributes redirectAttributes,
																HttpServletRequest req
																) {
				if(!Helper.verifySession(req, ""+id)){
						System.err.println(" not in session ");
						addMessage(" not in session ");
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
				model.addAttribute("allZipCodes", getAllZipCodes());
				model.addAttribute("allStates", getAllStates());
				model.addAttribute("allCities", getAllCities());
				model.addAttribute("messages", messages);
        return "incidentAdd";
    }
    @PostMapping("/incidentNext/{id}")
    public String incidentNext(@PathVariable("id") int id,
															 @Valid Incident incident,
															 BindingResult result, Model model,
															 HttpServletRequest req
															 ) {
        if (result.hasErrors()) {
						addError(Helper.extractErrors(result));
						model.addAttribute("entryTypes", entryTypes);
						model.addAttribute("allZipCodes", getAllZipCodes());
						model.addAttribute("allStates", getAllStates());
						model.addAttribute("allCities", getAllCities());
						model.addAttribute("errors", getErrors());
						resetAll();
            return "incidentAdd";
        }
				if(!incident.verifyAll(defaultCity,
															 defaultState,
															 zipCodes)){
						addError(incident.getErrorInfo());
						model.addAttribute("entryTypes", entryTypes);
						model.addAttribute("allZipCodes", getAllZipCodes());
						model.addAttribute("allStates", getAllStates());
						model.addAttribute("allCities", getAllCities());
						model.addAttribute("errors", getErrors());
						return "incidentAdd";
				}
        incidentService.update(incident);
				// check if incident have persons
				if(incident.hasPersonList()){
						model.addAttribute("incident", incident);		
						return "incident";		
				}
				else{
						return "redirect:/person/add/"+id;
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
				if(!Helper.verifySession(req, ""+id)){
						System.err.println(" not in session ");
						addMessage("not in session ");
				}
				try{
						incident = incidentService.findById(id);
				}catch(Exception ex){
						addError("Invalid incident Id "+id);
						logger.error(""+ex);
						redirectAttributes.addFlashAttribute("errors", errors);
						return "redirect:/error";
				}
        model.addAttribute("incident", incident);
				getMessagesFromSession(session);
				if(hasMessages()){
						model.addAttribute("messages", getMessages());
				}
				if(hasErrors()){
						model.addAttribute("errors",getErrors());
				}
				resetAll();
				return "incident";
    }
    // view mode
    @GetMapping("/incident/finalPage/{id}")
    public String incidentFinalPage(@PathVariable("id") int id,
																		Model model,
																		HttpServletRequest req,
																		RedirectAttributes redirectAttributes
																		) {
				Incident incident = null;
				if(!Helper.verifySession(req, ""+id)){
						System.err.println(" not in session ");
						addMessage("not in session ");
				}
				try{
						incident = incidentService.findById(id);
				}catch(Exception ex){
						addError("Invalid incident Id "+id);
						logger.error(""+ex);
						redirectAttributes.addFlashAttribute("errors", errors);
						return "redirect:/error";
				}
				addMessage("this is final page");
        model.addAttribute("incident", incident);
				model.addAttribute("messages", messages);
				resetAll();
				return "finalSubmit";
    }		
		
    @GetMapping("/incident/submit/{id}")
    public String submitIncident(@PathVariable("id") int id,
																 Model model,
																 RedirectAttributes redirectAttributes,
																 HttpServletRequest req
																 ) {
				if(!Helper.verifySession(req, ""+id)){
						addMessage("not in session ");						
				}
				Incident incident = null;
				try{
						String host = req.getServerName();
						String uri = req.getRequestURI();
						String scheme = req.getScheme();
						int port = req.getServerPort();
						String url = scheme+"://"+host;
						if(port > 0)
								url += ":"+port;
						if(application_name != null)
								url += application_name;
						url += "/incident/confirm/";
						incident = incidentService.findById(id);
						ActionLog actionLog = new ActionLog();
						actionLog.setIncident(incident);
						Action action = actionService.findById(1); // received action
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
						
				}catch(Exception ex){
						addError("Invalid incident Id "+id);
						logger.error(errors+" "+ex);
						model.addAttribute("errors", errors);
						return "start"; 
				}
				model.addAttribute("messages", messages);
				resetAll();
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
				else if(request.checkExpired()){ // with current time
						error = "The confirmation is too late, the request expired";
				}
				else{
						request.setConfirmed('y');
						requestService.update(request);
						//
						// add action log
						//
						ActionLog actionLog = new ActionLog();
						actionLog.setIncident(incident);
						Action action = actionService.findById(2); // confirmed action
						actionLog.setAction(action);
						actionLog.setDateNow();
						actionLogService.save(actionLog);
				}
				if(error.equals("")){
						return "confirm";
				}
				else{
						System.err.println(" *** failure message "+error);
						model.addAttribute("failure_message", error);
						return "failedconfirm";
				}
    }
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
				addMessagesToSession(session);
				resetAll();
				return "redirect:/incident/"+incident.getId();
    }

    @GetMapping("/incident/edit/{id}")
    public String showEditForm(@PathVariable("id") int id,
															 Model model,
															 HttpSession session
															 ) {
				if(!Helper.verifySession(session, ""+id)){
						System.err.println(" not in session ");
						addMessage(" not in session ");
				}
				Incident incident = null;
				try{
						incident = incidentService.findById(id);
						
				}catch(Exception ex){
						addError("Invalid incident Id "+id);
						logger.error(""+ex);
						model.addAttribute("errors", errors);
						resetAll();
						return "start";
				}
				model.addAttribute("incident", incident);
				model.addAttribute("entryTypes", entryTypes);
				model.addAttribute("allZipCodes", getAllZipCodes());
				model.addAttribute("allStates", getAllStates());
				model.addAttribute("allCities", getAllCities());
				if(hasMessages()){
						model.addAttribute("messages", messages);
						resetAll();
				}
				return "incidentUpdate";
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
						incident.setId(id);
						return "updateIncident";
				}
				if(!Helper.verifySession(session, ""+id)){
						System.err.println(" not in session ");
				}				
				incidentService.update(incident);
				addMessage("Updated Successfully");				
				model.addAttribute("messages", messages);
				addMessagesToSession(session);
				return "redirect:/incident/"+id;
    }
		
    @GetMapping("/incident/delete/{id}")
    public String deleteIncident(@PathVariable("id") int id,
																 Model model,
																 HttpServletRequest req
																 ) {
				if(!Helper.verifySession(req, ""+id)){
						System.err.println(" not in session ");
				}				
				Incident incident = null;
				try{
						incident = incidentService.findById(id);
						incidentService.delete(id);
						addMessage("Deleted Succefully");
				}catch(Exception ex){
						logger.error("Error delete incident "+id+" "+ex);
						addError("Invalid incident ID "+id);
				}
				model.addAttribute("incidents", incidentService.getAll());
				if(hasMessages()){
						model.addAttribute("messages", messages);
				}
				else if(hasErrors()){
						model.addAttribute("errors", errors);
				}
				return "redirect:/start";

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
				String message = "Click on the link ";
				message += url+id+"/"+hash+" to confirm.\n\n ";
				message += " Once your report is reviewed it will either be accepted or rejected, at which time you will receive another email explaining the reason for denial or a report reference number.\n\n";
				message += "Please do not reply to this email as this is an automated system.";
				if(email_host == null || email_sender == null){
						String error = " no email host or sender specified";
						addError(error);
						logger.error(error);
						return error;
				}
				System.err.println(" url "+url);
				System.err.println(" host "+email_host);
				System.err.println(" to "+to);
				System.err.println(" from "+email_sender);
	
				EmailHandle emailer = new EmailHandle(email_host,
																							to,
																							email_sender,
																							subject,
																							message);
				//
				// uncomment in production to send email
				//
				ret = emailer.send(); 
				return ret;
    }
		
}
