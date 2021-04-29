package in.bloomington.incident.control;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
import java.util.Collection;
import java.util.Enumeration;
import java.security.Principal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.web.bind.annotation.ResponseBody;
*/
import javax.validation.Valid;

//
// implementation of logging is logback logging
//
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import in.bloomington.incident.service.UserService;
import in.bloomington.incident.service.SearchService;
import in.bloomington.incident.model.User;
import in.bloomington.incident.model.Action;
import in.bloomington.incident.utils.Helper;

@Controller
public class LoginController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    UserService userService;
		@Autowired
		SearchService searchService;
		
    @Value("${incident.ldap.host}")    
    private String ldap_host;

		//
    @GetMapping("/logout")
    public String logout(HttpServletRequest req) {
				HttpSession session = req.getSession();
				session.invalidate();
        return "staff/logout";
    }
		// 
    @GetMapping("/login")
    public String login(HttpServletRequest req,
												Model model
												) {
				HttpSession session = req.getSession(true);
				getMessagesAndErrorsFromSession(session, model);
				return "staff/loginForm";
    }		
		//non CAS after login action
    @PostMapping("/loginUser")
    public String tryLogin(@RequestParam("username") String username,
													 @RequestParam("password") String password,
													 HttpServletRequest req
													 ) {
				HttpSession session = req.getSession(true);
				if(username == null || username.isEmpty()){
						return "staff/loginForm";
				}

				Helper helper = new Helper();
				// for test purpose commented out
				// uncomment for production
				if(!helper.checkUser(username, password, ldap_host)){
						addMessage("invalid username or password");
						addMessagesAndErrorsToSession(session);
						return "staff/loginForm";
				}
				// userService was not working
				try{
						User user = searchService.findUser(username);
						if(user == null){
								// addMessage("user not found "+username);
								// addMessagesAndErrorsToSession(session);
								addMessage("you do not have access to this system ");
								addMessagesAndErrorsToSession(session);						
								return "redirect:/login";
						}
						session.setAttribute("user", user);
						if(user.isAdmin()){
								session.setAttribute("isAmin", "true");
						}
						if(user.canApprove()){
								session.setAttribute("canApprove", "true");
						}
						if(user.canProcess()){
								session.setAttribute("canProcess", "true");
						}				
						return "staff/staff_intro";
				}catch(Exception ex){
						addMessage("you do not have access to this system ");
						addMessagesAndErrorsToSession(session);						
				}
				return "redirect:/login";						
    }
    @GetMapping("/settings")
    public String showSettings(Model model,
															 HttpSession session) {
				// check for user and if he is an admin
				String ret = canUserAccess(session);
				if(!ret.isEmpty()){
						return ret;
				}
        return "staff/settings";
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
