package ch.epfl.lasec.mova;

import gnu.crypto.prng.IRandom;
import gnu.crypto.prng.LimitReachedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Random;

import ch.epfl.lasec.IOHelper;


/**
 * 
 * Class representing a MOVA instance. 
 * It is defined by the domain paramters and the primitives
 * 
 * @author Sebastien Duc
 *
 */
public class Mova {
	 
	/**
	 * Public domain parameters used for mova scheme
	 */
	public DomainParameters domainParameters;
	
	/**
	 * Cryptographic primitives used in mova scheme
	 */
	public Primitives primitives;
	
	/**
	 * Creates a mova instance with parameters param
	 * @param param
	 */
	public Mova (DomainParameters param){
		domainParameters = param;
		primitives = new Primitives();
	}
	
	/**
	 * Constructor of copy
	 * @param movaInstance Object to copy
	 */
	public Mova(Mova movaInstance) {
		domainParameters = new DomainParameters(movaInstance.domainParameters);
		primitives = new Primitives();
	}
	
	/**
	 * Create a Mova instance
	 * @param pk Mova public key
	 * @param Lsig length of the signature
	 * @param Icon security parameter for the confirmation protocol
	 * @param Iden security parameter for the denial protocol
	 * @return the mova instance
	 */
	public static Mova createMovaInstance(MovaPublicKey pk,int Lsig, int Icon, int Iden){
		return new Mova(new DomainParameters(pk, Lsig, Icon, Iden));
	}
	
	/**
	 * 
	 * Method used to sign message m with the secret key sk and using this as mova instance.
	 * 
	 * @param m Message to sign
	 * @param sk Secret key used to sign the message.
	 * @return Return the mova signature of the message.
	 */
	public MovaSignature sign(Message m, MovaSecretKey sk){
		BigInteger [] Xsigs = primitives.getGenS().generateRandomSequence(m.getEncoded(), 
				domainParameters.Lsig, 
				domainParameters.nXgroup);
		YGroupElement [] Ysigs = new YGroupElement [Xsigs.length];
		for (int i = 0; i < Ysigs.length; i++) {
			Ysigs[i] = sk.Hom(Xsigs[i]);
		}
		return new MovaSignature(Ysigs);
	}
	
	/**
	 * Generate a random r in Xgroup. It is used for confirmation an denial protocol
	 * @return Returns r, a random element in Xgroup
	 */
	public BigInteger generateRandom_r(){
		Random uniformGenerator = new Random();
		BigInteger r = Primitives.
				generateRandomXGroupElement(uniformGenerator, this.domainParameters.nXgroup);
		return r;
	}
	
	/**
	 * Set the domain parameter Icon to Icon
	 * @param Icon
	 */
	public void setIcon(int Icon){
		this.domainParameters.Icon = Icon;
	}
	
	/**
	 * Set the domain parameter Iden to Iden
	 * @param Iden
	 */
	public void setIden(int Iden){
		this.domainParameters.Iden = Iden;
	}
	
	public int getIcon(){
		return this.domainParameters.Icon;
	}
	
	public int getIden(){
		return this.domainParameters.Iden;
	}
	
	/**
	 * Write the mova instance in os
	 * @param os 
	 * @throws IOException 
	 */
	public void write(OutputStream os) throws IOException{
		this.domainParameters.write(os);
	}
	
	/**
	 * Read a mova instance from is
	 * @param is
	 * @return the mova instance that was read.
	 * @throws IOException 
	 */
	public static Mova read(InputStream is) throws IOException{
		return new Mova(DomainParameters.read(is));
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
		Mova m = (Mova) obj;
		return this.domainParameters.equals(m.domainParameters);
	}

	@Override
	public String toString() {
		return "Mova [domainParameters=" + domainParameters + "]";
	}
	
	
	
}

/**
 * Class used to store all domain parameters used in MOVA.
 * We consider characters of order 2 on (Z/nZ)* as secret homomorphism (i.e. the legendre symbol).
 * So, Xgroup is always (Z/nZ)* where n = pq, p and q prime.
 * And Ygroup is always {-1,+1} 
 * 
 * @author Sebastien Duc
 *
 */
class DomainParameters {
	
	/**
	 * integer representing the length of the key
	 */
	public int Lkey;
	
	/**
	 * integer representing the length of the signature
	 */
	public int Lsig;
	
	/**
	 * Security parameter for the confirmation protocol
	 */
	public int Icon;
	
	/**
	 * Security parameter for the denial protocol
	 */
	public int Iden;
	
	/**
	 * integer defining Xgroup. Xgroup is (Z/nXgroupZ)*
	 */
	public BigInteger nXgroup;
	
	/**
	 * dYgroup is the order of Ygroup. It is always two.
	 */
	public final int dYgroup = YGroupElement.ORDER;
	
	/**
	 * TODO
	 */
	private int Ival;
	
	
	/**
	 * Constructor for DomainParameters
	 * @param Lkey
	 * @param Lsig
	 * @param Icon
	 * @param Iden
	 * @param n
	 */
	public DomainParameters(int Lkey, int Lsig, int Icon, int Iden, BigInteger n){
		this.Icon = Icon;
		this.Iden = Iden;
		this.Lkey = Lkey;
		this.Lsig = Lsig;
		this.nXgroup = n;
	}
	
	/**
	 * Create the domain paramters from the public key and some domain values
	 * @param pk
	 * @param Lsig
	 * @param Icon
	 * @param Iden
	 */
	public DomainParameters(MovaPublicKey pk, int Lsig, int Icon, int Iden){
		this.Icon = Icon;
		this.Lsig = Lsig;
		this.Iden = Iden;
		this.nXgroup = pk.getN();
		this.Lkey = pk.getYkeys().length;
	}
	
	/**
	 * Constructor of copy
	 * @param dp
	 */
	public DomainParameters(DomainParameters dp){
		this.Icon = dp.Icon;
		this.Iden = dp.Iden;
		this.Ival = dp.Ival;
		this.Lkey = dp.Lkey;
		this.Lsig = dp.Lsig;
		this.nXgroup = new BigInteger(dp.nXgroup.toByteArray());
	}
	
	/**
	 * Write this in a stream
	 * @param os Stream used to write on
	 * @throws IOException
	 */
	public void write(OutputStream os) throws IOException{
		/*os.write(Lkey);
		os.write(Lsig);
		os.write(Icon);
		os.write(Iden);
		os.write(nXgroup.toByteArray().length);
		os.write(nXgroup.toByteArray());
		os.write(Ival);*/
		
		IOHelper.writeEncodedBigInt(os, BigInteger.valueOf(Lkey));
		IOHelper.writeEncodedBigInt(os, BigInteger.valueOf(Lsig));
		IOHelper.writeEncodedBigInt(os, BigInteger.valueOf(Icon));
		IOHelper.writeEncodedBigInt(os, BigInteger.valueOf(Iden));
		IOHelper.writeEncodedBigInt(os, nXgroup);
	}
	
	/**
	 * Read the domain parameters from is
	 * @param is The stream used to read the domain parameters
	 * @return The domain parameters that where read
	 * @throws IOException
	 */
	public static DomainParameters read(InputStream is) throws IOException{
		/*int Lkey = is.read();
		int Lsig = is.read();
		int Icon = is.read();
		int Iden = is.read();
		byte [] nXgroup = new byte[is.read()];
		is.read(nXgroup);
		int Ival = is.read();*/
		int Lkey = IOHelper.readEncodedBigInt(is).intValue();
		int Lsig = IOHelper.readEncodedBigInt(is).intValue();
		int Icon = IOHelper.readEncodedBigInt(is).intValue();
		int Iden = IOHelper.readEncodedBigInt(is).intValue();
		BigInteger nXgroup = IOHelper.readEncodedBigInt(is);
		return new DomainParameters(Lkey, Lsig, Icon, Iden, nXgroup);
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
		DomainParameters dp = (DomainParameters) obj;
		return this.Icon == dp.Icon && this.Iden == dp.Iden && 
				this.Ival == dp.Ival && this.Lkey == dp.Lkey && 
				this.Lsig == dp.Lsig && this.nXgroup.equals(dp.nXgroup);
	}

	@Override
	public String toString() {
		return "DomainParameters [Lkey=" + Lkey + ", Lsig=" + Lsig + ", Icon="
				+ Icon + ", Iden=" + Iden + ", nXgroup=" + nXgroup
				+ ", dYgroup=" + dYgroup + ", Ival=" + Ival + "]";
	}
	
	
}

/**
 * 
 * Class to represent the cryptographic primitives used in MOVA
 * 
 * @author Sebastien Duc
 *
 */
class Primitives {
	
	/**
	 * Pseudo random generator used for the Key generation
	 */
	private PseudoRandomGenerator GenK;
	
	/**
	 * Pseudo random generator used for the signature generation
	 */
	private PseudoRandomGenerator GenS;
	
	/**
	 * Commitment scheme used for the 4-move MOVA
	 */
	private Commitment commitment;
	
	
	/**
	 * Creates the cryptographic primitives used in MOVA.
	 */
	public Primitives(){
		GenK = new PseudoRandomGenerator();
		GenS = new PseudoRandomGenerator();
		commitment = new Commitment();
	}
	
	/**
	 * Get the Pseudo random generator GenK.
	 * @return the pseudo random generator GenK
	 */
	public PseudoRandomGenerator getGenK(){
		return this.GenK;
	}
	
	/**
	 * Get the Pseudo random generator GenS.
	 * @return the pseudo random generator GenS
	 */
	public PseudoRandomGenerator getGenS(){
		return this.GenS;
	}
	
	/**
	 * Get the commitment.
	 * @return the commitment
	 */
	public Commitment getCommitment(){
		return this.commitment;
	}
	
	/**
	 * 
	 * Generate randomly (using Random r) an element of (Z/nXgroupZ)*
	 * 
	 * @param r Random number generator used to generate the element return by this method
	 * @param nXgroup nXgroup defines Xgroup (which is (Z/nXgroupZ)*
	 * @return a random element g of (Z/nXgroupZ)*
	 */
	public static BigInteger generateRandomXGroupElement(Random r, BigInteger nXgroup){
		BigInteger g;
		do {
			byte [] rand = new byte [nXgroup.toByteArray().length];
			r.nextBytes(rand);
			// keep element in range
			g = new BigInteger(rand).mod(nXgroup);
			// element has to be in (Z/nZ)* when
		} while (!g.gcd(nXgroup).equals(BigInteger.ONE));
		
		return g;
	}
	
	/**
	 * 
	 * Generate randomly (using IRandom r) an element of (Z/nXgroupZ)*
	 * IRandom is from gnu-crypto.
	 * 
	 * @param r Random number generator used to generate the element return by this method
	 * @param nXgroup nXgroup defines Xgroup (which is (Z/nXgroupZ)*
	 * @return a random element g of (Z/nXgroupZ)*
	 */
	
	public static BigInteger generateRandomXGroupElement(IRandom r, BigInteger nXgroup){
		BigInteger g;
		do {
			byte [] rand = new byte [nXgroup.toByteArray().length];
			try {
				r.nextBytes(rand,0,rand.length);
			} 
			catch (IllegalStateException e) {} 
			catch (LimitReachedException e) {}
			// keep element in range
			g = new BigInteger(rand).mod(nXgroup);
			// element has to be in (Z/nZ)* when
		} while (!g.gcd(nXgroup).equals(BigInteger.ONE));
		
		return g;
	}
	
}


