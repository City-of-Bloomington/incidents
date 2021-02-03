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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "addresses")
public class Address extends TopModel implements java.io.Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull(message = "Role name may not be null")
    private String name;
		private Double latitude;
		private Double longitude;
		private String city;
		private String state;
		private String zipcode;
		private String jurisdiction;
		@Column(name="address_id")
		private Integer addressId;
		@Column(name="subunit_id")
		private Integer subunitId;
		@Column(name="invalid_address")
		private Character invalidAddress;
		//
		// temporary place holder for incident type_id
		//
		@Transient
		private Integer type_id;
    public Address(){

    }
		
    public Address(int id,
									 @NotNull(message = "Address text is required") String name,
									 Double latitude,
									 Double longitude,
									 String city,
									 String state,
									 String zipcode,
									 String jurisdiction,
									 Integer addressId,
									 Integer subunitId,
									 Character invalidAddress
									 ) {
				super();
				this.id = id;
				this.name = name;
				this.latitude = latitude;
				this.longitude = longitude;
				this.city = city;
				this.state = state;
				this.zipcode = zipcode;
				this.jurisdiction = jurisdiction;
				this.addressId = addressId;
				this.subunitId = subunitId;
				this.invalidAddress = invalidAddress;
    }



    public int getId() {
				return id;
    }

    public void setId(int id) {
				this.id = id;
    }

    public String getName() {
				return name;
    }

    public void setName(String name) {
				this.name = name;
    }
    public Double getLatitude() {
				return latitude;
    }

    public void setLatitude(Double val) {
				this.latitude = val;
    }
    public Double getLongitude() {
				return longitude;
    }

    public void setLongitude(Double val) {
				this.longitude = val;
    }
		
    public String getCity() {
				return city;
    }

    public void setCity(String val) {
				this.city = val;
    }
    public String getState() {
				return state;
    }

    public void setState(String val) {
				this.state = val;
    }
    public String getZipcode() {
				return zipcode;
    }

    public void setZipcode(String val) {
				this.zipcode = val;
    }
    public String getJurisdiction() {
				return jurisdiction;
    }

    public void setJurisdiction(String val) {
				this.jurisdiction = val;
    }
    public Integer getAddressId() {
				return addressId;
    }

    public void setSubunitId(Integer val) {
				this.subunitId = val;
    }
    public Integer getSubunitId() {
				return subunitId;
    }

    public void setAddressId(Integer val) {
				this.addressId = val;
    }

    public Character getInvalidAddress() {
				return invalidAddress;
    }

    public void setInvalidAddress(Character val) {
				this.invalidAddress = val;
    }
		@Transient
		public void setType_id(Integer val){
				type_id = val;
		}
		@Transient
		public Integer getType_id(){
				return type_id;
		}
		@Transient
		public boolean isValid(){
				return invalidAddress == null;
		}
		@Transient
		public boolean isInvalid(){
				return invalidAddress != null;
		}
		@Transient
		public boolean hasData(){
				return name != null && !name.isEmpty();
		}
		@Transient
		public String getInfo(){
				String ret = name;
				if(city != null && !city.isEmpty()){
						ret +=", "+city;
				}
				if(state != null && !state.isEmpty()){
						ret +=", "+state;
				}
				if(zipcode != null && !zipcode.isEmpty()){
						ret +=" "+zipcode;
				}
				return ret;
		}
    public boolean equals(Object obj) { 
          
				if(this == obj) 
						return true; 
				
        if(obj == null || obj.getClass()!= this.getClass()) 
            return false; 
				
        Address one = (Address) obj; 
        return one.getId() == this.getId();
    }
    @Override
    public int hashCode(){ 
				int ret = 29;
        return ret += this.id; 
    }

    @Override
    public String toString() {
				return name;
    }
		@Transient
		public String getCityStateZip(){
				String ret = "";
				if(city != null && !city.isEmpty()){
						ret += city;
				}
				if(state != null && !state.isEmpty()){
						if(!ret.isEmpty()) ret +=", ";
						ret += state;
				}
				if(zipcode != null && !zipcode.isEmpty()){
						if(!ret.isEmpty()) ret +=", ";
						ret += zipcode;
				}				
				return ret;
		}
    @Transient
    public boolean verifyAddress(String default_city,
																 String default_jurisdiction,
																 String default_state,
																 List<String> default_zip_codes){
				if(name == null || name.trim().isEmpty()){
						addError("Address is required");
						return false;
				}
				else if(city == null || city.isEmpty()){
						addError("City is required");
						return false;
				}
				else if(!city.equals(default_city)){
						addError("Invalid city "+city+", verify your address or contact the related Police Department" );
						return false;
				}
				else if(state == null || state.isEmpty()){
						addError("State is required");
						return false;
				}
				else if(!state.equals(default_state)){
						addError("Invalid state "+state);
						return false;
				}
				else if(jurisdiction != null && !jurisdiction.equals(default_jurisdiction)){
						addError("Your address is not in "+default_jurisdiction+" jurisdiction, verify your address or contact the related Police Department ");
						return false;
				}
				else if(zipcode == null || zipcode.equals("")){
						addError("Zip code is required");
						return false;
				}
				else if(zipcode != null && !zipcode.equals("")){
						for(String str:default_zip_codes){
								if(zipcode.equals(str)){
										return true;
								}
						}
						addError("invalid zipcode "+zipcode);
						return false;
				}
				return true;
    }		
		
}
