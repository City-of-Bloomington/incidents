package in.bloomington.incident.control;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
// import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import in.bloomington.incident.service.IncidentService;
import in.bloomington.incident.service.IncidentInitialService;
import in.bloomington.incident.service.IncidentTypeService;

import in.bloomington.incident.model.IncidentInitial;
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.IncidentType;

@Controller
public class IncidentInitialController extends TopController{

		@Autowired
		IncidentInitialService initialService;		
		@Autowired
		IncidentService incidentService;		
		@Autowired
		IncidentTypeService incidentTypeService;
		
		@GetMapping("/start")		
		public String startIncident() {
				return "initialStart";
		}
		@RequestMapping("/initialStart")
		public String incidentStartNext(@RequestParam(required = true) String email,
																		@RequestParam(required = true) String email2,
																		Model model){ 
				if(!email.equals(email2)){
						addError("The two emails do not match");
						model.addAttribute("errors", errors);
						return "initialStart";						
				}
				IncidentInitial initial = new IncidentInitial();
				initial.setEmail(email);
				initial.setReceivedNow();
				initialService.save(initial);
				model.addAttribute("initial", initial);
				List<IncidentType> types = incidentTypeService.getAll();
				model.addAttribute("types", types);				
				return "initialSelectType";
		}
		@SuppressWarnings("unchecked")
		@PostMapping("/initialNext/{id}")
		public String initialNext(@PathVariable("id") int id,
															IncidentInitial initial, 
															BindingResult result,
															Model model,
															HttpServletRequest req
															) {
				if (result.hasErrors()) {
						initial.setId(id);
						return "initialSelectType";
				}
				addMessage("Updated Successfully");
				initial.setId(id);
				initialService.save(initial);
				Incident incident = new Incident();
				incident.setEmail(initial.getEmail());
				incident.setReceived(initial.getReceived());
				incident.setIncidentType(initial.getIncidentType());
				incidentService.save(incident);
				//
				// this is the only place we are adding
				// incident ID in the session
				//
				HttpSession session = req.getSession(true);
				List<String> ids = null;
				try{
						 ids = (List<String>) session.getAttribute("incident_ids");
				}catch(Exception ex){
						System.err.println(ex);
				}
				if(ids == null){
						ids = new ArrayList<>();
				}
				ids.add(""+incident.getId());
				session.setAttribute("incident_ids", ids);
				return "redirect:/incidentStart/"+incident.getId();
		}

		
}
