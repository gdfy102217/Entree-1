/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.CreateNewReservationException;
import util.exception.InputDataValidationException;
import util.exception.ReservationExistException;
import util.exception.ReservationNotFoundException;
import util.exception.RestaurantNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author fengyuan
 */
@Local
public interface ReservationSessionBeanLocal {

    public Reservation createNewReservation(Reservation newReservation, Long customerId, Long restaurantId) throws UnknownPersistenceException, InputDataValidationException, CreateNewReservationException, ReservationExistException;

    public List<Reservation> retrieveAllReservations();

    public void deleteReservation(Long reservationId) throws ReservationNotFoundException;

    public Reservation retrieveReservationById(Long reservationId) throws ReservationNotFoundException;

    public List<Reservation> retrieveReservationForCustomer(Long customerId) throws ReservationNotFoundException;

    public List<Integer> retrieveAvailableTableByTime(Long restaurantId, String date, double time) throws RestaurantNotFoundException;

    public List<Reservation> retrieveReservationsByRestaurantId(Long restaurantId, LocalDate date, Double reservationTime);
    
}
