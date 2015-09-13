package ch.epfl.lasec.simulator;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Start a client simulator
 * 
 * @author Sebastien Duc
 *
 */
public class RunClientSimulator {

	public static void main(String[] args) {
		try {
			ClientSimulator client = new ClientSimulator();
			client.startClient();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
