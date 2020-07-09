package in.bloomington.incident.control;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import java.util.Collection;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.core.Authentication;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.web.bind.annotation.ResponseBody;


//
// implementation of logging is logback logging
//
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import in.bloomington.incident.service.UserService;
import in.bloomington.incident.model.User;
import in.bloomington.incident.utils.Helper;
import in.bloomington.incident.configuration.IAuthenticationFacade;

@Controller
public class LoginController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    UserService userService;

    @Autowired
    private Environment env;

		@Autowired
		private IAuthenticationFacade authenticationFacade;
		
    @Value("${incident.ldap.host}")    
    private String ldap_host = "";

		// non CAS
		/**
    @GetMapping("/login")
    public String login(Model model) {
				User user = new User();
				// model.addAttribute("user", user);
        return "staff/loginForm";
    }
		*/
		// CAS login
		/**
		@GetMapping("/login")
    @ResponseBody
		public String loginUser(Authentication authentication) {
        String username = authentication.getName();
				System.err.println("username "+username);
				return username;
    }
		*/
		@GetMapping("/login")
    @ResponseBody
		public String loginUser(HttpServletRequest request) {
				// HttpServletRequest httpRequest = (HttpServletRequest) request;
				Enumeration<String> headerNames = request.getHeaderNames();
				if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
								System.out.println("Header: " + request.getHeader(headerNames.nextElement()));
            }
				}
        Authentication authentication = authenticationFacade.getAuthentication();
        String username = authentication.getName();
				System.err.println(" username "+username);
				return username;
    }
				/*
		@GetMapping("/login")
    @ResponseBody
    public String loginUser(HttpServletRequest request) {
				
				// Principal principal = null;
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				String username = null;
				if(!(auth instanceof AnonymousAuthenticationToken)){
						System.err.println(" it is not anonymous ");												
						username = auth.getName();
				}
				else{


				}
				principal = auth.getPrincipal();
				if (principal instanceof UserDetails) {
						System.err.println(" from details ");
						username = ((UserDetails)principal).getUsername();
						
				} else {
						System.err.println(" from principal ");
						username = principal.toString();
						
				}
				*/
				/**
				Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

				if (principal instanceof UserDetails) {
						System.err.println(" from details ");
						username = ((UserDetails)principal).getUsername();
						
				} else {
						System.err.println(" from principal ");
						username = principal.toString();
						
				}
				*/
		/*
				try{
						// principal = request.getUserPrincipal();
						// if(principal == null) System.err.println(" princ  is null ");
						HttpSession session = request.getSession();
						// String username =  principal.getName();
						System.err.println(" username "+username);
						if(username != null && !username.isEmpty()){
								User user = userService.findUserByUsername(username);
								if(user == null){
										addMessage("User not found "+username);
										return "failedLogin"; // ToDo
								}
								session.setAttribute("user", user);
						}
						else{
								return "login";
						}
				}catch(Exception ex){
						System.err.println(ex);
						// send to error page
				}
				return "staff/staff_intro";										
    }
		*/
		/**
			 // non CAS
    @GetMapping("/logout")
    public String logout(HttpServletRequest req) {
				HttpSession session = req.getSession();
				session.invalidate();
        return "staff/logout";
    }
		*/
		// For CAS 
		@GetMapping("/logout")
		public String logout(HttpServletRequest request,
												 HttpServletResponse response,
												 SecurityContextLogoutHandler logoutHandler) {
				Authentication auth = SecurityContextHolder
						.getContext().getAuthentication();
				logoutHandler.logout(request, response, auth );
				new CookieClearingLogoutHandler(
																				AbstractRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY)
						.logout(request, response, auth);
				return "staff/logout";
		}
		/**
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
				User user = userService.findUserByUsername(username);
				if(user == null){
						// addMessage("user not found "+username);
						// addMessagesAndErrorsToSession(session);
						return "staff/loginForm";
				}
				session.setAttribute("user", user);
				return "staff/staff_intro";
    }
		*/
    @GetMapping("/settings")
    public String showSettings(Model model,
															 HttpSession session) {
				// check for user and if he is an admin
				User user = getUserFromSession(session);
				if(user == null){
						// addMessage("user not found "+username);
						// addMessagesAndErrorsToSession(session);
						return "staff/loginForm";
				}	
				System.err.println("settings: user from session "+user);
        return "staff/settings";
    }

		

}
