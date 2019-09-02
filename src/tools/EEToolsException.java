package tools;

/**
 * Runtime exceptions for electrical tools
 */
public class EEToolsException extends RuntimeException {
	EEToolsException(){}

	/**
	 * Constructs a personalized message runtime exception
	 * @param str The error message.
	 */
	public EEToolsException(String str){
		super(str);
	}
}
