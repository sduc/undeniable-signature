package ch.epfl.lasec.universitycontest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

import ch.epfl.lasec.IOHelper;
import ch.epfl.lasec.LogContainer;
import ch.epfl.lasec.mova.CommunicationProtocol;
import ch.epfl.lasec.mova.Message;
import ch.epfl.lasec.mova.Mova;
import ch.epfl.lasec.mova.MovaPublicKey;
import ch.epfl.lasec.mova.MovaSignature;
import ch.epfl.lasec.tuple.Tuple;

/**
 * Class used to handle the protocol for university protocol.
 * 
 * @author Sebastien Duc
 *
 */
public class UniversityContestProtocol {
	
	/**
	 * Used by the client to query the latest challenge.
	 */
	public static final int ASK_NEW_CHALLENGE = 0;
	
	/**
	 * Used by the client to query his team score for all quiz the team did. 
	 */
	public static final int ASK_TEAM_SCORE = 1;
	
	/**
	 * Used by the client to query the server his university score.
	 */
	public static final int ASK_UNIVERSITY_SCORE = 2;
	
	/**
	 * Used by the client to query the solution of a specific challenge
	 * with the details score he had.
	 */
	public static final int ASK_CHALLENGE_SOLUTION = 6;
	
	/**
	 * Used by the client to tell the server he is going to send the filled challenge.
	 */
	public static final int SEND_CHALLENGE = 3;
	
	/**
	 * Used by the client to tell the server he wants to run a protocol linked with MOVA.
	 */
	public static final int STATE_MOVA = 4;
	
	/**
	 * Used to tell the other party that the protocol has to be ended.
	 */
	public static final int END = 5;
	
	/**
	 * Used to start authentication of the team.
	 */
	public static final int AUTH = 7;
	
	public static final int CURRENT_CHALLENGE_NULL = 0;
	public static final int SENDING_CURRENT_CHALLENGE_ID = 1;
	
	public static final int NULL = 42;
	public static final int NOT_NULL = 43;
	
	/**
	 * Charset used to encode strings
	 */
	public final static String CHARSET = "UTF-8";
	
	/**
	 * Send challenge over the network.
	 * @param out output channel
	 * @param c challenge to send
	 * @throws IOException
	 */
	public static void sendChallenge(OutputStream out , Challenge c) 
			throws IOException {
		IOHelper.writeEncodedObject(out, c.getEncoded());
	}
	
	/**
	 * Receive a challenge from the network.
	 * @param in input channel
	 * @return the received challenge
	 * @throws IOException
	 */
	public static Challenge receiveChallenge(InputStream in) 
			throws IOException {
		return Challenge.fromEncoded(IOHelper.readEncodedObject(in));
	}
	
	/**
	 * Send an array of scores over the network
	 * @param out
	 * @param scores
	 * @throws IOException
	 */
	public static void sendScores(OutputStream out, Score [] scores) throws IOException {
		//send the number of scores (length of the array)
		IOHelper.writeEncodedBigInt(out, BigInteger.valueOf(scores.length));
		//send the array
		for (Score score : scores) {
			IOHelper.writeEncodedObject(out, score.getEncoded());
		}
	}
	
	/**
	 * Receive an array of scores from the network
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static Score [] receiveScores(InputStream in) throws IOException {
		// get the number of scores (the length of the array)
		int length = IOHelper.readEncodedBigInt(in).intValue();
		Score [] scores = new Score[length];
		// get each score element
		for (int i = 0; i < scores.length; i++) {
			scores[i] = new Score(IOHelper.readEncodedObject(in));
		}
		return scores;
	}
	
	/**
	 * Ask the server for the newest challenge
	 * @param in Input channel
	 * @param out Output channel
	 * @return The newest challenge. If no new challenge, returns null.
	 * @throws IOException
	 * @throws InvalidSignatureException 
	 */
	public static Challenge askNewChallenge(InputStream in , OutputStream out, 
			Mova mova,
			MovaPublicKey mpk) throws IOException, InvalidSignatureException{
		return askNewChallenge(in, out, mova, mpk, null);
	}
	
	public static Challenge askNewChallenge(InputStream in , OutputStream out, 
			Mova mova,
			MovaPublicKey mpk,
			LogContainer logs)
			throws IOException, InvalidSignatureException {
		out.write(ASK_NEW_CHALLENGE);
		int isNull = in.read();
		if (isNull == NULL)
			return null;
		else {
			Challenge c = receiveChallenge(in);
			MovaSignature s = CommunicationProtocol.receiveSignature(in);
			
			//verify : GOTO state mova
			out.write(STATE_MOVA);
			//TODO 
			if (!CommunicationProtocol.askConfirmation(in, out, new Message(c.getEncoded()), 
					s, mova, mpk, mova.getIcon(),logs))
				throw new InvalidSignatureException();
			
			return c;
		}
	}
	
	/**
	 * Handle the client query askNewestChallenge. If there is a new one send it
	 * if not do nothing.
	 * @param in
	 * @param out
	 * @param newest
	 * @throws IOException
	 */
	public static void handleAskNewChallenge(InputStream in, OutputStream out, Challenge newest
			, MovaSignature sNewest)
			throws IOException {
		if(newest == null){
			out.write(NULL);
		}
		else {
			out.write(NOT_NULL);
			sendChallenge(out, newest);
			CommunicationProtocol.sendSignature(out, sNewest);
		}
	}
	
	
	/**
	 * Ask the server for the score of all the universities.
	 * @param in
	 * @param out
	 * @return
	 * @throws IOException
	 */
	public static Score[] askUniversityScore(InputStream in, OutputStream out)
			throws IOException {
		out.write(ASK_UNIVERSITY_SCORE);
		Score [] s = receiveScores(in);
		return s;
	}

	/**
	 * Used by the server to handle the query of the client which asks for the scores of 
	 * the universities.
	 * @param in
	 * @param out
	 * @param s
	 * @throws IOException
	 */
	public static void handleAskUniversityScore(InputStream in,
			OutputStream out, Score[] s) throws IOException {
		sendScores(out, s);
		
	}
	
	/**
	 * Query the server to get the user's team scores.
	 * 
	 * @param in
	 * @param out
	 * @return
	 * @throws IOException
	 */
	public static Score [] askTeamScore(InputStream in,
			OutputStream out) throws IOException {
		out.write(ASK_TEAM_SCORE);
		Score  [] s = receiveScores(in);
		return s;
		
	}
	
	/**
	 * Handle the query from the client who is asking for his team scores.
	 * 
	 * @param in
	 * @param out
	 * @param s
	 * @throws IOException
	 */
	public static void handleAskTeamScore(InputStream in, 
			OutputStream out, Score [] s) throws IOException {
		sendScores(out, s);
	}
	
	/**
	 * Used by the client to query the server for the solution of a specific (challengeName)
	 * challenge the client did. The client also receives the score he did in detail.
	 * 
	 * @param in
	 * @param out
	 * @param challengeName Name of the challenge of which the client wants to see the solution
	 * @return the score and the solution the client did for the specific challenge
	 * @throws IOException
	 */
	public static ChallengeScore askChallengeSolution(InputStream in,
			OutputStream out, String challengeName) throws IOException {
		out.write(ASK_CHALLENGE_SOLUTION);
		IOHelper.writeEncodedObject(out , challengeName.getBytes(CHARSET));
		//return new ChallengeScore(IOHelper.readEncodedObject(in));
		return ChallengeScore.read(in);
	}
	
	public static void handleAskChallengeSolution(OutputStream out, 
			ChallengeScore askedScore) throws IOException{
		//IOHelper.writeEncodedObject(out, askedScore.getEncoded());
		askedScore.write(out);
	}
	
	/**
	 * Send the challenge ID of challenge c.
	 * 
	 * @param out
	 * @param c
	 * @throws IOException
	 */
	public static void sendingChallengeId(OutputStream out, Challenge c)
			throws IOException {
		IOHelper.writeEncodedObject(out,
				BigInteger.valueOf(c.getTitle().hashCode()).toByteArray());
	}
	
	/**
	 * Ask the mova public key on the network.
	 * @param in
	 * @param out
	 * @return
	 * @throws IOException
	 */
	public static MovaPublicKey askMovaPublicKey(InputStream in, OutputStream out) 
			throws IOException {
		out.write(UniversityContestProtocol.STATE_MOVA);
		return CommunicationProtocol.askPublicKey(in, out);
	}
	
	/**
	 * Ask the mova instance to the server.
	 * @param in
	 * @param out
	 * @return
	 * @throws IOException
	 */
	public static Mova askMovaInstance(InputStream in, OutputStream out) 
			throws IOException {
		out.write(UniversityContestProtocol.STATE_MOVA);
		return CommunicationProtocol.askMovaInstance(in, out);
	}
	
	/**
	 * Ask the server to sign a challenge and send the signature back to the client.
	 * @param in
	 * @param out
	 * @param c
	 * @return
	 * @throws IOException
	 */
	public static MovaSignature askToSign(InputStream in, OutputStream out, Challenge c) 
			throws IOException {
		out.write(UniversityContestProtocol.STATE_MOVA);
		return CommunicationProtocol.askToSign(in, out, new Message(c.getEncoded()));
	}
	
	/**
	 * Method used by the client to send the filled signed (by the client's university server)
	 * challenge to the adversary university server.
	 * @param in
	 * @param out
	 * @param c
	 * @param sig
	 * @throws IOException
	 */
	public static void sendFilledSignedChallenge(InputStream in, OutputStream out,
			Challenge c, MovaSignature sig) throws IOException{
		out.write(SEND_CHALLENGE);
		sendChallenge(out, c);
		CommunicationProtocol.sendSignature(out, sig);
	}
	
	/**
	 * Method used by the server to receive the filled signed challenge from the client.
	 * @param in
	 * @param out
	 * @return the challenge and its signature
	 * @throws IOException
	 */
	public static Tuple receiveFilledSignedChallenge(InputStream in, OutputStream out) 
			throws IOException{
		Challenge c = receiveChallenge(in);
		MovaSignature sig = CommunicationProtocol.receiveSignature(in);
		
		return Challenge.CHALLENGE_SIGNATURE_COUPLE_TYPE.createTuple(c,sig);
	}
	
	
	/**
	 * End the communication between the client and the server.
	 * @param out
	 * @throws IOException
	 */
	public static void endCommunication(OutputStream out) throws IOException{
		out.write(END);
	}
	
	/**
	 * Used to ask authentication to the server. To achieve authentication, a challenge-response
	 * protocol is done.
	 * @param in
	 * @param out
	 * @param teamId ID of the team.
	 * @return true iff authentication succeeded
	 * @throws IOException
	 */
	public static boolean teamAuthentication(InputStream in , OutputStream out, int teamId, 
			String secret) 
			throws IOException {
		out.write(AUTH);
		IOHelper.writeEncodedBigInt(out, BigInteger.valueOf(teamId));
		
		if (in.read() == END){
			return false;
		}
		
		ChallengeResponse cr = new ChallengeResponse(teamId);
		cr.response(secret, in, out);
		
		return (in.read() == 1) ? true : false;
	}
	
	/**
	 * To be called when the server has already handled the authentication of the client
	 * and determines whether it has succeeded. It allows the server to answer to the client
	 * to tell him if it has succeeded.
	 * @param in
	 * @param out
	 * @param answer 
	 * @throws IOException
	 */
	public static void handleTeamAuthentication(InputStream in, OutputStream out, 
			boolean answer) throws IOException{
		out.write((answer)?1:0);
	}
	
}
