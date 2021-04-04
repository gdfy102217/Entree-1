package jsf.managedbean;

import ejb.session.stateless.DishSessionBeanLocal;
import entity.Dish;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.exception.DishNotFoundException;



@Named(value = "updateDishManagedBean")
@ViewScoped

public class UpdateDishManagedBean implements Serializable
{
    @EJB
    private DishSessionBeanLocal dishSessionBeanLocal;
    
    private Long dishIdToUpdate;
    private Dish dishToUpdate;
    private String recommended;
    
    
    public UpdateDishManagedBean() 
    {        
    }
    
    
    
    @PostConstruct
    public void postConstruct()
    {
        dishIdToUpdate = (Long)FacesContext.getCurrentInstance().getExternalContext().getFlash().get("dishIdToUpdate");
        
        try
        {
            if(dishIdToUpdate != null)
            {
                dishToUpdate = dishSessionBeanLocal.retrieveDishById(dishIdToUpdate);
            }
            else
            {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No dish has been selected", null));
            }
        }
        catch(DishNotFoundException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while retrieving the dish details: " + ex.getMessage(), null));
        }
        catch(Exception ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }
    
    
    
    public void back(ActionEvent event) throws IOException
    {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("dishIdToView", dishIdToUpdate);
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewDishDetails.xhtml");
    }
    
    
    
    public void foo()
    {        
    }
    
    
    
    public void updateDish()
    {
        boolean recommending = false;
        if (recommended.equals("Yes"))
        {
            recommending = true;
        }
        
        try
        {
            dishToUpdate.setRecommended(recommending);
            dishSessionBeanLocal.updateDish(dishToUpdate);

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
    
    
    
    public Dish getDishToUpdate() {
        return dishToUpdate;
    }

    public void setDishToUpdate(Dish dishToUpdate) {
        this.dishToUpdate = dishToUpdate;
    }
}

