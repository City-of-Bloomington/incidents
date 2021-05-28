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
    String name;
		@Column(name="corporate_address")
    String corporateAddress;
		@Column(name="business_number")		
		String businessNumber;
		@Column(name="phone")		
		String phone;
		@Column(name="email")		
		String email;
		@Column(name="street_address")		
		String streetAdress;
		@Column(name="street_address2")		
		String streetAdress2;
		@Column(name="city")		
		String city;
		@Column(name="state")		
		String state;
		@Column(name="zip_code")		
		String zipCode;
		@Column(name="zip_ext")		
		String zipExt;		
		@OneToOne
		@JoinColumn(name="business_id", updatable=false, referencedColumnName="id")
    Credential credential;
    //
    public Business(){

    }
    public Business(int id,
										String name,
										String corporateAddress,
										String businessNumber,
										String phone,
										String email,
										
										String streetAdress,
										String streetAdress2,
										String city,
										String state,
										String zipCode,
										
										String zipExt,
										Credential credential
										) {
				super();
				this.id = id;
				this.name = name;
				this.corporateAddress = corporateAddress;
				this.businessNumber = businessNumber;
				this.phone = phone;
				this.email = email;
				this.streetAdress = streetAdress;
				this.streetAdress2 = streetAdress2;
				this.city = city;
				this.state = state;
				this.zipCode = zipCode;
				this.zipExt = zipExt;
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
    public void setStreetAddress(String val) {
				if(val != null && !val.isEmpty())
						this.streetAdress = val;
    }
    public void setStreetAddress2(String val) {
				if(val != null && !val.isEmpty())
						this.streetAdress2 = val;
    }		
    public void setCity(String val) {
				if(val != null && !val.isEmpty())
						this.city = val;
    }
		public void setState(String val) {
				if(val != null && !val.isEmpty())
						this.state = val;
    }
		public void setZipCode(String val) {
				if(val != null && !val.isEmpty())
						this.zipCode = val;
    }
		public void setZipExt(String val) {
				if(val != null && !val.isEmpty())
						this.zipExt = val;
    }		

    public String getPhone() {
				return phone;
    }
    public String getEmail() {
				return email;
    }		
    public String getStreetAddress() {
				return streetAdress;
    }
    public String getStreetAddress2() {
				return streetAdress2;
    }		
    public String getCity() {
				return city;
    }
    public String getState() {
				return state;
    }
    public String getZipCode() {
				return zipCode;
    }
    public String getZipExt() {
				return zipExt;
    }
		public Credential getCredential(){
				return credential;
		}
		public void setCredential(Credential val){
				credential = val;
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
		boolean hasCorporateInfo(){
				return !getCorporateInfo().isEmpty();
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
				String ret = "Business: ["+getInfo()+"]";
				return ret;
    } 		

		
}
