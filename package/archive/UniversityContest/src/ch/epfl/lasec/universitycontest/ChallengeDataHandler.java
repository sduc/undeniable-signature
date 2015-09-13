package ch.epfl.lasec.universitycontest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ch.epfl.lasec.IOHelper;
import android.content.Context;
import android.util.Log;

public class ChallengeDataHandler {

	/**
	 * Context of the application calling the ChallengeDataHandler.
	 */
	private final Context c;
	
	private final static String CHALLENGE_SAVE_NAME = "current_challenge";
	
	private boolean deleted;
	
	/**
	 * Constructor for ChallengeDataHandler.
	 * 
	 * @param c The context of the application
	 */
	public ChallengeDataHandler(Context c){
		this.c = c;
		this.deleted = false;
	}
	
	/**
	 * Load the challenge if it is save in the phone.
	 * @return the challenge that was read
	 * @throws UnexpectedChallengeLoadException
	 * @throws FileNotFoundException
	 */
	public Challenge loadChallenge() throws UnexpectedChallengeLoadException, FileNotFoundException {
		FileInputStream fis = c.openFileInput(CHALLENGE_SAVE_NAME);
		Challenge ch = null;
		try {
			byte [] challengeEnc = IOHelper.readEncodedObject(fis);
			ch = Challenge.fromEncoded(challengeEnc);
		} catch (IOException e) {
			throw new UnexpectedChallengeLoadException();
		} finally {
			IOHelper.closeQuietly(fis);
		}
		return ch;
	}
	
	/**
	 * Save challenge ch on the phone.
	 * @param ch Challenge to save
	 * @throws UnexpectedChallengeLoadException 
	 */
	public void saveChallenge(Challenge ch) throws UnexpectedChallengeLoadException {
		if (this.deleted)
			return;
		
		FileOutputStream fos = null;
		try {
			fos = c.openFileOutput(CHALLENGE_SAVE_NAME, Context.MODE_PRIVATE);
			byte [] enc = ch.getEncoded();
			IOHelper.writeEncodedObject(fos, enc);
		} catch (FileNotFoundException e) {
			//should not happen
			assert false;
		} catch (IOException e) {
			throw new UnexpectedChallengeLoadException();
		}
	}
	
	/**
	 * Delete the save of the current challenge
	 */
	public void deleteCurrentSavedChallenge(){
		boolean test = c.deleteFile(CHALLENGE_SAVE_NAME);
		Log.v("info", "the file was deleted: "+test);
		this.deleted = true;
	}
	
}

class UnexpectedChallengeLoadException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5683362005951823136L;
	
	
}
