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
import in.bloomington.incident.service.IncidentTypeService;
import in.bloomington.incident.model.IncidentType;


@Controller
public class IncidentTypeController extends TopController{

		@Autowired
		IncidentTypeService incidentTypeService;
		
		@GetMapping("/incidentTypes")
    public String getAll(Model model) {
        model.addAttribute("types", incidentTypeService.getAll());
        return "incidentTypes";
    }
		@GetMapping("/incidentType/new")
    public String newIncidentType(Model model) {
				IncidentType incidentType = new IncidentType();
        model.addAttribute("type", incidentType);
        return "incidentTypeAdd";
    }     
    @PostMapping("/incidentType/add")
    public String addIncidentType(@Valid IncidentType incidentType, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "addIncidentType";
        }
        incidentTypeService.save(incidentType);
				addMessage("Added Successfully");
        model.addAttribute("types", incidentTypeService.getAll());
				model.addAttribute("messages", messages);				
        return "incidentTypes";
    }

		@GetMapping("/incidentType/edit/{id}")
		public String showEditForm(@PathVariable("id") int id, Model model) {
				IncidentType type = null;
				try{
						type = incidentTypeService.findById(id);
						
				}catch(Exception ex){
						addError("Invalid incident type Id");
						model.addAttribute("types", incidentTypeService.getAll());
						model.addAttribute("errors", errors);
						return "incidentTypes";
				}
				model.addAttribute("type", type);
				return "incidentTypeUpdate";
		}
		@PostMapping("/incidentType/update/{id}")
		public String updateIncidentType(@PathVariable("id") int id, @Valid IncidentType type, 
														 BindingResult result, Model model) {
				if (result.hasErrors()) {
						type.setId(id);
						return "incidentTypeUpdate";
				}
				addMessage("Updated Successfully");
				incidentTypeService.save(type);
				model.addAttribute("types", incidentTypeService.getAll());				
				model.addAttribute("messages", messages);
				return "incidentTypes";
		}
		
		@GetMapping("/incidentType/delete/{id}")
		public String deleteIncidentType(@PathVariable("id") int id, Model model) {

				try{
						IncidentType type = incidentTypeService.findById(id);
						incidentTypeService.delete(id);
						addMessage("Deleted Succefully");
				}catch(Exception ex){
						addError("Invalid incidentType ID "+id);
				}
				model.addAttribute("types", incidentTypeService.getAll());
				if(hasMessages()){
						model.addAttribute("messages", messages);
				}
				else if(hasErrors()){
						model.addAttribute("errors", errors);
				}
					 
				return "incidentTypes";
		}
		
}
