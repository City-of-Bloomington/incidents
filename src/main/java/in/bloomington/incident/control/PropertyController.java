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
    // max total value of damaged properties claim allowed
    // default $2000.0 if not set in properties file
    @Value( "${incident.property.maxtotalvalue:2000.0}")		
    private Double maxTotalValue;				

    @GetMapping("/property/add/{incident_id}")
    public String newProperty(@PathVariable("incident_id") int incident_id,
			      Model model) {
				
	Property property = new Property();
	Incident incident = null;
	try{
	    incident = incidentService.findById(incident_id);
	    property.setIncident(incident);
	    property.setBalance(incident.getTotalValue());
	    property.setMaxTotalValue(maxTotalValue);
	}catch(Exception ex){
	    addError("Invalid incident "+incident_id);
	    logger.error(" "+ex);
	}
        model.addAttribute("property", property);
	List<DamageType> types = damageTypeService.getAll();
	if(types != null)
	    model.addAttribute("damageTypes", types);
	if(incident.isVehicleRequired() && !incident.hasVehicleList()){
	    model.addAttribute("vehicleRequired","true");
	}
	if(hasErrors()){
	    model.addAttribute("errors",errors);
	}
        return "propertyAdd";
    }     
    @PostMapping("/property/save")
    public String addProperty(@Valid Property property,
			      BindingResult result,
			      Model model,
			      HttpSession session
			      ) {
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
	    List<DamageType> types = damageTypeService.getAll();
	    if(types != null)
		model.addAttribute("damageTypes", types);
	    handleErrorsAndMessages(model);
	    return "propertyAdd";						
	}				
        propertyService.save(property);
	addMessage("Added Successfully");
	addMessagesAndErrorsToSession(session);
	int incident_id = property.getIncident().getId();
	Incident incident = incidentService.findById(incident_id);
	//
	// next add vehicle if required
	if(incident.isVehicleRequired() && !incident.hasVehicleList()){
	    return "redirect:/vehicle/add/"+incident_id;
	}
	return "redirect:/incident/"+incident_id;
    }

    @GetMapping("/property/edit/{id}")
    public String showEditForm(@PathVariable("id") int id,
			       Model model,
			       HttpSession session) {
	Property property = null;
	try{
	    property = propertyService.findById(id);
	    Incident incident = property.getIncident();
	    if(incident != null){
		property.setBalance(incident.getTotalValue());
	    }
	    property.setMaxTotalValue(maxTotalValue);
	}catch(Exception ex){
	    addError("Invalid property "+id);
	    logger.error(" "+ex);
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/index";
	}
	model.addAttribute("property", property);
	List<DamageType> types = damageTypeService.getAll();
	if(types != null)
	    model.addAttribute("damageTypes", types);
	handleErrorsAndMessages(model);
	return "propertyUpdate";
    }
    @PostMapping("/property/update/{id}")
    public String updateProperty(@PathVariable("id") int id, @Valid Property property, 
				 BindingResult result,
				 Model model,
				 HttpSession session) {
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
	    model.addAttribute("properties", propertyService.getAll());
	    handleErrorsAndMessages(model);
	    return "propertyUpdate";						
	}
	propertyService.save(property);
	addMessage("Updated Successfully");
	Incident incident = property.getIncident();
	int incident_id = incident.getId();
	addMessagesAndErrorsToSession(session);
	// need redirect to incident
	return "redirect:/incident/"+incident_id;
    }
		
    @GetMapping("/property/delete/{id}")
    public String deleteProperty(@PathVariable("id") int id,
				 Model model,
				 HttpSession session) {

	Incident incident = null;
	try{
	    Property property = propertyService.findById(id);
	    incident = property.getIncident();
	    propertyService.delete(id);
	    addMessage("Deleted Succefully");
	}catch(Exception ex){
	    addError("Error delete property "+id);
	    logger.error(" "+ex);
	}
	addMessagesAndErrorsToSession(session);
	resetAll();
	return "redirect:/incident/"+incident.getId();

    }
    @GetMapping("/property/{id}")
    public String viewProperty(@PathVariable("id") int id, Model model) {

	try{
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
