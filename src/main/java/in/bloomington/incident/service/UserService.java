package in.bloomington.incident.service;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import in.bloomington.incident.model.User;

public interface UserService{
    public abstract void save(User user);
    public abstract void update(User user);
    public abstract void delete(int id);
    public abstract User findById(int id);
    public abstract List<User> getAll();		
    public abstract User findUserByUsername(String val);
    public abstract List<User> findByFirstnameOrByLastname(String firstname, String lastname);
}
