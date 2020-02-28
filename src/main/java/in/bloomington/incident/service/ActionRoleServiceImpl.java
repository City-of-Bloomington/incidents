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
import in.bloomington.incident.repos.ActionRoleRepository;
import in.bloomington.incident.model.ActionRole;

@Service
public class ActionRoleServiceImpl implements ActionRoleService {

		@Autowired
		ActionRoleRepository repository;

		@Override
		public void save(ActionRole actionRole){
				repository.save(actionRole);
		}
		@Override
		public void update(ActionRole actionRole){
				repository.save(actionRole);
		}
		@Override
		public void delete(int id){
				repository.deleteById(id);
		}
		@Override
		public List<ActionRole> getAll(){
				List<ActionRole> all = null;
				Iterable<ActionRole> it = repository.findAll();
				all = new ArrayList<>();
				for(ActionRole one:it){
						all.add(one);
				}
				return all;
		}

}
