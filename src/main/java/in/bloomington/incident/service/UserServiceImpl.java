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
import in.bloomington.incident.repos.UserRepository;
import in.bloomington.incident.model.User;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository repository;

    @Override
    public void save(User user){
	repository.save(user);
    }
    @Override
    public void update(User user){
	repository.save(user);
    }
    @Override
    public void delete(int id){
	repository.deleteById(id);
    }
    @Override
    public User findById(int id){
	User user = repository.findById(id)
	    .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
	return user;
    }		
    @Override
    public List<User> getAll(){
	List<User> all = null;
	Iterable<User> it = repository.findAll();
	all = new ArrayList<>();
	for(User one:it){
	    all.add(one);
	}
	return all;
    }
    @Override
    public User findUserByUsername(String username){
	User user = repository.findUserByUsername(username);
	return user;
    }
    @Override
    public List<User> findByFirstnameOrByLastname(String firstname, String lastname){
	List<User> users = repository.findByFirstnameOrByLastname(firstname, lastname);
	return users;
    }
}
