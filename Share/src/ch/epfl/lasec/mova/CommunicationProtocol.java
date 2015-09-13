package ch.epfl.lasec.mova;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ch.epfl.lasec.IOHelper;
import ch.epfl.lasec.LogContainer;


/**
 * Class implementing functions to communicate Mova parameters,
 * key, and start confirmation or denial protocols.
 * 
 * @author Sebastien Duc
 *
 */
public class CommunicationProtocol {
	
	
	/**
	 * Used by the client to ask the server to send him the public key.
	 */
	public final static int ASK_PUBLIC_KEY = 0;
	
	/**
	 * Used by the client to ask the server to start the confirmation protocol
	 */
	public final static int ASK_CONFIRMATION = 1;
	
	
	/**
	 * Used by the client to ask the server to start the denial protocol
	 */
	public final static int ASK_DENIAL = 2;
	
	/**
	 * Used by the client to ask the server to send him the mova instance
	 */
	public final static int ASK_MOVA = 3;
	
	/**
	 * Used by the client to ask the server to sign a message the client sends him. 
	 */
	public final static int ASK_TO_SIGN = 4;
	
	/**
	 * Send the public key over the channel
	 * @param os
	 * @param pk
	 * @throws IOException
	 */
	public static void sendPublicKey(OutputStream os, MovaPublicKey pk) 
			throws IOException{
		os.write(pk.getEncoded().length);
		os.write(pk.getEncoded());
	}
	
	/**
	 * Receive the public key from the channel.
	 * @param is
	 * @return the mova public key.
	 * @throws IOException
	 */
	public static MovaPublicKey receivePublicKey(InputStream is) 
			throws IOException{
		byte enc [] = new byte [is.read()];
		is.read(enc);
		return MovaPublicKey.getKeyFromEncoding(enc);
	}
	
	/**
	 * Ask for the mova instance using os and receive it from is.
	 * @param in
	 * @param out
	 * @return the received mova instance
	 * @throws IOException
	 */
	public static Mova askMovaInstance(InputStream in, OutputStream out) 
			throws IOException{
		out.write(ASK_MOVA);
		return Mova.read(in);
	}
	
	/**
	 * Method used (usually by the server) to send the mova instance m to the client that 
	 * asked for it
	 * @param in
	 * @param out
	 * @param m Mova instance to send.
	 * @throws IOException
	 */
	public static void sendMovaInstance(InputStream in, OutputStream out, Mova m) 
			throws IOException {
		m.write(out);
	}
	
	/**
	 * Ask for the public key using os and receive it from is.
	 * @param is
	 * @param os
	 * @return the received MOVA public key
	 * @throws IOException
	 */
	public static MovaPublicKey askPublicKey(InputStream in, OutputStream out) 
			throws IOException{
		out.write(ASK_PUBLIC_KEY);
		return receivePublicKey(in);
	}
	
	/**
	 * Method used to ask for the confirmation of validity of (m,s).
	 * It starts the MOVA confirmation protocol.
	 * @param in
	 * @param out 
	 * @param m message
	 * @param s signature
	 * @param mova mova instance
	 * @param pk Public key
	 * @param Icon the security parameter. It can be decided by the verifier 
	 * 			if not convinced by the default value sent by the prover.
	 * @return true if the signature is valid. 
	 * Returns false if the confirmation failed. 
	 * This doesn't mean that it is not valid.
	 * @throws IOException
	 */
	public static boolean askConfirmation(InputStream in, OutputStream out, 
			Message m, MovaSignature s, Mova mova, MovaPublicKey pk, int Icon) 
					throws IOException{
		return askMovaProtocol(in, out, m, s, mova, pk, Icon, ASK_CONFIRMATION,null);
	}
	
	public static boolean askConfirmation(InputStream in, OutputStream out, 
			Message m, MovaSignature s, Mova mova, MovaPublicKey pk, int Icon, 
			LogContainer logs) 
					throws IOException{
		return askMovaProtocol(in, out, m, s, mova, pk, Icon, ASK_CONFIRMATION,logs);
	}
	
	/**
	 * Method used to ask for the denial of validity of (m,s).
	 * It starts the MOVA denial protocol.
	 * @param in
	 * @param out
	 * @param m message
	 * @param s signature
	 * @param mova MOVA instance
	 * @param pk Public key
	 * @param Iden the security parameter. It can be decided by the verifier 
	 * 			if not convinced by the default value sent by the prover.
	 * @return true if the signature is invalid. 
	 * Returns false if the denial failed. 
	 * This doesn't mean that it is not invalid.
	 * @throws IOException
	 */
	public static boolean askDenial(InputStream in, OutputStream out, 
			Message m, MovaSignature s, Mova mova, MovaPublicKey pk, int Iden) 
					throws IOException{
		return askMovaProtocol(in, out, m, s, mova, pk, Iden, ASK_DENIAL,null);
	}

	private static boolean askMovaProtocol(InputStream in, OutputStream out,
			Message m, MovaSignature s, Mova mova, MovaPublicKey pk, int Icon,
			final int ASK_PROTOCOL, LogContainer logs) throws IOException {
		
		out.write(ASK_PROTOCOL);
		// send (m,s) to the prover
		sendMessage(out, m);
		sendSignature(out, s);
		// agree on the Icon
		out.write(Icon);
		mova.setIcon(Icon);
		// run the confirmation protocol
		if (ASK_PROTOCOL == ASK_CONFIRMATION)
			return new ConfirmationProtocol(in, out, mova).verifier(pk, m, s, logs);
		else
			return new DenialProtocol(in, out, mova).verifier(pk, m, s);
	}
	
	/**
	 * Method used to accept the confirmation of validity that was asked
	 * and start the MOVA confirmation protocol.
	 * Method to call when ASK_CONFIRMATION was received.
	 * @param in
	 * @param out
	 * @param mova MOVA instance
	 * @param kp Key pair (public,secret)
	 * @return true if the signature is valid.
	 * Returns false if the confirmation failed.
	 * This doesn't mean that it is not valid.
	 * @throws IOException 
	 */
	public static boolean acceptConfirmation(InputStream in, OutputStream out,
			Mova mova, KeyPair kp) 
			throws IOException{
		return acceptMovaProtocol(in, out, mova, kp, ASK_CONFIRMATION);
	}
	
	/**
	 * Method used to accept the denial of validity that was asked
	 * and start the MOVA denial protocol.
	 * Method to call when ASK_DENIAL was received.
	 * @param in
	 * @param out
	 * @param mova MOVA instance
	 * @param kp Key pair (public,secret)
	 * @return true if the signature is invalid. 
	 * Returns false if the denial failed. 
	 * This doesn't mean that it is not invalid.
	 * @throws IOException
	 */
	public static boolean acceptDenial(InputStream in, OutputStream out,
			Mova mova, KeyPair kp) 
			throws IOException{
		return acceptMovaProtocol(in, out, mova, kp, ASK_DENIAL);
	}
	
	private static boolean acceptMovaProtocol(InputStream in, OutputStream out,
			Mova mova, KeyPair kp, final int ASK_PROTOCOL) throws IOException {
		// receive (m,s)
		Message m = receiveMessage(in);
		MovaSignature s = receiveSignature(in);
		// agree on Icon
		int Icon = in.read();
		mova.setIcon(Icon);
		// run the confirmation protocol
		if (ASK_PROTOCOL == ASK_CONFIRMATION)
			return new ConfirmationProtocol(in, out, mova).prover(kp.getPk(),
					kp.getSk(), m, s, true);
		else
			return new DenialProtocol(in, out, mova).prover(kp.getPk(),
					kp.getSk(), m, s);
	}
	
	/**
	 * Sends message m over the channel.
	 * @param out
	 * @param m
	 * @throws IOException
	 */
	public static void sendMessage(OutputStream out, Message m) 
			throws IOException{
		IOHelper.writeEncodedObject(out, m.getEncoded());
	}
	
	/**
	 * Receive message from the channel.
	 * @param in
	 * @return the received message
	 * @throws IOException
	 */
	public static Message receiveMessage(InputStream in) 
			throws IOException{
		return new Message(IOHelper.readEncodedObject(in));
	}
	
	/**
	 * Sends signature s over the channel.
	 * @param out
	 * @param s
	 * @throws IOException
	 */
	public static void sendSignature(OutputStream out, MovaSignature s) 
			throws IOException{
		IOHelper.writeEncodedObject(out, s.getEncoded());
	}
	
	/**
	 * Receive a signature from the channel.
	 * @param in
	 * @return the received signature
	 * @throws IOException
	 */
	public static MovaSignature receiveSignature(InputStream in) 
			throws IOException{
		return new MovaSignature(IOHelper.readEncodedObject(in));
	}
	
	public static MovaSignature askToSign(InputStream in, OutputStream out, 
			Message m) throws IOException {
		out.write(ASK_TO_SIGN);
		sendMessage(out, m);
		return receiveSignature(in);
	}
	
	public static void handleAskToSign(InputStream in, OutputStream out, 
			Mova m, MovaSecretKey msk) throws IOException {
		Message msg = receiveMessage(in);
		//sign the message
		MovaSignature s = m.sign(msg, msk);
		sendSignature(out, s);
	}
	
	
}
