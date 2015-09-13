package ch.epfl.lasec;

import java.util.ArrayList;

public class LogContainer {
	
	private ArrayList<String> logs = new ArrayList<String>();
	
	public void addLogMessage(String message) {
		logs.add(message);
	}

	@Override
	public String toString() {
		String repr = "LOG:\n";
		for (String log : logs) {
			repr += log+"\n";
		}
		return repr;
	}
	
}
