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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.validation.ObjectError;
import org.springframework.validation.FieldError;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import in.bloomington.incident.service.IncidentService;
import in.bloomington.incident.service.OffenderService;
import in.bloomington.incident.service.RaceTypeService;
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.Offender;
import in.bloomington.incident.model.RaceType;
import in.bloomington.incident.model.User;
import in.bloomington.incident.utils.Helper;


@Controller
public class OffenderController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(OffenderController.class);
    @Autowired
    IncidentService incidentService;
    @Autowired
    OffenderService offenderService;		
    @Autowired
    RaceTypeService raceTypeService;
		
    //
    private List<String> phoneTypes = 
	new ArrayList<>(Arrays.asList("Cell","Home","Work"));
    private List<String> offenderTitles =
	new ArrayList<>(Arrays.asList("Mr","Ms","Mrs"));
    private List<String> sexTypes =
	new ArrayList<>(Arrays.asList("Male","Female","Nonbinary","Unknown"));
    private List<String> genderTypes =
	new ArrayList<>(Arrays.asList("Male","Female","Transgender"));
    private List<String> ethnicityTypes =
	new ArrayList<>(Arrays.asList("Hispanic","Non-hispanic","Unknown"));    
    @RequestMapping("/offender/add/{incident_id}")
    public String addOffender(@PathVariable("incident_id") int incident_id,
			      Model model,
			      HttpSession session) {
	Incident incident = null;
	model.addAttribute("app_url", app_url);
	try{
	    incident = incidentService.findById(incident_id);
	}catch(Exception ex){
	    addError("Invalid incident Id: "+incident_id);
	    logger.error(""+ex);
	    model.addAttribute("errors", errors);
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/forBusiness";
	}
	if(incident == null || !incident.canBeChanged()){
	    addMessage("No more changes can be made");
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/incident/"+incident_id;
	}
	if(!verifySession(session, ""+incident_id)){				
	    addMessage("No more changes can be made ");
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/";
	}							
	Offender offender = new Offender();				
	List<RaceType> raceTypes = raceTypeService.getAll();				
	offender.setIncident(incident);
	model.addAttribute("offender", offender);
	model.addAttribute("sexTypes", sexTypes);
	model.addAttribute("raceTypes", raceTypes);
	model.addAttribute("phoneTypes", phoneTypes);
	model.addAttribute("offenderTitles", offenderTitles);
	model.addAttribute("genderTypes", genderTypes);
	model.addAttribute("ethnicityTypes", ethnicityTypes);
        return "offenderAdd";
    }
    @PostMapping("/offender/save")
    public String offenderSave(@RequestParam String action,
			       Offender offender,
			       BindingResult result,
			       Model model,
			       HttpSession session
			       ) {
	
	model.addAttribute("app_url", app_url);
        if (result.hasErrors()) {
	    String error = Helper.extractErrors(result);
	    addError(error);
	    logger.error(error);
	    List<RaceType> raceTypes = raceTypeService.getAll();						
	    model.addAttribute("errors", errors);
	    model.addAttribute("offender", offender);
	    model.addAttribute("sexTypes", sexTypes);
	    model.addAttribute("raceTypes", raceTypes);
	    model.addAttribute("phoneTypes", phoneTypes);
	    model.addAttribute("offenderTitles", offenderTitles);
	    model.addAttribute("genderTypes", genderTypes);
	    model.addAttribute("ethnicityTypes", ethnicityTypes);
	    model.addAttribute("errors", errors);
	    handleErrorsAndMessages(model);
	    return "offenderAdd";
        }
	Incident incident = offender.getIncident();
	if(incident == null || !incident.canBeChanged()){
	    addMessage("No more changes can be made");
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/forBusiness";
	}
	int incident_id = incident.getId();
	if(!verifySession(session, ""+incident_id)){				
	    addMessage("No more changes can be made ");
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/forBusiness";
	}
	if(offender.getTransientOffender()){
	    incident.setTransientOffender(true);
	    incidentService.update(incident);
	    return "redirect:/businessIncident/"+incident_id;
	}
	if(!offender.verify()){
	    String error = offender.getErrorInfo();
	    addError(error);
	    List<RaceType> raceTypes = raceTypeService.getAll();						
	    model.addAttribute("sexTypes", sexTypes);
	    model.addAttribute("raceTypes", raceTypes);				
	    model.addAttribute("phoneTypes", phoneTypes);
	    model.addAttribute("offenderTitles", offenderTitles);
	    model.addAttribute("genderTypes", genderTypes);
	    model.addAttribute("ethnicityTypes", ethnicityTypes);
	    handleErrorsAndMessages(model);
	    return "offenderAdd";
	}
        offenderService.save(offender);
	addMessage("Saved Successfully");
	addMessagesAndErrorsToSession(session);
	System.err.println(" action "+action);
	if(!action.equals("Next")){ // add more
	    return "redirect:/offender/add/"+incident_id;
	}
	return "redirect:/businessIncident/"+incident.getId(); 
    }
    
    @GetMapping("/offender/{id}")
    public String showOffender(@PathVariable("id") int id, Model model) {
	Offender offender = null;
	try{
	    model.addAttribute("app_url", app_url);
	    offender = offenderService.findById(id);
	}catch(Exception ex){
	    addError("Invalid offender Id "+id);
	    logger.error(" "+ex);
	    model.addAttribute("errors", errors);
	    return "index"; // need fix
	}
        model.addAttribute("offender", offender);				
	return "offender";
    }
    //staff
    @GetMapping("/offenderView/{id}")
    public String viewOffender(@PathVariable("id") int id,
			       Model model,
			       HttpSession session) {
	Offender offender = null;
	model.addAttribute("app_url", app_url);
	User user = getUserFromSession(session);
	if(user == null){
	    return "redirect:/login";
	}
	try{
	    offender = offenderService.findById(id);
	}catch(Exception ex){
	    addError("Invalid offender Id "+id);
	    logger.error(" "+ex);
	    model.addAttribute("errors", errors);
	    return "staff/staff_intro"; 
	}
        model.addAttribute("offender", offender);				
	return "offenderView";
    }    
    
    @GetMapping("/offender/edit/{id}")
    public String showEditForm(@PathVariable("id") int id,
			       Model model,
			       HttpSession session) {
	Offender offender = null;
	model.addAttribute("app_url", app_url);
	try{
	    offender = offenderService.findById(id);
	}catch(Exception ex){
	    addError("Invalid offender Id "+id);
	    System.err.println(" offender edit error "+ex);
	    logger.error(" "+ex);
	    addMessagesAndErrorsToSession(session);	    
	    return "redirect:/"; 
	}
	Incident incident = offender.getIncident();
	if(incident == null || !incident.canBeChanged()){
	    addMessage("no more changes can be made");
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/";	    
	}
	int incident_id = incident.getId();
	if(!verifySession(session, ""+incident_id)){				
	    addMessage("No more changes can be made ");
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/";
	}							
	List<RaceType> raceTypes = raceTypeService.getAll();
	model.addAttribute("offender", offender);
	model.addAttribute("sexTypes", sexTypes);
	model.addAttribute("raceTypes", raceTypes);				
	model.addAttribute("phoneTypes", phoneTypes);
	model.addAttribute("offenderTitles", offenderTitles);
	model.addAttribute("genderTypes", genderTypes);
	model.addAttribute("ethnicityTypes", ethnicityTypes);
	handleErrorsAndMessages(model);
	return "offenderUpdate";
    }
    @PostMapping("/offender/update/{id}")
    public String updateOffender(@PathVariable("id") int id,
				 @Valid Offender offender, 
				 BindingResult result,
				 Model model,
				 HttpSession session
				 ) {
	model.addAttribute("app_url", app_url);
	if (result.hasErrors()) {
	    String error = Helper.extractErrors(result);
	    addError(error);
	    logger.error("Error update offender "+error);
	    offender.setId(id);
	    return "redirect:/offender/edit/"+id;
	    
	}
	Incident incident = offender.getIncident();
	if(incident == null || !incident.canBeChanged()){
	    addMessage("no more changes can be made");
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/";	    
	}
	int incident_id = incident.getId();
	if(!verifySession(session, ""+incident_id)){				
	    addMessage("No more changes can be made ");
	    addMessagesAndErrorsToSession(session);
	    return "redirect:/";
	}							
	if(!offender.verify()){
	    String error = offender.getErrorInfo();
	    addError(error);
	    List<RaceType> raceTypes = raceTypeService.getAll();
	    model.addAttribute("sexTypes", sexTypes);
	    model.addAttribute("raceTypes", raceTypes);				
	    model.addAttribute("phoneTypes", phoneTypes);
	    model.addAttribute("offenderTitles", offenderTitles);
	    model.addAttribute("genderTypes", genderTypes);
	    model.addAttribute("ethnicityTypes", ethnicityTypes);
	    handleErrorsAndMessages(model);
	    return "offenderUpdate";						
	}
	offenderService.update(offender);
	addMessage("Updated Successfully");				
	addMessagesAndErrorsToSession(session);
	return "redirect:/businessIncident/"+incident.getId();
    }
    
    @GetMapping("/offender/delete/{id}")
    public String deleteOffender(@PathVariable("id") int id,
				 Model model,
				 HttpSession session
				 ) {
	
	Offender offender = null;
	Incident incident = null;
	model.addAttribute("app_url", app_url);
	try{
	    offender = offenderService.findById(id);
	    if(offender != null)
		incident = offender.getIncident();
	    if(incident == null || !incident.canBeChanged()){
		addMessage("no more changes can be made");
		addMessagesAndErrorsToSession(session);
		return "redirect:/";	    
	    }
	    int incident_id = incident.getId();
	    if(!verifySession(session, ""+incident_id)){				
		addMessage("No more changes can be made ");
		addMessagesAndErrorsToSession(session);
		return "redirect:/";
	    }							
	    offenderService.delete(id);
	    addMessage("Deleted Succefully");
	}catch(Exception ex){
	    logger.error("Error delete offender "+id+" "+ex);
	    addError("Invalid offender ID "+id);
	}
	addMessagesAndErrorsToSession(session);
	return "redirect:/businessIncident/"+incident.getId();
	
    }
    
}
