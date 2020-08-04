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
import in.bloomington.incident.repos.FraudTypeRepository;
import in.bloomington.incident.model.FraudType;

@Service
public class FraudTypeServiceImpl implements FraudTypeService {

    @Autowired
    FraudTypeRepository repository;

    @Override
    public void save(FraudType type){
				repository.save(type);
    }
    @Override
    public FraudType findById(int id){
				FraudType type = repository.findById(id)
						.orElseThrow(() -> new IllegalArgumentException("Invalid fraud type Id:" + id));
				return type;
    }		
		
    @Override
    public void update(FraudType type){
				repository.save(type);
    }
    @Override
    public void delete(int id){
				repository.deleteById(id);
    }
    @Override
    public List<FraudType> getAll(){
				List<FraudType> all = null;
				Iterable<FraudType> it = repository.findAll();
				all = new ArrayList<>();
				for(FraudType one:it){
						all.add(one);
				}
				return all;
    }

}
