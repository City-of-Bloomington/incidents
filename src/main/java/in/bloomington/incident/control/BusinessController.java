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
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Value;
import in.bloomington.incident.service.BusinessService;
import in.bloomington.incident.service.IncidentService;
import in.bloomington.incident.service.DamageTypeService;
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.Business;
import in.bloomington.incident.model.DamageType;
import in.bloomington.incident.utils.Helper;

@Controller
public class BusinessController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(BusinessController.class);
    @Autowired
    BusinessService businessService;
    @Autowired
    IncidentService incidentService;		

    @GetMapping("/business/add")
    public String newBusiness(
															Model model,
															HttpSession session) {
				
				Business business = new Business();
        model.addAttribute("business", business);
        return "businessAdd";
    }     
    @PostMapping("/business/save")
    public String addBusiness(@Valid Business business,
															BindingResult result,
															Model model,
															HttpSession session
															) {
        if (result.hasErrors()) {
						String error = Helper.extractErrors(result);
						addError(error);
						logger.error(error);
						handleErrorsAndMessages(model);
            return "businessAdd";
        }
				if(!business.verify()){
						String error = business.getErrorInfo();
						addError(error);
						logger.error(error);
						handleErrorsAndMessages(model);
						return "businessAdd";						
				}
        businessService.save(business);
				addMessage("Business Added Successfully");
				int id = business.getId();
				addMessagesAndErrorsToSession(session);
				return "redirect:/business/"+id;
    }

    @GetMapping("/business/edit/{id}")
    public String showEditForm(@PathVariable("id") int id,
															 Model model,
															 HttpSession session) {
				Business business = null;
				try{
						business = businessService.findById(id);
				}catch(Exception ex){
						addError("Invalid business "+id);
						logger.error(" "+ex);
						addMessagesAndErrorsToSession(session);
						return "redirect:/index";
				}
				model.addAttribute("business", business);
				handleErrorsAndMessages(model);
				return "businessUpdate";
    }
    @PostMapping("/business/update/{id}")
    public String updateBusiness(@PathVariable("id") int id, @Valid Business business, 
																 BindingResult result,
																 Model model,
																 HttpSession session) {
				if (result.hasErrors()) {
						String error = Helper.extractErrors(result);
						error += " Error update business "+id;
						addError(error);
						logger.error(error);
						business.setId(id);
						addMessagesAndErrorsToSession(session);
						return "redirect:/business/edit/"+id;
				}
				if(!business.verify()){
						String error = business.getErrorInfo();
						addError(error);
						logger.error(error);
						handleErrorsAndMessages(model);
						return "businessUpdate";						
				}
				businessService.save(business);
				addMessage("Updated Successfully");
				addMessagesAndErrorsToSession(session);
				return "redirect:/business/"+id;
    }
		
    @GetMapping("/business/{id}")
    public String viewBusiness(@PathVariable("id") int id, Model model) {

				try{
						Business business = businessService.findById(id);
						model.addAttribute("business", business);						
				}catch(Exception ex){
						addError("Invalid business "+id);
						logger.error(" "+ex);
				}
				handleErrorsAndMessages(model);
				return "businessView";
    }
		
}
