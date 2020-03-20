package in.bloomington.incident.model;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.ConstructorResult;
import javax.persistence.ColumnResult;
import javax.validation.constraints.NotNull;

/**
  // the following moved to Action.class
  // so that hibernate can see it
@SqlResultSetMapping(name="ResultMapping"

*/
public class IncidentStats implements java.io.Serializable{

    public int total=0;
    private String name="";
    public IncidentStats(){

    }

    public IncidentStats(String name, Integer total){
	this.name = (name == null)? "":name;	
	this.total = (total == null)? 0:total;
    }

    public int getTotal() {
	return total;
    }

    public void setTotal(Integer val) {
	if(val != null)
	    this.total = val;
    }

    public String getName() {
	return name;
    }

    public void setName(String val) {
	if(val != null)
	    this.name = name;
    }

    public boolean equals(Object obj) { 
          
	if(this == obj) 
	    return true; 
				
        if(obj == null || obj.getClass()!= this.getClass()) 
            return false; 
				
        IncidentStats one = (IncidentStats) obj; 
        return one.getName() == this.getName();
    }
    @Override
    public int hashCode(){ 
	int ret = 29;
        return ret += this.total; 
    }

    @Override
    public String toString() {
	return name;
    } 	
		
}
