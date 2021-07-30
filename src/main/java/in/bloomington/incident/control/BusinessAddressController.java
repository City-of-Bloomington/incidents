package in.bloomington.incident.control;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
//
// implementation of logging is logback
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import in.bloomington.incident.util.AddressCheck;
import in.bloomington.incident.model.Item;
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.Address;
import in.bloomington.incident.service.AddressService;
import in.bloomington.incident.service.BusinessService;
import in.bloomington.incident.service.IncidentService;
import in.bloomington.incident.model.Business;
import in.bloomington.incident.utils.Helper;


@Controller
public class BusinessAddressController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(BusinessAddressController.class);

    @Autowired
    AddressCheck addressCheck;
    @Autowired
    AddressService addressService;
    @Autowired
    BusinessService businessService;
    @Autowired
    IncidentService incidentService;
		@Autowired 
    private HttpSession session;		
		
    @Value( "${incident.defaultcity}" )
    private String defaultCity;
    @Value( "${incident.defaultjurisdiction}" )
    private String defaultJurisdiction;    
    @Value( "${incident.defaultstate}" )
    private String defaultState;		
    @Value( "${incident.zipcodes}" )
    private List<String> zipCodes;
		

		/**
		 * business address (could be different from incident address)
		 */ 
    @CrossOrigin(origins = "https://bloomington.in.gov")
    @GetMapping("/businessAddrAdd/{incident_addr_id}") 
    public String businessAddrAdd(@PathVariable("incident_addr_id") int incident_addr_id,
																	Model model) {
				Address address = new Address();
				address.setIncident_addr_id(incident_addr_id);
				model.addAttribute("address", address);
        return "businessAddressAdd";
    }
    @CrossOrigin(origins = "https://bloomington.in.gov")
    @GetMapping("/businessAddrEdit/{id}/{incident_addr_id}")
    public String businessAddrUpdate(@PathVariable("id") int id,
																		 @PathVariable("incident_addr_id") int incident_addr_id,																		 
																			 Model model) {
				Address address = addressService.findById(id);
				address.setOld_id(id);
				address.setIncident_addr_id(incident_addr_id);
				model.addAttribute("address", address);
        return "businessAddressUpdate";
    }
    @PostMapping("/businessAddrSave")
    public String businessAddrSave(@Valid Address addr,
														 BindingResult result,
														 Model model
														 ) {
				boolean pass = true;
				Address address = null;
        if (result.hasErrors()) {
						addError(Helper.extractErrors(result));
						pass = false;
        }
				if(pass && checkAddress(addr)){
						address = saveOrUpdate(addr);
						return "redirect:/business/add/"+address.getId()+"/"+address.getIncident_addr_id();
				}
				addMessagesAndErrorsToSession(session);
				return "redirect:/businessAddrAdd/"+address.getIncident_addr_id();
		}
    @PostMapping("/businessAddrChange")
    public String businessAddrChange(@Valid Address addr,
														 BindingResult result,
														 Model model
														 ) {
				boolean pass = true;
        if (result.hasErrors()) {
						addError(Helper.extractErrors(result));
						pass = false;
        }
				if(pass){
						int old_id = addr.getOld_id();
						Address old_address = addressService.findById(old_id);
						//
						Address address = null;
						// check if the same address
						if(old_address.equals(addr)){
								//
								// go to next
								//
								address = addr;
						}
						else if(checkAddress(addr)){
							address = saveOrUpdate(addr);
						}
						return "redirect:/business/add/"+address.getId()+"/"+addr.getIncident_addr_id();
				}
				addMessagesAndErrorsToSession(session);
				return "redirect:/businessAddrEdit/"+addr.getOld_id()+"/"+addr.getIncident_addr_id();
		}
		
		/**
		 * incident address for a given business
		 */
    @CrossOrigin(origins = "https://bloomington.in.gov")
    @GetMapping("/businessIncidentAddressAdd")
    public String busIncidentAddrAdd(
																		 Model model) {
				Address address = new Address();
				// address.setIncident_addr_id(incident_addr_id);
				model.addAttribute("address", address);
				return "businessIncidentAddressAdd";
    }
    @PostMapping("/busIncidentAddrSave")
    public String busIncidentAddrSave(@Valid Address addr,
														 BindingResult result,
														 Model model
														 ) {
				boolean pass = true;
				Address address = null;
        if (result.hasErrors()) {
						addError(Helper.extractErrors(result));
						pass = false;
        }
				if(pass && checkAddress(addr)){
						address = saveOrUpdate(addr);
						return "redirect:/businessAddrAdd/"+address.getId();
				}
				addMessagesAndErrorsToSession(session);
				return "redirect:/businessIncidentAddressAdd";
		}		
		// from incident we find other parameters
    @CrossOrigin(origins = "https://bloomington.in.gov")
    @GetMapping("/businessIncidentAddrEdit/{id}")
		public String busIncidentAddrEdit(@PathVariable("id") int id,
																			Model model) {
				Address address = addressService.findById(id);
				address.setOld_id(id);				
				model.addAttribute("address", address);
				return "businessIncidentAddressUpdate";
    }				

		private boolean checkAddress(Address address){
				boolean pass = true;
				if(address != null){
						if(!address.verifyAddress(defaultCity,
																			defaultJurisdiction,
																			defaultState,
																			zipCodes)){
								pass = false;						
								addError(address.getErrorInfo());
						}
						if(pass && addressCheck.isInIUPDLayer(address.getLatitude(),
																							address.getLongitude())){
								pass = false;
								addError("This address is in IU Police Department district");
						}
				}
				return pass;
		}
		private Address saveOrUpdate(Address address){
				/**
				 * first we need to check if this address exist
				 */
				List<Address> addresses = addressService.findDistinctAddressByName(address.getName());
						
				if(addresses == null || addresses.size() == 0){
						System.err.println(" find address by name not found ");
						/**
						 * if not then we save
						 */
						addressService.save(address);								
				}
				else{
						System.err.println(" find address by name found "+addresses.size());
						// if exist we update 
								Address addr = addresses.get(0);
								address.setId(addr.getId());
								addressService.update(address);								
				}
				return address;
		}

    @PostMapping("/businessIncidentAddrChange")
    public String businessIncidentAddrChange(@Valid Address addr,
														 BindingResult result,
														 Model model
														 ) {
				boolean pass = true;
				Address address = null;
        if (result.hasErrors()) {
						addError(Helper.extractErrors(result));
						pass = false;
        }
				if(pass){
						int old_id = addr.getOld_id();
						Address old_address = addressService.findById(old_id);
						//
						// check if the same address
						if(old_address.equals(addr)){
								address = old_address;
						}
						else if(checkAddress(addr)){
								address = saveOrUpdate(addr);
						}
						// redirect to add business address
						return "redirect:/businessAddrAdd/"+address.getId();
				}
				addMessagesAndErrorsToSession(session);
				return "redirect:/businessIncidentAddrEdit/"+addr.getOld_id();
		}					
}


