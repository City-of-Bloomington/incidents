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
import in.bloomington.incident.repos.UserRoleRepository;
import in.bloomington.incident.model.UserRole;

@Service
public class UserRoleServiceImpl implements UserRoleService {

		@Autowired
		UserRoleRepository repository;

		@Override
		public void save(UserRole userRole){
				repository.save(userRole);
		}
		@Override
		public void update(UserRole userRole){
				repository.save(userRole);
		}
		@Override
		public void delete(int id){
				repository.deleteById(id);
		}
		@Override
		public List<UserRole> getAll(){
				List<UserRole> all = null;
				Iterable<UserRole> it = repository.findAll();
				all = new ArrayList<>();
				for(UserRole one:it){
						all.add(one);
				}
				return all;
		}

}
