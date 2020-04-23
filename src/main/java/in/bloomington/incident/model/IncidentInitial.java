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
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "incident_initials")
public class IncidentInitial implements java.io.Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    // @JoinTable(name = "incident_types", joinColumns = @JoinColumn(name = "incident_type_id"), inverseJoinColumns = @JoinColumn(name = "id"))		
    private IncidentType incidentType;

    private Date received;

    private String email;
		
    public IncidentInitial(){

    }
    public IncidentInitial(int id,
			   IncidentType incidentType,
			   Date received,
			   String email
			   ) {
	super();
	this.id = id;
	this.incidentType = incidentType;
	this.received = received;
	this.email = email;
    }



    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }


    public Date getReceived() {
	return received;
    }

    public void setReceived(Date received) {
	this.received = received;
    }


    public IncidentType getIncidentType() {
	return incidentType;
    }

    public void setIncidentType(IncidentType type) {
	this.incidentType = type;
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }
    @Transient
    public void setReceivedNow(){
	received = new Date();
    }
    @Override
    public boolean equals(Object obj) { 
          
	if(this == obj) 
	    return true; 
				
        if(obj == null || obj.getClass()!= this.getClass()) 
            return false; 
				
        IncidentInitial one = (IncidentInitial) obj; 
        return one.getId() == this.getId();
    }
    @Override
    public int hashCode(){ 
	int ret = 37;
        return ret += this.id; 
    }
    @Override
    public String toString() {
	return "Incident initial " + id;
    } 
						
}
