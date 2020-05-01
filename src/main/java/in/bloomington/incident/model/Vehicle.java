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
@Table(name = "vehicles")
public class Vehicle implements java.io.Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id")
    private Incident Incident;
		
    @OneToOne
    private CarDamageType carDamageType;
    private String make;
    private String model;
		
    private Integer year;
    private String color;
    private String plateNumber;
    private String state;
    private Integer plateYear;
    private String owner;
    private String description;
    private Double value;
		
    public Vehicle(int id,
		   in.bloomington.incident.model.@NotNull(message = "Incident is required") Incident incident,
		   CarDamageType carDamageType,
		   String make,
		   String model,
		   Integer year,
		   String color,
		   String plateNumber,
		   String state,
		   Integer plateYear,
		   String owner,
		   String description,
		   Double value) {
	super();
	this.id = id;
	Incident = incident;
	this.carDamageType = carDamageType;
	this.make = make;
	this.model = model;
	this.year = year;
	this.color = color;
	this.plateNumber = plateNumber;
	this.state = state;
	this.plateYear = plateYear;
	this.owner = owner;
	this.description = description;
	this.value = value;
    }


		
    public Vehicle(){

    }


    public int getId() {
	return id;
    }


    public void setId(int id) {
	this.id = id;
    }


    public Incident getIncident() {
	return Incident;
    }


    public void setIncident(Incident incident) {
	Incident = incident;
    }


    public CarDamageType getCarDamageType() {
	return carDamageType;
    }


    public void setCarDamageType(CarDamageType val) {
	if(val != null)
	    this.carDamageType = val;
    }


    public String getMake() {
	return make;
    }


    public void setMake(String val) {
	if(val != null && !val.isEmpty())
	    this.make = val;
    }


    public String getModel() {
	return model;
    }


    public void setModel(String val) {
	if(val != null && !val.isEmpty())
	    this.model = val;
    }


    public Integer getYear() {
	return year;
    }


    public void setYear(Integer val) {
	if(val != null)
	    this.year = val;
    }
    @Transient
    public String getMakeInfo(){
	String ret = "";
	if(make != null && !make.isEmpty()){
	    ret = make;
	}
	if(model != null && !model.isEmpty()){
	    if(!ret.isEmpty()) ret += " ";
	    ret += model;
	}
	if(year != null){
	    if(!ret.isEmpty()) ret += " ";
	    ret += year;
	}
	return ret;				

    }
    @Transient
    public String getPlateInfo(){
	String ret = "";
	if(state != null){
	    ret = state;
	}
	if(plateYear != null){
	    if(!ret.isEmpty()) ret += " ";
	    ret += plateYear;
	}				
	if(plateNumber != null && !plateNumber.isEmpty()){
	    if(!ret.isEmpty()) ret += " ";
	    ret += plateNumber;
	}
	return ret;				
    }				


    public String getColor() {
	return color;
    }


    public void setColor(String val) {
	if(val != null && !val.isEmpty())
	    this.color = val;
    }


    public String getPlateNumber() {
	return plateNumber;
    }


    public void setPlateNumber(String val) {
	if(val != null && !val.isEmpty())
	    this.plateNumber = val;
    }


    public String getState() {
	return state;
    }


    public void setState(String val) {
	if(val != null && !val.isEmpty())
	    this.state = val;
    }


    public Integer getPlateYear() {
	return plateYear;
    }


    public void setPlateYear(Integer val) {
	if(val != null)
	    this.plateYear = val;
    }


    public String getOwner() {
	return owner;
    }


    public void setOwner(String val) {
	this.owner = val;
    }


    public String getDescription() {
	return description;
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

    public void setDescription(String val) {
	if(val != null && !val.isEmpty())
	    this.description = val;
    }
    @Transient
    public String getCarInfo(){
	String ret = "";
	String str = getMakeInfo();
	if(!str.isEmpty()){
	    ret = "Make: "+str;
	}
	str = getPlateInfo();
	if(!str.isEmpty()){
	    if(ret.isEmpty()) ret += ", ";
	    ret += "Plate: "+str;
	}
	if(color != null && !color.isEmpty()){
	    if(ret.isEmpty()) ret += ", ";
	    ret += "Color: "+color;
	}				
	if(owner != null && !owner.isEmpty()){
	    if(ret.isEmpty()) ret += ", ";
	    ret += "Owner: "+owner;
	}
				
	if(description != null && !description.isEmpty()){
	    if(ret.isEmpty()) ret += ", ";
	    ret += description;
	}
	return ret;
    }
				

    @Override
    public boolean equals(Object obj) { 
          
	if(this == obj) 
	    return true; 
				
        if(obj == null || obj.getClass()!= this.getClass()) 
            return false; 
				
        Vehicle one = (Vehicle) obj; 
        return one.getId() == this.getId();
    }
		
    @Override
    public int hashCode(){ 
	int ret = 31;
        ret += this.id;
	return ret;
    }


    @Override
    public String toString() {
	return "Vehicle [make=" + make + ", model=" + model + ", year=" + year + ", color=" + color
	    + ", plateNumber=" + plateNumber + "]";
    } 

		
}
