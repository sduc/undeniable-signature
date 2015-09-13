package ch.epfl.lasec.mova;

import java.math.BigInteger;
import java.util.Arrays;

import ch.epfl.lasec.ArrayUtils;

/**
 * 
 * Class representing the public key in MOVA. The public key consits of 		<br/>
 * <dd>(Xgroup,Ygroup, d, seedK, (Ykey_1,...,Ykey_Lkey))						<br/>
 * In this implementation, Xgroup is (Z/nZ)*, Ygroup is {-1,+1) and so d = 2.	<br/>
 * 
 * @author Sebastien Duc
 *
 */
public class MovaPublicKey {
	
	/**
	 * It is used to represent Xgroup = (Z/nZ)*
	 */
	private BigInteger n;
	
	/**
	 * seedK is used to generate (Xkey_1,...,Xkey_Lkey) from a psedorandom generator 
	 */
	private byte[] seedK;
	
	/**
	 * Ykeys is an array of YGroupElements used to represent (YKey_1,...,Ykey_Lkey)
	 */
	private YGroupElement [] Ykeys;
	
	/**
	 * Create a secret key.
	 * 
	 * @param n Modulus, defining Xgroup
	 * @param seedK Seed used to recover Xkey_1 ,..., Xkey_Lkey
	 * @param Ykeys Ykey_1 ,...,Ykey_Lkey
	 */
	public MovaPublicKey(BigInteger n, byte [] seedK, YGroupElement [] Ykeys) {
		this.n = new BigInteger(n.toByteArray());
		
		this.seedK = Arrays.copyOf(seedK, seedK.length);
		
		this.Ykeys = new YGroupElement [Ykeys.length];
		for (int i = 0; i < Ykeys.length; i++) {
			this.Ykeys[i] = new YGroupElement(Ykeys[i]);
		}
	}
	
	/**
	 * Constructor of copy
	 * @param pk Object we want to copy
	 */
	public MovaPublicKey(MovaPublicKey pk) {
		this.n = new BigInteger(pk.n.toByteArray());
		this.seedK = Arrays.copyOf(pk.seedK, pk.seedK.length);
		this.Ykeys = new YGroupElement [pk.Ykeys.length];
		for (int i = 0; i < Ykeys.length; i++) {
			Ykeys[i] = new YGroupElement(pk.Ykeys[i]);
		}
	}
	
	/**
	 * Get the encoded form of the public key.																<br/>
	 * The key is encoded in the following way (note: [x] is the encoded form of x)							<br/>
	 *<dd> .................................................................................................<br/>
	 *<dd> |[seedK].length | [seedK] | [[n].length].length | [[n].length] | [n] | [Ykey_1] ... [Ykey_Lkey] |<br/>
	 *<dd> '''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''<br/>
	 * @return Returns the encoded form of the public key.
	 */
	public byte[] getEncoded(){
		byte [] nEncoded = n.toByteArray();
		byte [] yKeysEncoded = YGroupElement.encodeYGroupElementSequence(Ykeys);
		byte [] nLengthEncoded = BigInteger.valueOf(nEncoded.length).toByteArray();
		return ArrayUtils.concatAll(new byte[]{(byte) seedK.length},seedK,
				new byte[]{(byte) nLengthEncoded.length}, nLengthEncoded, nEncoded,
				yKeysEncoded);
	}
	
	/**
	 * Create a MOVA public key from an encoded version
	 * 
	 * @param encodedKey The encoded version of the key
	 * @return the Mova public key
	 */
	public static MovaPublicKey getKeyFromEncoding(byte [] encodedKey){
		int seedLength = encodedKey[0];
		int endSeedIndex = seedLength;
		byte [] seedK = Arrays.copyOfRange(encodedKey, 
												1, 
												endSeedIndex+1);
		int nLengthLength = encodedKey[endSeedIndex+1];
		int endNLengthIndex = endSeedIndex+2 + nLengthLength;
		int nLength = new BigInteger(Arrays.copyOfRange(encodedKey,
						endSeedIndex+2, 
						endNLengthIndex)).intValue();
		int endnEncodedIndex = endNLengthIndex + nLength;
		byte [] nEncoded = Arrays.copyOfRange(encodedKey, 
						endNLengthIndex, 
						endnEncodedIndex);
		byte [] yKeysEncoded = Arrays.copyOfRange(encodedKey,
												endnEncodedIndex, 
												encodedKey.length);
		return new MovaPublicKey(new BigInteger(nEncoded), seedK,
				YGroupElement.decodeEncodedYGroupElementSequence(yKeysEncoded));
	}
	
	/**
	 * Getter for SeedK
	 * @return the SeedK for the key
	 */
	public byte [] getSeedK(){
		return this.seedK;
	}
	
	/**
	 * Getter for Ykeys
	 * @return the Ykeys for the public key
	 */
	public YGroupElement[] getYkeys(){
		return this.Ykeys;
	}
	
	/**
	 * Getter for n
	 * @return the modulus n
	 */
	public BigInteger getN(){
		return this.n;
	}
	
	/**
	 * Method used to recover (Xkey_1,...,Xkey_Lkey) from seedK using GenK
	 * from movaInstance.
	 * @param movaInstance Mova instance used to get GenK 
	 * @return Returns (Xkey_1,...,Xkey_Lkey) as an array of BigIntegers
	 */
	public BigInteger [] recoverXkeysFromSeed(Mova movaInstance){
		BigInteger [] Xkeys = movaInstance.primitives.getGenK().
				generateRandomSequence(this.seedK, this.Ykeys.length, this.n);
		return Xkeys;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}
		if(obj == this){
			return true;
		}
		if(obj.getClass() != this.getClass()){
			return false;
		}
		MovaPublicKey pk = (MovaPublicKey) obj;
		return this.n.equals(pk.getN()) && 
				Arrays.equals(this.seedK,pk.getSeedK()) &&
				Arrays.deepEquals(this.Ykeys, pk.getYkeys());
	}

	@Override
	public String toString() {
		return "MovaPublicKey [n=" + n + ", seedK=" + Arrays.toString(seedK)
				+ ", Ykeys=" + Arrays.toString(Ykeys) + "]";
	}
	
	

}
