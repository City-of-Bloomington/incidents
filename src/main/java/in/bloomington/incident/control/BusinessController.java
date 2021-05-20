package in.bloomington.incident.control;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Value;
import in.bloomington.incident.service.BusinessService;
import in.bloomington.incident.service.IncidentService;
import in.bloomington.incident.service.DamageTypeService;
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.Business;
import in.bloomington.incident.model.DamageType;
import in.bloomington.incident.utils.Helper;

@Controller
public class BusinessController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(BusinessController.class);
    @Autowired
    BusinessService businessService;
    @Autowired
    IncidentService incidentService;		

    @GetMapping("/business/add/{incident_id}")
    public String newBusiness(@PathVariable("incident_id") int incident_id,
															Model model,
															HttpSession session) {
				
				Business business = new Business();
				Incident incident = null;
				incident = incidentService.findById(incident_id);
				
				try{
						if(incident == null || !incident.canBeChanged()){
								addMessage("no more changes can be made");
								addMessagesAndErrorsToSession(session);
								return "redirect:/";	    
						}
						if(!verifySession(session, ""+incident_id)){				
								addMessage("No more changes can be made ");
								addMessagesAndErrorsToSession(session);
								return "redirect:/";
						}							
						business.setIncident(incident);
				}catch(Exception ex){
						addError("Invalid incident "+incident_id);
						logger.error(" "+ex);
				}
        model.addAttribute("business", business);
				if(hasErrors()){
						model.addAttribute("errors",errors);
				}
        return "businessAdd";
    }     
    @PostMapping("/business/save")
    public String addBusiness(@Valid Business business,
															BindingResult result,
															Model model,
															HttpSession session
															) {
        if (result.hasErrors()) {
						String error = Helper.extractErrors(result);
						addError(error);
						logger.error(error);
						handleErrorsAndMessages(model);
            return "businessAdd";
        }
				if(!business.verify()){
						String error = business.getErrorInfo();
						addError(error);
						logger.error(error);
						handleErrorsAndMessages(model);
						return "businessAdd";						
				}
				int incident_id = business.getIncident().getId();
				Incident incident = incidentService.findById(incident_id);
				if(incident == null || !incident.canBeChanged()){
						addMessage("no more changes can be made");
						addMessagesAndErrorsToSession(session);
						return "redirect:/";	    
				}
				if(!verifySession(session, ""+incident_id)){				
						addMessage("No more changes can be made ");
						addMessagesAndErrorsToSession(session);
						return "redirect:/";
				}				

        businessService.save(business);
				addMessage("Added Successfully");
				addMessagesAndErrorsToSession(session);
				return "redirect:/incident/"+incident_id;
    }

    @GetMapping("/business/edit/{id}")
    public String showEditForm(@PathVariable("id") int id,
															 Model model,
															 HttpSession session) {
				Business business = null;
				Incident incident = null;
				try{
						business = businessService.findById(id);
						incident = business.getIncident();
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
				}catch(Exception ex){
						addError("Invalid business "+id);
						logger.error(" "+ex);
						addMessagesAndErrorsToSession(session);
						return "redirect:/index";
				}
				model.addAttribute("business", business);
				handleErrorsAndMessages(model);
				return "businessUpdate";
    }
    @PostMapping("/business/update/{id}")
    public String updateBusiness(@PathVariable("id") int id, @Valid Business business, 
																 BindingResult result,
																 Model model,
																 HttpSession session) {
				if (result.hasErrors()) {
						String error = Helper.extractErrors(result);
						error += " Error update business "+id;
						addError(error);
						logger.error(error);
						business.setId(id);
						addMessagesAndErrorsToSession(session);
						return "redirect:/business/edit/"+id;
				}
				if(!business.verify()){
						String error = business.getErrorInfo();
						addError(error);
						logger.error(error);
						// model.addAttribute("properties", businessService.getAll());
						handleErrorsAndMessages(model);
						return "businessUpdate";						
				}
				Incident incident = business.getIncident();
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
				businessService.save(business);
				addMessage("Updated Successfully");
				addMessagesAndErrorsToSession(session);
				// need redirect to incident
				return "redirect:/incident/"+incident_id;
    }
		
    @GetMapping("/business/delete/{id}")
    public String deleteBusiness(@PathVariable("id") int id,
																 Model model,
																 HttpSession session) {

				Incident incident = null;
				try{
						Business business = businessService.findById(id);
						incident = business.getIncident();
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
						businessService.delete(id);
						addMessage("Deleted Succefully");
				}catch(Exception ex){
						addError("Error delete business "+id);
						logger.error(" "+ex);
				}
				addMessagesAndErrorsToSession(session);
				resetAll();
				return "redirect:/incident/"+incident.getId();

    }
    @GetMapping("/business/{id}")
    public String viewBusiness(@PathVariable("id") int id, Model model) {

				try{
						Business business = businessService.findById(id);
						model.addAttribute("business", business);						
				}catch(Exception ex){
						addError("Invalid business "+id);
						logger.error(" "+ex);
				}
				handleErrorsAndMessages(model);
				return "business";
    }
    @GetMapping("/businessView/{id}")
    public String businessView(@PathVariable("id") int id, Model model) {

				try{
						Business business = businessService.findById(id);
						model.addAttribute("business", business);						
				}catch(Exception ex){
						addError("Invalid business "+id);
						logger.error(" "+ex);
				}
				handleErrorsAndMessages(model);
				return "businessView";
    }	    
		
}
