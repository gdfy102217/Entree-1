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
public class PromotionExistException extends Exception {

    /**
     * Creates a new instance of <code>PromotionExistException</code> without
     * detail message.
     */
    public PromotionExistException() {
    }

    /**
     * Constructs an instance of <code>PromotionExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public PromotionExistException(String msg) {
        super(msg);
    }
}
