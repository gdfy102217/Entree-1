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
public class CreateNewBankAccountException extends Exception {

    /**
     * Creates a new instance of <code>CreateNewBankAccountException</code>
     * without detail message.
     */
    public CreateNewBankAccountException() {
    }

    /**
     * Constructs an instance of <code>CreateNewBankAccountException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateNewBankAccountException(String msg) {
        super(msg);
    }
}
