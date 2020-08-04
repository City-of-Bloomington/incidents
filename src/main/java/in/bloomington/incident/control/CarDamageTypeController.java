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
import in.bloomington.incident.service.CarDamageTypeService;
import in.bloomington.incident.model.CarDamageType;


@Controller
public class CarDamageTypeController extends TopController{

		@Autowired
		CarDamageTypeService carDamageTypeService;
		
		@GetMapping("/carDamageTypes")
    public String getAll(Model model) {
        model.addAttribute("types", carDamageTypeService.getAll());
        return "staff/carDamageTypes";
    }
		@GetMapping("/carDamageType/new")
    public String newDamageType(Model model) {
				CarDamageType type = new CarDamageType();
        model.addAttribute("type", type);
        return "staff/carDamageTypeAdd";
    }     
    @PostMapping("/carDamageType/add")
    public String addDamageType(@Valid CarDamageType carDamageType, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "staff/addCarDamageType";
        }
        carDamageTypeService.save(carDamageType);
				addMessage("Added Successfully");
        model.addAttribute("types", carDamageTypeService.getAll());
				model.addAttribute("messages", messages);				
        return "staff/carDamageTypes";
    }

		@GetMapping("/carDamageType/edit/{id}")
		public String showEditForm(@PathVariable("id") int id, Model model) {
				CarDamageType type = null;
				try{
						type = carDamageTypeService.findById(id);
						
				}catch(Exception ex){
						addError("Invalid carDamage type Id");
						model.addAttribute("types", carDamageTypeService.getAll());
						model.addAttribute("errors", errors);
						return "staff/carDamageTypes";
				}
				model.addAttribute("type", type);
				if(hasMessages()){
						model.addAttribute("messages", messages);		
				}
				return "staff/carDamageTypeUpdate";
		}
		@PostMapping("/carDamageType/update/{id}")
		public String updateCarDamageType(@PathVariable("id") int id, @Valid CarDamageType type, 
														 BindingResult result, Model model) {
				if (result.hasErrors()) {
						type.setId(id);
						return "staff/carDamageTypeUpdate";
				}
				addMessage("Updated Successfully");
				carDamageTypeService.update(type);
				model.addAttribute("types", carDamageTypeService.getAll());				
				model.addAttribute("messages", messages);
				return "staff/carDamageTypes";
		}
		
		@GetMapping("/carDamageType/delete/{id}")
		public String deleteCarDamageType(@PathVariable("id") int id, Model model) {

				try{
						CarDamageType type = carDamageTypeService.findById(id);
						carDamageTypeService.delete(id);
						addMessage("Deleted Successfully");
				}catch(Exception ex){
						addError("Invalid damage type ID "+id);
				}
				model.addAttribute("types", carDamageTypeService.getAll());
				if(hasMessages()){
						model.addAttribute("messages", messages);
				}
				else if(hasErrors()){
						model.addAttribute("errors", errors);
				}
					 
				return "staff/carDamageTypes";
		}
		
}
