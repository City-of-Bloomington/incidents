package in.bloomington.incident.service;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import in.bloomington.incident.repos.PersonRepository;
import in.bloomington.incident.model.Person;

@Service
public class PersonServiceImpl implements PersonService {

		@Autowired
		PersonRepository repository;

		@Override
		public void save(Person person){
				repository.save(person);
		}
		@Override
		public Person findById(int id){
				Person person = repository.findById(id)
						.orElseThrow(() -> new IllegalArgumentException("Invalid person Id:" + id));
				return person;
		}				
		@Override
		public void update(Person person){
				repository.save(person);
		}
		@Override
		public void delete(int id){
				repository.deleteById(id);
		}
		@Override
		public List<Person> getAll(){
				List<Person> all = null;
				Iterable<Person> it = repository.findAll();
				all = new ArrayList<>();
				for(Person one:it){
						all.add(one);
				}
				return all;
		}
		@Override
		public List<Person> findDistinctPeopleByLastnameOrFirstname(String lastname, String firstname){
				return repository.findDistinctPeopleByLastnameOrFirstname(lastname, firstname);
		}
		public List<Person> findPeopleDistinctByLastnameOrFirstname(String lastname, String firstname){
				return repository.findPeopleDistinctByLastnameOrFirstname(lastname, firstname);
		}
		// List<Person> findByEmailAddressAndLastname(EmailAddress emailAddress, String lastname);
		// Enabling ignoring case for an individual property
		public List<Person> findByLastnameIgnoreCase(String lastname){
				return repository.findByLastnameIgnoreCase(lastname);
		}
		// Enabling ignoring case for all suitable properties
		public List<Person> findByLastnameAndFirstnameAllIgnoreCase(String lastname, String firstname){
				return repository.findByLastnameAndFirstnameAllIgnoreCase(lastname, firstname);
		}

		// Enabling static ORDER BY for a query
		public List<Person> findByLastnameOrderByFirstnameAsc(String lastname){
				return repository.findByLastnameOrderByFirstnameAsc(lastname);
		}
		public List<Person> findByLastnameOrderByFirstnameDesc(String lastname){
				return repository.findByLastnameOrderByFirstnameDesc(lastname);
		}
		

}
