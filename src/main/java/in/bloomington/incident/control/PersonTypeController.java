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
import in.bloomington.incident.service.PersonTypeService;
import in.bloomington.incident.model.PersonType;
import in.bloomington.incident.utils.Helper;

@Controller
public class PersonTypeController extends TopController{

		@Autowired
		PersonTypeService personTypeService;
		
		@GetMapping("/personTypes")
    public String getAll(Model model) {
        model.addAttribute("types", personTypeService.getAll());
        return "personTypes";
    }
		@GetMapping("/personType/new")
    public String newPersonType(Model model) {
				PersonType personType = new PersonType();
        model.addAttribute("type", personType);
        return "personTypeAdd";
    }     
    @PostMapping("/personType/add")
    public String addPersonType(@Valid PersonType personType, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "addPersonType";
        }
        personTypeService.save(personType);
				addMessage("Added Successfully");
        model.addAttribute("types", personTypeService.getAll());
				model.addAttribute("messages", messages);				
        return "personTypes";
    }

		@GetMapping("/personType/edit/{id}")
		public String showEditForm(@PathVariable("id") int id, Model model) {
				PersonType type = null;
				try{
						type = personTypeService.findById(id);
						
				}catch(Exception ex){
						addError("Invalid person type Id");
						model.addAttribute("types", personTypeService.getAll());
						model.addAttribute("errors", errors);
						return "personTypes";
				}
				model.addAttribute("type", type);
				if(hasMessages()){
						model.addAttribute("messages", messages);				
				}
				return "personTypeUpdate";
		}
		@PostMapping("/personType/update/{id}")
		public String updatePersonType(@PathVariable("id") int id, @Valid PersonType type, 
														 BindingResult result, Model model) {
				if (result.hasErrors()) {
						String error = Helper.extractErrors(result);
						addError(error);
						type.setId(id);
						return "personTypeUpdate";
				}
				addMessage("Updated Successfully");
				personTypeService.save(type);
				model.addAttribute("types", personTypeService.getAll());				
				model.addAttribute("messages", messages);
				return "personTypes";
		}
		
		@GetMapping("/personType/delete/{id}")
		public String deletePersonType(@PathVariable("id") int id, Model model) {

				try{
						PersonType type = personTypeService.findById(id);
						personTypeService.delete(id);
						addMessage("Deleted Succefully");
				}catch(Exception ex){
						addError("Invalid personType ID "+id);
				}
				model.addAttribute("types", personTypeService.getAll());
				if(hasMessages()){
						model.addAttribute("messages", messages);
				}
				else if(hasErrors()){
						model.addAttribute("errors", errors);
				}
					 
				return "personTypes";
		}
		
}
