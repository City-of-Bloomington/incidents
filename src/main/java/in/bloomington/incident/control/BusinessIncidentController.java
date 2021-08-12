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
import in.bloomington.incident.service.BusinessService;
import in.bloomington.incident.service.AddressService;
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.IncidentType;
import in.bloomington.incident.model.Action;
import in.bloomington.incident.model.Business;
import in.bloomington.incident.model.Address;
import in.bloomington.incident.model.ActionLog;
import in.bloomington.incident.model.Person;
import in.bloomington.incident.model.Request;
import in.bloomington.incident.model.User;
import in.bloomington.incident.utils.Helper;
import in.bloomington.incident.utils.EmailHelper;

@Controller
public class BusinessIncidentController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(BusinessIncidentController.class);		
    @Autowired
    IncidentService incidentService;
    @Autowired
    BusinessService businessService;			
    @Autowired
    IncidentTypeService incidentTypeService;
    @Autowired
    ActionService actionService;
    @Autowired
    ActionLogService actionLogService;
    @Autowired
    UserService userService;
    @Autowired
		AddressService addressService;		
    @Autowired
    private JavaMailSender mailSender;
		@Autowired 
    private HttpSession session;		
		
		//
    @Value("${incident.email.sender}")
    private String email_sender;
    @Value("${incident.application.name}")
    private String application_name;
		//
		// incident is already save, so we do update for the new fields
    @PostMapping("/businessIncidentChange/{id}")
    public String busIncidentChange(@PathVariable("id") int id,
															 @Valid Incident incident,
															 BindingResult result,
															 Model model
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
								model.addAttribute("incident", incident);
								handleErrorsAndMessages(model);
								resetAll();
								return "businessIncidentAdd";
						}
						int addr_id = incident.getAddr_id();
						if(addr_id > 0){
								Address address = addressService.findById(addr_id);;
								if(address != null)
										incident.setAddress(address);
						}						
						incidentService.update(incident);
						//
						// this redirect will decide the next step
						//
						return "redirect:/businessIncident/"+id;
				}
				else {
						addMessage("no more changes can be made");
						addMessagesAndErrorsToSession(session);
						return "redirect:/businessIncident/"+id;	    
				}
    }
    // view mode
    @GetMapping("/businessIncident/{id}")
    public String showBusIncident(@PathVariable("id") int id,
															 Model model,
															 HttpServletRequest req,
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
						if(incident.hasBusinessRecord()){
								Business business = incident.getBusiness();
						}
						else{
								System.err.println(" No business record ");
						}
				}catch(Exception ex){
						addError("Invalid incident Id "+id);
						logger.error(""+ex);
						redirectAttributes.addFlashAttribute("errors", errors);
						return "redirect:/error";
				}
				if(incident.canBeChanged()){
						if(!incident.verifyDetails()){
								addMessage("Add incident details");
								addMessagesAndErrorsToSession(session);	 
								return "redirect:/businessIncidentEdit/"+incident.getId();
						}
						if(!incident.hasBusinessRecord()){
								addMessage("You need to add business info");
								addMessagesAndErrorsToSession(session);	    
								return "redirect:/business/add/"+id;
						}
						if(!incident.hasOffenderList()){
								addMessage("You need to add offender");
								addMessagesAndErrorsToSession(session);	    
								return "redirect:/offender/add/"+id;
						}
						if(!incident.hasPropertyList()){
								addMessage("You need to add a property");
								addMessagesAndErrorsToSession(session);	    
								return "redirect:/businessProperty/add/"+id;
						}								
						if(!incident.hasMediaList()){
								addMessage("You need to add a receipt or photo of the damage");
								addMessagesAndErrorsToSession(session);
								return "redirect:/businessMedia/add/"+id;
						}
						Business business = incident.getBusiness();
						Address address = incident.getAddress();
						incident.setBusiness(business);
						incident.setAddress(address);
						incident.setIgnoreStatus(true);
						model.addAttribute("incident", incident);
						model.addAttribute("business", business);
						getMessagesAndErrorsFromSession(session, model);
						return "businessIncident";
				}
				else {
						addError("No more changes can be made to this incident");
						addMessagesAndErrorsToSession(session);
						return "redirect:/forBusiness";
				}
    }
		//
		@GetMapping("/businessIncidentAdd/{bus_id}/{addr_id}")
    public String busIncidentAdd(@PathVariable("addr_id") int addr_id,
																 @PathVariable("bus_id") int bus_id,
																 Model model
																 ) {
				getMessagesAndErrorsFromSession(session, model);		
				Address address = addressService.findById(addr_id);
				List<IncidentType> allTypes = incidentTypeService.getAll();
				List<IncidentType> types = null;
				if(allTypes != null){
						types = allTypes.stream()
								.filter( y -> y.getUsedInBusiness())
								.collect(Collectors.toList());
				}
				Business business = businessService.findById(bus_id);
				Incident incident = new Incident();
				incident.setAddr_id(addr_id);
				incident.setBus_id(bus_id);
				incident.setAddress(address);
				incident.setBusiness(business);
				incident.setEmail(business.getEmail());

				model.addAttribute("incident", incident);
				model.addAttribute("business", business);				
				model.addAttribute("types", types);
				getMessagesAndErrorsFromSession(session, model);
        return "businessIncidentAdd";
		}
		
		@PostMapping("/businessIncident/save")
    public String busIncidentSave(
																	@Valid Incident incident,
																	Model model,
																	BindingResult result
																	) {
				List<String> ids = null;
				int id = 0;
				try{
						ids = (List<String>) session.getAttribute("incident_ids");
				}catch(Exception ex){
						System.err.println(ex);
				}				
				Address address = addressService.findById(incident.getAddr_id());
				Business business = businessService.findById(incident.getBus_id());
				incident.setEmail(business.getEmail());
				incident.setReceivedNow();
				incident.setCategory("Business");
				incident.setAddress(address);
				incident.setBusiness(business);
				if(incident.verifyAll()){
						incidentService.save(incident);
						if(ids == null){
								ids = new ArrayList<>();
						}
						id = incident.getId();
						ids.add(""+id);
						session.setAttribute("incident_ids", ids);
						//
						// this redirect will decide what is next
						//
						return "redirect:/businessIncident/"+id;
				}
				else{
						addError(incident.getErrorInfo());
						addMessagesAndErrorsToSession(session);
						return "redirect:/businessIncidentAdd/"+business.getId()+"/"+address.getId();
				}
		}
		@GetMapping("/businessIncidentEdit/{id}")
    public String busIncidentUpdate(@PathVariable("id") int id,
																		Model model
																		) {
				Incident incident = incidentService.findById(id);				
				Address address = incident.getAddress();
				Business business = incident.getBusiness();
				incident.setAddress(address);
				incident.setBusiness(business);
				incident.setCategory("Business");
				model.addAttribute("incident", incident);
				List<IncidentType> allTypes = incidentTypeService.getAll();
				List<IncidentType> types = null;
				if(allTypes != null){
						types = allTypes.stream()
								.filter( y -> y.getUsedInBusiness())
								.collect(Collectors.toList());
				}
				if(types != null && types.size() > 0)
						model.addAttribute("types", types);
				getMessagesAndErrorsFromSession(session, model);					
        return "businessIncidentUpdate";
		}
		
    //
    @GetMapping("/businessIncident/finalPage/{id}")
    public String busIncidentFinalPage(@PathVariable("id") int id,
																		Model model,
																		RedirectAttributes redirectAttributes
																		) {
				Incident incident = null;
				if(!verifySession(session, ""+id)){
						addMessage("no more changes can be made ");
						addMessagesAndErrorsToSession(session);
						return "redirect:/forBusiness";
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
						return "businessFinalSubmit";
				}
				else{
						addMessage("incident can not be submitted ");
						addMessages(incident.getErrors());
						addMessagesAndErrorsToSession(session);
						return "redirect:/forBusiness";
				}
    }
    @GetMapping("/businessIncident/submit/{id}")
    public String busSubmitIncident(@PathVariable("id") int id,
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
						return "redirect:/forBusiness";
				}
				try{
						ActionLog actionLog = new ActionLog();
						actionLog.setIncident(incident);
						Action action = actionService.findById(2); // received action
						actionLog.setAction(action);
						actionLog.setDateNow();
						actionLogService.save(actionLog);
						//
						// create the request and related hash
						//
						String back = createRequestAndEmail(incident);
						// the success submission and what to do next
						// confirmation email
						//
						session.removeAttribute("incident_ids");
						session.invalidate();
				}catch(Exception ex){
						addError("Invalid incident Id "+id);
						logger.error(errors+" "+ex);
						addMessagesAndErrorsToSession(session);
						return "redirect:/forBusiness"; 
				}
				handleErrorsAndMessages(model);
				return "businessSuccessSubmission";
    }
		private String createRequestAndEmail(Incident incident){
				String ret = "";
				String lastname = "";
				int id = incident.getId();
				Business business = null;
				business = incident.getBusiness();
				//
				// now we can send the submission email
				//
				String subject = " Bloomington's Police Department Incident Online Reporting received ";
				String to = incident.getEmail();
				if(to != null){
						String body = "Your submission to the Bloomington Police Department Establishment Incident Reporting system has been received. Thank you!<br />\n\n";
						body += "Once your submission is processed, you will receive an email with the case number. If the submission is incomplete, or does not meet the requirements for the Establishment Incident Reporting System, you will receive an email explaining the rejection.<br /><br />\n\n";
						body += " Please do not reply to this email, as this is an automated system.<br />\n\n";
						body += "Bloomington Police Department (BPD) <br />\n";
						body += "220 E 3rd St, Bloomington, IN 47401 <br />\n";
						body += "(812) 339-4477<br />\n";
						body += "https://bloomington.in.gov/police <br />";
						body += "\n";
						EmailHelper emailHelper = new EmailHelper(mailSender, email_sender, to, subject, body);
						String back = emailHelper.send();
						if(!back.isEmpty()){
								addError(back);
								logger.error(back);
								ret += back;
						}
				}
				//
				// uncomment in production to send email
				//
				return ret;
    }
}
