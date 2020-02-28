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
import in.bloomington.incident.repos.IncidentInitialRepository;
import in.bloomington.incident.model.IncidentInitial;

@Service
public class IncidentInitialServiceImpl implements IncidentInitialService {

		@Autowired
		IncidentInitialRepository repository;

		@Override
		public void save(IncidentInitial val){
				repository.save(val);
		}
		@Override
		public IncidentInitial findById(int id){
				IncidentInitial val = repository.findById(id)
						.orElseThrow(() -> new IllegalArgumentException("Invalid incident Id:" + id));
				return val;
		}				
		
		@Override
		public void update(IncidentInitial val){
				repository.save(val);
		}
		@Override
		public void delete(int id){
				repository.deleteById(id);
		}
		@Override
		public List<IncidentInitial> getAll(){
				List<IncidentInitial> all = null;
				Iterable<IncidentInitial> it = repository.findAll();
				all = new ArrayList<>();
				for(IncidentInitial one:it){
						all.add(one);
				}
				return all;
		}

}
