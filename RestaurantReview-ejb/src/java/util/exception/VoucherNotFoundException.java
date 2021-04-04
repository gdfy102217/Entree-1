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
public class VoucherNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>VoucherNotFoundException</code> without
     * detail message.
     */
    public VoucherNotFoundException() {
    }

    /**
     * Constructs an instance of <code>VoucherNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public VoucherNotFoundException(String msg) {
        super(msg);
    }
}
