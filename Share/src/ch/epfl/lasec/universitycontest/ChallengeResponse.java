package ch.epfl.lasec.universitycontest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

/**
 * Class used for challenge response authentication.
 * 
 * @author Sebastien Duc
 *
 */
public class ChallengeResponse {

	private int teamID;
	
	private MessageDigest hash;
	
	/**
	 * algorithm to use for the hash function
	 */
	private final static String ALGORITHM = "SHA-256";
	
	private final static String CHARSET = "UTF-8";
	
	/**
	 * Random number generator used to generate r
	 */
	private Random rand;
	
	public final static int CHALLENGE_BYTE_SIZE = 32;
	public final static int DIGEST_BYTE_SIZE = 32;
	
	public ChallengeResponse(int id) {
		teamID = id;
		rand = new Random();
		try {
			hash = MessageDigest.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			assert false;
		}
	}
	
	/**
	 * Send the challenge using steam out.
	 * @param out Stream
	 * @throws IOException
	 */
	public void sendChallenge(OutputStream out) throws IOException{
		byte [] c = genChallenge();
		DataOutputStream dos = new DataOutputStream(out);
		dos.write(c);
	}
	
	/**
	 * Generate a challenge which is a random array of bytes
	 * @return the challenge, an array of bytes of size CHALLENGE_BYTE_SIZE
	 */
	public byte[] genChallenge(){
		byte [] c = new byte [CHALLENGE_BYTE_SIZE];
		rand.nextBytes(c);
		return c;
	}
	
	/**
	 * get the corresponding response which is h(team_id|challenge|secret).
	 * @param secret the preshared secret
	 * @param challenge the challenge.
	 * @return the response which is the digest of the hash function fed with
	 * the team ID, the challenge and the preshared secret.
	 */
	public byte[] genResponse(String secret,byte [] challenge){
		try {
			byte [] encS = secret.getBytes(CHARSET);
			hash.update(BigInteger.valueOf(teamID).toByteArray());
			hash.update(challenge);
			hash.update(encS);
		} catch (UnsupportedEncodingException e) {
			assert false;
		}
		return hash.digest();
	}
	
	/**
	 * Do the response part of the challenge response protocol. It consist of
	 * receiving the challenge and computing+sending the response using the secret.
	 * @param secret The preshared secret
	 * @param in Stream
	 * @param out Stream
	 * @throws IOException
	 */
	public void response(String secret,InputStream in, OutputStream out) throws IOException{
		
		DataInputStream dis = new DataInputStream(in);
		DataOutputStream dos = new DataOutputStream(out);
		
		byte [] c = new byte[CHALLENGE_BYTE_SIZE];
		dis.read(c);
		byte [] r = genResponse(secret, c);
		dos.write(r);
		
	}
	
	/**
	 * Do the challenge part of the challenge response protocol. It consists of sending the challenge,
	 * receive the response and finally check if the response is correct.
	 * @param secret The preshared secret
	 * @param in Stream
	 * @param out Stream
	 * @return true if the authentication succeeded, false otherwise.
	 * @throws IOException 
	 */
	public boolean challenge(String secret, InputStream in, OutputStream out) throws IOException{
		
		DataInputStream dis = new DataInputStream(in);
		DataOutputStream dos = new DataOutputStream(out);
		
		byte [] challenge = genChallenge();
		dos.write(challenge);
		byte [] response = new byte [DIGEST_BYTE_SIZE];
		dis.read(response);
		return verify(challenge, response, secret);
	}
	
	/**
	 * Verify if the response is correct for challenge.
	 * @param challenge the sent challenge.
	 * @param response the received response.
	 * @param secret the preshared secret.
	 * @return true if the response is correct. False otherwise.
	 */
	public boolean verify(byte [] challenge, byte [] response, String secret) {
		byte [] expectedResponse = genResponse(secret, challenge);
		return Arrays.equals(expectedResponse, response);
	}
}
