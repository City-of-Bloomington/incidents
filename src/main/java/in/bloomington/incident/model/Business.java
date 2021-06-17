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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.CascadeType;
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
@Table(name = "businesses")
public class Business extends TopModel implements java.io.Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
		@Column(name="name")
    private String name;
		@Column(name="corporate_address")
    private String corporateAddress;
		@Column(name="business_number")		
		private String businessNumber;
		@Column(name="phone")		
		private String phone;
		@Column(name="email")		
		private String email;
		@Column(name="reporter_name")
		private String reporterName;
		@Column(name="reporter_title")
		private String reporterTitle;
		
		@OneToOne
		@JoinColumn(name="address_id", updatable=false, referencedColumnName="id")
    private Address address;

		/**
    @OneToMany(fetch=FetchType.LAZY, mappedBy="business")
    private List<Incident> incidents;
		*/
		/**
		 * this is disabled right now
		 * since no login is required
		 * this may change in the future release
		 *
		@OneToOne(cascade=CascadeType.ALL, targetEntity=Credential.class,
							mappedBy="business")
    private Credential credential;
		*/
		
		@Transient
		private int addr_id = 0;
		@Transient
		private int type_id = 0; // for incident type (theft, vandal,..)
		@Transient
		private String email2; // for verification only
		//
		// Needed when email is changed
		@Transient
		private String oldEmail;
    //
    public Business(){

    }
    public Business(int id,
										String name,
										String corporateAddress,
										String businessNumber,
										String phone,
										String email,

										String reporterName,
										String reporterTitle,
										Address address
										) {
				super();
				this.id = id;
				this.name = name;
				this.corporateAddress = corporateAddress;
				this.businessNumber = businessNumber;
				this.phone = phone;
				this.email = email;
				this.reporterName = reporterName;
				this.reporterTitle = reporterTitle;
				this.address = address;
    }
    public int getId() {
				return id;
    }

    public void setId(int id) {
				this.id = id;
    }

    public String getBusinessNumber() {
				return businessNumber;
    }

    public void setName(String val) {
				if(val != null && !val.isEmpty())
						this.name = val;
    }
    public void setReporterName(String val) {
				if(val != null && !val.isEmpty())
						this.reporterName = val.trim();
    }
    public void setReporterTitle(String val) {
				if(val != null && !val.isEmpty())
						this.reporterTitle = val.trim();
    }		
    public void setCorporateAddress(String val) {
				if(val != null && !val.isEmpty())
						this.corporateAddress = val;
    }
    public String getCorporateAddress() {
				return corporateAddress;
    }
		public int getType_id(){
				return type_id;
		}
		public void setType_id(int val){
				type_id = val;
		}
    public void setBusinessNumber(String val) {
				if(val != null && !val.isEmpty())
						this.businessNumber = val;
    }
    public void setPhone(String val) {
				if(val != null && !val.isEmpty())
						this.phone = val;
    }
    public void setEmail(String val) {
				if(val != null && !val.isEmpty())
						this.email = val.toLowerCase();
    }
    public void setEmail2(String val) {
				if(val != null && !val.isEmpty())
						this.email2 = val.toLowerCase();
    }		
    public void setOldEmail(String val) {
				if(val != null && !val.isEmpty())
						this.oldEmail = val;
    }		
    public void setAddress(Address val) {
				if(val != null){
						this.address = val;
						addr_id = this.address.getId();
				}
    }
    public String getName() {
				return name;
    }
    public String getPhone() {
				return phone;
    }
		
    public String getEmail() {
				return email;
    }
    public String getEmail2() {
				if(email2 == null && email != null){
						return email;
				}
				return email2;
    }		
    public String getReporterName() {
				return reporterName;
    }
    public String getReporterTitle() {
				return reporterTitle;
    }		
    public Address getAddress() {
				return address;
    }

		@Transient
		public String getOldEmail(){
				if(oldEmail == null)
						return email;
				return oldEmail;
		}
		@Transient
		public boolean isEmailChanged(){
				return !(email != null && oldEmail != null &&
								 email.equals(oldEmail));
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
    public String getInfo(){
				String ret = name != null?name:"";
				if(businessNumber != null && !businessNumber.isEmpty()){
						if(!ret.isEmpty()){
								ret += ", ";
						}
						ret += "#: "+businessNumber;				
				}
				return ret;
    }
		@Transient
		public boolean hasCorporateData(){
				return !getCorporateInfo().isEmpty();
		}
    @Transient
    public boolean verify(){
				boolean ret = true;
				if(name == null || name.isEmpty()){
						addError("business name is required");
						ret = false;
				}
				if(email == null || email.isEmpty()){
						addError("Business email is required");
						ret = false;
				}
				else if(email2 == null || email2.isEmpty()){
						addError("Business verify email is required");
						ret = false;
				}
				else if(!email.equals(email2)){
						addError("The two emails are not the same,try again");
						ret = false;
				}
				if(phone == null || phone.isEmpty()){
						addError("Business phone is required");
						ret = false;
				}				
				return ret;
    }
		@Transient
		public String getReporterInfo(){
				String ret = reporterName == null? "":reporterName;
				if(reporterTitle != null && !reporterTitle.isEmpty()){
						if(!ret.isEmpty()){
								ret += " ";
						}
						ret += "("+reporterTitle+")";
				}
				return ret;
				
		}
		@Transient
		public boolean hasReporterInfo(){
				return !getReporterInfo().isEmpty();
		}
		@Transient
		public String getCorporateInfo(){
				String ret = "";
				if(name != null && !name.isEmpty()){
						ret += "Name: "+name;
				}
				if(businessNumber != null && !businessNumber.isEmpty()){
						if(!ret.isEmpty()){
								ret += ", ";
						}
						ret += "#: "+businessNumber;				
				}
				if(corporateAddress != null && !corporateAddress.isEmpty()){
						if(!ret.isEmpty()){
								ret += ", ";
						}
						ret += "Corporate address: "+corporateAddress;	
				}
				return ret;
		}
		@Transient
		public boolean hasContactData(){
				return !getContactInfo().isEmpty();
		}
		@Transient
		public String getContactInfo(){
				String ret = "";
				if(email != null && !email.isEmpty()){
						ret += "Email: "+email;
				}
				if(phone != null && !phone.isEmpty()){
						if(!ret.isEmpty()){
								ret +=", ";
						}
						ret += "Phone(s): "+phone;
				}				
				return ret;
		}
		@Transient
		public boolean hasAddressInfo(){
				return address != null;
		}		
    @Transient
    public String getAddressInfo(){
				String ret = "";
				if(hasAddressInfo()){
						ret += address.getInfo();
				}
				return ret;
    }		
		@Override
    public boolean equals(Object obj) { 
          
				if(this == obj) 
						return true; 
				
        if(obj == null || obj.getClass()!= this.getClass()) 
            return false; 
				
        Business one = (Business) obj; 
        return one.getId() == this.getId();
    }
    @Override
    public int hashCode(){ 
				int ret = 29;
        return ret += this.id; 
    }
    @Override
    public String toString() {
				return getInfo();
    } 		

		
}
