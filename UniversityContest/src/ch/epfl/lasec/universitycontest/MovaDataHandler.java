package ch.epfl.lasec.universitycontest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ch.epfl.lasec.IOHelper;
import ch.epfl.lasec.mova.Mova;
import ch.epfl.lasec.mova.MovaPublicKey;

import android.content.Context;

/**
 * 
 * Class to handle to storage of the mova key of the server 
 * 
 * @author Sebastien Duc
 *
 */
public class MovaDataHandler {

	/**
	 * Context of the application calling the ChallengeDataHandler.
	 */
	private final Context c;
	
	private final static String MOVA_SAVE_NAME = "mova";
	
	public MovaDataHandler(Context c){
		this.c = c;
	}
	
	/**
	 * 
	 * Load the mova public key associated with server serverId from the phone.
	 * 
	 * @param serverId Id of the server from which the public key  has to be loaded
	 * @return the loaded mova public key
	 * @throws IOException
	 */
	public MovaPublicKey loadMPK(int serverId) throws IOException{
		FileInputStream fis = c.openFileInput(MOVA_SAVE_NAME+serverId+".mpk");
		byte [] encMPK = IOHelper.readEncodedObject(fis);
		IOHelper.closeQuietly(fis);
		return MovaPublicKey.getKeyFromEncoding(encMPK);
	}
	
	
	/**
	 * Save the mova public key of server serverId on the phone.
	 * 
	 * @param mpk the mova public key to save
	 * @param serverId the id of the server to which mpk belongs
	 * @throws IOException
	 */
	public void saveMPK(MovaPublicKey mpk, int serverId) throws IOException {

		try {
			FileOutputStream fos = c.openFileOutput(MOVA_SAVE_NAME + serverId + ".mpk",
					Context.MODE_PRIVATE);
			byte [] mpkEnc = mpk.getEncoded();
			IOHelper.writeEncodedObject(fos, mpkEnc);
			IOHelper.closeQuietly(fos);
		} catch (FileNotFoundException e) {
			assert false; // should never happen
		} 
	}
	
	/**
	 * 
	 * Load the mova instance of the server serverId from the phone.
	 * 
	 * @param serverId Id of the server 
	 * @return the mova instance of server serverId that was previously save on the phone.
	 * @throws IOException
	 */
	public Mova loadMova(int serverId) throws IOException {
		FileInputStream fis = c.openFileInput(MOVA_SAVE_NAME+serverId+".mova");
		Mova m = Mova.read(fis);
		IOHelper.closeQuietly(fis);
		return m;
	}
	
	/**
	 * 
	 * Save the mova instance of the server serverId on the phone.
	 * 
	 * @param m mova instance to save on the phone.
	 * @param serverId Id of the server to which Mova m is associated.
	 * @throws IOException
	 */
	public void saveMova(Mova m, int serverId) throws IOException {
		FileOutputStream fos = c.openFileOutput(MOVA_SAVE_NAME+serverId+".mova", 
				Context.MODE_PRIVATE);
		m.write(fos);
		IOHelper.closeQuietly(fos);
	}
	
	public boolean hasKey(int serverId) {
		return c.getFileStreamPath(MOVA_SAVE_NAME+serverId+".mpk").exists();
	}
	
}
