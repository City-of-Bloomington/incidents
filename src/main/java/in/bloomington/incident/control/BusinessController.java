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
import in.bloomington.incident.service.AddressService;
import in.bloomington.incident.service.CredentialService;

import in.bloomington.incident.model.Business;
import in.bloomington.incident.model.Address;
import in.bloomington.incident.model.Credential;

import in.bloomington.incident.utils.Helper;

@Controller
public class BusinessController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(BusinessController.class);
    @Autowired
    BusinessService businessService;
    @Autowired
    AddressService addressService;		
		@Autowired
		CredentialService credentialService;
		
    @GetMapping("/business/add/{addr_id}")
    public String newBusiness(@PathVariable("addr_id") int addr_id,
															Model model,
															HttpSession session) {
				
				Business business = new Business();
				Address addr = addressService.findById(addr_id);
				if(addr != null)
						business.setAddress(addr);
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
				Credential credit = new Credential();
				credit.setEmail(business.getEmail());
				credit.setBusiness(business);
				credentialService.save(credit);
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
						return "redirect:/businesses";
				}
				model.addAttribute("business", business);
				handleErrorsAndMessages(model);
				return "businessUpdate";
    }
    @PostMapping("/business/update/{id}")
    public String updateBusiness(@PathVariable("id") int id,
																 @Valid Business business, 
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
						return "redirect:/business/edit/"+id;						
				}
				businessService.update(business);
				if(business.isEmailChanged()){
						Credential credit = business.getCredential();
						if(credit != null){
								credit.setEmail(business.getEmail());
								credentialService.update(credit);
						}
				}
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
    @GetMapping("/businesses")
    public String viewBusinesses(Model model) {

				try{
						List<Business> businesses = businessService.getAll();
						if(businesses == null || businesses.size() == 0){
								model.addAttribute("message","No business found ");
						}
						else{
								model.addAttribute("businesses", businesses);
								model.addAttribute("message","Found "+businesses.size());
						}
				}catch(Exception ex){
						logger.error(" "+ex);
				}
				handleErrorsAndMessages(model);
				return "businessesView";
    }		
		
}
