/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.ReviewSessionBeanLocal;
import entity.Review;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * REST Web Service
 *
 * @author fengyuan
 */
@Path("Review")
public class ReviewResource {

    ReviewSessionBeanLocal reviewSessionBeanLocal = lookupReviewSessionBeanLocal();

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ReviewResource
     */
    public ReviewResource() {
    }
    
    @Path("retrieveMyReviews/{customerId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveMyReviews(@PathParam("customerId") Long CustomerId)
    {
        try
        {
            List<Review> myReviews = reviewSessionBeanLocal.retrieveReviewsByCustomerId(CustomerId);
            
            if (myReviews.isEmpty())
            {
                return Response.status(Status.UNAUTHORIZED).entity("No Reviews Found").build();
            }
            
            for (Review r: myReviews)
            {
                r.setCreater(null);
                r.getReplies().clear();
                r.setOriginalReview(null);
                r.setReceiver(null);
            }

            GenericEntity<List<Review>> genericEntity = new GenericEntity<List<Review>>(myReviews) {
            };

            return Response.status(Status.OK).entity(genericEntity).build();
        }
        catch(Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
//    @PUT
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response createNewReviewForRestaurant(Review newReview, Long customerId, Long restaurantId)
//    {
//        if(newReview != null)
//        {
//            try
//            {
//                Review newReviewCreated = reviewSessionBeanLocal.createNewReviewForRestaurant(newReview, customerId, restaurantId);
//
//                return Response.status(Response.Status.OK).entity(newReviewCreated.getReviewId()).build();
//            }				
//            catch(Exception ex)
//            {
//                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
//            }
//        }
//        else
//        {
//            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid create new review request").build();
//        }
//    }

    private ReviewSessionBeanLocal lookupReviewSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (ReviewSessionBeanLocal) c.lookup("java:global/RestaurantReview/RestaurantReview-ejb/ReviewSessionBean!ejb.session.stateless.ReviewSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    
}
