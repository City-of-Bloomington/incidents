package in.bloomington.incident.repos;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */
import java.util.List;
import java.util.Date;
import java.util.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.ConstructorResult;
import javax.persistence.ColumnResult;
import in.bloomington.incident.model.StatsRequest;
import in.bloomington.incident.model.IncidentStats;
import in.bloomington.incident.utils.Helper;

@Repository
@Transactional(readOnly = true)
public class StatsRepositoryImpl implements StatsRepository {

    @PersistenceContext
    @Autowired
    private EntityManager entityManager;
    //
    public boolean lastWeek=false, lastMonth=false, lastYear=false;
    public String dateFrom = "", dateTo = "";
    public String type="";
    
    @Override
    public List<IncidentStats> getActionStats(StatsRequest request){
				request.setOptedPeriod();
				lastWeek = request.getLastWeek();
				lastMonth = request.getLastMonth();
				lastYear = request.getLastYear();
				dateFrom = request.getDateFrom();
				dateTo = request.getDateTo();
				type = request.getType();
				Date startDate = null;
				Date endDate = null;
				Date date = new Date();
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				// find date range
				if(lastWeek){
						int i = cal.get(Calendar.DAY_OF_WEEK) - cal.getFirstDayOfWeek();
						cal.add(Calendar.DATE, -i - 7);
						startDate = cal.getTime();
						cal.add(Calendar.DATE, 6);
						endDate = cal.getTime();
				}
				else if(lastMonth){
						cal.add(Calendar.MONTH, -1);
						cal.set(Calendar.DATE, 1);
						startDate = cal.getTime();
						cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE)); 
						endDate = cal.getTime();	    
				}
				else if(lastYear){ // 1/1/yyyy - 12/31/yyyy
						cal.add(Calendar.YEAR, -1);
						cal.set(Calendar.DATE, 1);
						cal.set(Calendar.MONTH, 0);
						startDate = cal.getTime();
						cal.set(Calendar.DATE, 31);
						cal.set(Calendar.MONTH, 11);
						endDate = cal.getTime();	    
				}
				if(!dateFrom.isEmpty()){
						System.err.println(" from "+dateFrom);
						try{
								startDate = Helper.dfDate.parse(dateFrom);
						}catch(Exception ex){
								System.err.println(ex);
						}
				}
				if(!dateTo.isEmpty()){
						try{
								endDate = Helper.dfDate.parse(dateTo);
						}catch(Exception ex){
								System.err.println(ex);
						}
				}
				System.err.println(" *** from "+startDate);
				System.err.println(" *** to "+endDate);

				String qw = "", qf="";
				/*
					SELECT a.name as name,count(l.incident_id) as total FROM action_logs as l, actions as a where a.id=l.action_id and l.action_id = (select max(l2.action_id) from action_logs l2 where l2.incident_id=l.incident_id) and l.date > '2020-01-01' group by a.name order by a.name;

				*/
				String qq = "SELECT a.name as name,count(l.incident_id) as total FROM action_logs as l, actions as a where a.id=l.action_id and l.action_id = (select max(l2.action_id) from action_logs l2 where l2.incident_id=l.incident_id)";
				if(startDate != null){
						qw += " and ";
						qw += "l.date >= ? ";
				}
				if(endDate != null){
						qw += " and ";
						qw += "l.date <= ? ";
				}
				if(!qw.isEmpty()){
						qq += qw;
				}
				qq += " group by a.name order by a.name "; 
        Query query = entityManager.createNativeQuery(qq,
																											"ResultMapping");
				int jj=1;
				try{
						if(startDate != null){
								query.setParameter(jj++, startDate, TemporalType.DATE);
						}
						if(endDate != null){
								query.setParameter(jj++, endDate, TemporalType.DATE);
						}
				}catch(Exception ex){
						System.err.println(ex);
				}

        return query.getResultList();
    }
    /**
		 // test

		 SELECT a.name as name,count(l.incident_id) as total FROM action_logs as l, actions as a where a.id=l.action_id and l.action_id = (select max(l2.action_id) from action_logs l2 where l2.incident_id=l.incident_id) and l.date > '2020-01-01' group by a.name order by a.name;       

		*/

}
