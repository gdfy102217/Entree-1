/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CreditCard;
import entity.Customer;
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
import util.exception.BankAccountNotFoundException;
import util.exception.CreateNewCreditCardException;
import util.exception.CreditCardExistException;
import util.exception.CreditCardNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author fengyuan
 */
@Stateless
public class CreditCardSessionBean implements CreditCardSessionBeanLocal {

    @PersistenceContext(unitName = "RestaurantReview-ejbPU")
    private EntityManager em;

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public CreditCardSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public CreditCard createNewCreditCard(CreditCard newCreditCard, Long customerId) 
            throws UnknownPersistenceException, InputDataValidationException, CreateNewCreditCardException, CreditCardExistException
    {
        Set<ConstraintViolation<CreditCard>>constraintViolations = validator.validate(newCreditCard);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                Customer customer = customerSessionBeanLocal.retrieveCustomerById(customerId);
                
                em.persist(newCreditCard);
                newCreditCard.setOwner(customer);
                customer.setCreditCard(newCreditCard);
                
                em.flush();

                return newCreditCard;
            }
            catch(PersistenceException ex)
            {
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                    {
                        throw new CreditCardExistException();
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
            catch(CustomerNotFoundException ex)
            {
                throw new CreateNewCreditCardException("An error has occurred while creating the new credit card: " + ex.getMessage());
            }
        }
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public List<CreditCard> retrieveAllCreditCards()
    {
        Query query = em.createQuery("SELECT cc FROM CreditCard cc ORDER BY cc.cardNumber ASC");        
        List<CreditCard> creditCards = query.getResultList();
        
        for(CreditCard creditCard:creditCards)
        {
//            reservation.getCategoryEntity();
//            reservation.getTagEntities().size();
        }
        
        return creditCards;
    }
    
    @Override
    public List<CreditCard> retrieveCreditCardsByCustomerId(Long customerId)
    {
        Query query = em.createQuery("SELECT cc FROM CreditCard cc WHERE cc.owner.userId = :inCustomerId");
        query.setParameter("inCustomerId", customerId);
        List<CreditCard> creditCards = query.getResultList();
        
        for(CreditCard creditCard:creditCards)
        {
//            reservation.getCategoryEntity();
//            reservation.getTagEntities().size();
        }
        
        return creditCards;
    }
    
    @Override
    public CreditCard retrieveCreditCardById(Long creditCardId) throws CreditCardNotFoundException
    {
        CreditCard creditCard = em.find(CreditCard.class, creditCardId);
        
        if(creditCard != null)
        {
//            productEntity.getCategoryEntity();
//            productEntity.getTagEntities().size();
            
            return creditCard;
        }
        else
        {
            throw new CreditCardNotFoundException("CreditCard ID " + creditCardId + " does not exist!");
        }               
    }
    
    @Override
    public void deleteCreditCard(Long creditCardId) throws CreditCardNotFoundException
    {
        CreditCard creditCardToRemove = retrieveCreditCardById(creditCardId);
        
        creditCardToRemove.getTransaction().setCreditCard(null);
        creditCardToRemove.getOwner().setCreditCard(null);
        creditCardToRemove.setOwner(null);
        creditCardToRemove.setTransaction(null);
        
        em.remove(creditCardToRemove);

    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<CreditCard>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
    
}
