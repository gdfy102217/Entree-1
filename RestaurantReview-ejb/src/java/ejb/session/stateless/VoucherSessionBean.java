/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import entity.CustomerVoucher;
import entity.Restaurant;
import entity.SaleTransaction;
import entity.Voucher;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CreateNewCustomerVoucherException;
import util.exception.CreateTransactionException;
import util.exception.CustomerNotFoundException;
import util.exception.CustomerVoucherExistException;
import util.exception.CustomerVoucherExpiredException;
import util.exception.CustomerVoucherNotFoundException;
import util.exception.CustomerVoucherRedeemedException;
import util.exception.DuplicatePurchaseException;
import util.exception.InputDataValidationException;
import util.exception.RestaurantNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.VoucherExistException;
import util.exception.VoucherNotFoundException;

/**
 *
 * @author fengyuan
 */
@Stateless
public class VoucherSessionBean implements VoucherSessionBeanLocal {

    @EJB
    private SaleTransactionSessionBeanLocal transactionSessionBeanLocal;

    @EJB
    private RestaurantSessionBeanLocal restaurantSessionBeanLocal;

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;
    
    
    @PersistenceContext(unitName = "RestaurantReview-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public VoucherSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public Voucher createNewVoucher(Voucher newVoucher) throws UnknownPersistenceException, InputDataValidationException, VoucherExistException
    {
        Set<ConstraintViolation<Voucher>>constraintViolations = validator.validate(newVoucher);
        
        if(constraintViolations.isEmpty())
        {
            try
            { 
                em.persist(newVoucher);      
                em.flush();

                return newVoucher;
            }
            catch(PersistenceException ex)
            {
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                    {
                        throw new VoucherExistException();
                    }
                    else
                    {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                }
                else
                {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        }
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public Long createNewCustomerVoucher(CustomerVoucher newCustomerVoucher, Long voucherId, Long customerId) 
            throws UnknownPersistenceException, InputDataValidationException, CreateNewCustomerVoucherException, CustomerVoucherExistException,DuplicatePurchaseException
    {
        Set<ConstraintViolation<CustomerVoucher>> constraintViolations = validator.validate(newCustomerVoucher);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                Customer owner = customerSessionBeanLocal.retrieveCustomerById(customerId);
                Voucher voucher = retrieveVoucherById(voucherId);
                
                for(CustomerVoucher cv: voucher.getCustomerVouchers()) 
                {
                    if(cv.getOwner().getUserId().equals(customerId))
                    {
                        throw new DuplicatePurchaseException("Each customer can only purchase the same voucher once.");
                    }
                }
                
                em.persist(newCustomerVoucher);
                SaleTransaction newSaleTransaction = new SaleTransaction(voucher.getPrice(), new Date());
                
//                Long transactionId = transactionSessionBeanLocal.createTransactionForVoucher(newSaleTransaction, customerId, newCustomerVoucher);

                em.persist(newSaleTransaction);
                
                newSaleTransaction.setCustomer(owner);
                owner.getTransactions().add(newSaleTransaction);
                newSaleTransaction.setCreditCard(owner.getCreditCard());
//                owner.getCreditCard().setTransaction(newSaleTransaction);
                newSaleTransaction.setCustomerVoucher(newCustomerVoucher);
//                newSaleTransaction.getCustomerVouchers().add(newCustomerVoucher);
                newCustomerVoucher.setSaleTransaction(newSaleTransaction);
                
                newCustomerVoucher.setOwner(owner);
                owner.getCustomerVouchers().add(newCustomerVoucher);
                newCustomerVoucher.setVoucher(voucher);
                voucher.getCustomerVouchers().add(newCustomerVoucher);
                newCustomerVoucher.setRedeemed(Boolean.FALSE);
                
                em.flush();

                return newSaleTransaction.getTransactionId();
            }
            catch(PersistenceException ex)
            {
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                    {
                        throw new CustomerVoucherExistException();
                    }
                    else
                    {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                }
                else
                {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
            catch(CustomerNotFoundException | VoucherNotFoundException ex)
            {
                throw new CreateNewCustomerVoucherException("An error has occurred while creating the new customer voucher: " + ex.getMessage());
            }
        }
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessageCustomerVoucher(constraintViolations));
        }
    }
    
    @Override
    public List<Voucher> retrieveAllVouchers()
    {
        Query query = em.createQuery("SELECT v FROM Voucher v ORDER BY v.voucherId ASC");       
        List<Voucher> vouchers = query.getResultList();
        
        for(Voucher voucher: vouchers)
        {
//            reservation.getCategoryEntity();
//            reservation.getTagEntities().size();
        }
        
        return vouchers;
    }
    
    @Override
    public Voucher retrieveVoucherById(Long voucherId) throws VoucherNotFoundException
    {
        Voucher voucher = em.find(Voucher.class, voucherId);
        
        if(voucher != null)
        {
//            productEntity.getCategoryEntity();
//            productEntity.getTagEntities().size();
            
            return voucher;
        }
        else
        {
            throw new VoucherNotFoundException("Voucher ID " + voucherId + " does not exist!");
        }               
    }
    
    @Override
    public CustomerVoucher retrieveCustomerVoucherById(Long customerVoucherId) throws CustomerVoucherNotFoundException
    {
        CustomerVoucher customerVoucher = em.find(CustomerVoucher.class, customerVoucherId);
        
        if(customerVoucher != null)
        {
//            productEntity.getCategoryEntity();
//            productEntity.getTagEntities().size();
            
            return customerVoucher;
        }
        else
        {
            throw new CustomerVoucherNotFoundException("Customer Voucher ID " + customerVoucherId + " does not exist!");
        }               
    }
    
    @Override
    public CustomerVoucher retrieveCustomerVoucherBySixDigitCode(String sixDigitCode) throws CustomerVoucherNotFoundException
    {

        try 
        {
            Query query = em.createQuery("SELECT cv FROM CustomerVoucher cv WHERE cv.sixDigitCode = :inSixDigitCode");
            query.setParameter("inSixDigitCode", sixDigitCode); 
            
            System.out.println("****** Voucher Session Bean ****** Customer voucher to be redeemed found!!!!");
            
            CustomerVoucher customerVoucherToRedeem = (CustomerVoucher) query.getSingleResult();
//            customerVoucherToRedeem.getVoucher();
//            customerVoucherToRedeem.getSaleTransaction();
//            customerVoucherToRedeem.getOwner();
            
            return customerVoucherToRedeem;
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new CustomerVoucherNotFoundException("Customer voucher with code " + sixDigitCode + " does not exist!");
        }              
    }
    
    @Override
    public Restaurant redeemCustomerVoucher(String sixDigitCode, Long restaurantId) 
            throws CustomerVoucherNotFoundException, RestaurantNotFoundException, CustomerVoucherRedeemedException, CustomerVoucherExpiredException
    {
        try 
        {
            
            Restaurant restaurant = restaurantSessionBeanLocal.retrieveRestaurantById(restaurantId);
            CustomerVoucher customerVoucherToRedeem = retrieveCustomerVoucherBySixDigitCode(sixDigitCode);
            System.out.println("****** Voucher Session Bean ****** Restaurant ID: " + restaurant.getUserId());
            System.out.println("****** Voucher Session Bean ****** Customer Voucher ID: " + customerVoucherToRedeem.getOwner().getFirstName());
            CustomerVoucher cvToUpdate = retrieveCustomerVoucherById(customerVoucherToRedeem.getCustomerVoucherId());
            
                
            
            if (customerVoucherToRedeem.getRedeemed()) 
            {
                throw new CustomerVoucherRedeemedException();
            } 
            else if (customerVoucherToRedeem.getVoucher().getExpiryDate().isBefore(LocalDate.now()))
            {
                throw new CustomerVoucherExpiredException();
            }
            else
            {
                customerVoucherToRedeem.setRedeemed(Boolean.TRUE);
                customerVoucherToRedeem.setRestaurant(restaurant);
                BigDecimal transferCredit = customerVoucherToRedeem.getVoucher().getAmountRedeemable();
                System.out.println("Start credit");
                BigDecimal newAmount = restaurant.getCreditAmount().add(transferCredit);
                restaurant.setCreditAmount(newAmount);
                System.out.println("Credit tranferred!");
                restaurant.getCustomerVouchers().add(customerVoucherToRedeem);
                System.out.println("Added to customer voucher list!");
                return restaurant;
                
//                cvToUpdate.setRedeemed(Boolean.TRUE);
//                cvToUpdate.setRestaurant(restaurant);
//                BigDecimal transferCredit = cvToUpdate.getVoucher().getAmountRedeemable();
//                System.out.println("Start credit");
//                BigDecimal newAmount = restaurant.getCreditAmount().add(transferCredit);
//                restaurant.setCreditAmount(newAmount);
//                System.out.println("Credit tranferred!");
//                restaurant.getCustomerVouchers().add(cvToUpdate);
//                System.out.println("Added to customer voucher list!");
//                return restaurant;
            }
            
            
        }
        catch(CustomerVoucherNotFoundException ex)
        {
            throw new CustomerVoucherNotFoundException("Customer voucher with code " + sixDigitCode + " does not exist!");
        }
        catch(RestaurantNotFoundException ex)
        {
            throw new RestaurantNotFoundException("Restaurant with ID " + restaurantId + " is not found!");
        }
    }
    
    @Override
    public List<CustomerVoucher> retrieveAllCustomerVouchersByCustomerId(Long customerId) throws CustomerVoucherNotFoundException
    {
        Query query = em.createQuery("SELECT cv FROM CustomerVoucher cv WHERE cv.owner.userId = :inCustomerId ORDER BY cv.voucher.expiryDate ASC");
        query.setParameter("inCustomerId", customerId);        
        
        try {
            List<CustomerVoucher> customerVouchers = query.getResultList();

            for(CustomerVoucher customerVoucher: customerVouchers)
            {
                customerVoucher.getVoucher();
                customerVoucher.getSaleTransaction();
                customerVoucher.getOwner();
            }

            return customerVouchers;
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new CustomerVoucherNotFoundException("Customer with ID " + customerId + " does not have any voucher!");
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Voucher>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
    
    private String prepareInputDataValidationErrorsMessageCustomerVoucher(Set<ConstraintViolation<CustomerVoucher>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
