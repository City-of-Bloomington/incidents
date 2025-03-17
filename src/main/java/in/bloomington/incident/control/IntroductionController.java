package in.bloomington.incident.control;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import javax.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Value;

@Controller
public class IntroductionController extends TopController{
	@Autowired 
    private HttpSession session;
	
    @RequestMapping(value = "/")
    public String introPersonal(Model model){
	getMessagesAndErrorsFromSession(session, model);
	model.addAttribute("app_url", app_url);
	System.err.println(" app_url "+app_url);
	resetAll();
	return "intro/intro";
    }

	@PostMapping("/emergencyQuestion")
	public String emergencyQuestion(@RequestParam("question1") String answer, Model model) {
		if ("Yes".equals(answer)) {
			return "redirect:/emergency";
		} else {
			return "redirect:/age";
		}
	}
	@GetMapping(value = "/emergency")
    public String emergency(Model model){
	getMessagesAndErrorsFromSession(session, model);
	model.addAttribute("app_url", app_url);
	System.err.println(" app_url "+app_url);
	resetAll();
        return "intro/stop/emergency";
    }
	@GetMapping(value = "/age")
    public String age(Model model){
	getMessagesAndErrorsFromSession(session, model);
	model.addAttribute("app_url", app_url);
	System.err.println(" app_url "+app_url);
	resetAll();
        return "intro/age";
    }

	@PostMapping("/ageQuestion")
	public String ageQuestion(@RequestParam("question2") String answer, Model model) {
		if ("No".equals(answer)) {
			return "redirect:/underage";
		} else {
			return "redirect:/suspect";
		}
	}
	@GetMapping(value = "/underage")
    public String underage(Model model){
	getMessagesAndErrorsFromSession(session, model);
	model.addAttribute("app_url", app_url);
	System.err.println(" app_url "+app_url);
	resetAll();
        return "intro/stop/underage";
	}
	@GetMapping(value = "/suspect")
    public String suspect(Model model){
	getMessagesAndErrorsFromSession(session, model);
	model.addAttribute("app_url", app_url);
	System.err.println(" app_url "+app_url);
	resetAll();
        return "intro/suspect";
    }

	@PostMapping("/suspectQuestion")
	public String suspectQuestion(@RequestParam("question3") String answer, Model model) {
		if ("Yes".equals(answer)) {
			return "redirect:/suspect_info";
		} else {
			return "redirect:/injury";
		}
	}
	@GetMapping(value = "/suspect_info")
    public String suspect_info(Model model){
	getMessagesAndErrorsFromSession(session, model);
	model.addAttribute("app_url", app_url);
	System.err.println(" app_url "+app_url);
	resetAll();
        return "intro/stop/suspect_info";
	}
	@GetMapping(value = "/injury")
    public String injury(Model model){
	getMessagesAndErrorsFromSession(session, model);
	model.addAttribute("app_url", app_url);
	System.err.println(" app_url "+app_url);
	resetAll();
        return "intro/injury";
	}

	@PostMapping("/injuryQuestion")
	public String injuryQuestion(@RequestParam("question4") String answer, Model model) {
		if ("Yes".equals(answer)) {
			return "redirect:/injury_info";
		} else {
			return "redirect:/selectCategory";
		}
	}
	@GetMapping(value = "/injury_info")
    public String injury_info(Model model){
	getMessagesAndErrorsFromSession(session, model);
	model.addAttribute("app_url", app_url);
	System.err.println(" app_url "+app_url);
	resetAll();
        return "intro/stop/injury_info";
	}

}