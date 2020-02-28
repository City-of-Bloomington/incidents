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
import in.bloomington.incident.repos.ActionLogRepository;
import in.bloomington.incident.model.ActionLog;

@Service
public class ActionLogServiceImpl implements ActionLogService {

		@Autowired
		ActionLogRepository repository;

		@Override
		public void save(ActionLog actionLog){
				repository.save(actionLog);
		}

		@Override
		public ActionLog findById(int id){
				ActionLog log = repository.findById(id)
						.orElseThrow(() -> new IllegalArgumentException("Invalid action log Id:" + id));
				return log;
		}				
		@Override
		public void update(ActionLog actionLog){
				repository.save(actionLog);
		}
		@Override
		public void delete(int id){
				repository.deleteById(id);
		}
		@Override
		public List<ActionLog> getAll(){
				List<ActionLog> all = null;
				Iterable<ActionLog> it = repository.findAll();
				all = new ArrayList<>();
				for(ActionLog one:it){
						all.add(one);
				}
				return all;
		}

}
