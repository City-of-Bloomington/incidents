package in.bloomington.incident.service;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */
import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import in.bloomington.incident.repos.SearchRepository;
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.Search;
import in.bloomington.incident.model.User;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    SearchRepository repository;

    @Override
    public List<Incident> find(Search search){
				List<Incident> all = repository.find(search);
				return all;
    }
    @Override
    public User findUser(String username) throws IOException{
				User user = repository.findUser(username);
				return user;
    }
}
