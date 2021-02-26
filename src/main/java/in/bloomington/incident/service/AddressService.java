package in.bloomington.incident.service;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import in.bloomington.incident.model.Address;

public interface AddressService{
		/**
    public abstract void save(Address val);
    public abstract void update(Address val);
    public abstract Address findById(int id);
    public Address findOne(int id);
    public abstract void delete(int id);
    public abstract List<Address> getAll();
		*/
		public List<Address> findDistinctAddressByName(String name);
		public List<Address> findDistinctAddressByAddressId(Integer addressId);
		public List<Address> findDistinctAddressByAddressIdAndSubunitId(Integer addressId, Integer subunitId);
		public List<Address> findByNameContaining(String str);
		
}
