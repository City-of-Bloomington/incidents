package in.bloomington.incident.repos;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.Query;
import in.bloomington.incident.model.Search;
import in.bloomington.incident.model.Incident;

@Repository
@Transactional(readOnly = true)
public class SearchRepositoryImpl implements SearchRepository {

    @PersistenceContext
    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Incident> find(Search search){

	return null;
    }

    /*
        public List<Employee> getFirstNamesLike(String firstName) {

        Query query = entityManager.createNativeQuery("SELECT em.* FROM spring_data_jpa_example.employee as em " +

                "WHERE em.firstname LIKE ?", Employee.class);

        query.setParameter(1, firstName + "%");

        return query.getResultList();

    }
    */    
}
