package jsf.managedbean;

import ejb.session.stateless.DishSessionBeanLocal;
import entity.Dish;
import entity.Restaurant;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;



@Named(value = "viewAllDishesManagedBean")
@ViewScoped

public class ViewAllDishesManagedBean implements Serializable
{
    @EJB
    private DishSessionBeanLocal DishSessionBeanLocal;
    
    private List<Dish> dishes;
    
    private Restaurant currentRestaurant;
    
    
    public ViewAllDishesManagedBean(ActionEvent event) 
    {
        currentRestaurant = (Restaurant)event.getComponent().getAttributes().get("currentRestaurant");
    }
    
    
    
    @PostConstruct
    public void postConstruct()
    {
        setDishes(DishSessionBeanLocal.retrieveAllDishesForParticularRestaurant(currentRestaurant.getId()));
    }
    
    
    
    public void viewDishDetails(ActionEvent event) throws IOException
    {
        Long dishIdToView = (Long)event.getComponent().getAttributes().get("dishIdToView");
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("dishIdToView", dishIdToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewDishDetails.xhtml");
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }
   
}

