package in.bloomington.incident.control;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */
import java.util.UUID;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
//
// implementation of logging is logback
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import in.bloomington.incident.model.User;
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.IncidentIncomplete;
import in.bloomington.incident.control.IncompleteJob;
import in.bloomington.incident.service.IncidentIncompleteService;

@Controller
public class IncompleteController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(IncompleteController.class);
		
    @Autowired
    private Scheduler scheduler;
    @Autowired
    IncidentIncompleteService incompleteService;
    String jobName = "incomplete_job";
    String groupName = "incomplete_group";


    private JobDetail buildJobDetail(){
	return JobBuilder.newJob(IncompleteJob.class)
	    .withIdentity(jobName, groupName)
	    .withDescription("Send Email Job")
	    //.usingJobData(jobDataMap)
	    .storeDurably()
	    .build();
    }
    private Trigger buildJobTrigger(JobDetail jobDetail) {
	java.util.Calendar cal = Calendar.getInstance();
	cal.set(Calendar.HOUR_OF_DAY, 7);//to run at 7am of the day
	cal.set(Calendar.MINUTE, 0);
	Date startTime = cal.getTime();
	return TriggerBuilder.newTrigger()
	    .withIdentity(jobName, groupName)
	    .startAt(startTime)
	    .withSchedule(
			  SimpleScheduleBuilder.simpleSchedule()
			  // .withIntervalInMinutes(3)
			  .withIntervalInHours(24) // every day
			  .repeatForever()
			  // .withRepeatCount(2) 
			  // .withMisfireHandlingInstructionFireNow())
			  .withMisfireHandlingInstructionIgnoreMisfires())
	    // .endAt(endDate)						  
	    .build();
    }
    private String startSchedule(){
	String back = "";
	try{
	    JobDetail jobDetail = buildJobDetail();
	    Trigger trigger = buildJobTrigger(jobDetail);
	    scheduler.scheduleJob(jobDetail, trigger);
	}
	catch(Exception ex){
	    back += ex;
	}
	return back;
    }
    
    @GetMapping("/staff/incompleteOptions")
    public String incompleteOptions(Model model,
				    HttpSession session
				    ) {
	List<Incident> all = null;
	List<IncidentIncomplete> plist = incompleteService.getAll();
	if(plist != null){
	    all = new ArrayList<>();
	    for(IncidentIncomplete one:plist){
		Incident incident = one.getIncident();
		if(incident != null)
		    all.add(incident);
	    }
	}
	if(all != null && all.size() > 0){
	    // 
	    model.addAttribute("incidents", all);
	}
	else{
	    addMessage("No incident found");
	    model.addAttribute("messages", messages);
	}
        return "staff/incompleteOptions";
    }    
    @GetMapping("/staff/incompleteAction")
    public String incompleteAction(@RequestParam String action,
				   Model model,
				   HttpSession session
			      ){
	boolean schedule_flag = false, run_flag=false;
	if(action !=null){
	    if(action.equals("Schedule")){
		schedule_flag = true;
	    }
	    else if(action.equals("Run")){
		run_flag = true;
	    }
	}
	if(run_flag){
	    List<Incident> all = null;
	    List<IncidentIncomplete> plist = incompleteService.getAll();
	    if(plist != null){
		all = new ArrayList<>();
		for(IncidentIncomplete one:plist){
		    Incident incident = one.getIncident();
		    if(incident != null)
			all.add(incident);
		}
	    }
	}
	else if(schedule_flag){
	    back = startSchedule();
	    if(!back.isEmpty()){
		addError(back);
	    }
	}
        return "staff/incomplete";
    }
    

		
}
