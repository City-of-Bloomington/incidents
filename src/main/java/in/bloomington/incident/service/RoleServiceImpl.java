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
import in.bloomington.incident.repos.RoleRepository;
import in.bloomington.incident.model.Role;

@Service
public class RoleServiceImpl implements RoleService {

		@Autowired
		RoleRepository repository;

		@Override
		public void save(Role role){
				repository.save(role);
		}
		@Override
		public Role findById(int id){
				Role role = repository.findById(id)
						.orElseThrow(() -> new IllegalArgumentException("Invalid role Id:" + id));
				return role;
		}						
		@Override
		public void update(Role role){
				repository.save(role);
		}
		@Override
		public void delete(int id){
				repository.deleteById(id);
		}
		@Override
		public List<Role> getAll(){
				List<Role> all = null;
				Iterable<Role> it = repository.findAll();
				all = new ArrayList<>();
				for(Role one:it){
						all.add(one);
				}
				return all;
		}

}
