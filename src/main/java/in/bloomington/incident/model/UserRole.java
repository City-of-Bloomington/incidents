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

// not needed
// @Entity
// @Table(name = "user_roles")
public class UserRole implements java.io.Serializable{

		//		@Id
		//   @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
		//
		// @NotNull
		// @OneToOne			
		User user;
		// @JoinColumn(name = "user_id")		
		// @NotNull
		// @OneToOne
		// @JoinColumn(name = "role_id")
		Role role;
		
		public UserRole(){

		}
		
		public UserRole(int id, User user, Role role){
				this.id = id;
				this.user = user;
				this.role = role;
		}
		public int getId() {
				return id;
		}


		public void setId(int id) {
				this.id = id;
		}
		
		public User getUser() {
				return user;
		}

		public void setUser(User user) {
				this.user = user;
		}

		public Role getRole() {
				return role;
		}

		public void setRole(Role role) {
				this.role = role;
		}
		@Override
    public boolean equals(Object obj) { 
          
				if(this == obj) 
						return true; 
				
        if(obj == null || obj.getClass()!= this.getClass()) 
            return false; 
				
        UserRole one = (UserRole) obj; 
        return (one.getRole().getId() == this.getRole().getId() &&
								one.getUser().getId() == this.getUser().getId());
		}
		@Override
		public int hashCode(){ 
				int ret = 29;
        return ret += this.getRole().hashCode()+this.getUser().hashCode(); 
    }

		@Override
		public String toString() {
				return this.getUser().toString()+" "+this.getRole().toString();
		} 	
		
}
