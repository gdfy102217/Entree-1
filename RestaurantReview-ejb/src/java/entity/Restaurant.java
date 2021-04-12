/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Restaurant extends User implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @NotNull
    @Column(nullable = false)
    private String name;
    
    @NotNull
    @Column(nullable = false)
    private String address;
    
    @NotNull
    @Column(nullable = false)
    @Size(max = 6, min = 6)
    private String postalCode;
    
    @NotNull
    @Column(nullable = false)
    @Size(max = 8, min = 8)
    private String contactNumber;
    
    private List<String> photos;
    
    @NotNull
    @Column(nullable = false)
    private Boolean acceptReservation;
    
    @Max(23)
    @Min(0)
    private Integer openTime;
    
    @Max(23)
    @Min(0)
    private Integer closeTime;
    
    private double creditAmount;
    
    private String description;
    
    @OneToOne
    private BankAccount bankAccount;
    
    @OneToOne(optional = true)
    private TableConfiguration tableConfiguration;
    
    
    @OneToMany
    private List<Dish> dishes;
    
    @OneToMany(mappedBy = "restaurant")
    private List<Reservation> reservations;
    
    @OneToMany
    private List<Promotion> promotions;
    
    @OneToMany(mappedBy = "receiver")
    private List<Review> reviews;
    
    @OneToMany(mappedBy = "restaurant")
    private List<Transaction> transactions;
    
    @OneToMany(mappedBy = "restaurant")
    private List<CustomerVoucher> customerVouchers;
    
    public Restaurant() {
        super();
        dishes = new ArrayList<>();
        reservations = new ArrayList<>();
        promotions = new ArrayList<>();
        reviews = new ArrayList<>();
        transactions = new ArrayList<>();
        acceptReservation = Boolean.TRUE;
        this.photos = new ArrayList<>();
        this.creditAmount = 0.0;
        this.bankAccount = null;
        customerVouchers = new ArrayList<>();
        this.photos = new ArrayList<>();
    }

    public Restaurant(String email, String password, String name, String address, 
            String postalCode, String contactNumber, Boolean acceptReservation, 
            String description, Integer openTime, Integer closeTime) {
        super(email, password);
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.contactNumber = contactNumber;
        this.photos = new ArrayList<>();
        this.acceptReservation = acceptReservation;
        this.creditAmount = 0.0;
        this.description = description;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.bankAccount = null;
    }

    
    public Long getId() {
        return super.getUserId();
    }

    public void setId(Long id) {
        super.setUserId(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public Boolean getAcceptReservation() {
        return acceptReservation;
    }

    public void setAcceptReservation(Boolean acceptReservation) {
        this.acceptReservation = acceptReservation;
    }

    public double getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(double creditAmount) {
        this.creditAmount = creditAmount;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public TableConfiguration getTableConfiguration() {
        return tableConfiguration;
    }

    public void setTableConfiguration(TableConfiguration tableConfiguration) {
        this.tableConfiguration = tableConfiguration;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public List<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<Promotion> promotions) {
        this.promotions = promotions;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
    
    public Integer getOpenTime()
    {
        return openTime;
    }

    public void setOpenTime(Integer openTime)
    {
        this.openTime = openTime;
    }

    public Integer getCloseTime()
    {
        return closeTime;
    }

    public void setCloseTime(Integer closeTime)
    {
        this.closeTime = closeTime;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Restaurant)) {
            return false;
        }
        Restaurant other = (Restaurant) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Restaurant[ id=" + this.getId() + " ]";
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public List<CustomerVoucher> getCustomerVouchers()
    {
        return customerVouchers;
    }

    public void setCustomerVouchers(List<CustomerVoucher> customerVouchers)
    {
        this.customerVouchers = customerVouchers;
    }
    
}
