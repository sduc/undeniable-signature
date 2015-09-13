import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;


public class Toto {
	
	public static void main(String[] args) {
		try {
			Socket s = new Socket("192.168.1.3", 12345);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
