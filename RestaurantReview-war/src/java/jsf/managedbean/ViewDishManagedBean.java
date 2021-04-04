package jsf.managedbean;

import ejb.session.stateless.DishSessionBeanLocal;
import entity.Dish;
import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import util.exception.DishNotFoundException;



@Named(value = "viewDishManagedBean")
@ViewScoped

public class ViewDishManagedBean implements Serializable
{
    private Dish dishToView;
    
    
    
    public ViewDishManagedBean() 
    {
        dishToView = new Dish();
    }
    
    
    
    @PostConstruct
    public void postConstruct()
    {
    }
    
    public Dish getDishToView() {
        return dishToView;
    }

    public void setDishToView(Dish dishToView) {
        this.dishToView = dishToView;
    }

}