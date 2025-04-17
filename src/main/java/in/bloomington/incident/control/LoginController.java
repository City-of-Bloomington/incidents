package in.bloomington.incident.control;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import java.util.Enumeration;
import java.net.URI;
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
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.id.*;
import com.nimbusds.oauth2.sdk.token.*;
import com.nimbusds.openid.connect.sdk.Nonce;

import javax.validation.Valid;

//
// implementation of logging is logback logging
//
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import in.bloomington.incident.service.SearchService;
import in.bloomington.incident.model.User;
import in.bloomington.incident.model.Action;
import in.bloomington.incident.model.Credential;
import in.bloomington.incident.model.Business;
import in.bloomington.incident.utils.Helper;
import in.bloomington.incident.utils.Configuration;
import in.bloomington.incident.utils.OidcClient;
import in.bloomington.incident.utils.CityClient;

@Controller
public class LoginController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(LoginController.class);
    static Configuration config = null;
    @Value("${incident.ldap.host}")    
    private String ldap_host;
    // ADFS params
    @Value("${incident.adfs.auth_end_point}")
    private String auth_end_point;
    @Value("${incident.adfs.token_end_point}")
    private String token_end_point;
    @Value("${incident.adfs.callback_uri}")    
    private String callback_uri;
    @Value("${incident.adfs.client_id}")
    private String client_id;
    @Value("${incident.adfs.client_secret}")
    private String client_secret;    
    @Value("${incident.adfs.discovery_uri}")
    private String discovery_uri;
    @Value("${incident.adfs.adfs_username}")
    private String adfs_username;
    @Value("${incident.app.url}")
    private String url;    
    //
    private String scope="openid";
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
	String ret_str = "";
	model.addAttribute("app_url",app_url);
	HttpSession session = req.getSession(true);
	getMessagesAndErrorsFromSession(session, model);
	User user = (User)session.getAttribute("user");
	if(user == null){ // already logged in
	    OidcClient oidcClient = OidcClient.getInstance();
	    //
	    //
	    if(config == null){
		prepareConf();
	    }
	    oidcClient.setConfig(config);
	    URI redirectUrl = oidcClient.getRequestURI();
	    // System.err.println("login auth url "+redirectUrl.toString());
	    State state = oidcClient.getState();
	    Nonce nonce = oidcClient.getNonce();
	    session.setAttribute("state", state.toString());
	    session.setAttribute("nonce", nonce.toString());
	    // config stuff
	    session.setAttribute("auth_end_point", auth_end_point);
	    session.setAttribute("token_end_point", token_end_point);
	    session.setAttribute("callback_uri", callback_uri);
	    session.setAttribute("client_id", client_id);
	    session.setAttribute("client_secret", client_secret);
	    session.setAttribute("discovery_uri", discovery_uri);
	    session.setAttribute("adfs_username", adfs_username);
	    session.setAttribute("scope",scope);
	    if(config == null)
		prepareConf();
	    // session.setAttribute("config", config);
	    session.setAttribute("url", url);	    
	    // System.err.println("config "+config.toString());
	    // System.err.println("url "+url);
	    // System.err.println("login state "+state.toString());	    
	    // save state in session for verification later	   
	    // need to redirect
	    //
	    ret_str = redirectUrl.toString();
	}
	else{
	    ret_str =  "/staff";
	}
	return "redirect:"+ret_str;
    }
    @GetMapping("/callbackNext")
    public String callbackNext(HttpServletRequest req,
			       Model model
			       ) {
	HttpSession session = req.getSession();	
	Enumeration values = req.getParameterNames();
	String name= "";
	String value = "";
	String return_str = "";
	String url = "";
	boolean error_flag = false;
	String username = null;
	while (values.hasMoreElements()) {
	    name = ((String)values.nextElement()).trim();
	    value = req.getParameter(name).trim();
	    /**
	    if(name.equals("username")){
		username = value;
		System.err.println(" username : "+username);		
	    }
	    */
	}
	username = (String) session.getAttribute("username");
	if(username != null){
	    try{
		User user = userService.findUser(username);
		// System.err.println(" user "+user);
		if(user != null){
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
		    return_str = "staff";
		    return return_str;
		}
		else{
		    error_flag = true;
		}
	    }catch(Exception ex){
		System.err.println(ex);
	    }
	}
	if(error_flag){
	    return_str = "/error";
	}
	return "redirect:"+return_str;
    }
    
    /**
    //non CAS after login action
    @PostMapping("/loginUser")
    public String tryLogin(@RequestParam("username") String username,
			   @RequestParam("password") String password,
			   HttpServletRequest req,
			   Model model
			   ) {
	HttpSession session = req.getSession(true);
	model.addAttribute("app_url",app_url);
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
	    User user = userService.findUser(username);
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
    */
    @GetMapping("/settings")
    public String showSettings(Model model,
			       HttpSession session) {
	// check for user and if he is an admin
	model.addAttribute("app_url",app_url);
	String ret = canUserAccess(session);
	if(!ret.isEmpty()){
	    return ret;
	}
        return "staff/settings";
    }
    private void prepareConf(){
	config = new
	    Configuration(auth_end_point, token_end_point, callback_uri, client_id, client_secret, scope, discovery_uri, adfs_username);
    }

}
