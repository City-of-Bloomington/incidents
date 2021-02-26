package in.bloomington.incident.repos;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import java.util.Map.Entry;
import org.springframework.data.repository.Repository;
import org.springframework.data.jpa.repository.Query;
import in.bloomington.incident.model.Address;

public interface AddressRepositoryCustom extends Repository<Address, Integer>{

		//
		// Enables the distinct flag for the query
		List<Address> findDistinctAddressByName(String name);
		List<Address> findByNameContaining(String name);		
		// addressId is master address id
		// subunitId is master address subunit id
		List<Address> findDistinctAddressByAddressId(Integer addressId);
		List<Address> findDistinctAddressByAddressIdAndSubunitId(Integer addressId, Integer subunitId);

		
}
