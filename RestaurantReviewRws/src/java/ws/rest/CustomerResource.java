/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.CustomerSessionBeanLocal;
import entity.Customer;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;

/**
 * REST Web Service
 *
 * @author fengyuan
 */
@Path("Customer")
public class CustomerResource 
{
    CustomerSessionBeanLocal customerSessionBeanLocal = lookupCustomerSessionBeanLocal();
    
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of CustomerResource
     */
    public CustomerResource() {
    }

    @Path("retrieveAllCustomers")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllCustomers()
    {
        try
        {
            List<Customer> customers = customerSessionBeanLocal.retrieveAllCustomers();
            
            for(Customer c: customers) 
            {
                c.getCreditCard().setOwner(null);
                c.getCustomerVouchers().clear();
                c.getReviews().clear();
                c.getTransactions().clear();
                c.getReservations().clear();
            }
            
            GenericEntity<List<Customer>> genericEntity = new GenericEntity<List<Customer>>(customers) {
            };

            return Response.status(Status.OK).entity(genericEntity).build();
        }
        catch(Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @Path("createNewCustomer")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createNewCustomer(Customer newCustomer)
    {
        if(newCustomer != null)
        {
            try
            {
                Long newCustomerId = customerSessionBeanLocal.createNewCustomer(newCustomer);

                return Response.status(Response.Status.OK).entity(newCustomerId).build();
            }
            catch(Exception ex)
            {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid create new customer request").build();
        }
    }
    
    @Path("customerLogin")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response customerLogin(@QueryParam("email") String username, @QueryParam("password") String password)
    {
        try
        {
            Customer customer = customerSessionBeanLocal.customerLogin(username, password);
            System.out.println("********** CustomerResource.customerLogin(): Customer " + customer.getEmail() + " login");
            
            customer.getReservations().clear();
            if (customer.getCreditCard() != null) {
                customer.getCreditCard().setOwner(null);
            }
            
            customer.getCustomerVouchers().clear();
            customer.getReviews().clear();
            customer.getTransactions().clear();

//            customer.setPassword(null);
            //customer.setSalt(null);         
            
            return Response.status(Status.OK).entity(customer).build();
        }
        catch(InvalidLoginCredentialException ex)
        {
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        }
        catch(Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @Path("retrieveCustomerById/{customerId}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCustomerById(@PathParam("customerId") Long customerId)
    {
        try
        {
            Customer customer = customerSessionBeanLocal.retrieveCustomerById(customerId);
            
            customer.getReservations().clear();
            customer.getCreditCard().setOwner(null);
            customer.getCustomerVouchers().clear();
            customer.getReviews().clear();
            customer.getTransactions().clear();

//            customer.setPassword(null);
            //customer.setSalt(null);          
            
            return Response.status(Status.OK).entity(customer).build();
        }
        catch(CustomerNotFoundException ex)
        {
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        }
        catch(Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response customerUpdate(Customer customerToUpdate)
    {
        if(customerToUpdate != null)
        {
            try
            {
                Long customerToUpdateId = customerSessionBeanLocal.updateCustomer(customerToUpdate);

                return Response.status(Response.Status.OK).entity(customerToUpdateId).build();
            }
            catch(Exception ex)
            {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid update customer request").build();
        }
    }
            
    private CustomerSessionBeanLocal lookupCustomerSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (CustomerSessionBeanLocal) c.lookup("java:global/RestaurantReview/RestaurantReview-ejb/CustomerSessionBean!ejb.session.stateless.CustomerSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
