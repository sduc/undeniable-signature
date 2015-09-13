package ch.epfl.lasec.mova;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

import ch.epfl.lasec.IOHelper;
import ch.epfl.lasec.LogContainer;

/**
 * 
 * Class used to implement the MOVA confirmation protocol.
 * The main methods are the prover side and the verifier side of the protocol.<br/>
 * For more information on the protocol refer to the GHIproof protocol in MOVA.
 * 
 * @author Sebastien Duc
 *
 */
public class ConfirmationProtocol implements InteractiveProof{
	
	/**
	 * InputStream used to receive data from the channel.
	 */
	private InputStream channelIn;
	
	/**
	 * OutputStream used to write on the channel.
	 */
	private OutputStream channelOut;
	
	/**
	 * Mova instance use to have access to domain parameters and cryptographic primitives. 
	 */
	private Mova movaInstance;
	
	/**
	 * 
	 * Creates a ConfirmationProtocol 
	 * 
	 * @param in
	 * @param out
	 * @param movaInstance
	 */
	public ConfirmationProtocol(InputStream in, OutputStream out, Mova movaInstance) {
		this.movaInstance = movaInstance;
		this.channelIn = in;
		this.channelOut = out;
	}
	
	/**
	 * 
	 * This method runs the verifier side of the confirmation protocol
	 * 
	 * @param pk Public key
	 * @param m Message that was signed
	 * @param s Signature of the message
	 * @return Returns true if (m,s) is valid and the protocol ran correctly, false otherwise.
	 */
	public boolean verifier(MovaPublicKey pk, Message m, MovaSignature s){
		return verifier(pk, m, s,false);
	}
	
	public boolean verifier(MovaPublicKey pk, Message m, MovaSignature s, boolean debugMode){
		//////////////////////////////////
		///// Initialization         /////
		//////////////////////////////////

		BigInteger [] Xkeys = pk.recoverXkeysFromSeed(this.movaInstance);
		BigInteger [] Xsigs = MovaSignature.recoverXsigsFromMessage(m, this.movaInstance);
		YGroupElement [] Ykeys = pk.getYkeys();
		YGroupElement [] Ysigs = s.getYsigs();
		
		//------------------------------
		if (debugMode){
			System.out.println("INIT");
			System.out.println("INIT: Xkeys = "+Arrays.toString(Xkeys));
			System.out.println("INIT: Xsigs = "+Arrays.toString(Xsigs));
		}
		//------------------------------
		
		//////////////////////////////////
		///// step 1 of the protocol /////
		//////////////////////////////////
		//------------------------------
		if (debugMode){
			System.out.println("STEP -1-");
			System.out.println("step 1: the verifier generates randomly"+
			" a_ij and r_i, i = 1,...,Icon and j = 1,...,Lkey+Lsig");
		}
		//------------------------------
		// the verifier generates randomly a_ij and r_i, i = 1,...,Icon and j = 1,...,Lkey+Lsig
		BigInteger [] r = new BigInteger[this.movaInstance.domainParameters.Icon];
		for (int i = 0; i < r.length; i++) {
			r[i] = movaInstance.generateRandom_r();
		}
		int [][] a = generateRandomA();
		//------------------------------
		if (debugMode)
			System.out.println("step 1: compute u_i and w_i, i = 1,...,Icon");
		//------------------------------
		// then compute u_i and w_i, i = 1,...,Icon
		BigInteger [] u = new BigInteger[this.movaInstance.domainParameters.Icon];
		YGroupElement [] w = new YGroupElement[this.movaInstance.domainParameters.Icon];
		for (int i = 0; i < u.length; i++) {
			u[i] = this.compute_u_i(Xkeys, Xsigs, r[i], a[i]);
			w[i] = this.compute_w_i(Ykeys, Ysigs, a[i]);
		}
		//------------------------------
		if (debugMode)
			System.out.println("step 1: send u = " + Arrays.toString(u));
		//------------------------------
		try {
			sendU(u);
		} catch (IOException e) {
			return false;
		}
		
		////////////////////////////////
		// Step 3 of the protocol //////
		////////////////////////////////
		//------------------------------
		if (debugMode){
			System.out.println("STEP -3-");
			System.out.println("step 3: receive com from prover");
		}
		//------------------------------
		// receive com from prover
		byte [] com = null;
		try {
			com = this.receiveCom();
			// send r and a to the prover
			//------------------------------
			if (debugMode){
				System.out.println("step 3: com = " + Arrays.toString(com));
				System.out.println("step 3: send r and a to prover");
				System.out.println("step 3: r = " + Arrays.toString(r));
				System.out.println("step 3: a = " + Arrays.deepToString(a));
			}
			//------------------------------
			sendR(r);
			sendA(a);
		} catch (IOException e) {
			return false;
		}
		
		////////////////////////////////
		// Step 5 of the protocol //////
		////////////////////////////////
		//------------------------------
		if (debugMode){
			System.out.println("STEP -5-");
			System.out.println("step 5: receive v_i and decR to open commitment");
		}
		//------------------------------
		// receive v_i and decR to open commitment
		YGroupElement [] v = null;
		byte [] decR = null;
		try {
			v = this.receiveV();
			decR = this.receiveDecR();
		} catch (IOException e) {
			return false;
		}
		//------------------------------
		if (debugMode){
			System.out.println("step 5: v = " + Arrays.toString(v));
			System.out.println("step 5: decR = " + Arrays.toString(decR));
			System.out.println("step 5: check that v[i] = w[i] for all i");
		}
		//------------------------------
		// check that v[i] = w[i] for all i
		if (!Arrays.deepEquals(v, w)){
			//---------------------
			if (debugMode){
				System.out.println("step 5: arrays v and w are not equal");
				System.out.println("step 5: array v = ");
				for (YGroupElement y : v) {
					System.out.println(y);
				}
				System.out.println("step 5: array w = ");
				for (YGroupElement y : w) {
					System.out.println(y);
				}
			}
			//---------------------
			return false;
		}
		//------------------------------
		if (debugMode)
			System.out.println("step 5: open commitment");
		//------------------------------
		// open commitment
		return this.movaInstance.primitives.getCommitment().
				open(com, YGroupElement.encodeYGroupElementSequence(v), decR);
	}
	
	public boolean verifier(MovaPublicKey pk, Message m, MovaSignature s, LogContainer log){
		//////////////////////////////////
		///// Initialization         /////
		//////////////////////////////////

		BigInteger [] Xkeys = pk.recoverXkeysFromSeed(this.movaInstance);
		BigInteger [] Xsigs = MovaSignature.recoverXsigsFromMessage(m, this.movaInstance);
		YGroupElement [] Ykeys = pk.getYkeys();
		YGroupElement [] Ysigs = s.getYsigs();
		
		//------------------------------
		log.addLogMessage("INIT");
		log.addLogMessage("INIT: Xkeys = "+Arrays.toString(Xkeys));
		log.addLogMessage("INIT: Xsigs = "+Arrays.toString(Xsigs));
		//------------------------------
		
		//////////////////////////////////
		///// step 1 of the protocol /////
		//////////////////////////////////
		//------------------------------
		log.addLogMessage("STEP -1-");
		log.addLogMessage("step 1: the verifier generates randomly"+
			" a_ij and r_i, i = 1,...,Icon and j = 1,...,Lkey+Lsig");
		
		//------------------------------
		// the verifier generates randomly a_ij and r_i, i = 1,...,Icon and j = 1,...,Lkey+Lsig
		BigInteger [] r = new BigInteger[this.movaInstance.domainParameters.Icon];
		for (int i = 0; i < r.length; i++) {
			r[i] = movaInstance.generateRandom_r();
		}
		int [][] a = generateRandomA();
		//------------------------------
		log.addLogMessage("step 1: compute u_i and w_i, i = 1,...,Icon");
		//------------------------------
		// then compute u_i and w_i, i = 1,...,Icon
		BigInteger [] u = new BigInteger[this.movaInstance.domainParameters.Icon];
		YGroupElement [] w = new YGroupElement[this.movaInstance.domainParameters.Icon];
		for (int i = 0; i < u.length; i++) {
			u[i] = this.compute_u_i(Xkeys, Xsigs, r[i], a[i]);
			w[i] = this.compute_w_i(Ykeys, Ysigs, a[i]);
		}
		//------------------------------
		log.addLogMessage("step 1: send u = " + Arrays.toString(u));
		//------------------------------
		try {
			sendU(u);
		} catch (IOException e) {
			return false;
		}
		
		////////////////////////////////
		// Step 3 of the protocol //////
		////////////////////////////////
		//------------------------------
		log.addLogMessage("STEP -3-");
		log.addLogMessage("step 3: receive com from prover");
		//------------------------------
		// receive com from prover
		byte [] com = null;
		try {
			com = this.receiveCom();
			// send r and a to the prover
			//------------------------------
			log.addLogMessage("step 3: com = " + Arrays.toString(com));
			log.addLogMessage("step 3: send r and a to prover");
			log.addLogMessage("step 3: r = " + Arrays.toString(r));
			log.addLogMessage("step 3: a = " + Arrays.deepToString(a));
			//------------------------------
			sendR(r);
			sendA(a);
		} catch (IOException e) {
			return false;
		}
		
		////////////////////////////////
		// Step 5 of the protocol //////
		////////////////////////////////
		//------------------------------
		log.addLogMessage("STEP -5-");
		log.addLogMessage("step 5: receive v_i and decR to open commitment");
		//------------------------------
		// receive v_i and decR to open commitment
		YGroupElement [] v = null;
		byte [] decR = null;
		try {
			v = this.receiveV();
			decR = this.receiveDecR();
		} catch (IOException e) {
			return false;
		}
		//------------------------------
		log.addLogMessage("step 5: v = " + Arrays.toString(v));
		log.addLogMessage("step 5: decR = " + Arrays.toString(decR));
		log.addLogMessage("step 5: check that v[i] = w[i] for all i");
		//------------------------------
		// check that v[i] = w[i] for all i
		if (!Arrays.deepEquals(v, w)){
			//---------------------
			log.addLogMessage("step 5: arrays v and w are not equal");
			log.addLogMessage("step 5: array v = ");
			for (YGroupElement y : v) {
				log.addLogMessage(y.toString());
			}
			log.addLogMessage("step 5: array w = ");
			for (YGroupElement y : w) {
				log.addLogMessage(y.toString());
			}
			//---------------------
			return false;
		}
		//------------------------------
		log.addLogMessage("step 5: open commitment");
		//------------------------------
		// open commitment
		return this.movaInstance.primitives.getCommitment().
				open(com, YGroupElement.encodeYGroupElementSequence(v), decR);
	}

	/**
	 * 
	 * This method runs the prover side of the confirmation protocol
	 * 
	 * @param pk Public key
	 * @param sk Secret key
	 * @param m message that was signed
	 * @param s signature of the message
	 * @return Returns true if (m,s) is value and the protocol runs correctly, false otherwise.
	 */
	public boolean prover(MovaPublicKey pk, MovaSecretKey sk, Message m, MovaSignature s){
		return prover(pk, sk, m, s, false);
	}
	
	public boolean prover(MovaPublicKey pk, MovaSecretKey sk, Message m, MovaSignature s, 
			boolean debugMode){
		////////////////////////////
		// Initialization         //
		////////////////////////////
		
		BigInteger [] Xkeys = pk.recoverXkeysFromSeed(this.movaInstance);
		BigInteger [] Xsigs = MovaSignature.recoverXsigsFromMessage(m, this.movaInstance);
		YGroupElement [] Ysigs = s.getYsigs();
		
		// ------------------------------
		if (debugMode){
			System.out.println("INIT");
			System.out.println("INIT: Xkeys = "+Arrays.toString(Xkeys));
			System.out.println("INIT: Xsigs = "+Arrays.toString(Xsigs));
		}
		// ------------------------------
		
		////////////////////////////
		// Step 2 of the protocol //
		////////////////////////////
		//------------------------------
		if (debugMode)
			System.out.println("STEP -2-");
		// the prover receives the values u_i for i=1,..,Icon
		if (debugMode)
			System.out.println("step 2: the prover receives the values u_i for i=1,..,Icon");
		//------------------------------
		BigInteger[] u;
		try {
			u = this.recieveU();
		} catch (IOException e1) {
			//if channel fail then confirmation protocol fails
			return false;
		}
		// first check that the signature is indeed valid
		if (!signatureIsValid(Xsigs,Ysigs,sk)){
			if (debugMode)
				System.out.println("step 2: Signature is invalid");
			try {
				channelOut.write(ABORTING);
			} catch (IOException e) {}
			
			return false;
		}
		
		// compute v[i] = Hom(u[i])
		//------------------------------
		if (debugMode){
			System.out.println("step 2: u = " + Arrays.toString(u));
			System.out.println("step 2: compute v[i] = Hom(u[i])");
		}
		//------------------------------
		YGroupElement [] v = new YGroupElement [u.length];
		for (int i = 0; i < v.length; i++) {
			v[i] = sk.Hom(u[i]);
		}
		// commit on v
		//------------------------------
		if (debugMode)
			System.out.println("step 2: commit on v");
		//------------------------------
		byte [] com = this.movaInstance.primitives.getCommitment().
				commit(YGroupElement.encodeYGroupElementSequence(v));
		// used after to open the commitment
		byte [] decR = this.movaInstance.primitives.getCommitment().getR();
		// send com to the verifier
		//------------------------------
		if (debugMode){
			System.out.println("step 2: send com to the verifier");
			System.out.println("step 2: com = " + Arrays.toString(com));
		}
		//------------------------------
		try {
			channelOut.write(SENDING_DATA);
			channelOut.write(com);
		} catch (IOException e) {
			return false;
		}
		
		////////////////////////////
		// Step 4 of the protocol //
		////////////////////////////
		//------------------------------
		if (debugMode){
			System.out.println("STEP -4-");
			System.out.println("step 4: receive r_i and a_ij");
		}
		//------------------------------
		// receive r_i and a_ij
		BigInteger [] r = null;
		int [][] a = null;
		try {
			r = this.receiveR();
			a = this.receiveA();
		} catch (IOException e) {
			return false;
		}
		//------------------------------
		if (debugMode){
			System.out.println("step 4 : r = "+ Arrays.toString(r));
			System.out.println("step 4 : a = "+ Arrays.deepToString(a));
			System.out.println("step 4 : check that u_i where correctly computed.");
		}
		//------------------------------
		// check that u_i where correctly computed.
		for (int i = 0; i < u.length; i++) {
			if (!u[i].equals(this.compute_u_i(Xkeys, Xsigs, r[i], a[i]))){
				//-------------------------------
				if (debugMode){
					System.out.println("step 4: u_"+i+" was not properly computed.");
					System.out.println("step 4: u[i] = "+u[i]);
					System.out.println("step 4: compute_u_i = "+
							this.compute_u_i(Xkeys, Xsigs, r[i], a[i]));
					System.out.flush();
				}
				//-------------------------------
				try {
					channelOut.write(ABORTING);
				} catch (IOException e) {
					assert false;
				}
				return false;
			}
		}
		//------------------------------
		if (debugMode){
			System.out.println("step 4: open the commitment by sending dec to the verifier");
			System.out.println("step 4: decR = "+ Arrays.toString(decR));
		}
		//------------------------------
		// open the commitment by sending dec to the verifier
		try {
			sendDec(v, decR);
		} catch (IOException e) {
			return false;
		}
		
		//------------------------------
		if (debugMode)
			System.out.println("Prover side finished without errors");
		//------------------------------
		return true;
	}
	
	/**
	 * Method generating the set of a_ij in Z/dZ used in the proof of validity of MOVA.
	 * d = 2 in this implementation.
	 * @return Return a set of randomly generated a_ij in Z/dZ where d is the order of Ygroup
	 */
	private int [][] generateRandomA(){
		int [][] randomA = new 
				int [this.movaInstance.domainParameters.Icon]
						[this.movaInstance.domainParameters.Lkey+
						 this.movaInstance.domainParameters.Lsig];
		Random uniformGenerator = new Random();
		for (int i = 0; i < randomA.length; i++) {
			for (int j = 0; j < randomA[0].length; j++) {
				randomA[i][j] = (uniformGenerator.nextBoolean())?1:0;
			}
		}
		return randomA;
	}
	
	/**
	 * 
	 * Method used to compute u_i as											<br/>
	 * u_i = r_i^d * (Xkey_1)^a_{i,1} * ... * (Xkey_Lkey)^a_{i,Lkey} * 			<br/>
	 * <dd>   *(Xsigs_1)^a_{i,Lkey+1} * ... * (Xsigs_Lsigs)^a_{i,Lkey+Lsigs}	<br/>
	 * 
	 * @param Xkeys
	 * @param Xsigs
	 * @param r_
	 * @param a_j
	 * @return Returns u_i
	 */
	private BigInteger compute_u_i(BigInteger [] Xkeys, BigInteger [] Xsigs,
								   BigInteger r_, int [] a_j){
		int d = this.movaInstance.domainParameters.dYgroup;
		BigInteger n = this.movaInstance.domainParameters.nXgroup;
		
		BigInteger u_i = r_.modPow(BigInteger.valueOf(d), n);
		for (int j = 0; j < a_j.length; j++) {
			if(j < Xkeys.length)
				u_i = u_i.multiply(Xkeys[j].modPow(BigInteger.valueOf(a_j[j]), n));
			else
				u_i = u_i.multiply(Xsigs[j-Xkeys.length].modPow(BigInteger.valueOf(a_j[j]), n));
			u_i = u_i.mod(n);
		}
		
		return u_i;
	}
	
	/**
	 * Method used to compute w_i as										<br/>
	 * w_i = (YKey_1)^a_{i,1} * ... * (Ykey_Lkey)^a_{i,Lkey} * 				<br/>
	 * <dd>  *(Ysigs_1)^a_{i,Lkey+1} * ... * (Ysigs_Lsigs)^a_{i,Lkey+Lsigs}	<br/>
	 * 
	 * @param Ykeys
	 * @param Ysigs
	 * @param a_j
	 * @return Returns w_i
	 */
	private YGroupElement compute_w_i(YGroupElement[] Ykeys,
			YGroupElement[] Ysigs, int[] a_j) {
		YGroupElement w_i = new YGroupElement(YGroupElement.ONE);
		for (int j = 0; j < a_j.length; j++) {
			if(j < Ykeys.length)
				w_i = w_i.multiply(Ykeys[j].pow(a_j[j]));
			else
				w_i = w_i.multiply(Ysigs[j-Ykeys.length].pow(a_j[j]));
		}
		return w_i;
	}
	
	/**
	 * 
	 * Method used to handle the communication and receive  u from the verifier.
	 * It receives a stream of bytes and convert it in an array of BigInteger.
	 * 
	 * @return Return an array of BigInteger which contains u_i for i = 1,...,Icon
	 * @throws IOException 
	 */
	private BigInteger [] recieveU() throws IOException{
		if (channelIn.read() == ABORTING)
			throw new IOException();
		
		BigInteger [] u = new BigInteger [this.movaInstance.domainParameters.Icon];
		for (int i = 0; i < u.length; i++) {
			u[i] = IOHelper.readEncodedBigInt(this.channelIn);
		}
		// if u_i is of the wrong size then throw an exception TODO
		return u;
	}
	
	/**
	 * 
	 * Method used to send u over the channel to the prover.
	 * 
	 * @param u What needs to be sent.
	 * @throws IOException 
	 */
	private void sendU(BigInteger [] u) throws IOException{
		channelOut.write(SENDING_DATA);
		for (int i = 0; i < u.length; i++) {
			IOHelper.writeEncodedBigInt(this.channelOut, u[i]);
		}
		
	}
	
	/**
	 * Method used to send r over the channel to the prover.
	 * 
	 * @param r
	 * @throws IOException
	 */
	private void sendR(BigInteger [] r) throws IOException{
		channelOut.write(SENDING_DATA);
		for (int i = 0; i < r.length; i++) {
			IOHelper.writeEncodedBigInt(channelOut, r[i]);
		}
	}
	
	/**
	 * Method used to send a over the channel to the prover.
	 * 
	 * @param a
	 * @throws IOException
	 */
	private void sendA(int [][] a) throws IOException{
		channelOut.write(SENDING_DATA);
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				this.channelOut.write(a[i][j]);
			}
		}
	}
	
	/**
	 * Method used to receive r sent by the verifier from the channel
	 * 
	 * @return Returns r that was received
	 * @throws IOException
	 */
	private BigInteger [] receiveR() throws IOException{
		if (channelIn.read() == ABORTING)
			throw new IOException();
		
		BigInteger [] r = new BigInteger [this.movaInstance.domainParameters.Icon];
		for (int i = 0; i < r.length; i++) {
			r[i] = IOHelper.readEncodedBigInt(channelIn);
		}
		return r;
	}
	
	/**
	 * Method used to receive a sent by the verifier from the channel
	 * 
	 * @return Returns a that was received
	 * @throws IOException
	 */
	private int [][] receiveA() throws IOException{
		if (channelIn.read() == ABORTING)
			throw new IOException();
		
		int [][] a = new int [this.movaInstance.domainParameters.Icon]
				[this.movaInstance.domainParameters.Lkey + this.movaInstance.domainParameters.Lsig];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				a[i][j] = this.channelIn.read();
			}
		}
		return a;
	}
	
	/**
	 * Method used by the prover to open his commitment by sending dec = (v,decR) to
	 * the verifier.
	 * 
	 * @param v Values that where committed
	 * @param decR Random seed that was used to commit 
	 * @throws IOException
	 */
	private void sendDec(YGroupElement [] v, byte [] decR) throws IOException{
		channelOut.write(SENDING_DATA);
		//send v
		IOHelper.writeEncodedYGroupElements(channelOut, v);
		//send decR
		channelOut.write(SENDING_DATA);
		this.channelOut.write(decR);
	}
	
	/**
	 * Method used by the verifier to receive the committed values v
	 * 
	 * @return Returns v
	 * @throws IOException
	 */
	private YGroupElement [] receiveV() throws IOException{
		if (channelIn.read() == ABORTING)
			throw new IOException();
		return IOHelper.readEncodedYGroupElements(channelIn);
	}
	
	/**
	 * Method used by the verifier to receive the random seed decR for the commitment.
	 * 
	 * @return Returns decR
	 * @throws IOException
	 */
	private byte [] receiveDecR() throws IOException{
		if (channelIn.read() == ABORTING)
			throw new IOException();
		byte [] decR = new byte [Commitment.RANDOM_BYTE_SIZE];
		this.channelIn.read(decR);
		return decR;
	}
	
	/**
	 * Method used by the verifier to receive com from the channel, 
	 * where (com,dec) <- commit(u)
	 * 
	 * @return Returns com
	 * @throws IOException
	 */
	private byte [] receiveCom() throws IOException{
		if (channelIn.read() == ABORTING)
			throw new IOException();
		byte [] com = new byte[Commitment.DIGEST_BYTE_SIZE];
		this.channelIn.read(com);
		return com;
	}
	
	/**
	 * Get the MOVA instance
	 * @return the MOVA instance
	 */
	public Mova getMovaInstance(){
		return this.movaInstance;
	}

	/**
	 * Get the channel input
	 * @return the channel input
	 */
	public InputStream getChannelIn() {
		return channelIn;
	}

	/**
	 * Get the channel output
	 * @return the channel output
	 */
	public OutputStream getChannelOut() {
		return channelOut;
	}
	
	private boolean signatureIsValid(BigInteger [] Xsigs, YGroupElement [] Ysigs
			, MovaSecretKey sk) {
		return Arrays.equals(sk.Hom(Xsigs), Ysigs);
	}
	
}
