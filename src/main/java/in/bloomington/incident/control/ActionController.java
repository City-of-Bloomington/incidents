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
//
// implementation of logging is logback
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import in.bloomington.incident.service.ActionService;
import in.bloomington.incident.service.UserService;
import in.bloomington.incident.model.Action;
import in.bloomington.incident.model.User;

@Controller
public class ActionController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(ActionController.class);
    @Autowired
    ActionService actionService;
    @Autowired
    UserService userService;		
    @Autowired 
    private HttpSession session;
		
    @GetMapping("/actions")
    public String getAll(Model model) {
	String ret = canUserAccess(session);
	if(!ret.isEmpty()){
	    return ret;
	}				
        model.addAttribute("actions", actionService.getAll());
	model.addAttribute("app_url", app_url);
        return "staff/actions";
    }
    @GetMapping("/action/new")
    public String newAction(Model model) {
	String ret = canUserAccess(session);
	if(!ret.isEmpty()){
	    return ret;
	}
	Action action = new Action();
        model.addAttribute("action", action);
	model.addAttribute("app_url", app_url);
        return "staff/actionAdd";
    }     
    @PostMapping("/action/add")
    public String addAction(@Valid Action action, BindingResult result, Model model) {
	String ret = canUserAccess(session);
	if(!ret.isEmpty()){
	    return ret;
	}
        if (result.hasErrors()) {
	    logger.error(" Error creating new action ");
            return "staff/addAction";
        }
        actionService.save(action);
	addMessage("Added Successfully");
	logger.debug("Action added successfully");
        model.addAttribute("actions", actionService.getAll());
	model.addAttribute("app_url", app_url);
	model.addAttribute("messages", messages);				
        return "staff/actions";
    }

    @GetMapping("/action/edit/{id}")
    public String showEditForm(@PathVariable("id") int id, Model model) {
	Action action = null;
	model.addAttribute("app_url", app_url);
	String ret = canUserAccess(session);
	if(!ret.isEmpty()){
	    return ret;
	}
	try{
	    action = actionService.findById(id);
						
	}catch(Exception ex){
	    addError("Invalid action Id");
	    model.addAttribute("actions", actionService.getAll());
	    model.addAttribute("errors", errors);

	    logger.error("Exception getting action ="+id+" "+ex);
	    return "staff/actions";
	}
	model.addAttribute("action", action);
	return "staff/actionUpdate";
    }
    @PostMapping("/action/update/{id}")
    public String updateAction(@PathVariable("id") int id,
			       @Valid Action action, 
			       BindingResult result, Model model) {
	String ret = canUserAccess(session);
	if(!ret.isEmpty()){
	    return ret;
	}
	if (result.hasErrors()) {
	    action.setId(id);
	    logger.error("Error update action ="+id);						
	    return "staff/updateAction";
	}
	addMessage("Updated Successfully");
	actionService.update(action);
	model.addAttribute("actions", actionService.getAll());
	model.addAttribute("app_url", app_url);
	model.addAttribute("messages", messages);
	return "staff/actions";
    }
		
    @GetMapping("/action/delete/{id}")
    public String deleteAction(@PathVariable("id") int id, Model model) {
	String ret = canUserAccess(session);
	if(!ret.isEmpty()){
	    return ret;
	}
	try{
	    Action action = actionService.findById(id);
	    actionService.delete(id);
	    addMessage("Deleted Succefully");
	}catch(Exception ex){
	    logger.error("Error delete action ="+id+" "+ex);								
	    addError("Invalid action ID "+id);
	}
	model.addAttribute("actions", actionService.getAll());
	if(hasMessages()){
	    model.addAttribute("messages", messages);
	}
	else if(hasErrors()){
	    model.addAttribute("errors", errors);
	}
	model.addAttribute("app_url", app_url);
	return "staff/actions";
    }

}
