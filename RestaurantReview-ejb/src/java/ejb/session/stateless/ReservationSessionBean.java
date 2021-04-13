/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import entity.Reservation;
import entity.Restaurant;
import java.time.LocalDateTime;
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
import util.exception.CreateNewReservationException;
import util.exception.CustomerNotFoundException;
import util.exception.DeleteReservationException;
import util.exception.InputDataValidationException;
import util.exception.ReservationExistException;
import util.exception.ReservationNotFoundException;
import util.exception.RestaurantNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author fengyuan
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanLocal {

    @EJB
    private RestaurantSessionBeanLocal restaurantSessionBeanLocal;

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    @PersistenceContext(unitName = "RestaurantReview-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public ReservationSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public Reservation createNewReservation(Reservation newReservation, Long customerId, Long restaurantId) 
            throws UnknownPersistenceException, InputDataValidationException, CreateNewReservationException, ReservationExistException
    {
        Set<ConstraintViolation<Reservation>>constraintViolations = validator.validate(newReservation);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                Customer customer = customerSessionBeanLocal.retrieveCustomerById(customerId);
                Restaurant restaurant = restaurantSessionBeanLocal.retrieveRestaurantById(restaurantId);
                newReservation.setTimeOfCreation(LocalDateTime.now());
                em.persist(newReservation);
                newReservation.setCustomer(customer);
                customer.getReservations().add(newReservation);
                newReservation.setRestaurant(restaurant);
                restaurant.getReservations().add(newReservation);
                
                
                em.flush();

                return newReservation;
            }
            catch(PersistenceException ex)
            {
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                    {
                        throw new ReservationExistException();
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
            catch(CustomerNotFoundException | RestaurantNotFoundException ex)
            {
                throw new CreateNewReservationException("An error has occurred while creating the new product: " + ex.getMessage());
            }
        }
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public List<Reservation> retrieveAllReservations()
    {
        Query query = em.createQuery("SELECT r FROM Reservation r ORDER BY r.reservationTime ASC");        
        List<Reservation> reservations = query.getResultList();
        
        for(Reservation reservation:reservations)
        {
//            reservation.getCategoryEntity();
//            reservation.getTagEntities().size();
        }
        
        return reservations;
    }
    
    @Override
    public Reservation retrieveReservationById(Long reservationId) throws ReservationNotFoundException
    {
        Reservation reservation = em.find(Reservation.class, reservationId);
        
        if(reservation != null)
        {
//            productEntity.getCategoryEntity();
//            productEntity.getTagEntities().size();
            
            return reservation;
        }
        else
        {
            throw new ReservationNotFoundException("Reservation ID " + reservationId + " does not exist!");
        }               
    }
    
    @Override
    public Reservation retrieveReservationForCustomer(Long customerId) throws ReservationNotFoundException
    {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.customer.userId = :inCustomerId");
        query.setParameter("inCustomerId", customerId);
        Reservation reservation = (Reservation) query.getSingleResult();
        
        if(reservation.getReservationId() != null)
        {       
            return reservation;
        }
        else
        {
            throw new ReservationNotFoundException("Reservation associated with customer ID " + customerId + " does not exist!");
        }               
    }
    
    @Override
    public void deleteReservation(Long reservationId) throws ReservationNotFoundException
    {
        Reservation reservationToRemove = retrieveReservationById(reservationId);
        
        em.remove(reservationToRemove);
    }
    
//    public List<Reservation> retrieveReservationByRestaurantId(Long restaurantId)
//    {
//        List<Reservation> reservations = em.createQuery("SELECT r FROM reservation r WHERE r.")
//    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Reservation>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
