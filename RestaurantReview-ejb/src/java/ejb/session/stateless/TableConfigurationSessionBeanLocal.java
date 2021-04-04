/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.TableConfiguration;
import java.util.List;
import javax.ejb.Local;
import util.exception.InputDataValidationException;
import util.exception.TableConfigurationExistException;
import util.exception.TableConfigurationNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author fengyuan
 */
@Local
public interface TableConfigurationSessionBeanLocal {

    public Long createNewTableConfiguration(TableConfiguration newTableConfiguration) throws UnknownPersistenceException, InputDataValidationException, TableConfigurationExistException;

    public List<TableConfiguration> retrieveAllTableConfigurations();

    public TableConfiguration retrieveTableConfigurationById(Long tableConfigurationId) throws TableConfigurationNotFoundException;

    public void deleteTableConfiguration(Long tableConfigurationId) throws TableConfigurationNotFoundException;
    
}
