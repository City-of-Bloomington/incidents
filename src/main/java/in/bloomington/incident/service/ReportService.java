package in.bloomington.incident.service;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import in.bloomington.incident.model.IncidentStats;
import in.bloomington.incident.model.StatsRequest;

public interface ReportService{
    public abstract List<IncidentStats> getActionStats(StatsRequest val);

}
