package in.bloomington.incident.control;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
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
// import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import in.bloomington.incident.service.DamageTypeService;
import in.bloomington.incident.model.DamageType;


@Controller
public class ProcessController extends TopController{

		
		@GetMapping("/rejectAction")
    public String rejectAction(Model model) {
				//
				// will show reject form
        return "rejectForm";
    }
		@GetMapping("/rejectEmail")
    public String rejectEmail(Model model) {
				//
				// sending reject email
				// going back to the incidents list for staff
				//
        return "staff_menu";
    }		
		@GetMapping("/approveAction")
    public String approveAction(Model model) {
				//
				// 
        return "";
    }
		@GetMapping("/processAction")
    public String processAction(Model model) {
				//
				// here will send approve email with cfs #
        return "";
    }		
		private String sendApproveEmail(Incident Incident){
				String ret = "";
				if(!incident.hasCfsNumber()){
						ret = "incident has no CSF number";
						return ret;
				}
				if(!Incident.hasPersonList()){
						ret = "incident has no person to send email to";
						return ret;
				}
				List<Person> persons = incident.getPersons();
				Person person = persons.get(0); // we need one
				String email = person.getEmail();
				String subject = "Incident Reporting Approval";
				String from = "\"incident_reporting@bloomington.in.gov\",'-fwebmaster@bloomington.in.gov'";
				String message = "Dear "+person.getFullname()+
						"\n\n Your report has been approved. "+
						"The CFS Number of your report is "+Incident.getCfsNumber()+

						" You can use this number in your future contacts with the Bloomington Police Department. \n\n"+
						"Please do not reply to this email as this is an automated system.";
				//
				// send the email
				
		}
		
		
}
