package ch.epfl.lasec.mova;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.Random;

/**
 * Class used to generate the public key and the secret key.
 * <p>In this implementation, the secret key is the Legendre symbol Hom=(./p) where p is prime.</p>
 * <p><dd>	Hom:(Z/nZ)* -> {-1,1}</p>
 * <p>and n=p.q</p>
 * 
 * @author Sebastien Duc
 *
 */
public class KeyPairGenerator {
	
	/**
	 * Lkey is the length of the key. It's a public parameter included in the public key.
	 */
	private int Lkey;
	
	/**
	 * nBitSize is the bit size of the modulus n. n is the product of two primes p,q. 
	 */
	private int nBitSize;
	
	/**
	 * the byte size of seedK. SeedK is used with pseudo random generator GenK to 
	 * generate GenK(seedK)->(X_1,...,X_Lkey)
	 */
	private int seedSize;
	

	private Random random;
	
	
	/**
	 * Constructor for KeyPairGenerator
	 * 
	 * @param keyLengthParameter length of the key in bits
	 * @param modulusSize length of the modulus in bits
	 * @param seedSize length of the seed in bits
	 */
	public KeyPairGenerator(int keyLengthParameter, int modulusSize, int seedSize) {
		this.Lkey = keyLengthParameter;
		this.nBitSize = modulusSize;
		this.seedSize = seedSize;
		random = new Random();
	}
	
	/**
	 * Generate a MOVA key pair
	 * 
	 * @return Returns the pair (public key,secret key)
	 */
	public KeyPair generateKeyPair(){
		// Generate the RSA modulus n
		RSAPrivateCrtKeySpec rpks = null;
		try {
			java.security.KeyPairGenerator kpg = java.security.
					KeyPairGenerator.getInstance("RSA");
			kpg.initialize(this.nBitSize);
			java.security.KeyPair kp = kpg.genKeyPair();
			KeyFactory kf = KeyFactory.getInstance("RSA");
			rpks = kf.getKeySpec(kp.getPrivate(), 
					RSAPrivateCrtKeySpec.class);
		} catch (NoSuchAlgorithmException e) {
			assert false;
		} catch (InvalidKeySpecException e) {
			assert false;
		}
		
		BigInteger p = rpks.getPrimeP();
		BigInteger n = rpks.getModulus();
		// create the secret key from p
		MovaSecretKey sk = new MovaSecretKey(p);
		//generate the seedK for the public key
		byte [] seedK = new byte [this.seedSize];
		random.nextBytes(seedK);
		// generate Xkeys from SeedK and Ykeys from Xkeys and the secret homomorphism
		BigInteger [] Xkeys = new PseudoRandomGenerator().generateRandomSequence(seedK,Lkey,n); 
		YGroupElement [] Ykeys = sk.Hom(Xkeys);
		// generate the public key
		MovaPublicKey pk = new MovaPublicKey(n,seedK,Ykeys);
		
		return new KeyPair(pk, sk);
	}

}
