/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.VoucherSessionBeanLocal;
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

/**
 * REST Web Service
 *
 * @author zhiliangwang
 */
@Path("Voucher")
public class VoucherResource
{

    VoucherSessionBeanLocal voucherSessionBean = lookupVoucherSessionBeanLocal();
    

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of VoucherResource
     */
    public VoucherResource()
    {
    }

    

    private VoucherSessionBeanLocal lookupVoucherSessionBeanLocal()
    {
        try
        {
            javax.naming.Context c = new InitialContext();
            return (VoucherSessionBeanLocal) c.lookup("java:global/RestaurantReview/RestaurantReview-ejb/VoucherSessionBean!ejb.session.stateless.VoucherSessionBeanLocal");
        } catch (NamingException ne)
        {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
