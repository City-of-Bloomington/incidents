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
import in.bloomington.incident.service.DamageTypeService;
import in.bloomington.incident.model.DamageType;


@Controller
public class DamageTypeController extends TopController{

		@Autowired
		DamageTypeService damageTypeService;
		
		@GetMapping("/damageTypes")
    public String getAll(Model model) {
        model.addAttribute("types", damageTypeService.getAll());
        return "damageTypes";
    }
		@GetMapping("/damageType/new")
    public String newDamageType(Model model) {
				DamageType damageType = new DamageType();
        model.addAttribute("type", damageType);
        return "damageTypeAdd";
    }     
    @PostMapping("/damageType/add")
    public String addDamageType(@Valid DamageType damageType, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "addDamageType";
        }
        damageTypeService.save(damageType);
				addMessage("Added Successfully");
        model.addAttribute("types", damageTypeService.getAll());
				model.addAttribute("messages", messages);				
        return "damageTypes";
    }

		@GetMapping("/damageType/edit/{id}")
		public String showEditForm(@PathVariable("id") int id, Model model) {
				DamageType type = null;
				try{
						type = damageTypeService.findById(id);
						
				}catch(Exception ex){
						addError("Invalid damage type Id");
						model.addAttribute("types", damageTypeService.getAll());
						model.addAttribute("errors", errors);
						return "damageTypes";
				}
				model.addAttribute("type", type);
				if(hasErrors()){
						model.addAttribute("errors", errors);
				}
				return "dDamageTypeUpdate";
		}
		@PostMapping("/damageType/update/{id}")
		public String updateDamageType(@PathVariable("id") int id, @Valid DamageType type, 
														 BindingResult result, Model model) {
				if (result.hasErrors()) {
						type.setId(id);
						return "damageTypeUpdate";
				}
				addMessage("Updated Successfully");
				damageTypeService.update(type);
				model.addAttribute("types", damageTypeService.getAll());				
				model.addAttribute("messages", messages);
				return "damageTypes";
		}
		
		@GetMapping("/damageType/delete/{id}")
		public String deleteDamageType(@PathVariable("id") int id, Model model) {

				try{
						DamageType type = damageTypeService.findById(id);
						damageTypeService.delete(id);
						addMessage("Deleted Succefully");
				}catch(Exception ex){
						addError("Invalid damageType ID "+id);
				}
				model.addAttribute("types", damageTypeService.getAll());
				if(hasMessages()){
						model.addAttribute("messages", messages);
				}
				else if(hasErrors()){
						model.addAttribute("errors", errors);
				}
					 
				return "damageTypes";
		}
		
}
