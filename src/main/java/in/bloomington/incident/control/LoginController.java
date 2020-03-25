package in.bloomington.incident.control;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Value;
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
import in.bloomington.incident.service.UserService;
import in.bloomington.incident.model.User;
import in.bloomington.incident.utils.Helper;


@Controller
public class LoginController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    UserService userService;

    @Autowired
    private Environment env;

    @Value("${incident.ldap.host}")    
    private String ldap_host = "";
		
    @GetMapping("/login")
    public String login(Model model) {
	User user = new User();
	// model.addAttribute("user", user);
        return "staff/loginForm";
    }     
    @PostMapping("/loginUser")
    public String tryLogin(@RequestParam("username") String username,
			   @RequestParam("password") String password,
			   HttpServletRequest req
			   ) {
	/*
	if (result.hasErrors()) {
	    logger.error("Error login ");
	    addError("Error login ");
	    return "staff/loginForm";
	}
	*/
	HttpSession session = req.getSession(true);
	System.err.println(" *** username "+username);
	System.err.println(" *** pass "+password);	
	if(username == null || username.isEmpty()){
	    return "staff/loginForm";
	}
	/**
	   // uncomment later 
	Helper helper = new Helper();
	helper.populateHosts();
	if(!helper.checkUser(username, password, ldap_host)){
	    addMessage("invalid username or password");
	    addMessagesToSession(session);
	    return "staff/loginForm";
	}
	*/
	System.err.println(" **** finding user ");
	User user = userService.findUserByUsername(username);
	System.err.println(" **** user "+user);
	if(user == null){
	    addMessage("user not found "+username);
	    addMessagesToSession(session);
	    return "staff/loginForm";
	}
	session.setAttribute("user", user);
	return "staff/staff_intro";
    }
    @GetMapping("/settings")
    public String showSettings(Model model,
			       HttpSession session) {
	// check for user and if he is an admin
	User user = getUserFromSession(session);
	System.err.println("settings: user from session "+user);
        return "staff/settings";
    }         
		
}
