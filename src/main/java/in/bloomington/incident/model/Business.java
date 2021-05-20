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
		@Column(name="business_name")
    String businessName;
		@Column(name="corporate_address")
    String corporateAddress;
		@Column(name="business_number")		
		String businessNumber;
		@Column(name="business_phone")		
		String businessPhone;
		@Column(name="contact_name")		
		String contactName;
		@Column(name="contact_title")		
		String contactTitle;
		@Column(name="contact_phone")		
		String contactPhone;
		@Column(name="contact_email")		
		String contactEmail;
    //
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id")
    Incident incident;
    //
    public Business(){

    }
    public Business(int id,
										Incident incident,
										String businessName,
										String corporateAddress,
										String businessNumber,
										String businessPhone,
										String contactName,
										String contactTitle,
										String contactPhone,
										String contactEmail
										) {
				super();
				this.id = id;
				this.incident = incident;
				this.businessName = businessName;
				this.corporateAddress = corporateAddress;
				this.businessNumber = businessNumber;
				this.businessPhone = businessPhone;
				this.contactName = contactName;
				this.contactTitle = contactTitle;
				this.contactPhone = contactPhone;
				this.contactEmail = contactEmail
    }
    public int getId() {
				return id;
    }

    public void setId(int id) {
				this.id = id;
    }

    public String getBusinessName() {
				return businessNumber;
    }

    public void setBusinessName(String val) {
				if(val != null && !val.isEmpty())
						this.businessName = val;
    }

    public String getCorporateAddress() {
				return corporateAddress;
    }

    public void setBusinessNumber(String val) {
				if(val != null && !val.isEmpty())
						this.businessNumber = val;
    }
    public void setBusinessPhone(String val) {
				if(val != null && !val.isEmpty())
						this.businessPhone = val;
    }
    public void setContactName(String val) {
				if(val != null && !val.isEmpty())
						this.contactName = val;
    }
    public void setContactTitle(String val) {
				if(val != null && !val.isEmpty())
						this.contactTitle = val;
    }		
    public void setContactPhone(String val) {
				if(val != null && !val.isEmpty())
						this.contactPhone = val;
    }
		public void setContactEmail(String val) {
				if(val != null && !val.isEmpty())
						this.contactEmail = val;
    }

    public String getBusinessName() {
				return businessName;
    }
    public String getBusinessNumber() {
				return businessNumber;
    }
    public String getContactName() {
				return contactName;
    }
    public String getContactTitle() {
				return contactTitle;
    }		
    public String getContactPhone() {
				return contactPhone;
    }
    public String getContactEmail() {
				return contactEmail;
    }
    public Incident getIncident() {
				return incident;
    }

    public void setIncident(Incident incident) {
				this.incident = incident;
    }
    public String getInfo(){
				String ret = businessName != null?businessName:"";
				if(contactName != null && !contactName.isEmpty()){
						if(!ret.isEmpty()){
								ret += ", ";
						}
						ret += "contact: "+contactName;
				}
				return ret;
    }
    @Transient
    public boolean verify(){
				boolean ret = true;

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
				String ret = "Business: ["+getInfo()+"]";
				return ret;
    } 		

		
}
