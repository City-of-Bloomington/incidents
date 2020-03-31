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
    String serialNum;
    String owner;
    String description;
    //
    @OneToOne		
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

    public void setValue(Double value) {
	if(value != null)
	    this.value = value;
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
	if(val != null)
	    this.oldValue = val;
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

    public String getDescription() {
	return description;
    }

    public void setDescription(String val) {
	if(val != null && !val.isEmpty())
	    this.description = val;
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
	    ret = brand;
	}
	if(model != null && !model.isEmpty()){
	    if(!ret.isEmpty()) ret += " ";
	    ret += model;
	}
	if(serialNum != null && !serialNum.isEmpty()){
	    if(!ret.isEmpty()) ret += " ";
	    ret += serialNum;
	}				
	return ret;
    }
    @Transient
    public String getInfo(){
	String ret = getBrandInfo();
	if(description != null && !description.isEmpty()){
	    if(!ret.isEmpty()){
		ret += " ";
	    }
	    ret += description;
	}
	if(owner != null && !owner.isEmpty()){
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
	if(!verifyMaxTotal()){
	    ret = false;
	}
	return ret;
    }
    @Transient
    public boolean verifyMaxTotal(){
	boolean ret = true;
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
	String ret = "Item ["+getInfo()+"]";
	return ret;
    } 		

		
}
