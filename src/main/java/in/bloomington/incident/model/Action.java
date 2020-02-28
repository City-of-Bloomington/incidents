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
@Table(name = "actions")
public class Action implements java.io.Serializable{

		@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
		@NotNull
		private String name;
		private String description;
		private Integer workflowStep;
		@ManyToMany
		@JoinTable(name = "role_actions")
		List<Role> roles;
		//
		public Action(){

		}

		public Action(int id, @NotNull String name, String description, Integer workflow_step, List<Role> roles) {
			super();
			this.id = id;
			this.name = name;
			this.description = description;
			this.workflowStep = workflow_step;
			this.roles = roles;
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

		public void setName(String name) {
			this.name = name;
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

		public void setWorkflowStep(Integer workflow_step) {
			this.workflowStep = workflow_step;
		}
		public void setRoles(List<Role> roles) {
				this.roles = roles;
		}
		@Transient
		private boolean hasRoles(){
				return roles != null && roles.size() > 0;
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
			return name;
		} 	
		
}
