/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import entity.Restaurant;
import entity.Transaction;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CreateTransactionException;
import util.exception.InputDataValidationException;
import util.exception.RestaurantNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author zhiliangwang
 */
@Stateless
public class TransactionSessionBean implements TransactionSessionBeanLocal
{
    
    @EJB
    private RestaurantSessionBeanLocal restaurantSessionBeanLocal;

    @PersistenceContext(unitName = "RestaurantReview-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public TransactionSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    
    @Override
    public Long createCashOutTransaction(Transaction newTransaction, Long restaurantId) 
            throws CreateTransactionException, UnknownPersistenceException, RestaurantNotFoundException, InputDataValidationException
    {
        Set<ConstraintViolation<Transaction>>constraintViolations = validator.validate(newTransaction);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                Restaurant restaurant = restaurantSessionBeanLocal.retrieveRestaurantById(restaurantId);
                
                em.persist(newTransaction);
                newTransaction.setRestaurant(restaurant);                
                newTransaction.setBankAccount(restaurant.getBankAccount());
                restaurant.getTransactions().add(newTransaction);
                
                em.flush();

                return newTransaction.getTransactionId();
            }
            catch(PersistenceException ex)
            {
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                    {
                        throw new CreateTransactionException();
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
            catch(RestaurantNotFoundException ex)
            {
                throw new CreateTransactionException("An error has occurred while creating the new transaction: " + ex.getMessage());
            }
        }
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
                
    }
    
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Transaction>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
    
}
