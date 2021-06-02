package in.bloomington.incident.model;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.Date;
import java.util.regex.*;
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


@Entity
@Table(name = "credentials")
public class Credential extends TopModel implements java.io.Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;		

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "business_id")		
    private Business business;
		@Column(name="password")
    private String password;
		@Column(name="email")
    private String email;		
		@Column(name="last_update")
    private Date lastUpdate;
		@Transient
		private String newPassword;
    //
    public Credential(){

    }

    public Credential(int id,
											Business business,
											String password,
											String email,
											Date lastUpdate) {
				super();
				this.id = id;
				this.business = business;
				this.password = password;
				this.email = email;
				this.lastUpdate = lastUpdate;
    }

    public int getId() {
				return id;
    }

    public void setId(int id) {
				this.id = id;
    }

    public Business getBusiness() {
				return business;
    }

    public void setBusiness(Business business) {
				this.business = business;
    }

    public String getPassword() {
				return password;
    }
    public String getEmail() {
				return email;
    }
		@Transient
		public boolean checkPassword(String encypted){
				return encypted != null && password.equals(encypted);
		}
		@Transient
		public void setNewPassword(String val){
				if(isValidPassword(val)){
						newPassword = val;
				}
		}
		/**
			 a valid password will have lowercase, upercase, number and
			 special character and at least 14 characters long
		*/
		@Transient
		boolean isValidPassword(String val){
				String regex = "^(?=.*[a-z])(?=."
                       + "*[A-Z])(?=.*\\d)"
                       + "(?=.*[-+_!@#$%^&*., ?]).+$";
 
				if(val == null || val.trim().isEmpty()){
						addError("password should be at least 14 characters");						
						return false;
				}
				if(val.length() < 14){
						addError("password is less than 14 characters");
						return false;
				}
				// Compile the ReGex
        Pattern p = Pattern.compile(regex);				
				Matcher m = p.matcher(val);
				if(!m.matches()){
						addError("invalid password");
						return false;
				}
				return true;
				
		}
		@Transient
		public boolean isPasswordSet(){
				if(password == null || password.isEmpty()){
						return false;
				}
				return true;
		}
    public void setPassword(String val) {
				if(val != null && !val.isEmpty())				
						this.password = val;
    }
    public void setEmail(String val) {
				if(val != null && !val.isEmpty())				
						this.email = val;
    }
    public Date getLastUpdate() {
				return lastUpdate;
    }

    public void setLastUpdate(Date val) {
				if(val != null)
						this.lastUpdate = val;
    }

    @Override
    public boolean equals(Object obj) { 
          
				if(this == obj) 
						return true; 
				
        if(obj == null || obj.getClass()!= this.getClass()) 
            return false; 
				
        Credential one = (Credential) obj; 
        return one.getEmail().equals(this.getEmail());
    }
    @Override
    public int hashCode(){ 
				int ret = 29;
        return ret += this.id; 
    }

    @Override
    public String toString() {
				return this.email;
    } 			
}
