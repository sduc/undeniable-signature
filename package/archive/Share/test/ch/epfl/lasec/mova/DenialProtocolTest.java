package ch.epfl.lasec.mova;

import static org.junit.Assert.*;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.epfl.lasec.IOHelper;

/**
 * Class used do perform unit tests on the denial protocol class.
 * 
 * @author Sebastien Duc
 *
 */
public class DenialProtocolTest {
	
	DenialProtocol denialVerifier;
	DenialProtocol denialProver;

	PipedOutputStream channelOutVerif;
	PipedInputStream channelInProv;
	PipedOutputStream channelOutProv;
	PipedInputStream channelInVerif;
	
	MovaPublicKey pk;
	MovaSecretKey sk;

	Message m;
	MovaSignature s;
	
	@Before
	public void setUp() throws Exception {
		// simulation the channel with a pipe
		channelOutVerif = new PipedOutputStream();
		channelInProv = new PipedInputStream(channelOutVerif);
		channelOutProv = new PipedOutputStream();
		channelInVerif = new PipedInputStream(channelOutProv);
		// Key generator
		initKeys();
		// Mova instance
		DomainParameters dp = new DomainParameters(pk, 40, 10, 10);
		Mova movaInstance = new Mova(dp);

		this.denialProver = new DenialProtocol(channelInProv, channelOutProv, 
				new Mova(movaInstance));
		this.denialVerifier = new DenialProtocol(channelInVerif, channelOutVerif, 
				new Mova(movaInstance));
		
		
	}

	@After
	public void tearDown() throws Exception {
		IOHelper.closeQuietly(this.channelInProv);
		IOHelper.closeQuietly(this.channelOutProv);
		IOHelper.closeQuietly(this.channelInVerif);
		IOHelper.closeQuietly(this.channelOutVerif);
	}

	@Test
	public void testCompletness() throws InterruptedException {
		// generate random message
		generateRandomMessage();
		// corrput the signature
		corruptSignature();
		
		// verifier thread
		AsyncTester verifier = new AsyncTester(new Thread() {
			@Override
			public void run() {
				boolean res = denialVerifier.verifier(new MovaPublicKey(pk),
						new Message(m), new MovaSignature(s), true);
				assertTrue(res);
			}
		});

		// prover thread
		AsyncTester prover = new AsyncTester(new Thread() {
			@Override
			public void run() {
				boolean res = denialProver.prover(new MovaPublicKey(pk),
						new MovaSecretKey(sk), new Message(m),
						new MovaSignature(s), true);
				assertTrue(res);
			}
		});
		verifier.start();
		prover.start();
		verifier.test();
		prover.test();
	}
	
	@Test
	public void testSoudness() throws InterruptedException {
		// generate random message
		generateRandomMessage();

		// generate signature
		this.s = this.denialProver.getMovaInstance().sign(m, sk);
		
		// verifier thread
		AsyncTester verifier = new AsyncTester(new Thread(){
			@Override
			public void run() {
				boolean res = denialVerifier.verifier(new MovaPublicKey(pk), 
						new Message(m), new MovaSignature(s),true);
				assertFalse(res);
			}
		});

		// prover thread
		AsyncTester prover = new AsyncTester(new Thread() {
			@Override
			public void run() {
				boolean res = denialProver.prover(new MovaPublicKey(pk),
						new MovaSecretKey(sk), new Message(m),
						new MovaSignature(s), true);
				assertFalse(res);
			}
		});
		verifier.start();
		prover.start();
		verifier.test();
		prover.test();
	}
	
	private void initKeys(){
		KeyPairGenerator kpg = new KeyPairGenerator(80, 512, 512/8);
		KeyPair kp = kpg.generateKeyPair();
		pk = kp.getPk();
		sk = kp.getSk();
	}

	private void generateRandomMessage(){
		byte [] randomM = new byte [1000];
		Random rand = new Random();
		rand.nextBytes(randomM);
		this.m = new Message(randomM);
	}
	
	private void corruptSignature(){
		// modify signature so that it is not valid anymore
		byte [] randomM = new byte [1000];
		Random rand = new Random();
		rand.nextBytes(randomM);
		this.s = denialProver.getMovaInstance().sign(new Message(randomM), sk);
	}

}
