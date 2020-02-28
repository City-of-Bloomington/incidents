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
import in.bloomington.incident.repos.PropertyRepository;
import in.bloomington.incident.model.Property;

@Service
public class PropertyServiceImpl implements PropertyService {

		@Autowired
		PropertyRepository repository;

		@Override
		public void save(Property type){
				repository.save(type);
		}
		@Override
		public Property findById(int id){
				Property type = repository.findById(id)
						.orElseThrow(() -> new IllegalArgumentException("Invalid property Id:" + id));
				return type;
		}				
		@Override
		public void update(Property type){
				repository.save(type);
		}
		@Override
		public void delete(int id){
				repository.deleteById(id);
		}
		@Override
		public List<Property> getAll(){
				List<Property> all = null;
				Iterable<Property> it = repository.findAll();
				all = new ArrayList<>();
				for(Property one:it){
						all.add(one);
				}
				return all;
		}

}
