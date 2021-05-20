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
import in.bloomington.incident.repos.BusinessRepository;
import in.bloomington.incident.model.Business;

@Service
public class BusinessServiceImpl implements BusinessService {

		@Autowired
		BusinessRepository repository;

		@Override
		public void save(Business type){
				repository.save(type);
		}
		@Override
		public Business findById(int id){
				Business type = repository.findById(id)
						.orElseThrow(() -> new IllegalArgumentException("Invalid business Id:" + id));
				return type;
		}				
		@Override
		public void update(Business type){
				repository.save(type);
		}
		@Override
		public void delete(int id){
				repository.deleteById(id);
		}
		@Override
		public List<Business> getAll(){
				List<Business> all = null;
				Iterable<Business> it = repository.findAll();
				all = new ArrayList<>();
				for(Business one:it){
						all.add(one);
				}
				return all;
		}

}
