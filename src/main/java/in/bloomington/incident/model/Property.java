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
@Table(name = "properties")
public class Property extends TopModel implements java.io.Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    String brand;
    String model;
    Double value;
		@Column(name="serial_num")
    String serialNum;
    String owner;
    String description;
    //
    @OneToOne
		@JoinColumn(name = "damage_type_id")		
    DamageType damageType;
    //
		
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id")
    Incident incident;
    //
    // we need these to check if the amount added exceeds the limit
    //
    @Transient
    double balance = 0; // needed when we add multiple items
    @Transient
    double oldValue = 0;
    @Transient
    double maxTotalValue = 0;
		
    public Property(){

    }
    public Property(int id, String brand, String model, Double value, String serialNum, String owner,
										String description,
										Incident incident,
										DamageType damageType) {
				super();
				this.id = id;
				this.brand = brand;
				this.model = model;
				this.setValue(value);
				if(value !=null){
						this.setOldValue(value);
				}
				this.serialNum = serialNum;
				this.owner = owner;
				this.description = description;
				this.incident = incident;
				this.damageType = damageType;
    }
    public DamageType getDamageType(){
				return damageType;
    }
    public void setDamageType(DamageType val){
				if(val != null){
						damageType = val;
				}
    }
		@Transient
		public boolean hasDamageTypeInfo(){
				return damageType != null;
		}
		
    public int getId() {
				return id;
    }

    public void setId(int id) {
				this.id = id;
    }

    public String getBrand() {
				return brand;
    }

    public void setBrand(String val) {
				if(val != null && !val.isEmpty())
						this.brand = val;
    }

    public String getModel() {
				return model;
    }

    public void setModel(String val) {
				if(val != null && !val.isEmpty())
						this.model = val;
    }

    public Double getValue() {
				return value;
    }
    @Transient
    public String getValueFr() {
				String str = "";
				if(value != null){
						try{
								str = Helper.curFr.format(value);
						}catch(Exception ex){
								System.err.println(value+" "+ex);
						}
				}
				return str;
    }		
    @Transient
		public void setValueFr(String str){
				if(str != null){
						String str2 = Helper.cleanNumber(str);
						value = Double.parseDouble(str2);
				}
		}
    public void setValue(Double str) {
				if(str != null)
						value = str;

    }
		
    public double getBalance() {
				return balance;
    }

    public void setBalance(Double val) {
				if(val != null)
						this.balance = val;
    }
    public double getOldValue() {
				return oldValue;
    }

    public void setOldValue(Double val) {
				if(val != null){
						this.oldValue = val;
				}
    }
    public double getMaxTotalValue() {
				return maxTotalValue;
    }

    public void setMaxTotalValue(double val) {
				this.maxTotalValue = val;
    }				

    public String getSerialNum() {
				return serialNum;
    }

    public void setSerialNum(String val) {
				if(val != null && !val.isEmpty())
						this.serialNum = val;
    }

    public String getOwner() {
				return owner;
    }

    public void setOwner(String val) {
				if(val != null && !val.isEmpty())
						this.owner = val;
    }

		@Transient
		public boolean hasOwner(){
				return owner != null && !owner.isEmpty();
		}
    public String getDescription() {
				return description;
    }

    public void setDescription(String val) {
				if(val != null && !val.isEmpty())
						this.description = val;
    }
		@Transient
		public boolean hasDescription(){
				return description != null && !description.isEmpty();
		}
    public Incident getIncident() {
				return incident;
    }

    public void setIncident(Incident incident) {
				this.incident = incident;
    }
    @Transient
    public String getBrandInfo(){
				String ret = "";
				if(brand != null && !brand.isEmpty()){
						ret = "Brand: "+brand;
				}
				if(model != null && !model.isEmpty()){
						if(!ret.isEmpty()) ret += ", ";
						ret += "Model: "+model;
				}
				if(serialNum != null && !serialNum.isEmpty()){
						if(!ret.isEmpty()) ret += ", ";
						ret += "Serial #: "+serialNum;
				}				
				return ret;
    }
		@Transient
		public boolean hasBrandInfo(){
				String ret = getBrandInfo();
				return !ret.isEmpty();
		}
    @Transient
    public String getInfo(){
				String ret = getBrandInfo();
				if(description != null && !description.isEmpty()){
						if(!ret.isEmpty()){
								ret += ", ";
						}
						ret += description;
				}
				if(hasOwner()){
						if(!ret.isEmpty()){
								ret += ", ";
						}
						ret += " Owner: "+owner;
				}				
				return ret;
    }
    @Transient
    public boolean verify(){
				boolean ret = true;
				String str = getInfo();
				if(str.isEmpty()){
						addError("No identification of the item is provided");
						ret = false;
				}
				if(!hasValidValue()){
						addError("No value of the item is provided");
						ret = false;
				}
				if(!verifyMaxTotal()){
						ret = false;
				}
				return ret;
    }
    @Transient
    public boolean hasValidValue(){
				return value != null && value > 0;
    }
    @Transient
    public boolean verifyMaxTotal(){
				boolean ret = true;
				//
				// if we need to ignore total allowed value we set
				// maxTotalValue = 0 in application.properties
				//
				if(maxTotalValue == 0) return ret;
				if(value != null){
						double total = balance+value-oldValue;
						if(total > maxTotalValue){
								addError(total +" total value exceeds max total of  "+maxTotalValue);
								ret = false;
						}
				}
				return ret;
    }
    @Override
    public boolean equals(Object obj) { 
          
				if(this == obj) 
						return true; 
				
        if(obj == null || obj.getClass()!= this.getClass()) 
            return false; 
				
        Property one = (Property) obj; 
        return one.getId() == this.getId();
    }
    @Override
    public int hashCode(){ 
				int ret = 29;
        return ret += this.id; 
    }
    @Override
    public String toString() {
				String ret = "Property: ["+getInfo()+"]";
				return ret;
    } 		

		
}
