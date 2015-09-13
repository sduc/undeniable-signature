package ch.epfl.lasec.universitycontest;

import static org.junit.Assert.*;

import java.text.ParseException;

import org.junit.Test;

public class RiddleTest {

	@Test
	public void testEncoded() {
		try {
			Riddle r = new Riddle("What is your quest?", "2011-12-12", "Riddle 1");
			assertEquals(r, Riddle.fromEncoded(r.getEncoded()));
			r.setAnswer("To seek the holy grail");
			assertEquals(r, Riddle.fromEncoded(r.getEncoded()));
		} catch (ParseException e) {}
	}

}
