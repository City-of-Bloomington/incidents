package in.bloomington.incident.control;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import java.util.ArrayList;
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
import in.bloomington.incident.service.IncidentIncompleteService;
import in.bloomington.incident.service.IncidentReceivedService;
import in.bloomington.incident.service.IncidentConfirmedService;
import in.bloomington.incident.service.IncidentApprovedService;
import in.bloomington.incident.service.IncidentTypeService;
import in.bloomington.incident.service.SearchService;
import in.bloomington.incident.service.ActionService;
import in.bloomington.incident.service.IncidentService;
import in.bloomington.incident.service.UserService;
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.IncidentIncomplete;
import in.bloomington.incident.model.IncidentReceived;
import in.bloomington.incident.model.IncidentConfirmed;
import in.bloomington.incident.model.IncidentApproved;
import in.bloomington.incident.model.Search;
import in.bloomington.incident.model.User;
import in.bloomington.incident.model.Action;
import in.bloomington.incident.model.IncidentType;

@Controller
public class SearchController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(SearchController.class);
    @Autowired
    IncidentIncompleteService incompleteService;
    @Autowired
    IncidentReceivedService receivedService;
    @Autowired
    IncidentConfirmedService confirmedService;    
    @Autowired
    IncidentApprovedService approvedService;		
    @Autowired
    IncidentTypeService incidentTypeService;
    @Autowired
    SearchService searchService;
    @Autowired
    ActionService actionService;
    @Autowired
    IncidentService incidentService;		
    @Autowired 
    private HttpSession session;
		
    
    @GetMapping("/search/received")
    public String findReceieved(Model model) {
	model.addAttribute("app_url", app_url);
	User user = findUserFromSession(session);
	if(user == null ){
	    return "redirect:/login";
	}
	resetAll();
	List<Incident> all = null;
	List<IncidentReceived> plist = receivedService.getAll();
	if(plist != null){
	    all = new ArrayList<>();
	    for(IncidentReceived one:plist){
		Incident incident = one.getIncident();
		if(incident != null)
		    all.add(incident);
	    }
	}
	if(all != null && all.size() > 0){
	    addMessage("Found "+all.size()+" incidents");
	    model.addAttribute("incidents", all);
	    model.addAttribute("messages", messages);
	    resetAll();
	}
        return "staff/received_list";
    }
    @GetMapping("/search/incomplete")
    public String findIncomplete(Model model){

	List<Incident> all = null;
	model.addAttribute("app_url", app_url);
	User user = findUserFromSession(session);
	if(user == null ){
	    return "redirect:/login";
	}
	resetAll();
	List<IncidentIncomplete> plist = incompleteService.getAll();
	if(plist != null){
	    all = new ArrayList<>();
	    for(IncidentIncomplete one:plist){
		Incident incident = one.getIncident();
		if(incident != null)
		    all.add(incident);
	    }
	}
	if(all != null && all.size() > 0){
	    addMessage("Found "+all.size()+" incidents");
	    model.addAttribute("incidents", all);
	    model.addAttribute("messages", messages);
	    resetAll();
	}
        return "staff/incompletes";
    }
    
    @GetMapping("/search/confirmed")
    public String findConfirmed(Model model){
	List<Incident> all = null;
	model.addAttribute("app_url", app_url);
	User user = findUserFromSession(session);
	if(user == null ){
	    return "redirect:/login";
	}
	resetAll();
	List<IncidentConfirmed> plist = confirmedService.getAll();
	if(plist != null){
	    all = new ArrayList<>();
	    for(IncidentConfirmed one:plist){
		Incident incident = one.getIncident();
		if(incident != null)
		    all.add(incident);
	    }
	}
	if(all != null && all.size() > 0){
	    addMessage("Found "+all.size()+" incidents");
	    model.addAttribute("incidents", all);
	    model.addAttribute("messages", messages);
	    resetAll();
	}
	model.addAttribute("statusOutcome", "Confirmed");				getMessagesAndErrorsFromSession(session, model);		
        return "staff/confirmed_list";
    }    
    
    @GetMapping("/search/approved")
    public String findApproved(Model model){
	List<Incident> all = null;
	model.addAttribute("app_url", app_url);
	User user = findUserFromSession(session);
	if(user == null ){
	    return "redirect:/login";
	}
	resetAll();
	List<IncidentApproved> plist = approvedService.getAll();
	if(plist != null){
	    all = new ArrayList<>();
	    for(IncidentApproved one:plist){
		Incident incident = one.getIncident();
		if(incident != null)
		    all.add(incident);
	    }
	}
	resetAll();
	if(all != null && all.size() > 0){
	    addMessage("Found "+all.size()+" incidents");
	    model.addAttribute("incidents", all);
	    model.addAttribute("messages", messages);
	    resetAll();
	}
	model.addAttribute("statusOutcome", "Approved");	
        return "staff/approved_outcomes";
    }
    @GetMapping("/search")
    public String search(Model model) {
	model.addAttribute("app_url", app_url);
	User user = findUserFromSession(session);
	if(user == null ){
	    return "redirect:/login";
	}
	Search search = new Search();
	List<IncidentType> types = incidentTypeService.getAll();
	List<Action> actions = actionService.getAll();
        model.addAttribute("search", search);
	model.addAttribute("types", types);
	model.addAttribute("actions",actions);
	getMessagesAndErrorsFromSession(session, model);
	resetAll();
        return "staff/search";
    }
    @PostMapping("/search/find")
    public String searchFind(@Valid Search search,
			     BindingResult result,
			     Model model
			     ) {
	model.addAttribute("app_url", app_url);
        if (result.hasErrors()) {
	    logger.error(" Error creating new action ");
            return "redirect:/search";
        }
	User user = findUserFromSession(session);
	if(user == null ){
	    return "redirect:/login";
	}	
	if(!search.isValid()){
	    addError("You have to fill some search fields");
	    
	    return "redirect:/search";
	}
	List<Incident> incidents = null;				
	if(!search.getId().isEmpty()){
	    int id = search.getIdInt();
	    if(id > 0){						
		if(user.canApprove()){
		    Incident incident = incidentService.findById(id);
		    if(incident != null && !incident.canBeDiscarded()){
			return "redirect:/incidentView/"+id;
		    }
		    else{
			incidents = new ArrayList<>();
			incidents.add(incident);
		    }
		}
		else{
		    return "redirect:/incidentView/"+id;										
		}								
	    }
	}
	resetAll();
	if(incidents == null){
	    incidents = searchService.find(search);
	}
	if(incidents != null && incidents.size() > 0){
	    addMessage(" found "+incidents.size()+" incidents");
	}
	else{
	    addMessage(" No match found ");
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/search";
	}
        model.addAttribute("incidents", incidents);
	model.addAttribute("messages", getMessages());
	resetAll();
	if(user.canApprove()){ // or admins
	    return "staff/searchAdminResults";
	}
        return "staff/searchResult";
    }    
    
		
}
