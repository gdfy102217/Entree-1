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
public class CreateNewPromotionException extends Exception {

    /**
     * Creates a new instance of <code>CreateNewPromotionException</code>
     * without detail message.
     */
    public CreateNewPromotionException() {
    }

    /**
     * Constructs an instance of <code>CreateNewPromotionException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateNewPromotionException(String msg) {
        super(msg);
    }
}
