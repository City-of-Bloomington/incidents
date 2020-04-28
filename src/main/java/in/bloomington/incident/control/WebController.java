package in.bloomington.incident.control;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import in.bloomington.incident.model.User;
import in.bloomington.incident.service.UserService;

@Controller
public class WebController extends TopController{

    @Autowired
    UserService userService;       
    @RequestMapping(value = "/staff")
    public String staff(HttpSession session) {
	User user = findUserFromSession(session);
	if(user == null ){
	    return "redirect:/login";
	}       
	return "staff";
    }
    @RequestMapping(value = "/")
    public String index() {
	return "redirect:/introStart";
    }
    @GetMapping("/index")		
    public String indexStart(Model model,
			     HttpSession session) {
	getMessagesAndErrorsFromSession(session, model);
	return "redirect:/introStart"; 
    }
    
    @RequestMapping(value = "/introStart")
    public String introStart(Model model,
			     HttpSession session){
	getMessagesAndErrorsFromSession(session, model);
	return "intro_questions";
    }
    @RequestMapping(value = "/introTheft")
    public String introTheft(Model model,
			     HttpSession session) {
	getMessagesAndErrorsFromSession(session, model);
	return "theft_questions";
    }
    @RequestMapping(value = "/introVandal")
    public String introVandal(Model model,
			      HttpSession session) {
	getMessagesAndErrorsFromSession(session, model);
	return "vandal_questions";
    }       
    @RequestMapping(value = "/introLost")
    public String introLost(Model model,
			    HttpSession session) {
	getMessagesAndErrorsFromSession(session, model);
	return "lost_questions";
    }
    @RequestMapping(value = "/introEmail")
    public String introEmails(@RequestParam(required = true) int type_id,
			      Model model,
			      HttpSession session) {
	getMessagesAndErrorsFromSession(session, model);
	model.addAttribute("type_id", type_id);
	return "email_questions";
    }    
    private User findUserFromSession(HttpSession session){
	User user = null;
    	User user2 = getUserFromSession(session);
	if(user2 != null){
	    user = userService.findById(user2.getId());
	}
	return user;
    }    
}
