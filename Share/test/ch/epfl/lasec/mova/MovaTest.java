package ch.epfl.lasec.mova;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.math.BigInteger;

import org.junit.Test;

public class MovaTest {

	@Test
	public void testWrite() {
		PipedInputStream pis = new PipedInputStream();
		PipedOutputStream pos;
		try {
			pos = new PipedOutputStream(pis);
			Mova mova = new Mova(new DomainParameters(10,20,30,40,BigInteger.valueOf(10001)));
			mova.write(pos);
			Mova newMova = Mova.read(pis);
			assertEquals(mova, newMova);
		} catch (IOException e) {
			assertFalse(true);
		}
	}


}
