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
import in.bloomington.incident.service.UserService;
import in.bloomington.incident.service.RoleService;
import in.bloomington.incident.model.User;
import in.bloomington.incident.model.Role;


@Controller
public class UserController {

		@Autowired
		UserService userService;
		@Autowired
		RoleService roleService;
		
		String errors="", messages="";
		@GetMapping("/users")
    public String getAll(Model model) {
        model.addAttribute("users", userService.getAll());
        return "users";
    }
		@GetMapping("/user/new")
    public String newUser(Model model) {
				User user = new User();
        model.addAttribute("user", user);
				List<Role> roles = roleService.getAll();
				if(roles != null)
						model.addAttribute("roles", roles);						
        return "userAdd";
    }     
    @PostMapping("/user/add")
    public String addUser(@Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "userAdd";
        }
        userService.save(user);
				messages = "Added Successfully";
        model.addAttribute("users", userService.getAll());
				model.addAttribute("messages", messages);				
        return "users";
    }

		@GetMapping("/user/edit/{id}")
		public String showEditForm(@PathVariable("id") int id, Model model) {
				User user = null;
				try{
						user = userService.findById(id);
						
				}catch(Exception ex){
						errors += "Invalid user Id";
						model.addAttribute("users", userService.getAll());
						model.addAttribute("errors", errors);
						return "users";
				}
				List<Role> roles = roleService.getAll();
				if(roles != null)
						model.addAttribute("roles", roles);				
				model.addAttribute("user", user);
				return "userUpdate";
		}
		@PostMapping("/user/update/{id}")
		public String updateUser(@PathVariable("id") int id, @Valid User user, 
														 BindingResult result, Model model) {
				if (result.hasErrors()) {
						user.setId(id);
						return "userUpdate";
				}
				messages = "Updated Successfully";
				userService.update(user);
				model.addAttribute("users", userService.getAll());				
				List<Role> roles = roleService.getAll();
				if(roles != null)
						model.addAttribute("roles", roles);
				model.addAttribute("messages", messages);
				return "users";
		}
		
		@GetMapping("/user/delete/{id}")
		public String deleteUser(@PathVariable("id") int id, Model model) {

				try{
						User user = userService.findById(id);
						userService.delete(id);
						messages = "Deleted Succefully";
				}catch(Exception ex){
						errors += "Invalid user ID "+id;
				}
				model.addAttribute("users", userService.getAll());
				if(!messages.equals("")){
						model.addAttribute("messages", messages);
				}
				else if(!errors.equals("")){
						model.addAttribute("errors", errors);
				}
					 
				return "users";
		}
		
}
