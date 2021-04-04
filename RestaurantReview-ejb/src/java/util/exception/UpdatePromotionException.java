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
public class UpdatePromotionException extends Exception {

    /**
     * Creates a new instance of <code>UpdatePromotionException</code> without
     * detail message.
     */
    public UpdatePromotionException() {
    }

    /**
     * Constructs an instance of <code>UpdatePromotionException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UpdatePromotionException(String msg) {
        super(msg);
    }
}
