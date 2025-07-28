package in.bloomington.incident.service;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import in.bloomington.incident.repos.AddressRepository;
import in.bloomington.incident.model.Address;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    AddressRepository repository;

    @Override
    public void save(Address val){
	repository.save(val);
    }
    @Override
    public Address findById(int id){
	Address address = repository.findById(id)
	    .orElseThrow(() -> new IllegalArgumentException("Invalid address Id:" + id));
	return address;
    }
    @Override
    public void update(Address val){
	repository.save(val);
    }
    @Override
    public void delete(int id){
	repository.deleteById(id);
    }
    @Override
    public Address findOne(int id){
	return repository.findOne(id);
    }		
    @Override
    public List<Address> getAll(){
	Iterable<Address> it = repository.findAll();
	return changeToList(it);
    }
    public List<Address> findDistinctAddressByName(String name){
	Iterable<Address> it = repository.findDistinctAddressByName(name);
	return changeToList(it);
    }
    
    public List<Address> findDistinctAddressByAddressId(Integer addressId){
	Iterable<Address> it = repository.findDistinctAddressByAddressId(addressId);
	return changeToList(it);				
    }
    public List<Address> findDistinctAddressByAddressIdAndSubunitId(Integer addressId, Integer subunitId){
	Iterable<Address> it = repository.findDistinctAddressByAddressIdAndSubunitId(addressId, subunitId);
	return changeToList(it);	
    }
    public List<Address> findByNameContaining(String name){
	Iterable<Address> it = repository.findByNameContaining(name);
	return changeToList(it);	
    }
    private List<Address> changeToList(Iterable<Address> it){
	if(it == null)
	    return null;
	List<Address> all = null; 
	for(Address one:it){
	    if(one != null && one.hasData()){
		if(all == null) all = new ArrayList<>();
		all.add(one);
	    }
	}
	return all;	
    }
}
