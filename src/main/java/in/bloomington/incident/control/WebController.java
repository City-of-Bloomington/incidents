package in.bloomington.incident.control;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import javax.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController extends TopController{

		@Autowired 
    private HttpSession session;	
		
    @GetMapping("/index")		
    public String indexStart(Model model
														 ) {
				getMessagesAndErrorsFromSession(session, model);
				return "intro_questions";
    }
    
    @RequestMapping(value = "/all")
    public String introStart(Model model
														 ){
				getMessagesAndErrorsFromSession(session, model);
				return "intro_all";
    }
		// for testing purpose only
    @RequestMapping(value = "/staff_menu")
    public String staffMenu(Model model
														){
				getMessagesAndErrorsFromSession(session, model);
				return "staff_menu";
    }
    @RequestMapping(value = "/")
    public String introPersonal(Model model
														 ){
				getMessagesAndErrorsFromSession(session, model);
				return "intro_questions";
    }
    @RequestMapping(value = "/forBusiness")
    public String forBusiness(Model model
														 ){
				getMessagesAndErrorsFromSession(session, model);
				return "business_questions";
    }
    @RequestMapping(value = "/forbusiness")
    public String forBusLower(Model model
														 ){
				return "redirect/forBusiness";
    }						
		
    @RequestMapping(value = "/selectCategory")
    public String selectCategory(Model model
																 ){
				getMessagesAndErrorsFromSession(session, model);
				return "introSelectType";
    }
		
    @RequestMapping("/introTheft/{type_id}")
    public String introTheft(@PathVariable("type_id") int type_id,
														 Model model
														 ) {
				getMessagesAndErrorsFromSession(session, model);
				model.addAttribute("type_id", type_id);
				return "theft_questions";
    }
    @RequestMapping("/introFraud")
    public String introFraud(Model model
														 ) {
				getMessagesAndErrorsFromSession(session, model);
				// we do not have special questions for fraud right now
				// so we go to email page directly
				return "redirect:/addressInput/6";				
    }   		
    @RequestMapping("/introVandal")
    public String introVandal(Model model
															) {
				getMessagesAndErrorsFromSession(session, model);
				return "redirect:/addressInput/2";
    }       
    @RequestMapping("/introLost")
    public String introLost(Model model
														) {
				getMessagesAndErrorsFromSession(session, model);
				return "lost_questions";
    }
    @RequestMapping("/businessSteps")
    public String busSteps(Model model
														) {
				getMessagesAndErrorsFromSession(session, model);
				return "introBusinessSteps";
    }		

    @RequestMapping("/emailAdd")
    public String emailAdd(@RequestParam(required=true) int type_id,
													 @RequestParam(required=true) int address_id,
													 Model model
													 ){
				getMessagesAndErrorsFromSession(session, model);
        model.addAttribute("type_id", type_id);
				model.addAttribute("address_id", address_id);
				return "emailAdd";
    }


}
