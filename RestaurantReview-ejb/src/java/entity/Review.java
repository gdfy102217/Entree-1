/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.validation.constraints.Digits;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;


@Entity
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;
    
    @NotNull
    @Column(nullable = false)
    private String content;
    
    @Digits(integer = 1, fraction = 0)
    private Integer rating;
    
    private List<String> photos;
    
    @OneToMany
    private List<Customer> customerLikes;

    
    @FutureOrPresent
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date timeOfCreation;
    
    @ManyToOne
    private Customer creater;
    
    @ManyToOne
    private Restaurant receiver;
    
    public Review() {
      
    }

    public Review(String content, Integer rating, List<String> photos) {
        this.content = content;
        this.rating = rating;
        this.photos = photos;
        this.customerLikes = new ArrayList<>();
        this.timeOfCreation = new Date(new Date().getTime() + 1000);
    }
    
    

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public List<Customer> getCustomerLikes() {
        return customerLikes;
    }


    public void setCustomerLikes(List<Customer> customerLikes) {
        this.customerLikes = customerLikes;
    }

    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reviewId != null ? reviewId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reviewId fields are not set
        if (!(object instanceof Review)) {
            return false;
        }
        Review other = (Review) object;
        if ((this.reviewId == null && other.reviewId != null) || (this.reviewId != null && !this.reviewId.equals(other.reviewId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Review[ id=" + reviewId + " ]";
    }

    public Date getTimeOfCreation() {
        return timeOfCreation;
    }

    public void setTimeOfCreation(Date timeOfCreation) {
        this.timeOfCreation = timeOfCreation;
    }

    public Customer getCreater() {
        return creater;
    }

    public void setCreater(Customer creater) {
        this.creater = creater;
    }

    public Restaurant getReceiver() {
        return receiver;
    }

    public void setReceiver(Restaurant receiver) {
        this.receiver = receiver;
    }
    
}