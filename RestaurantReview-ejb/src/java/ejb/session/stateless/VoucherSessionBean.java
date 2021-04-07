/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import entity.CustomerVoucher;
import entity.Restaurant;
import entity.Voucher;
import java.util.List;
import java.util.Set;
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
import util.exception.CustomerNotFoundException;
import util.exception.CustomerVoucherExistException;
import util.exception.CustomerVoucherNotFoundException;
import util.exception.CustomerVoucherRedeemedException;
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
    public CustomerVoucher createNewCustomerVoucher(CustomerVoucher newCustomerVoucher, Long voucherId, Long customerId) 
            throws UnknownPersistenceException, InputDataValidationException, CreateNewCustomerVoucherException, CustomerVoucherExistException
    {
        Set<ConstraintViolation<CustomerVoucher>> constraintViolations = validator.validate(newCustomerVoucher);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                Customer owner = customerSessionBeanLocal.retrieveCustomerById(customerId);
                Voucher voucher = retrieveVoucherById(voucherId);
                
                em.persist(newCustomerVoucher);
                newCustomerVoucher.setOwner(owner);
                owner.getCustomerVouchers().add(newCustomerVoucher);
                newCustomerVoucher.setVoucher(voucher);
                voucher.getCustomerVouchers().add(newCustomerVoucher);
                
                em.flush();

                return newCustomerVoucher;
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
    public CustomerVoucher retrieveCustomerVoucherBySixDigitCode(String sixDigitCode) throws CustomerVoucherNotFoundException
    {
        Query query = em.createQuery("SELECT cv FROM CustomerVoucher cv WHERE cv.sixDigitCode = :inSixDigitCode");
        query.setParameter("inSixDigitCode", sixDigitCode); 
        
        try 
        {
            CustomerVoucher customerVoucherToRedeem = (CustomerVoucher) query.getSingleResult();
            customerVoucherToRedeem.getVoucher();
            customerVoucherToRedeem.getTransaction();
            customerVoucherToRedeem.getOwner();
            
            return customerVoucherToRedeem;
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new CustomerVoucherNotFoundException("Customer voucher with code " + sixDigitCode + " does not exist!");
        }              
    }
    
    @Override
    public Restaurant redeemCustomerVoucher(String sixDigitCode, Long restaurantId) throws CustomerVoucherNotFoundException, RestaurantNotFoundException, CustomerVoucherRedeemedException
    {
        try 
        {
            Restaurant restaurant = restaurantSessionBeanLocal.retrieveRestaurantById(restaurantId);
            CustomerVoucher customerVoucherToRedeem = retrieveCustomerVoucherBySixDigitCode(sixDigitCode);
            if (customerVoucherToRedeem.getRedeemed()) 
            {
                throw new CustomerVoucherRedeemedException();
            }
            else
            {
                System.out.println("Start redeem");
                customerVoucherToRedeem.setRedeemed(true);
                customerVoucherToRedeem.setRestaurant(restaurant);
                Double transferCredit = customerVoucherToRedeem.getVoucher().getAmountRedeemable().doubleValue();
                System.out.println("Start credit");
                restaurant.setCreditAmount(restaurant.getCreditAmount() + transferCredit);
                restaurant.getCustomerVouchers().add(customerVoucherToRedeem);
                return restaurant;
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
        Query query = em.createQuery("SELECT cv FROM CustomerVoucher cv WHERE cv.owner.useId = :inCustomerId ORDER BY cv.customerVoucherId ASC");
        query.setParameter("inCustomerId", customerId);        
        
        try {
            List<CustomerVoucher> customerVouchers = query.getResultList();

            for(CustomerVoucher customerVoucher: customerVouchers)
            {
                customerVoucher.getVoucher();
                customerVoucher.getTransaction();
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
