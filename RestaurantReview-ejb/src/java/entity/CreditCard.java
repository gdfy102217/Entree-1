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
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;


@Entity
public class CreditCard implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long creditCardId;
    
    @NotNull
    @Column(nullable = false, length = 16)
    private String cardNumber;
    
    @NotNull
    @Column(nullable = false, length = 3)
    private String cvv;
    
    @NotNull
    @Column(nullable = false)
    @FutureOrPresent
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date expiryDate;
    
    @NotNull
    @Column(nullable = false, length = 128)
    private String nameOnCard;
    

    @OneToOne(mappedBy = "creditCards")
    private Customer owner;
    
    @OneToOne(mappedBy = "creditCard")
    private Transaction transaction;

    public CreditCard() {
    }

    public CreditCard(String cardNumber, String cvv, Date expiryDate, String nameOnCard) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
        this.nameOnCard = nameOnCard;
    }
    
    
    public Long getCreditCardId() {
        return creditCardId;
    }

    public void setCreditCardId(Long creditCardId) {
        this.creditCardId = creditCardId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    public Customer getOwner() {
        return owner;
    }

    public void setOwner(Customer owner) {
        this.owner = owner;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (creditCardId != null ? creditCardId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the creditCardId fields are not set
        if (!(object instanceof CreditCard)) {
            return false;
        }
        CreditCard other = (CreditCard) object;
        if ((this.creditCardId == null && other.creditCardId != null) || (this.creditCardId != null && !this.creditCardId.equals(other.creditCardId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.CreditCard[ id=" + creditCardId + " ]";
    }
    
}
