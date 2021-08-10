package in.bloomington.incident.model;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
public class Email{

    private String type=""; // reject, approve, other
    private String sender=""; // from
    private String receiver="";
    private String subject="", message="";
    private Integer incident_id=null;
    private Integer user_id=null;
    //
    public Email(){

    }

    public String getType() {
	return type;
    }

    public void setType(String val) {
	if(val != null)
	    this.type = val;
    }
    public String getReceiver() {
	return receiver;
    }

    public void setReceiver(String val) {
	if(val != null)
	    this.receiver = val;
    }
    public String getSender() {
	return sender;
    }

    public void setSender(String val) {
	if(val != null)
	    this.sender = val;
    }
    public String getSubject() {
	return subject;
    }

    public void setSubject(String val) {
	if(val != null)
	    this.subject = val;
    }
    public String getMessage() {
	return message;
    }

    public void setMessage(String val) {
	if(val != null)
	    this.message = val;
    }
    public Integer getIncident_id() {
	return incident_id;
    }

    public void setIncident_id(Integer val) {
	if(val != null)
	    this.incident_id = val;
    }
    public Integer getUser_id() {
	return user_id;
    }

    public void setUser_id(Integer val) {
	if(val != null)
	    this.user_id = val;
    }        

    @Override
    public boolean equals(Object obj) { 
          
	if(this == obj) 
	    return true; 
				
        if(obj == null || obj.getClass()!= this.getClass()) 
            return false; 
				
        Email one = (Email) obj; 
        return one.getMessage().equals( this.getMessage());
    }
    @Override
    public int hashCode(){ 
	int ret = 29;
	return ret + message.hashCode();
    }

    @Override
    public String toString() {
	return receiver+" "+message;
    }
    public void populateEmail(Incident incident, String type){
	setType(type);
	if(incident != null){
	    incident_id = incident.getId();
	    receiver = incident.getEmail();
	    if(type.equals("reject")){
		subject = "Incident Report Rejection Reasons";
		message = "We received an incident report from you on "+incident.getReceivedStr()+"<br />\n\n"+
		    incident.getDetails()+"<br />\n\n"+
		    "Your request was rejected for the reasons below:";
	    }
	    else if(type.equals("approve")){
		Person person = null;
		List<Person> persons = incident.getPersons();
		if(persons != null && persons.size() > 0)
		    person = persons.get(0); // we need one
		subject = "Incident Reporting Approval";
		if(person != null)
		    message = "Dear "+person.getFullname()+"<br />\n\n ";
		message += " Your report has been approved. "+
		    "The Case Number of your report is "+incident.getCaseNumber()+
	    
		    " You can use this number in your future contacts with the Bloomington Police Department. <br />\n\n"+
		    "Please do not reply to this email as this is an automated system.<br />\n\n";
	    }
	}
    }
		
}
