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
import entity.TableConfiguration;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import util.exception.BankAccountExistException;
import util.exception.BankAccountNotFoundException;
import util.exception.CreateNewBankAccountException;
import util.exception.DishNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.RestaurantNotFoundException;
import util.exception.TableConfigurationNotFoundException;
import util.exception.UnknownPersistenceException;


@Named(value = "restaurantManagedBean")
@ViewScoped
public class RestaurantManagedBean implements Serializable {

    @EJB
    private BankAccountSessionBeanLocal bankAccountSessionBeanLocal;

    @EJB
    private RestaurantSessionBeanLocal restaurantSessionBeanLocal;
    
    
    private Restaurant currentRestaurant;
    private BankAccount newBankAccount;
    
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
    
    public void deletePhoto(ActionEvent event){
        System.out.println("********** RestaurantManagedBean.deletePhoto()");
        
        currentRestaurant.getPhotos().clear();
       
    }
    
    public void handleFileUpload(FileUploadEvent event)
    {
        try
        {
            String newFilePath = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("alternatedocroot_1") + System.getProperty("file.separator") + event.getFile().getFileName();
//            newRestaurant.getPhotos().add("http://localhost:8080/RestaurantReview-war/uploadedFiles/" + event.getFile().getFileName());
            System.err.println("********** RestaurantManagedBean.handleFileUpload(): File name: " + event.getFile().getFileName());
//            System.err.println("********** Demo03ManagedBean.handleFileUpload(): newFilePath: " + newFilePath);
            
            // add file path to the list
//            newFilePaths.add(newFilePath);
//            System.out.println("File Path size: " + newFilePaths.size());

            File file = new File(newFilePath);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            
//            byte[] picInBytes = new byte[(int) file.length()];
//            FileInputStream fileInputStream = new FileInputStream(file);
//            fileInputStream.read(picInBytes);
//            fileInputStream.close();
//            getNewRestaurant().setProfiePic(picInBytes);

            int a;
            int BUFFER_SIZE = 8192;
            byte[] buffer = new byte[BUFFER_SIZE];

            InputStream inputStream = event.getFile().getInputStream();

            while (true)
            {
                a = inputStream.read(buffer);

                if (a < 0)
                {                    
                    break;
                }
                
                fileOutputStream.write(buffer, 0, a);
                fileOutputStream.flush();
            }
            currentRestaurant.getPhotos().add("http://localhost:8080/RestaurantReview-war/uploadedFiles/" + event.getFile().getFileName());
            fileOutputStream.close();
            inputStream.close();
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,  "File uploaded successfully", ""));
        }
        catch(IOException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,  "File upload error: " + ex.getMessage(), ""));
        }
    }
    
}
