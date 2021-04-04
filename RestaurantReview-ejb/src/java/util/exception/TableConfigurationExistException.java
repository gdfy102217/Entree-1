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
public class TableConfigurationExistException extends Exception {

    /**
     * Creates a new instance of <code>TableConfigurationExistException</code>
     * without detail message.
     */
    public TableConfigurationExistException() {
    }

    /**
     * Constructs an instance of <code>TableConfigurationExistException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public TableConfigurationExistException(String msg) {
        super(msg);
    }
}
