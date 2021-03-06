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


@Entity
@Table(name = "fraud_types")
public class FraudType implements java.io.Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull(message = "Fraud type may not be null")
    private String name;
		
    public FraudType(){

    }
		
    public FraudType(int id, @NotNull(message = "Damage type may not be null") String name) {
				super();
				this.id = id;
				this.name = name;
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
    public void setName(String val) {
				if(val != null)	
						this.name = val;
    }
		@Transient
		public boolean isAccountRelated(){
				return (id == 1 || id == 3 || id == 4);
		}
		@Transient
		public boolean isPersonal(){
				return (id == 2 || id == 5 || id == 6);
		}
		@Transient
		public boolean isUnspecified(){
				return (id == 7);
		}		
    @Override
    public boolean equals(Object obj) { 
          
				if(this == obj) 
						return true; 
				
        if(obj == null || obj.getClass()!= this.getClass()) 
            return false; 
				
        FraudType one = (FraudType) obj; 
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
		
}
