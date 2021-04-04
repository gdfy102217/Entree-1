/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.ReservationSessionBeanLocal;
import entity.Reservation;
import entity.Restaurant;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author zhiliangwang
 */
@Named(value = "reservationManagedBean")
@ViewScoped
public class ReservationManagedBean implements Serializable
{

    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;
    
    private List<Reservation> reservations;
    
    private Reservation selectedReservation;
    
    private Restaurant currRestaurant;
    
    private List<Reservation> filtedReservations;
    
    public ReservationManagedBean()
    {
        selectedReservation = new Reservation();
    }
    
    @PostConstruct
    public void postConstruct()
    {

            Restaurant currRestaurant = (Restaurant)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentRestaurant");
            System.out.println("current rest: " + currRestaurant.getName());
            reservations = currRestaurant.getReservations();
            
            
//            List<Reservation> tempList = currRestaurant.getReservations();
            
            // Get today's reservation list only
//            for(Reservation resev: tempList)
//            {
//                if(resev.getReservationTime().equals())
//            }
            
            
            
//            List<Reservation> tempList = currRestaurant.getReservations();
//            
//            for (Reservation rest: tempList)
//            {
//                if(rest.getReservationTime().equals(rest))
//            }
            
            //reservationSessionBeanLocal.retrieveReservationById(currRest.getId());

    }
    
    public void viewReservationDetails(ActionEvent event) throws IOException
    {
        setSelectedReservation((Reservation)event.getComponent().getAttributes().get("reservationToView"));
//        System.out.println(selectedReservation.getReservationTime());
//        Long reservationIdToView = (Long)event.getComponent().getAttributes().get("reservationId");
//        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("reservationIdToView", reservationIdToView);
        //FacesContext.getCurrentInstance().getExternalContext().redirect();
    }
    
    public boolean filterByDate(Object value, Object filter, Locale locale) 
    {
        System.out.println("Filter function called!");
        if( filter == null ) {
            return true;
        }

        if( value == null ) {
            return false;
        }

        Date dt2 = (Date) filter;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", locale);
        String date1 = sdf.format(value);
        String date2 = sdf.format(dt2);
        boolean status = date2.equals(date1);
        return status;
    }
    
    public List<Reservation> getReservations()
    {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations)
    {
        this.reservations = reservations;
    }

    public Reservation getSelectedReservation()
    {
        return selectedReservation;
    }

    public void setSelectedReservation(Reservation selectedReservation)
    {
        this.selectedReservation = selectedReservation;
    }

    public Restaurant getCurrRestaurant()
    {
        return currRestaurant;
    }

    public void setCurrRestaurant(Restaurant currRestaurant)
    {
        this.currRestaurant = currRestaurant;
    }
    
    
    public List<Reservation> getFiltedReservations()
    {
        return filtedReservations;
    }

    public void setFiltedReservations(List<Reservation> filtedReservations)
    {
        this.filtedReservations = filtedReservations;
    }

}
