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
import in.bloomington.incident.repos.RequestRepository;
import in.bloomington.incident.model.Request;

@Service
public class RequestServiceImpl implements RequestService {

    @Autowired
    RequestRepository repository;

    @Override
    public void save(Request request){
	repository.save(request);
    }
    @Override
    public Request findById(int id){
	Request request = repository.findById(id)
	    .orElseThrow(() -> new IllegalArgumentException("Invalid request Id:" + id));
	return request;
    }						
    @Override
    public void update(Request request){
	repository.save(request);
    }
    @Override
    public void delete(int id){
	repository.deleteById(id);
    }
    @Override
    public List<Request> getAll(){
	List<Request> all = null;
	Iterable<Request> it = repository.findAll();
	all = new ArrayList<>();
	for(Request one:it){
	    all.add(one);
	}
	return all;
    }

}
