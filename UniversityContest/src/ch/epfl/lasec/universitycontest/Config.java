package ch.epfl.lasec.universitycontest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import ch.epfl.lasec.IOHelper;

import android.content.Context;

/**
 * 
 * Class used to represent the configuration of the application.
 * 
 * @author Sebastien Duc
 *
 */
public class Config {
	
	String myServerIpAddress;
	String advServerIpAddress;
	
	Context _this;
	
	int teamID = Config.NO_TEAM;
	
	String secret = null;
	
	private static final String CONF_FILE_NAME = ".conf";
	public static final int NO_TEAM = -1;
	
	public Config(Context c) {
		this._this = c;
	}
	
	/**
	 * 
	 * Save the current configuration
	 * 
	 * @throws IOException
	 * @throws AddressNotSetException
	 * @throws TeamNotSetException
	 * @throws SecretNotSetException 
	 */
	public void saveConfig() throws IOException, AddressNotSetException,
			TeamNotSetException, SecretNotSetException {
		
		// first check if everything is set
		if (myServerIpAddress == null || advServerIpAddress == null)
			throw new AddressNotSetException();

		if (!hasTeamId())
			throw new TeamNotSetException();
		
		if (secret == null)
			throw new SecretNotSetException();

		FileOutputStream fos = _this.openFileOutput(Config.CONF_FILE_NAME,
				Context.MODE_PRIVATE);

		IOHelper.writeEncodedObject(fos, myServerIpAddress.getBytes());
		IOHelper.writeEncodedObject(fos, advServerIpAddress.getBytes());
		IOHelper.writeEncodedBigInt(fos, BigInteger.valueOf(teamID));
		IOHelper.writeEncodedString(fos, secret);

		IOHelper.closeQuietly(fos);
	}
	
	/**
	 * Load the application config.
	 * @throws IOException
	 */
	public void loadConfig() throws IOException{
		FileInputStream fis = _this.openFileInput(Config.CONF_FILE_NAME);
		
		this.myServerIpAddress = new String(IOHelper.readEncodedObject(fis));
		this.advServerIpAddress = new String(IOHelper.readEncodedObject(fis));
		this.teamID = IOHelper.readEncodedBigInt(fis).intValue();
		this.secret = IOHelper.readEncodedString(fis);
		
		IOHelper.closeQuietly(fis);
	}
	
	/**
	 * Check if the team ID is known
	 * @return true if it is known, false otherwise.
	 */
	public boolean hasTeamId(){
		return teamID != NO_TEAM;
	}
	
	public boolean incomplete(){
		return myServerIpAddress == null || advServerIpAddress == null || !hasTeamId();
	}
	
	/**
	 * Check if the .conf config file exists or not.
	 * @return
	 */
	public boolean configFileExists() {
		try {
			FileInputStream fis = _this.openFileInput(CONF_FILE_NAME);
			IOHelper.closeQuietly(fis);
			return true;
		} catch (FileNotFoundException e) {
			return false;
		}
	}
	
}

class AddressNotSetException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}

class TeamNotSetException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}

class SecretNotSetException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}
