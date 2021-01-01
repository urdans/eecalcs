package tools;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 This class contains and manage messages that results from any calculations
 or validation performed by this package tool. Negative message number are
 considered errors that impeaches proper calculation. Positive messages numbers
 are warnings that doesn't affect the calculation result but that the user
 needs to be aware of.
 */
public class ResultMessages implements ROResultMessages {
	private final List<Message> messages =  new ArrayList<>();
	//private Message[] mensaje;

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

	@Override
	public String getMessage(int number) {
		for(Message msg: messages){
			if(msg.number == number) return msg.message;
		}
		return "";
	}

	@Override
	public boolean containsMessage(int number){
		return !getMessage(number).equals("");
	}

	@Override
	public boolean containsMessage(Message msg){
		return messages.contains(msg);
	}

	@Override
	public boolean hasMessages() {
		return messages.size() > 0;
	}

	@Override
	public boolean hasErrors() {
		return errorCount() > 0;
	}

	@Override
	public boolean hasWarnings() {
		return warningCount() > 0;
	}

	@Override
	public int errorCount() {
		int result = 0;
		for(Message msg: messages){
			if(msg.number < 0) result++;
		}
		return result;
	}

	@Override
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

	@Override
	public List<Message> getMessages() {
		List<Message> m = new ArrayList<>();
		for (Message mes:messages) {
			m.add(new Message(mes.message, mes.number));
		}
		return m;
	}

	/**
	 Clear all the registered messages in this container.
	 */
	public void clearMessages(){
		messages.clear();
	}

	/**
	 Copies all the messages from the given source into this object
	 @param source The ResultMessages object from which messages are copied.
	 @return The number of messages copied.
	 */
	public int copyFrom(@NotNull ResultMessages source){
		int count = 0;
		for(Message sourceMessage: source.getMessages()) {
			if (!containsMessage(sourceMessage.number)){
				count++;
				add(sourceMessage);
			}
		}
		return count;
	}
}


