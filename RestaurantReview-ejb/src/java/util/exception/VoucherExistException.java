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
public class VoucherExistException extends Exception {

    /**
     * Creates a new instance of <code>VoucherExistException</code> without
     * detail message.
     */
    public VoucherExistException() {
    }

    /**
     * Constructs an instance of <code>VoucherExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public VoucherExistException(String msg) {
        super(msg);
    }
}
