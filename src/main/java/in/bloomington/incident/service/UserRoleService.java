package in.bloomington.incident.service;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import in.bloomington.incident.model.UserRole;

public interface UserRoleService{
		public abstract void save(UserRole val);
		public abstract void update(UserRole val);
		public abstract void delete(int id);
		public abstract List<UserRole> getAll();		

}
