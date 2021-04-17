/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.RestaurantSessionBeanLocal;
import entity.Customer;
import entity.Promotion;
import entity.Restaurant;
import entity.Review;
import java.util.ArrayList;
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
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * REST Web Service
 *
 * @author fengyuan
 */
@Path("Restaurant")
public class RestaurantResource {

    RestaurantSessionBeanLocal restaurantSessionBeanLocal = lookupRestaurantSessionBeanLocal();
    
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of RestaurantResource
     */
    public RestaurantResource() {
    }
    
    @Path("retrieveAllRestaurants")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllRestaurants()
    {
        try
        {
            List<Restaurant> restaurants = restaurantSessionBeanLocal.retrieveAllRestaurants();
            
            for (Restaurant restaurant: restaurants){
                restaurant.setBankAccount(null);
                restaurant.getReservations().clear();
                restaurant.getTransactions().clear();
                restaurant.getCustomerVouchers().clear();
                
                for(Promotion promo:restaurant.getPromotions()){
                    promo.setRestaurant(null);
                }
//                for(Reservation res:restaurant.getReservations()){
//                    res.setRestaurant(null);
////                    res.setCustomer(customer);
//                }
                for(Review rw: restaurant.getReviews()){
                    rw.setReceiver(null);
                    Customer tempCust = new Customer();
                    tempCust.setFirstName(rw.getCreater().getFirstName());
                    tempCust.setLastName(rw.getCreater().getLastName());
                    rw.setCreater(tempCust);
                }
//                for(SaleTransaction t: restaurant.getTransactions()){
//                    t.setRestaurant(null);
//                    
//                }
//                for(CustomerVoucher cv: restaurant.getCustomerVouchers()){
//                    cv.setRestaurant(null);
//                    cv.set
//                }
                
                
                
                
//                restaurant.getReservations().clear();
//                restaurant.getReviews().clear();
//                restaurant.getTransactions().clear();
//                restaurant.getCustomerVouchers().clear();
            }
            
            GenericEntity<List<Restaurant>> genericEntity = new GenericEntity<List<Restaurant>>(restaurants) {
            };

            return Response.status(Status.OK).entity(genericEntity).build();
        }
        catch(Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @Path("retrieveRestaurantDetails")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveRestaurantDetails(@QueryParam("restaurantId") Long restaurantId)
    {
        try
        {
            Restaurant restaurant = restaurantSessionBeanLocal.retrieveRestaurantById(restaurantId);

            restaurant.setBankAccount(null);
            restaurant.setPassword(null);
            restaurant.getReservations().clear();
            
            for (Promotion promotion: restaurant.getPromotions())
            {
                promotion.setRestaurant(null);
            }
            
            List<Review> dummyReviewList = new ArrayList<>();
            for (Review review: restaurant.getReviews())
            {
                Review dummyReview = new Review();
                
                Restaurant dummyReceiver = new Restaurant();
                dummyReceiver.setName(review.getReceiver().getName());
                dummyReceiver.setId(review.getReceiver().getId());
                dummyReview.setReceiver(dummyReceiver);
                
                Customer dummyCreater = new Customer();
                dummyCreater.setId(review.getCreater().getId());
                dummyCreater.setFirstName(review.getCreater().getFirstName());
                dummyCreater.setLastName(review.getCreater().getLastName());
                dummyReview.setCreater(dummyCreater);
                
                dummyReview.setContent(review.getContent());
                dummyReview.setRating(review.getRating());
                dummyReview.setPhotos(review.getPhotos());
                dummyReview.setTimeOfCreation(review.getTimeOfCreation());
                
                dummyReviewList.add(dummyReview);
            }
            restaurant.setReviews(dummyReviewList);

            restaurant.getTransactions().clear();
            restaurant.getCustomerVouchers().clear();


            GenericEntity<Restaurant> genericEntity = new GenericEntity<Restaurant>(restaurant) {
            };

            return Response.status(Status.OK).entity(genericEntity).build();
        }
        catch(Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    private RestaurantSessionBeanLocal lookupRestaurantSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (RestaurantSessionBeanLocal) c.lookup("java:global/RestaurantReview/RestaurantReview-ejb/RestaurantSessionBean!ejb.session.stateless.RestaurantSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    
}
