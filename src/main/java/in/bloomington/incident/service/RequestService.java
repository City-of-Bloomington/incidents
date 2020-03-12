package in.bloomington.incident.service;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import in.bloomington.incident.model.Request;

public interface RequestService{
    public abstract void save(Request one);
    public abstract Request findById(int id);
    public abstract void update(Request one);
    public abstract void delete(int id);
    public abstract List<Request> getAll();		

}
