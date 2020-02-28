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


@Controller
public class PersonTypeController {

		@Autowired
		PersonTypeService personTypeService;
		
		String errors="", messages="";
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
				messages = "Added Successfully";
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
						errors += "Invalid person type Id";
						model.addAttribute("types", personTypeService.getAll());
						model.addAttribute("errors", errors);
						return "personTypes";
				}
				model.addAttribute("type", type);
				return "personTypeUpdate";
		}
		@PostMapping("/personType/update/{id}")
		public String updatePersonType(@PathVariable("id") int id, @Valid PersonType type, 
														 BindingResult result, Model model) {
				if (result.hasErrors()) {
						type.setId(id);
						return "personTypeUpdate";
				}
				messages = "Updated Successfully";
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
						messages = "Deleted Succefully";
				}catch(Exception ex){
						errors += "Invalid personType ID "+id;
				}
				model.addAttribute("types", personTypeService.getAll());
				if(!messages.equals("")){
						model.addAttribute("messages", messages);
				}
				else if(!errors.equals("")){
						model.addAttribute("errors", errors);
				}
					 
				return "personTypes";
		}
		
}
