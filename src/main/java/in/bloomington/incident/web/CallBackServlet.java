package in.bloomington.incident.web;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */
import com.nimbusds.oauth2.sdk.token.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import java.io.*;
import java.util.*;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import in.bloomington.incident.model.User;
import in.bloomington.incident.model.Action;
import in.bloomington.incident.model.Credential;
import in.bloomington.incident.model.Business;
import in.bloomington.incident.utils.Helper;
import in.bloomington.incident.utils.Configuration;
import in.bloomington.incident.utils.OidcClient;
import in.bloomington.incident.utils.CityClient;

@WebServlet(urlPatterns = {"/callback"}, loadOnStartup = 1)
public class CallBackServlet extends HttpServlet{

    static Logger logger = LogManager.getLogger(CallBackServlet.class);

    private String auth_end_point;
    private String token_end_point;
    private String callback_uri;
    private String client_id;
    private String client_secret;    
    private String discovery_uri;
    private String adfs_username;
    private String scope;
    //
    private String url;    
    boolean debug = false;
    private Configuration config = null;
    
    public void doGet(HttpServletRequest request,
		      HttpServletResponse response)
	throws IOException {

	PrintWriter out = response.getWriter();
	String id = "";
	Enumeration values = request.getParameterNames();
	String name= "";
	String value = "";
	String return_str = "", code="", state="";
	// String url="https://outlaw.bloomington.in.gov/incidents";
	boolean error_flag = false;
	String error_txt = "";
	HttpSession session = request.getSession();
	while (values.hasMoreElements()) {
	    name = ((String)values.nextElement()).trim();
	    value = request.getParameter(name).trim();
	    if (name.equals("id"))
		id = value;
	    else if (name.equals("code"))
		code = value;
	    else if (name.equals("state"))
		state = value;
	    else if(name.equals("error")){
		error_flag = true;
		error_txt = value;
	    }
	    else{
		System.err.println(name+" "+value);
	    }
	}
	// System.err.println(" call back called, code "+code);
	if(!error_flag){
	    String original_state = (String)session.getAttribute("state");
	    url = (String)session.getAttribute("url");
	    auth_end_point = (String)session.getAttribute("auth_end_point");
	    token_end_point = (String)session.getAttribute("token_end_point");
	    callback_uri = (String)session.getAttribute("callback_uri");
	    client_id = (String)session.getAttribute("client_id");
	    client_secret = (String)session.getAttribute("client_secret");
	    discovery_uri = (String)session.getAttribute("discovery_uri");
	    adfs_username = (String)session.getAttribute("adfs_username");
	    scope = (String)session.getAttribute("scope");
	    config = new Configuration(auth_end_point, token_end_point, callback_uri, client_id, client_secret, scope, discovery_uri, adfs_username);	    
	    // System.err.println("callback url "+url);
	    // System.err.println(" callback conf "+config.toString());
	    if(state == null || !original_state.equals(state)){
		System.err.println(" invalid state "+state);
		// error_flag = true;
		// 
	    }
	    // System.err.println("callback conf "+config.toString());
	    String username = CityClient.getInstance().endAuthentication(code, config);
	    if(username != null){
		session.setAttribute("username", username);		
		String str ="<head><title></title><META HTTP-EQUIV=\""+
		    "refresh\" CONTENT=\"0; URL=" + url +
		    "/callbackNext\">";
		out.println(str);				
		out.println("<body>");
		out.println("</body>");
		out.println("</html>");
	    }
	    else{
		error_flag = true;
	    }
	}
	if(error_flag){
	    String str ="<head><title></title><META HTTP-EQUIV=\""+
		"refresh\" CONTENT=\"0; URL=" + url +
		"/login\">";
	    out.println(str);				
	    out.println("<body>");
	    out.println("</body>");
	    out.println("</html>");
	}
	out.flush();
    }
    public void doPost(HttpServletRequest request,
		      HttpServletResponse response)
	throws IOException {
	doGet(request, response);
    }
    /**
    private void prepareConf(){
	config = new
	    Configuration(auth_end_point, token_end_point, callback_uri, client_id, client_secret, scope, discovery_uri, adfs_username);
    }
    */
}
