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
import in.bloomington.incident.repos.CarDamageTypeRepository;
import in.bloomington.incident.model.CarDamageType;

@Service
public class CarDamageTypeServiceImpl implements CarDamageTypeService {

		@Autowired
		CarDamageTypeRepository repository;

		@Override
		public void save(CarDamageType type){
				repository.save(type);
		}
		@Override
		public CarDamageType findById(int id){
				CarDamageType type = repository.findById(id)
						.orElseThrow(() -> new IllegalArgumentException("Invalid vehicle damage type Id:" + id));
				return type;
		}		
		
		@Override
		public void update(CarDamageType type){
				repository.save(type);
		}
		@Override
		public void delete(int id){
				repository.deleteById(id);
		}
		@Override
		public List<CarDamageType> getAll(){
				List<CarDamageType> all = null;
				Iterable<CarDamageType> it = repository.findAll();
				all = new ArrayList<>();
				for(CarDamageType one:it){
						all.add(one);
				}
				return all;
		}

}
