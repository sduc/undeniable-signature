package ch.epfl.lasec.mova;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for class YGroupElement
 * 
 * @author Sebastien Duc
 *
 */
public class YGroupElementTest {
	
	Random rand;
	YGroupElement [] arbitrarySequence;
	YGroupElement [] multipleOfEightSequence;
	
	@Before
	public void setUp(){
		rand = new Random();
		int size = Math.abs(rand.nextInt() % 50);
		int more = Math.abs(1 + rand.nextInt() % 7);
		arbitrarySequence = new YGroupElement[8*size+more];
		for (int i = 0; i < arbitrarySequence.length; i++) {
			arbitrarySequence[i] = new YGroupElement(rand.nextBoolean());
		}
		multipleOfEightSequence = new YGroupElement[size*5];
		for (int i = 0; i < multipleOfEightSequence.length; i++) {
			multipleOfEightSequence[i] = new YGroupElement(rand.nextBoolean());
		}
	}

	/**
	 * Let's denote YGroupElement.encodeYGroupElementSequence by Enc
	 * and YGroupElement.decodeEncodedYGroupElementSequence by Dec.
	 * Let also x be an arbitrary sequence and x8 be a sequence of length 8*k for some k in N 
	 * In this test we check that:
	 * 		- Enc(Dec(x)) = x
	 * 		- Enc(Dec(x8)) = x8
	 */
	@Test
	public void testEncodeYGroupElementSequence() {
		// Test on arbitrary sequence
		byte [] encoded = YGroupElement.encodeYGroupElementSequence(arbitrarySequence);
		YGroupElement [] decoded = YGroupElement.decodeEncodedYGroupElementSequence(encoded);
		assertArrayEquals("Error: Dec(Enc(x)) != x, when x is arbitrary",
				arbitrarySequence, decoded);
		
		// Test on multiple of 8 length sequence
		encoded = YGroupElement.encodeYGroupElementSequence(multipleOfEightSequence);
		decoded = YGroupElement.decodeEncodedYGroupElementSequence(encoded);
		assertArrayEquals("Error: Dec(Enc(x8)) != x8, when x8 has length multiple of 8",
				multipleOfEightSequence, decoded);
	}
	
	@Test
	public void testEquals(){
		YGroupElement g1 = new YGroupElement(rand.nextBoolean());
		YGroupElement g2 = new YGroupElement(g1);
		assertEquals("Error: g1.equals(clone(g1)) return false",g1,g2);
		assertTrue("Error: g1.equals(null) return true",!g1.equals(null));
		assertEquals("Error: equals function is not symmetric",g1,g1);
		assertEquals("Error: equals function doesn't allow cast",g1, (Object)g2);
	}
	
	@Test
	public void testMultiply(){
		assertEquals("Error: 1*1 != 1",
				YGroupElement.one(), 
				YGroupElement.one().multiply(YGroupElement.one()));
		assertEquals("Error: -1*-1 != 1",
				YGroupElement.one(),
				YGroupElement.minusOne().multiply(YGroupElement.minusOne()));
		assertEquals("Error: -1*1 != -1",
				YGroupElement.minusOne(),
				YGroupElement.minusOne().multiply(YGroupElement.one()));
		assertEquals("Error: 1*-1 != -1",
				YGroupElement.minusOne(),
				YGroupElement.one().multiply(YGroupElement.minusOne()));
	}
	
	@Test
	public void testPow(){
		assertEquals("Error: 1^p != 1",
				YGroupElement.one(),
				YGroupElement.one().pow(rand.nextInt()));
		assertEquals("Error: -1^2p != 1",
				YGroupElement.one(),
				YGroupElement.minusOne().pow(2*rand.nextInt()));
		assertEquals("Error: -1^(2p+1) != -1",
				YGroupElement.minusOne(),
				YGroupElement.minusOne().pow(2*rand.nextInt()+1));
	}

}
