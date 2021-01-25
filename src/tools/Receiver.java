package tools;

/**
 This interface is intended to be used as means of communication between two
 objects. If the object "myObject" is an instance of a class that implements
 this interface, it means that myObject can receive messages from any other
 arbitrary object "callerObject" that has a reference to myObject.<br>

 callerObject calls myObject.messaging(callerObject, message) passing a
 reference to itself and an object intended to be treated as container for a
 message by myObject.

 myObject.messaging() checks for the parameters and decides to ignore the
 call or to respond.<br>

 myObject.messaging() responds by placing a response into the message
 container, so right after callerObject calls myObject.messaging can check
 for the response in the message object.

 if myObject.messaging() returns true indicating the message has being
 processed or false indicating the message has being ignored.
 */
public interface Receiver {
	/**
	 Receives a message sent by an arbitrary object. This object can respond
	 to the message by overwriting the content of the Message fields id and
	 container with the proper message information.
	 @param sender The object that sends the message
	 @param message Container for the message. Refer to {@link Message} for
	 details.
	 @return True if the message sent by the sender was processed, or false if
	 the message has been ignored.
	 */
	boolean messaging(Object sender, Message message);
}
