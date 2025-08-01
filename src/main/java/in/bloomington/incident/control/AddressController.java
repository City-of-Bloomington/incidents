package in.bloomington.incident.control;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
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
import in.bloomington.incident.service.IncidentService;
import in.bloomington.incident.utils.Helper;


@Controller
public class AddressController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(AddressController.class);
    @Autowired
    AddressCheck addressCheck;
    @Autowired
    AddressService addressService;
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
		

    @PostMapping("/address/update/{id}")
    public String addressUpdate(@PathVariable("id") int id,
				@Valid Address address, 
				BindingResult result, Model model) {
	if (result.hasErrors()) {
	    address.setId(id);
	    logger.error("Error update address = "+id);						
	    return "updateAddress";
	}
	addMessage("Updated Successfully");
	addressService.update(address);
	model.addAttribute("messages", messages);
	model.addAttribute("app_url", app_url);
	return "redirect:address/"+id;
    }
    @GetMapping("/address/edit/{id}")
    public String addressEdit(@PathVariable("id") int id, Model model) {
	Address address = null;
	try{
	    address = addressService.findById(id);
						
	}catch(Exception ex){
	    addError("Invalid address Id"+id);
	    model.addAttribute("errors", errors);
	    model.addAttribute("app_url", app_url);
	    logger.error("Exception getting address ="+id+" "+ex);
	    return "address/"+id;
	}
	model.addAttribute("address", address);
	return "addressUpdate";
    }
    @GetMapping("/address/{id}")
    public String addressView(@PathVariable("id") int id, Model model) {
	Address address = null;
	try{
	    address = addressService.findById(id);
						
	}catch(Exception ex){
	    addError("Invalid address Id "+id);
	    model.addAttribute("errors", errors);
	    model.addAttribute("app_url", app_url);
	    logger.error("Exception getting address ="+id+" "+ex);
	    return "address/"+id;
	}
	model.addAttribute("address", address);
	return "addressView";
    }				
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
    @GetMapping("/addressWindow")
    public String addressWindow(HttpServletRequest req,
				Model model) {
	model.addAttribute("messages","Enter your address");
        return "addressWindow";
    }    		
    @GetMapping("/addressInput/{type_id}")
    public String addressInput(@PathVariable("type_id") int type_id,
			       Model model) {
	model.addAttribute("type_id", type_id);
	model.addAttribute("app_url", app_url);
	if(hasErrors()){
	    System.err.println(" has Errors "+errors);
	    System.err.println("addresInput "+hasErrors());
	    model.addAttribute("errors", errors);
	}
	if(hasMessages()){
	    model.addAttribute("messages", messages);
	}
	// resetAll();
        return "addressInput";
    }

    @CrossOrigin(origins = "https://bloomington.in.gov")
    @GetMapping("/addressPicked/{type_id}/{addr_ref}")
    public String addressPicked(@PathVariable("type_id") int type_id,
			       @PathVariable("addr_ref") String addr_ref,
			       Model model) {
	Address addr = null;
	Map<String, Address> map = null;
	if(session != null){
	    map = (Map<String, Address>) session.getAttribute("addrMap");
	}
	if(map != null){
	    addr = map.get(addr_ref);
	    System.err.println("found addr "+addr.getName());
	}
	if(addr != null){
	    //
	    // check if within city limits using lat/long
	    //
	    System.err.println(" checking address ");
	    if(checkAddress(addr)){
		System.err.println(" addr in Bloomington ");
		if(session != null && session.getAttribute("addrMap") !=null){
		    session.removeAttribute("addrMap");
		}
		Address addr2 = saveOrUpdate(addr);
		if(addr2 != null){
		    addr2.setType_id(type_id);
		    model.addAttribute("type_id", type_id);
		    model.addAttribute("address_id", addr2.getId());
		    model.addAttribute("app_url", app_url);
		    return "emailAdd";
		}
		else{
		    System.err.println(" Error saving addr ");
		}
	    }
	    else{
		System.err.println(" addr not in Bloomington ");
		addMessage("Address not in Bloomington Police District");
		model.addAttribute("messages", messages);
	    }
	}
	//model.addAttribute("address", address);
	model.addAttribute("type_id", type_id);
	model.addAttribute("app_url", app_url);
        return "addressInput";
    }

    @CrossOrigin(origins = "https://bloomington.in.gov")
    @RequestMapping("/addressFind")	
    public String addressFind(@RequestParam(required=true) int type_id,
			      @RequestParam(required=true) String addr_in,
			      Model model) {
	// ToDo this is not the right one to call
	JSONObject jObj = null;
	JSONArray jarr = null;
	String jsonStr = addressCheck.findMatchedAddresses(addr_in);
	System.err.println(" json "+jsonStr);
	if(jsonStr != null && !jsonStr.trim().equals("[]")){
	    if(jsonStr.indexOf("[") > -1){
		jarr = new JSONArray(jsonStr);
		if(jarr.length() == 1){ // we get exact address
		    jObj = jarr.getJSONObject(0);
		}
	    }
	    else{
		jObj = new JSONObject(jsonStr);
	    }
	}
	else{
	    System.err.println(" no match found");
	}
	if(jObj != null){
	    Double lat = jObj.getDouble("latitude");
	    Double lon = jObj.getDouble("longitude");
	    // Integer sub_id = jobj.getInt("subunit_id");
	    Integer add_id = jObj.getInt("address_id");
	    Integer sub_id = null;
	    if(jObj.isNull("subunit_id")){
		System.err.println(" junit id is null");
	    }
	    else{
		sub_id=jObj.getInt("subunit_id");
	    }
	    String adr = jObj.getString("value");
	    String city = jObj.getString("city");
	    String state = jObj.getString("state");
	    String zip = "";
	    if(jObj.has("zip")){
		zip =""+jObj.getInt("zip");
	    }
	    Address addr = new Address(adr,
				       lat,
				       lon,
				       city,
				       state,
				       zip,
				       jObj.getString("jurisdiction_name"),
				       add_id,
				       sub_id);
	    // we need to check for lat long
	    //
	    if(checkAddress(addr)){
		Address addr2 = saveOrUpdate(addr);
		addr2.setType_id(type_id);
		model.addAttribute("type_id", type_id);
		model.addAttribute("address_id", addr2.getId());
		model.addAttribute("app_url", app_url);
		return "emailAdd";		
	    }
	    else{
		System.err.println(" not in bloomington ");
		addMessage("This address is not in bloomington Police district ");
		model.addAttribute("messages", messages);
	    }
	}
	else if(jarr != null){
	    Map<String, Address> addrMap = new HashMap<>();
	    List<Address> addresses = new ArrayList<>();
	    for(int j = 0; j < jarr.length(); j++){
		JSONObject jobj = jarr.getJSONObject(j);
		System.err.println(j+" "+jobj);
		Double lat = jobj.getDouble("latitude");
		Double lon = jobj.getDouble("longitude");
		// Integer sub_id = jobj.getInt("subunit_id");
		Integer add_id = jobj.getInt("address_id");
		Integer sub_id = null;
		if(jobj.isNull("subunit_id")){
		    System.err.println(" junit id is null");
		}
		else{
		    sub_id=jobj.getInt("subunit_id");
		}
		String adr = jobj.getString("value");
		String city = jobj.getString("city");
		String state = jobj.getString("state");
		String zip = "";
		if(jobj.has("zip")){
		    zip =""+jobj.getInt("zip");
		}
		Address addr = new Address(adr,
					   lat,
					   lon,
					   city,
					   state,
					   zip,
					   jobj.getString("jurisdiction_name"),
					   add_id,
					   sub_id);
		
		addrMap.put(addr.getAddrid_subid(), addr);
		addresses.add(addr);
	    }
	    if(session != null){
		session.setAttribute("addrMap",addrMap);
	    }
	    // convert json to addresses
	    // model.addAttribute("addresses", addresses);
	    model.addAttribute("app_url", app_url);
	    model.addAttribute("type_id", type_id);
	    model.addAttribute("addresses", addresses);
	    return "addressSelect";
	}
	else{
	    addMessage("No matching address found, try again");
	    model.addAttribute("messages", messages);
	}
	model.addAttribute("app_url", app_url);
	model.addAttribute("type_id", type_id);	    
	return "addressInput";
    }    
    // for testing purpose
    // @CrossOrigin(origins = "https://bloomington.in.gov")
    @GetMapping("/addressTest")
    public String addressTest(Model model) {
	Address address = new Address();
	address.setType_id(1);
	model.addAttribute("address", address);
	model.addAttribute("app_url", app_url);
        return "addressTest";				
    }
    private boolean checkAddress(Address address){
	boolean pass = true;
	if(address != null){
	    if(!address.verifyAddress(defaultCity,
				      defaultJurisdiction,
				      defaultState,
				      zipCodes)){
		addError(address.getErrorInfo());
		return false;
	    }
	    if(address.hasLatitudeLongitude()){
		if(addressCheck.isInIUPDLayer(address.getLatitude(),
					      address.getLongitude())){
		    addError("This address is in IU Police Department district");
		    return false;
		}
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
	    // System.err.println(" find address by name not found ");
	    /**
	     * if not then we save
	     */
	    addressService.save(address);								
	}
	else{
	    // System.err.println("find address by name found "+addresses.size());
	    // if exist we update 
	    Address addr = addresses.get(0);
	    address.setId(addr.getId());
	    addressService.update(address);								
	}
	return address;
    }
    //
    @PostMapping("/addressSave")
    public String addressSave(@Valid Address addr,
			      BindingResult result,
			      Model model
			      ) {
	boolean pass = true;
	Address address = null;
	resetAll();
        if (result.hasErrors()) {
	    addError(Helper.extractErrors(result));
	    pass = false;
        }
	if(pass && checkAddress(addr)){
	    address = saveOrUpdate(addr);
	    // next go to email request
	    model.addAttribute("type_id", address.getType_id());
	    model.addAttribute("address_id", address.getId());
	    model.addAttribute("app_url", app_url);
	    return "emailAdd";								
	}
	// no pass then we send the user to address input again with error message
	return "redirect:/addressInput/"+addr.getType_id();
    }
    //
    // we come here when back button is pressed
    //
    @PostMapping("/addressUpdateCheck")
    public String addressUpdateCheck(@Valid Address addr,
				     BindingResult result,
				     Model model
				     ) {
	boolean pass = true;
        if (result.hasErrors()) {
	    addError(Helper.extractErrors(result));
	    pass = false;
        }
	int old_id = addr.getOld_id();
	Address old_address = addressService.findById(old_id);
	//
	// check if the same address
	if(old_address.equals(addr)){
	    //
	    // no change in address, so we go to next step
	    //
	    // System.err.println("The same address, no change");
	    model.addAttribute("type_id", addr.getType_id());
	    model.addAttribute("address_id", old_id);
	    model.addAttribute("app_url", app_url);		
	    return "emailAdd";
	}
	// System.err.println("The address changed");				
	if(pass && checkAddress(addr)){
	    Address address = null;
	    /**
	     * first we need to check if this address exist
	     */
	    List<Address> addresses = addressService.findDistinctAddressByName(address.getName());
	    address = saveOrUpdate(addr);
	    // next go to email request
	    model.addAttribute("type_id", address.getType_id());
	    model.addAttribute("address_id", address.getId());
	    model.addAttribute("app_url", app_url);
	    return "emailAdd";
	}
	// no pass then we send the user to address input again with error messa
	model.addAttribute("type_id",addr.getType_id());
	model.addAttribute("old_id", addr.getId());
	model.addAttribute("address", addr);
	model.addAttribute("app_url", app_url);
	model.addAttribute("errors", errors);
	resetAll();
	return "addressUpdate";
				
    }		
    /**
       list of addresses to try

       IUPD Area
       1275 E 10th st
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
    @PostMapping("/addressCheckOld")
    public String addressCheckOld(@RequestParam("address") String address,
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
	// System.err.println(" answer "+answer);
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
	model.addAttribute("app_url", app_url);
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

@RestController
class AddressServiceController{
    @Autowired
    private AddressService addressService;
    @Autowired
    AddressCheck addressCheck;
    //
    @GetMapping(value = "/addressService", produces = "application/json")
    public String addressService(@RequestParam("term") String term,
				 Model model)
    {
        String json = "";
	// System.err.println(" term "+term);
        if (term != null && term.length() >= 5) {
	    // if term has at least two parts
	    String[] arr = term.split("\\s+");
	    if(arr.length >= 2){
		String back = addressCheck.findMatchedAddresses(term);
		if(back.indexOf("Exception") == -1){
		    json = back;
		}
		// System.err.println(back);
	    }
        }
        return json;
    }

    private String buildJson(List<Address> ones, String term)
    {
        String    json = "";
        JSONArray jArr = new JSONArray();
        for (Address one : ones) {
            JSONObject jObj = new JSONObject();
            jObj.put("value",       one.getName());
            jObj.put("address_id",  one.getAddressId());
            jObj.put("streetAddress",  one.getName());
            jObj.put("city", one.getCity());
            jObj.put("state", one.getState());
            jObj.put("zip", one.getZipcode());
	    jObj.put("latitude", one.getLatitude());
	    jObj.put("longitude",one.getLongitude());
            jObj.put("jurisdiction_name","Bloomington");
	    jObj.put("subunit_id",  one.getSubunitId());
            jArr.put(jObj);
        }
        json = jArr.toString();
        return json;
    }
}
