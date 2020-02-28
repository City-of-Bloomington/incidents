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
import in.bloomington.incident.service.RoleService;
import in.bloomington.incident.model.Role;
import in.bloomington.incident.service.ActionService;
import in.bloomington.incident.model.Action;



@Controller
public class RoleController {

		@Autowired
		RoleService roleService;
		@Autowired
		ActionService actionService;
		
		String errors="", messages="";
		@GetMapping("/roles")
    public String getAll(Model model) {
        model.addAttribute("roles", roleService.getAll());
        return "roles";
    }
		@GetMapping("/role/new")
    public String newRole(Model model) {
				Role role = new Role();
        model.addAttribute("role", role);
				List<Action> actions = actionService.getAll();
				if(actions != null)
						model.addAttribute("actions", actions);
        return "roleAdd";
    }     
    @PostMapping("/role/add")
    public String addRole(@Valid Role role, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "roleAdd";
        }
        roleService.save(role);
				messages = "Added Successfully";
        model.addAttribute("roles", roleService.getAll());
				model.addAttribute("messages", messages);				
        return "roles";
    }

		@GetMapping("/role/edit/{id}")
		public String showEditForm(@PathVariable("id") int id, Model model) {
				Role role = null;
				try{
						role = roleService.findById(id);
						
				}catch(Exception ex){
						errors += "Invalid role Id";
						model.addAttribute("roles", roleService.getAll());
						model.addAttribute("errors", errors);
						return "roles";
				}
				model.addAttribute("role", role);
				List<Action> actions = actionService.getAll();
				if(actions != null)
						model.addAttribute("actions", actions);
				return "roleUpdate";
		}
		@PostMapping("/role/update/{id}")
		public String updateRole(@PathVariable("id") int id, @Valid Role role, 
														 BindingResult result, Model model) {
				if (result.hasErrors()) {
						role.setId(id);
						return "roleUpdate";
				}
				messages = "Updated Successfully";
				roleService.save(role);
				model.addAttribute("roles", roleService.getAll());				
				model.addAttribute("messages", messages);
				return "roles";
		}
		
		@GetMapping("/role/delete/{id}")
		public String deleteRole(@PathVariable("id") int id, Model model) {

				try{
						Role role = roleService.findById(id);
						roleService.delete(id);
						messages = "Deleted Succefully";
				}catch(Exception ex){
						errors += "Invalid role ID "+id;
				}
				model.addAttribute("roles", roleService.getAll());
				if(!messages.equals("")){
						model.addAttribute("messages", messages);
				}
				else if(!errors.equals("")){
						model.addAttribute("errors", errors);
				}
					 
				return "roles";
		}
		
}
