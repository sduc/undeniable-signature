package ch.epfl.lasec.universitycontest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import ch.epfl.lasec.mova.Mova;
import ch.epfl.lasec.mova.MovaPublicKey;

public class UniversityContestClientDebugger {

public static final String ADV_UNIVERSITY_SERVER_IP = "127.0.0.1";
	
	// TODO set ip address dynamically
	/**
	 * Server of "MY" university. Used to sign the filled quizzes.
	 */
	private Server myUniversityServer = new Server(
			"",
			UniversityContestServer.SERVER_PORT);
	
	// TODO set ip address dynamically
	/**
	 * Server of the adversary (university) in the contest. This server provides the quizzes.
	 */
	private Server adversaryUniversityServer = new Server(
			ADV_UNIVERSITY_SERVER_IP,
			UniversityContestServer.SERVER_PORT);

	/**
	 * The current challenge to solve
	 */
	private Challenge currentChallenge;
	
	private MovaPublicKey mpk;
	
	private Mova mova;
	
	/**
	 * If the phone doesn't have the mova instance/key of the servers yet. 
	 * Get the keys from them.
	 */
	private void getMova() {

		try {
			adversaryUniversityServer.startCommunication();
			// first get and save key
			this.mpk = UniversityContestProtocol.askMovaPublicKey(
					adversaryUniversityServer.getIn(),
					adversaryUniversityServer.getOut());
			

			// then get and save mova instance
			this.mova = UniversityContestProtocol.askMovaInstance(
					adversaryUniversityServer.getIn(),
					adversaryUniversityServer.getOut());


			adversaryUniversityServer.endCommunication();

		} catch (IOException e) {
			// TODO
		}

	}
	
	/**
	 * Download if there is the latest challenge from the server
	 */
	private void downloadChallenge(){
		// download if there is, the last challenge from the server.
		try {
			adversaryUniversityServer.startCommunication();
			
			// get the challenge and verify the signature.
			this.currentChallenge = UniversityContestProtocol.
					askNewChallenge(adversaryUniversityServer.getIn(), 
							        adversaryUniversityServer.getOut(),
							        mova,
							        mpk);
			System.out.println("Signature valid");
		
		} catch (UnknownHostException e1) {
			//TODO
			
		} catch (IOException e1) {
			//TODO
			
		} catch (InvalidSignatureException e) {
			System.out.println("Signature invalid");
		} finally {
			try {
				adversaryUniversityServer.endCommunication();
			} catch (IOException e) {
				//TODO
			}
		}
		
	}
	
	/**
	 * 
	 * Class used by the client to represent a server to which the client
	 * may connect to.
	 * 
	 * @author Sebastien Duc
	 *
	 */
	class Server {

		private String ipAddress;
		private int port;
		private Socket s = null;
		
		public Server(String ip, int port) {
			this.ipAddress = ip;
			this.port = port;
		}
		
		public int getId() {
			return ipAddress.hashCode() + port;
		}
		
		public void startCommunication() throws UnknownHostException, IOException {
			s = new Socket(ipAddress,port);
		}
		
		/**
		 * Get the input stream of the communication with this server. If not started return null.
		 * @return The input stream if the communication with this server was started. null otherwise.
		 * @throws IOException
		 */
		public InputStream getIn() throws IOException {
			if (s == null)
				return null;
			return s.getInputStream();
		}
		
		/**
		 * Get the output stream of the communication with this server. If not started return null.
		 * @return The output stream if the communication with this server was started. null otherwise.
		 * @throws IOException
		 */
		public OutputStream getOut() throws IOException {
			if (s == null)
				return null;
			return s.getOutputStream();
		}
		
		public void endCommunication() throws IOException {
			if (s == null)
				return;
			UniversityContestProtocol.endCommunication(this.getOut());
			s.close();
		}
		
	}
	
	public static void main(String[] args) {
		UniversityContestClientDebugger u = new UniversityContestClientDebugger();
		u.getMova();
		u.downloadChallenge();
	}
	
	
}
