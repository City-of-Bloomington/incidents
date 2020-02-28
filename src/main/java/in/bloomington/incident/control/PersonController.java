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
// import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import in.bloomington.incident.service.IncidentService;
import in.bloomington.incident.service.PersonService;
import in.bloomington.incident.service.PersonTypeService;

import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.Person;
import in.bloomington.incident.model.PersonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class PersonController {

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
		String errors="", messages="";
		public String getErrors(){
				return errors;
		}
		public String getMessages(){
				return messages;
		}

		@RequestMapping("/person/add/{incident_id}")
    public String addPerson(@PathVariable("incident_id") int incident_id, Model model) {
				Incident incident = null;
				try{
						incident = incidentService.findById(incident_id);
				}catch(Exception ex){
						errors += "Invalid incident Id: "+incident_id;
						model.addAttribute("errors", errors);
						return "redirect:/start";
				}
				List<PersonType> personTypes = personTypeService.getAll();
				Person person = new Person();
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
														 Model model
														 ) {
				System.err.println(" *** person save *** ");				
        if (result.hasErrors()) {
						errors = "";
						System.err.println(" *** got error *** ");
						for (ObjectError error : result.getAllErrors()) {
								// if (error instanceof FieldError) {
										if(!errors.equals("")) errors += " ";
										errors += error.getObjectName() + " - " + error.getDefaultMessage();
						}
						System.err.println(" **** errors "+errors);
						List<PersonType> personTypes = personTypeService.getAll();
						model.addAttribute("errors", errors);
						model.addAttribute("person", person);
						model.addAttribute("sexTypes", sexTypes);
						model.addAttribute("raceTypes", raceTypes);
						model.addAttribute("phoneTypes", phoneTypes);
						model.addAttribute("personTitles", personTitles);
						model.addAttribute("personTypes", personTypes);						
						return "personAdd";
        }
				/*
				if(!incident.verifyAll(defaultCity,
															 defaultState,
															 zipCodes)){
						errors = incident.getErrorInfo();
						model.addAttribute("errors", errors);
						model.addAttribute("entryTypes", entryTypes);

						return "addIncident";
				}
				*/
        personService.save(person);
        return "redirect:/incident/"+person.getIncident().getId(); 
    }
		
		@GetMapping("/person/{id}")
		public String showPerson(@PathVariable("id") int id, Model model) {
				Person person = null;
				try{
						person = personService.findById(id);
				}catch(Exception ex){
						errors += "Invalid person Id "+id;
						model.addAttribute("errors", errors);
						return "index"; // need fix
				}
        model.addAttribute("person", person);				
				return "person";
		}
		
		@GetMapping("/person/edit/{id}")
		public String showEditForm(@PathVariable("id") int id, Model model) {
				Person person = null;
				try{
						person = personService.findById(id);
						
				}catch(Exception ex){
						errors += "Invalid person Id";
						model.addAttribute("errors", errors);
						return " "; // need fix
				}
				List<PersonType> personTypes = personTypeService.getAll();
				model.addAttribute("person", person);
				model.addAttribute("sexTypes", sexTypes);
				model.addAttribute("raceTypes", raceTypes);				
				model.addAttribute("phoneTypes", phoneTypes);
				model.addAttribute("personTitles", personTitles);
				model.addAttribute("personTypes", personTypes);				
				return "personUpdate";
		}
		@PostMapping("/person/update/{id}")
		public String updatePerson(@PathVariable("id") int id,
															 @Valid Person person, 
															 BindingResult result,
															 Model model) {
				if (result.hasErrors()) {
						person.setId(id);
						return "redirect:/person/edit/"+id;
						
				}
				messages = "Updated Successfully";
				Incident incident = person.getIncident();
				personService.update(person);
				model.addAttribute("messages", messages);
				return "redirect:/incident/"+incident.getId();
		}
		
		@GetMapping("/person/delete/{id}")
		public String deletePerson(@PathVariable("id") int id, Model model) {

				Person person = null;
				Incident incident = null;
				try{
						person = personService.findById(id);
						incident = person.getIncident();
						personService.delete(id);
						messages = "Deleted Succefully";
				}catch(Exception ex){
						errors += "Invalid person ID "+id;
				}
				return "redirect:/incident/"+incident.getId();

		}
		
}
