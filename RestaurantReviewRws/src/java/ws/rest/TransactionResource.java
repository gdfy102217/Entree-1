/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.SaleTransactionSessionBeanLocal;
import entity.BankAccount;
import entity.CreditCard;
import entity.Customer;
import entity.Restaurant;
import entity.SaleTransaction;
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
@Path("Transaction")
public class TransactionResource {

    SaleTransactionSessionBeanLocal saleTransactionSessionBeanLocal = lookupSaleTransactionSessionBeanLocal();

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of TransactionResource
     */
    public TransactionResource() {
    }

    @Path("retrieveMyTransactions/{customerId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveMyTransactions(@PathParam("customerId") Long CustomerId)
    {
        try
        {
            List<SaleTransaction> myTransactions = saleTransactionSessionBeanLocal.retrieveTransactionsByCustomerId(CustomerId);
            
            if (myTransactions.isEmpty())
            {
                return Response.status(Status.UNAUTHORIZED).entity("No Transactions Found!").build();
            } 
            
            for (SaleTransaction st: myTransactions) {
                if (st.getCustomer() != null) {
                    Customer dummyCustomer = new Customer();
                    dummyCustomer.setFirstName(st.getCustomer().getFirstName());
                    dummyCustomer.setLastName(st.getCustomer().getLastName());
                    st.setCustomer(dummyCustomer);
                }
            
                if (st.getCreditCard() != null) {
                    CreditCard dummyCreditCard = new CreditCard();
                    dummyCreditCard.setCreditCardId(st.getCreditCard().getCreditCardId());
                    dummyCreditCard.setCardNumber(st.getCreditCard().getCardNumber());
                    st.setCreditCard(dummyCreditCard);
                }
                
                if (st.getRestaurant() != null) {
                    Restaurant dummyRestaurant = new Restaurant();
                    dummyRestaurant.setUserId(st.getRestaurant().getUserId());
                    dummyRestaurant.setName(st.getRestaurant().getName());
                    st.setRestaurant(dummyRestaurant);
                }
                
                if (st.getBankAccount() != null) {
                    BankAccount dummyBankAccount = new BankAccount();
                    dummyBankAccount.setBankAccountId(st.getBankAccount().getBankAccountId());
                    dummyBankAccount.setBankAccountNumber(st.getBankAccount().getBankAccountNumber());
                    dummyBankAccount.setNameOfBank(st.getBankAccount().getNameOfBank());
                    st.setBankAccount(dummyBankAccount);
                }
                
//                st.getCustomerVouchers().clear();
                st.setCustomerVoucher(null);
            }

            GenericEntity<List<SaleTransaction>> genericEntity = new GenericEntity<List<SaleTransaction>>(myTransactions) {
            };

            return Response.status(Status.OK).entity(genericEntity).build();
        }
        catch(Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    private SaleTransactionSessionBeanLocal lookupSaleTransactionSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (SaleTransactionSessionBeanLocal) c.lookup("java:global/RestaurantReview/RestaurantReview-ejb/SaleTransactionSessionBean!ejb.session.stateless.SaleTransactionSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
