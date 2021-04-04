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
public class TableConfigurationNotFoundException extends Exception {

    /**
     * Creates a new instance of
     * <code>TableConfigurationNotFoundException</code> without detail message.
     */
    public TableConfigurationNotFoundException() {
    }

    /**
     * Constructs an instance of
     * <code>TableConfigurationNotFoundException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public TableConfigurationNotFoundException(String msg) {
        super(msg);
    }
}
