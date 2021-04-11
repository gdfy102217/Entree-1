/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.VoucherSessionBeanLocal;
import entity.CustomerVoucher;
import entity.Voucher;
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
@Path("Voucher")
public class VoucherResource {

    VoucherSessionBeanLocal voucherSessionBeanLocal = lookupVoucherSessionBeanLocal();

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of VoucherResource
     */
    public VoucherResource() {
    }

    @Path("retrieveMyCustomerVouchers/{customerId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveMyCustomerVouchers(@PathParam("customerId") Long CustomerId)
    {
        try
        {
            List<CustomerVoucher> myVouchers = voucherSessionBeanLocal.retrieveAllCustomerVouchersByCustomerId(CustomerId);
            
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
