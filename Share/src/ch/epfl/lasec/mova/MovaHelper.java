package ch.epfl.lasec.mova;

import java.math.BigInteger;

/**
 * Class used to regroup function that are implemented in C for optimization concern.
 * 
 * @author Sebastien Duc
 *
 */
public class MovaHelper {
	
	/**
	 * Compute the Legendre symbol of x mod p 
	 * (x/p).
	 * 
	 * @param x
	 * @param p
	 * @return returns an integer which is either 1 if x is a quadratic residue, -1 if not.
	 */
	//public static native int legendre(BigInteger x, BigInteger p);
	
	public static int legendre(BigInteger x, BigInteger p){
		BigInteger pMinusOne = p.subtract(BigInteger.ONE);
		// compute x^{(p-1)/2} mod p
		BigInteger res = x.modPow(pMinusOne.divide(BigInteger.valueOf(2)), p);
		if(res.equals(BigInteger.ONE)){
			return 1;
		}
		else if(res.equals(pMinusOne)){
			return -1;
		}
		else{
			return 0;
		}
	}

}
