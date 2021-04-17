/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.VoucherSessionBeanLocal;
import entity.Customer;
import entity.CustomerVoucher;
import entity.Restaurant;
import entity.SaleTransaction;
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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import util.exception.DuplicatePurchaseException;

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
                if (cv.getOwner() != null) {
                    Customer dummyCustomer = new Customer();
                    dummyCustomer.setFirstName(cv.getOwner().getFirstName());
                    dummyCustomer.setLastName(cv.getOwner().getLastName());
                    cv.setOwner(dummyCustomer);
                }
            
                if (cv.getVoucher() != null) {
                    Voucher dummyVoucher = new Voucher();
                    dummyVoucher.setTitle(cv.getVoucher().getTitle());
                    dummyVoucher.setContent(cv.getVoucher().getContent());
                    dummyVoucher.setExpiryDate(cv.getVoucher().getExpiryDate());
                    cv.setVoucher(dummyVoucher);
                }
                
                if (cv.getRestaurant() != null) {
                    Restaurant dummyRestaurant = new Restaurant();
                    dummyRestaurant.setName(cv.getRestaurant().getName());
                    cv.setRestaurant(dummyRestaurant);
                }

                if (cv.getSaleTransaction() != null) {
                    SaleTransaction dummyTransaction = new SaleTransaction();
                    dummyTransaction.setTransactionId(cv.getSaleTransaction().getTransactionId());
                    dummyTransaction.setTransactionDate(cv.getSaleTransaction().getTransactionDate());
                    dummyTransaction.setPaidAmount(cv.getSaleTransaction().getPaidAmount());
                    cv.setSaleTransaction(dummyTransaction);
                }
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
                if (cv.getOwner() != null) {
                    Customer dummyCustomer = new Customer();
                    dummyCustomer.setFirstName(cv.getOwner().getFirstName());
                    dummyCustomer.setLastName(cv.getOwner().getLastName());
                    cv.setOwner(dummyCustomer);
                }
            
                if (cv.getVoucher() != null) {
                    Voucher dummyVoucher = new Voucher();
                    dummyVoucher.setTitle(cv.getVoucher().getTitle());
                    dummyVoucher.setContent(cv.getVoucher().getContent());
                    cv.setVoucher(dummyVoucher);
                }
                
                if (cv.getRestaurant() != null) {
                    Restaurant dummyRestaurant = new Restaurant();
                    dummyRestaurant.setName(cv.getRestaurant().getName());
                    cv.setRestaurant(dummyRestaurant);
                }
                
                if (cv.getSaleTransaction() != null) {
                    SaleTransaction dummyTransaction = new SaleTransaction();
                    dummyTransaction.setTransactionId(cv.getSaleTransaction().getTransactionId());
                    dummyTransaction.setTransactionDate(cv.getSaleTransaction().getTransactionDate());
                    dummyTransaction.setPaidAmount(cv.getSaleTransaction().getPaidAmount());
                    cv.setSaleTransaction(dummyTransaction);
                }
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

            if (cv.getSaleTransaction() != null) {
                SaleTransaction dummyTransaction = new SaleTransaction();
                dummyTransaction.setTransactionId(cv.getSaleTransaction().getTransactionId());
                dummyTransaction.setTransactionDate(cv.getSaleTransaction().getTransactionDate());
                dummyTransaction.setPaidAmount(cv.getSaleTransaction().getPaidAmount());
                cv.setSaleTransaction(dummyTransaction);
            }
            
            if (cv.getVoucher() != null) {
                cv.getVoucher().getCustomerVouchers().clear();
            }

            if (cv.getRestaurant() != null) {
                Restaurant dummyRestaurant = new Restaurant();
                dummyRestaurant.setName(cv.getRestaurant().getName());
                cv.setRestaurant(dummyRestaurant);
            }
            
            if (cv.getOwner() != null) {
                Customer dummyCustomer = new Customer();
                dummyCustomer.setFirstName(cv.getOwner().getFirstName());
                dummyCustomer.setLastName(cv.getOwner().getLastName());
                cv.setOwner(dummyCustomer);
            }
            
            

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
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response buyVoucher(@QueryParam("voucherId") Long voucherId, @QueryParam("customerId") Long customerId)
    {
        if(voucherId != null & customerId != null)
        {
            try
            {
                Long customerVoucherId = voucherSessionBeanLocal.createNewCustomerVoucher(new CustomerVoucher(false, new Date(new Date().getTime() + (60 * 60 * 1000))), voucherId, customerId);
                
                return Response.status(Response.Status.OK).entity(customerVoucherId).build();
            }				
            catch(DuplicatePurchaseException ex)
            {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
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
