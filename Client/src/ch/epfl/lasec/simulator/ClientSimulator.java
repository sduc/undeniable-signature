package ch.epfl.lasec.simulator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import ch.epfl.lasec.IOHelper;
import ch.epfl.lasec.mova.ConfirmationProtocol;
import ch.epfl.lasec.mova.DenialProtocol;
import ch.epfl.lasec.mova.Message;
import ch.epfl.lasec.mova.Mova;
import ch.epfl.lasec.mova.MovaPublicKey;
import ch.epfl.lasec.mova.MovaSignature;

/**
 * Class implementing a client for the simulator.
 * 
 * @author Sebastien Duc
 *
 */
public class ClientSimulator {
	
	/**
	 * Socket used to communicate
	 */
	private Socket socket;
	
	/**
	 * Public key used to verify signatures
	 */
	private MovaPublicKey pk;
	
	/**
	 * Mova instance used to verify Mova signatures.
	 */
	private Mova movaInstance;
	
	/**
	 * Creates a Client for the simulator.
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public ClientSimulator() throws UnknownHostException, IOException{
		socket = new Socket("127.0.0.1",ServerSimulator.SERVER_PORT);
	}
	
	public void startClient(){
		InputStream in = null;
		OutputStream out = null;
		try {
			in = socket.getInputStream();
			out = socket.getOutputStream();
			
			receiveMova(in);
			receiveSignedDummyMessage(in,out);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			// close everything
			IOHelper.closeQuietly(in);
			IOHelper.closeQuietly(out);
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void receiveMova(InputStream in) throws IOException{
		this.movaInstance = Mova.read(in);
		System.out.println("Received mova instance = "+ movaInstance);
		byte pkEnc [] = new byte[in.read()];
		in.read(pkEnc);
		this.pk = MovaPublicKey.getKeyFromEncoding(pkEnc);
		System.out.println("Received public key = "+ pk);
	}
	
	private void receiveSignedDummyMessage(InputStream in, OutputStream out) 
			throws IOException{
		// receive dummy message and signature
		byte [] encHello = new byte [in.read()];
		in.read(encHello);
		Message hello = new Message(encHello);
		System.out.println(hello);
		// signature
		byte [] encsHello = new byte [in.read()];
		in.read(encsHello);
		MovaSignature sHello = new MovaSignature(encsHello);
		System.out.println(sHello);
		
		// verify signature --- role verifier
		ConfirmationProtocol conf = new ConfirmationProtocol(in, out, movaInstance);
		boolean b = conf.verifier(pk, hello, sHello,true);
		if (b)
			System.out.println("Signature is valid");
		else
			System.out.println("Verification failed");
		
		DenialProtocol den = new DenialProtocol(in, out, movaInstance);
		b = den.verifier(pk, hello, sHello,true);
		if (b)
			System.out.println("Signature is invalid");
		else
			System.out.println("denial failed");
	}

}
