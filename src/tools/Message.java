package tools;

/**
 This class is intended to be used as a record for passing arbitrary messages
 between objects
 */
public class Message {
	public int id;
	public Object container;

	public Message(int id, Object container) {
		this.id = id;
		this.container = container;
	}
}
