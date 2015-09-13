package ch.epfl.lasec.mova;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * class representing a message.
 * A message can be created from a sequence of bytes.
 * It can also be created from a sequence of characters (string).
 * 
 * @author Sebastien Duc
 *
 */
public class Message {
	
	private byte [] message;
	
	/**
	 * Charset used to encode the messages.
	 */
	public static final String CHARSET = "UTF-8";
	
	/**
	 * Create a message from a sequence of bytes
	 * @param m sequence of bytes that represent a message.
	 */
	public Message(byte [] m){
		this.message = Arrays.copyOf(m, m.length);
	}
	
	/**
	 * Creates a message from a string.
	 * @param m string message
	 */
	public Message(String m){
		try {
			this.message = m.getBytes(Message.CHARSET);
		} catch (UnsupportedEncodingException e) {
			assert false;
		}
	}
	
	/**
	 * Constructor of copy for messages.
	 * @param m Message to copy
	 */
	public Message(Message m) {
		this.message = Arrays.copyOf(m.message, m.message.length);
	}
	
	/**
	 * Get the encoded version of the message
	 * @return
	 */
	public byte [] getEncoded(){
		return message;
	}

	@Override
	public String toString() {
		String s = null;
		try {
			s = new String(this.message, Message.CHARSET);
		} catch (UnsupportedEncodingException e) {
			assert false;
		}
		return s;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}
		if(obj == this){
			return true;
		}
		if(obj.getClass() != this.getClass()){
			return false;
		}
		Message m = (Message) obj;
		return Arrays.equals(m.message, this.message);
	}
	
	
}
