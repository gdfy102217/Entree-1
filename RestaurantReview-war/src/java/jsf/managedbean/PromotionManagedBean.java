/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.PromotionSessionBeanLocal;
import entity.Promotion;
import entity.Restaurant;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import util.exception.CreateNewPromotionException;
import util.exception.InputDataValidationException;
import util.exception.PromotionExistException;
import util.exception.PromotionNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author fengyuan
 */
@Named(value = "promotionManagedBean")
@ViewScoped
public class PromotionManagedBean implements Serializable{

    @EJB
    private PromotionSessionBeanLocal promotionSessionBeanLocal;

    private Restaurant currentRestaurant;
    private List<Promotion> promotions;
    private List<Promotion> filteredPromotions;
    
    private Promotion newPromotion;
    
    private Promotion promotionToUpdate;
    private Promotion promotionToView;
    
    private String filePath;
    
    public PromotionManagedBean() 
    {
        newPromotion = new Promotion();
    }
    
    @PostConstruct
    public void postConstruct()
    {
        currentRestaurant = (Restaurant) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentRestaurant");
        promotions = promotionSessionBeanLocal.retrievePromotionByRestaurantId(currentRestaurant.getUserId());
//        setPromotions(promotionSessionBeanLocal.retrieveAllPromotions());
    }
    
    public void createNewPromotion(ActionEvent event)
    {        
        try
        {
            if (filePath != null) 
            {
                newPromotion.setPhoto(filePath);
            }
            Promotion p = promotionSessionBeanLocal.createNewPromotion(getNewPromotion(), getCurrentRestaurant().getUserId());
            getPromotions().add(p);
            
            if(getFilteredPromotions() != null)
            {
                getFilteredPromotions().add(p);
            }
            
            setNewPromotion(new Promotion());

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New promotion created successfully (Promotion ID: " + p.getPromotionId() + ")", null));
        }
        catch(InputDataValidationException | CreateNewPromotionException | PromotionExistException | UnknownPersistenceException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new promotion: " + ex.getMessage(), null));
        }
    }   
    
    public void viewPromotionPhoto(ActionEvent event)
    {
        setPromotionToView((Promotion)event.getComponent().getAttributes().get("promotionToView"));
    }
    
    public void deletePhoto(ActionEvent event){
        System.out.println("********** PromotionManagedBean.deletePhoto()");
        
        promotionToUpdate.setPhoto(null);
       
    }
    
    public void handleFileUpload(FileUploadEvent event)
    {
        try
        {
            String newFilePath = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("alternatedocroot_1") + System.getProperty("file.separator") + event.getFile().getFileName();
//            newRestaurant.getPhotos().add("http://localhost:8080/RestaurantReview-war/uploadedFiles/" + event.getFile().getFileName());
            System.err.println("********** Demo03ManagedBean.handleFileUpload(): InitParameter: " + FacesContext.getCurrentInstance().getExternalContext().getInitParameter("alternatedocroot_1"));
            System.err.println("********** Demo03ManagedBean.handleFileUpload(): File name: " + event.getFile().getFileName());
//            System.err.println("********** Demo03ManagedBean.handleFileUpload(): newFilePath: " + newFilePath);
            
            // add file path to the list
//            filePaths.add(newFilePath);
//            System.out.println("File Path size: " + filePaths.size());

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
            filePath = ("http://localhost:8080/RestaurantReview-war/uploadedFiles/" + event.getFile().getFileName());
            fileOutputStream.close();
            inputStream.close();
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,  "File uploaded successfully", ""));
        }
        catch(IOException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,  "File upload error: " + ex.getMessage(), ""));
        }
    }
    
    public void updatePhoto(FileUploadEvent event)
    {
        try
        {
            String newFilePath = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("alternatedocroot_1") + System.getProperty("file.separator") + event.getFile().getFileName();
//            newRestaurant.getPhotos().add("http://localhost:8080/RestaurantReview-war/uploadedFiles/" + event.getFile().getFileName());
            System.err.println("********** Demo03ManagedBean.handleFileUpload(): InitParameter: " + FacesContext.getCurrentInstance().getExternalContext().getInitParameter("alternatedocroot_1"));
            System.err.println("********** Demo03ManagedBean.handleFileUpload(): File name: " + event.getFile().getFileName());
//            System.err.println("********** Demo03ManagedBean.handleFileUpload(): newFilePath: " + newFilePath);
            
            // add file path to the list
//            filePaths.add(newFilePath);
//            System.out.println("File Path size: " + filePaths.size());

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
            promotionToUpdate.setPhoto("http://localhost:8080/RestaurantReview-war/uploadedFiles/" + event.getFile().getFileName());
            fileOutputStream.close();
            inputStream.close();
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,  "File uploaded successfully", ""));
        }
        catch(IOException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,  "File upload error: " + ex.getMessage(), ""));
        }
    }
    
    public void doUpdatePromotion(ActionEvent event)
    {
        setPromotionToUpdate((Promotion)event.getComponent().getAttributes().get("promotionToUpdate"));
    }
    
    public void updatePromotion(ActionEvent event)
    {        
        try
        {
            promotionSessionBeanLocal.updatePromotion(getPromotionToUpdate());

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Promotion updated successfully", null));
        }
        catch(PromotionNotFoundException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating promotion: " + ex.getMessage(), null));
        }
        catch(Exception ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }
    
    
    
    public void deletePromotion(ActionEvent event)
    {
        try
        {
            Promotion promotionToDelete = (Promotion)event.getComponent().getAttributes().get("promotionToDelete");
            promotionSessionBeanLocal.deletePromotion(promotionToDelete.getPromotionId());
            
            getPromotions().remove(promotionToDelete);
            
            if(getFilteredPromotions() != null)
            {
                getFilteredPromotions().remove(promotionToDelete);
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Promotion deleted successfully", null));
        }
        catch(PromotionNotFoundException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting promotion: " + ex.getMessage(), null));
        }
        catch(Exception ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public Restaurant getCurrentRestaurant() {
        return currentRestaurant;
    }

    public void setCurrentRestaurant(Restaurant currentRestaurant) {
        this.currentRestaurant = currentRestaurant;
    }

    public List<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<Promotion> promotions) {
        this.promotions = promotions;
    }

    public List<Promotion> getFilteredPromotions() {
        return filteredPromotions;
    }

    public void setFilteredPromotions(List<Promotion> filteredPromotions) {
        this.filteredPromotions = filteredPromotions;
    }

    public Promotion getNewPromotion() {
        return newPromotion;
    }

    public void setNewPromotion(Promotion newPromotion) {
        this.newPromotion = newPromotion;
    }

    public Promotion getPromotionToView() {
        return promotionToView;
    }

    public void setPromotionToView(Promotion promotionToView) {
        this.promotionToView = promotionToView;
    }

    public Promotion getPromotionToUpdate() {
        return promotionToUpdate;
    }

    public void setPromotionToUpdate(Promotion promotionToUpdate) {
        this.promotionToUpdate = promotionToUpdate;
    }
}