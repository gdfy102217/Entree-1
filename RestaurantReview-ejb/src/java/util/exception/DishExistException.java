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
public class DishExistException extends Exception {

    /**
     * Creates a new instance of <code>DishExistException</code> without detail
     * message.
     */
    public DishExistException() {
    }

    /**
     * Constructs an instance of <code>DishExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DishExistException(String msg) {
        super(msg);
    }
}
