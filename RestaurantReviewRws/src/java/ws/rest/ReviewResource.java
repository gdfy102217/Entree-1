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
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import util.exception.ReviewNotFoundException;

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
    
    /**
     *
     * @param reviewId
     * @return
     */
    @Path("retrieveReviewById/{reviewId}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveReviewById(@PathParam("reviewId") Long reviewId)
    {
        try
        {
            Review review = reviewSessionBeanLocal.retrieveReviewById(reviewId);
            
            //detach its customer with other entities
            review.getCreater().getCustomerVouchers().clear();
            review.getCreater().getReservations().clear();
            review.getCreater().getReviews().clear();
            review.getCreater().getTransactions().clear();
            review.getCreater().setPassword(null);
            
            //detach its restaurant with other entities
//            reservation.getRestaurant().getTransactions().clear();
//            reservation.getRestaurant().getReviews().clear();
//            reservation.getRestaurant().getReservations().clear();
//            reservation.getRestaurant().getPromotions().clear();
//            reservation.getRestaurant().getDishs().clear();
//            reservation.getRestaurant().getCustomerVouchers().clear();
//            reservation.getRestaurant().setBankAccount(null);
//            reservation.getRestaurant().setPassword(null);

            review.setReceiver(null);
            review.setOriginalReview(null);
            for (Review r: review.getReplies())
            {
                r.setCreater(null);
                r.setReceiver(null);
                r.setOriginalReview(null);
                r.getReplies().clear();
            }
                     
            return Response.status(Status.OK).entity(review).build();
        }
        catch(ReviewNotFoundException ex)
        {
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        }
        catch(Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @Path("createNewReview/{customerId}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewReview(Review newReview, @PathParam("customerId") Long customerId, @QueryParam("restaurantId") Long restaurantId)
    {
        if(newReview != null)
        {
            try
            {
                Review newReviewCreated = reviewSessionBeanLocal.createNewReviewForRestaurant(newReview, customerId, restaurantId);

                return Response.status(Response.Status.OK).entity(newReviewCreated.getReviewId()).build();
            }				
            catch(Exception ex)
            {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid create new review request").build();
        }
    }
    
    @Path("replyReview/{createrId}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response replyReview(Review newReply, @PathParam("createrId") Long createrId, @QueryParam("receiverId") Long receiverId)
    {
        if(newReply != null)
        {
            try
            {
                Review newReviewCreated = reviewSessionBeanLocal.createNewReviewForCustomer(newReply, createrId, receiverId);

                return Response.status(Response.Status.OK).entity(newReviewCreated.getReviewId()).build();
            }				
            catch(Exception ex)
            {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid create new reply request").build();
        }
    }
    
    @Path("reviewUpdate")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response reviewUpdate(Review reviewToUpdate)
    {
        if(reviewToUpdate != null)
        {
            try
            {
                Long customerToUpdateId = reviewSessionBeanLocal.updateReview(reviewToUpdate);

                return Response.status(Response.Status.OK).entity(customerToUpdateId).build();
            }
            catch(Exception ex)
            {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid update review request").build();
        }
    }
    
    @PUT
    @Path("deleteReview")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateReview(Review reviewToUpdate, @QueryParam("reviewId") Long reviewId)
    {
        if(reviewId != null)
        {
            try
            {
                reviewSessionBeanLocal.deleteReview(reviewId);

                return Response.status(Response.Status.OK).entity(reviewId).build();
            }
            catch(Exception ex)
            {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid delete review request").build();
        }
    }
    
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
