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
import in.bloomington.incident.repos.SearchRepository;
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.Search;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    SearchRepository repository;

    /*
    @Override
    public Incident findById(int id){
	Incident incident = repository.findById(id)
	    .orElseThrow(() -> new IllegalArgumentException("Invalid incident Id:" + id));
	return incident;
    }
    */
    @Override
    public List<Incident> find(Search search){
	List<Incident> all = null;
	/*
	Iterable<Incident> it = repository.find();
	all = new ArrayList<>();
	for(Action one:it){
	    all.add(one);
	}
	*/
	return all;
    }

}
