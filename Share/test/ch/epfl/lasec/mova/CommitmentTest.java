package ch.epfl.lasec.mova;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

/**
 * 
 * Unit test to test Commitment scheme.
 * 
 * @author Sebastien Duc
 *
 */
public class CommitmentTest {
	
	@Test
	public void testCommit() {
		//--- Initialization ----
		Random rand = new Random();
		// initialize the random message
		int randomMessageSize = Math.abs(rand.nextInt()) % 10000;
		byte [] randomM = new byte [randomMessageSize];
		rand.nextBytes(randomM);
		// commitment scheme generation
		Commitment commitment = new Commitment();
		
		//--- apply commit and open
		byte [] com = commitment.commit(randomM);
		byte [] decR = commitment.getR();
		
		//-- completeness --
		assertTrue("Error: open(commit(m),m,decR) should return true",
				commitment.open(com, randomM, decR));
		
		//-- soundness --
		byte [] decR2 = new byte [Commitment.RANDOM_BYTE_SIZE];
		do{
			rand.nextBytes(decR2);
		} while (Arrays.equals(decR, decR2));
		
		assertFalse("Error: open(commit(m),m,decR') should return false",
				commitment.open(com, randomM, decR2));
		
		byte [] randomM2 = new byte [randomMessageSize];
		do{
			rand.nextBytes(randomM);
		} while (Arrays.equals(randomM2, randomM));
		
		assertFalse("Error: open(commit(m),m',decR) where m' != m should return false",
				commitment.open(com, randomM2, decR));
		
		// test multiple commit
		com = commitment.commit(randomM2);
		decR2 = commitment.getR();
		assertTrue("Error: commitment doesn't work when used multiple times",
				commitment.open(com, randomM2, decR2));
		assertFalse("Error: decR doesn't change when commitment used multiple times",
				Arrays.equals(decR, decR2));
	}

}
