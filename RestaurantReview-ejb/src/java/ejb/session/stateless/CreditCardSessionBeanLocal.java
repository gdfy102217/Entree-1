/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CreditCard;
import java.util.List;
import javax.ejb.Local;
import util.exception.CreateNewCreditCardException;
import util.exception.CreditCardExistException;
import util.exception.CreditCardNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author fengyuan
 */
@Local
public interface CreditCardSessionBeanLocal {

    public CreditCard createNewCreditCard(CreditCard newCreditCard, Long customerId) throws UnknownPersistenceException, InputDataValidationException, CreateNewCreditCardException, CreditCardExistException;

    public List<CreditCard> retrieveAllCreditCards();

    public CreditCard retrieveCreditCardById(Long creditCardId) throws CreditCardNotFoundException;

    public void deleteCreditCard(Long creditCardId) throws CreditCardNotFoundException;

    public List<CreditCard> retrieveCreditCardsByCustomerId(Long customerId);
    
}
