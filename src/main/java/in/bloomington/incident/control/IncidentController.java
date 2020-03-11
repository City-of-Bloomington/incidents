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
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.IncidentType;
import in.bloomington.incident.model.Action;
import in.bloomington.incident.model.ActionLog;
import in.bloomington.incident.utils.Helper;

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
		private Environment env;
		@Value( "${incident.defaultcity}" )
		private String defaultCity;
		@Value( "${incident.defaultstate}" )
		private String defaultState;		
		@Value( "${incident.zipcodes}" )
		private List<String> zipCodes;

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
		/*
		@GetMapping("/incidents")
    public String getAll(Model model) {
        model.addAttribute("incidents", incidentService.getAll());
        return "incidents";
    }
		*/
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
						incident = incidentService.findById(id);
						ActionLog actionLog = new ActionLog();
						actionLog.setIncident(incident);
						Action action = actionService.findById(1); // received action
						actionLog.setAction(action);
						actionLog.setDateNow();
						actionLogService.save(actionLog);
						//
						// the success submission and what to do next
						// confirmation email
						//
						
				}catch(Exception ex){
						addError("Invalid incident Id "+id);
						logger.error(errors+" "+ex);
						model.addAttribute("errors", errors);
						return "start"; 
				}
        model.addAttribute("incident", incident);
				model.addAttribute("messages", messages);
				resetAll();
				return "incident";
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
		/**
			 sending email to user that can be used to confirm the request
			 -get the first person and email
			 -use lastname and incident id to hash using md5
			 String subject = " Bloomington's Police Department Online Reporting Confirmation ";
			 String message = "Please click on the link below to confirm your incident reporting request to Bloomington Police Department. Click on the link ";
			 message += BASE_URL."/confirm?id={$id}&hash={$hash} to confirm.\n\n ";
			 message += " Once your report is reviewed it will either be accepted or rejected, at which time you will receive another email explaining the reason for denial or a report reference number.\n\n
			 message += "Please do not reply to this email as this is an automated system.";

		 */
		
}
