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
import javax.servlet.http.HttpSession;
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
	return "redirect:/index";
    }
    @RequestMapping(value = "/introStart")
    public String introStart() {
	return "intro_questions";
    }
    @RequestMapping(value = "/introTheft")
    public String introTheft() {
	return "theft_questions";
    }
    @RequestMapping(value = "/introVandal")
    public String introVandal() {
	return "vandal_questions";
    }       
    @RequestMapping(value = "/introLost")
    public String introLost() {
	return "lost_questions";
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
