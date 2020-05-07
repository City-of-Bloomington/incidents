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
import in.bloomington.incident.repos.IncidentIncompleteRepo;
import in.bloomington.incident.model.IncidentIncomplete;

@Service
public class IncidentIncompleteServiceImpl implements IncidentIncompleteService {

    @Autowired
    IncidentIncompleteRepo repository;
    
    @Override
    public List<IncidentIncomplete> getAll(){
	List<IncidentIncomplete> all = null;
	Iterable<IncidentIncomplete> it = repository.findAll();
	all = new ArrayList<>();
	for(IncidentIncomplete one:it){
	    all.add(one);
	}
	return all;
    }

}
