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
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "incident_pre_approve") // view
public class IncidentPreApproved implements java.io.Serializable{

    @Id
    private int id;

    @OneToOne
    @JoinColumn(name="id",insertable=false, updatable=false)		
    Incident incident;
		
    public IncidentPreApproved(){

    }

    public IncidentPreApproved(int id, Incident val){
	super();
	this.id = id;
	this.incident = val;
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }
    public Incident getIncident(){
	return incident;
    }
    public void setIncident(Incident val){
	incident = val;
    }
}
