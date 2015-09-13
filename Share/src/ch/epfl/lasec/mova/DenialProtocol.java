package ch.epfl.lasec.mova;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

import ch.epfl.lasec.IOHelper;

/**
 * Class used to implement the denial protocol.
 * The two main methods are implementing the prover and the verifier side 
 * of the protocol. <br/>
 * For more information on the protocol, refer to the coGHIproof in MOVA.
 * 
 * @author Sebastien Duc
 *
 */
public class DenialProtocol implements InteractiveProof {

	/**
	 * InputStream used to receive data from the channel.
	 */
	private InputStream channelIn;

	/**
	 * OutputStream used to write on the channel.
	 */
	private OutputStream channelOut;

	/**
	 * Mova instance use to have access to domain parameters and cryptographic
	 * primitives.
	 */
	private Mova movaInstance;

	/**
	 * Creates a Denial Protocol
	 * 
	 * @param in
	 * @param out
	 * @param movaInstance
	 */
	public DenialProtocol(InputStream in, OutputStream out, Mova movaInstance) {
		this.movaInstance = movaInstance;
		this.channelIn = in;
		this.channelOut = out;
	}

	public boolean verifier(MovaPublicKey pk, Message m, MovaSignature s) {
		return verifier(pk, m, s, false);
	}
	
	public boolean verifier(MovaPublicKey pk, Message m, MovaSignature s, boolean debugMode){
		////////////////////////////////
		/// Initialization         /////
		////////////////////////////////
		//------------------------------
		if (debugMode)
			System.out.println("INIT");
		//------------------------------
		BigInteger [] Xkeys = pk.recoverXkeysFromSeed(this.movaInstance);
		BigInteger [] Xsigs = MovaSignature.recoverXsigsFromMessage(m, this.movaInstance);
		YGroupElement [] Ykeys = pk.getYkeys();
		YGroupElement [] Zsigs = s.getYsigs();
		
		//////////////////////////////////
		///// step 1 of the protocol /////
		//////////////////////////////////
		//------------------------------
		if (debugMode){
			System.out.println("STEP -1-");
			System.out.println("step 1: the verifier generates randomly"+
			" a_ijk and r_ik, i = 1,...,IDen, j = 1,...,Lkey and k = 1,...,Lsig");
		}
		//------------------------------
		BigInteger [][] r = generateRandomR();
		
		int [][][] a = generateRandomA();
		
		int [] lambda = generateRandomLambda();
		
		// compute u and w
		BigInteger [][] u = computeU(Xkeys,Xsigs,r,a,lambda);
		YGroupElement [][] w = computeW(Ykeys,Zsigs,a,lambda);
		//------------------------------
		if (debugMode)
			System.out.println("step 1: send u and w");
		//------------------------------
		
		try {
			sendU(u);
			sendW(w);
		} catch (IOException e) {
			e.printStackTrace();
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
		byte [] com = null;
		try {
			com = this.receiveCom();
			// send r and a to the prover
			//------------------------------
			if (debugMode)
				System.out.println("step 3: send r and a to prover");
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
			System.out.println("step 5: receive lambda and decR to open commitment");
		}
		//------------------------------
		int [] lambdaProver = null;
		byte [] decR = null;
		try {
			lambdaProver = this.receiveLambda();
			decR = this.receiveDecR();
		} catch (IOException e) {
			return false;
		}
		
		// ------------------------------
		if (debugMode)
			System.out.println("step 5: check that lambda[i] = lambda[i] for all i");
		// ------------------------------
		// check that lambda[i] = lambda[i] for all i
		if (lambda.length != lambdaProver.length){
			return false;
		}
		for (int i = 0; i < lambdaProver.length; i++) {
			if (lambdaProver[i] != lambda[i]){
				//---------------------
				if (debugMode){
					System.out.println("step 5: arrays lambda and"+
										" lambdaProver are not equal");
					System.out.println("step 5: array lambda = "+
										Arrays.toString(lambda));
					System.out.println("step 5: array lambdaProver = "+
										Arrays.toString(lambdaProver));
				}
				//---------------------
				return false;
			}
		}
		
		//------------------------------
		if (debugMode)
			System.out.println("step 5: open commitment");
		//------------------------------
		// open commitment
		return this.movaInstance.primitives.getCommitment().
				open(com, getEncodedLamda(lambdaProver), decR);

	}

	public boolean prover(MovaPublicKey pk, MovaSecretKey sk, Message m,
			MovaSignature s) {
		return prover(pk, sk, m, s, false);
	}
	
	public boolean prover(MovaPublicKey pk, MovaSecretKey sk, Message m,
			MovaSignature s, boolean debugMode) {
		////////////////////////////////
		/// Initialization         /////
		////////////////////////////////
		//------------------------------
		if (debugMode)
			System.out.println("INIT");
		//------------------------------
		BigInteger [] Xkeys = pk.recoverXkeysFromSeed(this.movaInstance);
		BigInteger [] Xsigs = MovaSignature.recoverXsigsFromMessage(m, this.movaInstance);
		YGroupElement [] Ykeys = pk.getYkeys();
		YGroupElement [] Zsigs = s.getYsigs();
		
		////////////////////////////
		// Step 2 of the protocol //
		////////////////////////////
		//------------------------------
		if (debugMode)
			System.out.println("STEP -2-");
		// the prover receives the values u and w
		if (debugMode)
			System.out.println("step 2: the prover receives the values u and w");
		//------------------------------
		// receive u and w
		BigInteger [][] u = null;
		YGroupElement [][] w = null;
		try {
			u = receiveU();
			w = receiveW();
		} catch (IOException e) {
			return false;
		}
		// compute y = Hom(Xsigs)
		//------------------------------
		if (debugMode)
			System.out.println("step 2: compute y = Hom(Xsigs) and"+
							" check that Zsig_k != y_k at least for one k");
		//------------------------------
		
		YGroupElement [] y = computeY(Xsigs,sk);
		
		// check that Zsig_k != y_k for at least one k
		assert y.length == Zsigs.length;
		boolean test = true;
		for (int i = 0; i < y.length; i++) {
			test = test && (y[i].equals(Zsigs[i])); 
		}
		if(test){
			//------------------------------
			if (debugMode)
				System.out.println("step 2: Zsig_k = y_k for all k. Abort protocol.");
			//------------------------------
			try {
				channelOut.write(ABORTING);
			} catch (IOException e) {}
			return false;
		}
		
		// compute v = Hom(u)
		YGroupElement [][] v = computeV(u,sk);
		
		//------------------------------
		if (debugMode)
			System.out.println("step 2: recover lambda");
		//------------------------------
		// recover lambda from u,v
		int [] lambda = recoverLambda(v,w,Zsigs,y);
		
		//commit on lambda
		byte [] com = this.movaInstance.primitives.getCommitment().
										commit(getEncodedLamda(lambda));
		byte [] decR = this.movaInstance.primitives.getCommitment().getR();
		
		// send com to the verifier
		// ------------------------------
		if (debugMode)
			System.out.println("step 2: send com to the verifier");
		// ------------------------------
		try {
			channelOut.write(SENDING_DATA);
			channelOut.write(com);
		} catch (IOException e) {
			return false;
		}
		
		////////////////////////////
		// Step 4 of the protocol //
		////////////////////////////
		// receive r and a from the verifier.
		// ------------------------------
		if (debugMode) {
			System.out.println("STEP -4-");
			System.out.println("step 4: receive r_i and a_ij");
		}
		// ------------------------------
		// receive r_i and a_ij
		BigInteger[][] r = null;
		int[][][] a = null;
		try {
			r = this.receiveR();
			a = this.receiveA();
		} catch (IOException e) {
			return false;
		}
		
		// check that u and w where correctly computed.
		// ------------------------------
		if (debugMode)
			System.out.println("step 4 : check that u and w where correctly computed.");
		// ------------------------------
		BigInteger [][] recomputedU = computeU(Xkeys, Xsigs, r, a, lambda);
		YGroupElement [][] recomputedW = computeW(Ykeys, Zsigs, a, lambda);
		for (int i = 0; i < recomputedW.length; i++) {
			for (int k = 0; k < recomputedW[0].length; k++) {
				if (!recomputedU[i][k].equals(u[i][k]) || 
						! recomputedW[i][k].equals(w[i][k])){
					//-------------------------------
					if (debugMode){
						System.out.println("step 4: u and w where not"+
								" properly computed by verifier");
					}
					//-------------------------------
					try {
						this.channelOut.write(ABORTING);
					} catch (IOException e) {}
					return false;
				}
			}
		}
		// ------------------------------
		if (debugMode)
			System.out.println("step 4: open the commitment"+
								" by sending dec to the verifier");
		// ------------------------------
		// open the commitment by sending dec to the verifier
		try {
			sendDec(lambda, decR);
		} catch (IOException e) {
			return false;
		}

		// ------------------------------
		if (debugMode)
			System.out.println("Prover side finished without errors");
		// ------------------------------
		return true;
	}
	
	/**
	 * Method generating the set of a_ijk in Z/dZ used in the proof.
	 * d = 2 in this implementation.
	 * @return Return a set of randomly generated a_ijk in Z/dZ where d is the order of Ygroup
	 */
	private int[][][] generateRandomA() {
		int [][][] randomA = new 
				int [this.movaInstance.domainParameters.Iden]
					[this.movaInstance.domainParameters.Lsig]
					[this.movaInstance.domainParameters.Lkey];
		Random uniformGenerator = new Random();
		for (int i = 0; i < randomA.length; i++) {
			for (int k = 0; k < randomA[0].length; k++) {
				for (int j = 0; j < randomA[0][0].length; j++) {
					randomA[i][k][j] = (uniformGenerator.nextBoolean())?1:0;
				}
			}
		}
		return randomA;
	}
	
	/**
	 * Method used to generate the random r_ik in (Z/nZ)* used in the protocol
	 * @return the random generated r
	 */
	private BigInteger[][] generateRandomR() {
		BigInteger[][] r = new BigInteger[this.movaInstance.domainParameters.Iden]
										 [this.movaInstance.domainParameters.Lsig];
		for (int i = 0; i < r.length; i++) {
			for (int k = 0; k < r[0].length; k++) {
				r[i][k] = movaInstance.generateRandom_r();
			}
		}
		return r;
	}
	
	/**
	 * Method used to generate uniformly random the lambda_i in Z/pZ 
	 * where p is the smallest prime factor of d = 2. Hence p = 2.
	 * @return the generated lambda
	 */
	private int[] generateRandomLambda() {
		int [] lambda = new int [this.movaInstance.domainParameters.Iden];
		Random uniformGenerator = new Random();
		for (int i = 0; i < lambda.length; i++) {
			lambda[i] = (uniformGenerator.nextBoolean())?1:0;
		}
		return lambda;
	}
	
	/**
	 * 
	 * Method used to compute u_i as													<br/>
	 * u_ik = r_ik^d * (Xkey_1)^a_{i,1,k} * ... * (Xkey_Lkey)^a_{i,Lkey,k} * 			<br/>
	 * <dd>   *(Xsig_k)^lambda_i														<br/>
	 * 
	 * @param Xkeys
	 * @param Xsigs
	 * @param r_ik
	 * @param a_ik
	 * @return Returns u_ik
	 */
	private BigInteger compute_u_ik(BigInteger [] Xkeys, BigInteger Xsigs_k,
								   BigInteger r_ik, int [] a_ik, int lambda_i){
		int d = this.movaInstance.domainParameters.dYgroup;
		BigInteger n = this.movaInstance.domainParameters.nXgroup;
		
		BigInteger u_i = r_ik.modPow(BigInteger.valueOf(d), n);
		for (int j = 0; j < a_ik.length; j++) {
			u_i = u_i.multiply(Xkeys[j].modPow(BigInteger.valueOf(a_ik[j]), n));
			u_i = u_i.mod(n);
		}
		u_i.multiply(Xsigs_k.modPow(BigInteger.valueOf(lambda_i), n));
		
		return u_i.mod(n);
	}

	/**
	 * Method used to compute u_ik for all i,k in range.
	 * 
	 * @param Xkeys
	 * @param Xsigs
	 * @param r
	 * @param a
	 * @param lambda
	 * @return
	 */
	private BigInteger [][] computeU(BigInteger [] Xkeys, BigInteger [] Xsigs, 
								BigInteger [][] r, int [][][] a, int [] lambda){
		
		BigInteger [][] u = new BigInteger[this.movaInstance.domainParameters.Iden]
										[this.movaInstance.domainParameters.Lsig];
		for (int i = 0; i < u.length; i++) {
			for (int k = 0; k < u[0].length; k++) {
				u[i][k] = compute_u_ik(Xkeys, Xsigs[k], r[i][k], a[i][k], lambda[i]);
			}
		}
		return u;
		
	}
	
	/**
	 * 
	 * Method used to compute w_ik.
	 * 
	 * @param ykeys
	 * @param ysigs
	 * @param a
	 * @param lambda
	 * @return
	 */
	private YGroupElement compute_w_ik(YGroupElement[] ykeys, YGroupElement ysigs_k, 
			int[] a_ik, int lambda_i){
		
		YGroupElement w_ik = YGroupElement.one();
		for (int j = 0; j < a_ik.length; j++) {
			w_ik = w_ik.multiply(ykeys[j].pow(a_ik[j]));
		}
		return w_ik.multiply(ysigs_k.pow(lambda_i));
	}
	
	/**
	 * Method used to compute w_ik for all i,k in range.
	 * 
	 * @param ykeys
	 * @param ysigs
	 * @param a
	 * @param lambda
	 * @return
	 */
	private YGroupElement[][] computeW(YGroupElement[] ykeys,
			YGroupElement[] ysigs, int[][][] a, int[] lambda) {
		
		YGroupElement [][] w = new YGroupElement[this.movaInstance.domainParameters.Iden]
												[this.movaInstance.domainParameters.Lsig];
		for (int i = 0; i < w.length; i++) {
			for (int k = 0; k < w[0].length; k++) {
				w[i][k] = compute_w_ik(ykeys,ysigs[k],a[i][k],lambda[i]);
			}
		}
		
		return w;
	}
	
	/**
	 * 
	 * Method used to send u over the channel to the prover.
	 * 
	 * @param u What needs to be sent.
	 * @throws IOException 
	 */
	private void sendU(BigInteger [][] u) throws IOException{
		channelOut.write(SENDING_DATA);
		for (int i = 0; i < u.length; i++) {
			for (int k = 0; k < u[0].length; k++) {
				IOHelper.writeEncodedBigInt(this.channelOut, u[i][k]);
			}
		}
		
	}
	
	/**
	 * 
	 * Method used to handle the communication and receive  u from the verifier.
	 * It receives a stream of bytes and convert it in an array of BigInteger.
	 * 
	 * @return Return an array of BigInteger which contains u_ik
	 * @throws IOException 
	 */
	private BigInteger [][] receiveU() throws IOException{
		if (channelIn.read() == ABORTING)
			throw new IOException();
		
		BigInteger [][] u = new BigInteger [this.movaInstance.domainParameters.Iden]
				[this.movaInstance.domainParameters.Lsig];
		for (int i = 0; i < u.length; i++) {
			for (int k = 0; k < u[0].length; k++) {
				u[i][k] = IOHelper.readEncodedBigInt(this.channelIn);	
			}
		}
		return u;
	}
	
	/**
	 * Method used to send w over the channel to the prover.
	 *  
	 * @param w What needs to be sent.
	 * @throws IOException
	 */
	private void sendW(YGroupElement [][] w) throws IOException{
		channelOut.write(SENDING_DATA);
		for (int i = 0; i < w.length; i++) {
			IOHelper.writeEncodedYGroupElements(channelOut, w[i]);
		}
	}
	
	/**
	 * Method used to handle the communication and receive  w from the verifier.
	 * It receives a stream of bytes and convert it in an array of YGroupElement
	 * 
	 * @return Return an array of BigInteger which contains w_ik
	 * @throws IOException
	 */
	private YGroupElement[][] receiveW() throws IOException{
		if (channelIn.read() == ABORTING)
			throw new IOException();
		YGroupElement [][] w = new YGroupElement[this.movaInstance.domainParameters.Iden][];
		for (int i = 0; i < w.length; i++) {
			w[i] = IOHelper.readEncodedYGroupElements(channelIn);
		}
		return w;
	}
	
	/**
	 * This method is used to compute y<sub>i</sub> = Hom(Xsig<sub>i</sub>)
	 * 
	 * @param Xsigs
	 * @param sk used to compute Hom
	 * @return y which is the image of Xsigs by <bold>Hom</bold>
	 */
	private YGroupElement[] computeY(BigInteger [] Xsigs,MovaSecretKey sk){
		return sk.Hom(Xsigs);
	}
	
	/**
	 * Method used to compute v = Hom(u)
	 * 
	 * @param u
	 * @param sk
	 * @return
	 */
	private YGroupElement[][] computeV(BigInteger [][] u, MovaSecretKey sk){
		YGroupElement [][] v = new YGroupElement[this.movaInstance.domainParameters.Iden][];
		for (int i = 0; i < v.length; i++) {
			v[i] = sk.Hom(u[i]);
		}
		return v;
	}
	
	/**
	 * Recover lambda_i by solving equation <br/>
	 * <dd> w_ik / v_ik = (Zsig_k / y_k)^(lambda_i) <br/>
	 * for at least one k in range.
	 * If not found then generate lamda_i randomly.
	 * @param v_ik
	 * @param w_ik
	 * @param Zsig_i
	 * @param y_i
	 * @return
	 */
	private int recoverLambda_i(YGroupElement [] v_ik, YGroupElement [] w_ik,
			YGroupElement [] Zsig, YGroupElement [] y){
		
		for (int k = 0; k < y.length; k++) {
			if (!Zsig[k].equals(y[k])) {
				YGroupElement wDivByV = w_ik[k].multiply(v_ik[k].inverse());
				YGroupElement zDivByY = Zsig[k].multiply(y[k].inverse());
				if (zDivByY.pow(0).equals(wDivByV)) {
					return 0;
				}
				else{
					return 1;
				}
			}
		}
		Random rand = new Random();
		return rand.nextBoolean()?1:0;
	}
	
	/**
	 * Method used to recover all lambda from v, w, Zsigs, y.
	 * 
	 * @param v
	 * @param w
	 * @param Zsigs
	 * @param y
	 * @return
	 */
	private int[] recoverLambda(YGroupElement[][] v, YGroupElement[][] w, 
			YGroupElement[] Zsigs, YGroupElement[] y){
		int [] lambda = new int [this.movaInstance.domainParameters.Iden];
		for (int i = 0; i < lambda.length; i++) {
			lambda[i] = recoverLambda_i(v[i], w[i], Zsigs, y);
		}
		return lambda;
	}
	
	/**
	 * Method used by the verifier to receive com from the channel, 
	 * where (com,dec) <- commit(lambda)
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
	 * Method used to send r over the channel to the prover.
	 * 
	 * @param r
	 * @throws IOException
	 */
	private void sendR(BigInteger [][] r) throws IOException{
		this.channelOut.write(SENDING_DATA);
		for (int i = 0; i < r.length; i++) {
			for (int k = 0; k < r[0].length; k++) {
				IOHelper.writeEncodedBigInt(channelOut, r[i][k]);
			}	
		}
	}
	
	/**
	 * Method used to send a over the channel to the prover.
	 * 
	 * @param a
	 * @throws IOException
	 */
	private void sendA(int [][][] a) throws IOException{
		this.channelOut.write(SENDING_DATA);
		for (int i = 0; i < a.length; i++) {
			for (int k = 0; k < a[0].length; k++) {
				for (int j = 0; j < a[0][0].length; j++) {
					this.channelOut.write(a[i][k][j]);
				}
			}
		}
	}
	
	/**
	 * Method used to receive r sent by the verifier from the channel
	 * 
	 * @return Returns r that was received
	 * @throws IOException
	 */
	private BigInteger [][] receiveR() throws IOException{
		if (this.channelIn.read() == ABORTING)
			throw new IOException();
		BigInteger [][] r = new BigInteger [this.movaInstance.domainParameters.Iden]
										[this.movaInstance.domainParameters.Lsig];
		for (int i = 0; i < r.length; i++) {
			for (int k = 0; k < r[0].length; k++) {
				r[i][k] = IOHelper.readEncodedBigInt(channelIn);
			}
		}
		return r;
	}
	
	/**
	 * Method used to receive a sent by the verifier from the channel
	 * 
	 * @return Returns a that was received
	 * @throws IOException
	 */
	private int [][][] receiveA() throws IOException{
		if (this.channelIn.read() == ABORTING)
			throw new IOException();
		int [][][] a = new int [this.movaInstance.domainParameters.Iden]
							[this.movaInstance.domainParameters.Lsig]
							[this.movaInstance.domainParameters.Lkey];
		for (int i = 0; i < a.length; i++) {
			for (int k = 0; k < a[0].length; k++) {
				for (int j = 0; j < a[0][0].length; j++) {
					a[i][k][j] = this.channelIn.read();
				}
			}
		}
		return a;
	}
	
	/**
	 * Method used by the prover to open his commitment by sending dec = (lambda,decR) to
	 * the verifier.
	 * 
	 * @param lambda Values that where committed
	 * @param decR Random seed that was used to commit 
	 * @throws IOException
	 */
	private void sendDec(int [] lambda, byte [] decR) throws IOException{
		this.channelOut.write(SENDING_DATA);
		//send lambda
		for (int i = 0; i < lambda.length; i++) {
			channelOut.write(lambda[i]);
		}
		//send decR
		this.channelOut.write(decR);
	}
	
	/**
	 * Method used by the verifier to receive the committed values lambda
	 * 
	 * @return Returns lambda
	 * @throws IOException
	 */
	private int [] receiveLambda() throws IOException{
		if (this.channelIn.read() == ABORTING)
			throw new IOException();
		int [] lambda = new int [this.movaInstance.domainParameters.Iden];
		for (int i = 0; i < lambda.length; i++) {
			lambda[i] = channelIn.read();
		}
		return lambda;
	}
	
	/**
	 * Method used by the verifier to receive the random seed decR for the commitment.
	 * 
	 * @return Returns decR
	 * @throws IOException
	 */
	private byte [] receiveDecR() throws IOException{
		byte [] decR = new byte [Commitment.RANDOM_BYTE_SIZE];
		this.channelIn.read(decR);
		return decR;
	}
	
	/**
	 * to encode lambda into an array of bytes, we use the assumption that p is small.
	 * @param lambda
	 * @return the encoded version of lambda
	 */
	private byte [] getEncodedLamda(int [] lambda){
		byte [] encodedLambda = new byte [lambda.length];
		for (int i = 0; i < encodedLambda.length; i++) {
			encodedLambda[i] = (byte) lambda[i];
		}
		return encodedLambda;
	}
	
	/**
	 * Get the mova instance
	 * @return the mova instance
	 */
	public Mova getMovaInstance(){
		return this.movaInstance;
	}

}
