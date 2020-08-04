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
import in.bloomington.incident.service.FraudTypeService;
import in.bloomington.incident.service.UserService;
import in.bloomington.incident.model.FraudType;
import in.bloomington.incident.model.User;


@Controller
public class FraudTypeController extends TopController{

		@Autowired
		FraudTypeService fraudTypeService;
		@Autowired 
    private HttpSession session;
		@Autowired
    UserService userService;
		
		@GetMapping("/fraudTypes")
    public String getAll(Model model) {
				String ret = canUserAccess(session);
				if(!ret.isEmpty()){
						return ret;
				}				
        model.addAttribute("types", fraudTypeService.getAll());
        return "staff/fraudTypes";
    }
		@GetMapping("/fraudType/new")
    public String newFraudType(Model model) {
				String ret = canUserAccess(session);
				if(!ret.isEmpty()){
						return ret;
				}
				FraudType fraudType = new FraudType();
        model.addAttribute("type", fraudType);
        return "staff/fraudTypeAdd";
    }     
    @PostMapping("/fraudType/add")
    public String addFraudType(@Valid FraudType fraudType, BindingResult result, Model model) {
				String ret = canUserAccess(session);
				if(!ret.isEmpty()){
						return ret;
				}
        if (result.hasErrors()) {
            return "staff/addFraudType";
        }
        fraudTypeService.save(fraudType);
				addMessage("Added Successfully");
        model.addAttribute("types", fraudTypeService.getAll());
				model.addAttribute("messages", messages);				
        return "staff/fraudTypes";
    }

		@GetMapping("/fraudType/edit/{id}")
		public String showEditForm(@PathVariable("id") int id, Model model) {
				String ret = canUserAccess(session);
				if(!ret.isEmpty()){
						return ret;
				}
				FraudType type = null;
				try{
						type = fraudTypeService.findById(id);
						
				}catch(Exception ex){
						addError("Invalid fraud type Id");
						model.addAttribute("types", fraudTypeService.getAll());
						model.addAttribute("errors", errors);
						return "staff/fraudTypes";
				}
				model.addAttribute("type", type);
				if(hasErrors()){
						model.addAttribute("errors", errors);
				}
				return "staff/fraudTypeUpdate";
		}
		@PostMapping("/fraudType/update/{id}")
		public String updateFraudType(@PathVariable("id") int id,
																	 @Valid FraudType type, 
																	 BindingResult result, Model model) {
				String ret = canUserAccess(session);
				if(!ret.isEmpty()){
						return ret;
				}
				if (result.hasErrors()) {
						type.setId(id);
						return "staff/fraudTypeUpdate";
				}
				addMessage("Updated Successfully");
				fraudTypeService.update(type);
				model.addAttribute("types", fraudTypeService.getAll());				
				model.addAttribute("messages", messages);
				return "staff/fraudTypes";
		}
		
		@GetMapping("/fraudType/delete/{id}")
		public String deleteFraudType(@PathVariable("id") int id, Model model) {
				String ret = canUserAccess(session);
				if(!ret.isEmpty()){
						return ret;
				}
				try{
						FraudType type = fraudTypeService.findById(id);
						fraudTypeService.delete(id);
						addMessage("Deleted Succefully");
				}catch(Exception ex){
						addError("Invalid fraudType ID "+id);
				}
				model.addAttribute("types", fraudTypeService.getAll());
				if(hasMessages()){
						model.addAttribute("messages", messages);
				}
				else if(hasErrors()){
						model.addAttribute("errors", errors);
				}
					 
				return "staff/fraudTypes";
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
