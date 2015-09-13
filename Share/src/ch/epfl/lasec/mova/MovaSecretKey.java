package ch.epfl.lasec.mova;

import java.math.BigInteger;

/**
 * 
 * In MOVA the secret key is a secret group homomorphism
 * This class represent a secret key in the MOVA scheme.
 * In this implementation the homomorphism is from (Z/nZ)* -> {-1,+1} 
 * with n = pq (p,q are prime) and it is the Legendre symbol (./p)
 * 
 * @author Sebastien Duc
 *
 */
public class MovaSecretKey {
	
	/**
	 * p is a prime number. It fully defines the homomorphism
	 * which is the Legendre symbol (./p).
	 */
	private BigInteger p;
	
	/**
	 * Constructor for MovaSecretKey
	 * 
	 * @param p Prime number p, where Xgroup = (Z/nZ)* and n=p.q
	 */
	public MovaSecretKey(BigInteger p){
		this.p = new BigInteger(p.toByteArray());
	}
	
	/**
	 * Create a MovaSecretKey from its encoded form
	 * @param encodedKey The encoded form of the secret key
	 */
	public MovaSecretKey(byte [] encodedKey){
		this.p = new BigInteger(encodedKey);
	}
	
	/**
	 * Constructor of copies
	 * @param sk Object we want to copy
	 */
	public MovaSecretKey(MovaSecretKey sk) {
		this.p = new BigInteger(sk.p.toByteArray());
	}

	/**
	 * Get the signature in its byte encoded form
	 * @return Returns an array of byte which encodes the signature
	 */
	public byte [] getEncoded(){
		return p.toByteArray();
	}
	
	
	/**
	 * 
	 * Compute the image of g by the secret homomorphism described by the secret key.
	 * In this implementation it is the Legendre symbol of g mod p.
	 * 
	 * @param g The element of which we want to compute Hom(g)
	 * @return Returns the image of g by Hom i.e. Hom(g) = (g / p)
	 */
	public YGroupElement Hom(BigInteger g){
		return new YGroupElement(MovaHelper.legendre(g, p));
	}
	
	/**
	 * Compute the image of each element in g by homomorphism<br/>
	 * <dd> Hom:G->H<br/>
	 * where Hom is the Legendre symbol.
	 * 
	 * @param g Set of elements in G
	 * @return {Hom(x): for all x in g}
	 */
	public YGroupElement [] Hom(BigInteger [] g){
		YGroupElement [] homG = new YGroupElement [g.length];
		for (int i = 0; i < homG.length; i++) {
			homG[i] = new YGroupElement(MovaHelper.legendre(g[i], p));
		}
		return homG;
	}
	
}
