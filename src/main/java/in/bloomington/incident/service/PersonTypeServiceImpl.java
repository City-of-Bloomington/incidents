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
import in.bloomington.incident.repos.PersonTypeRepository;
import in.bloomington.incident.model.PersonType;

@Service
public class PersonTypeServiceImpl implements PersonTypeService {

		@Autowired
		PersonTypeRepository repository;

		@Override
		public void save(PersonType type){
				repository.save(type);
		}
		@Override
		public PersonType findById(int id){
				PersonType type = repository.findById(id)
						.orElseThrow(() -> new IllegalArgumentException("Invalid person type Id:" + id));
				return type;
		}				
		@Override
		public void update(PersonType type){
				repository.save(type);
		}
		@Override
		public void delete(int id){
				repository.deleteById(id);
		}
		@Override
		public List<PersonType> getAll(){
				List<PersonType> all = null;
				Iterable<PersonType> it = repository.findAll();
				all = new ArrayList<>();
				for(PersonType one:it){
						all.add(one);
				}
				return all;
		}

}
