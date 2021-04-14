/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

/**
 *
 * @author xuyis
 */
@Entity
public class CustomerVoucher implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerVoucherId;
    
    @NotNull
    @Column(nullable = false)
    private Boolean redeemed;
    
    @FutureOrPresent
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date timeOfCreation;
    
    
    // Need to auto generate 
    private String sixDigitCode;
    
    @ManyToOne(optional = false)
    private Customer owner;
    @ManyToOne(optional = false)
    private Voucher voucher;
    
    
    @OneToOne(mappedBy = "customerVoucher")
    private SaleTransaction saleTransaction;
    
    @ManyToOne
    private Restaurant restaurant;

    public CustomerVoucher() {
        redeemed = false;
    }

    public CustomerVoucher(Boolean redeemed, Date timeOfCreation) {
        this.redeemed = redeemed;
        this.timeOfCreation = timeOfCreation;
        this.sixDigitCode = generateSixDigitCode();
    }
    
    

    public Long getCustomerVoucherId() {
        return customerVoucherId;
    }

    public void setCustomerVoucherId(Long customerVoucherId) {
        this.customerVoucherId = customerVoucherId;
    }

    public Boolean getRedeemed() {
        return redeemed;
    }

    public void setRedeemed(Boolean redeemed) {
        this.redeemed = redeemed;
    }

    public Customer getOwner() {
        return owner;
    }

    public void setOwner(Customer owner) {
        this.owner = owner;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public SaleTransaction getSaleTransaction() {
        return saleTransaction;
    }

    public void setSaleTransaction(SaleTransaction saleTransaction) {
        this.saleTransaction = saleTransaction;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (customerVoucherId != null ? customerVoucherId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the customerVoucherId fields are not set
        if (!(object instanceof CustomerVoucher)) {
            return false;
        }
        CustomerVoucher other = (CustomerVoucher) object;
        if ((this.customerVoucherId == null && other.customerVoucherId != null) || (this.customerVoucherId != null && !this.customerVoucherId.equals(other.customerVoucherId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.CustomerVoucher[ id=" + customerVoucherId + " ]";
    }

    public String getSixDigitCode() {
        return sixDigitCode;
    }

    public void setSixDigitCode(String sixDigitCode) {
        this.sixDigitCode = sixDigitCode;
    }
    
    public static String generateSixDigitCode() 
    {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    public Date getTimeOfCreation() {
        return timeOfCreation;
    }

    public void setTimeOfCreation(Date timeOfCreation) {
        this.timeOfCreation = timeOfCreation;
    }

    public Restaurant getRestaurant()
    {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant)
    {
        this.restaurant = restaurant;
    }
    
}
