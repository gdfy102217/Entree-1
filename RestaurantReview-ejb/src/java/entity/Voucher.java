/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.validation.constraints.Digits;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;


@Entity
public class Voucher implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voucherId;
    
    @NotNull
    @Column(nullable = false)
    private String title;
    
    @NotNull
    @Column(nullable = false)
    @FutureOrPresent
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date expiryDate;
    
    @NotNull
    @Column(nullable = false)
    @Digits(integer = 5, fraction = 2)
    private BigDecimal amountRedeemable;
    
    @NotNull
    @Column(nullable = false)
    @Digits(integer = 5, fraction = 2)
    private BigDecimal price;
    
    @NotNull
    @Column(nullable = false)
    private Boolean validity;
    
    @NotNull
    @Column(nullable = false)
    private String content;
    
    
    @OneToMany(mappedBy = "voucher")
    private List<CustomerVoucher> customerVouchers;
    
    

    public Voucher() {
        customerVouchers = new ArrayList<>();
    }

    public Voucher(String title, Date expiryDate, BigDecimal amountRedeemable, BigDecimal price, Boolean validity, String content) {
        this.title = title;
        this.expiryDate = expiryDate;
        this.amountRedeemable = amountRedeemable;
        this.price = price;
        this.validity = validity;
        this.content = content;
    }
    
    

    public Long getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Long voucherId) {
        this.voucherId = voucherId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public BigDecimal getAmountRedeemable() {
        return amountRedeemable;
    }

    public void setAmountRedeemable(BigDecimal amountRedeemable) {
        this.amountRedeemable = amountRedeemable;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getValidity() {
        return validity;
    }

    public void setValidity(Boolean validity) {
        this.validity = validity;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<CustomerVoucher> getCustomerVouchers() {
        return customerVouchers;
    }

    public void setCustomerVouchers(List<CustomerVoucher> customerVouchers) {
        this.customerVouchers = customerVouchers;
    }

    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (voucherId != null ? voucherId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the voucherId fields are not set
        if (!(object instanceof Voucher)) {
            return false;
        }
        Voucher other = (Voucher) object;
        if ((this.voucherId == null && other.voucherId != null) || (this.voucherId != null && !this.voucherId.equals(other.voucherId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Voucher[ id=" + voucherId + " ]";
    }
    
}
