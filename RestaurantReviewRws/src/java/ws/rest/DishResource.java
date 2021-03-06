/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.DishSessionBeanLocal;
import entity.Dish;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import util.exception.DishNotFoundException;

/**
 * REST Web Service
 *
 * @author fengyuan
 */
@Path("Dish")
public class DishResource {

    DishSessionBeanLocal dishSessionBeanLocal = lookupDishSessionBeanLocal();
    
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of DishResource
     */
    public DishResource() {
    }
    
    /**
     *
     * @param restaurantId
     * @return
     */
    @Path("retrieveDishesByRestaurant")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveDishesByRestaurant(@QueryParam("restaurantId") Long restaurantId)
    {
        try
        {
            List<Dish> dishes = dishSessionBeanLocal.retrieveAllDishesForParticularRestaurant(restaurantId);
            
            if (dishes.isEmpty()) 
            {
                return Response.status(Status.UNAUTHORIZED).entity("No dishes found").build();
            }
            
            GenericEntity<List<Dish>> genericEntity = new GenericEntity<List<Dish>>(dishes) {
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
     * @param dishId
     * @return
     */
    @Path("retrieveDishDetails")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveDishDetails(@QueryParam("dishId") Long dishId)
    {
        try
        {
            Dish dish = dishSessionBeanLocal.retrieveDishById(dishId);
            
            GenericEntity<Dish> genericEntity = new GenericEntity<Dish>(dish) {
            };

            return Response.status(Status.OK).entity(genericEntity).build();
        } 
        catch(DishNotFoundException ex)
        {
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        }
        catch(Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    

    private DishSessionBeanLocal lookupDishSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (DishSessionBeanLocal) c.lookup("java:global/RestaurantReview/RestaurantReview-ejb/DishSessionBean!ejb.session.stateless.DishSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    
}
