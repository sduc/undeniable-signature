package ch.epfl.lasec.mova;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * Class used to represent a Mova signature.
 * A mova signature is described by 				<br/>
 * <dd> (Ysig<sub>1</sub>,...,Ysig<sub>Lsig</sub>) 	<br/>
 * which was computed from the message that was signed by <br/>
 * <dd> GenS(m) = (Xsig<sub>1</sub>,...,Xsig<sub>Lsig</sub> <br/>
 * and then for all i = 1 ,..., Lsig:<br/>
 * <dd> Ysig<sub>i</sub> = Hom(Xsig<sub>i</sub>).
 * 
 * 
 * @author Sebastien Duc
 *
 */
public class MovaSignature {
	
	/**
	 * List of YGroupElement representing<br/>
	 * <dd> (Ysig<sub>1</sub>,...,Ysig<sub>Lsig</sub>)
	 */
	private YGroupElement [] Ysigs;
	
	/**
	 * Creates a MOVA signature from a MOVA instance to initialize the size of the signature
	 * @param movaInstance
	 */
	public MovaSignature(Mova movaInstance){
		Ysigs = new YGroupElement [movaInstance.domainParameters.Lsig];
	}
	
	/**Creates a Mova signature from an encoded version
	 * @param enc Encoded signature
	 */
	public MovaSignature(byte [] enc){
		this.Ysigs = YGroupElement.decodeEncodedYGroupElementSequence(enc);
	}
	
	/**
	 * Creates a MOVA signature from<br/>
	 * <dd> (Ysig<sub>1</sub>,...,Ysig<sub>Lsig</sub>)
	 * @param Ysigs
	 */
	public MovaSignature(YGroupElement [] Ysigs){
		this.Ysigs = new YGroupElement [Ysigs.length];
		for (int i = 0; i < Ysigs.length; i++) {
			this.Ysigs[i] = Ysigs[i];
		}
	}
	
	/**
	 * Creates a copy of s.
	 * @param s the object to copy
	 */
	public MovaSignature(MovaSignature s) {
		this.Ysigs  = new YGroupElement[s.Ysigs.length];
		for (int i = 0; i < Ysigs.length; i++) {
			this.Ysigs[i] = new YGroupElement(s.Ysigs[i]);
		}
	}
	
	/**
	 * Method used to recover (Xsig_1,...,Xsig_Lsig) from the message and the movaInstance.
	 * (Xsig_1,...,Xsig_Lsig) = GenS(m), where GenS is a pseudo random generator given in movaInstance.
	 * 
	 * @param m Message used to recover Xsigs
	 * @param movaInstance MOVA instance used to access to GenS.
	 * @return
	 */
	public static BigInteger[] recoverXsigsFromMessage(Message m, Mova movaInstance){
		BigInteger [] Xsigs = movaInstance.primitives.getGenS().
				generateRandomSequence(m.getEncoded(), 
						movaInstance.domainParameters.Lsig, 
						movaInstance.domainParameters.nXgroup);
		return Xsigs;
	}

	/**
	 * Get the list of Ysigs
	 * @return the list of Ysigs
	 */
	public YGroupElement[] getYsigs() {
		return Ysigs;
	}
	
	/**
	 * Get the byte encoded version of the signature.
	 * @return the byte encoded verison of the signature
	 */
	public byte [] getEncoded(){
		return YGroupElement.encodeYGroupElementSequence(this.Ysigs);
	}
	
	/**
	 * Creates and returns a mova signature from the encoded version of it.
	 * @param encoded Encoded version of the signature
	 * @return the mova signature which gets encoded to encoded
	 */
	public static MovaSignature getSignatureFromEncoding(byte [] encoded){
		return new MovaSignature(encoded);
	}

	@Override
	public String toString() {
		return "MovaSignature [Ysigs=" + Arrays.toString(Ysigs) + "]";
	}
	
	
}
