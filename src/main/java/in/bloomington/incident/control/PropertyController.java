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
import in.bloomington.incident.service.PropertyService;
import in.bloomington.incident.service.IncidentService;
import in.bloomington.incident.service.DamageTypeService;
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.Property;
import in.bloomington.incident.model.DamageType;
import in.bloomington.incident.utils.Helper;

@Controller
public class PropertyController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(PropertyController.class);
    @Autowired
    PropertyService propertyService;
    @Autowired
    IncidentService incidentService;		
    @Autowired
    DamageTypeService damageTypeService;
    @Autowired
    private Environment env;
    @Autowired 
    private HttpSession session;	
    // max total value of damaged properties claim allowed
    // default 0 if not set in properties file, 0 means no limit
    // to set a limit add a value such as 1000.0
    @Value( "${incident.property.maxtotalvalue:0}")		
    private Double maxTotalValue;				

    @GetMapping("/property/add/{incident_id}")
    public String newProperty(@PathVariable("incident_id") int incident_id,
			      Model model
			      ) {
				
	Property property = new Property();
	Incident incident = null;
	model.addAttribute("app_url", app_url);
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
	    property.setIncident(incident);
	    property.setBalance(incident.getTotalValue());
	    property.setMaxTotalValue(maxTotalValue);
	}catch(Exception ex){
	    addError("Invalid incident "+incident_id);
	    logger.error(" "+ex);
	}
        model.addAttribute("property", property);
	if(incident.isNotLostRelated()){
	    List<DamageType> types = damageTypeService.getActiveOnly();
	    if(types != null)
		model.addAttribute("damageTypes", types);
	}
	if(incident.isVehicleRequired() && !incident.hasVehicleList()){
	    model.addAttribute("vehicleRequired","true");
	}
	if(hasErrors()){
	    model.addAttribute("errors",errors);
	}
        return "propertyAdd";
    }     
    @PostMapping("/property/save")
    public String addProperty(@RequestParam String action,
			      @Valid Property property,
			      BindingResult result,
			      Model model
			      ) {
	model.addAttribute("app_url", app_url);
        if (result.hasErrors()) {
	    String error = Helper.extractErrors(result);
	    addError(error);
	    logger.error(error);
	    handleErrorsAndMessages(model);
            return "propertyAdd";
        }
	if(!property.verify()){
	    String error = property.getErrorInfo();
	    addError(error);
	    logger.error(error);
	    List<DamageType> types = damageTypeService.getActiveOnly();
	    if(types != null)
		model.addAttribute("damageTypes", types);
	    handleErrorsAndMessages(model);
	    return "propertyAdd";						
	}
	int incident_id = property.getIncident().getId();
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

        propertyService.save(property);
	addMessage("Added Successfully");
	addMessagesAndErrorsToSession(session);
	if(!action.equals("Next")){ // more
	    return "redirect:/property/add/"+incident_id;						
	}
	return "redirect:/incident/"+incident_id;
    }

    @GetMapping("/property/edit/{id}")
    public String showEditForm(@PathVariable("id") int id,
			       Model model
			       ) {
	Property property = null;
	Incident incident = null;
	model.addAttribute("app_url", app_url);
	try{
	    property = propertyService.findById(id);
	    incident = property.getIncident();
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
	    property.setBalance(incident.getTotalValue());
	    property.setMaxTotalValue(maxTotalValue);
	}catch(Exception ex){
	    addError("Invalid property "+id);
	    logger.error(" "+ex);
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/index";
	}
	model.addAttribute("property", property);
	if(incident.isNotLostRelated()){
	    List<DamageType> types = damageTypeService.getActiveOnly();
	    if(types != null)
		model.addAttribute("damageTypes", types);
	}
	handleErrorsAndMessages(model);
	return "propertyUpdate";
    }
    @PostMapping("/property/update/{id}")
    public String updateProperty(@PathVariable("id") int id, @Valid Property property, 
				 BindingResult result,
				 Model model
				 ) {
	model.addAttribute("app_url", app_url);
	if (result.hasErrors()) {
	    String error = Helper.extractErrors(result);
	    error += " Error update property "+id;
	    addError(error);
	    logger.error(error);
	    property.setId(id);
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/property/edit/"+id;
	}
	if(!property.verify()){
	    String error = property.getErrorInfo();
	    addError(error);
	    logger.error(error);
	    // model.addAttribute("properties", propertyService.getAll());
	    handleErrorsAndMessages(model);
	    return "propertyUpdate";						
	}
	Incident incident = property.getIncident();
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
	propertyService.save(property);
	addMessage("Updated Successfully");
	addMessagesAndErrorsToSession(session);
	// need redirect to incident
	if(incident.isBusinessRelated()){
	    return "redirect:/businessIncident/"+incident_id;
	}
	return "redirect:/incident/"+incident_id;
    }
		
    @GetMapping("/property/delete/{id}")
    public String deleteProperty(@PathVariable("id") int id,
				 Model model
				 ) {

	Incident incident = null;
	int incident_id=0;
	model.addAttribute("app_url", app_url);
	try{
	    Property property = propertyService.findById(id);
	    incident = property.getIncident();
	    if(incident == null || !incident.canBeChanged()){
		addMessage("no more changes can be made");
		addMessagesAndErrorsToSession(session);
		return "redirect:/";	    
	    }
	    incident_id = incident.getId();
	    if(!verifySession(session, ""+incident_id)){				
		addMessage("No more changes can be made ");
		addMessagesAndErrorsToSession(session);
		return "redirect:/";
	    }				
	    propertyService.delete(id);
	    addMessage("Deleted Succefully");
	}catch(Exception ex){
	    addError("Error delete property "+id);
	    logger.error(" "+ex);
	}
	addMessagesAndErrorsToSession(session);
	resetAll();
	if(incident.isBusinessRelated()){
	    return "redirect:/businessIncident/"+incident_id;
	}
	return "redirect:/incident/"+incident.getId();

    }
    @GetMapping("/property/{id}")
    public String viewProperty(@PathVariable("id") int id, Model model) {

	try{
	    model.addAttribute("app_url", app_url);
	    Property property = propertyService.findById(id);
	    model.addAttribute("property", property);						
	}catch(Exception ex){
	    addError("Invalid property "+id);
	    logger.error(" "+ex);
	}
	handleErrorsAndMessages(model);
	return "property";
    }
    @GetMapping("/propertyView/{id}")
    public String propertyView(@PathVariable("id") int id, Model model) {

	try{
	    model.addAttribute("app_url", app_url);
	    Property property = propertyService.findById(id);
	    model.addAttribute("property", property);						
	}catch(Exception ex){
	    addError("Invalid property "+id);
	    logger.error(" "+ex);
	}
	handleErrorsAndMessages(model);
	return "propertyView";
    }	    
		
}
