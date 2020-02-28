package in.bloomington.incident.model;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import javax.persistence.Transient;
import java.util.List;
import java.util.ArrayList;


public abstract class TopModel{

		@Transient
		List<String> errors = null;
		//
		public TopModel(){

		}
		void addError(String error){
				if(errors == null)
						errors = new ArrayList<>();
				if(error != null)
						errors.add(error);
		}
		public boolean hasErrors(){
				return errors != null && errors.size() > 0;
		}
		public List<String> getErrors(){
				return errors;
		}
		public String getErrorInfo(){
				String ret = "";
				if(hasErrors()){
						for(String error:errors){
								if(!ret.equals("")) ret +=", ";
								ret += error;
						}
				}
				return ret;
		}
				

}
