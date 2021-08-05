package in.bloomington.incident.model;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.Date;
import java.util.Calendar;
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
import in.bloomington.incident.utils.Helper;

@Entity
@Table(name = "action_logs")
public class ActionLog implements java.io.Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Date date;
    private String comments;
		private Character cancelled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id")
    private Incident incident;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "action_id")				
    private Action action;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
		
		@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cancelled_by")						
		private User cancelledByUser;		


    // temporary place holder for adding new action logs
    @Transient
    String caseNumber;

    //
    public ActionLog(){

    }
    public ActionLog(int id,
										 Incident incident,
										 Date date,
										 Action action,
										 User user,
										 String comments,
										 Character cancelled,
										 User cancelledByUser) {
				super();
				this.id = id;
				this.incident = incident;
				this.date = date;
				this.action = action;
				this.user = user;
				this.comments = comments;
				this.cancelled = cancelled;
				this.cancelledByUser = cancelledByUser;
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
    public String getDateStr(){
				String ret = "";
				if(date != null){
						try{
								ret = Helper.dft.format(date);
						}catch(Exception ex){

						}	    
				}
				return ret;
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
    public User getCancelledByUser() {
				return cancelledByUser;
    }
    public void setCancelledByUser(User val) {
				this.cancelledByUser = val;
    }		
		@Transient
		public boolean hasUserInfo(){
				return user != null;
		}
		public boolean getCancelled(){
				return cancelled != null;
		}
		public void setCancelled(boolean val){
				if(val){
						cancelled = 'y';
				}
		}
    public String getComments() {
				return comments;
    }
    public void setComments(String comments) {
				this.comments = comments;
    }
		@Transient
		public boolean hasCommentsInfo(){
				return comments != null && !comments.isEmpty();
		}
    @Transient
    public void setDateNow(){
				date = new Date();
    }
		@Transient
		public boolean canBeCancelled(){
			 return (id > 0 &&
							 cancelled == null &&
							 action != null &&
							 (action.isApproved() || action.isRejected()) &&
							 isDateLessThanBy(7));
		}
		@Transient
		public String getInfo(){
				String ret = "";
				if(id > 0){
						if(action != null){
								ret += action;
						}
						if(user != null){
								ret += " by "+user.getFullname();
						}
						if(date != null){
								ret += ", "+getDateStr();
						}
						if(getCancelled()){
								if(cancelledByUser != null){
										ret += " (Cancelled by "+cancelledByUser.getFullname()+")";
								}
								else{
										ret += " (Cancelled)";
								}
						}
				}
				return ret;
		}
		@Transient
		private boolean isDateLessThanBy(int days){
				// creating a Calendar object
				if(date != null && days >= 0){
						try{
								Calendar cal = Calendar.getInstance();
								cal.add(Calendar.DATE, -days);
								Date old_date = cal.getTime();
								int ret = old_date.compareTo(date); // it should be negative
								System.err.println(" date compare "+ret);
								return ret <= 0;
						}
						catch(Exception ex){
								System.err.println(ex);
						}
				}
				return false;
		}
		
    @Transient
    public void setCaseNumber(String val){    
				if(val != null)
						caseNumber = val;
    }
    @Transient
    public String getCaseNumber(){    
				return caseNumber;
    }
    @Transient
    public boolean hasCaseNumber(){
				return caseNumber != null && !caseNumber.isEmpty();
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
				return "Action Log [id=" + id + "]";
    } 	
		
}
