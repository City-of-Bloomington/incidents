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
import java.util.stream.Collectors;
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
import javax.persistence.Convert;
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

		@Column(name="case_number")
    private String caseNumber;
		
    @OneToOne
		@JoinColumn(name="incident_type_id", referencedColumnName="id")
    private IncidentType incidentType;

    private Date received;

    private Date date;

    private String details;

		private String evidence;
		
    // private Character invalidAddress;
		@Column(name="date_description")
    private String dateDescription;
		@Column(name="end_date")
    private Date endDate;
		@Column(name="entry_type")
    private String entryType;
		@Column(name="other_entry")
    private String otherEntry;
		@Column(name="have_media")
    private Character haveMedia;

    private String email;

    @Transient
    private String dateStr=null;
    @Transient
    private String timeStr=null;
    @Transient
    private String endDateStr=null;
    @Transient
    private String endTimeStr=null;
    @Transient
    private Action lastAction=null;
    @Transient
    String status = "";
    @Transient
    private Double latitude;
    @Transient
    private Double longitude;    
    @Transient
    private String jurisdiction;
    @Transient
    private String oldAddress;
		@Transient
		private int addr_id = 0;
    
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
    private List<Fraud> frauds;
		
    @OneToMany
    @JoinColumn(name="incident_id",insertable=false, updatable=false)		
    private List<Media> medias;

    @OneToMany
    @JoinColumn(name="incident_id",insertable=false, updatable=false)
    private List<ActionLog> actionLogs;

		@Convert(converter = AddressConverter.class)
		@OneToOne
		// @ManyToOne(optional=false, fetch=FetchType.EAGER)
		@JoinColumn(name="address_id", updatable=false, referencedColumnName="id")
    Address address;
		
    public Incident(){
				super();
    }
		
    public Incident(int id,
										String caseNumber,
										IncidentType incidentType,
										Date received,
										Date date,
										String details,
										String evidence,
										String dateDescription,
										Date endDate,
										String entryType,
										String otherEntry,
										Character haveMedia,
										String email,
										Address address,
										List<Person> persons,										
										List<Property> properties,
										List<Vehicle> vehicles,
										List<ActionLog> actionLogs,
										List<Media> medias,
										List<Fraud> frauds
										
										) {
				super();
				this.id = id;
				this.caseNumber = caseNumber;
				this.incidentType = incidentType;
				this.received = received;
				this.date = date;
				this.details = details;
				this.evidence = evidence;
				this.dateDescription = dateDescription;
				this.endDate = endDate;
				this.entryType = entryType;
				this.otherEntry = otherEntry;
				this.haveMedia = haveMedia;
				this.email = email;
				this.persons = persons;				
				this.properties = properties;
				this.vehicles = vehicles;
				setActionLogs(actionLogs);
				this.medias = medias;
				this.frauds = frauds;
				this.address = address;
    }

    public int getId() {
				return id;
    }

    public void setId(int id) {
				this.id = id;
    }

    public String getCaseNumber() {
				return caseNumber;
    }

    public void setCaseNumber(String val) {
				
				if(val != null && !val.isEmpty())						
						this.caseNumber = val.trim();
    }
    @Transient
    public boolean hasCaseNumber(){
				return caseNumber != null && !caseNumber.isEmpty();
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
								str = Helper.dfDate.format(date);
						}catch(Exception ex){

						}
				}
				return str;
    }
    @Transient
    public String getTimeStr() {
				String str="";
				if(date != null){
						try{
								str = Helper.dfTimeAmPm.format(date);
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
						dateStr = val;
						date = setDateValue(dateStr, timeStr);
				}
    }
    @Transient
    public void setTimeStr(String val) {
				System.err.println(" time "+val);
				if(val != null && !val.equals("")){
						timeStr = val;
						date = setDateValue(dateStr, timeStr);
				}
    }
    @Transient
    private Date setDateValue(String ddate, String ttime){
				Date ret = null;
				if(ddate != null && !ddate.isEmpty() &&
					 ttime != null && !ttime.isEmpty()){
						String str = ddate+" "+ttime;
						try{
								ret = Helper.dfDateTimeAmPm.parse(str);
						}catch(Exception ex){
								System.err.println(" invalid time format");
								addError(" "+ex);
						}						
						
				}
				return ret;
    }

		public Address getAddress() {
				return address;
    }

    public void setAddress(Address val) {
				if(val != null){
						this.address = val;
						if(val != null)
								addr_id = address.getId();
				}
    }		

    public String getDetails() {
				return details;
    }

    public void setDetails(String val) {
				if(val != null && !val.trim().isEmpty())
						this.details = val.trim();
				
    }
    public String getEvidence() {
				return evidence;
    }

    public void setEvidence(String val) {
				if(val != null && !val.trim().isEmpty())
						this.evidence = val.trim();
				
    }		
		/*
    @Transient
    public boolean hasValidAddress(){
				return this.invalidAddress == null;
    }
		*/		
    @Transient
    public boolean hasEvidenceInfo(){
				return this.evidence != null && !this.evidence.isEmpty();
    }		
    @Transient
    public boolean isVehicleRequired(){
				boolean ret = false;
				if(incidentType != null && incidentType.isVehicleRequired()){
						ret = true;
				}
				return ret;
    }

		@Transient
		public boolean hasAddressInfo(){
				return address != null;
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
    public boolean isLostRelated(){
				return incidentType != null && incidentType.isLostRelated();
    }
    @Transient
    public boolean isNotLostRelated(){
				return !isLostRelated();
    }
    @Transient
    public boolean isVandalRelated(){
				return incidentType != null && incidentType.isVandalRelated();
    }
		
    @Transient
    public boolean isNotVandalRelated(){
				return !isVandalRelated();
    }
		@Transient
    public boolean isFraudRelated(){
				return incidentType != null && incidentType.isFraudRelated();
    }
		// entry gained is not required for lost property or vandalism
    @Transient
    public boolean showGainedEntry(){
				return !(isVandalRelated() || isLostRelated() || isFraudRelated());
    }				
    @Transient
    public boolean canEdit(){
				return canBeChanged();
    }
		@Transient
		public int getAddr_id(){
				if(address != null)
						addr_id = address.getId();
				return addr_id;
		}
		@Transient
		public void setAddr_id(int val){
				addr_id = val;
		}
    @Transient
    public String getStartEndDate(){
				String ret = "";
				try{
						if(date != null){
								if(date != null){
										ret = "Started: "+Helper.dft.format(date);
								}
						}
						if(endDate != null){
								if(!ret.equals("")) ret += " - ";
								ret += " Ended: "+Helper.dft.format(endDate);;
						}
				}catch(Exception ex){
						System.err.println(ex);
				}
				return ret;
    }
    @Transient
    public String getCityStateZip(){
				String ret = "";
				if(hasAddressInfo()){
						ret = address.getCityStateZip();
				}
				return ret;
    }
				
    @Transient
    public String getEndDateStr() {
				String str = "";
				if(endDate != null){
						try{
								str = Helper.dfDate.format(endDate);
						}catch(Exception ex){

						}
				}
				return str;
    }
    @Transient
    public void setEndDateStr(String val) {
				if(val != null && !val.equals("")){
						endDateStr = val;
						endDate = setDateValue(endDateStr, endTimeStr);
				}
    }
    @Transient
    public String getEndTimeStr() {
				String str = "";
				if(endDate != null){
						try{
								str = Helper.dfTimeAmPm.format(endDate);
						}catch(Exception ex){

						}
				}
				return str;
    }
    @Transient
    public void setEndTimeStr(String val) {
				if(val != null && !val.equals("")){
						endTimeStr = val;
						endDate = setDateValue(endDateStr, endTimeStr);
				}
    }				
    @Transient
    public boolean hasAccessInfo(){
				return isNotLostRelated() && !getEntryInfo().isEmpty();
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

    public boolean hasEmail() {
				return email != null && !email.isEmpty();
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
    public void setProperties(List<Property> properties){
				this.properties = properties;
    }
    public List<Person> getPersons(){
				return this.persons;
    }		
    public void setPersons(List<Person> vals){
				this.persons = vals;
    }
    public List<Fraud> getFrauds(){
				return this.frauds;
    }		
    public void setFrauds(List<Fraud> vals){
				this.frauds = vals;
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
    //sorted at the same time
    public void setActionLogs(List<ActionLog> list){
				if(list != null){
						if(list.size() > 1){ 
								actionLogs = list.stream().
										sorted((o1, o2)->o1.getAction().getObjId().
													 compareTo(o2.getAction().getObjId())).
										collect(Collectors.toList());
						}
						else{
								actionLogs = list;
						}
				}
    }
    @Transient
    public void sortActionLogs(){
				List<ActionLog> list = new ArrayList<>();
				if(actionLogs != null){
						for(ActionLog log:actionLogs){
								list.add(log);
						}
						if(list.size() > 1){
								// reverse order last first
								actionLogs = list.stream().
										sorted((o1, o2)->o2.getAction().getObjId().
													 compareTo(o1.getAction().getObjId())).
										collect(Collectors.toList());
						}
						System.err.println(" logs  "+actionLogs);						
				}
    }		
    // status is the last action
    @Transient
    public String getStatus(){
				if(status.isEmpty()){
						sortActionLogs();
						if(actionLogs != null && actionLogs.size() > 0){
								ActionLog actionLog = actionLogs.get(0);
								lastAction = actionLog.getAction();
								status = lastAction.getDescription();
						}
				}
				return status;
    }
		@Transient
		public boolean hasStatusInfo(){
				getStatus();
				return !status.isEmpty();
		}
    @Transient
    public boolean hasNextAction(){
				getStatus();
				if(lastAction != null && lastAction.hasNextstep()){
						return true;
				}
				return false;
    }
    @Transient
    public Action getLastAction(){		
				return lastAction;
    }
    public List<Media> getMedias(){
				return this.medias;
    }
    public void setMedias(List<Media> medias){
				this.medias = medias;
    }
		@Transient
		public String getFirstPersonId(){
				if(hasPersonList()){
						return ""+persons.get(0).getId();
				}
				return "";
		}
    /**
     *
     the incident can be submitted if the following conditions are met
     1-It has at least one person
     2-It has either one property or one vehicle included
     3-Has no action logs yet
    */
    @Transient
    public boolean canBeSubmitted(){
				if(!hasPersonList()){
						addError("Person information are required");
						return false;
				}
				if(isFraudRelated() && !hasFraudList()){
						addError("You need to add fraud/scam information");
						return false;
				}
				if(!isFraudRelated() && !hasPropertyList()){
						addError("You need to add property information");
						return false;
				}
				if(isVehicleRequired() && !hasVehicleList()){
						addError("You need to add vehicle information");
						return false;
				}
				if(hasActionLogs() && !hasEmailActionLogOnly()){
						addError("The incident is already submitted");
						return false;
				}
				return true;
    }
    //
    // to check if the incident can be changed
    // if the incident is submitted no more changes can be done
    @Transient
    public boolean canBeChanged(){
				return !hasActionLogs() || hasEmailActionLogOnly();
    }    
    @Transient
    public boolean hasFraudList(){
				return frauds != null && frauds.size() > 0;
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
		public int getMediaCount(){
				return hasMediaList() ? medias.size():0;
		}
    @Transient
    public boolean hasActionLogs(){
				return actionLogs != null && actionLogs.size() > 0;
    }
    @Transient
    public boolean hasEmailActionLogOnly(){
				if(hasActionLogs()){
						if(actionLogs.size() == 1){
								ActionLog actionLog = actionLogs.get(0);
								Action action = actionLog.getAction();
								if(action != null && action.getName().indexOf("email") > -1){
										return true;
								}
						}
				}
				return false;
    }
    @Transient
    public Double getLatitude(){
				return latitude;
    }
    @Transient
    public Double getLongitude(){
				return longitude;
    }
    @Transient
    public String getJurisdiction(){
				return jurisdiction;
    }
    @Transient
    public void setLatitude(Double val){
				latitude = val;
    }
    @Transient
    public void setLongitude(Double val){
				longitude = val;
    }
    @Transient
    public void setJurisdiction(String val){
				jurisdiction = val;
    }    
    @Transient
    public void setReceivedNow(){
				received = new Date();
    }
    @Transient
    public double getTotalValue(){
				double total = 0;
				if(hasPropertyList()){
						if(properties  != null){
								for(Property one:properties){
										if(one.getValue() != null){
												total += one.getValue();
										}
								}
						}
				}
				if(hasVehicleList()){
						if(vehicles  != null){
								for(Vehicle one:vehicles){
										if(one.getValue() != null){
												total += one.getValue();
										}
								}
						}
				}
				return total;
    }
    @Transient
    public String getTotalValueFr(){
				String str="";
				double total = getTotalValue();
				if(total > 0){
						str = Helper.curFr.format(total);
				}
				return str;
    }
    @Transient
    public String getAddressInfo(){
				String ret = "";
				if(hasAddressInfo()){
						ret += address.getInfo();
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
    public boolean verifyAll(){
				boolean ret = verifyTimes();
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
				if(endDate != null){
						Date date2 = new Date();
						if(date2.compareTo(endDate) < 0){
								addError("Incident end date can not be in the future");
								return false;
						}
				}
				return true;
    }
    @Transient
    public boolean verifyDetails(){
				if(details == null || details.trim().isEmpty()){
						addError("Incident details are required");
						return false;
				}
				return true;
    }
    @Transient
    public String getDateTimeInfo(){
				String ret = getStartEndDate();
				if(dateDescription != null && !dateDescription.isEmpty()){
						if(!ret.isEmpty()) ret += ", ";
						ret += dateDescription;
				}
				return ret;
    }

}
