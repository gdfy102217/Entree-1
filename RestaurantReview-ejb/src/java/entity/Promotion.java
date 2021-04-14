/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;


@Entity
public class Promotion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long promotionId;
    
    @NotNull
    @Column(nullable = false)
    private String content;
    
    @NotNull
    @Column(nullable = false)
    private String title;
    private String photo;
    
    @NotNull
    @Column(nullable = false)  
    @FutureOrPresent
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date startDate;
    
    @NotNull
    @Column(nullable = false)
    @FutureOrPresent
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date endDate;

    @ManyToOne(optional = false)
    private Restaurant restaurant;

    public Promotion() {
    }

    public Promotion(String content, String title, String photo, Date startDate, Date endDate) {
        this.content = content;
        this.title = title;
        this.photo = photo;
        this.startDate = new Date(startDate.getTime() + 1000);
        this.endDate = new Date(endDate.getTime() + 1000);
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (promotionId != null ? promotionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the promotionId fields are not set
        if (!(object instanceof Promotion)) {
            return false;
        }
        Promotion other = (Promotion) object;
        if ((this.promotionId == null && other.promotionId != null) || (this.promotionId != null && !this.promotionId.equals(other.promotionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Promotion[ id=" + promotionId + " ]";
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
    
}