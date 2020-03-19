package in.bloomington.incident.control;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import java.util.ArrayList;
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
import in.bloomington.incident.service.IncidentPreApprovedService;
import in.bloomington.incident.service.IncidentApprovedService;
import in.bloomington.incident.service.IncidentTypeService;
import in.bloomington.incident.service.SearchService;
import in.bloomington.incident.service.ActionService;
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.IncidentPreApproved;
import in.bloomington.incident.model.IncidentApproved;
import in.bloomington.incident.model.Search;
import in.bloomington.incident.model.IncidentType;

@Controller
public class SearchController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(SearchController.class);
    @Autowired
    IncidentPreApprovedService preApprovedService;
    @Autowired
    IncidentApprovedService approvedService;		
    @Autowired
    IncidentTypeService incidentTypeService;
    @Autowired
    SearchService searchService;
    @Autowired
    ActionService actionService;
    
    @GetMapping("/search/preApproved")
    public String findPreApproved(Model model) {
	List<Incident> all = null;
	List<IncidentPreApproved> plist = preApprovedService.getAll();
	if(plist != null){
	    all = new ArrayList<>();
	    for(IncidentPreApproved one:plist){
		Incident incident = one.getIncident();
		if(incident != null)
		    all.add(incident);
	    }
	}
	if(all != null && all.size() > 0){
	    model.addAttribute("incidents", all);
	}
	else{
	    addMessage("No incident found");
	    model.addAttribute("messages", messages);
	}
        return "pre_approved";
    }
    @GetMapping("/search/approved")
    public String findApproved(Model model) {
	List<Incident> all = null;
	List<IncidentApproved> plist = approvedService.getAll();
	if(plist != null){
	    all = new ArrayList<>();
	    for(IncidentApproved one:plist){
		Incident incident = one.getIncident();
		if(incident != null)
		    all.add(incident);
	    }
	}
	if(all != null && all.size() > 0){
	    model.addAttribute("incidents", all);
	}
	else{
	    addMessage("No incident found");
	    model.addAttribute("messages", messages);
	}
        return "approved";
    }
    @GetMapping("/search")
    public String search(Model model) {
	Search search = new Search();
	List<IncidentType> types = incidentTypeService.getAll();
        model.addAttribute("search", search);
	model.addAttribute("types", types);
	if(hasErrors())
	    model.addAttribute("errors", getErrors());
	if(hasMessages()){
	    model.addAttribute("messages", getMessages());
	}
        return "search";
    }
    @PostMapping("/search/find")
    public String searchFind(@Valid Search search,
			    BindingResult result,
			    Model model) {
        if (result.hasErrors()) {
	    logger.error(" Error creating new action ");
            return "redirect:/search";
        }
	if(!search.isValid()){
	    addError("You have to fill some search fields");
	    
	    return "redirect:/search";
	}
	if(!search.getId().isEmpty()){
	    return "redirect:/incidentView/"+search.getId();
	}
        List<Incident> incidents = searchService.find(search);
	if(incidents != null && incidents.size() > 0){
	    addMessage(" found "+incidents.size()+" incidents");
	    if(incidents.size() == 1){
		return "redirect:/incidentView/"+incidents.get(0).getId();
	    }
	}
	else{
	    addMessage(" no match found ");
	    return "redirect:/search";
	}
        model.addAttribute("incidents", incidents);
	model.addAttribute("messages", getMessages());				
        return "incidents";
    }    
    
    
		
}
