/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import util.enumeration.TableSize;


@Entity
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;
    
    @NotNull
    @Column(nullable = false)
    @Future
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date reservationTime;
    
    @NotNull
    @Column(nullable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date timeOfCreation;
    
    @NotNull
    @Column(nullable = false )
    @Digits(integer = 2, fraction = 0)
    private Integer numOfPax;
    
    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TableSize tableSizeAssigned;
    
    
    private String remark;
    
    @OneToOne(optional = false)
    private Restaurant restaurant;
    
    @OneToOne(optional = false)
    private Customer customer;

    public Reservation() {
    }

    public Reservation(Date reservationTime, Date timeOfCreation, Integer numOfPax, TableSize tableSizeAssigned, String remark) {
        this.reservationTime = reservationTime;
        this.timeOfCreation = timeOfCreation;
        this.numOfPax = numOfPax;
        this.tableSizeAssigned = tableSizeAssigned;
        this.remark = remark;
    }
    
    
    
    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Date getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(Date reservationTime) {
        this.reservationTime = reservationTime;
    }

    public Date getTimeOfCreation() {
        return timeOfCreation;
    }

    public void setTimeOfCreation(Date timeOfCreation) {
        this.timeOfCreation = timeOfCreation;
    }

    public Integer getNumOfPax() {
        return numOfPax;
    }

    public void setNumOfPax(Integer numOfPax) {
        this.numOfPax = numOfPax;
    }

    public TableSize getTableSizeAssigned() {
        return tableSizeAssigned;
    }

    public void setTableSizeAssigned(TableSize tableSizeAssigned) {
        this.tableSizeAssigned = tableSizeAssigned;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationId != null ? reservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reservationId fields are not set
        if (!(object instanceof Reservation)) {
            return false;
        }
        Reservation other = (Reservation) object;
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Reservation[ id=" + reservationId + " ]";
    }
    
}
