/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CustomerVoucher;
import entity.SaleTransaction;
import javax.ejb.Local;
import util.exception.CreateTransactionException;
import util.exception.InputDataValidationException;
import util.exception.RestaurantNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author zhiliangwang
 */
@Local
public interface SaleTransactionSessionBeanLocal
{

    public Long createCashOutTransaction(SaleTransaction newTransaction, Long restaurantId) throws CreateTransactionException, UnknownPersistenceException, RestaurantNotFoundException, InputDataValidationException;

    public Long createTransactionForVoucher(SaleTransaction newTransaction, Long customerId, CustomerVoucher customerVoucher) throws CreateTransactionException, UnknownPersistenceException, InputDataValidationException;
    
}
