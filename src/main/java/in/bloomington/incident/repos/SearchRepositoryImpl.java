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
import in.bloomington.incident.model.User;
import in.bloomington.incident.model.Search;
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.utils.Helper;

@Repository
@Transactional(readOnly = true)
public class SearchRepositoryImpl implements SearchRepository {

    @PersistenceContext
    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Incident> find(Search search){
				String id="",caseNumber="",address="",zip="",city="",
						state="",dateFrom="",dateTo="",incidentTypeId="",
						name="", dln="", dob="";
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
				if(!caseNumber.isEmpty()){
						qw = "em.caseNumber = ? ";
				}
				if(!address.isEmpty()){
						qf = " join addresses ad on em.address_id=ad.id ";
						if(!qw.isEmpty()) qw += " and ";
						qw += "ad.name like ? ";
				}
				if(!zip.isEmpty()){
						if(qf.isEmpty() || qf.indexOf("addresses") == -1)
								qf += " join addresses ad on em.address_id=ad.id ";
						if(!qw.isEmpty()) qw += " and ";
						qw += "ad.zipcode like ? ";
				}
				if(!incidentTypeId.isEmpty()){
						if(!qw.isEmpty()) qw += " and ";
						qw += "em.incident_type_id = ? ";
				}
				if(!name.isEmpty()){
						qf = " join persons pr on pr.incident_id=em.id ";
						if(!qw.isEmpty()) qw += " and ";
						qw += "(pr.firstname like ? or pr.lastname like ?)";
				}
				if(!dln.isEmpty()){
						if(qf.isEmpty() || qf.indexOf("persons") == -1)
								qf = " join persons pr on pr.incident_id=em.id ";
						if(!qw.isEmpty()) qw += " and ";
						qw += "pr.dln like ? ";
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
						if(qf.isEmpty() || qf.indexOf("persons") == -1)
								qf = " join persons pr on pr.incident_id=em.id ";
						if(!qw.isEmpty()) qw += " and ";
						qw += "pr.dob = ? ";
				}	
				if(!qf.isEmpty()){
						qq += qf;
				}
				if(!qw.isEmpty()){
						qq += " where "+qw;
				}
	
				System.err.println(" *** qq "+qq);
      
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
						System.err.println(ex);
				}

        return query.getResultList();
    }
		@Override
		public User findUser(String username) throws IOException {
				String qq = "SELECT u.* FROM users as u where u.username = ?";
				if(username == null || username.isEmpty()){
						throw new IOException("Username is required");
				}
				System.err.println(" *** qq "+qq);
        Query query = entityManager.createNativeQuery(qq, User.class);
				query.setParameter(1, username);				
				List<User> users =  query.getResultList();
				if(users != null && users.size() > 0){
						return users.get(0);
				}
				return null;
		}

}







