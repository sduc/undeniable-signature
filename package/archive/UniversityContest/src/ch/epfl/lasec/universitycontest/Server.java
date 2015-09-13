package ch.epfl.lasec.universitycontest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 
 * Class used by the client to represent a server to which the client
 * may connect to.
 * 
 * @author Sebastien Duc
 *
 */
public class Server {
	
	public static final String ADV_UNIVERSITY_SERVER_IP = "128.178.236.65";
	
	public static final String MY_UNIVERSITY_IP = "128.178.236.65";

	private String ipAddress;
	private int port;
	private Socket s = null;
	private int teamID;
	private String secret;
	
	public Server(String ip, int port, int teamID, String secret) {
		this.ipAddress = ip;
		this.port = port;
		this.teamID = teamID;
		this.secret = secret;
	}
	
	public int getId() {
		return ipAddress.hashCode() + port;
	}
	
	/**
	 * Start the communication with the server.
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws InvalidAuthenticationException 
	 */
	public void startCommunication() throws UnknownHostException, IOException,
			InvalidAuthenticationException {
		s = new Socket(ipAddress, port);
		boolean success = UniversityContestProtocol.teamAuthentication(getIn(),
				getOut(), this.teamID, this.secret);
		if (!success) {
			s.close();
			throw new InvalidAuthenticationException();
		}
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
	
	
	/**
	 * End the communication with the server
	 * @throws IOException
	 */
	public void endCommunication() throws IOException {
		if (s == null)
			return;
		UniversityContestProtocol.endCommunication(this.getOut());
		s.close();
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setTeamID(int teamID) {
		this.teamID = teamID;
	}
	
	public void setSecret(String secret){
		this.secret = secret;
	}
	
	
	
}
