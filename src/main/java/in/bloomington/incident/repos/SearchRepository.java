package in.bloomington.incident.repos;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */
import java.io.*;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

import in.bloomington.incident.model.Search;
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.User;

public interface SearchRepository{

    public List<Incident> find(Search search);    
		public User findUser(String username) throws IOException;
		
}
