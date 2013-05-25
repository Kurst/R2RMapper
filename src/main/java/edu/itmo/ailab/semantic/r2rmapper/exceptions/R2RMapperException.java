package edu.itmo.ailab.semantic.r2rmapper.exceptions;


 public class R2RMapperException extends Exception {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -8644631752018296125L;

	public R2RMapperException(String message, Throwable cause)
	{
		super("R2RMapper Exception occured: " + message,cause);
	}
	
	public R2RMapperException(String message)
	{
		super("R2RMapper Exception occured: " + message);
	}
}
