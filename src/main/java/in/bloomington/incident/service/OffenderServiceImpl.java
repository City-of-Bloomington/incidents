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
import in.bloomington.incident.repos.OffenderRepository;
import in.bloomington.incident.model.Offender;

@Service
public class OffenderServiceImpl implements OffenderService {

		@Autowired
		OffenderRepository repository;

		@Override
		public void save(Offender val){
				repository.save(val);
		}
		@Override
		public Offender findById(int id){
				Offender one = repository.findById(id)
						.orElseThrow(() -> new IllegalArgumentException("Invalid offender Id:" + id));
				return one;
		}				
		@Override
		public void update(Offender val){
				repository.save(val);
		}
		@Override
		public void delete(int id){
				repository.deleteById(id);
		}
		@Override
		public List<Offender> getAll(){
				List<Offender> all = null;
				Iterable<Offender> it = repository.findAll();
				all = new ArrayList<>();
				for(Offender one:it){
						all.add(one);
				}
				return all;
		}

}
