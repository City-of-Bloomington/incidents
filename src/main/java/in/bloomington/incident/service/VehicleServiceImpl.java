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
import in.bloomington.incident.repos.VehicleRepository;
import in.bloomington.incident.model.Vehicle;

@Service
public class VehicleServiceImpl implements VehicleService {

		@Autowired
		VehicleRepository repository;

		@Override
		public void save(Vehicle val){
				repository.save(val);
		}
		@Override
		public Vehicle findById(int id){
				Vehicle val = repository.findById(id)
						.orElseThrow(() -> new IllegalArgumentException("Invalid vehicle Id:" + id));
				return val;
		}				
		
		@Override
		public void update(Vehicle val){
				repository.save(val);
		}
		@Override
		public void delete(int id){
				repository.deleteById(id);
		}
		@Override
		public List<Vehicle> getAll(){
				List<Vehicle> all = null;
				Iterable<Vehicle> it = repository.findAll();
				all = new ArrayList<>();
				for(Vehicle one:it){
						all.add(one);
				}
				return all;
		}

}
