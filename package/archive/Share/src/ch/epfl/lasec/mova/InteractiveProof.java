package ch.epfl.lasec.mova;

abstract public interface InteractiveProof {
	
	/**
	 * 
	 * This method runs the verifier side of the interactive protocol
	 * 
	 * @param pk Public key
	 * @param m Message that was signed
	 * @param s Signature of the message
	 * @return Returns true if the protocol ran correctly, false otherwise.
	 */
	abstract boolean verifier(MovaPublicKey pk, Message m, MovaSignature s);
	
	/**
	 * 
	 * This method runs the prover side of the interactive protocol
	 * 
	 * @param pk Public key
	 * @param sk Secret key
	 * @param m message that was signed
	 * @param s signature of the message
	 * @return Returns true the protocol runs correctly, false otherwise.
	 */
	abstract boolean prover(MovaPublicKey pk, MovaSecretKey sk, Message m, MovaSignature s);
	
	public static final int SENDING_DATA = 0;
	
	public static final int ABORTING = 1;
}
