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
import in.bloomington.incident.repos.CredentialRepository;
import in.bloomington.incident.model.Credential;

@Service
public class CredentialServiceImpl implements CredentialService {

		@Autowired
		CredentialRepository repository;

		@Override
		public void save(Credential type){
				repository.save(type);
		}
		@Override
		public Credential findById(int id){
				Credential val = repository.findById(id)
						.orElseThrow(() -> new IllegalArgumentException("Invalid credential Id:" + id));
				return val;
		}				
		@Override
		public void update(Credential val){
				try{
						Credential credit = findById(val.getId());
						// credit.setBusiness(val.getBusiness());
						credit.setPassword(val.getPassword());
						credit.setLastUpdate(val.getLastUpdate());
						credit.setEmail(val.getEmail());
						repository.save(credit);
				}catch(Exception ex){
				}
		}
		@Override
		public void delete(int id){
				repository.deleteById(id);
		}
		@Override
		public List<Credential> getAll(){
				List<Credential> all = null;
				Iterable<Credential> it = repository.findAll();
				all = new ArrayList<>();
				for(Credential one:it){
						all.add(one);
				}
				return all;
		}

}
