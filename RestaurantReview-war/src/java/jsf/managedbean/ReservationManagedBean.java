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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.event.SelectEvent;
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
    
    private List<Reservation> allReservations;
    
    private List<Reservation> selectedReservations;
    
    private Reservation selectedReservation;
    
    private Restaurant currRestaurant;
    
    private List<Reservation> filtedReservations;
    
    private Date selectedDate;
    
    public ReservationManagedBean()
    {
        selectedReservation = new Reservation();
        selectedDate = new Date();
        allReservations = new ArrayList<>();
        selectedReservations = new ArrayList<>();
    }
    
    @PostConstruct
    public void postConstruct()
    {

            Restaurant currRestaurant = (Restaurant)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentRestaurant");
            System.out.println("current rest: " + currRestaurant.getName());
            allReservations = reservationSessionBeanLocal.retrieveReservationsById(currRestaurant.getUserId());
//             = currRestaurant.getReservations();
            
            for (Reservation res: allReservations)
            {
                System.out.println("TODAT's DATE!!!! " + selectedDate);
                if (res.getReservationDate().equals(getSelectedDate()))
                {
                    selectedReservations.add(res);
                    System.out.println(res);
                }
            }
            

    }
    
    public void viewReservationDetails(ActionEvent event) throws IOException
    {
        setSelectedReservation((Reservation)event.getComponent().getAttributes().get("reservationToView"));
//        System.out.println(selectedReservation.getReservationTime());
//        Long reservationIdToView = (Long)event.getComponent().getAttributes().get("reservationId");
//        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("reservationIdToView", reservationIdToView);
        //FacesContext.getCurrentInstance().getExternalContext().redirect();
    }
    
    public void getSelectedReservationByDate(SelectEvent<Date> event)
    {
        Restaurant currRestaurant = (Restaurant)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentRestaurant");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy,MM.dd");
//        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Date Selected", event.getObject().format(formatter)));
        
        
//        System.out.println("GET selected reservation called!!!!!!" + event.getObject().format(formatter));
        allReservations = reservationSessionBeanLocal.retrieveReservationsById(currRestaurant.getUserId());
        System.out.println("Seleted Date: " + getSelectedDate());
        System.out.println("Size: " + allReservations.size());
        selectedReservations.clear();
        
        for (Reservation res: allReservations)
            {
                if (res.getReservationDate().equals(getSelectedDate()))
                {
                    selectedReservations.add(res);
                }
            }
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
    
    public List<Reservation> getSelectedReservations()
    {
        return selectedReservations;
    }

    public void setSelectedReservations(List<Reservation> selectedReservations)
    {
        this.selectedReservations = selectedReservations;
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

    public Date getSelectedDate()
    {
        return selectedDate;
    }

    public void setSelectedDate(Date selectedDate)
    {
        this.selectedDate = selectedDate;
    }

}
