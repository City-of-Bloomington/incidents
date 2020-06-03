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
import javax.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
//
// implementation of logging is logback
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import in.bloomington.incident.util.AddressCheck;
import in.bloomington.incident.model.Item;


// @RestController
@Controller
public class AddressController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(AddressController.class);

    @Autowired
    AddressCheck addressCheck;
    /**
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
    */
    @CrossOrigin(origins = "https://bloomington.in.gov")
    @GetMapping("/addressInput")
    public String addressInput(HttpServletRequest req,
			       Model model) {
	model.addAttribute("messages","Enter your address");
        return "addressInput";
    }
    @CrossOrigin(origins = "https://bloomington.in.gov")
    @GetMapping("/addressWindow")
    public String addressWindow(HttpServletRequest req,
			       Model model) {
	model.addAttribute("messages","Enter your address");
        return "addressWindow";
    }    
    
    /**
       list of addresses to try

       IUPD Area
       275 N Jordan AVE
       https://bloomington.in.gov/master_address/addresses/32177
       location_id: 42326
       state_plane_x: 3113403
       state_plane_y: 1428294        
       Latitude:	39.16840902
       Longitude:	-86.51670272

       BPD Area
       932 N Fairview ST
       https://bloomington.in.gov/master_address/addresses/13653
       location_id: 15279
       state_plane_x: 3106537
       state_plane_y: 1431066
       Latitude:    39.17613466
       Longitude:   -86.54085991

       MSCO Area within City of Bloomington Limits
       401 W 7th ST
       https://bloomington.in.gov/master_address/addresses/23239
       location_id: 35113
       state_plane_x: 3107465
       state_plane_y: 1428192
       Latitude: 39.16822894
       Longitude: -86.53764758

       MCSO Area (not in city limits)
       3304 S Rogers ST
       https://bloomington.in.gov/master_address/addresses/9157
       location_id: 10173
       state_plane_x: 3107175
       state_plane_y: 1413619
       Latitude: 39.12822405
       Longitude: -86.53897868


       Ellettsville
       5352 N Monica CT
       location_id:  70924
       https://bloomington.in.gov/master_address/addresses/58416
       x: 3089943
       y: 1449642
       Latitude:  39.22739223
       Longitude:  -86.59904201

       Stinesville

       8357 N Market ST
       location_id: 77983
       https://bloomington.in.gov/master_address/addresses/65392
       x: 3075115
       y: 1475365
       Latitude: 39.29821949
       Longitude: -86.65094971
       
     */
    @PostMapping("/addressCheck")
    public String addressCheck(@RequestParam("address") String address,
			       @RequestParam("latitude") Double latitude,
			       @RequestParam("longitude") Double longitude,
			       Model model
			       ) {
	// in compus
	String addr = "275 N Jordan AVE";
	double lati = 	39.16840902;
	double longi = -86.51670272;

	// String project = "4269"; // lat long projection
	// boolean answer = addressCheck.isInIUCompus(lati, longi);
	// System.err.println(" answer "+answer);
	// not in compus, in city
	addr = "932 N Fairview ST";
	lati  =  39.17613466;
	longi = -86.54085991;
	// not in compus, not in city
	//
	// addr = "3304 S Rogers ST";
	// lati = 39.12822405;
	// longi = -86.53897868;
	//
	// Ellettsville
	//
	addr = "5352 N Monica CT";
	lati = 39.22739223;
	longi =  -86.59904201;
	//
	// Stinesville
	addr = "8357 N Market ST";
	lati = 39.29821949;
	longi = -86.65094971;	
	
	boolean answer = addressCheck.isInIUPDLayer(lati, longi);
	String msg = "";
	System.err.println(" answer "+answer);
	if(answer)
	    msg = "In IU Compus PD ";
	else
	    msg = "Not in IU Compus";
	answer = addressCheck.isInCityPDLayer(lati, longi);
	System.err.println(" answer "+answer);
	if(answer)
	    msg += " In City BPD area ";
	else
	    msg += " Not in City BPD area ";
	model.addAttribute("address",addr);
	model.addAttribute("latitude", lati);
	model.addAttribute("longitude", longi);
	model.addAttribute("msg", msg);
        return "addressInfo";
    }
    /**
       // ESPG = 2966 for state coordinate
       // lat long coordinate is ESPG = 4269
       // bloomington boundary name publicgis:BloomingtonMunicipalBoundary
       // for IU compus name publicgis:IUCampusArea
     // You can check within which polygon, if any, your point
     // is by using Contains filter.
     // we need to use layers to such as 
     propertyName=publicgis:IUCampusArea
     propertyName=publicgis:BloomingtonMunicipalBoundary
     
    <wfs:GetFeature service="WFS" version="1.0.0"
      outputFormat="GML2"
      xmlns:topp="http://www.openplans.org/topp"
      xmlns:wfs="http://www.opengis.net/wfs"
      xmlns="http://www.opengis.net/ogc"
      xmlns:gml="http://www.opengis.net/gml"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://www.opengis.net/wfs
                          http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd">
      <wfs:Query typeName="topp:states">
        <Filter>
          <Contains>
            <PropertyName>the_geom</PropertyName>
              <gml:Point srsName="http://www.opengis.net/gml/srs/epsg.xml#4326">
                <gml:coordinates>-74.817265,40.5296504</gml:coordinates>
              </gml:Point>
            </Contains>
          </Filter>
      </wfs:Query>
    </wfs:GetFeature>
    //
    // for get
    //
    http://localhost:8080/geoserver/wfs?service=WFS&request=GetFeature&version=1.0.0&typeName=topp:states&outputFormat=GML2&FILTER=%3CFilter%20xmlns=%22http://www.opengis.net/ogc%22%20xmlns:gml=%22http://www.opengis.net/gml%22%3E%3CContains%3E%3CPropertyName%3Ethe_geom%3C/PropertyName%3E%3Cgml:Point%20srsName=%22EPSG:4326%22%3E%3Cgml:coordinates%3E-74.817265,40.5296504%3C/gml:coordinates%3E%3C/gml:Point%3E%3C/Contains%3E%3C/Filter%3E
    
    */
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
