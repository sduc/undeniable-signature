package ch.epfl.lasec.mova;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 * Unit test used to test class MovaPublicKey
 * 
 * @author Sebastien Duc
 *
 */
public class MovaPublicKeyTest {

	@Test
	public void testGetEncoded() {
		KeyPairGenerator kpg = new KeyPairGenerator(80, 512, 512/8);
		KeyPair kp = kpg.generateKeyPair();
		MovaPublicKey pk = kp.getPk();
		byte [] encoded = pk.getEncoded();
		assertEquals("Error: Dec(Enc(key)) != key",
				pk, 
				MovaPublicKey.getKeyFromEncoding(encoded));
	}
	
}
