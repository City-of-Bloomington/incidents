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
public class IncidentController {

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

		String errors="", messages="";
		public String getErrors(){
				return errors;
		}
		public String getMessages(){
				return messages;
		}
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
																RedirectAttributes redirectAttributes
																) {
				Incident incident = null;
				try{
						incident = incidentService.findById(id);
				}catch(Exception ex){
						errors += "Invalid incident Id: "+id;
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
        return "incidentAdd";
    }
		@PostMapping("/incidentNext/{id}")
    public String incidentNext(@PathVariable("id") int id,
															 @Valid Incident incident,
															 BindingResult result, Model model,
															 RedirectAttributes redirectAttributes
															 ) {
        if (result.hasErrors()) {
						errors = Helper.extractErrors(result);
						model.addAttribute("entryTypes", entryTypes);
						model.addAttribute("allZipCodes", getAllZipCodes());
						model.addAttribute("allStates", getAllStates());
						model.addAttribute("allCities", getAllCities());
						redirectAttributes.addFlashAttribute("errors",errors);
						// model.addAttribute("errors", errors);
            return "incidentAdd";
        }
				if(!incident.verifyAll(defaultCity,
															 defaultState,
															 zipCodes)){
						errors = incident.getErrorInfo();
						model.addAttribute("entryTypes", entryTypes);
						model.addAttribute("allZipCodes", getAllZipCodes());
						model.addAttribute("allStates", getAllStates());
						model.addAttribute("allCities", getAllCities());
						redirectAttributes.addFlashAttribute("errors",errors);
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
		public String showIncident(@PathVariable("id") int id, Model model) {
				Incident incident = null;
				try{
						incident = incidentService.findById(id);
				}catch(Exception ex){
						errors += "Invalid incident Id";
						logger.error(errors+" "+ex);
						model.addAttribute("errors", errors);
						return "index"; // need fix
				}
        model.addAttribute("incident", incident);				
				return "incident";
		}
		@GetMapping("/incident/submit/{id}")
		public String submitIncident(@PathVariable("id") int id,
																 Model model,
																 RedirectAttributes redirectAttributes
																 ) {
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
						errors += "Invalid incident Id";
						logger.error(errors+" "+ex);
						model.addAttribute("errors", errors);
						return "index"; // need fix
				}
        model.addAttribute("incident", incident);				
				return "incident";
		}		

    @PostMapping("/incident/add")
    public String addIncident(@Valid Incident incident, BindingResult result, Model model) {
        if (result.hasErrors()) {
						errors = Helper.extractErrors(result);
						logger.error("Error starting add new incident "+errors);
            return "incidentAdd";
        }
        incidentService.save(incident);
				messages = "Added Successfully";
				return "redirect:/incident/"+incident.getId();
    }

		@GetMapping("/incident/edit/{id}")
		public String showEditForm(@PathVariable("id") int id, Model model) {
				Incident incident = null;
				try{
						incident = incidentService.findById(id);
						
				}catch(Exception ex){
						errors += "Invalid incident Id";
						logger.error(errors+" "+ex);
						model.addAttribute("errors", errors);
						return "start";
				}
				model.addAttribute("incident", incident);
				model.addAttribute("entryTypes", entryTypes);
				model.addAttribute("allZipCodes", getAllZipCodes());
				model.addAttribute("allStates", getAllStates());
				model.addAttribute("allCities", getAllCities());	
				return "incidentUpdate";
		}
		@PostMapping("/incident/update/{id}")
		public String updateIncident(@PathVariable("id") int id,
																 @Valid Incident incident, 
																 BindingResult result, Model model) {
				if (result.hasErrors()) {
						errors = Helper.extractErrors(result);
						logger.error("Error update incident "+id+" "+errors);
						incident.setId(id);
						return "updateIncident";
				}
				incidentService.update(incident);
				messages = "Updated Successfully";				
				model.addAttribute("messages", messages);
				return "redirect:/incident/"+id;
		}
		
		@GetMapping("/incident/delete/{id}")
		public String deleteIncident(@PathVariable("id") int id, Model model) {

				Incident incident = null;
				try{
						incident = incidentService.findById(id);
						incidentService.delete(id);
						messages = "Deleted Succefully";
				}catch(Exception ex){
						logger.error("Error delete incident "+id+" "+ex);
						errors += "Invalid incident ID "+id;
				}
				model.addAttribute("incidents", incidentService.getAll());
				if(!messages.equals("")){
						model.addAttribute("messages", messages);
				}
				else if(!errors.equals("")){
						model.addAttribute("errors", errors);
				}
				return "redirect:/start";

		}
		
}
