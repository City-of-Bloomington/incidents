package in.bloomington.incident.repos;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import org.springframework.data.repository.CrudRepository;

import in.bloomington.incident.model.Media;

// This will be AUTO IMPLEMENTED by Spring into a Bean 
// CRUD refers Create, Read, Update, Delete

public interface MediaRepository extends CrudRepository<Media, Integer> {

}
