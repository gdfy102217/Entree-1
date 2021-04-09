/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Transaction;
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
public interface TransactionSessionBeanLocal
{

    public Long createCashOutTransaction(Transaction newTransaction, Long restaurantId) throws CreateTransactionException, UnknownPersistenceException, RestaurantNotFoundException, InputDataValidationException;
    
}
