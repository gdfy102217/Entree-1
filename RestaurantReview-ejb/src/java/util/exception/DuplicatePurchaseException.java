/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author zhiliangwang
 */
public class DuplicatePurchaseException extends Exception
{

    /**
     * Creates a new instance of <code>DuplicatePurchaseException</code> without
     * detail message.
     */
    public DuplicatePurchaseException()
    {
    }

    /**
     * Constructs an instance of <code>DuplicatePurchaseException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public DuplicatePurchaseException(String msg)
    {
        super(msg);
    }
}
