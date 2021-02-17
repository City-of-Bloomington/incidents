package in.bloomington.incident.service;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */
import java.io.*;
import java.util.List;
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.Search;
import in.bloomington.incident.model.User;
public interface SearchService{
    public abstract List<Incident> find(Search val);		
		public abstract User findUser(String username) throws IOException;
}
