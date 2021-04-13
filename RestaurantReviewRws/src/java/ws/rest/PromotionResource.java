/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.PromotionSessionBeanLocal;
import entity.Customer;
import entity.Promotion;
import entity.Restaurant;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * REST Web Service
 *
 * @author fengyuan
 */
@Path("Promotion")
public class PromotionResource {

    PromotionSessionBeanLocal promotionSessionBeanLocal = lookupPromotionSessionBeanLocal();

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of PromotionResource
     */
    public PromotionResource() {
    }

    @Path("retrieveAllPromotions")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllPromotions()
    {
        try
        {
            List<Promotion> promotions = promotionSessionBeanLocal.retrieveAllPromotions();
            
            for (Promotion p: promotions)
            {
                Restaurant dummyRestaurant = new Restaurant();
                dummyRestaurant.setId(p.getRestaurant().getId());
                dummyRestaurant.setName(p.getRestaurant().getName());
                p.setRestaurant(dummyRestaurant);
            }

            GenericEntity<List<Promotion>> genericEntity = new GenericEntity<List<Promotion>>(promotions) {
            };

            return Response.status(Status.OK).entity(genericEntity).build();
        }
        catch(Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    private PromotionSessionBeanLocal lookupPromotionSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (PromotionSessionBeanLocal) c.lookup("java:global/RestaurantReview/RestaurantReview-ejb/PromotionSessionBean!ejb.session.stateless.PromotionSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
