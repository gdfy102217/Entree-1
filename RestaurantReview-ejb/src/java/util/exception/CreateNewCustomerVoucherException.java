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
public class CreateNewCustomerVoucherException extends Exception {

    /**
     * Creates a new instance of <code>CreateNewCustomerVoucherException</code>
     * without detail message.
     */
    public CreateNewCustomerVoucherException() {
    }

    /**
     * Constructs an instance of <code>CreateNewCustomerVoucherException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateNewCustomerVoucherException(String msg) {
        super(msg);
    }
}
