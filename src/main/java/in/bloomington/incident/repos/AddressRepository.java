package in.bloomington.incident.repos;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import in.bloomington.incident.model.Address;

// This will be AUTO IMPLEMENTED by Spring into a Bean 
// CRUD refers Create, Read, Update, Delete

public interface AddressRepository extends CrudRepository<Address, Integer>,
AddressRepositoryCustom{
		@Query(value="select * from addresses a where a.id=?",
					 nativeQuery=true)
		public Address findOne(Integer id);

		
}
