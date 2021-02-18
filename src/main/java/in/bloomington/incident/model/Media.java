package in.bloomington.incident.model;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */


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
@Table(name = "media")
public class Media implements java.io.Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;		

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "incident_id")		
    private Incident incident;
    @NotNull
		@Column(name="file_name")
    private String fileName;
		@Column(name="old_file_name")
    private String oldFileName;
    private Integer year;		
		@Column(name="mime_type")
    private String mimeType;		
    private String notes;

    //
    public Media(){

    }

    public Media(int id, Incident incident, @NotNull String file_name, String old_file_name, Integer year,
								 String mime_type, String notes) {
				super();
				this.id = id;
				this.incident = incident;
				this.fileName = file_name;
				this.oldFileName = old_file_name;
				this.year = year;
				this.mimeType = mimeType;
				this.notes = notes;
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

    public String getFileName() {
				return fileName;
    }

    public void setFileName(String val) {
				if(val != null && !val.isEmpty())				
						this.fileName = val;
    }

    public String getOldFileName() {
				return oldFileName;
    }

    public void setOldFileName(String val) {
				if(val != null && !val.isEmpty())
						this.oldFileName = val;
    }

    public Integer getYear() {
				return year;
    }

    public void setYear(Integer year) {
				this.year = year;
    }

    public String getMimeType() {
				return mimeType;
    }

    public void setMimeType(String mime_type) {
				this.mimeType = mime_type;
    }

    public String getNotes() {
				return notes;
    }

    public void setNotes(String notes) {
				this.notes = notes;
    }
		@Transient
		public boolean hasNotes(){
				return notes != null && !notes.isEmpty();
		}
    @Transient
    public String getInfo(){
				String ret = "";
				if(fileName != null){
						ret = "name:"+fileName;
				}
				if(year != null){
						if(!ret.isEmpty()) ret += " ";
						ret += "year:"+year;
				}
				if(mimeType != null){
						if(!ret.isEmpty()) ret += " ";
						ret += "type:"+mimeType;
				}
				return ret;
    }
    @Override
    public boolean equals(Object obj) { 
          
				if(this == obj) 
						return true; 
				
        if(obj == null || obj.getClass()!= this.getClass()) 
            return false; 
				
        Media one = (Media) obj; 
        return one.getId() == this.getId();
    }
    @Override
    public int hashCode(){ 
				int ret = 29;
        return ret += this.id; 
    }

    @Override
    public String toString() {
				return "Media [" + id + ", " + oldFileName + "]";
    } 			
}
