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
public class BankAccountNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>BankAccountNotFoundException</code>
     * without detail message.
     */
    public BankAccountNotFoundException() {
    }

    /**
     * Constructs an instance of <code>BankAccountNotFoundException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public BankAccountNotFoundException(String msg) {
        super(msg);
    }
}
