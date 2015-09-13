package ch.epfl.lasec.mova;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

public class PseudoRandomGeneratorTest {

	@Test
	public void testGenerateRandomSequence() {
		// some parameters
		int sequSize = 20;
		int seedSize = 512/8;
		
		PseudoRandomGenerator Gen = new PseudoRandomGenerator();
		Random rand = new Random();
		byte [] seed = new byte [seedSize];
		rand.nextBytes(seed);
		
		BigInteger [] s1 = Gen.generateRandomSequence
				(seed, sequSize, BigInteger.valueOf(10001));
		BigInteger [] s2 = Gen.generateRandomSequence
				(seed, sequSize, BigInteger.valueOf(10001));
		
		System.out.println(Arrays.toString(s1));
		System.out.println(Arrays.toString(s2));
		assertArrayEquals("Error: generating two sequences with the same seed should output the " +
				"same sequence",s1, s2);
	}

}
