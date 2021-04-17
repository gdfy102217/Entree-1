/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.ReviewSessionBeanLocal;
import entity.Customer;
import entity.Restaurant;
import entity.Review;
import java.util.ArrayList;
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
import util.exception.CustomerInLikeListException;
import util.exception.ReviewNotFoundException;

/**
 * REST Web Service
 *
 * @author fengyuan
 */
@Path("Review")
public class ReviewResource
{

    CustomerSessionBeanLocal customerSessionBeanLocal = lookupCustomerSessionBeanLocal();

    ReviewSessionBeanLocal reviewSessionBeanLocal = lookupReviewSessionBeanLocal();

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ReviewResource
     */
    public ReviewResource()
    {
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

            Customer dummyCreater = new Customer();
            dummyCreater.setId(CustomerId);
            dummyCreater.setFirstName(customerSessionBeanLocal.retrieveCustomerById(CustomerId).getFirstName());
            dummyCreater.setLastName(customerSessionBeanLocal.retrieveCustomerById(CustomerId).getLastName());

            for (Review r : myReviews)
            {
                r.setCreator(dummyCreater);
                Restaurant dummyReceiver = new Restaurant();
                dummyReceiver.setId(r.getReceiver().getId());
                dummyReceiver.setName(r.getReceiver().getName());
                r.setReceiver(dummyReceiver);

                r.setCustomerLikes(new ArrayList<Customer>());
                for (Customer customer : r.getCustomerLikes())
                {
                    Customer dummyCustomer = new Customer();
                    dummyCustomer.setUserId(customer.getUserId());
                    r.getCustomerLikes().add(dummyCustomer);
                }
            }

            GenericEntity<List<Review>> genericEntity = new GenericEntity<List<Review>>(myReviews)
            {
            };

            return Response.status(Status.OK).entity(genericEntity).build();
        } catch (Exception ex)
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

            Customer dummyCreator = new Customer();
            dummyCreator.setId(review.getCreator().getId());
            dummyCreator.setFirstName(review.getCreator().getFirstName());
            dummyCreator.setLastName(review.getCreator().getLastName());
            review.setCreator(dummyCreator);

            Restaurant dummyReceiver = new Restaurant();
            dummyReceiver.setId(review.getReceiver().getId());
            dummyReceiver.setName(review.getReceiver().getName());
            review.setReceiver(dummyReceiver);
            
            List<Customer> tempCustLikes = new ArrayList<>();

            for (Customer customer : review.getCustomerLikes())
            {
                Review dummyReview = new Review();
                dummyReview.setCustomerLikes(new ArrayList<Customer>());

                Customer dummyCustomer = new Customer();
                dummyCustomer.setUserId(customer.getUserId());
                
                dummyReview.getCustomerLikes().add(dummyCustomer);
                tempCustLikes.add(dummyCustomer);
            }
            review.setCustomerLikes(tempCustLikes);

            return Response.status(Status.OK).entity(review).build();
        } catch (ReviewNotFoundException ex)
        {
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        } catch (Exception ex)
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
        if (newReview != null)
        {
            try
            {
                Review newReviewCreated = reviewSessionBeanLocal.createNewReviewForRestaurant(newReview, customerId, restaurantId);

                return Response.status(Response.Status.OK).entity(newReviewCreated.getReviewId()).build();
            } catch (Exception ex)
            {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        } else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid create new review request").build();
        }
    }

    @Path("reviewUpdate")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response reviewUpdate(Review reviewToUpdate)
    {
        if (reviewToUpdate != null)
        {
            try
            {
                Long customerToUpdateId = reviewSessionBeanLocal.updateReview(reviewToUpdate);

                return Response.status(Response.Status.OK).entity(customerToUpdateId).build();
            } catch (Exception ex)
            {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        } else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid update review request").build();
        }
    }

    @Path("addCustomerToLikeList")
    @GET
    public Response addCustomerToLikeList(@QueryParam("customerId") Long customerId, @QueryParam("reviewId") Long reviewId)
    {
        if (customerId != null && reviewId != null)
        {
            try
            {
                reviewSessionBeanLocal.addCustomerToLikeList(customerId, reviewId);

                return Response.status(Response.Status.OK).entity("Add customer to list of like successfully!").build();
            } catch (CustomerInLikeListException ex)
            {
                String res = "Customer (ID: " + customerId + ") already like reivew (ID: " + reviewId + ")!";
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(res).build();
            } catch (Exception ex)
            {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        } else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid add like request").build();
        }
    }

    @DELETE
    @Path("deleteReview")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteReview(@QueryParam("reviewId") Long reviewId)
    {
        if (reviewId != null)
        {
            try
            {
                reviewSessionBeanLocal.deleteReview(reviewId);

                return Response.status(Response.Status.OK).entity("Review " + reviewId + " is deleted!").build();
            } catch (Exception ex)
            {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        } else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid delete review request").build();
        }
    }

    private ReviewSessionBeanLocal lookupReviewSessionBeanLocal()
    {
        try
        {
            javax.naming.Context c = new InitialContext();
            return (ReviewSessionBeanLocal) c.lookup("java:global/RestaurantReview/RestaurantReview-ejb/ReviewSessionBean!ejb.session.stateless.ReviewSessionBeanLocal");
        } catch (NamingException ne)
        {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private CustomerSessionBeanLocal lookupCustomerSessionBeanLocal()
    {
        try
        {
            javax.naming.Context c = new InitialContext();
            return (CustomerSessionBeanLocal) c.lookup("java:global/RestaurantReview/RestaurantReview-ejb/CustomerSessionBean!ejb.session.stateless.CustomerSessionBeanLocal");
        } catch (NamingException ne)
        {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

}
