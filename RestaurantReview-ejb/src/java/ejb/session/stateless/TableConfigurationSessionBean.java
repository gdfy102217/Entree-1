/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.TableConfiguration;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.InputDataValidationException;
import util.exception.TableConfigurationExistException;
import util.exception.TableConfigurationNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author fengyuan
 */
@Stateless
public class TableConfigurationSessionBean implements TableConfigurationSessionBeanLocal {

    @PersistenceContext(unitName = "RestaurantReview-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public TableConfigurationSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public Long createNewTableConfiguration(TableConfiguration newTableConfiguration) 
            throws UnknownPersistenceException, InputDataValidationException, TableConfigurationExistException
    {
        Set<ConstraintViolation<TableConfiguration>>constraintViolations = validator.validate(newTableConfiguration);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                em.persist(newTableConfiguration);
                em.flush();

                return newTableConfiguration.getTableConfigurationId();
            }
            catch(PersistenceException ex)
            {
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                    {
                        throw new TableConfigurationExistException();
                    }
                    else
                    {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                }
                else
                {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        }
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public List<TableConfiguration> retrieveAllTableConfigurations()
    {
        Query query = em.createQuery("SELECT tc FROM TableConfiguration tc ORDER BY tc.tableConfigurationId ASC");        
        List<TableConfiguration> tableConfigurations = query.getResultList();
        
        for(TableConfiguration tableConfiguration: tableConfigurations)
        {
//            reservation.getCategoryEntity();
//            reservation.getTagEntities().size();
        }
        
        return tableConfigurations;
    }
    
    @Override
    public TableConfiguration retrieveTableConfigurationById(Long tableConfigurationId) throws TableConfigurationNotFoundException
    {
        TableConfiguration tableConfiguration = em.find(TableConfiguration.class, tableConfigurationId);
        
        if(tableConfiguration != null)
        {
//            productEntity.getCategoryEntity();
//            productEntity.getTagEntities().size();
            
            return tableConfiguration;
        }
        else
        {
            throw new TableConfigurationNotFoundException("TableConfiguration ID " + tableConfigurationId + " does not exist!");
        }               
    }
    
    @Override
    public void deleteTableConfiguration(Long tableConfigurationId) throws TableConfigurationNotFoundException
    {
        TableConfiguration tableConfigurationToRemove = retrieveTableConfigurationById(tableConfigurationId);
        
        em.remove(tableConfigurationToRemove);

    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<TableConfiguration>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
