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
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import util.exception.BankAccountExistException;
import util.exception.BankAccountNotFoundException;
import util.exception.CreateNewBankAccountException;
import util.exception.DishNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.RestaurantNotFoundException;
import util.exception.TableConfigurationNotFoundException;
import util.exception.UnknownPersistenceException;


@Named(value = "restaurantManagedBean")
@RequestScoped
public class RestaurantManagedBean {

    @EJB
    private BankAccountSessionBeanLocal bankAccountSessionBeanLocal;

    @EJB
    private RestaurantSessionBeanLocal restaurantSessionBeanLocal;
    
    
    
    private Restaurant currentRestaurant;
    private BankAccount newBankAccount;
    private String photoToView;
    
    public RestaurantManagedBean() {
    }
    
    @PostConstruct
    public void postConstruct(){
        currentRestaurant = (Restaurant)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentRestaurant");
        newBankAccount = new BankAccount();
    }
    
    
    public void updateRestaurant(ActionEvent event){
        
        try {
            if (currentRestaurant.getAcceptReservation()==false) {
            currentRestaurant.getTableConfiguration().setNumOfSmallTable(0);
            currentRestaurant.getTableConfiguration().setNumOfMediumTable(0);
            currentRestaurant.getTableConfiguration().setNumOfLargeTable(0);
            }
            
            Long restaurantId = restaurantSessionBeanLocal.updateRestaurant(currentRestaurant);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Restaurant " + restaurantId + " updated successfully", null));  
        } catch (RestaurantNotFoundException | InputDataValidationException | DishNotFoundException | BankAccountNotFoundException | TableConfigurationNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Update error: " + ex.getMessage(), null));
        }
        
    }
    
    public void createNewBankAccount(ActionEvent event){
        try {
            System.out.println("Ceate Bank Account!!!!!!!!!!!");
            Long newBankAccountId = bankAccountSessionBeanLocal.createNewBankAccount(newBankAccount, currentRestaurant.getUseId());
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New bank account " + newBankAccountId + " registered successfully", null));  
        } catch (UnknownPersistenceException | InputDataValidationException | CreateNewBankAccountException | BankAccountExistException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid data input: " + ex.getMessage(), null));
        }
    }

    public Restaurant getCurrentRestaurant() {
        return currentRestaurant;
    }

    public void setCurrentRestaurant(Restaurant currentRestaurant) {
        this.currentRestaurant = currentRestaurant;
    }

    public BankAccount getNewBankAccount() {
        return newBankAccount;
    }

    public void setNewBankAccount(BankAccount newBankAccount) {
        this.newBankAccount = newBankAccount;
    }

    public String getPhotoToView() {
        return photoToView;
    }

    public void setPhotoToView(String photoToView) {
        this.photoToView = photoToView;
    }
    
    
    
    public void viewPhoto(ActionEvent event) throws IOException{
        
        System.out.println("********** viewPhoto():" + photoToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "uploadedFiles/" + photoToView);
        
    }
    
    
    
}
