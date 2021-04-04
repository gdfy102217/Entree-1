/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;


@Entity
public class Dish implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dishId;
    
    @NotNull
    @Column(nullable = false, length = 64)
    private String name;
    
    private String description;
    
    private String photo;
    
    @NotNull
    @Column(nullable = false)
    private BigDecimal price;
    
    
    private Boolean recommended;
    
    @ManyToOne(optional = false)
    private Restaurant restaurant;

    public Dish() {
    }

    public Dish(String name, String description, String photo, BigDecimal price, Boolean recommended) {
        this.name = name;
        this.description = description;
        this.photo = photo;
        this.price = price;
        this.recommended = recommended;
    }
    
    

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getRecommended() {
        return recommended;
    }

    public void setRecommended(Boolean recommended) {
        this.recommended = recommended;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dishId != null ? dishId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the dishId fields are not set
        if (!(object instanceof Dish)) {
            return false;
        }
        Dish other = (Dish) object;
        if ((this.dishId == null && other.dishId != null) || (this.dishId != null && !this.dishId.equals(other.dishId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Dish[ id=" + dishId + " ]";
    }
    
}
