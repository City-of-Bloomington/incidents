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
import javax.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
//
// implementation of logging is logback
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import in.bloomington.incident.model.Item;



@RestController
public class AddressController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(AddressController.class);

    @GetMapping(value = "/addressService", produces = "application/json")
    public String addressService(@RequestParam("term") String term, Locale locale, Model model){
        String json = "";
        if (term != null && term.length() > 2) {
            List<Item> addresses = null; //addressService.getList(term);
            if (addresses != null && addresses.size() > 0) {
                json = buildJson(addresses);
            }
        }
        return json;
    }

    private String buildJson(List<Item> ones){
        String    json = "";
        JSONArray jArr = new JSONArray();
        for (Item one : ones) {
            JSONObject jObj = new JSONObject();
            jObj.put("id", one.getId());
            jObj.put("value", one.getName());
            jArr.put(jObj);
        }
        json = jArr.toString();
        return json;
    }

    

		
}
