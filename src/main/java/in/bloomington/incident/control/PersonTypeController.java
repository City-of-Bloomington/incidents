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
import in.bloomington.incident.service.PersonTypeService;
import in.bloomington.incident.service.UserService;
import in.bloomington.incident.model.PersonType;
import in.bloomington.incident.model.User;
import in.bloomington.incident.utils.Helper;

@Controller
public class PersonTypeController extends TopController{

    @Autowired
    PersonTypeService personTypeService;
    @Autowired 
    private HttpSession session;
    @Autowired
    UserService userService;
		
    @GetMapping("/personTypes")
    public String getAll(Model model) {
	model.addAttribute("app_url", app_url);
	String ret = canUserAccess(session);
	if(!ret.isEmpty()){
	    return ret;
	}
        model.addAttribute("types", personTypeService.getAll());
        return "staff/personTypes";
    }
    @GetMapping("/personType/new")
    public String newPersonType(Model model) {
	model.addAttribute("app_url", app_url);
	String ret = canUserAccess(session);
	if(!ret.isEmpty()){
	    return ret;
	}
	PersonType personType = new PersonType();
        model.addAttribute("type", personType);
        return "staff/personTypeAdd";
    }     
    @PostMapping("/personType/add")
    public String addPersonType(@Valid PersonType personType, BindingResult result, Model model) {
	model.addAttribute("app_url", app_url);
	String ret = canUserAccess(session);
	if(!ret.isEmpty()){
	    return ret;
	}
        if (result.hasErrors()) {
            return "staff/addPersonType";
        }
        personTypeService.save(personType);
	addMessage("Added Successfully");
        model.addAttribute("types", personTypeService.getAll());
	model.addAttribute("messages", messages);				
        return "staff/personTypes";
    }

    @GetMapping("/personType/edit/{id}")
    public String showEditForm(@PathVariable("id") int id, Model model) {
	model.addAttribute("app_url", app_url);
	String ret = canUserAccess(session);
	if(!ret.isEmpty()){
	    return ret;
	}
	PersonType type = null;
	try{
	    type = personTypeService.findById(id);
						
	}catch(Exception ex){
	    addError("Invalid person type Id");
	    model.addAttribute("types", personTypeService.getAll());
	    model.addAttribute("errors", errors);
	    return "staff/personTypes";
	}
	model.addAttribute("type", type);
	if(hasMessages()){
	    model.addAttribute("messages", messages);				
	}
	return "staff/personTypeUpdate";
    }
    @PostMapping("/personType/update/{id}")
    public String updatePersonType(@PathVariable("id") int id,
				   @Valid PersonType type, 
				   BindingResult result, Model model) {
	model.addAttribute("app_url", app_url);
	String ret = canUserAccess(session);
	if(!ret.isEmpty()){
	    return ret;
	}
	if (result.hasErrors()) {
	    String error = Helper.extractErrors(result);
	    addError(error);
	    type.setId(id);
	    return "staff/personTypeUpdate";
	}
	addMessage("Updated Successfully");
	personTypeService.save(type);
	model.addAttribute("types", personTypeService.getAll());				
	model.addAttribute("messages", messages);
	return "staff/personTypes";
    }
		
    @GetMapping("/personType/delete/{id}")
    public String deletePersonType(@PathVariable("id") int id, Model model) {
	model.addAttribute("app_url", app_url);
	String ret = canUserAccess(session);
	if(!ret.isEmpty()){
	    return ret;
	}
	try{
	    PersonType type = personTypeService.findById(id);
	    personTypeService.delete(id);
	    addMessage("Deleted Succefully");
	}catch(Exception ex){
	    addError("Invalid personType ID "+id);
	}
	model.addAttribute("types", personTypeService.getAll());
	if(hasMessages()){
	    model.addAttribute("messages", messages);
	}
	else if(hasErrors()){
	    model.addAttribute("errors", errors);
	}
					 
	return "staff/personTypes";
    }


}
