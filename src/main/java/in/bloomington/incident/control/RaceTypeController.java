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
import in.bloomington.incident.service.RaceTypeService;
import in.bloomington.incident.service.UserService;
import in.bloomington.incident.model.RaceType;
import in.bloomington.incident.model.User;
import in.bloomington.incident.utils.Helper;

@Controller
public class RaceTypeController extends TopController{

		@Autowired
		RaceTypeService raceTypeService;
		@Autowired 
    private HttpSession session;
		@Autowired
    UserService userService;
		
		
		@GetMapping("/raceTypes")
    public String getAll(Model model) {
				String ret = canUserAccess(session);
				if(!ret.isEmpty()){
						return ret;
				}
        model.addAttribute("types", raceTypeService.getAll());
        return "raceTypes";
    }
		@GetMapping("/raceType/new")
    public String newRaceType(Model model) {
				String ret = canUserAccess(session);
				if(!ret.isEmpty()){
						return ret;
				}
				RaceType raceType = new RaceType();
        model.addAttribute("type", raceType);
        return "raceTypeAdd";
    }     
    @PostMapping("/raceType/add")
    public String addRaceType(@Valid RaceType raceType, BindingResult result, Model model) {
				String ret = canUserAccess(session);
				if(!ret.isEmpty()){
						return ret;
				}
        if (result.hasErrors()) {
            return "addRaceType";
        }
        raceTypeService.save(raceType);
				addMessage("Added Successfully");
        model.addAttribute("types", raceTypeService.getAll());
				model.addAttribute("messages", messages);				
        return "raceTypes";
    }

		@GetMapping("/raceType/edit/{id}")
		public String showEditForm(@PathVariable("id") int id, Model model) {
				String ret = canUserAccess(session);
				if(!ret.isEmpty()){
						return ret;
				}
				RaceType type = null;
				try{
						type = raceTypeService.findById(id);
						
				}catch(Exception ex){
						addError("Invalid race type Id");
						model.addAttribute("types", raceTypeService.getAll());
						model.addAttribute("errors", errors);
						return "raceTypes";
				}
				model.addAttribute("type", type);
				if(hasMessages()){
						model.addAttribute("messages", messages);				
				}
				return "raceTypeUpdate";
		}
		@PostMapping("/raceType/update/{id}")
		public String updateRaceType(@PathVariable("id") int id,
																	 @Valid RaceType type, 
																	 BindingResult result, Model model) {
				String ret = canUserAccess(session);
				if(!ret.isEmpty()){
						return ret;
				}
				if (result.hasErrors()) {
						String error = Helper.extractErrors(result);
						addError(error);
						type.setId(id);
						return "raceTypeUpdate";
				}
				addMessage("Updated Successfully");
				raceTypeService.save(type);
				model.addAttribute("types", raceTypeService.getAll());				
				model.addAttribute("messages", messages);
				return "raceTypes";
		}
		
		@GetMapping("/raceType/delete/{id}")
		public String deleteRaceType(@PathVariable("id") int id, Model model) {
				String ret = canUserAccess(session);
				if(!ret.isEmpty()){
						return ret;
				}
				try{
						RaceType type = raceTypeService.findById(id);
						raceTypeService.delete(id);
						addMessage("Deleted Succefully");
				}catch(Exception ex){
						addError("Invalid raceType ID "+id);
				}
				model.addAttribute("types", raceTypeService.getAll());
				if(hasMessages()){
						model.addAttribute("messages", messages);
				}
				else if(hasErrors()){
						model.addAttribute("errors", errors);
				}
					 
				return "raceTypes";
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
