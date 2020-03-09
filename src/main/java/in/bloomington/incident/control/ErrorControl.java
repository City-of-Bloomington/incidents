package in.bloomington.incident.control;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.boot.web.servlet.error.ErrorController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.RequestDispatcher;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ErrorControl implements ErrorController  {

		@RequestMapping(value = "error", method = RequestMethod.GET)
    public ModelAndView renderErrorPage(HttpServletRequest
																				httpRequest,
																				@ModelAttribute("errors") Object errors
																				) {
         
        ModelAndView errorPage = new ModelAndView("errorPage");
        String errorMsg = "";
        int errorCode = getErrorCode(httpRequest);
				
        switch (errorCode) {
            case 400: {
                errorMsg = "Error Code: 400. Bad Request";
                break;
            }
            case 401: {
                errorMsg = "Error Code: 401. Unauthorized";
                break;
            }
            case 404: {
                errorMsg = "Error Code: 404. Resource not found";
                break;
            }
            case 500: {
                errorMsg = "Error Code: 500. Internal Server Error";
                break;		
						}
				    default:{
						    errorMsg = "Error Code: "+errorCode+" Internal Server Error";
						    break;		
				    }
        }
        errorPage.addObject("errorMsg", errorMsg);
				if(errors != null)
						errorPage.addObject("errors", errors);
        return errorPage;
    }
     
    private int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest
          .getAttribute("javax.servlet.error.status_code");
    }
		
    @Override
    public String getErrorPath() {
        return "/error";
    }
}
