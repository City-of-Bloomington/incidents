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
import in.bloomington.incident.repos.ActionRepository;
import in.bloomington.incident.model.Action;

@Service
public class ActionServiceImpl implements ActionService {

    @Autowired
    ActionRepository repository;

    @Override
    public void save(Action action){
	repository.save(action);
    }
    @Override
    public Action findById(int id){
	Action action = repository.findById(id)
	    .orElseThrow(() -> new IllegalArgumentException("Invalid action Id:" + id));
	return action;
    }
    @Override
    public void update(Action action){
	repository.save(action);
    }
    @Override
    public void delete(int id){
	repository.deleteById(id);
    }
    @Override
    public List<Action> getAll(){
	List<Action> all = null;
	Iterable<Action> it = repository.findAll();
	all = new ArrayList<>();
	for(Action one:it){
	    all.add(one);
	}
	return all;
    }

}
