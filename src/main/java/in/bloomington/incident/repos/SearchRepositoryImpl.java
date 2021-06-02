package in.bloomington.incident.repos;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */
import java.io.*;
import java.util.List;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import in.bloomington.incident.model.User;
import in.bloomington.incident.model.Search;
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.utils.Helper;

@Repository
@Transactional(readOnly = true)
public class SearchRepositoryImpl implements SearchRepository {

		final static Logger logger = LoggerFactory.getLogger(SearchRepositoryImpl.class);		
    @PersistenceContext
    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Incident> find(Search search){
				String id="",caseNumber="",address="",zip="",city="",
						state="",dateFrom="",dateTo="",incidentTypeId="",
						name="", dln="", dob="", actionId="",sortBy="";
				String qw = "", qf="";
				String qq = "SELECT em.* FROM incidents as em ";
				id = search.getId(); // we handle this separately
				caseNumber = search.getCaseNumber();
				address = search.getAddress();
				zip = search.getZip();
				dateFrom = search.getDateFrom();
				dateTo = search.getDateTo();
				incidentTypeId = search.getIncidentTypeId();
				name = search.getName();
				dln = search.getDln();
				dob = search.getDob();
				actionId = search.getActionId();
				sortBy = search.getSortBy();
				boolean addrTbl = false;
				boolean personTbl = false;
				boolean actionTbl = false;
				if(!caseNumber.isEmpty()){
						qw = "em.case_number like ? ";
				}
				if(!address.isEmpty()){
						addrTbl = true;
						// qf = " join addresses ad on em.address_id=ad.id ";
						if(!qw.isEmpty()) qw += " and ";
						qw += "ad.name like ? ";
				}
				if(!zip.isEmpty()){
						
						// qf += " join addresses ad on em.address_id=ad.id ";
						if(!qw.isEmpty()) qw += " and ";
						qw += "ad.zipcode like ? ";
				}
				if(!incidentTypeId.isEmpty()){
						if(!qw.isEmpty()) qw += " and ";
						qw += "em.incident_type_id = ? ";
				}
				if(!name.isEmpty()){
						personTbl = true;
						// qf = " join persons pr on pr.incident_id=em.id ";
						if(!qw.isEmpty()) qw += " and ";
						qw += "(pr.firstname like ? or pr.lastname like ?)";
				}
				if(!dln.isEmpty()){
						personTbl = true;
						/// if(qf.isEmpty() || qf.indexOf("persons") == -1)
						//qf = " join persons pr on pr.incident_id=em.id ";
						if(!qw.isEmpty()) qw += " and ";
						qw += "pr.dln like ? ";
				}
				if(!actionId.isEmpty()){
						actionTbl = true;

						if(!qw.isEmpty()) qw += " and ";
						qw +=" (al.action_id = ? and al.action_id in (select max(l2.action_id) from action_logs l2 where l2.incident_id=al.incident_id)) ";
				}
				if(!dateFrom.isEmpty()){
						if(!qw.isEmpty()) qw += " and ";
						qw += "em.received >= ? ";
				}
				if(!dateTo.isEmpty()){
						if(!qw.isEmpty()) qw += " and ";
						qw += "em.received <= ? ";
				}
				if(!dob.isEmpty()){
						// if(qf.isEmpty() || qf.indexOf("persons") == -1)
						//	qf = " join persons pr on pr.incident_id=em.id ";
						if(!qw.isEmpty()) qw += " and ";
						qw += "pr.dob = ? ";
				}
				if(!sortBy.isEmpty()){
						if(sortBy.indexOf("name") > -1){
								personTbl = true;
								sortBy = "pr.lastname, pr.firstname";
						}
						else if(sortBy.indexOf("address") > -1){
								addrTbl = true;
								sortBy = "ad.name";
						}
						else if(sortBy.indexOf("id") > -1 || sortBy.indexOf("date") > -1){
								sortBy = "em."+sortBy;
						}
				}
				if(personTbl){
						qf = " join persons pr on pr.incident_id=em.id ";
				}
				if(addrTbl){
						qf += " join addresses ad on em.address_id=ad.id ";
				}
				if(actionTbl){
						qf += " join action_logs al on al.incident_id=em.id ";
				}
				if(!qf.isEmpty()){
						qq += qf;
				}
				if(!qw.isEmpty()){
						qq += " where "+qw;
				}
				if(!sortBy.isEmpty()){
						qq += " order by "+sortBy;
				}
        Query query = entityManager.createNativeQuery(qq, Incident.class);
				int jj=1;
				if(!caseNumber.isEmpty()){
						query.setParameter(jj++, caseNumber + "%");
				}
				if(!address.isEmpty()){
						query.setParameter(jj++, "%"+address + "%");
				}
				if(!zip.isEmpty()){
						query.setParameter(jj++, zip + "%");
				}
				if(!incidentTypeId.isEmpty()){
						query.setParameter(jj++, incidentTypeId);
				}
				if(!name.isEmpty()){
						query.setParameter(jj++, "%"+name + "%");
						query.setParameter(jj++, "%"+name + "%");
				}
				if(!dln.isEmpty()){
						query.setParameter(jj++, dln + "%");
				}
				if(!actionId.isEmpty()){
						query.setParameter(jj++, actionId);
				}
				try{
						if(!dateFrom.isEmpty()){
								query.setParameter(jj++, Helper.dfDate.parse(dateFrom), TemporalType.DATE);
						}
						if(!dateTo.isEmpty()){
								query.setParameter(jj++, Helper.dfDate.parse(dateTo), TemporalType.DATE);
						}
						if(!dob.isEmpty()){
								query.setParameter(jj++, Helper.dfDate.parse(dob), TemporalType.DATE);
						}	    
	    
				}catch(Exception ex){
						System.err.println(ex+" "+qq);
						logger.error(ex+" "+qq);
				}

        return query.getResultList();
    }


}







