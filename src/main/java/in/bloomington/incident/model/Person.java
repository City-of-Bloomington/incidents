package in.bloomington.incident.model;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.Date;
import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import in.bloomington.incident.utils.Helper;


@Entity
@Table(name = "persons")
public class Person extends TopModel implements java.io.Serializable{
		
		@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
		//
		@NotNull(message = "Incident is required")		
		@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id")
		Incident incident;
		@OneToOne

		PersonType personType;
		String title; // emum ('Mr','Ms','Mrs')
		@NotNull(message = "First name is required")		
		String firstname;
		@NotNull(message = "Lsst name is required")				
		String lastname;
		String midname;
		String suffix;
		String address;
		String city;
		String state;
		String zip;
		String phone;
		String phone2;
		String phoneType; // emum('Cell','Home','Work')
		// added
		String phoneType2; // emum('Cell','Home','Work')
		String email;
		String email2;
		String dln;
		Date dob;
		String ssn;
		String race; // enum('Caucasion','Hispanic','African American','Native American','Asian','Other')
		Integer heightFeet;
		Integer heightInch;
		String weight;
		String sex; // emum('Male','Female','Other'),
		String occupation;
		Character reporter;
		// the following are temp var 
		@Transient
		Integer dobDay;
		@Transient
		Integer dobMonth;
		@Transient
		Integer dobYear;
		
		public Person(){

		}
		
		public Person(int id,
									@NotNull(message = "Incident is required") Incident incident,
									PersonType personType,
									String title,
									@NotNull(message = "First name is required") String firstname,
									@NotNull(message = "Lsst name is required") String lastname,
									String midname,
									String suffix,
									String address,
									String city,
									String state,
									String zip,
									String phone,
									String phone2,
									String phoneType,
									String phoneType2,
									String email,
									String email2,
									String dln,
									Date dob,
									String ssn,
									String race,
									Integer height_feet,
									Integer height_inch,
									String weight,
									String sex,
									String occupation,
									Character reporter) {
				super();
				this.id = id;
				this.incident = incident;
				this.personType = personType;
				this.title = title;
				this.firstname = firstname;
				this.lastname = lastname;
				this.midname = midname;
				this.suffix = suffix;
				this.address = address;
				this.city = city;
				this.state = state;
				this.zip = zip;
				this.phone = phone;
				this.phone2 = phone2;
				this.phoneType = phoneType;
				this.phoneType2 = phoneType2;
				this.email = email;
				this.email2 = email2;
				this.dln = dln;
				this.dob = dob;
				this.ssn = ssn;
				this.race = race;
				this.heightFeet = height_feet;
				this.heightInch = height_inch;
				this.weight = weight;
				this.sex = sex;
				this.occupation = occupation;
				this.reporter = reporter;
		}

		public int getId() {
				return id;
		}

		public void setId(int id) {
				this.id = id;
		}

		public Incident getIncident() {
				return incident;
		}

		public void setIncident(Incident incident) {
				this.incident = incident;
		}

		public PersonType getPersonType() {
				return personType;
		}

		public void setPersonType(PersonType personType) {
				this.personType = personType;
		}

		public String getTitle() {
				return title;
		}

		public void setTitle(String val) {
				if(val != null && !val.isEmpty())
						this.title = val;
		}

		public String getFirstname() {
				return firstname;
		}

		public void setFirstname(String val) {
				if(val != null && !val.isEmpty())
						this.firstname = val;
		}

		public String getLastname() {
				return lastname;
		}

		public void setLastname(String val) {
				if(val != null && !val.isEmpty())
						this.lastname = val;
		}

		public String getMidname() {
				return midname;
		}

		public void setMidname(String val) {
				if(val != null && !val.isEmpty())
						this.midname = val;
		}

		public String getSuffix() {
				return suffix;
		}

		public void setSuffix(String val) {
				if(val != null && !val.isEmpty())
				this.suffix = val;
		}

		public String getAddress() {
				return address;
		}

		public void setAddress(String val) {
				if(val != null && !val.isEmpty())
						this.address = val;
		}

		public String getCity() {
				return city;
		}

		public void setCity(String val) {
				if(val != null && !val.isEmpty())
						this.city = val;
		}

		public String getState() {
				return state;
		}

		public void setState(String val) {
				if(val != null && !val.isEmpty())
						this.state = val;
		}

		public String getZip() {
				return zip;
		}

		public void setZip(String val) {
				if(val != null && !val.isEmpty())
						this.zip = val;
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

		public String getPhone() {
				return phone;
		}

		public void setPhone(String val) {
				if(val != null && !val.isEmpty())
						this.phone = val;
		}

		public String getPhone2() {
				return phone2;
		}

		public void setPhone2(String val) {
				if(val != null && !val.isEmpty())
						this.phone2 = val;
		}

		public String getPhoneType() {
				return phoneType;
		}

		public void setPhoneType(String val) {
				if(val != null && !val.isEmpty())
						this.phoneType = val;
		}

		public String getPhoneType2() {
				return phoneType2;
		}

		public void setPhoneType2(String val) {
				if(val != null && !val.isEmpty())
						this.phoneType2 = val;
		}

		public String getEmail() {
				return email;
		}

		public void setEmail(String val) {
				if(val != null && !val.isEmpty())
						this.email = val;
		}

		public String getEmail2() {
				return email2;
		}

		public void setEmail2(String val) {
				if(val != null && !val.isEmpty())
				this.email2 = val;
		}

		public String getDln() {
				return dln;
		}

		public void setDln(String val) {
				if(val != null && !val.isEmpty())
				this.dln = val;
		}

		public Date getDob() {
				if(dob == null
					 && dobYear != null
					 && dobMonth != null
					 && dobDay != null){
						setDobFromParts();
				}
				return this.dob;
		}
		@Transient
		public String getDobStr(){
				String ret = "";
				getDob();
				if(dob != null){
						ret = Helper.df.format(dob);
				}
				return ret;
		}
				
		@Transient
		void doSplitDob(){
				
				if(dob != null){
						String str = Helper.df.format(dob);
						try{
								String[] arr = str.split("/");
								if(arr != null && arr.length == 3){
										dobMonth = Integer.parseInt(arr[0]);
										dobDay = Integer.parseInt(arr[1]);
										dobYear = Integer.parseInt(arr[2]);
								}
						}
						catch(Exception ex){
								System.err.println(ex);
						}
				}
		}
		public Integer getDobYear(){
				if(dobYear == null && dob != null){
						doSplitDob();
				}
				return dobYear;
		}
		public Integer getDobMonth(){
				if(dobMonth == null && dob != null){
						doSplitDob();
				}
				return dobMonth;
		}
		public Integer getDobDay(){
				if(dobDay == null && dob != null){
						doSplitDob();
				}
				return dobDay;
		}
		public void setDob(Date dob) {
				this.dob = dob;
		}
		
		public void setDobYear(Integer val) {
				if(val !=null)
						this.dobYear = val;
				setDobFromParts();
		}
		public void setDobMonth(Integer val) {
				if(val != null)
						this.dobMonth = val;
				setDobFromParts();
		}
		public void setDobDay(Integer val) {
				if(val != null)
						this.dobDay = val;
				setDobFromParts();
		}
		@Transient
		void setDobFromParts(){
				if(this.dobYear != null && this.dobMonth != null &&
					 dobDay != null){
						String str = this.dobMonth+"/"+this.dobDay+"/"+this.dobYear;
						try{
								dob = Helper.df.parse(str);
						}catch(Exception ex){
								System.err.println(ex);
						}
				}
		}
		
		public String getSsn() {
				return ssn;
		}

		public void setSsn(String val) {
				if(val != null && !val.isEmpty())
						this.ssn = val;
		}

		public String getRace() {
				return race;
		}

		public void setRace(String val) {
				if(val != null && !val.isEmpty())
						this.race = val;
		}

		public Integer getHeightFeet() {
				return heightFeet;
		}

		public void setHeightFeet(Integer val) {
				if(val != null)
						this.heightFeet = val;
		}

		public Integer getHeightInch() {
				return heightInch;
		}

		public void setHeightInch(Integer val) {
				if(val != null)
						this.heightInch = val;
		}

		public String getWeight() {
				return weight;
		}

		public void setWeight(String val) {
				if(val != null && !val.isEmpty())
						this.weight = val;
		}

		public String getSex() {
				return sex;
		}

		public void setSex(String val) {
				if(val != null && !val.isEmpty())
						this.sex = val;
		}

		public String getOccupation() {
				return occupation;
		}

		public void setOccupation(String val) {
				if(val != null && !val.isEmpty())
						this.occupation = val;
		}

		public Character getReporter() {
				return reporter;
		}

		public void setReporter(Character val) {
				if(val != null)
				this.reporter = val;
		}
		@Transient
		public boolean verify(){
				boolean ret = true;
				if(firstname == null || firstname.isEmpty()){
						addError("First name is required");
						ret = false;
				}
				if(lastname == null || lastname.isEmpty()){
						addError("Last name is required");
						ret = false;
				}
				if(address == null || address.isEmpty()){
						addError("Address is required");
						ret = false;
				}
				if(!isMoreThan18()){
						ret = false;
				}
				String str = getContactInfo();
				if(str.isEmpty()){
						addError("A contact phone number or email is required");
						ret = false;
				}
				
				return ret;
		}
		@Transient
		private boolean isMoreThan18(){
				boolean ret = false;
				if(dob != null){
						Calendar calendar = Calendar.getInstance();
						System.out.println("Current Date = " + calendar.getTime());
						// Add -18 years to compare to 
						calendar.add(Calendar.YEAR, -18);
						Date date = calendar.getTime();
						if(dob.compareTo(date) > 0){
								addError("Person age can not be less than 18");
								return ret;
						}
						ret = true;
				}
				return ret;
		}
				
				
		@Transient
		public String getSexAndRace(){
				String ret = "";
				if(sex != null && !sex.isEmpty()){
						ret += sex;
				}
				if(race != null && !race.isEmpty()){
						if(!ret.isEmpty()) ret += ", ";
						ret += race;
				}
				return ret;
		}
		@Transient
		public String getHeightAndWeight(){
				String ret = "";
				if(heightFeet != null){
						ret += "Height: "+heightFeet+"ft.";
						if(heightInch != null)
								if(!ret.isEmpty()) ret += " "; 
								ret += heightInch+"in.";
				}
				if(weight != null && !weight.isEmpty()){
						if(!ret.isEmpty()) ret += ", ";
						ret += " Weight: "+weight;
				}
				return ret;
		}				

		@Transient
		public String getSsnLastFour(){
				String ret = "";
				if(ssn != null){
						if(ssn.length() >= 9){
								ret = ssn.substring(5);
						}
				}
				return ret;
		}
		@Transient
		public String getEmails(){
				String ret = "";
				if(email != null)
						ret = email;
				if(email2 != null){
						if(!ret.equals("")) ret += ", ";
						ret += email2;
				}
				return ret;
		}
		@Transient
		public String getPhones(){
				String ret = "";
				if(phone != null){
						ret += phone;
						if(phoneType != null)
								ret += " ("+phoneType+")";
				}
				if(phone2 != null){
						if(!ret.equals("")) ret += ", ";
						ret += phone2;
						if(phoneType != null)
								ret += " ("+phoneType2+")";
				}				
				return ret;
		}
		@Transient
		public String getContactInfo(){
				String ret = "";
				String str = getPhones();
				if(!str.isEmpty()){
						ret = "phone(s): "+str;
				}
				str = getEmails();
				if(!str.isEmpty()){
						if(!ret.isEmpty()){
								ret += ", ";
						}
						ret = "email(s): "+str;
				}				
				return ret;
		}
				
		@Transient
		public String getHeight(){
				String ret = "";
				if(heightFeet != null){
						ret += heightFeet+" (ft.)";
						if(heightInch != null){
								ret += " ";
								ret += heightInch+" (in.)";
						}
				}
				return ret;
		}
		@Transient
		public String getRaceAndSex(){
				String ret = "";
				if(race != null)
						ret += race;
				if(sex != null){
						if(!ret.equals("")) ret += ", ";
						ret += sex;
				}
				return ret;
		}
				
		//
		// we need setters for these taken from dob
		//
		@Override
    public boolean equals(Object obj) { 
          
				if(this == obj) 
						return true; 
				
        if(obj == null || obj.getClass()!= this.getClass()) 
            return false; 
				
        Person one = (Person) obj; 
        return one.getId() == this.getId();
		}
		@Transient
		public String getFullname(){
				String ret = "";
				if(title != null && !title.isEmpty()){
						ret += title;
				}
				if(lastname != null && !lastname.isEmpty()){
						if(!ret.isEmpty()) ret += " ";
						ret += lastname;
				}
				if(firstname != null && !firstname.isEmpty()){
						if(!ret.isEmpty()) ret += ", ";
						ret += firstname;
				}
				if(midname != null && !midname.isEmpty()){
						if(!ret.isEmpty()) ret += " ";
						ret += midname;
				}
				if(suffix != null && !suffix.isEmpty()){
						if(!ret.isEmpty()) ret += " ";
						ret += suffix;
				}				
				return ret;

		}
		@Transient
		public String getFullAddress(){
				String ret = "";
				if(address != null && !address.isEmpty()){
						ret = address;
				}
				String str = getCityStateZip();
				if(!str.isEmpty()){
						if(!ret.isEmpty()) ret += ", ";						
						ret += str;
				}
				return ret;
		}
				
		
		@Override
		public int hashCode(){ 
				int ret = 29;
        return ret += this.id; 
    }
		@Override
		public String toString() {
				return getFullname();
		} 	

}
