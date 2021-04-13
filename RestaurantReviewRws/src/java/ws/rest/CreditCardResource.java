/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.CreditCardSessionBeanLocal;
import entity.CreditCard;
import entity.Customer;
import entity.CustomerVoucher;
import entity.SaleTransaction;
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
import javax.ws.rs.PUT;
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
@Path("CreditCard/{customerId}")
public class CreditCardResource {

    CreditCardSessionBeanLocal creditCardSessionBeanLocal = lookupCreditCardSessionBeanLocal();

    
    @Context
    private UriInfo context;
    
    @PathParam("customerId")
    private Long customerId;

    /**
     * Creates a new instance of CreditCardResource
     */
    public CreditCardResource() {
    }
    
    @Path("retrieveMyCreditCards")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveMyCreditCards()
    {
        try
        {
            List<CreditCard> myCreditCards = creditCardSessionBeanLocal.retrieveCreditCardsByCustomerId(customerId);
            
            if (myCreditCards.isEmpty())
            {
                return Response.status(Status.UNAUTHORIZED).entity("No Credit Cards Found!").build();
            }
            
            for (CreditCard cc: myCreditCards)
            {
                Customer dummyCustomer = new Customer();
                dummyCustomer.setId(customerId);
                dummyCustomer.setFirstName(cc.getOwner().getFirstName());
                dummyCustomer.setLastName(cc.getOwner().getLastName());
                cc.setOwner(dummyCustomer);
            }

            GenericEntity<List<CreditCard>> genericEntity = new GenericEntity<List<CreditCard>>(myCreditCards) {
            };

            return Response.status(Status.OK).entity(genericEntity).build();
        }
        catch(Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @Path("createNewCreditCard")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewCreditCard(CreditCard newCreditCard)
    {
        if(newCreditCard != null)
        {
            try
            {
                creditCardSessionBeanLocal.createNewCreditCard(newCreditCard, customerId);

                return Response.status(Response.Status.OK).entity(newCreditCard.getCreditCardId()).build();
            }
            catch(Exception ex)
            {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid create new credit card request").build();
        }
    }
    
    @Path("viewCreditCardDetails")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    public Response viewCreditCardDetails(@QueryParam("creditCardId") Long creditCardId)
    {
        try
        {
            CreditCard creditCard = creditCardSessionBeanLocal.retrieveCreditCardById(creditCardId);

            if (creditCard.getTransaction() != null) {
                SaleTransaction dummyTransaction = new SaleTransaction();
                dummyTransaction.setPaidAmount(creditCard.getTransaction().getPaidAmount());
                dummyTransaction.setTransactionDate(creditCard.getTransaction().getTransactionDate());
                dummyTransaction.setTransactionId(creditCard.getTransaction().getTransactionId());
            }
            
            Customer dummyCustomer = new Customer();
            dummyCustomer.setId(customerId);
            dummyCustomer.setFirstName(creditCard.getOwner().getFirstName());
            dummyCustomer.setLastName(creditCard.getOwner().getLastName());
            creditCard.setOwner(dummyCustomer);
            

            GenericEntity<CreditCard> genericEntity = new GenericEntity<CreditCard>(creditCard) {
            };

            return Response.status(Status.OK).entity(genericEntity).build();
        }
        catch(Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @DELETE
    @Consumes(MediaType.TEXT_PLAIN)
    public Response deleteCreditCard(@QueryParam("creditCardId") Long creditCardId)
    {
        if(creditCardId != null)
        {
            try
            {
                creditCardSessionBeanLocal.deleteCreditCard(creditCardId);

                return Response.status(Response.Status.OK).entity(creditCardId).build();
            }
            catch(Exception ex)
            {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid delete credit card request").build();
        }
    }

    private CreditCardSessionBeanLocal lookupCreditCardSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (CreditCardSessionBeanLocal) c.lookup("java:global/RestaurantReview/RestaurantReview-ejb/CreditCardSessionBean!ejb.session.stateless.CreditCardSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

}
