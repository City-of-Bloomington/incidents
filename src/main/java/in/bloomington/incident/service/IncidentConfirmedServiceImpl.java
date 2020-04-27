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
import in.bloomington.incident.repos.IncidentConfirmedRepo;
import in.bloomington.incident.model.IncidentConfirmed;

@Service
public class IncidentConfirmedServiceImpl implements IncidentConfirmedService {

    @Autowired
    IncidentConfirmedRepo repository;
    
    @Override
    public List<IncidentConfirmed> getAll(){
	List<IncidentConfirmed> all = null;
	Iterable<IncidentConfirmed> it = repository.findAll();
	all = new ArrayList<>();
	for(IncidentConfirmed one:it){
	    all.add(one);
	}
	return all;
    }

}
