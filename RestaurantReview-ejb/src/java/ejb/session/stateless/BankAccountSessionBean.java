/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.BankAccount;
import entity.Restaurant;
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
import util.exception.BankAccountExistException;
import util.exception.BankAccountNotFoundException;
import util.exception.CreateNewBankAccountException;
import util.exception.InputDataValidationException;
import util.exception.RestaurantNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author fengyuan
 */
@Stateless
public class BankAccountSessionBean implements BankAccountSessionBeanLocal {

    @EJB
    private RestaurantSessionBeanLocal restaurantSessionBeanLocal;
    
    @PersistenceContext(unitName = "RestaurantReview-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public BankAccountSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public Long createNewBankAccount(BankAccount newBankAccount, Long restaurantId) 
            throws UnknownPersistenceException, InputDataValidationException, CreateNewBankAccountException, BankAccountExistException
    {
        Set<ConstraintViolation<BankAccount>>constraintViolations = validator.validate(newBankAccount);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                Restaurant restaurant = restaurantSessionBeanLocal.retrieveRestaurantById(restaurantId);
                
                em.persist(newBankAccount);
                newBankAccount.setRestaurant(restaurant);
                restaurant.setBankAccount(newBankAccount);
                
                em.flush();

                return newBankAccount.getBankAccountId();
            }
            catch(PersistenceException ex)
            {
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                    {
                        throw new BankAccountExistException();
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
                throw new CreateNewBankAccountException("An error has occurred while creating the new bank account: " + ex.getMessage());
            }
        }
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public List<BankAccount> retrieveAllBankAccounts()
    {
        Query query = em.createQuery("SELECT ba FROM BankAccount ba ORDER BY ba.bankAccountNumber ASC");        
        List<BankAccount> bankAccounts = query.getResultList();
        
        for(BankAccount bankAccount:bankAccounts)
        {
//            reservation.getCategoryEntity();
//            reservation.getTagEntities().size();
        }
        
        return bankAccounts;
    }
    
    @Override
    public BankAccount retrieveBankAccountById(Long bankAccountId) throws BankAccountNotFoundException
    {
        BankAccount bankAccount = em.find(BankAccount.class, bankAccountId);
        
        if(bankAccount != null)
        {
//            productEntity.getCategoryEntity();
//            productEntity.getTagEntities().size();
            
            return bankAccount;
        }
        else
        {
            throw new BankAccountNotFoundException("BankAccount ID " + bankAccountId + " does not exist!");
        }               
    }
    
    @Override
    public void deleteBankAccount(Long bankAccountId) throws BankAccountNotFoundException
    {
        BankAccount bankAccountToRemove = retrieveBankAccountById(bankAccountId);
        
        em.remove(bankAccountToRemove);

    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<BankAccount>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
