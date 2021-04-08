/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.BankAccountSessionBeanLocal;
import ejb.session.stateless.RestaurantSessionBeanLocal;
import entity.BankAccount;
import entity.Restaurant;
import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import util.exception.BankAccountExistException;
import util.exception.CreateNewBankAccountException;
import util.exception.CreateTransactionException;
import util.exception.InputDataValidationException;
import util.exception.RestaurantNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author zhiliangwang
 */
@Named(value = "bankAccountManagedBean")
@ViewScoped
public class BankAccountManagedBean implements Serializable
{

    
    @EJB
    private BankAccountSessionBeanLocal bankAccountSessionBeanLocal;  
    
    @EJB
    private RestaurantSessionBeanLocal restaurantSessionBeanLocal;
    
    
    private Restaurant currentRestaurant;
    private BankAccount bankAccount;
    
    private Double creditAmount;
    
    public BankAccountManagedBean()
    {
    }
    
    @PostConstruct
    public void postConstruct(){
        currentRestaurant = (Restaurant)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentRestaurant");
        creditAmount = currentRestaurant.getCreditAmount();
        if (currentRestaurant.getBankAccount() == null)
        {
            bankAccount = new BankAccount();           
        }
        else
        {
            bankAccount = currentRestaurant.getBankAccount();
        }
    }
    
    
    public void createNewBankAccount(ActionEvent event) throws IOException
    {
        try
        {
            System.out.println("Ceate Bank Account!!!!!!!!!!!");
            Restaurant newRestaurant = bankAccountSessionBeanLocal.createNewBankAccount(getBankAccount(), getCurrentRestaurant().getId());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentRestaurant", newRestaurant);            
            bankAccount = newRestaurant.getBankAccount();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New bank account " + newRestaurant.getBankAccount().getBankAccountId() + " registered successfully", null));
//            FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/bankAccountManagement.xhtml");
        } catch (UnknownPersistenceException | InputDataValidationException | CreateNewBankAccountException | BankAccountExistException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid data input: " + ex.getMessage(), null));
        }
    }
    
    public void cashOut(ActionEvent event)
    {
        try
        {
            System.out.println("Cash Out!!!");
            Long newTransactionId = restaurantSessionBeanLocal.cashOutCredit(currentRestaurant.getId());
            Restaurant updatedRestaurant = restaurantSessionBeanLocal.retrieveRestaurantById(currentRestaurant.getId());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentRestaurant", updatedRestaurant);            
            creditAmount = new Double(0);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Cash out successfully! With transaction ID: " + newTransactionId, null));
        }
        catch(UnknownPersistenceException | CreateTransactionException | RestaurantNotFoundException | InputDataValidationException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid data input: " + ex.getMessage(), null));
        }
    }

    public Restaurant getCurrentRestaurant()
    {
        return currentRestaurant;
    }

    public void setCurrentRestaurant(Restaurant currentRestaurant)
    {
        this.currentRestaurant = currentRestaurant;
    }

    public BankAccount getBankAccount()
    {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount)
    {
        this.bankAccount = bankAccount;
    }

    public Double getCreditAmount()
    {
        return creditAmount;
    }

    public void setCreditAmount(Double creditAmount)
    {
        this.creditAmount = creditAmount;
    }
}
