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
public class CreateTransactionException extends Exception
{

    /**
     * Creates a new instance of <code>CreateTransactionException</code> without
     * detail message.
     */
    public CreateTransactionException()
    {
    }

    /**
     * Constructs an instance of <code>CreateTransactionException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateTransactionException(String msg)
    {
        super(msg);
    }
}
