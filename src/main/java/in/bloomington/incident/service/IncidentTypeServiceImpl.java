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
import in.bloomington.incident.repos.IncidentTypeRepository;
import in.bloomington.incident.model.IncidentType;

@Service
public class IncidentTypeServiceImpl implements IncidentTypeService {

		@Autowired
		IncidentTypeRepository repository;

		@Override
		public void save(IncidentType type){
				repository.save(type);
		}
		@Override
		public IncidentType findById(int id){
				IncidentType type = repository.findById(id)
						.orElseThrow(() -> new IllegalArgumentException("Invalid incident type Id:" + id));
				return type;
		}				
		
		@Override
		public void update(IncidentType type){
				repository.save(type);
		}
		@Override
		public void delete(int id){
				repository.deleteById(id);
		}
		@Override
		public List<IncidentType> getAll(){
				List<IncidentType> all = null;
				Iterable<IncidentType> it = repository.findAll();
				all = new ArrayList<>();
				for(IncidentType one:it){
						all.add(one);
				}
				return all;
		}

}
