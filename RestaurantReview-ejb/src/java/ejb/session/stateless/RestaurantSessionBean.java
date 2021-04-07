/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.BankAccount;
import entity.Dish;
import entity.Restaurant;
import entity.TableConfiguration;
import entity.Transaction;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.BankAccountNotFoundException;
import util.exception.ChangePasswordException;
import util.exception.CreateTransactionException;
import util.exception.DishNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.RestaurantNotFoundException;
import util.exception.RestaurantUsernameExistException;
import util.exception.TableConfigurationExistException;
import util.exception.TableConfigurationNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author fengyuan
 */
@Stateless
public class RestaurantSessionBean implements RestaurantSessionBeanLocal {

    @EJB
    private DishSessionBeanLocal dishSessionBeanLocal;
    @EJB
    private BankAccountSessionBeanLocal bankAccountSessionBeanLocal;
    @EJB
    private TableConfigurationSessionBeanLocal tableConfigurationSessionBeanLocal;
    @EJB
    private TransactionSessionBeanLocal transactionSessionBeanLocal;
    
    @PersistenceContext(unitName = "RestaurantReview-ejbPU")
    private EntityManager em;
            
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public RestaurantSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public Long createNewRestaurant(Restaurant newRestaurant, TableConfiguration newTableConfiguration) throws UnknownPersistenceException, InputDataValidationException, RestaurantUsernameExistException, TableConfigurationExistException
    {
        Set<ConstraintViolation<Restaurant>> constraintViolations = validator.validate(newRestaurant);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                em.persist(newRestaurant);
                if (newRestaurant.getAcceptReservation() == Boolean.FALSE) {
                    newTableConfiguration = new TableConfiguration();
                }
                
                tableConfigurationSessionBeanLocal.createNewTableConfiguration(newTableConfiguration);
                newRestaurant.setTableConfiguration(newTableConfiguration);
                
                em.flush();

                return newRestaurant.getUseId();
            }
            catch(PersistenceException ex)
            {
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                    {
                        throw new RestaurantUsernameExistException("Username already exists");
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
    public List<Restaurant> retrieveAllRestaurants()
    {
        Query query = em.createQuery("SELECT r FROM Restaurant r ORDER BY r.useId ASC");        
        List<Restaurant> restaurants = query.getResultList();
        
        for(Restaurant restaurant: restaurants)
        {
            restaurant.getDishs().size();
            restaurant.getPromotions().size();
            restaurant.getReservations().size();
            restaurant.getReviews().size();
            restaurant.getTransactions().size();
        }
        
        return restaurants;
    }
    
    @Override
    public Restaurant retrieveRestaurantById(Long restaurantId) throws RestaurantNotFoundException 
    {
        Restaurant restaurant = em.find(Restaurant.class, restaurantId);
        
        if(restaurant != null)
        {
            restaurant.getDishs().size();
            restaurant.getReservations().size();
            return restaurant;
        }
        else
        {
            throw new RestaurantNotFoundException("Restaurant ID " + restaurantId + " does not exist!");
        }
    }
    
    @Override
    public Restaurant retrieveRestaurantByEmail(String email) throws RestaurantNotFoundException
    {
        Query query = em.createQuery("SELECT r FROM Restaurant r WHERE r.email = :inEmail");
        query.setParameter("inEmail", email);
        
        try
        {
            return (Restaurant)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new RestaurantNotFoundException("Restaurant with Email " + email + " does not exist!");
        }
    }
    
    @Override
    public List<Restaurant> searchRestaurantsByName(String searchString)
    {
        Query query = em.createQuery("SELECT r FROM Restaurant r WHERE r.name LIKE :inSearchString ORDER BY r.name ASC");
        query.setParameter("inSearchString", "%" + searchString + "%");
        List<Restaurant> restaurants = query.getResultList();
        
        for(Restaurant restaurant:restaurants)
        {
            restaurant.getDishs().size();
            restaurant.getPromotions().size();
            restaurant.getReservations().size();
            restaurant.getReviews().size();
            restaurant.getTransactions().size();
        }
        
        return restaurants;
    }
    
    @Override
    public Restaurant changePassword(Long restaurantId, String newPassword) throws ChangePasswordException
    {
        try
        {
            Restaurant restaurant = em.find(Restaurant.class, restaurantId);
            
            if(restaurant != null)
            {
                restaurant.setPassword(newPassword);
                restaurant.getReservations().size();
                restaurant.getPhotos().size();
                return restaurant;
            }
            else
            {
                throw new ChangePasswordException("Password is not changed!");
            }
        }
        catch(ChangePasswordException ex)
        {
            throw new ChangePasswordException("Password is not changed!");
        }
        
    }
    
    @Override
    public Restaurant restaurantLogin(String username, String password) throws InvalidLoginCredentialException
    {
        try
        {
            Restaurant restaurant = retrieveRestaurantByEmail(username);
            
            if(restaurant.getPassword().equals(password))
            {
//                customerEntity.getSaleTransactionEntities().size();  
                restaurant.getReservations().size();
                restaurant.getPhotos().size();
                return restaurant;
            }
            else
            {
                throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
            }
        }
        catch(RestaurantNotFoundException ex)
        {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }
    
    @Override
    public void cashOutCredit(Long restaurantId) 
            throws UnknownPersistenceException, CreateTransactionException, RestaurantNotFoundException, InputDataValidationException
    {
        try
        {
            Restaurant restaurant = em.find(Restaurant.class, restaurantId);
            
            if(restaurant != null)
            {
                
                Transaction newTransaction = new Transaction();
                newTransaction.setPaidAmount(restaurant.getCreditAmount());
                newTransaction.setTransactionDate(new Date(new Date().getTime() + (60 * 60 * 1000)));
                
                try
                {
                    transactionSessionBeanLocal.createCashOutTransaction(newTransaction, restaurantId);
                    restaurant.setCreditAmount(0);
                }
                catch(CreateTransactionException ex)
                {
                    throw new CreateTransactionException("Failed to create new transaction!");
                }
                
            }
            else
            {
                throw new UnknownPersistenceException("Invalid Restaurant ID: " + restaurantId);
            }
        }
        catch(UnknownPersistenceException ex)
        {
            throw new UnknownPersistenceException("Transaction failed!");
        }
    }
    
    @Override
    public Long updateRestaurant(Restaurant restaurant) 
            throws RestaurantNotFoundException, InputDataValidationException, DishNotFoundException, BankAccountNotFoundException, TableConfigurationNotFoundException
    {
        if(restaurant != null && restaurant.getUseId()!= null)
        {
            Set<ConstraintViolation<Restaurant>>constraintViolations = validator.validate(restaurant);
        
            if(constraintViolations.isEmpty())
            {
                Restaurant restaurantToUpdate = retrieveRestaurantById(restaurant.getUseId());

                //alr done in Bank Account SB
//                if(bankAccountId != null && (!restaurantToUpdate.getBankAccount().getBankAccountId().equals(bankAccountId)))
//                {
//                    BankAccount bankAccountToUpdate = bankAccountSessionBeanLocal.retrieveBankAccountById(bankAccountId);
//                    restaurantToUpdate.setBankAccount(bankAccountToUpdate);
//                    bankAccountToUpdate.setRestaurant(restaurantToUpdate);
//                }
                
                    //change data of the current table config
//                if(tableConfigurationId != null && (!restaurantToUpdate.getTableConfiguration().getTableConfigurationId().equals(tableConfigurationId)))
//                {
//                    TableConfiguration tableConfigurationToUpdate = tableConfigurationSessionBeanLocal.retrieveTableConfigurationById(tableConfigurationId);
//                    restaurantToUpdate.setTableConfiguration(tableConfigurationToUpdate);                    
//                }

                
                restaurantToUpdate.setName(restaurant.getName());
                restaurantToUpdate.setAddress(restaurant.getAddress());
                restaurantToUpdate.setContactNumber(restaurant.getContactNumber());
                restaurantToUpdate.setPhotos(restaurant.getPhotos());
                restaurantToUpdate.setAcceptReservation(restaurant.getAcceptReservation());
                restaurantToUpdate.setTableConfiguration(restaurant.getTableConfiguration());
                restaurantToUpdate.setCreditAmount(restaurant.getCreditAmount());
                restaurantToUpdate.setDescription(restaurant.getDescription());
                
                return restaurantToUpdate.getUseId();
                
            }
            else
            {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        }
        else
        {
            throw new RestaurantNotFoundException("Product ID not provided for product to be updated");
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Restaurant>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
