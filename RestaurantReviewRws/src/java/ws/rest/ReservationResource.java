/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.ReservationSessionBeanLocal;
import entity.Customer;
import entity.Reservation;
import entity.Restaurant;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import util.exception.ReservationNotFoundException;

/**
 * REST Web Service
 *
 * @author fengyuan
 */
@Path("Reservation")
public class ReservationResource {

    ReservationSessionBeanLocal reservationSessionBeanLocal = lookupReservationSessionBeanLocal();

    
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ReservationResource
     */
    public ReservationResource() {
    }

    @Path("retrieveAllReservations")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllReservations()
    {
        try
        {
            List<Reservation> reservations = reservationSessionBeanLocal.retrieveAllReservations();

            GenericEntity<List<Reservation>> genericEntity = new GenericEntity<List<Reservation>>(reservations) {
            };

            return Response.status(Status.OK).entity(genericEntity).build();
        }
        catch(Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    /**
     *
     * @param customerId
     * @return
     */
    @Path("retrieveMyReservationForCustomer")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveMyReservationForCustomer(@QueryParam("customerId") Long customerId)
    {
        try
        {
            Reservation reservation = reservationSessionBeanLocal.retrieveReservationForCustomer(customerId);
            
            Customer dummyCustomer = new Customer();
            dummyCustomer.setId(customerId);
            dummyCustomer.setFirstName(reservation.getCustomer().getFirstName());
            dummyCustomer.setLastName(reservation.getCustomer().getLastName());
            reservation.setCustomer(dummyCustomer);
            
            //detach its restaurant with other entities
            Restaurant dummyRestaurant = new Restaurant();
            dummyRestaurant.setId(reservation.getRestaurant().getId());
            dummyRestaurant.setName(reservation.getRestaurant().getName());
            reservation.setRestaurant(dummyRestaurant);
                     
            return Response.status(Status.OK).entity(reservation).build();
        }
        catch(ReservationNotFoundException ex)
        {
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        }
        catch(Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
        
    @Path("retrieveRestaurantAvailableTableByTime")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveRestaurantAvailableTableByTime(@QueryParam("restaurantId") Long restaurantId, @QueryParam("date") long date, @QueryParam("time") long time)
    {
        try
        {
            int[] availabilityArr = reservationSessionBeanLocal.retrieveAvailableTableByTime(restaurantId, date, time);
            System.out.println(Arrays.toString(availabilityArr));
            
            JsonObject availabilityJson = Json.createObjectBuilder()
                    .add("numOfLargeTable", availabilityArr[0])
                    .add("numOfMediumTable", availabilityArr[1])
                    .add("numOfSmallTable", availabilityArr[2])
                    .build();
            
            System.out.println(availabilityJson.toString());
            
//            GenericEntity<JsonObject> genericEntity = new GenericEntity<JsonObject>(res) {
//            };

            return Response.status(Status.OK).entity(availabilityJson).build();
        }
        catch(Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createNewReservation(Reservation newReservation, @QueryParam("customerId") Long customerId,
            @QueryParam("restaurantId") Long restaurantId)
    {
        if(newReservation != null)
        {
            try
            {
                Reservation reservation = reservationSessionBeanLocal.createNewReservation(newReservation, customerId, restaurantId);

                return Response.status(Response.Status.OK).entity(reservation.getReservationId()).build();
            }				
            catch(Exception ex)
            {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid create new reservation request").build();
        }
    }
    
    @DELETE
    @Path("deleteReservation")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteReservation(@QueryParam("reservationId") Long reservationId)
    {
        if(reservationId != null)
        {
            try
            {
                reservationSessionBeanLocal.deleteReservation(reservationId);

                return Response.status(Response.Status.OK).entity("Reservation No." + reservationId + " is deleted!").build();
            }
            catch(Exception ex)
            {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid delete reservation request").build();
        }
    }

    private ReservationSessionBeanLocal lookupReservationSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (ReservationSessionBeanLocal) c.lookup("java:global/RestaurantReview/RestaurantReview-ejb/ReservationSessionBean!ejb.session.stateless.ReservationSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
