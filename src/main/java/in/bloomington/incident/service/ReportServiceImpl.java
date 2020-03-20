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
import in.bloomington.incident.repos.StatsRepository;
import in.bloomington.incident.model.IncidentStats;
import in.bloomington.incident.model.StatsRequest;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    StatsRepository repository;

    @Override
    public List<IncidentStats> getActionStats(StatsRequest request){
	List<IncidentStats> all = repository.getActionStats(request);
	return all;
    }

}
