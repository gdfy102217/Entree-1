/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import entity.Reservation;
import entity.Restaurant;
import entity.TableConfiguration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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
import util.enumeration.TableSize;
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
    public List<Reservation> retrieveReservationsByRestaurantId(Long restaurantId, LocalDate date, Double reservationTime)
    {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.restaurant.userId = :inRestaurantId AND r.reservationDate = :inDate AND r.reservationTime = :inReservationTime");
        query.setParameter("inRestaurantId", restaurantId);
        query.setParameter("inReservationTime", reservationTime);
        query.setParameter("inDate", date);
        List<Reservation> reservations = query.getResultList();
        
        for(Reservation reservation:reservations)
        {
//            reservation.getCategoryEntity();
//            reservation.getTagEntities().size();
        }
        
        return reservations;
    }
    
    @Override

    public List<Integer> retrieveAvailableTableByTime(Long restaurantId, String date, double time) throws RestaurantNotFoundException{
        TableConfiguration tc = restaurantSessionBeanLocal.retrieveRestaurantById(restaurantId).getTableConfiguration();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate d = LocalDate.parse(date, dtf);
        List<Reservation> reservationList = retrieveReservationsByRestaurantId(restaurantId, d, time);
        

        
        int numOfLargeAvailable = tc.getNumOfLargeTable();
        int numOfMediumAvailable = tc.getNumOfMediumTable();
        int numOfSmallAvailable = tc.getNumOfSmallTable();
        
        for (Reservation r: reservationList)
        {
            if (r.getTableSizeAssigned().equals(TableSize.LARGE)) {
                numOfLargeAvailable -= 1;
            } else if (r.getTableSizeAssigned().equals(TableSize.MEDIUM)) {
                numOfMediumAvailable -= 1;
            } else if (r.getTableSizeAssigned().equals(TableSize.SMALL)) {
                numOfSmallAvailable -= 1;
            }
        }
        
        List<Integer> list = new ArrayList<>();
        list.add(numOfSmallAvailable);
        list.add(numOfMediumAvailable);
        list.add(numOfLargeAvailable);
        
        return list;
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
    public List<Reservation> retrieveReservationForCustomer(Long customerId) throws ReservationNotFoundException
    {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.customer.userId = :inCustomerId");
        query.setParameter("inCustomerId", customerId);
        
        List<Reservation> reservations = query.getResultList();
        
//        Reservation reservation = (Reservation) query.getSingleResult();
        
        if(reservations.size() > 0)
        {       
            return reservations;
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
        
        reservationToRemove.getCustomer().getReservations().remove(reservationToRemove);
        reservationToRemove.setCustomer(new Customer());
        reservationToRemove.getRestaurant().getReservations().remove(reservationToRemove);
        reservationToRemove.setRestaurant(new Restaurant());
        
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
