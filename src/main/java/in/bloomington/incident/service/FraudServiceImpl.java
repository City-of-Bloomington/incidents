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
import in.bloomington.incident.repos.FraudRepository;
import in.bloomington.incident.model.Fraud;

@Service
public class FraudServiceImpl implements FraudService {

		@Autowired
		FraudRepository repository;

		@Override
		public void save(Fraud val){
				repository.save(val);
		}
		@Override
		public Fraud findById(int id){
				Fraud fraud = repository.findById(id)
						.orElseThrow(() -> new IllegalArgumentException("Invalid fraud Id:" + id));
				return fraud;
		}				
		@Override
		public void update(Fraud fraud){
				repository.save(fraud);
		}
		@Override
		public void delete(int id){
				repository.deleteById(id);
		}
		@Override
		public List<Fraud> getAll(){
				List<Fraud> all = null;
				Iterable<Fraud> it = repository.findAll();
				all = new ArrayList<>();
				for(Fraud one:it){
						all.add(one);
				}
				return all;
		}

}
