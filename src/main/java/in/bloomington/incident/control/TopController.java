package in.bloomington.incident.control;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */
import javax.servlet.http.HttpSession;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.validation.ObjectError;
import org.springframework.validation.FieldError;
import java.util.List;
import java.util.ArrayList;
import in.bloomington.incident.model.User;

public abstract class TopController {

    List<String> errors = null;
    List<String> messages = null;
    public TopController(){

    }		
    public boolean hasErrors(){
	return errors != null && errors.size() > 0;
    }
    public boolean hasMessages(){
	return messages != null && messages.size() > 0;
    }
    public void addError(String val){
	if(val != null){
	    if(errors == null)
		errors = new ArrayList<>();
	    if(!errors.contains(val))
		errors.add(val);
	}
    }
    public void addErrors(List<String> vals){
	if(vals != null){
	    for(String error:vals){
		addError(error);
	    }
	}
    }
				
    public void addMessage(String val){
	if(val != null){
	    if(messages == null)
		messages = new ArrayList<>();
	    if(!messages.contains(val))
		messages.add(val);
	}
    }
    public void addMessages(List<String> vals){
	if(vals != null){
	    for(String str:vals){
		addMessage(str);
	    }
	}
    }		
    public List<String> getErrors(){
	return errors;
    }
    public List<String> getMessages(){
	return messages;
    }
    public void resetAll(){
	if(messages != null){
	    messages = new ArrayList<>();
	}
	if(errors != null){
	    errors = new ArrayList<>();
	}
    }
    public String getErrorsInfo(){
	String ret = "";
	if(hasErrors()){
	    for(String error:errors){
		if(!ret.equals("")) ret += ", ";
		ret += error;
	    }
	}
	return ret;
    }
    public String getMessagesInfo(){
	String ret = "";
	if(hasMessages()){
	    for(String val:messages){
		if(!ret.equals("")) ret += ", ";
		ret += val;
	    }
	}
	return ret;
    }
    public void addMessagesToSession(HttpSession session){
	if(session != null && messages != null){
	    session.setAttribute("messages", messages);
	}
    }
    @SuppressWarnings("unchecked")		
    public void getMessagesFromSession(final HttpSession session){
	if(session != null){
	    Object obj = session.getAttribute("messages");
	    if(obj != null && obj instanceof List){
		List<String> vals = (List<String>)  obj;
		for(String val:vals){
		    addMessage(val);
		}
		session.setAttribute("messages",null);
	    }
	}
    }
    @SuppressWarnings("unchecked")		
    public User getUserFromSession(final HttpSession session){
	User user = null;
	if(session != null){
	    Object obj = session.getAttribute("user");
	    if(obj != null && obj instanceof User){
		user = (User)  obj;
	    }
	}
	return user;
    }	    
    public void printMessages(){
	if(messages != null){
	    for(String val:messages){
		System.err.println(" ** message "+val);
	    }
	}
    }
    @SuppressWarnings("unchecked")
    public final static boolean verifySession(final HttpSession session, final String id){
	// no new session
	if(session != null){
	    List<String> ids = (List<String>) session.getAttribute("incident_ids");
	    if(ids != null){
		System.err.println(" ** ids ** "+ids);
		if(ids.contains(id)){
		    return true;
		}
	    }
	}
	return false;
    }
    public String extractErrors(final BindingResult result)
    {
	String errors = "";
	if(result != null){
	    for (ObjectError error : result.getAllErrors()) {
		if(!errors.equals("")) errors += " ";
		errors += error.getObjectName() + " - " + error.getDefaultMessage();
	    }
	}
        return errors;
    }
    
}
