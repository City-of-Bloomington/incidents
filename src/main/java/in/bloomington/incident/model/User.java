package in.bloomington.incident.model;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import java.util.ArrayList;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.ManyToMany;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.NamedQuery;
import javax.persistence.NamedNativeQuery;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "users")
/**
@NamedQuery(name = "User.findUserByUsername",
  query = "SELECT u FROM User u WHERE u.username = ?1")
*/
@NamedNativeQuery(name = "User.findUserByUsername", query = "SELECT * FROM users WHERE username = ?", resultClass = User.class)
@NamedNativeQuery(name = "User.findUserByFirstNameOrLastname", query = "SELECT * FROM users WHERE firstname like ? or lastname like ?", resultClass = User.class)	
public class User implements java.io.Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    private String username;
    @NotNull
    private String lastname;		
    private String firstname;
    private Character inactive;
    @ManyToMany
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id",insertable=false, updatable=false), inverseJoinColumns = @JoinColumn(name = "role_id",insertable=false, updatable=false))
    List<Role> roles;
    @Transient
    private List<Action> actions;
		
    public User(){

    }
    public User(int id,
								@NotNull String username,
								String first_name,
								@NotNull String last_name,
								Character inactive,
								List<Role> roles
								) {
				super();
				this.id = id;
				this.username = username;
				this.firstname = first_name;
				this.lastname = last_name;
				this.inactive = inactive;
				this.roles = roles;
    }
    public int getId() {
				return id;
    }
    public void setId(int id) {
				this.id = id;
    }
    public String getUsername() {
				return username;
    }
    public void setUsername(String username) {
				this.username = username;
    }
    public String getFirstname() {
				return firstname;
    }
    public void setFirstname(String first_name) {
				this.firstname = first_name;
    }
    public String getLastname() {
				return lastname;
    }
    public void setLastname(String last_name) {
				this.lastname = last_name;
    }
    public Character getInactive() {
				return inactive;
    }
    public void setInactive(Character inactive) {
				this.inactive = inactive;
    }
    @Transient
    public boolean isActive(){
				return this.inactive == null;
    }
    @Transient
    public void setActive(boolean val){
				if(val)
						this.inactive = null;
				else
						this.inactive = 'y';
    }		
				
    public List<Role> getRoles() {
				return roles;
    }
    public void setRoles(List<Role> roles) {
				this.roles = roles;
    }
    @Transient
    private boolean hasRoles(){
				return roles != null && roles.size() > 0;
    }

    @Transient
    public String getRolesInfo(){
				String ret = "";
				if(hasRoles()){
						for(Role one:roles){
								if(!ret.equals("")) ret += ", ";
								ret += one.getName();
						}
				}
				return ret;
    }
    @Transient
    private boolean hasActions(){
				getActions();
				return actions != null && actions.size() > 0;
    }		
    @Transient
    public List<Action> getActions(){
				if(hasRoles() && actions == null){
						for(Role one:roles){
								if(actions == null)
										actions = new ArrayList<>();
								List<Action> ones = one.getActions();
								if(ones != null)
										actions.addAll(ones);
						}
				}
				return actions;
    }
    @Transient
    public String getActionsInfo(){
				String ret = "";
				if(hasActions()){
						for(Action one:actions){
								if(!ret.equals("")) ret += ", ";
								ret += one.getName();
						}
				}
				return ret;
    }		
    @Transient
    public boolean canApprove(){
				if(isAdmin()) return true;
				if(hasActions()){
						for(Action one:actions){
								if(one.getName().indexOf("approve") > -1){
										return true;
								}
						}
				}
				return false;
    }
    @Transient
    public boolean canProcess(){
				if(isAdmin()) return true;
				if(hasActions()){
						for(Action one:actions){
								if(one.getName().indexOf("process") > -1){
										return true;
								}
						}
				}
				return false;
    }
    @Transient
    public boolean isAdmin(){		
				if(hasRoles()){
						for(Role role:roles){
								if(role.isAdmin()){
										return true;
								}
						}
				}
				return false;
    }
    @Override
    public boolean equals(Object obj) { 
          
				if(this == obj) 
						return true; 
				
        if(obj == null || obj.getClass()!= this.getClass()) 
            return false; 
				
        User one = (User) obj; 
        return one.getId() == this.getId();
    }
    @Override
    public int hashCode(){ 
				int ret = 17;
        return ret += this.id; 
    }
    @Override
    public String toString() {
				return getFullname();
    } 	
		@Transient
		public String getFullname(){
				String ret = "";
				if(firstname != null && !firstname.isEmpty()){
						ret = firstname;
				}
				if(lastname != null && !lastname.isEmpty()){
						if(!ret.isEmpty()) ret += " ";
						ret += lastname;
				}				
				return ret;
		}
}
