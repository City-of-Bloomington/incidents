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


		
    @RequestMapping(value = "/")
    public String index() {
				return "redirect:/introStart";
    }
    @GetMapping("/index")		
    public String indexStart(Model model,
														 HttpSession session) {
				getMessagesAndErrorsFromSession(session, model);
				return "intro_all";
    }
    
    @RequestMapping(value = "/introStart")
    public String introStart(Model model,
														 HttpSession session){
				getMessagesAndErrorsFromSession(session, model);
				return "intro_all";
    }
    @RequestMapping(value = "/introPersonal")
    public String introPersonal(Model model,
														 HttpSession session){
				getMessagesAndErrorsFromSession(session, model);
				return "intro_questions";
    }		
    @RequestMapping(value = "/introBusiness")
    public String introBusiness(Model model,
														 HttpSession session){
				getMessagesAndErrorsFromSession(session, model);
				return "introBusiness";
    }		
		
    @RequestMapping(value = "/selectCategory")
    public String selectCategory(Model model,
																 HttpSession session){
				getMessagesAndErrorsFromSession(session, model);
				return "introSelectType";
    }		
    @RequestMapping("/introTheft/{type_id}")
    public String introTheft(@PathVariable("type_id") int type_id,
														 Model model,
														 HttpSession session) {
				getMessagesAndErrorsFromSession(session, model);
				model.addAttribute("type_id", type_id);
				return "theft_questions";
    }
    @RequestMapping("/businessIncident/{type_id}")
    public String businessIncident(@PathVariable("type_id") int type_id,
																	 Model model,
																	 HttpSession session) {
				getMessagesAndErrorsFromSession(session, model);
				return "redirect:/addressBusinessInput/"+type_id;
    }		

    @RequestMapping("/introFraud")
    public String introFraud(Model model,
														 HttpSession session) {
				getMessagesAndErrorsFromSession(session, model);
				// we do not have special questions for fraud right now
				// so we go to email page directly
				return "redirect:/addressInput/6";				
    }   		
    @RequestMapping("/introVandal")
    public String introVandal(Model model,
															HttpSession session) {
				getMessagesAndErrorsFromSession(session, model);
				return "redirect:/addressInput/2";
    }       
    @RequestMapping("/introLost")
    public String introLost(Model model,
														HttpSession session) {
				getMessagesAndErrorsFromSession(session, model);
				return "lost_questions";
    }

    @RequestMapping("/emailAdd")
    public String emailAdd(@RequestParam(required=true) int type_id,
													 @RequestParam(required=true) int address_id,
													 @RequestParam String category,
													 Model model,
													 HttpSession session
													 ){
				getMessagesAndErrorsFromSession(session, model);
        model.addAttribute("type_id", type_id);
				model.addAttribute("address_id", address_id);
				if(category != null && !category.isEmpty()){
						model.addAttribute("category", category);
				}
				return "emailAdd";
    }


}
