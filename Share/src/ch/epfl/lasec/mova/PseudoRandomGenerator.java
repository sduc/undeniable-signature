package ch.epfl.lasec.mova;

import gnu.crypto.prng.IRandom;
import gnu.crypto.prng.MDGenerator;
import gnu.crypto.prng.PRNGFactory;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;


/**
 * 
 * Implementation of a pseudo random generator using SecureRandom from java.security package.
 * The used algorithm is SHA1PRNG
 * 
 * @author Sebastien Duc
 *
 */
class PseudoRandomGenerator {
	
	/**
	 * Pseudo random generator from java.security used
	 * to implement this primitives.
	 */
	private SecureRandom secRand;
	
	private IRandom rand;
	
	/**
	 * Algorithm used for the pseudo random number generation
	 */
	private final static String ALGORITHM = "SHA1PRNG";
	
	
	/**
	 * Creates a pseudo random generator.
	 */
	public PseudoRandomGenerator(){
		try {
			secRand = SecureRandom.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			// should not happen
			assert false;
		}
	}
	
	/**
	 * Set the seed of the pseudo random generator.
	 * 
	 * @param seed Seed to use for the pseudo random generator
	 */
	public void setSeed(byte [] seed){
		try {
			secRand = SecureRandom.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			// do nothing
		}
		secRand.setSeed(seed);
		
		
		HashMap<String, Object> attrib = new HashMap<String, Object>();
		this.rand = PRNGFactory.getInstance("MD");
		attrib.put(MDGenerator.MD_NAME, "SHA1");
		attrib.put(MDGenerator.SEEED, seed);
		this.rand.init(attrib);
		
	}
	
	/**
	 * Method used to generate a sequence of elements in Xgroup from the pseudo random generator
	 * 
	 * @param seed Seed used to generate the sequence
	 * @param sequenceSize Size of the sequence
	 * @param n BigInteger defining Xgroup = (Z/nZ)* in this implementation
	 * @return Returns the sequence
	 */
	public BigInteger [] generateRandomSequence(byte [] seed, int sequenceSize, 
			BigInteger n){
		this.setSeed(seed);
		BigInteger [] sequence = new BigInteger [sequenceSize];
		for (int i = 0; i < sequenceSize; i++) {
			sequence[i] = Primitives.generateRandomXGroupElement(rand, n);
		}
		return sequence;
	}
	
	
}

