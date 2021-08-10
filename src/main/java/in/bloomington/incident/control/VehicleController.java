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
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Value;
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
		@Autowired 
    private HttpSession session;	
		
    private Environment env;		
    // max total value of damaged properties claim allowed
    // default $2000.0 if not set in properties file
    @Value( "${incident.property.maxtotalvalue:2000.0}")		
    private Double maxTotalValue;
		
    @GetMapping("/vehicle/add/{incident_id}")
    public String newVehicle(@PathVariable("incident_id") int incident_id,
														 Model model
														 ) {
				
				Vehicle vehicle = new Vehicle();
				Incident incident = null;
				try{
						incident = incidentService.findById(incident_id);
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
						vehicle.setBalance(incident.getTotalValue());
						vehicle.setMaxTotalValue(maxTotalValue);	    
						vehicle.setIncident(incident);
				}catch(Exception ex){
						addError("Invalid incident "+incident_id);
						logger.error(" "+ex);
						model.addAttribute("errors", errors);
						return "redirect:/";
				}				
        model.addAttribute("vehicle", vehicle);
				List<CarDamageType> types = damageTypeService.getAll();
				if(types != null)
						model.addAttribute("damageTypes", types);					
        return "vehicleAdd";
    }     
    @PostMapping("/vehicle/save")
    public String addVehicle(@RequestParam String action,
														 @Valid Vehicle vehicle,
														 BindingResult result,
														 Model model
														 ) {
        if (result.hasErrors()) {
						String error = Helper.extractErrors(result);						
						addError("Error new add vehicle "+error);
						logger.error(error);
            return "vehicleAdd";
        }
				if(!vehicle.verify()){
						String error = vehicle.getErrorInfo();
						addError(error);
						logger.error(error);
						List<CarDamageType> types = damageTypeService.getAll();
						if(types != null)
								model.addAttribute("damageTypes", types);
						model.addAttribute("vehicle", vehicle);
						handleErrorsAndMessages(model);
						return "vehicleAdd";						
				}
				int incident_id = vehicle.getIncident().getId();
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
        vehicleService.save(vehicle);
				addMessage("Added Successfully");
				addMessagesAndErrorsToSession(session);
				if(!action.equals("Next")){ // more
						return "redirect:/vehicle/add/"+incident_id;						
				}				
				if(incident.isBusinessRelated()){
						return "redirect:/businessIncident/"+incident_id;
				}
				return "redirect:/incident/"+incident_id;
    }

    @GetMapping("/vehicle/edit/{id}")
    public String showEditForm(@PathVariable("id") int id,
															 Model model
															 ) {
				Vehicle vehicle = null;
				try{
						vehicle = vehicleService.findById(id);
						Incident incident = vehicle.getIncident();
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
						vehicle.setBalance(incident.getTotalValue());
						vehicle.setMaxTotalValue(maxTotalValue);
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
																Model model
																) {
				if (result.hasErrors()) {
						String error = Helper.extractErrors(result);
						addError("Error update vehicle "+error);
						logger.error(error);
						return "reditect:/error";
				}
				if(!vehicle.verify()){
						String error = vehicle.getErrorInfo();
						addError(error);
						logger.error(error);
						List<CarDamageType> types = damageTypeService.getAll();
						if(types != null)
								model.addAttribute("damageTypes", types);
						model.addAttribute("vehicle", vehicle);
						handleErrorsAndMessages(model);
						return "vehicleUpdate";						
				}
				Incident incident = vehicle.getIncident();
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
				vehicleService.save(vehicle);	
				addMessage("Updated Successfully");
				addMessagesAndErrorsToSession(session);
				if(incident.isBusinessRelated()){
						return "redirect:/businessIncident/"+incident_id;
				}
				return "redirect:/incident/"+incident_id;
    }
		
    @GetMapping("/vehicle/delete/{id}")
    public String deleteVehicle(@PathVariable("id") int id,
																Model model
																) {

				Incident incident = null;
				try{
						Vehicle vehicle = vehicleService.findById(id);
						incident = vehicle.getIncident();
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
						vehicleService.delete(id);
						addMessage("Deleted Succefully");
						addMessagesAndErrorsToSession(session);
				}catch(Exception ex){
						addError("Error delete vehicle "+id);						
						logger.error(" "+ex);
				}
				if(incident.isBusinessRelated()){
						return "redirect:/businessIncident/"+incident.getId();
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
