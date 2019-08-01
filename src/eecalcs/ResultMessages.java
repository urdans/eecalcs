package eecalcs;


import java.util.ArrayList;
import java.util.List;

/*This class will manage messages that results from any calculation done by this package tool.
Negative message number are considered errors that impeach proper calculation.
Positive messages are warnings that doesn't affect the calculation result*/

public class ResultMessages {

	private List<Message> messages =  new ArrayList<>();

	public void add(String resultMessage, int number) {
		for(Message msg: messages){
			if(msg.number == number) return;
		}
		messages.add(new Message(resultMessage, number));
	}

	public void add(Message msg){
		if(messages.contains(msg)) return;
		messages.add(msg);
	}

	public void add(String subject, Message msg){
		if(messages.contains(msg)) return;
		msg.message = String.format(msg.message,subject);
		messages.add(msg);
	}

	public String getMessage(int number) {
		for(Message msg: messages){
			if(msg.number == number) return msg.message;
		}
		return "";
	}

	public boolean containsMessage(int number){
		return !getMessage(number).equals("");
	}

	public boolean containsMessage(Message msg){
		return messages.contains(msg);
	}

	public boolean hasMessages() {
		return messages.size() > 0;
	}

	public boolean hasErrors() {
		return errorCount() > 0;
	}

	public boolean hasWarnings() {
		return warningCount() > 0;
	}

	public int errorCount() {
		int result = 0;
		for(Message msg: messages){
			if(msg.number < 0) result++;
		}
		return result;
	}

	public int warningCount() {
		int result = 0;
		for(Message msg: messages){
			if(msg.number > 0) result++;
		}
		return result;
	}

	public void remove(int number){
		for(Message msg : messages){
			if(msg.number == number) {
				messages.remove(msg);
				break;
			}
		}
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void clearMessages(){
		messages.clear();
	}
}


