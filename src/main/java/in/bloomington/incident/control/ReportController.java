package in.bloomington.incident.control;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import java.util.ArrayList;
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
//
// implementation of logging is logback
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import in.bloomington.incident.service.ReportService;
import in.bloomington.incident.model.StatsRequest;
import in.bloomington.incident.model.IncidentStats;

@Controller
public class ReportController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(ReportController.class);
    final static String[] periods = {"Last Week","Last Month","Last Year","Other Specify"};
    @Autowired
    ReportService reportService;
    
    @GetMapping("/report")
    public String reportStats(Model model) {
	StatsRequest report = new StatsRequest();
	model.addAttribute("report", report);
	model.addAttribute("periods", periods);	
	if(hasErrors())
	    model.addAttribute("errors", getErrors());
	if(hasMessages()){
	    model.addAttribute("messages", getMessages());
	}
        return "staff/report";
    }
    @PostMapping("/report/find")
    public String searchFind(@Valid StatsRequest report,
			    BindingResult result,
			    Model model) {
        if (result.hasErrors()) {
	    logger.error(" Error creating new action ");
            return "redirect:/report";
        }
        List<IncidentStats> stats = reportService.getActionStats(report);
	if(stats == null && stats.size() == 0){
	    addMessage(" no match found ");
	    return "redirect:/report";
	}
	System.err.println(" *** stats size "+stats.size());
        model.addAttribute("stats", stats);
	if(hasMessages())
	    model.addAttribute("messages", getMessages());
        return "staff/showStats";
    }    
    
    
		
}
