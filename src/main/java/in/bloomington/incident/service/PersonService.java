package in.bloomington.incident.service;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import in.bloomington.incident.model.Person;

public interface PersonService{
		//
		// from Crud repos
		public abstract void save(Person person);
		public abstract Person findById(int id);
		public abstract void update(Person person);
		public abstract void delete(int id);
		public abstract List<Person> getAll();
		//
		// from custom methods
		public List<Person> findDistinctPeopleByLastnameOrFirstname(String lastname, String firstname);
		public List<Person> findPeopleDistinctByLastnameOrFirstname(String lastname, String firstname);
		// List<Person> findByEmailAddressAndLastname(EmailAddress emailAddress, String lastname);
		// Enabling ignoring case for an individual property
		public List<Person> findByLastnameIgnoreCase(String lastname);
		// Enabling ignoring case for all suitable properties
		public List<Person> findByLastnameAndFirstnameAllIgnoreCase(String lastname, String firstname);

		// Enabling static ORDER BY for a query
		public List<Person> findByLastnameOrderByFirstnameAsc(String lastname);
		public List<Person> findByLastnameOrderByFirstnameDesc(String lastname);
		
}
