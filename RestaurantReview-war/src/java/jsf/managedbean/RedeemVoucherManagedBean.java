/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.VoucherSessionBeanLocal;
import entity.Restaurant;
import entity.Voucher;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import util.exception.CustomerVoucherExpiredException;
import util.exception.CustomerVoucherRedeemedException;

/**
 *
 * @author fengyuan
 */
@Named(value = "redeemVoucherManagedBean")
@SessionScoped
public class RedeemVoucherManagedBean implements Serializable{

    @EJB
    private VoucherSessionBeanLocal voucherSessionBeanLocal;

    private String sixDigitCode;
    private Restaurant currentRestaurant;
    private Voucher voucherType;
    
    public RedeemVoucherManagedBean() {
    }
    
    @PostConstruct
    public void postConstruct()
    {
        currentRestaurant = (Restaurant) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentRestaurant");
    }
    
    public void redeemVoucher(ActionEvent event)
    {        
        try
        {
            System.out.println("Restaurant ID: " + currentRestaurant.getUserId());
            Restaurant updatedRestaurant = voucherSessionBeanLocal.redeemCustomerVoucher(sixDigitCode, currentRestaurant.getUserId());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentRestaurant", updatedRestaurant);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Redeem voucher successfully", null));
        }
        catch(CustomerVoucherRedeemedException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "This voucher has been redeemed! ", null));
        }
        catch(CustomerVoucherExpiredException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "This voucher is expired! ", null));
        }
        catch(Exception ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public String getSixDigitCode() {
        return sixDigitCode;
    }

    public void setSixDigitCode(String sixDigitCode) {
        this.sixDigitCode = sixDigitCode;
    }

    public Voucher getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(Voucher voucherType) {
        this.voucherType = voucherType;
    }
    
    
}
