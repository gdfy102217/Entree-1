/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.DishSessionBeanLocal;
import entity.Dish;
import entity.Restaurant;
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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.primefaces.event.FileUploadEvent;
import util.exception.CreateNewDishException;
import util.exception.DeleteDishException;
import util.exception.DishExistException;
import util.exception.DishNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author nichelle
 */
@Named(value = "dishManagementManagedBean")
@ViewScoped
public class DishManagementManagedBean implements Serializable
{
    @EJB
    private DishSessionBeanLocal dishSessionBeanLocal;
    @Inject
    private ViewDishManagedBean viewDishManagedBean;
    
    private List<Dish> dishes;
    private List<Dish> filteredDishes;
    private Restaurant currentRestaurant;
    private Dish newDish;
    private Dish dishToUpdate;
    private Dish dishToView;
    private String filePath;

    public DishManagementManagedBean()
    {
        newDish = new Dish();
        
    }
    
    
    
    @PostConstruct
    public void postConstruct()
    {
        currentRestaurant = (Restaurant)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentRestaurant");
        //setDishes(dishSessionBeanLocal.retrieveAllDishesForParticularRestaurant(currentRestaurant.getId()));
        dishes = currentRestaurant.getDishes();
    }
    
    
    
    public void viewDishDetails(ActionEvent event) throws IOException
    {
        Long dishIdToView = (Long)event.getComponent().getAttributes().get("dishId");
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("dishIdToView", dishIdToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewDishDetails.xhtml");
    }
    
    
    
    public void createNewDish(ActionEvent event)
    {        
        try
        {
            if (filePath != null) 
            {
                newDish.setPhoto(filePath);
            }
            Dish dish = dishSessionBeanLocal.createNewDish(getNewDish(), getCurrentRestaurant().getId());
            getDishes().add(dish);
            if(getFilteredDishes() != null)
            {
                getFilteredDishes().add(dish);
            }
            setNewDish(new Dish());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New Dish created successfully (Dish ID: " + dish.getDishId() + ")", null));
        
        } catch (DishExistException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Dish already exists " + ex.getMessage(), null));
        }
        catch(InputDataValidationException | CreateNewDishException | UnknownPersistenceException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new dish: " + ex.getMessage(), null));
        }
    }
    
    public void handleFileUpload(FileUploadEvent event)
    {
        try
        {
            String newFilePath = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("alternatedocroot_1") + System.getProperty("file.separator") + event.getFile().getFileName();
//            newRestaurant.getPhotos().add("http://localhost:8080/RestaurantReview-war/uploadedFiles/" + event.getFile().getFileName());
            System.err.println("********** DishManagementManagedBean.handleFileUpload(): File name: " + event.getFile().getFileName());
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
            filePath = ("http://localhost:8080/RestaurantReview-war/uploadedFiles/" + event.getFile().getFileName());
            fileOutputStream.close();
            inputStream.close();
//            filePath = null;
            
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
            dishToUpdate.setPhoto("http://localhost:8080/RestaurantReview-war/uploadedFiles/" + event.getFile().getFileName());
            fileOutputStream.close();
            inputStream.close();
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,  "File uploaded successfully", ""));
        }
        catch(IOException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,  "File upload error: " + ex.getMessage(), ""));
        }
    }
    
    
    
    public void doUpdateDish(ActionEvent event)
    {
        setDishToUpdate((Dish)event.getComponent().getAttributes().get("dishToUpdate"));
    }
    
     
    
    public void updateDish(ActionEvent event)
    {        
        try
        {
            dishSessionBeanLocal.updateDish(getDishToUpdate());

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Dish updated successfully", null));
        }
        catch(DishNotFoundException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating dish: " + ex.getMessage(), null));
        }
        catch(Exception ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }
    
    
    
    public void deleteDish(ActionEvent event)
    {
        try
        {
            Dish dishToDelete = (Dish)event.getComponent().getAttributes().get("dishToDelete");
            dishSessionBeanLocal.deleteDish(dishToDelete.getDishId());
            
            getDishes().remove(dishToDelete);
            
            if(getFilteredDishes() != null)
            {
                getFilteredDishes().remove(dishToDelete);
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Dish deleted successfully", null));
        }
        catch(DishNotFoundException | DeleteDishException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting dish: " + ex.getMessage(), null));
        }
        catch(Exception ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public ViewDishManagedBean getViewDishManagedBean() {
        return viewDishManagedBean;
    }

    public void setViewDishManagedBean(ViewDishManagedBean viewDishManagedBean) {
        this.viewDishManagedBean = viewDishManagedBean;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public List<Dish> getFilteredDishes() {
        return filteredDishes;
    }

    public void setFilteredDishes(List<Dish> filteredDishes) {
        this.filteredDishes = filteredDishes;
    }

    public Restaurant getCurrentRestaurant() {
        return currentRestaurant;
    }

    public void setCurrentRestaurant(Restaurant currentRestaurant) {
        this.currentRestaurant = currentRestaurant;
    }

    public Dish getNewDish() {
        return newDish;
    }

    public void setNewDish(Dish newDish) {
        this.newDish = newDish;
    }

    public Dish getDishToUpdate() {
        return dishToUpdate;
    }

    public void setDishToUpdate(Dish dishToUpdate) {
        this.dishToUpdate = dishToUpdate;
    }

    public Dish getDishToView() {
        return dishToView;
    }

    public void setDishToView(Dish dishToView) {
        this.dishToView = dishToView;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
