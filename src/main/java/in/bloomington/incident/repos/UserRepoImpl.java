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
import in.bloomington.incident.model.Credential;
import in.bloomington.incident.utils.Helper;

@Repository
@Transactional(readOnly = true)
public class UserRepoImpl implements UserRepo {

		final static Logger logger = LoggerFactory.getLogger(UserRepoImpl.class);		
    @PersistenceContext
    @Autowired
    private EntityManager entityManager;

		@Override
		public User findUser(String username) throws IOException {
				String qq = "SELECT u.* FROM users as u where u.username = ?";
				if(username == null || username.isEmpty()){
						logger.error("username not set");
						throw new IOException("Username is required");
				}
        Query query = entityManager.createNativeQuery(qq, User.class);
				query.setParameter(1, username);				
				List<User> users =  query.getResultList();
				if(users != null && users.size() > 0){
						return users.get(0);
				}
				return null;
		}
		@Override
		public String encryptString(String str) throws IOException{
				String qq = "select sha2(?,256) ";
				if(str == null || str.isEmpty()){
						logger.error("invalid string");
						throw new IOException("Invalid String");
				}
        Query query = entityManager.createNativeQuery(qq);
				query.setParameter(1, str);
				return (String) query.getSingleResult();
		}
		@Override
		public Credential findCredential(String email) throws IOException {
				String qq = "SELECT c.* FROM credentials as c where c.email = ?";
				if(email == null || email.isEmpty()){
						logger.error("email not set");
						throw new IOException("Email is required");
				}
        Query query = entityManager.createNativeQuery(qq, Credential.class);
				query.setParameter(1, email);				
				return (Credential) query.getSingleResult();
		}		

}







