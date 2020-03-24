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
import in.bloomington.incident.service.PersonService;
import in.bloomington.incident.service.PersonTypeService;

import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.Person;
import in.bloomington.incident.model.PersonType;
import in.bloomington.incident.model.User;
import in.bloomington.incident.utils.Helper;


@Controller
public class PersonController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(PersonController.class);
    @Autowired
    IncidentService incidentService;
    @Autowired
    PersonService personService;		
    @Autowired
    PersonTypeService personTypeService;
    //
    private List<String> raceTypes = 
	new ArrayList<>(Arrays.asList("Caucasion",
				      "Hispanic",
				      "African American",
				      "Native American",
				      "Asian",
				      "Other"));
    private List<String> phoneTypes = 
	new ArrayList<>(Arrays.asList("Cell","Home","Work"));
    private List<String> personTitles =
	new ArrayList<>(Arrays.asList("Mr","Ms","Mrs"));
    private List<String> sexTypes =
	new ArrayList<>(Arrays.asList("Male","Female","Other"));		
    
    @RequestMapping("/person/add/{incident_id}")
    public String addPerson(@PathVariable("incident_id") int incident_id,
			    Model model,
			    HttpSession session) {
	Incident incident = null;
	try{
	    incident = incidentService.findById(incident_id);
	}catch(Exception ex){
	    addError("Invalid incident Id: "+incident_id);
	    logger.error(""+ex);
	    model.addAttribute("errors", errors);
	    return "redirect:/start";
	}
	if(incident == null || !incident.canBeChanged()){
	    addMessage("No more changes can be made");
	    addMessagesToSession(session);
	    return "redirect:/incident/"+incident_id;
	}
	Person person = new Person();				
	List<PersonType> personTypes = personTypeService.getAll();
	person.setIncident(incident);
	model.addAttribute("person", person);
	model.addAttribute("sexTypes", sexTypes);
	model.addAttribute("raceTypes", raceTypes);
	model.addAttribute("phoneTypes", phoneTypes);
	model.addAttribute("personTitles", personTitles);
	model.addAttribute("personTypes", personTypes);
        return "personAdd";
    }
    @PostMapping("/person/save")
    public String personSave(@Valid Person person,
			     BindingResult result,
			     Model model,
			     HttpSession session
			     ) {
        if (result.hasErrors()) {
	    String error = Helper.extractErrors(result);
	    addError(error);
	    logger.error(error);
	    List<PersonType> personTypes = personTypeService.getAll();
	    model.addAttribute("errors", errors);
	    model.addAttribute("person", person);
	    model.addAttribute("sexTypes", sexTypes);
	    model.addAttribute("raceTypes", raceTypes);
	    model.addAttribute("phoneTypes", phoneTypes);
	    model.addAttribute("personTitles", personTitles);
	    model.addAttribute("personTypes", personTypes);
	    model.addAttribute("errors", errors);
	    return "personAdd";
        }
	Incident incident = person.getIncident();
	if(incident == null || !incident.canBeChanged()){
	    addMessage("No more changes can be made");
	    addMessagesToSession(session);
	    return "redirect:/";
	}	
	if(!person.verify()){
	    String error = person.getErrorInfo();
	    addError(error);
	    List<PersonType> personTypes = personTypeService.getAll();
	    model.addAttribute("sexTypes", sexTypes);
	    model.addAttribute("raceTypes", raceTypes);				
	    model.addAttribute("phoneTypes", phoneTypes);
	    model.addAttribute("personTitles", personTitles);
	    model.addAttribute("personTypes", personTypes);		
	    model.addAttribute("errors", errors);
	    return "personAdd";
	}
        personService.save(person);
	addMessage("Saved Succefully");
	addMessagesToSession(session);
	return "redirect:/incident/"+incident.getId(); 
    }
    
    @GetMapping("/person/{id}")
    public String showPerson(@PathVariable("id") int id, Model model) {
	Person person = null;
	try{
	    person = personService.findById(id);
	}catch(Exception ex){
	    addError("Invalid person Id "+id);
	    logger.error(" "+ex);
	    model.addAttribute("errors", errors);
	    return "index"; // need fix
	}
        model.addAttribute("person", person);				
	return "person";
    }
    //login staff
    @GetMapping("/personView/{id}")
    public String viewPerson(@PathVariable("id") int id,
			     Model model,
			     HttpSession session) {
	Person person = null;
	User user = getUserFromSession(session);
	if(user == null){
	    return "redirect:/login";
	}
	try{
	    person = personService.findById(id);
	}catch(Exception ex){
	    addError("Invalid person Id "+id);
	    logger.error(" "+ex);
	    model.addAttribute("errors", errors);
	    return "index"; // need fix
	}
        model.addAttribute("person", person);				
	return "personView";
    }    
    
    @GetMapping("/person/edit/{id}")
    public String showEditForm(@PathVariable("id") int id,
			       Model model,
			       HttpSession session) {
	Person person = null;
	try{
	    person = personService.findById(id);
	    
	}catch(Exception ex){
	    addError("Invalid person Id "+id);
	    model.addAttribute("errors", errors);
	    logger.error(" "+ex);
	    return "index"; // need fix
	}
	Incident incident = person.getIncident();
	if(incident == null || !incident.canBeChanged()){
	    addMessage("no more changes can be made");
	    addMessagesToSession(session);
	    return "redirect:/";	    
	}
	List<PersonType> personTypes = personTypeService.getAll();
	model.addAttribute("person", person);
	model.addAttribute("sexTypes", sexTypes);
	model.addAttribute("raceTypes", raceTypes);				
	model.addAttribute("phoneTypes", phoneTypes);
	model.addAttribute("personTitles", personTitles);
	model.addAttribute("personTypes", personTypes);
	if(hasErrors()){
	    model.addAttribute("errors", errors);
	}
	return "personUpdate";
    }
    @PostMapping("/person/update/{id}")
    public String updatePerson(@PathVariable("id") int id,
			       @Valid Person person, 
			       BindingResult result,
			       Model model,
			       HttpSession session
			       ) {
	if (result.hasErrors()) {
	    String error = Helper.extractErrors(result);
	    addError(error);
	    logger.error("Error update person "+error);
	    person.setId(id);
	    return "redirect:/person/edit/"+id;
	    
	}
	Incident incident = person.getIncident();
	if(incident == null || !incident.canBeChanged()){
	    addMessage("no more changes can be made");
	    addMessagesToSession(session);
	    return "redirect:/";	    
	}	
	if(!person.verify()){
	    String error = person.getErrorInfo();
	    addError(error);
	    List<PersonType> personTypes = personTypeService.getAll();
	    model.addAttribute("sexTypes", sexTypes);
	    model.addAttribute("raceTypes", raceTypes);				
	    model.addAttribute("phoneTypes", phoneTypes);
	    model.addAttribute("personTitles", personTitles);
	    model.addAttribute("personTypes", personTypes);		
	    model.addAttribute("errors", errors);
	    return "personUpdate";						
	}
	personService.update(person);
	addMessage("Updated Successfully");				
	addMessagesToSession(session);
	return "redirect:/incident/"+incident.getId();
    }
    
    @GetMapping("/person/delete/{id}")
    public String deletePerson(@PathVariable("id") int id,
			       Model model,
			       HttpSession session
			       ) {
	
	Person person = null;
	Incident incident = null;
	incident = person.getIncident();
	if(incident == null || !incident.canBeChanged()){
	    addMessage("no more changes can be made");
	    addMessagesToSession(session);
	    return "redirect:/";	    
	}		
	try{
	    person = personService.findById(id);

	    personService.delete(id);
	    addMessage("Deleted Succefully");
	}catch(Exception ex){
	    logger.error("Error delete person "+id+" "+ex);
	    addError("Invalid person ID "+id);
	}
	return "redirect:/incident/"+incident.getId();
	
    }
    
}
