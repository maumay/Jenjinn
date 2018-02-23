/**
 * 
 */
package jenjinn.engine.exceptions;

/**
 * @author t
 *
 */
public class AmbiguousPgnException extends Exception
{

	private static final long serialVersionUID = -822852476447898443L;

	/**
	 * 
	 */
	public AmbiguousPgnException()
	{
	}

	/**
	 * @param message
	 */
	public AmbiguousPgnException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public AmbiguousPgnException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public AmbiguousPgnException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public AmbiguousPgnException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
