/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.ReviewSessionBeanLocal;
import entity.Restaurant;
import entity.Review;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author fengyuan
 */
@Named(value = "reviewManagedBean")
@ViewScoped
public class ReviewManagedBean implements Serializable {

    @EJB
    private ReviewSessionBeanLocal reviewSessionBeanLocal;
    
    private Restaurant currentRestaurant;
    private List<Review> myReviews;
    private List<Review> filteredReviews;
    
    private Review newReview;
    
    public ReviewManagedBean() {
    }
    
    @PostConstruct
    public void postConstruct()
    {
        setCurrentRestaurant((Restaurant) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentRestaurant"));
        setMyReviews(reviewSessionBeanLocal.retrieveReviewsByRestaurantId(getCurrentRestaurant().getUseId()));
//        setPromotions(promotionSessionBeanLocal.retrieveAllPromotions());
    }
    
    public void replyReview(ActionEvent event)
    {
//        try
//        {
            Review reviewToReply = (Review)event.getComponent().getAttributes().get("reviewToReply");
//            Review r = reviewSessionBeanLocal.createNewReview(new Review, , Long.MIN_VALUE);
//            getPromotions().add(p);
//            
//            if(getFilteredPromotions() != null)
//            {
//                getFilteredPromotions().add(p);
//            }
//            
//            setNewPromotion(new Promotion());

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New promotion created successfully (Promotion ID: "  + ")", null));
//        }
//        catch()
//        {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new promotion: " + ex.getMessage(), null));
//        }
    }

    public Restaurant getCurrentRestaurant() {
        return currentRestaurant;
    }

    public void setCurrentRestaurant(Restaurant currentRestaurant) {
        this.currentRestaurant = currentRestaurant;
    }

    public List<Review> getMyReviews() {
        return myReviews;
    }

    public void setMyReviews(List<Review> myReviews) {
        this.myReviews = myReviews;
    }

    public List<Review> getFilteredReviews() {
        return filteredReviews;
    }

    public void setFilteredReviews(List<Review> filteredReviews) {
        this.filteredReviews = filteredReviews;
    }
    
}
