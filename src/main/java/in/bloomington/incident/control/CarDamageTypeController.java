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
public class CarDamageTypeController {

		@Autowired
		CarDamageTypeService carDamageTypeService;
		
		String errors="", messages="";
		@GetMapping("/carDamageTypes")
    public String getAll(Model model) {
        model.addAttribute("types", carDamageTypeService.getAll());
        return "carDamageTypes";
    }
		@GetMapping("/carDamageType/new")
    public String newDamageType(Model model) {
				CarDamageType type = new CarDamageType();
        model.addAttribute("type", type);
        return "carDamageTypeAdd";
    }     
    @PostMapping("/carDamageType/add")
    public String addDamageType(@Valid CarDamageType carDamageType, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "addCarDamageType";
        }
        carDamageTypeService.save(carDamageType);
				messages = "Added Successfully";
        model.addAttribute("types", carDamageTypeService.getAll());
				model.addAttribute("messages", messages);				
        return "carDamageTypes";
    }

		@GetMapping("/carDamageType/edit/{id}")
		public String showEditForm(@PathVariable("id") int id, Model model) {
				CarDamageType type = null;
				try{
						type = carDamageTypeService.findById(id);
						
				}catch(Exception ex){
						errors += "Invalid carDamage type Id";
						model.addAttribute("types", carDamageTypeService.getAll());
						model.addAttribute("errors", errors);
						return "carDamageTypes";
				}
				model.addAttribute("type", type);
				return "carDamageTypeUpdate";
		}
		@PostMapping("/carDamageType/update/{id}")
		public String updateCarDamageType(@PathVariable("id") int id, @Valid CarDamageType type, 
														 BindingResult result, Model model) {
				if (result.hasErrors()) {
						type.setId(id);
						return "carDamageTypeUpdate";
				}
				messages = "Updated Successfully";
				carDamageTypeService.update(type);
				model.addAttribute("types", carDamageTypeService.getAll());				
				model.addAttribute("messages", messages);
				return "carDamageTypes";
		}
		
		@GetMapping("/carDamageType/delete/{id}")
		public String deleteCarDamageType(@PathVariable("id") int id, Model model) {

				try{
						CarDamageType type = carDamageTypeService.findById(id);
						carDamageTypeService.delete(id);
						messages = "Deleted Successfully";
				}catch(Exception ex){
						errors += "Invalid car damage type ID "+id;
				}
				model.addAttribute("types", carDamageTypeService.getAll());
				if(!messages.equals("")){
						model.addAttribute("messages", messages);
				}
				else if(!errors.equals("")){
						model.addAttribute("errors", errors);
				}
					 
				return "carDamageTypes";
		}
		
}
