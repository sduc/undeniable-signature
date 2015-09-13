package ch.epfl.lasec.mova;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Class used to unit test the class Message.
 * 
 * @author Sebastien Duc
 *
 */
public class MessageTest {

	@Test
	public void testStringMessage() {
		Message m = new Message("this is a dummy message");
		Message m2 = new Message(m.getEncoded());
		assertEquals(m, m2);
	}

}
