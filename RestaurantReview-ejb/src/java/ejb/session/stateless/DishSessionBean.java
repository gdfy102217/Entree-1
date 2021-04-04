/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Dish;
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
import util.exception.CreateNewDishException;
import util.exception.DeleteDishException;
import util.exception.DishExistException;
import util.exception.DishNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.RestaurantNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author fengyuan
 */
@Stateless
public class DishSessionBean implements DishSessionBeanLocal {
    
    @EJB
    private RestaurantSessionBeanLocal restaurantSessionBeanLocal;
    
    @PersistenceContext(unitName = "RestaurantReview-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public DishSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public Dish createNewDish(Dish newDish, Long restaurantId) 
            throws UnknownPersistenceException, InputDataValidationException, CreateNewDishException, DishExistException
    {
        Set<ConstraintViolation<Dish>>constraintViolations = validator.validate(newDish);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                Restaurant restaurant = restaurantSessionBeanLocal.retrieveRestaurantById(restaurantId);
                
                em.persist(newDish);
                newDish.setRestaurant(restaurant);
                restaurant.getDishs().add(newDish);
                
                em.flush();

                return newDish;
            }
            catch(PersistenceException ex)
            {
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                    {
                        throw new DishExistException();
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
                throw new CreateNewDishException("An error has occurred while creating the new product: " + ex.getMessage());
            }
        }
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public List<Dish> retrieveAllDishes()
    {
        Query query = em.createQuery("SELECT d FROM Dish d ORDER BY d.name ASC");        
        List<Dish> dishes = query.getResultList();
        
        for(Dish dish:dishes)
        {
//            reservation.getCategoryEntity();
//            reservation.getTagEntities().size();
        }
        
        return dishes;
    }
    
    @Override
    public List<Dish> retrieveAllDishesForParticularRestaurant(Long restaurantId)
    {
        Query query = em.createQuery("SELECT d FROM Dish d WHERE d.restaurantId =:inRestaurantId ORDER BY d.name ASC");
        query.setParameter("inRestaurantId", restaurantId);        
        List<Dish> dishes = query.getResultList();
        
        for(Dish dish:dishes)
        {
//            reservation.getCategoryEntity();
//            reservation.getTagEntities().size();
        }
        
        return dishes;
    }
    
    @Override
    public Dish retrieveDishById(Long dishId) throws DishNotFoundException
    {
        Dish dish = em.find(Dish.class, dishId);
        
        if(dish != null)
        {
//            productEntity.getCategoryEntity();
//            productEntity.getTagEntities().size();
            
            return dish;
        }
        else
        {
            throw new DishNotFoundException("Dish ID " + dishId + " does not exist!");
        }               
    }
    
    @Override
    public void updateDish(Dish dish) throws DishNotFoundException, InputDataValidationException
    {
        if(dish != null && dish.getDishId()!= null)
        {
            Set<ConstraintViolation<Dish>>constraintViolations = validator.validate(dish);
        
            if(constraintViolations.isEmpty())
            {
                Dish dishToUpdate = retrieveDishById(dish.getDishId());

                    dishToUpdate.setName(dish.getName());
                    dishToUpdate.setDescription(dish.getDescription());
                    dishToUpdate.setPhoto(dish.getPhoto());
                    dishToUpdate.setPrice(dish.getPrice());
                    dishToUpdate.setRecommended(dish.getRecommended());
            }
            else
            {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        }
        else 
        {
            throw new DishNotFoundException("Dish ID not provided for dish to be updated");
        }
    }
    
    @Override
    public void deleteDish(Long dishId) throws DishNotFoundException, DeleteDishException
    {
        Dish dishToRemove = retrieveDishById(dishId);
        
        em.remove(dishToRemove);

    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Dish>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
    
    
}
