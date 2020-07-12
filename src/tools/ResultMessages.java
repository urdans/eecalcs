package tools;

import java.util.ArrayList;
import java.util.List;

/**
 This class contains and manage messages that results from any calculations
 or validation performed by this package tool. Negative message number are
 considered errors that impeaches proper calculation. Positive messages numbers
 are warnings that doesn't affect the calculation result but that the user
 needs to be aware of.
 */
public class ResultMessages {
	private List<Message> messages =  new ArrayList<>();

	/**
	 Adds a new message to this result message container; if the message number
	 already exists, nothing is added and the existing message will remain.
	 Negative numbers are considered errors, while positive numbers are
	 considered warnings.

	 @param resultMessage The text of the message.
	 @param number The number of the message.
	 @see Message
	 */
	public void add(String resultMessage, int number) {
		for(Message msg: messages){
			if(msg.number == number) return;
		}
		messages.add(new Message(resultMessage, number));
	}

	/**
	 Adds a message object to this result message container.

	 @param msg The existing message object to be added to this result message
	 container.

	 @see Message
	 */
	public void add(Message msg){
		if(messages.contains(msg)) return;
		messages.add(msg);
	}

	/**
	 Gets the message string corresponding to the given message number.

	 @param number The number of the message.
	 @return The message string. If the number if not in the container returns
	 an empty string.
	 */
	public String getMessage(int number) {
		for(Message msg: messages){
			if(msg.number == number) return msg.message;
		}
		return "";
	}

	/**
	 Asks if this result message container already contains the given message
	 number.

	 @param number The message number to check.
	 @return True if this container contains that message number,
	 false otherwise.
	 */
	public boolean containsMessage(int number){
		return !getMessage(number).equals("");
	}

	/**
	 Asks if this result message container already contains the given message
	 object.

	 @param msg The message object to check.
	 @return True if this container contains that message object, false
	 otherwise.
	 */
	public boolean containsMessage(Message msg){
		return messages.contains(msg);
	}

	/**
	 Asks if the container has messages.

	 @return True if there is at least one message in the container, false
	 otherwise.
	 */
	public boolean hasMessages() {
		return messages.size() > 0;
	}

	/**
	 Asks if the container has error messages, that is, any message whose number
	 is negative.

	 @return True if there is at least one error message, false otherwise.
	 */
	public boolean hasErrors() {
		return errorCount() > 0;
	}

	/**
	 Asks if the container has warning messages, that is, any message whose
	 number is positive.

	 @return True if there is at least one warning message, false otherwise.
	 */
	public boolean hasWarnings() {
		return warningCount() > 0;
	}

	/**
	 Returns the number of error messages in this container.

	 @return The number of error messages in this container.
	 */
	public int errorCount() {
		int result = 0;
		for(Message msg: messages){
			if(msg.number < 0) result++;
		}
		return result;
	}

	/**
	 Returns the number of warning messages in this container.

	 @return The number of warning messages in this container.
	 */
	public int warningCount() {
		int result = 0;
		for(Message msg: messages){
			if(msg.number > 0) result++;
		}
		return result;
	}

	/**
	 Removes from this result message container the message identified with the
	 given number.

	 @param number The number of the message to be removed from this container.
	 */
	public void remove(int number){
		for(Message msg : messages){
			if(msg.number == number) {
				messages.remove(msg);
				break;
			}
		}
	}

	/**
	 Removes a message object from this result message container.

	 @param msg The existing message object to be removed from this result
	 message container.
	 @see Message
	 */
	public void remove(Message msg){
		messages.remove(msg);
	}

	/**
	 Returns the list containing all the registered message objects.

	 @return The list containing all the registered message objects.
	 */
	public List<Message> getMessages() {
		return messages;
	}

	/**
	 Clear all the registered messages in this container.
	 */
	public void clearMessages(){
		messages.clear();
	}
}


