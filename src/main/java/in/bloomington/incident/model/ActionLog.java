package in.bloomington.incident.model;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.Date;
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
@Table(name = "action_logs")
public class ActionLog implements java.io.Serializable{

		@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
		private Date date;
		private String comments;
		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "incident_id")
		private Incident incident;

		@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "action_id")				
		private Action action;

		@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")				
		private User user;

		//
		public ActionLog(){

		}
		public ActionLog(int id,
										 Incident incident,
										 Date date,
										 Action action,
										 User user,
										 String comments) {
			super();
			this.id = id;
			this.incident = incident;
			this.date = date;
			this.action = action;
			this.user = user;
			this.comments = comments;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public Incident getIncident() {
			return incident;
		}
		public void setIncident(Incident incident) {
			this.incident = incident;
		}
		public Date getDate() {
			return date;
		}
		public void setDate(Date date) {
			this.date = date;
		}
		public Action getAction() {
			return action;
		}
		public void setAction(Action action) {
			this.action = action;
		}
		public User getUser() {
			return user;
		}
		public void setUser(User user) {
			this.user = user;
		}
		public String getComments() {
			return comments;
		}
		public void setComments(String comments) {
			this.comments = comments;
		}
		@Override
    public boolean equals(Object obj) { 
          
				if(this == obj) 
						return true; 
				
        if(obj == null || obj.getClass()!= this.getClass()) 
            return false; 
				
        ActionLog one = (ActionLog) obj; 
        return one.getId() == this.getId();
		}
		@Override
		public int hashCode(){ 
				int ret = 37;
        return ret += this.id; 
    }
		@Override
		public String toString() {
			return "ActionLog [id=" + id + "]";
		} 	
		
}
