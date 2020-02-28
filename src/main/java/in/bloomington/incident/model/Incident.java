package in.bloomington.incident.model;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Cacheable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.constraints.NotNull;
import in.bloomington.incident.utils.Helper;
		

@Entity
@Table(name = "incidents")
@Cacheable(false)
public class Incident extends TopModel implements java.io.Serializable{


		@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
		
		@Column(name ="cfsNumber")
    private String cfsNumber;
		@OneToOne

    private IncidentType incidentType;

    private Date received;

    private Date date;

    private String address;

		private String city;
    private String state;

    private String zip;

    private String details;
		
    private Character invalidAddress;
		
    private String dateDescription;

    private Date endDate;

    private String entryType;

    private String otherEntry;
		
    private Character haveMedia;

    private String email;
    
    // private int status_id;
		// @OneToMany(fetch=FetchType.LAZY, mappedBy="person")
		@OneToMany
		@JoinColumn(name="incident_id",insertable=false, updatable=false)		
		private List<Person> persons;		

		@OneToMany
		@JoinColumn(name="incident_id", insertable=false, updatable=false)
		private List<Property> properties;

		@OneToMany
		@JoinColumn(name="incident_id",insertable=false, updatable=false)
		private List<Vehicle> vehicles;

		@OneToMany
		@JoinColumn(name="incident_id",insertable=false, updatable=false)		
		private List<Media> medias;

		@OneToMany
		@JoinColumn(name="incident_id",insertable=false, updatable=false)
		private List<ActionLog> actionLogs;
		
    public Incident(){
				super();
    }
    public Incident(int id,
										String cfsNumber,
										IncidentType incidentType,
										Date received,
										Date date,
										String address,
										String city,										
										String state,
										String zip,
										String details,
										Character invalidAddress,
										String dateDescription,
										Date endDate,
										String entryType,
										String otherEntry,
										Character haveMedia,
										String email,
										List<Person> persons,										
										List<Property> properties,
										List<Vehicle> vehicles,
										List<ActionLog> actionLogs,
										List<Media> medias
										) {
				super();
				this.id = id;
				this.cfsNumber = cfsNumber;
				this.incidentType = incidentType;
				this.received = received;
				this.date = date;
				this.address = address;
				this.city = city;				
				this.state = state;
				this.zip = zip;
				this.details = details;
				this.invalidAddress = invalidAddress;
				this.dateDescription = dateDescription;
				this.endDate = endDate;
				this.entryType = entryType;
				this.otherEntry = otherEntry;
				this.haveMedia = haveMedia;
				this.email = email;
				this.persons = persons;				
				this.properties = properties;
				this.vehicles = vehicles;
				this.actionLogs = actionLogs;
				this.medias = medias;
		}

		public int getId() {
				return id;
		}

		public void setId(int id) {
				this.id = id;
		}

		public String getCfsNumber() {
				return cfsNumber;
		}

		public void setCfsNumber(String val) {
				
				if(val != null && !val.isEmpty())						
						this.cfsNumber = val.trim();
		}

		public Date getReceived() {
				return received;
		}

		public void setReceived(Date received) {
				this.received = received;
		}
		public String getReceivedStr() {
				String str = "";
				if(received != null){
						try{
								str = Helper.dft.format(received);
						}catch(Exception ex){
								System.err.println(ex);
						}
				}
				return str;
		}

		public void setReceivedStr(String val) {
				if(val != null && !val.equals("")){
						try{
								this.received = Helper.dft.parse(val);
						}catch(Exception ex){

						}
				}
		}
		
		public Date getDate() {
				return date;
		}		
		
		@Transient
		public String getDateStr() {
				String str="";
				if(date != null){
						try{
								str = Helper.dft.format(date);
						}catch(Exception ex){

						}
				}
				return str;
		}

		public void setDate(Date date) {
				this.date = date;
		}
		@Transient
		public void setDateStr(String val) {
				if(val != null && !val.equals("")){
						try{
								this.date = Helper.dft.parse(val);
						}catch(Exception ex){
								System.err.println(" invalid date format");
								addError(" "+ex);
						}
				}
		}
		public String getAddress() {
				return address;
		}

		public void setAddress(String address) {
				if(address != null && !address.isEmpty())
						this.address = address.trim();
		}
		public String getCity() {
				return city;
		}

		public void setCity(String city) {
				if(city != null && !city.isEmpty())
						this.city = city.trim();
		}
		public String getState() {
				return state;
		}

		public void setState(String state) {
				if(state != null && !state.isEmpty())
						this.state = state.trim();
		}

		public String getZip() {
				return zip;
		}

		public void setZip(String val) {
				if(val != null && !val.isEmpty())
						this.zip = val.trim();
			 
		}

		public String getDetails() {
				return details;
		}

		public void setDetails(String val) {
				if(val != null && !val.isEmpty())
						this.details = val.trim();
				
		}

		public Character getInvalidAddress() {
				return invalidAddress;
		}

		public void setInvalidAddress(Character val) {
				this.invalidAddress = val;
		}
		
		@Transient
		public boolean hasValidAddress(){
				return this.invalidAddress == null;
		}
		public String getDateDescription() {
				return dateDescription;
		}

		public void setDateDescription(String val) {
				if(val != null && !val.isEmpty())
						this.dateDescription = val.trim();
		}

		public Date getEndDate() {
				return endDate;
		}

		public void setEndDate(Date end_date) {
				this.endDate = end_date;
		}
		@Transient
		public String getStartEndDate(){
				String ret = "";
				if(date != null){
						ret = getDateStr();
				}
				if(endDate != null){
						if(!ret.equals("")) ret += " - ";
						ret += getEndDateStr();
				}
				return ret;
		}
		@Transient
		public String getCityStateZip(){
				String ret = "";
				if(city != null)
						ret += city;
				if(state != null){
						if(!ret.equals("")) ret += ", ";
						ret += state;
				}
				if(zip != null){
						if(!ret.equals("")) ret += " ";
						ret += zip;
				}				
				return ret;
		}
				
		@Transient
		public String getEndDateStr() {
				String str = "";
				if(endDate != null){
						try{
								str = Helper.dft.format(endDate);
						}catch(Exception ex){

						}
				}
				return str;
		}
		@Transient
		public void setEndDateStr(String val) {
				if(val != null && !val.equals("")){
						try{
								this.endDate = Helper.dft.parse(val);
						}catch(Exception ex){
								System.err.println(ex);
						}
				}
		}		

		public String getEntryType() {
				return entryType;
		}

		public void setEntryType(String val) {
				if(val != null && !val.trim().isEmpty())
						this.entryType = val;
		}
		public IncidentType getIncidentType() {
				return incidentType;
		}

		public void setIncidentType(IncidentType type) {
				this.incidentType = type;
		}
		public String getOtherEntry() {
				return otherEntry;
		}

		public void setOtherEntry(String val) {
				if(val != null && !val.isEmpty())
						this.otherEntry = val;
		}

		public Character getHaveMedia() {
				return haveMedia;
		}

		public void setHaveMedia(Character haveMedia) {
				this.haveMedia = haveMedia;
		}

		public String getEmail() {
				return email;
		}

		public void setEmail(String val) {
				if(val != null && !val.isEmpty())
						this.email = val;
		}
		public List<Property> getProperties(){
				return this.properties;
		}
		public List<Person> getPersons(){
				return this.persons;
		}		
		public void setPersons(List<Person> vals){
				this.persons = vals;
		}		
		public void setProperties(List<Property> properties){
				this.properties = properties;
		}
		public List<Vehicle> getVehicles(){
				return this.vehicles;
		}
		public void setVehicles(List<Vehicle> vehicles){
				this.vehicles = vehicles;
		}
		public List<ActionLog> getActionLogs(){
				return this.actionLogs;
		}
		public void setActionLogs(List<ActionLog> actionLogs){
				this.actionLogs = actionLogs;
		}
		public List<Media> getMedias(){
				return this.medias;
		}
		public void setMedias(List<Media> medias){
				this.medias = medias;
		}
		@Transient
		public boolean hasPersonList(){
				return persons != null && persons.size() > 0;
		}
		@Transient		
		public boolean hasPropertyList(){
				return properties != null && properties.size() > 0;
		}
		@Transient
		public boolean hasVehicleList(){
				return vehicles != null && vehicles.size() > 0;
		}
		@Transient
		public boolean hasMediaList(){
				return medias != null && medias.size() > 0;
		}		
		@Transient
		public void setReceivedNow(){
				received = new Date();
		}
		@Transient
		public String getAddressInfo(){
				String ret = address;
				if(city != null && !city.isEmpty()){
						if(!ret.isEmpty()) ret += " ";
						ret += city;
				}
				if(state != null && !state.isEmpty()){
						if(!ret.isEmpty()) ret += ", ";
						ret += state;
				}
				if(zip != null && !zip.isEmpty()){
						if(!ret.isEmpty()) ret += " ";
						ret += zip;
				}
				return ret;
		}
		@Transient
		public String getEntryInfo(){
				String ret = "";
				if(entryType != null)
						ret += entryType;
				if(otherEntry != null && !otherEntry.isEmpty()){
						if(!ret.isEmpty()) ret += " ";
						ret += otherEntry;
				}
				return ret;
		}
		
		@Override
    public boolean equals(Object obj) { 
          
				if(this == obj) 
						return true; 
				
        if(obj == null || obj.getClass()!= this.getClass()) 
            return false; 
				
        Incident one = (Incident) obj; 
        return one.getId() == this.getId();
		}
		@Override
		public int hashCode(){ 
				int ret = 37;
        return ret += this.id; 
    }
		@Override
		public String toString() {
			return "Incident " + id;
		}
		@Transient
		public boolean verifyAll(
														 String default_city,
														 String default_state,
														 List<String> default_zip_codes){
				boolean ret = verifyTimes();
				ret = ret && verifyAddressInfo(default_city,
																	default_state,
																	default_zip_codes);
				ret = ret && verifyDetails();
				return ret;
		}
				
		@Transient
		public boolean verifyTimes(){
				if(date == null && (dateDescription == null ||
														dateDescription.equals(""))){
							 addError("incident date and time are required");
							 return false;
				}
				else if(date != null){
						Date date2 = new Date();
						if(date2.compareTo(date) < 0){
								addError("Incident date can not be in the future");
								return false;
						}
				}
				return true;
		}
		@Transient
		public boolean verifyDetails(){
				if(details == null || details.equals("")){
						addError("Incident details are required");
						return false;
				}
				return true;
		}
		@Transient
		public String getDateTimeInfo(){
				String ret = "";
				if(date != null){
						ret = "Started: "+getDateStr();
				}
				if(endDate != null){
						if(!ret.isEmpty()) ret +=", ";
						ret += " Ended: "+getEndDateStr();
				}
				if(dateDescription != null && !dateDescription.isEmpty()){
						if(!ret.isEmpty()) ret += ", ";
						ret += dateDescription;
				}
				return ret;
		}
				
		@Transient
		public boolean verifyAddressInfo(String default_city,
																		 String default_state,
																		 List<String> default_zip_codes){
				if(address == null || address.trim().equals("")){
						addError("Address is required");
						return false;
				}
				else if(city == null || city.equals("")){
						addError("City is required");
						return false;
				}
				else if(!city.equals(default_city)){
						addError("Invalid city "+city);
						return false;
				}
				else if(state == null || state.equals("")){
						addError("State is required");
						return false;
				}
				else if(!state.equals(default_state)){
						addError("Invalid state "+state);
						return false;
				}
				else if(zip == null || zip.equals("")){
						addError("Zip code is required");
						return false;
				}
				else if(zip != null && !zip.equals("")){
						for(String str:default_zip_codes){
								if(zip.equals(str)){
										return true;
								}
						}
						addError("invalid zipcode "+zip);
						return false;
				}
				return true;
		}
						
}
