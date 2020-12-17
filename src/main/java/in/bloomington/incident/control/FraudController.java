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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.validation.ObjectError;
import org.springframework.validation.FieldError;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import in.bloomington.incident.service.IncidentService;
import in.bloomington.incident.service.FraudService;
import in.bloomington.incident.service.FraudTypeService;
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.Fraud;
import in.bloomington.incident.model.FraudType;
import in.bloomington.incident.model.User;
import in.bloomington.incident.utils.Helper;


@Controller
public class FraudController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(FraudController.class);
    @Autowired
    IncidentService incidentService;
    @Autowired
    FraudService fraudService;		
    @Autowired
    FraudTypeService fraudTypeService;
		
    //
    @RequestMapping("/fraud/add/{incident_id}")
    public String addFraud(@PathVariable("incident_id") int incident_id,
														Model model,
														HttpSession session) {
				if(!verifySession(session, ""+incident_id)){				
						addMessage("No more changes can be made ");
						addMessagesAndErrorsToSession(session);
						return "redirect:/index";
				}
				Incident incident = null;
				try{
						incident = incidentService.findById(incident_id);
				}catch(Exception ex){
						addError("Invalid incident Id: "+incident_id);
						logger.error(""+ex);
						model.addAttribute("errors", errors);
						addMessagesAndErrorsToSession(session);
						return "redirect:/start";
				}
				if(incident == null || !incident.canBeChanged()){
						addMessage("No more changes can be made");
						addMessagesAndErrorsToSession(session);
						return "redirect:/incident/"+incident_id;
				}
				Fraud fraud = new Fraud();				
				List<FraudType> fraudTypes = fraudTypeService.getAll();
				fraud.setIncident(incident);
				model.addAttribute("fraud", fraud);
				model.addAttribute("fraudTypes", fraudTypes);
        return "fraudAdd";
    }
    @PostMapping("/fraud/save")
    public String fraudSave(@Valid Fraud fraud,
														 BindingResult result,
														 Model model,
														 HttpSession session
														 ) {
        if (result.hasErrors()) {
						String error = Helper.extractErrors(result);
						addError(error);
						logger.error(error);
						List<FraudType> fraudTypes = fraudTypeService.getAll();
						model.addAttribute("errors", errors);
						model.addAttribute("fraud", fraud);
						model.addAttribute("fraudTypes", fraudTypes);
						model.addAttribute("errors", errors);
						handleErrorsAndMessages(model);
						return "fraudAdd";
        }
				Incident incident = fraud.getIncident();
				if(incident == null || !incident.canBeChanged()){
						addMessage("No more changes can be made");
						addMessagesAndErrorsToSession(session);
						return "redirect:/";
				}
				int id = incident.getId();
				if(!verifySession(session, ""+id)){				
						addMessage("No more changes can be made ");
						addMessagesAndErrorsToSession(session);
						return "redirect:/index";
				}
        fraudService.save(fraud);
				addMessage("Saved Succefully");
				addMessagesAndErrorsToSession(session);
				return "redirect:/incident/"+incident.getId(); 
    }
    
    @GetMapping("/fraud/{id}")
    public String showFraud(@PathVariable("id") int id, Model model) {
				Fraud fraud = null;
				try{
						fraud = fraudService.findById(id);
				}catch(Exception ex){
						addError("Invalid fraud Id "+id);
						logger.error(" "+ex);
						model.addAttribute("errors", errors);
						return "index"; // need fix
				}
        model.addAttribute("fraud", fraud);				
				return "fraud";
    }
    //staff
    @GetMapping("/fraudView/{id}")
    public String viewFraud(@PathVariable("id") int id,
														 Model model,
														 HttpSession session) {
				Fraud fraud = null;
				User user = getUserFromSession(session);
				if(user == null){
						return "redirect:/login";
				}
				try{
						fraud = fraudService.findById(id);
				}catch(Exception ex){
						addError("Invalid fraud Id "+id);
						logger.error(" "+ex);
						model.addAttribute("errors", errors);
						return "staff/staff_intro"; 
				}
        model.addAttribute("fraud", fraud);				
				return "fraudView";
    }    
    
    @GetMapping("/fraud/edit/{id}")
    public String showEditForm(@PathVariable("id") int id,
															 Model model,
															 HttpSession session) {
				Fraud fraud = null;
				try{
						fraud = fraudService.findById(id);
	    
				}catch(Exception ex){
						addError("Invalid fraud Id "+id);
						logger.error(" "+ex);
						addMessagesAndErrorsToSession(session);	    
						return "redirect:/"; 
				}
				Incident incident = fraud.getIncident();
				if(incident == null || !incident.canBeChanged()){
						addMessage("no more changes can be made");
						addMessagesAndErrorsToSession(session);
						return "redirect:/";	    
				}
				int incident_id = incident.getId();
				if(!verifySession(session, ""+incident_id)){				
						addMessage("No more changes can be made ");
						addMessagesAndErrorsToSession(session);
						return "redirect:/";
				}
				List<FraudType> fraudTypes = fraudTypeService.getAll();
				model.addAttribute("fraud", fraud);
				model.addAttribute("fraudTypes", fraudTypes);
				handleErrorsAndMessages(model);
				return "fraudUpdate";
    }
    @PostMapping("/fraud/update/{id}")
    public String updateFraud(@PathVariable("id") int id,
															 @Valid Fraud fraud, 
															 BindingResult result,
															 Model model,
															 HttpSession session
															 ) {
				if (result.hasErrors()) {
						String error = Helper.extractErrors(result);
						addError(error);
						logger.error("Error update fraud "+error);
						fraud.setId(id);
						return "redirect:/fraud/edit/"+id;
	    
				}
				Incident incident = fraud.getIncident();
				if(incident == null || !incident.canBeChanged()){
						addMessage("no more changes can be made");
						addMessagesAndErrorsToSession(session);
						return "redirect:/";	    
				}
				int incident_id = incident.getId();
				if(!verifySession(session, ""+incident_id)){				
						addMessage("No more changes can be made ");
						addMessagesAndErrorsToSession(session);
						return "redirect:/";
				}							
				fraudService.update(fraud);
				addMessage("Updated Successfully");				
				addMessagesAndErrorsToSession(session);
				return "redirect:/incident/"+incident.getId();
    }
    
    @GetMapping("/fraud/delete/{id}")
    public String deleteFraud(@PathVariable("id") int id,
															 Model model,
															 HttpSession session
															 ) {
	
				Fraud fraud = null;
				Incident incident = null;
				try{
						fraud = fraudService.findById(id);
						if(fraud != null)
								incident = fraud.getIncident();
						if(incident == null || !incident.canBeChanged()){
								addMessage("no more changes can be made");
								addMessagesAndErrorsToSession(session);
								return "redirect:/";	    
						}
						int incident_id = incident.getId();
						if(!verifySession(session, ""+incident_id)){				
								addMessage("No more changes can be made ");
								addMessagesAndErrorsToSession(session);
								return "redirect:/";
						}									
						fraudService.delete(id);
						addMessage("Deleted Succefully");
				}catch(Exception ex){
						logger.error("Error delete fraud "+id+" "+ex);
						addError("Invalid fraud ID "+id);
				}
				addMessagesAndErrorsToSession(session);
				return "redirect:/incident/"+incident.getId();
	
    }
    
}
