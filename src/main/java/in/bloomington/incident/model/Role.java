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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "roles")
public class Role implements java.io.Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull(message = "Role name may not be null")
    private String name;

    @ManyToMany(mappedBy="roles")
		/*
    @JoinTable(name = "user_roles",
							 joinColumns = @JoinColumn(name = "role_id", referencedColumnName="id"),
							 inverseJoinColumns = @JoinColumn(name = "user_id"))
		*/
    List<User> users;
		
    @ManyToMany
    @JoinTable(name = "role_actions", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "action_id"))
    List<Action> actions;		
    public Role(){

    }
		
    public Role(int id, @NotNull(message = "Role name may not be null") String name, List<User> users, List<Action> actions) {
				super();
				this.id = id;
				this.name = name;
				this.users = users;
				this.actions = actions;
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
    public List<User> getUsers(){
				return users;
    }
    public void setUsers(List<User> ones){
				users = ones;
    }
    public List<Action> getActions(){
				return actions;
    }
    public void setActions(List<Action> actions){
				actions = actions;
    }
    @Transient
    public boolean hasAssignedActions(){
				return actions != null && actions.size() > 0;
    }
    @Transient
    public String getActionsInfo(){
				String ret = "";
				if(hasAssignedActions()){
						for(Action one:actions){
								if(!ret.equals("")) ret += ", ";
								ret += one;
						}
				}
				return ret;
    }
    @Transient
    public boolean isAdmin(){
				return name.indexOf("Admin") > -1;
    }				
    @Override
    public boolean equals(Object obj) { 
          
				if(this == obj) 
						return true; 
				
        if(obj == null || obj.getClass()!= this.getClass()) 
            return false; 
				
        Role one = (Role) obj; 
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
