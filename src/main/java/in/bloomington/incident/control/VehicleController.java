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
// import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import in.bloomington.incident.service.VehicleService;
import in.bloomington.incident.service.IncidentService;
import in.bloomington.incident.service.CarDamageTypeService;
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.Vehicle;
import in.bloomington.incident.model.CarDamageType;


@Controller
public class VehicleController {

		@Autowired
		VehicleService vehicleService;
		@Autowired
		IncidentService incidentService;		
		@Autowired
		CarDamageTypeService damageTypeService;
		
		String errors="", messages="";
		@GetMapping("/vehicles")
    public String getAll(Model model) {
        model.addAttribute("vehicles", vehicleService.getAll());

        return "vehicles";
    }
		@GetMapping("/vehicle/add/{incident_id}")
    public String newVehicle(@PathVariable("incident_id") int incident_id, Model model) {
				
				Vehicle vehicle = new Vehicle();
				Incident incident = incidentService.findById(incident_id);
				vehicle.setIncident(incident);
        model.addAttribute("vehicle", vehicle);
				List<CarDamageType> types = damageTypeService.getAll();
				if(types != null)
		        model.addAttribute("damageTypes", types);					
        return "vehicleAdd";
    }     
    @PostMapping("/vehicle/save")
    public String addVehicle(@Valid Vehicle vehicle, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "vehicleAdd";
        }
        vehicleService.save(vehicle);
				messages = "Added Successfully";
				int incident_id = vehicle.getIncident().getId();
				return "redirect:/incident/"+incident_id;
    }

		@GetMapping("/vehicle/edit/{id}")
		public String showEditForm(@PathVariable("id") int id, Model model) {
				Vehicle vehicle = null;
				try{
						vehicle = vehicleService.findById(id);
						
				}catch(Exception ex){
						errors += "Invalid vehicle Id "+id;
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
														 BindingResult result, Model model) {
				if (result.hasErrors()) {
						return "reditect:/errors";
				}
				messages = "Updated Successfully";
				vehicleService.save(vehicle);
				Incident incident = vehicle.getIncident();
				int incident_id = incident.getId();
				// need redirect to incident
				model.addAttribute("messages", messages);
				return "redirect:/incident/"+incident_id;
		}
		
		@GetMapping("/vehicle/delete/{id}")
		public String deleteVehicle(@PathVariable("id") int id, Model model) {

				Incident incident = null;
				try{
						Vehicle vehicle = vehicleService.findById(id);
						incident = vehicle.getIncident();
						vehicleService.delete(id);
						messages = "Deleted Succefully";
				}catch(Exception ex){
						errors += "Invalid vehicle ID "+id;
				}
				model.addAttribute("properties", vehicleService.getAll());
				if(!messages.equals("")){
						model.addAttribute("messages", messages);
				}
				else if(!errors.equals("")){
						model.addAttribute("errors", errors);
				}
				return "redirect:/incident/"+incident.getId();

		}
		@GetMapping("/vehicle/{id}")
		public String viewVehicle(@PathVariable("id") int id, Model model) {

				try{
						Vehicle vehicle = vehicleService.findById(id);
						model.addAttribute("vehicle", vehicle);						
				}catch(Exception ex){
						errors += "Invalid vehicle ID "+id;
				}
				return "vehicle";

		}		
		
}
