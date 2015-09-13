package ch.epfl.lasec.mova;

import static org.junit.Assert.*;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import ch.epfl.lasec.IOHelper;

/**
 * Class used to run unit test on the confirmation protocol
 * 
 * @author Sebastien Duc
 *
 */
public class ConfirmationProtocolTest {

	ConfirmationProtocol confirmationVerifier;
	ConfirmationProtocol confirmationProver;
	
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
		DomainParameters dp = new DomainParameters(pk, 80, 10, 10);
		Mova movaInstance = new Mova(dp);

		confirmationProver = new 
				ConfirmationProtocol(channelInProv, channelOutProv, new Mova(movaInstance));
		confirmationVerifier = new 
				ConfirmationProtocol(channelInVerif, channelOutVerif, new Mova(movaInstance));

		// generate random message
		generateRandomMessage();

		// generate signature
		this.s = movaInstance.sign(m, sk);
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

	@Test
	public void testConfirmationProtocol() throws InterruptedException {
		// test completeness of confirmation protocol when used several times 
		testCompleteness();
		
	}
	
	@Test
	public void testConfimrationSoundness() throws InterruptedException{
		// test soundness
		testSoundness();
	}

	private void testCompleteness() throws InterruptedException{
		// verifier thread
		AsyncTester verifier = new AsyncTester(new Thread(){
			@Override
			public void run() {
				boolean res = confirmationVerifier.verifier(new MovaPublicKey(pk), 
						new Message(m), new MovaSignature(s),true);
				assertTrue(res);
			}
		});
		// prover thread
		AsyncTester prover = new AsyncTester(new Thread(){
			@Override
			public void run() {
				boolean res = confirmationProver.prover(new MovaPublicKey(pk), 
						new MovaSecretKey(sk), new Message(m), new MovaSignature(s),true);
				assertTrue(res);
			}
		});
		verifier.start();
		prover.start();
		verifier.test();
		prover.test();
	}

	private void testSoundness() throws InterruptedException{
		// corrput the signature
		corruptSignature();
		AsyncTester verifier = new AsyncTester(new Thread(){
			@Override
			public void run() {
				boolean res = confirmationVerifier.verifier(new MovaPublicKey(pk), 
						new Message(m), new MovaSignature(s),true);
				assertFalse(res);
			}
		});

		AsyncTester prover = new AsyncTester(new Thread() {
			@Override
			public void run() {
				boolean res = confirmationProver.prover(new MovaPublicKey(pk), 
						new MovaSecretKey(sk), new Message(m), new MovaSignature(s),true);
				assertFalse(res);
			}
		});

		verifier.start();
		prover.start();
		verifier.test();
		prover.test();
	}

	private void corruptSignature(){
		// modify signature so that it is not valid anymore
		byte [] randomM = new byte [1000];
		Random rand = new Random();
		rand.nextBytes(randomM);
		this.s = confirmationProver.getMovaInstance().sign(new Message(randomM), sk);
	}

	public void tearDown(){
		IOHelper.closeQuietly(this.channelInProv);
		IOHelper.closeQuietly(this.channelOutProv);
		IOHelper.closeQuietly(this.channelInVerif);
		IOHelper.closeQuietly(this.channelOutVerif);
	}

}
