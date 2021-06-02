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
		@OneToOne
		@JoinColumn(name="address_id", updatable=false, referencedColumnName="id")
    private Address address;
		@OneToOne
		@JoinColumn(name="business_id", referencedColumnName="id")
    private Credential credential;
		
		@Transient
		private int addr_id = 0;
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
										
										Address address,
										Credential credential
										) {
				super();
				this.id = id;
				this.name = name;
				this.corporateAddress = corporateAddress;
				this.businessNumber = businessNumber;
				this.phone = phone;
				this.email = email;
				this.address = address;
				this.credential = credential;
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

    public String getCorporateAddress() {
				return corporateAddress;
    }
		public Credential getCredential(){
				return credential;
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
    public void setOldEmail(String val) {
				if(val != null && !val.isEmpty())
						this.oldEmail = val.toLowerCase();
    }		
    public void setAddress(Address val) {
				if(val != null)
						this.address = val;
    }

    public String getPhone() {
				return phone;
    }
    public String getEmail() {
				return email;
    }
		public String getOldEmail(){
				if(oldEmail !=  null) return oldEmail;
				return email;
		}
    public Address getAddress() {
				return address;
    }
		public void setCredential(Credential val){
				credential = val;
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
				if(phone == null || phone.isEmpty()){
						addError("Business phone is required");
						ret = false;
				}				
				return ret;
    }
		@Transient
		String getCorporateInfo(){
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
		String getContactInfo(){
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
		boolean hasCorporateInfo(){
				return !getCorporateInfo().isEmpty();
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
