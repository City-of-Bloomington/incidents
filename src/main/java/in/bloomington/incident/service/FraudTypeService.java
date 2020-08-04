package in.bloomington.incident.service;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import in.bloomington.incident.model.FraudType;

public interface FraudTypeService{
    public abstract void save(FraudType type);
    public abstract FraudType findById(int id);
    public abstract void update(FraudType type);
    public abstract void delete(int id);
    public abstract List<FraudType> getAll();

}
