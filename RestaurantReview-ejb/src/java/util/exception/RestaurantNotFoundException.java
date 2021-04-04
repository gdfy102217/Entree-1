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
public class RestaurantNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>RestaurantNotFoundException</code>
     * without detail message.
     */
    public RestaurantNotFoundException() {
    }

    /**
     * Constructs an instance of <code>RestaurantNotFoundException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public RestaurantNotFoundException(String msg) {
        super(msg);
    }
}
