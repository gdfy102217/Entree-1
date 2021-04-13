/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import entity.Restaurant;
import entity.Review;
import entity.User;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import util.exception.CreateNewReviewException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.RestaurantNotFoundException;
import util.exception.ReviewExistException;
import util.exception.ReviewNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author fengyuan
 */
@Stateless
public class ReviewSessionBean implements ReviewSessionBeanLocal {

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    @EJB
    private RestaurantSessionBeanLocal restaurantSessionBeanLocal;

    @PersistenceContext(unitName = "RestaurantReview-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public ReviewSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public Review createNewReviewForRestaurant(Review newReview, Long createrId, Long receiverId) 
            throws UnknownPersistenceException, InputDataValidationException, CreateNewReviewException, ReviewExistException
    {
        Set<ConstraintViolation<Review>>constraintViolations = validator.validate(newReview);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                Customer creater = customerSessionBeanLocal.retrieveCustomerById(createrId);
                Restaurant receiver = restaurantSessionBeanLocal.retrieveRestaurantById(receiverId);
                
                em.persist(newReview); 
                newReview.setCreater(creater);
                creater.getReviews().add(newReview);
                newReview.setReceiver(receiver);
                receiver.getReviews().add(newReview);
                
                em.flush();

                return newReview;
            }
            catch(PersistenceException ex)
            {
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                    {
                        throw new ReviewExistException();
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
            catch(RestaurantNotFoundException | CustomerNotFoundException ex)
            {
                throw new CreateNewReviewException("An error has occurred while creating the new review: " + ex.getMessage());
            }
        }
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
//    @Override
//    public Review createNewReviewForCustomer(Review newReview, Long createrId, Long receiverId) 
//            throws UnknownPersistenceException, InputDataValidationException, CreateNewReviewException, ReviewExistException
//    {
//        Set<ConstraintViolation<Review>>constraintViolations = validator.validate(newReview);
//        
//        if(constraintViolations.isEmpty())
//        {
//            try
//            {
//                Customer creater = customerSessionBeanLocal.retrieveCustomerById(createrId);
//                Restaurant receiver = restaurantSessionBeanLocal.retrieveRestaurantById(receiverId);
//                
//                em.persist(newReview); 
//                newReview.setCreater(creater);
//                creater.getReviews().add(newReview);
//                newReview.setReceiver(receiver);
//                receiver.getReviews().add(newReview);
//                
//                em.flush();
//
//                return newReview;
//            }
//            catch(PersistenceException ex)
//            {
//                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
//                {
//                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
//                    {
//                        throw new ReviewExistException();
//                    }
//                    else
//                    {
//                        throw new UnknownPersistenceException(ex.getMessage());
//                    }
//                }
//                else
//                {
//                    throw new UnknownPersistenceException(ex.getMessage());
//                }
//            }
//            catch(CustomerNotFoundException|RestaurantNotFoundException ex)
//            {
//                throw new CreateNewReviewException("An error has occurred while creating the new review: " + ex.getMessage());
//            } 
//        }
//        else
//        {
//            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
//        }
//    }

    @Override
    public List<Review> retrieveAllReviews()
    {
        Query query = em.createQuery("SELECT r FROM Review r ORDER BY r.reviewId ASC");        
        List<Review> reviews = query.getResultList();
        
        for(Review review:reviews)
        {
//            reservation.getCategoryEntity();
//            reservation.getTagEntities().size();
        }
        
        return reviews;
    }
    
    @Override
    public Review retrieveReviewById(Long reviewId) throws ReviewNotFoundException
    {
        Review review = em.find(Review.class, reviewId);
        
        if(review != null)
        {
//            productEntity.getCategoryEntity();
//            productEntity.getTagEntities().size();
            
            return review;
        }
        else
        {
            throw new ReviewNotFoundException("Review ID " + reviewId + " does not exist!");
        }               
    }
    
    @Override
    public List<Review> retrieveReviewsByRestaurantId(Long restaurantId)
    {
        Query query = em.createQuery("SELECT r FROM Review r WHERE r.receiver.userId = :inRestaurantId ORDER BY r.reviewId ASC");
        query.setParameter("inRestaurantId", restaurantId);
        List<Review> reviews = query.getResultList();
        
        return reviews;
    }
    
    @Override
    public List<Review> retrieveReviewsByCustomerId(Long customerId)
    {
        Query query = em.createQuery("SELECT r FROM Review r WHERE r.creater.userId = :inCustomerId ORDER BY r.reviewId ASC");
        query.setParameter("inCustomerId", customerId);
        List<Review> reviews = query.getResultList();
        
        return reviews;
    }
    
    @Override
    public Long updateReview(Review review) throws InputDataValidationException, ReviewNotFoundException
    {
        if(review != null && review.getReviewId()!= null)
        {
            Set<ConstraintViolation<Review>>constraintViolations = validator.validate(review);
        
            if(constraintViolations.isEmpty())
            {
                Review reviewToUpdate = retrieveReviewById(review.getReviewId());

                    reviewToUpdate.setContent(review.getContent());
                    reviewToUpdate.setNumOfLikes(review.getNumOfLikes());
                    reviewToUpdate.setPhotos(review.getPhotos());
                    reviewToUpdate.setRating(review.getRating());
                    reviewToUpdate.setCreater(review.getCreater());
                    reviewToUpdate.setReceiver(review.getReceiver());
                    reviewToUpdate.setTimeOfCreation(review.getTimeOfCreation());
                    return reviewToUpdate.getReviewId();
            }
            else
            {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        }
        else 
        {
            throw new ReviewNotFoundException("Review ID not provided for review to be updated");
        }
    }
    
    @Override
    public void deleteReview(Long reviewId) throws ReviewNotFoundException
    {
        Review reviewToRemove = retrieveReviewById(reviewId);
        
        reviewToRemove.getCreater().getReviews().remove(reviewToRemove);
        reviewToRemove.setCreater(new Customer());
        reviewToRemove.getReceiver().getReviews().remove(reviewToRemove);
        reviewToRemove.setReceiver(new Restaurant());
        
        em.remove(reviewToRemove);

    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Review>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }

}
