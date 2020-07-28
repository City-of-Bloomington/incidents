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
import in.bloomington.incident.repos.RaceTypeRepository;
import in.bloomington.incident.model.RaceType;

@Service
public class RaceTypeServiceImpl implements RaceTypeService {

		@Autowired
		RaceTypeRepository repository;

		@Override
		public void save(RaceType type){
				repository.save(type);
		}
		@Override
		public RaceType findById(int id){
				RaceType type = repository.findById(id)
						.orElseThrow(() -> new IllegalArgumentException("Invalid race type Id:" + id));
				return type;
		}				
		@Override
		public void update(RaceType type){
				repository.save(type);
		}
		@Override
		public void delete(int id){
				repository.deleteById(id);
		}
		@Override
		public List<RaceType> getAll(){
				List<RaceType> all = null;
				Iterable<RaceType> it = repository.findAll();
				all = new ArrayList<>();
				for(RaceType one:it){
						all.add(one);
				}
				return all;
		}

}
