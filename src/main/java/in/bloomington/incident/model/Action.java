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
import javax.persistence.SqlResultSetMapping;
import javax.persistence.ConstructorResult;
import javax.persistence.ColumnResult;

@SqlResultSetMapping(name="ResultMapping", classes={
	@ConstructorResult(
			   targetClass=IncidentStats.class,
			   columns={
			       @ColumnResult(name="name", type=String.class),
			       @ColumnResult(name="total", type=Integer.class),
			   }
			   )
    }
    )

@Entity
@Table(name = "actions")
public class Action implements java.io.Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull		
    @Column(name="name")
    private String name;
    @Column(name="description")
    private String description;
    @Column(name="workflow_step")
    private Integer workflowStep;
    @Column(name="nextstep")
    private Integer nextstep;
    @ManyToMany
    @JoinTable(name = "role_actions")
    List<Role> roles;
		@Transient
		String nameCap = "";
    //
    public Action(){

    }

    public Action(int id, @NotNull String name, String description, Integer workflowStep, Integer nextstep, List<Role> roles) {
				super();
				this.id = id;
				this.name = name;
				this.description = description;
				this.workflowStep = workflowStep;
				this.nextstep = nextstep;
				this.roles = roles;
    }

    public int getId() {
				return id;
    }

    public void setId(int id) {
				this.id = id;
    }

    @Transient
    public Integer getObjId(){
				return new Integer(id);
    }
    public String getName() {
				return name;
    }

    public void setName(String name) {
				this.name = name;
				this.nameCap = toString();
    }
		@Transient
		public String getNameCap(){
				if(nameCap == null || nameCap.isEmpty()){
						this.nameCap = toString();
				}
				return nameCap;
		}

    public String getDescription() {
				return description;
    }

    public void setDescription(String description) {
				this.description = description;
    }

    public Integer getWorkflowStep() {
	return workflowStep;
    }

    public void setNextstep(Integer val) {
	this.nextstep = val;
    }
    public Integer getNextstep(){
	return nextstep;
    }
    @Transient
    public boolean hasNextstep(){
	return nextstep != null && nextstep > 0;
    }
    public void setWorkflowStep(Integer workflowStep) {
	this.workflowStep = workflowStep;
    }    
    public void setRoles(List<Role> roles) {
	this.roles = roles;
    }
    @Transient
    private boolean hasRoles(){
	return roles != null && roles.size() > 0;
    }
    @Transient
    public boolean isApproved(){
	return name != null && name.equals("approved");
    }		
    @Transient
    public boolean isRejected(){
	return name != null && name.equals("rejected");
    }
    @Transient
    public boolean isProcessed(){
	return name != null && name.equals("processed");
    }				
    @Override
    public boolean equals(Object obj) { 
          
	if(this == obj) 
	    return true; 
				
        if(obj == null || obj.getClass()!= this.getClass()) 
            return false; 
				
        Action one = (Action) obj; 
        return one.getId() == this.getId();
    }
    @Override
    public int hashCode(){ 
	int ret = 29;
        return ret += this.id; 
    }

    @Override
    public String toString() {
	if(name == null || name.isEmpty()) {
	    return "";
	}
	return name.substring(0, 1).toUpperCase() + name.substring(1);
    } 	
		
}
