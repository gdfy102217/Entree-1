/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.BankAccount;
import entity.Restaurant;
import java.util.List;
import javax.ejb.Local;
import util.exception.BankAccountExistException;
import util.exception.BankAccountNotFoundException;
import util.exception.CreateNewBankAccountException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author fengyuan
 */
@Local
public interface BankAccountSessionBeanLocal {

    public Restaurant createNewBankAccount(BankAccount newBankAccount, Long restaurantId) throws UnknownPersistenceException, InputDataValidationException, CreateNewBankAccountException, BankAccountExistException;

    public List<BankAccount> retrieveAllBankAccounts();

    public BankAccount retrieveBankAccountById(Long bankAccountId) throws BankAccountNotFoundException;

    public void deleteBankAccount(Long bankAccountId) throws BankAccountNotFoundException;
    
}
