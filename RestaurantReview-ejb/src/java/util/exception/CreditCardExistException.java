/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author fengyuan
 */
public class CreditCardExistException extends Exception {

    /**
     * Creates a new instance of <code>CreditCardExistException</code> without
     * detail message.
     */
    public CreditCardExistException() {
    }

    /**
     * Constructs an instance of <code>CreditCardExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CreditCardExistException(String msg) {
        super(msg);
    }
}
