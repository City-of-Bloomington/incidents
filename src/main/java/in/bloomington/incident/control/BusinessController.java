package in.bloomington.incident.control;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import java.util.Date;
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
import in.bloomington.incident.service.UserService;

import in.bloomington.incident.model.Business;
import in.bloomington.incident.model.Address;
// import in.bloomington.incident.model.Credential;

import in.bloomington.incident.utils.Helper;

@Controller
public class BusinessController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(BusinessController.class);
    @Autowired
    BusinessService businessService;
    @Autowired
    AddressService addressService;		
		@Autowired
		UserService userService;		
		@Autowired 
    private HttpSession session;
		
    @GetMapping("/business/add/{addr_id}/{type_id}")
    public String newBusiness(@PathVariable("addr_id") int addr_id,
															@PathVariable("type_id") int type_id,
															Model model) {
				Business business = new Business();
				business.setType_id(type_id);
				Address addr = addressService.findById(addr_id);
				if(addr != null){
						business.setAddress(addr);
				}
        model.addAttribute("business", business);
        return "businessAdd";
    }     
    @PostMapping("/business/save")
    public String addBusiness(@Valid Business business,
															BindingResult result,
															Model model
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
				int addr_id = business.getAddr_id();
				if(addr_id > 0){
						Address addr = addressService.findById(addr_id);
						if(addr != null){
								business.setAddress(addr);
						}
				}
        businessService.save(business);
				addMessage("Business Added Successfully");				
				int id = business.getId();
				addMessagesAndErrorsToSession(session);
				//
				// to adding incident address
				return "redirect:/businessIncidentAddressAdd/"+id+"/"+business.getType_id();
    }

    @GetMapping("/businessEdit/{id}/{type_id}")
    public String showEditForm(@PathVariable("id") int id,
															 @PathVariable("type_id") int type_id,
															 Model model
															 ) {
				Business business = null;
				try{
						business = businessService.findById(id);
				}catch(Exception ex){
						addError("Invalid business "+id);
						logger.error(" "+ex);
						addMessagesAndErrorsToSession(session);
						return "redirect:/businesses";
				}
				if(type_id > 0)
						business.setType_id(type_id);
				model.addAttribute("business", business);
				handleErrorsAndMessages(model);
				return "businessEdit";
    }
    @PostMapping("/businessUpdate/{id}")
    public String updateBusiness(@PathVariable("id") int id,
																 @Valid Business business, 
																 BindingResult result,
																 Model model) {
				if (result.hasErrors()) {
						String error = Helper.extractErrors(result);
						error += " Error update business "+id;
						addError(error);
						logger.error(error);
						business.setId(id);
						addMessagesAndErrorsToSession(session);
						return "redirect:/businessEdit/"+id;
				}
				if(!business.verify()){
						String error = business.getErrorInfo();
						addError(error);
						logger.error(error);
						handleErrorsAndMessages(model);
						return "redirect:/businessEdit/"+id;						
				}
				business.setId(id);
				businessService.update(business);				
				addMessage("Updated Successfully");
				addMessagesAndErrorsToSession(session);
				return "redirect:/businessIncidentAddressAdd/"+id+"/"+business.getType_id();
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
				getMessagesAndErrorsFromSession(session, model);		
				return "businessView";
    }
    @GetMapping("/businesses")
    public String viewBusinesses(Model model) {

				if(session == null || session.getAttribute("user") == null){
						return "staff/loginForm";
				}
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
				getMessagesAndErrorsFromSession(session, model);		
				handleErrorsAndMessages(model);
				return "businessesView";
    }

		/**
		//
		//for business password reset
		//			 
    @GetMapping("/credential/{id}")
    public String credentialReset(@PathVariable("id") int id, Model model) {

				try{
						if(session == null || session.getAttribute("user") == null){
								return "staff/loginForm";
						}
						Credential credential = credentialService.findById(id);
						model.addAttribute("credential", credential);
						getMessagesAndErrorsFromSession(session, model);						
				}catch(Exception ex){
						addError("Credential not found ID: "+id);
						logger.error(" "+ex);
				}
				handleErrorsAndMessages(model);
				return "staff/passwordReset";
    }
		*/
		/**
    @PostMapping("/credential/update/{id}")
    public String updateBusiness(@PathVariable("id") int id,
																 @Valid Credential credential, 
																 BindingResult result,
																 Model model) {
				if(session == null || session.getAttribute("user") == null){
						return "staff/loginForm";
				}
				credential.setId(id);
				if (result.hasErrors()) {
						String error = Helper.extractErrors(result);
						error += " Error update business "+id;
						addError(error);
						logger.error(error);
						credential.setId(id);
						addMessagesAndErrorsToSession(session);
						return "redirect:/credential/"+id;
				}
				if(!credential.verify()){
						String error = credential.getErrorInfo();
						addError(error);
						logger.error(error);
						addMessagesAndErrorsToSession(session);
						return "redirect:/credential/"+id;
				}
				if(credential.isEmailChanged()){
						System.err.println(" email is changed ");
						Business business = credential.getBusiness();
						if(business != null){
								business.setEmail(credential.getEmail());
								businessService.update(business);
						}
						else{
								System.err.println(" business is null ");
						}
				}
				try{
						String encrypted = userService.encryptString(credential.getPassword());
						if(encrypted != null){
								credential.setPassword(encrypted);
								credential.setLastUpdate(new Date());
								credentialService.update(credential);
								Business bus = credential.getBusiness();
								addMessage("Updated Successfully");
								addMessagesAndErrorsToSession(session);
								if(bus != null){
										return "redirect:/business/"+bus.getId();
								}
								else{
										return "redirect:/businesses";
								}
						}
				}catch(Exception ex){
						System.err.println(ex);
						addError("encyption error "+ex);
				}
				addMessagesAndErrorsToSession(session);
				return "redirect:/credential/"+id;						
    }
		*/
}
