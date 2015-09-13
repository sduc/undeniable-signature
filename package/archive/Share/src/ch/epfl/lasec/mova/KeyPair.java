package ch.epfl.lasec.mova;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

import ch.epfl.lasec.IOHelper;

/**
 * Class representing a key pair:<br/>
 * <dd>  (sk,pk)<br/>
 * where sk is the secret key and pk is the public key
 * 
 * @author Sebastien Duc
 *
 */
public class KeyPair {
	
	/**
	 * Secret key
	 */
	private MovaPublicKey pk;
	
	/**
	 * Public key
	 */
	private MovaSecretKey sk;
	
	/**
	 * Creates a KeyPair with public key pk and secret key sk
	 * @param pk public key
	 * @param sk secret key
	 */
	public KeyPair(MovaPublicKey pk, MovaSecretKey sk) {
		this.pk = pk;
		this.sk = sk;
	}

	/**
	 * Get the public key
	 * @return the public key
	 */
	public MovaPublicKey getPk() {
		return pk;
	}

	/**
	 * Get the secret key
	 * @return the secret key
	 */
	public MovaSecretKey getSk() {
		return sk;
	}
	
	/**
	 * Write the key pair in the stream
	 * @param os
	 * @throws IOException
	 */
	public void write(OutputStream os) throws IOException {
		IOHelper.writeEncodedObject(os, pk.getEncoded());
		IOHelper.writeEncodedObject(os, sk.getEncoded());
	}
	
	/**
	 * Read a key pair from a stream.
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static KeyPair read(InputStream is) throws IOException {
		MovaPublicKey pk = MovaPublicKey.getKeyFromEncoding(IOHelper.readEncodedObject(is));
		MovaSecretKey sk = new MovaSecretKey(IOHelper.readEncodedObject(is));
		return new KeyPair(pk, sk);
	}

}
