package in.bloomington.incident.model;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;


public class Search implements java.io.Serializable{

    private String id="", actionId="";
    private String caseNumber="";
    private String address="";
    private String zip="";
    // these two not used
    private String city="",state="";
    //
    private String dateFrom="",dateTo="";
    private String incidentTypeId="";

    // person related
    private String name="";
    private String dln="";
    private String dob="";
    //
    // the following not used
    private String race="",sex="";
		//
		private String sortBy="id";
    //
    //
    public Search(){

    }

    public String getId() {
				return id;
    }

    public void setId(String id) {
				this.id = id;
    }
    public String getActionId() {
				return actionId;
    }

    public void setActionId(String val) {
				this.actionId = val;
    }		
    public String getSortBy() {
				return sortBy;
    }
    public void setSortBy(String val) {
				this.sortBy = val;
    }		
    public String getCaseNumber() {
				return caseNumber;
    }
    public void setCaseNumber(String val) {
				if(val != null && !val.isEmpty())
						caseNumber = val;
    }    

    public String getAddress() {
				return address;
    }

    public void setAddress(String val) {
				if(val != null && !val.isEmpty())
						address = val;
    }
    public String getCity() {
				return city;
    }

    public void setCity(String val) {
				if(val != null && !val.isEmpty())
						city = val;
    }
    public String getZip() {
				return zip;
    }

    public void setZip(String val) {
				if(val != null && !val.isEmpty())
						zip = val;
    }
    public String getDateFrom() {
				return dateFrom;
    }

    public void setDateFrom(String val) {
				if(val != null && !val.isEmpty())
						dateFrom = val;
    }
    public String getDateTo() {
				return dateTo;
    }

    public void setDateTo(String val) {
				if(val != null && !val.isEmpty() && !val.equals("-1"))
						dateTo = val;
    }
    public String getIncidentTypeId() {
				return incidentTypeId;
    }

    public void setIncidentTypeId(String val) {
				if(val != null && !val.isEmpty())
						incidentTypeId = val;
    }
    //
    // person related
    public String getName() {
				return name;
    }

    public void setName(String val) {
				if(val != null && !val.isEmpty())
						name = val;
    }
    public String getDln() {
				return dln;
    }

    public void setDln(String val) {
				if(val != null && !val.isEmpty())
						dln = val;
    }
    public String getRace() {
				return race;
    }

    public void setRace(String val) {
				if(val != null && !val.isEmpty())
						race = val;
    }
    public String getSex() {
				return sex;
    }

    public void setSex(String val) {
				if(val != null && !val.isEmpty())
						sex = val;
    }
    public String getDob() {
				return dob;
    }

    public void setDob(String val) {
				if(val != null && !val.isEmpty())
						dob = val;
    }
    // make sure we have at leas one thing to search for
    public boolean isValid(){
				if(id.isEmpty() &&
					 caseNumber.isEmpty() &&
					 address.isEmpty() &&
					 zip.isEmpty() &&
					 city.isEmpty() &&
					 state.isEmpty() &&
					 incidentTypeId.isEmpty() &&
					 dateFrom.isEmpty() &&
					 dateTo.isEmpty() &&
					 name.isEmpty() &&
					 dln.isEmpty() &&
					 dob.isEmpty() &&
					 race.isEmpty() &&
					 actionId.isEmpty() &&
					 sex.isEmpty()){
						return false;
				}
				return true;
    }
	

    @Override
    public boolean equals(Object obj) { 
          
				if(this == obj)
	    
						return true; 
				
        if(obj == null || obj.getClass()!= this.getClass()) 
            return false; 
				
        Search one = (Search) obj; 
        return one.getId() == this.getId();
    }
    @Override
    public int hashCode(){ 
				int ret = 29;
				int int_id = 0;
				if(id.isEmpty()){
						try{
								int_id = Integer.parseInt(id);
						}catch(Exception ex){}
				}
        return ret += int_id; 
    }

    @Override
    public String toString() {
				return id;
    } 	
		
}
