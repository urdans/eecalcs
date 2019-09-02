package tools;

/**
 * This class encapsulates a message and a number resulting from a calculation. It can be an error or a warning, depending on the sign of
 * the message number
 */
public class Message {
	public String message;
	public int number;

	/**
	 * Constructs a message object with a personalized message and number. Negative numbers within eecalcs are considered errors,
	 * while positive message numbers are considered warnings
	 * @param message The string containing the message
	 * @param number The number of the message
	 */
	public Message(String message, int number) {
		this.message = message;
		this.number = number;
	}
}
