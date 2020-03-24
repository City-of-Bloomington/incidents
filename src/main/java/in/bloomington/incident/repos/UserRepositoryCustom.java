package in.bloomington.incident.repos;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import in.bloomington.incident.model.User;

public interface UserRepositoryCustom extends Repository<User, Integer> {

    @Query(value = "SELECT * FROM USERS WHERE username = ?1", nativeQuery = true)
    /*
    @NamedQuery(name = "User.findUserByUsername",
		query = "select u from User u where u.username = ?1")
    */
    User findUserByUsername(String username);
    //
    @Query(value="select u from User u where u.firstname like %?1 or u.lastname like %?2", nativeQuery = true)
    List<User> findByFirstnameOrByLastname(String firstname, String lastname);

}
