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
import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import in.bloomington.incident.service.VehicleService;
import in.bloomington.incident.service.IncidentService;
import in.bloomington.incident.service.CarDamageTypeService;
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.Vehicle;
import in.bloomington.incident.model.CarDamageType;
import in.bloomington.incident.utils.Helper;

@Controller
public class VehicleController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(VehicleController.class);		
    @Autowired
    VehicleService vehicleService;
    @Autowired
    IncidentService incidentService;		
    @Autowired
    CarDamageTypeService damageTypeService;
		
    @GetMapping("/vehicle/add/{incident_id}")
    public String newVehicle(@PathVariable("incident_id") int incident_id, Model model) {
				
	Vehicle vehicle = new Vehicle();
	try{
	    Incident incident = incidentService.findById(incident_id);
	    vehicle.setIncident(incident);
	}catch(Exception ex){
	    addError("Invalid incident "+incident_id);
	    logger.error(" "+ex);
	    model.addAttribute("errors", errors);
	    return "redirect:/start";
	}				
        model.addAttribute("vehicle", vehicle);
	List<CarDamageType> types = damageTypeService.getAll();
	if(types != null)
	    model.addAttribute("damageTypes", types);					
        return "vehicleAdd";
    }     
    @PostMapping("/vehicle/save")
    public String addVehicle(@Valid Vehicle vehicle, BindingResult result,
			     Model model,
			     HttpSession session
			     ) {
        if (result.hasErrors()) {
	    String error = Helper.extractErrors(result);						
	    addError("Error new add vehicle "+error);
	    logger.error(error);
            return "vehicleAdd";
        }
        vehicleService.save(vehicle);
	addMessage("Added Successfully");
	addMessagesAndErrorsToSession(session);
	int incident_id = vehicle.getIncident().getId();
	return "redirect:/incident/"+incident_id;
    }

    @GetMapping("/vehicle/edit/{id}")
    public String showEditForm(@PathVariable("id") int id, Model model) {
	Vehicle vehicle = null;
	try{
	    vehicle = vehicleService.findById(id);
						
	}catch(Exception ex){
	    addError("Invalid vehicle Id "+id);
	    logger.error(" "+ex);
	    model.addAttribute("errors", errors);
	    return "redirect:/index";
	}
	model.addAttribute("vehicle", vehicle);
	List<CarDamageType> types = damageTypeService.getAll();
	if(types != null)
	    model.addAttribute("damageTypes", types);					
	return "vehicleUpdate";
    }
    @PostMapping("/vehicle/update")
    public String updateVehicle(@Valid Vehicle vehicle, 
				BindingResult result,
				Model model,
				HttpSession session
				) {
	if (result.hasErrors()) {
	    String error = Helper.extractErrors(result);
	    addError("Error update vehicle "+error);
	    logger.error(error);
	    return "reditect:/error";
	}
	addMessage("Updated Successfully");
	vehicleService.save(vehicle);
	Incident incident = vehicle.getIncident();
	int incident_id = incident.getId();
	addMessagesAndErrorsToSession(session);
	return "redirect:/incident/"+incident_id;
    }
		
    @GetMapping("/vehicle/delete/{id}")
    public String deleteVehicle(@PathVariable("id") int id,
				Model model,
				HttpSession session
				) {

	Incident incident = null;
	try{
	    Vehicle vehicle = vehicleService.findById(id);
	    incident = vehicle.getIncident();
	    vehicleService.delete(id);
	    addMessage("Deleted Succefully");
	    addMessagesAndErrorsToSession(session);
	}catch(Exception ex){
	    addError("Error delete vehicle "+id);						
	    logger.error(" "+ex);
	}
	return "redirect:/incident/"+incident.getId();

    }
    @GetMapping("/vehicle/{id}")
    public String viewVehicle(@PathVariable("id") int id, Model model) {

	try{
	    Vehicle vehicle = vehicleService.findById(id);
	    model.addAttribute("vehicle", vehicle);						
	}catch(Exception ex){
	    addError("Invalid vehicle ID "+id);
	    logger.error(" "+ex);
	}
	return "vehicle";

    }
    //login staff
    @GetMapping("/vehicleView/{id}")
    public String vehicleView(@PathVariable("id") int id, Model model) {

	try{
	    Vehicle vehicle = vehicleService.findById(id);
	    model.addAttribute("vehicle", vehicle);						
	}catch(Exception ex){
	    addError("Invalid vehicle ID "+id);
	    logger.error(" "+ex);
	}
	return "vehicleView";

    }	    
		
}
