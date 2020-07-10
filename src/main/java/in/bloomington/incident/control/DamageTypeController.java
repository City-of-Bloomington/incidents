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
// import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import in.bloomington.incident.service.DamageTypeService;
import in.bloomington.incident.service.UserService;
import in.bloomington.incident.model.DamageType;
import in.bloomington.incident.model.User;


@Controller
public class DamageTypeController extends TopController{

		@Autowired
		DamageTypeService damageTypeService;
		@Autowired 
    private HttpSession session;
		@Autowired
    UserService userService;
		
		@GetMapping("/damageTypes")
    public String getAll(Model model) {
				String ret = canUserAccess(session);
				if(!ret.isEmpty()){
						return ret;
				}				
        model.addAttribute("types", damageTypeService.getAll());
        return "damageTypes";
    }
		@GetMapping("/damageType/new")
    public String newDamageType(Model model) {
				String ret = canUserAccess(session);
				if(!ret.isEmpty()){
						return ret;
				}
				DamageType damageType = new DamageType();
        model.addAttribute("type", damageType);
        return "damageTypeAdd";
    }     
    @PostMapping("/damageType/add")
    public String addDamageType(@Valid DamageType damageType, BindingResult result, Model model) {
				String ret = canUserAccess(session);
				if(!ret.isEmpty()){
						return ret;
				}
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
				String ret = canUserAccess(session);
				if(!ret.isEmpty()){
						return ret;
				}
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
		public String updateDamageType(@PathVariable("id") int id,
																	 @Valid DamageType type, 
																	 BindingResult result, Model model) {
				String ret = canUserAccess(session);
				if(!ret.isEmpty()){
						return ret;
				}
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
				String ret = canUserAccess(session);
				if(!ret.isEmpty()){
						return ret;
				}
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
		private User findUserFromSession(HttpSession session){
				User user = null;
				User user2 = getUserFromSession(session);
				if(user2 != null){
						user = userService.findById(user2.getId());
				}
				return user;
    }
		private String canUserAccess(HttpSession session){
				User user = findUserFromSession(session);
				if(user == null){
						return "redirect:/login";
				}
				if(!user.isAdmin()){
						addMessage("you can not access");
						addMessagesAndErrorsToSession(session);
						return "redirect:staff";
				}
				return "";
		}		
}
