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
import in.bloomington.incident.service.IncidentPreApproveService;
import in.bloomington.incident.service.IncidentApprovedService;
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.IncidentPreApprove;
import in.bloomington.incident.model.IncidentApproved;


@Controller
public class SearchController extends TopController{

		final static Logger logger = LoggerFactory.getLogger(SearchController.class);
		@Autowired
		IncidentPreApproveService preApproveService;
		@Autowired
		IncidentApprovedService approvedService;		
		
		@GetMapping("/search/preApproved")
    public String findPreApproved(Model model) {
				List<Incident> all = null;
				List<IncidentPreApprove> plist = preApproveService.getAll();
				if(plist != null){
						all = new ArrayList<>();
						for(IncidentPreApprove one:plist){
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
		
}
