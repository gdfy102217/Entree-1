/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author nichelle
 */
public class DeleteDishException extends Exception {

    /**
     * Creates a new instance of <code>DeleteDishException</code> without detail
     * message.
     */
    public DeleteDishException() {
    }

    /**
     * Constructs an instance of <code>DeleteDishException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteDishException(String msg) {
        super(msg);
    }
}
