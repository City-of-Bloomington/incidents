package in.bloomington.incident.control;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import javax.servlet.http.HttpSession;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import in.bloomington.incident.service.UserService;
import in.bloomington.incident.service.RoleService;
import in.bloomington.incident.model.User;
import in.bloomington.incident.model.Role;
import in.bloomington.incident.utils.Helper;

@Controller
public class UserController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(UserController.class);			
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;
    @Autowired
    HttpSession session;
		
    @GetMapping("/users")
    public String getAll(Model model) {
	model.addAttribute("app_url", app_url);
	String ret = canUserAccess(session);
	if(!ret.isEmpty()){
	    return ret;
	}
        model.addAttribute("users", userService.getAll());
        return "staff/users";
    }
    @GetMapping("/users/find/{name}")
    public String findUsers(@PathVariable("name") String name, Model model) {
	model.addAttribute("app_url", app_url);
	String ret = canUserAccess(session);
	if(!ret.isEmpty()){
	    return ret;
	}
	if(name != null && !name.isEmpty()){
	    String str = "%"+name+"%";
	    List<User> users = userService.findByFirstnameOrByLastname(str, str);
	    if(users != null)
		model.addAttribute("users", users);
	    return "staff/users";
	}
	else{
	    return "redirect:/staff";
	}
    }		
    @GetMapping("/user/new")
    public String newUser(Model model) {
	model.addAttribute("app_url", app_url);
	String ret = canUserAccess(session);
	if(!ret.isEmpty()){
	    return ret;
	}
	User user = new User();
        model.addAttribute("user", user);
	List<Role> roles = roleService.getAll();
	if(roles != null)
	    model.addAttribute("roles", roles);						
        return "staff/userAdd";
    }     
    @PostMapping("/user/add")
    public String addUser(@Valid User user, BindingResult result, Model model) {
	model.addAttribute("app_url", app_url);
	String ret = canUserAccess(session);
	if(!ret.isEmpty()){
	    return ret;
	}
        if (result.hasErrors()) {
            return "staff/userAdd";
        }
        userService.save(user);
	addMessage("Added Successfully");
	logger.debug("New user added "+user);
        model.addAttribute("users", userService.getAll());
	model.addAttribute("messages", messages);				
        return "staff/users";
    }

    @GetMapping("/user/edit/{id}")
    public String showEditForm(@PathVariable("id") int id, Model model) {
	model.addAttribute("app_url", app_url);
	String ret = canUserAccess(session);
	if(!ret.isEmpty()){
	    return ret;
	}
	User user = null;
	try{
	    user = userService.findById(id);
						
	}catch(Exception ex){
	    addError("Invalid user Id "+id);
	    logger.error(" "+ex);
	    model.addAttribute("users", userService.getAll());
	    model.addAttribute("errors", errors);
	    return "staff/users";
	}
	List<Role> roles = roleService.getAll();
	if(roles != null)
	    model.addAttribute("roles", roles);				
	model.addAttribute("user", user);
	return "staff/userUpdate";
    }
    @PostMapping("/user/update/{id}")
    public String updateUser(@PathVariable("id") int id, @Valid User user, 
			     BindingResult result, Model model) {
	model.addAttribute("app_url", app_url);
	String ret = canUserAccess(session);
	if(!ret.isEmpty()){
	    return ret;
	}
	if (result.hasErrors()) {
	    String error = Helper.extractErrors(result);
	    addError("Error update user "+error);
	    user.setId(id);
	    return "staff/userUpdate";
	}
	addMessage("Updated Successfully");
	userService.update(user);
	model.addAttribute("users", userService.getAll());				
	List<Role> roles = roleService.getAll();
	if(roles != null)
	    model.addAttribute("roles", roles);
	model.addAttribute("messages", messages);
	return "staff/users";
    }
		
    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") int id, Model model) {
	model.addAttribute("app_url", app_url);
	String ret = canUserAccess(session);
	if(!ret.isEmpty()){
	    return ret;
	}
	try{
	    User user = userService.findById(id);
	    userService.delete(id);
	    addMessage("Deleted Succefully");
	}catch(Exception ex){
	    addError("Eror delete user "+id);
	    logger.error(" "+ex);
	}
	model.addAttribute("users", userService.getAll());
	if(hasMessages()){
	    model.addAttribute("messages", messages);
	}
	else if(hasErrors()){
	    model.addAttribute("errors", errors);
	}
	return "staff/users";
    }
		
}
