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
public class BankAccountExistException extends Exception {

    /**
     * Creates a new instance of <code>BankAccountExistException</code> without
     * detail message.
     */
    public BankAccountExistException() {
    }

    /**
     * Constructs an instance of <code>BankAccountExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public BankAccountExistException(String msg) {
        super(msg);
    }
}
