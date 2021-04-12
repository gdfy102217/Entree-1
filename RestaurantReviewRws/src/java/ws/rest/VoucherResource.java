/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.VoucherSessionBeanLocal;
import entity.CustomerVoucher;
import entity.Voucher;
import java.util.Date;
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
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
@Path("Voucher")
public class VoucherResource {

    VoucherSessionBeanLocal voucherSessionBeanLocal = lookupVoucherSessionBeanLocal();

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of VoucherResource
     */
    public VoucherResource()
    {
    }

    @Path("retrieveMyCustomerVouchers/{customerId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveMyCustomerVouchers(@PathParam("customerId") Long customerId)
    {
        try
        {
            List<CustomerVoucher> myVouchers = voucherSessionBeanLocal.retrieveAllCustomerVouchersByCustomerId(customerId);
            
            for(CustomerVoucher cv: myVouchers)
            {
                cv.setOwner(null);
                cv.setVoucher(null);
                cv.setRestaurant(null);
                cv.setTransaction(null);
            }

            GenericEntity<List<CustomerVoucher>> genericEntity = new GenericEntity<List<CustomerVoucher>>(myVouchers) {
            };

            return Response.status(Status.OK).entity(genericEntity).build();
        }
        catch(Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @Path("retrieveAllVouchers")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllVouchers()
    {
        try
        {
            List<Voucher> vouchers = voucherSessionBeanLocal.retrieveAllVouchers();
            
            for(Voucher v: vouchers)
            {
                v.getCustomerVouchers().clear();
            }

            GenericEntity<List<Voucher>> genericEntity = new GenericEntity<List<Voucher>>(vouchers) {
            };

            return Response.status(Status.OK).entity(genericEntity).build();
        }
        catch(Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @Path("retrieveVoucherDetails/{voucherId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveVoucherDetails(@PathParam("voucherId") Long voucherId)
    {
        try
        {
            Voucher voucher = voucherSessionBeanLocal.retrieveVoucherById(voucherId);
            
            for (CustomerVoucher cv: voucher.getCustomerVouchers())
            {
                cv.setOwner(null);
                cv.setRestaurant(null);
                cv.setTransaction(null);
                cv.setVoucher(null);
            }

            GenericEntity<Voucher> genericEntity = new GenericEntity<Voucher>(voucher) {
            };

            return Response.status(Status.OK).entity(genericEntity).build();
        }
        catch(Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @Path("retrieveCustomerVoucherDetails/{customerVoucherId}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCustomerVoucherDetails(@PathParam("customerVoucherId") Long customerVoucherId)
    {
        try
        {
            CustomerVoucher cv = voucherSessionBeanLocal.retrieveCustomerVoucherById(customerVoucherId);
            
            cv.setRestaurant(null);
            cv.setTransaction(null);
        
//            cv.getRestaurant().getTransactions().clear();
//            cv.getRestaurant().getReviews().clear();
//            cv.getRestaurant().getReservations().clear();
//            cv.getRestaurant().getPromotions().clear();
//            cv.getRestaurant().getDishs().clear();
//            cv.getRestaurant().getCustomerVouchers().clear();
//            cv.getRestaurant().setBankAccount(null);
//            cv.getRestaurant().setPassword(null);

            
//            cv.getTransaction().setBankAccount(null);
//            cv.getTransaction().setCreditCard(null);
//            cv.getTransaction().setCustomer(null);
//            cv.getTransaction().setRestaurant(null);
            
//            cv.getTransaction().getCustomerVouchers().clear();
            cv.getVoucher().getCustomerVouchers().clear();
            cv.getOwner().getCustomerVouchers().clear();
            cv.getOwner().getCreditCard().setOwner(null);
            cv.getOwner().getCustomerVouchers().clear();
            cv.getOwner().getReservations().clear();
            cv.getOwner().getReviews().clear();
            cv.getOwner().getTransactions().clear();
            cv.getOwner().setPassword(null);

            GenericEntity<CustomerVoucher> genericEntity = new GenericEntity<CustomerVoucher>(cv) {
            };

            return Response.status(Status.OK).entity(genericEntity).build();
        }
        catch(Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @Path("buyVoucher")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response buyVoucher(@QueryParam("voucherId") Long voucherId, @QueryParam("customerId") Long customerId)
    {
        if(voucherId != null & customerId != null)
        {
            try
            {
                CustomerVoucher cv = voucherSessionBeanLocal.createNewCustomerVoucher(new CustomerVoucher(false, new Date(new Date().getTime() + (60 * 60 * 1000))), voucherId, customerId);
                return Response.status(Response.Status.OK).entity(cv.getCustomerVoucherId()).build();
            }				
            catch(Exception ex)
            {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid buy voucher request").build();
        }
    }

    private VoucherSessionBeanLocal lookupVoucherSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (VoucherSessionBeanLocal) c.lookup("java:global/RestaurantReview/RestaurantReview-ejb/VoucherSessionBean!ejb.session.stateless.VoucherSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
