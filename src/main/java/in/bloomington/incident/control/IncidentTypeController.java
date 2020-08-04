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
import in.bloomington.incident.service.IncidentTypeService;
import in.bloomington.incident.service.UserService;
import in.bloomington.incident.model.IncidentType;
import in.bloomington.incident.model.User;


@Controller
public class IncidentTypeController extends TopController{

    @Autowired
    IncidentTypeService incidentTypeService;

		@Autowired 
    private HttpSession session;
		@Autowired
    UserService userService;
		
    @GetMapping("/incidentTypes")
    public String getAll(Model model) {
				String ret = canUserAccess(session);
				if(!ret.isEmpty()){
						return ret;
				}
        model.addAttribute("types", incidentTypeService.getAll());
        return "staff/incidentTypes";
    }
    @GetMapping("/incidentType/new")
    public String newIncidentType(Model model) {
				String ret = canUserAccess(session);
				if(!ret.isEmpty()){
						return ret;
				}
				IncidentType incidentType = new IncidentType();
        model.addAttribute("type", incidentType);
        return "staff/incidentTypeAdd";
    }     
    @PostMapping("/incidentType/add")
    public String addIncidentType(@Valid IncidentType incidentType, BindingResult result, Model model) {
				String ret = canUserAccess(session);
				if(!ret.isEmpty()){
						return ret;
				}
        if (result.hasErrors()) {
            return "staff/addIncidentType";
        }
        incidentTypeService.save(incidentType);
				addMessage("Added Successfully");
        model.addAttribute("types", incidentTypeService.getAll());
				model.addAttribute("messages", messages);				
        return "staff/incidentTypes";
    }

    @GetMapping("/incidentType/edit/{id}")
    public String showEditForm(@PathVariable("id") int id, Model model) {
				String ret = canUserAccess(session);
				if(!ret.isEmpty()){
						return ret;
				}
				IncidentType type = null;
				try{
						type = incidentTypeService.findById(id);
						
				}catch(Exception ex){
						addError("Invalid incident type Id");
						model.addAttribute("types", incidentTypeService.getAll());
						model.addAttribute("errors", errors);
						return "staff/incidentTypes";
				}
				model.addAttribute("type", type);
				return "staff/incidentTypeUpdate";
    }
    @PostMapping("/incidentType/update/{id}")
    public String updateIncidentType(@PathVariable("id") int id, @Valid IncidentType type, 
																		 BindingResult result, Model model) {
				String ret = canUserAccess(session);
				if(!ret.isEmpty()){
						return ret;
				}
				if (result.hasErrors()) {
						type.setId(id);
						return "staff/incidentTypeUpdate";
				}
				addMessage("Updated Successfully");
				incidentTypeService.save(type);
				model.addAttribute("types", incidentTypeService.getAll());				
				model.addAttribute("messages", messages);
				return "staff/incidentTypes";
    }
		
    @GetMapping("/incidentType/delete/{id}")
    public String deleteIncidentType(@PathVariable("id") int id, Model model) {

				String ret = canUserAccess(session);
				if(!ret.isEmpty()){
						return ret;
				}
				try{
						IncidentType type = incidentTypeService.findById(id);
						incidentTypeService.delete(id);
						addMessage("Deleted Succefully");
				}catch(Exception ex){
						addError("Invalid incidentType ID "+id);
				}
				model.addAttribute("types", incidentTypeService.getAll());
				if(hasMessages()){
						model.addAttribute("messages", messages);
				}
				else if(hasErrors()){
						model.addAttribute("errors", errors);
				}
					 
				return "staff/incidentTypes";
    }
		// 
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
						return "redirect:/staff";
				}
				return "";
		}		
		
}
