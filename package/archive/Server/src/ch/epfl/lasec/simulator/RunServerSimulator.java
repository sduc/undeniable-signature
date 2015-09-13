package ch.epfl.lasec.simulator;

import java.io.IOException;

/**
 * Class just used to start the server.
 * 
 * @author Sebastien Duc
 *
 */
public class RunServerSimulator {
	
	public static void main(String[] args) {
		try {
			ServerSimulator server = new ServerSimulator();
			server.startServer();
		} catch (IOException e) {
			System.out.println("Could not start server on port "+ ServerSimulator.SERVER_PORT);
			e.printStackTrace();
		}
	}

}
