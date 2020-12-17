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
@Table(name = "frauds")
public class Fraud extends TopModel implements java.io.Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    String otherType;
    String identityUsed;
    String accountUsed;
    Double amountTaken;
    String details;
    //
    @OneToOne		
    FraudType fraudType;
    //
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id")
    Incident incident;
    //
		
    public Fraud(){

    }
    public Fraud(int id,
								 String otherType,
								 String identityUsed,
								 String accountUsed,
								 Double amountTaken,
								 String details,
								 Incident incident,
								 FraudType fraudType) {
				super();
				this.id = id;
				this.otherType = otherType;
				this.identityUsed = identityUsed;
				this.setAmountTaken(amountTaken);
				this.accountUsed = accountUsed;
				this.details = details;
				this.incident = incident;
				this.fraudType = fraudType;
    }
    public FraudType getFraudType(){
				return fraudType;
    }
    public void setFraudType(FraudType val){
				if(val != null){
						fraudType = val;
				}
    }
		@Transient
		public boolean hasFraudTypeInfo(){
				return fraudType != null;
		}
		
    public int getId() {
				return id;
    }

    public void setId(int id) {
				this.id = id;
    }

    public String getOtherType() {
				return otherType;
    }

    public void setOtherType(String val) {
				if(val != null && !val.isEmpty())
						this.otherType = val;
    }

    public String getIdentityUsed() {
				return identityUsed;
    }

    public void setIdentityUsed(String val) {
				if(val != null && !val.isEmpty())
						this.identityUsed = val;
    }
		
    public String getAccountUsed() {
				return accountUsed;
    }

    public void setAcountUsed(String val) {
				if(val != null && !val.isEmpty())
						this.accountUsed = val;
    }		

    public Double getAmountTaken() {
				return amountTaken;
    }
    @Transient
    public String getAmountTakenFr() {
				String str = "";
				if(amountTaken != null){
						try{
								str = Helper.curFr.format(amountTaken);
						}catch(Exception ex){
								System.err.println(amountTaken+" "+ex);
						}
				}
				return str;
    }		

    public void setAmountTaken(Double value) {
				if(value != null)
						this.amountTaken = value;
    }
		
    public String getDetails() {
				return details;
    }

    public void setDetails(String val) {
				if(val != null && !val.isEmpty())
						this.details = val;
    }

    public Incident getIncident() {
				return incident;
    }

    public void setIncident(Incident incident) {
				this.incident = incident;
    }
		@Transient
		public boolean isPersonal(){
				if(fraudType != null){
						return fraudType.isPersonal();
				}
				return false;
		}
		@Transient
		public boolean isAccountRelated(){
				if(fraudType != null){
						return fraudType.isAccountRelated();
				}
				return false;
		}		
		@Transient
		public boolean isUnspecified(){
				if(fraudType != null){
						return fraudType.isUnspecified();
				}
				return false;
		}				
    @Transient
    public String getInfo(){
				String ret = "";
				if(otherType != null && !otherType.isEmpty()){
						ret += "Fraud/Scam Type: "+otherType;
				}
				if(identityUsed != null && !identityUsed.isEmpty()){
						if(!ret.isEmpty()){
								ret += ", ";
						}
						ret += " Identity used: "+identityUsed;
				}
				if(accountUsed != null && !accountUsed.isEmpty()){
						if(!ret.isEmpty()){
								ret += ", ";
						}
						ret += " Account used: "+accountUsed;
				}
				if(details != null && !details.isEmpty()){
						if(!ret.isEmpty()){
								ret += ", ";
						}
						ret += " Other Details: "+details;
						
				}
				return ret;
    }
    @Transient
    public boolean hasValidAmount(){
				return amountTaken != null && amountTaken > 0;
    }
    @Override
    public boolean equals(Object obj) { 
          
				if(this == obj) 
						return true; 
				
        if(obj == null || obj.getClass()!= this.getClass()) 
            return false; 
				
        Fraud one = (Fraud) obj; 
        return one.getId() == this.getId();
    }
    @Override
    public int hashCode(){ 
				int ret = 29;
        return ret += this.id; 
    }
    @Override
    public String toString() {
				String ret = getInfo();
				if(hasValidAmount()){
						ret += ", Amount Taken: "+getAmountTakenFr();
				}
				return ret;
    } 		

		
}
