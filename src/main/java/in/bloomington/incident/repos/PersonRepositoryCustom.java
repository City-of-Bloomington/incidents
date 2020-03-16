package in.bloomington.incident.repos;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import java.util.Map.Entry;
import org.springframework.data.repository.Repository;
import in.bloomington.incident.model.Person;


public interface PersonRepositoryCustom extends Repository<Person, Integer> {

		// Enables the distinct flag for the query		
		List<Person> findDistinctPeopleByLastnameOrFirstname(String lastname, String firstname);
		List<Person> findPeopleDistinctByLastnameOrFirstname(String lastname, String firstname);
		//
		// Enabling ignoring case for an individual property
		List<Person> findByLastnameIgnoreCase(String lastname);
		// Enabling ignoring case for all suitable properties
		List<Person> findByLastnameAndFirstnameAllIgnoreCase(String lastname, String firstname);

		// Enabling static ORDER BY for a query
		List<Person> findByLastnameOrderByFirstnameAsc(String lastname);
		List<Person> findByLastnameOrderByFirstnameDesc(String lastname);
		
}
