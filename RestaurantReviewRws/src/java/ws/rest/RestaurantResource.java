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
import entity.SaleTransaction;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
                    tempCust.setFirstName(rw.getCreator().getFirstName());
                    tempCust.setLastName(rw.getCreator().getLastName());
                    rw.setCreator(tempCust);
                    rw.setCustomerLikes(null);
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
        
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
    Date date = new Date();  
    System.out.println(formatter.format(date));  
        
        try
        {
            Restaurant restaurant = restaurantSessionBeanLocal.retrieveRestaurantById(restaurantId);

            restaurant.setBankAccount(null);
            restaurant.setPassword(null);
            restaurant.getReservations().clear();
            List<Promotion> tempPromo = new ArrayList<>();
            
            for (Promotion promotion: restaurant.getPromotions())
            {
//                System.out.println("Promotion img: " + promotion.getPhoto());
//                System.out.println("New Date: " + today.toString());
                promotion.setRestaurant(null);
                if (promotion.getEndDate().after(new Date()))
                {
                    tempPromo.add(promotion);
                    System.out.println("Promotion Id: " + promotion.getPromotionId());
                    System.out.println("Promotion not expired!!!!");
                }
            }
            
            restaurant.setPromotions(tempPromo);
            
            List<Review> dummyReviewList = new ArrayList<>();
            for (Review review: restaurant.getReviews())
            {
                Review dummyReview = new Review();
                
                Restaurant dummyReceiver = new Restaurant();
                dummyReceiver.setName(review.getReceiver().getName());
                dummyReceiver.setId(review.getReceiver().getId());
                dummyReview.setReceiver(dummyReceiver);
                
                Customer dummyCreater = new Customer();
                dummyCreater.setId(review.getCreator().getId());
                dummyCreater.setFirstName(review.getCreator().getFirstName());
                dummyCreater.setLastName(review.getCreator().getLastName());
                dummyReview.setCreator(dummyCreater);
                
                dummyReview.setReviewId(review.getReviewId());
                dummyReview.setContent(review.getContent());
                dummyReview.setCustomerLikes(new ArrayList<Customer>());
                for (Customer customer: review.getCustomerLikes()){
                    Customer dummyCustomer = new Customer();
                    dummyCustomer.setUserId(customer.getUserId());
                    dummyReview.getCustomerLikes().add(dummyCustomer);
                }
                dummyReview.setRating(review.getRating());
//                dummyReview.setPhotos(review.getPhotos());
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
