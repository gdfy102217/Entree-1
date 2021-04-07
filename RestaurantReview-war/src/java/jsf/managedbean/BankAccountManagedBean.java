/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.BankAccountSessionBeanLocal;
import entity.BankAccount;
import entity.Restaurant;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import util.exception.BankAccountExistException;
import util.exception.CreateNewBankAccountException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author zhiliangwang
 */
@Named(value = "bankAccountManagedBean")
@RequestScoped
public class BankAccountManagedBean
{

    
    @EJB
    private BankAccountSessionBeanLocal bankAccountSessionBeanLocal;    
    
    
    private Restaurant currentRestaurant;
    private BankAccount bankAccount;
    
    private Boolean disableCreateBtn;
    
    public BankAccountManagedBean()
    {
    }
    
    @PostConstruct
    public void postConstruct(){
        currentRestaurant = (Restaurant)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentRestaurant");
        
        if (currentRestaurant.getBankAccount() == null)
        {
            disableCreateBtn = false;
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
            Restaurant newRestaurant = bankAccountSessionBeanLocal.createNewBankAccount(getBankAccount(), getCurrentRestaurant().getUseId());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentRestaurant", newRestaurant);
            disableCreateBtn = true;
            bankAccount = newRestaurant.getBankAccount();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New bank account " + newRestaurant.getBankAccount().getBankAccountId() + " registered successfully", null));
//            FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/bankAccountManagement.xhtml");
        } catch (UnknownPersistenceException | InputDataValidationException | CreateNewBankAccountException | BankAccountExistException ex)
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
    
    public Boolean getDisableCreateBtn()
    {
        return disableCreateBtn;
    }

    public void setDisableCreateBtn(Boolean disableCreateBtn)
    {
        this.disableCreateBtn = disableCreateBtn;
    }
}
