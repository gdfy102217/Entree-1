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
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;


@Entity
public class SaleTransaction implements Serializable {


    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;
    
    @NotNull
    @Column(nullable = false)
    @Digits(integer = 5, fraction = 2)
    private Double paidAmount;
    
    @NotNull
    @Column(nullable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date transactionDate;
    
    @ManyToOne
    private Customer customer;
    
    @ManyToOne
    private Restaurant restaurant;
    
    
    @OneToOne
    private CreditCard creditCard;
    
    @ManyToOne
    private BankAccount bankAccount;
    
//    @OneToMany(mappedBy = "transaction")
//    private List<CustomerVoucher> customerVouchers;
    
    @OneToOne
    private CustomerVoucher customerVoucher;

    public SaleTransaction() {
//        this.customerVouchers = new ArrayList<>();
    }

    public SaleTransaction(Double paidAmount, Date transactionDate) {
        super();
        this.paidAmount = paidAmount;
        this.transactionDate = transactionDate;
    }
    
    

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }
//
//    public List<CustomerVoucher> getCustomerVouchers() {
//        return customerVouchers;
//    }
//
//    public void setCustomerVouchers(List<CustomerVoucher> customerVouchers) {
//        this.customerVouchers = customerVouchers;
//    }
//    
    
        public CustomerVoucher getCustomerVoucher()
    {
        return customerVoucher;
    }

    public void setCustomerVoucher(CustomerVoucher customerVoucher)
    {
        this.customerVoucher = customerVoucher;
    }

    
    

    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (transactionId != null ? transactionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the transactionId fields are not set
        if (!(object instanceof SaleTransaction)) {
            return false;
        }
        SaleTransaction other = (SaleTransaction) object;
        if ((this.transactionId == null && other.transactionId != null) || (this.transactionId != null && !this.transactionId.equals(other.transactionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Transaction[ id=" + transactionId + " ]";
    }
    
}
