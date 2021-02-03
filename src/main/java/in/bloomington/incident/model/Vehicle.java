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
public class Vehicle extends TopModel implements java.io.Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id")
    private Incident Incident;
		
    @OneToOne
		@JoinColumn(name="car_damage_type_id")
    private CarDamageType carDamageType;
    private String make;
    private String model;
		
    private Integer year;
    private String color;
		@Column(name="plate_number")
    private String plateNumber;
    private String state;
		@Column(name="plate_year")
    private Integer plateYear;
    private String owner;
    private String description;
    private Double value;

    @Transient
    double balance = 0; // needed when we add multiple items
    @Transient
    double oldValue = 0;
    @Transient
    double maxTotalValue = 0;
    
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
    public void setMaxTotalValue(Double val) {
				if(val != null)
						this.maxTotalValue = val;
    }
    @Transient
    public boolean verifyMaxTotal(){
				boolean ret = true;
				if(maxTotalValue == 0) return true; // case no limit
				if(value != null){
						double total = balance+value-oldValue;
						if(total > maxTotalValue){
								addError(total +" total value exceeds max total of  "+maxTotalValue);
								ret = false;
						}
				}
				return ret;
    }
    @Transient
    public boolean verify(){
				boolean ret = true;
				String str = getCarInfo();
				if(str.isEmpty()){
						addError("No identification of the vehicle is provided");
						ret = false;
				}
				if(!verifyMaxTotal()){
						ret = false;
				}
				return ret;
    }    
    @Transient
    public String getMakeInfo(){
				String ret = "";
				if(make != null && !make.isEmpty()){
						ret = "Make: "+make;
				}
				if(model != null && !model.isEmpty()){
						if(!ret.isEmpty()) ret += ", ";
						ret += "Model: "+model;
				}
				if(year != null){
						if(!ret.isEmpty()) ret += ", ";
						ret += "Year: "+year;
				}
				if(color != null && !color.isEmpty()){
						if(!ret.isEmpty()) ret += ", ";
						ret += "Color: "+color;
				}
				return ret;				
    }
		@Transient
		public boolean hasMakeInfo(){
				String ret = getMakeInfo();
				return !ret.isEmpty();
		}
    @Transient
    public String getPlateInfo(){
				String ret = "";
				if(state != null){
						ret = "State: "+state;
				}
				if(plateYear != null){
						if(!ret.isEmpty()) ret += ", ";
						ret += "Plate Year: "+plateYear;
				}				
				if(plateNumber != null && !plateNumber.isEmpty()){
						if(!ret.isEmpty()) ret += ", ";
						ret += "Plate #: "+plateNumber;
				}
				return ret;				
    }
		@Transient
		public boolean hasPlateInfo(){
				String ret = getPlateInfo();
				return !ret.isEmpty();
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
		@Transient
		public boolean hasOwner(){
				return owner != null && !owner.isEmpty();
		}		

    public String getDescription() {
				return description;
    }
		@Transient
		public boolean hasDescription(){
				return description != null && !description.isEmpty();
		}
    
    public Double getValue() {
				return value;
    }
		@Transient
		public boolean hasValue(){
				return value != null && value > 0;
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
						if(!ret.isEmpty()) ret += ", ";
						ret += "Plate: "+str;
				}
				if(color != null && !color.isEmpty()){
						if(!ret.isEmpty()) ret += ", ";
						ret += "Color: "+color;
				}				
				if(owner != null && !owner.isEmpty()){
						if(!ret.isEmpty()) ret += ", ";
						ret += "Owner: "+owner;
				}
				if(value != null && value > 0){
						if(!ret.isEmpty()) ret += ", ";
						ret += "Value: $"+value;
				}
				if(description != null && !description.isEmpty()){
						if(!ret.isEmpty()) ret += ", ";
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
