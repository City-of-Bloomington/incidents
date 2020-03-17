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
import in.bloomington.incident.repos.IncidentPreApprovedRepo;
import in.bloomington.incident.model.IncidentPreApproved;

@Service
public class IncidentPreApprovedServiceImpl implements IncidentPreApprovedService {

    @Autowired
    IncidentPreApprovedRepo repository;
    
    @Override
    public List<IncidentPreApproved> getAll(){
	List<IncidentPreApproved> all = null;
	Iterable<IncidentPreApproved> it = repository.findAll();
	all = new ArrayList<>();
	for(IncidentPreApproved one:it){
	    all.add(one);
	}
	return all;
    }

}
