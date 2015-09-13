package ch.epfl.lasec.universitycontest;

import java.io.IOException;

public class RunUniversityContestServer {

	public static void main(String[] args) {
		try {
			UniversityContestServer s = new UniversityContestServer();
			s.startServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
