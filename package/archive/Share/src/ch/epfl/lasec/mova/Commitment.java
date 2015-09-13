package ch.epfl.lasec.mova;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

/**
 * 
 * Class used to implement a cryptographic commitment scheme
 * The implementation uses a cryptographic hash function (SHA-256)
 * (com,dec) = (SHA2(m|r),m|r) <- commit(m)
 * Where r is 32 randomly selected bytes (256 bits)
 * 
 * @author Sebastien Duc
 *
 */
class Commitment {
	
	/**
	 * Hash function
	 */
	private MessageDigest hash;
	
	/**
	 * algorithm to use for the hash function
	 */
	private final static String ALGORITHM = "SHA-256";
	
	/**
	 * Random number generator used to generate r
	 */
	private Random rand;
	
	/**
	 * Generated number r that is appended to the value to commit
	 */
	private byte [] r;
	public final static int RANDOM_BYTE_SIZE = 32;
	public final static int DIGEST_BYTE_SIZE = 32;
	
	/**
	 * Creates a commitment.
	 */
	public Commitment(){
		try {
			this.hash = MessageDigest.getInstance(Commitment.ALGORITHM);
			this.rand = new Random();
			this.r = null;
		} catch (NoSuchAlgorithmException e) {
			//shoud not happen
			assert false;
		}
	}
	
	/**
	 * Commit to message m
	 * 
	 * @param m Message to commit
	 * @return Return com (without r). r can be accessed by its associated getter.
	 */
	public byte[] commit(byte [] m){
		this.r = new byte [Commitment.RANDOM_BYTE_SIZE];
		rand.nextBytes(r);
		hash.update(m);
		hash.update(r);
		return hash.digest();
	}
	
	/**
	 * Get the randomness generated for the commitment.
	 * Used just after using obj.commit(m):<br/>
	 * <dd> byte[] decR = obj.getR()
	 * @return
	 */
	public byte [] getR(){
		return this.r;
	}
	
	/**
	 * Open the commitment with dec
	 * 
	 * @param com Committed value previously received
	 * @param m Part of the key to open the commitment
	 * @param r Other part of the key to open the commitment
	 * @return Returns true if it could open the commitment, false otherwise
	 */
	public boolean open(byte [] com, byte [] m, byte [] r){
		hash.update(m);
		hash.update(r);
		return Arrays.equals(hash.digest(), com);
	}
	
}
