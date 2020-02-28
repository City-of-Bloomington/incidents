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
import in.bloomington.incident.repos.IncidentRepository;
import in.bloomington.incident.model.Incident;

@Service
public class IncidentServiceImpl implements IncidentService {

		@Autowired
		IncidentRepository repository;

		@Override
		public void save(Incident val){
				repository.save(val);
		}
		@Override
		public Incident findById(int id){
				Incident val = repository.findById(id)
						.orElseThrow(() -> new IllegalArgumentException("Invalid incident Id:" + id));
				return val;
		}				
		
		@Override
		public void update(Incident val){
				repository.save(val);
		}
		@Override
		public void delete(int id){
				repository.deleteById(id);
		}
		@Override
		public List<Incident> getAll(){
				List<Incident> all = null;
				Iterable<Incident> it = repository.findAll();
				all = new ArrayList<>();
				for(Incident one:it){
						all.add(one);
				}
				return all;
		}

}
