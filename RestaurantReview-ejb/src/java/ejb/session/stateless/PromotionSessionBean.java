/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Promotion;
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
import util.exception.CreateNewPromotionException;
import util.exception.InputDataValidationException;
import util.exception.PromotionExistException;
import util.exception.PromotionNotFoundException;
import util.exception.RestaurantNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdatePromotionException;

/**
 *
 * @author fengyuan
 */
@Stateless
public class PromotionSessionBean implements PromotionSessionBeanLocal {

    @EJB
    private RestaurantSessionBeanLocal restaurantSessionBeanLocal;

    @PersistenceContext(unitName = "RestaurantReview-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public PromotionSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public Promotion createNewPromotion(Promotion newPromotion, Long restaurantId) 
            throws UnknownPersistenceException, InputDataValidationException, CreateNewPromotionException, PromotionExistException
    {
        Set<ConstraintViolation<Promotion>>constraintViolations = validator.validate(newPromotion);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                Restaurant restaurant = restaurantSessionBeanLocal.retrieveRestaurantById(restaurantId);
                
                em.persist(newPromotion);
                restaurant.getPromotions().add(newPromotion);
                
                em.flush();

                return newPromotion;
            }
            catch(PersistenceException ex)
            {
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                    {
                        throw new PromotionExistException();
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
                throw new CreateNewPromotionException("An error has occurred while creating the new promotion: " + ex.getMessage());
            }
        }
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public List<Promotion> retrieveAllPromotions()
    {
        Query query = em.createQuery("SELECT p FROM Promotion p ORDER BY p.promotionId ASC");        
        List<Promotion> promotions = query.getResultList();
        
        for(Promotion promotion:promotions)
        {
//            reservation.getCategoryEntity();
//            reservation.getTagEntities().size();
        }
        
        return promotions;
    }
    
    @Override
    public Promotion retrievePromotionById(Long promotionId) throws PromotionNotFoundException
    {
        Promotion promotion = em.find(Promotion.class, promotionId);
        
        if(promotion != null)
        {
//            productEntity.getCategoryEntity();
//            productEntity.getTagEntities().size();
            
            return promotion;
        }
        else
        {
            throw new PromotionNotFoundException("Promotion ID " + promotionId + " does not exist!");
        }               
    }
    
    @Override
    public List<Promotion> retrievePromotionByRestaurantId(Long restaurantId)
    {
        Query query = em.createQuery("SELECT p FROM Promotion p WHERE p.restaurant.useId = :inRestaurantId ORDER BY p.promotionId ASC");
        query.setParameter("inRestaurantId", restaurantId);
        List<Promotion> promotions = query.getResultList();
        
        return promotions;
    }
    
    @Override
    public void updatePromotion(Promotion promotion) throws PromotionNotFoundException, UpdatePromotionException, InputDataValidationException
    {
        Set<ConstraintViolation<Promotion>>constraintViolations = validator.validate(promotion);

        if(constraintViolations.isEmpty())
        {
            Promotion promotionToUpdate = retrievePromotionById(promotion.getPromotionId());

            if(promotionToUpdate.getPromotionId().equals(promotion.getPromotionId()))
            {

                promotionToUpdate.setTitle(promotion.getTitle());
                promotionToUpdate.setContent(promotion.getContent());
                promotionToUpdate.setStartDate(promotion.getStartDate());
                promotionToUpdate.setEndDate(promotion.getEndDate());

            }
            else
            {
                throw new UpdatePromotionException("ID of promotion to be updated does not match the existing record");
            }
        }
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }

    }
    
    @Override
    public void deletePromotion(Long promotionId) throws PromotionNotFoundException
    {
        Promotion promotionToRemove = retrievePromotionById(promotionId);
        
        em.remove(promotionToRemove);

    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Promotion>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
