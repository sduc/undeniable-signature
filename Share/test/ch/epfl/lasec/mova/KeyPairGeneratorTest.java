package ch.epfl.lasec.mova;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 * Unit test for the key pair generator.
 * 
 * @author Sebastien Duc
 *
 */
public class KeyPairGeneratorTest {

	@Test
	public void testGeneratePublicKey() {
		int Lkey = 80;
		int modBitSize = 1024;
		int seedSize = 512/8;
		KeyPairGenerator kpg = new KeyPairGenerator(Lkey, modBitSize, seedSize);
		KeyPair kp = kpg.generateKeyPair();
		
		assertEquals("Error : "+kp.getPk().getN().bitLength()+
				" should have the same value that "+modBitSize,
				modBitSize,
				kp.getPk().getN().bitLength());
		
		assertEquals("Error : seed size is incorrect",
				seedSize, 
				kp.getPk().getSeedK().length);
		
		assertEquals("Error : Lkey is not correct in the public key",
				Lkey,
				kp.getPk().getYkeys().length);
	}

}
