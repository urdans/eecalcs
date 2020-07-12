package tools;

/**
 * Runtime exceptions for electrical tools.
 */
public class EEToolsException extends RuntimeException {
	EEToolsException(){}

	/**
	 * Constructs a personalized runtime exception with a message.
	 * @param msg The exception message.
	 */
	public EEToolsException(String msg){
		super(msg);
	}
}
