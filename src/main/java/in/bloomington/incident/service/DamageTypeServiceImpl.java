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
import in.bloomington.incident.repos.DamageTypeRepository;
import in.bloomington.incident.model.DamageType;

@Service
public class DamageTypeServiceImpl implements DamageTypeService {

		@Autowired
		DamageTypeRepository repository;

		@Override
		public void save(DamageType type){
				repository.save(type);
		}
		@Override
		public DamageType findById(int id){
				DamageType type = repository.findById(id)
						.orElseThrow(() -> new IllegalArgumentException("Invalid damage type Id:" + id));
				return type;
		}		
		
		@Override
		public void update(DamageType type){
				repository.save(type);
		}
		@Override
		public void delete(int id){
				repository.deleteById(id);
		}
		@Override
		public List<DamageType> getAll(){
				List<DamageType> all = null;
				Iterable<DamageType> it = repository.findAll();
				all = new ArrayList<>();
				for(DamageType one:it){
						all.add(one);
				}
				return all;
		}

}
