/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.CreditCardSessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import ejb.session.stateless.RestaurantSessionBeanLocal;
import ejb.session.stateless.ReviewSessionBeanLocal;
import ejb.session.stateless.TableConfigurationSessionBeanLocal;
import ejb.session.stateless.VoucherSessionBeanLocal;
import entity.CreditCard;
import entity.Customer;
import entity.Reservation;
import entity.CustomerVoucher;
import entity.Restaurant;
import entity.Review;
import entity.TableConfiguration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import entity.Voucher;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.TableSize;
import util.exception.CreateNewCreditCardException;
import util.exception.CreateNewReservationException;
import util.exception.CreateNewCustomerVoucherException;
import util.exception.CreateNewReviewException;
import util.exception.CreditCardExistException;
import util.exception.CustomerNotFoundException;
import util.exception.CustomerUsernameExistException;
import util.exception.CustomerVoucherExistException;
import util.exception.InputDataValidationException;
import util.exception.ReservationExistException;
import util.exception.RestaurantUsernameExistException;
import util.exception.ReviewExistException;
import util.exception.TableConfigurationExistException;
import util.exception.UnknownPersistenceException;
import util.exception.VoucherExistException;
import util.exception.VoucherNotFoundException;


@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {

    @EJB(name = "CreditCardSessionBeanLocal")
    private CreditCardSessionBeanLocal creditCardSessionBeanLocal;

    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;
    
    @PersistenceContext(unitName = "RestaurantReview-ejbPU")
    private EntityManager em;
    
    
    @EJB
    private RestaurantSessionBeanLocal restaurantSessionBeanLocal;
    
    @EJB
    private VoucherSessionBeanLocal voucherSessionBeanLocal;

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;
    
    @EJB
    private ReviewSessionBeanLocal reviewSessionBeanLocal;
    
    @EJB
    private TableConfigurationSessionBeanLocal tableConfigurationSessionBeanLocal;
    
    
    
    private Long restaurantIdToTest;
    private Long customerIdToTest;
    private Voucher voucherToTest;
    
    @PostConstruct
    public void postConstruct()
    {
        if(em.find(Customer.class, 1l) == null)
        {
            initializeCustomerData();
        }
        if(em.find(Restaurant.class, 3l) == null)
        {
            initializeRestaurantData();
        }
        if(em.find(Reservation.class, 1l) == null)
        {
            initializeReservationData();
        }
        if(em.find(Voucher.class, 1l) == null)
        {
            initializeVoucherData();
        }
        if(em.find(CustomerVoucher.class, 1l) == null)
        {
            initializeCustomerVoucherData();
        }
        if(em.find(Review.class, 1l) == null)
        {
            initializeReviewData();
        }

    }
    
    private void initializeCustomerData()
    {
        try
        {
            this.customerIdToTest = customerSessionBeanLocal.createNewCustomer(new Customer("custone@test.com", "password", "Customer", "One", "12345678"));
            customerSessionBeanLocal.createNewCustomer(new Customer("custotwo@test.com", "password", "Customer", "Two", "87654321"));
        }
        catch(UnknownPersistenceException | InputDataValidationException | CustomerUsernameExistException ex)
        {
            ex.printStackTrace();
        }
    }
    
    private void initializeRestaurantData()
    {
        try
        {
            this.restaurantIdToTest = restaurantSessionBeanLocal.createNewRestaurant(new Restaurant("restone@test.com", "password", "Restaurant One", "kent Ridge 1","111111", "66666666", true, "This is test Restaurant One", 8, 23), new TableConfiguration(2,3,4));
            restaurantSessionBeanLocal.createNewRestaurant(new Restaurant("resttwo@test.com", "password", "Restaurant Two", "kent Ridge 2","111111", "77777777", false, "This is test Restaurant Two", 9, 22), null);
        }
        catch (UnknownPersistenceException | InputDataValidationException | RestaurantUsernameExistException | TableConfigurationExistException ex)
        {
            ex.printStackTrace();
        }
    }
    
    private void initializeReservationData()
    {
        Date currDate = new Date();
        LocalDateTime newLocal = currDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        newLocal = newLocal.plusDays(2);
        Date newDate = Date.from(newLocal.atZone(ZoneId.systemDefault()).toInstant());
        
        LocalDate localDate = LocalDate.now();
        
        LocalTime localTime = LocalTime.now();
        
        try
        {
            reservationSessionBeanLocal.createNewReservation(new Reservation(localDate, localTime, 2, TableSize.SMALL, "noooo"), 1l, 3l, null);
            reservationSessionBeanLocal.createNewReservation(new Reservation(localDate, localTime, 8, TableSize.MEDIUM, "noooo"), 2l, 3l, null);
        }
        catch(UnknownPersistenceException | InputDataValidationException | CreateNewReservationException | ReservationExistException ex)
        {
            ex.printStackTrace();
        }
    }
    
    private void initializeVoucherData() 
    {
        LocalDate localDate = LocalDate.now().plusMonths(3);
        
        try
        {
            this.voucherToTest = voucherSessionBeanLocal.createNewVoucher(new Voucher("Voucher1", localDate, new BigDecimal(10.00), new BigDecimal(9.00), true, "for testing"));
            this.voucherToTest = voucherSessionBeanLocal.createNewVoucher(new Voucher("Voucher2", localDate, new BigDecimal(50.00), new BigDecimal(45.00), true, "for testing"));
        }
        catch (UnknownPersistenceException | InputDataValidationException | VoucherExistException ex)
        {
            ex.printStackTrace();
        }
    }
    
    private void initializeCustomerVoucherData()
    {
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/yy");
            String strDate = "10/23";
            Date newDate = sdf.parse(strDate);
//            LocalDate localDate = LocalDate.parse(strDate);
             
            voucherSessionBeanLocal.createNewCustomerVoucher(new CustomerVoucher(false, new Date(new Date().getTime() + (60 * 60 * 1000))), voucherToTest.getVoucherId(), customerIdToTest);
            voucherSessionBeanLocal.createNewCustomerVoucher(new CustomerVoucher(false, new Date(new Date().getTime() + (60 * 60 * 1000))), voucherToTest.getVoucherId(), customerIdToTest);
            
            creditCardSessionBeanLocal.createNewCreditCard(new CreditCard("1111222233334444", "123", newDate, "Cust One"), 1L);
            
        }
        catch (UnknownPersistenceException | InputDataValidationException | CreateNewCustomerVoucherException | CustomerVoucherExistException |
                CreateNewCreditCardException | CreditCardExistException | ParseException ex)
        {
            ex.printStackTrace();
        }
        
    }
    
    private void initializeReviewData()
    {
        try
        {
            reviewSessionBeanLocal.createNewReviewForRestaurant(new Review("testing1", 5, new ArrayList<>()), customerIdToTest, restaurantIdToTest);
            reviewSessionBeanLocal.createNewReviewForRestaurant(new Review("testing2", 4, new ArrayList<>()), customerIdToTest, restaurantIdToTest);
            reviewSessionBeanLocal.createNewReviewForRestaurant(new Review("testing3", 3, new ArrayList<>()), customerIdToTest, restaurantIdToTest);
            reviewSessionBeanLocal.createNewReviewForRestaurant(new Review("testing4", 2, new ArrayList<>()), customerIdToTest, restaurantIdToTest);
            reviewSessionBeanLocal.createNewReviewForRestaurant(new Review("testing5", 1, new ArrayList<>()), customerIdToTest, restaurantIdToTest);
        }
        catch (UnknownPersistenceException | InputDataValidationException | ReviewExistException | CreateNewReviewException ex)
        {
            ex.printStackTrace();
        }
    }

//    public void persist(Object object)
//    {
//        em.persist(object);
//    }
}