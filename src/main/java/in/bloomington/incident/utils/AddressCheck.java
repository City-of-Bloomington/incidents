package in.bloomington.incident.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
//

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import in.bloomington.incident.model.Item;

@Component
public class AddressCheck{

    private static final Logger logger = LoggerFactory.getLogger(AddressCheck.class);
    static String url = "";
		
    @Autowired
    private Environment env;
    @Value("${incident.address.arcGisUrl}")
    private String arcGisUrl;

    private String geoUrl=""; //old geo server, not used
    
    // Lat Long projection 
    private String projection="4269"; // old gis
    private String spatial_type="4362"; // for arcGIS lat/long 
    // address service
    @Value("${incident.master.addressService}")
    private String masterAddressService;		
    //
    // for state plane coordinate 
    // uncomment if using state plane coordinate 
    // private String projection="2966";
    //
    Item address = null, exactMatchAddress = null;
    List<Item> addresses = null;

    public AddressCheck(){

    }
		
    public AddressCheck(Item val){
	setAddress(val);
    }		
    public void setAddress(Item val){
	if(val != null)
	    address = val;
    }
    public Item getExactMatchAddress(){
	return exactMatchAddress;
    }
    public List<Item> getAddresses(){
	return addresses;
    }
    public boolean foundAddresses(){
	return addresses != null && addresses.size() > 0;
    }
    public boolean findExactMatch(){
	if(address != null && addresses != null){
	    String val = address.getName().toUpperCase();
	    for(Item one:addresses){
		String val2 = one.getName().toUpperCase();
		if(val.equals(val2)){
		    exactMatchAddress = one;
		    return true;
		}
	    }
	}
	return false;
    }
    public String findMatchedAddresses(String addrStr){
	String back = "", json="";
	try{
	    HttpClient client = HttpClientBuilder.create().build();
	    String encodedStr = URLEncoder.encode(addrStr, StandardCharsets.UTF_8.toString());
	    // "https://bloomington.in.gov/master_address/locations?format=json;location="+encodedStr);
	    HttpGet request = new HttpGet(masterAddressService+"?format=json;location="+encodedStr);
	    request.addHeader("accept", "application/json");
	    HttpResponse response = client.execute(request);
						
	    String jsonStr = IOUtils.toString(response.getEntity().getContent());
	    // System.err.println(" response "+jsonStr);
	    JSONObject jsonObj = new JSONObject(jsonStr);
	    if(!jsonObj.isNull("locations")){
		JSONArray jsonArr = jsonObj.getJSONArray("locations");
		String str = jsonArr.toString();
		json = str.replaceAll("streetAddress","value");
		// System.err.println(" json "+json);
	    }
	    if(json != null && !json.isEmpty()){
		return json;
	    }
	}
	catch(Exception ex){
	    back += ex;
	    System.err.println(" "+ex);
	}
	return back;
    }
    // old gis code
    public String isInTheLayer(String xmlStr){
	String back = "";
	// System.err.println(" xml "+xmlStr);
	// System.err.println(" geo url "+geoUrl);
	try{
	    URL url = new URL(geoUrl+"/wfs/Service?");
	    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	    connection.setConnectTimeout(20000);
	    connection.setReadTimeout(20000);
	    
	    // Set DoOutput to true if you want to use URLConnection for output.
	    // Default is false
	    connection.setDoOutput(true);
	    // connection.setUseCaches(true);
	    connection.setRequestMethod("POST");
	    //
	    // Set Headers
	    connection.setRequestProperty("Accept", "application/xml");
	    connection.setRequestProperty("Content-Type", "application/xml");
	    //
	    // Write XML
	    OutputStream outputStream = connection.getOutputStream();
	    byte[] bytes = xmlStr.getBytes("UTF-8");
	    outputStream.write(bytes);
	    outputStream.flush();
	    outputStream.close();
	    //
	    // Read XML
	    InputStream inputStream = connection.getInputStream();
	    byte[] res = new byte[2048];
	    int i = 0;
	    StringBuilder response = new StringBuilder();
	    while ((i = inputStream.read(res)) != -1) {
		response.append(new String(res, 0, i));
	    }
	    inputStream.close();
	    back = response.toString();
	    // System.out.println("Response: " + back);
	}catch(Exception ex){
	    System.err.println(" "+ex);
	    back += ex;
	}
	return back;
    }
    // old gis code
    public boolean isInIUPDLayer2(double lat,
				 double longi){
	// IU Compus
	String typeName="publicgis:IUPDPoliceDistrict";
	String xmlStr = buildXmlString(lat, longi, typeName);
	String back = isInTheLayer(xmlStr);
	//
	//temporary till the geoserver is fixed, we are check for the exception
	//
	if(back != null && back.indexOf("IOException") > -1)
	    return true;
	return (back != null  && back.indexOf("Polygon") > -1);
    }
    public boolean isInIUPDLayer(double lat,
				 double longi){
	String back = "";
	String urlStr = arcGisUrl+"query?where=&text=&objectIds=&time=&timeRelation=esriTimeRelationOverlaps&geometry=%7B%27%27x%27%27%3A"+longi+"%2C%27%27y%27%27%3A"+lat+"%2C%27%27spatialReference%27%27%3A4326%7D&geometryType=esriGeometryPoint&inSR=4326&spatialRel=esriSpatialRelIntersects&distance=&units=esriSRUnit_Foot&relationParam=&outFields=*&returnGeometry=false&returnTrueCurves=false&maxAllowableOffset=&geometryPrecision=&outSR=&havingClause=&returnIdsOnly=false&returnCountOnly=false&orderByFields=&groupByFieldsForStatistics=&outStatistics=&returnZ=false&returnM=false&gdbVersion=&historicMoment=&returnDistinctValues=false&resultOffset=&resultRecordCount=&returnExtentOnly=false&sqlFormat=none&datumTransformation=&parameterValues=&rangeValues=&quantizationParameters=&featureEncoding=esriDefault&f=json";
	
	System.err.println(" url "+urlStr);
	try{
	    URL url_obj = new URL(urlStr);
	    HttpURLConnection con = (HttpURLConnection) url_obj.openConnection();
	    con.setRequestMethod("GET");
	    int code = con.getResponseCode();
	    System.out.println("GET Code :: " + code);
	    if (code == HttpURLConnection.HTTP_OK) { // success
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		
		while ((inputLine = in.readLine()) != null) {
		    response.append(inputLine);
		}
		in.close();
		back = response.toString();
		System.out.println(back);
		if(back.indexOf("error") > 0){
		    logger.error(back);
		}
		return (back.indexOf("IUPD") > 0);
	    } else {
		System.out.println("Code Error "+code);
	    }
	}catch(Exception ex){
	    System.err.println(" "+ex);
	    back += ex;
	}
	if(back.indexOf("IUPD") > 0) return true;
	return false;
    }	

    public boolean isInCityPDLayer(double lat,
				   double longi){
	// Bloomington City Boundary
	String typeName = "publicgis:BPDPoliceDistrict";
	String xmlStr = buildXmlString(lat, longi, typeName);
	String back = isInTheLayer(xmlStr);
	return (back != null && back.indexOf("Polygon") > -1);
    }    
    //
    public String buildXmlString(double lat,
				 double longi,
				 String typeName){
	String xmlStr = "";
	xmlStr ="<wfs:GetFeature service=\"WFS\" version=\"1.0.0\"\n"+
	    "outputFormat=\"GML2\"\n"+
	    "xmlns:topp=\"http://www.openplans.org/topp\"\n"+
	    "xmlns:wfs=\"http://www.opengis.net/wfs\"\n"+
	    "xmlns=\"http://www.opengis.net/ogc\"\n"+
	    "xmlns:gml=\"http://www.opengis.net/gml\"\n"+
	    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"+
	    "xsi:schemaLocation=\"http://www.opengis.net/wfs\n"+
	    "http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd\">"+
	    "<wfs:Query typeName=\""+typeName+"\">"+
	    "<Filter>"+
	    "<Contains>"+
	    "<PropertyName>the_geom</PropertyName>"+
	    "<gml:Point srsName=\"http://www.opengis.net/gml/srs/epsg.xml#"+projection+"\">"+
	    "<gml:coordinates>"+longi+","+lat+"</gml:coordinates>"+
	    "</gml:Point>"+
	    "</Contains>"+
	    "</Filter>"+
	    "</wfs:Query>"+
	    "</wfs:GetFeature>";
	return xmlStr;
    }
    /**
     * given certain address, find similar addresses in master_address app
     * we are going to use Cliff js addressChooser to pick the right address
     */
		
    public String findSimilarAddr(){
	//
	String back = "";
	String urlStr = "";
	// String query="format=json;queryType=address;query=";
	String query="url"+"/addresses?address=401+n+morton+st;format=json";
						
	if(url.equals("")){
	    if(env != null){
		url = env.getProperty("addressCheckUrl");
	    }
	}
	urlStr = url+"/?";
	// System.err.println(" addr url "+urlStr);
	if(address == null){
	    back = " No address set ";
	    return back;
	}				
	String addr = address.getName();
	if(addr == null || addr.equals("")){
	    back = " No address set ";
	    return back;
	}
	DefaultHttpClient httpclient = new DefaultHttpClient();		
	ResponseHandler<String> responseHandler = new BasicResponseHandler();
	try{
	    query += java.net.URLEncoder.encode(addr, "UTF-8");
	    query +="+Bloomington;";
	    urlStr += query;
	    HttpGet httpget = new HttpGet(urlStr);
	    // System.err.println(urlStr);
	    logger.debug(urlStr);
            String responseBody = httpclient.execute(httpget, responseHandler);
	    // System.err.println(" response "+responseBody);
            logger.debug("----------------------------------------");
            logger.debug(responseBody);
            logger.debug("----------------------------------------");
	    JSONArray jArray = new JSONArray(responseBody);
	    addresses = new ArrayList<>();
	    for (int i = 0; i < jArray.length(); i++) {
		JSONObject jObj = jArray.getJSONObject(i);
		if(jObj.has("id")){
		    String master_address_id = jObj.getString("id");
		    String location_id = jObj.getString("location_id");
		    //
		    //
		    String latVal = jObj.getString("latitude");
		    String lngVal = jObj.getString("longitude");
		    /*
		      if(latVal != null){
		      address.setLatitude(new Double(latVal));
		      }
		      if(lngVal != null){
		      address.setLongitude(new Double(lngVal));
		      }
		    */
		    String street = "";
		    String full_addr="";
		    if(!jObj.isNull("streetAddress")){
			street = jObj.getString("streetAddress");
			address.setName(street);
			full_addr = street;
		    }
		    if(!jObj.isNull("subunit_count")){
			JSONArray jArr2 = jObj.getJSONArray("subunits");
			if(jArr2 != null){
			    for(int j=0;j<jArr2.length();j++){
				JSONObject jObj2 = jArr2.getJSONObject(j);
				if(!jObj2.isNull("id")){
				    full_addr = street;
				    Item one = new Item();
				    one.setId(new Integer(master_address_id));
				    String type = jObj2.getString("type_code");
				    String ident = jObj2.getString("identifier");
				    full_addr += " "+type;
				    full_addr += " "+ident;
				    one.setName(full_addr);
				    addresses.add(one);
				}
			    }
			}
		    }
		    else{ // no subunit
			Item one = new Item();
			one.setName(full_addr);
			one.setId(new Integer(master_address_id));
			addresses.add(one);
		    }
		}
	    }
	}
	catch(Exception ex){
	    back = ex+" "+urlStr;
	    logger.error(back);
	}
	finally{
	    // 
	    httpclient.getConnectionManager().shutdown();
	}
	return back;
    }

		
}

		
