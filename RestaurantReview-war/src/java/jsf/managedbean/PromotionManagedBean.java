/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.PromotionSessionBeanLocal;
import entity.Promotion;
import entity.Restaurant;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
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
    
    public PromotionManagedBean() 
    {
        newPromotion = new Promotion();
    }
    
    @PostConstruct
    public void postConstruct()
    {
        currentRestaurant = (Restaurant) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentRestaurant");
        promotions = promotionSessionBeanLocal.retrievePromotionByRestaurantId(currentRestaurant.getUseId());
//        setPromotions(promotionSessionBeanLocal.retrieveAllPromotions());
    }
    
    public void createNewPromotion(ActionEvent event)
    {        
        try
        {
            Promotion p = promotionSessionBeanLocal.createNewPromotion(getNewPromotion(), getCurrentRestaurant().getUseId());
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
