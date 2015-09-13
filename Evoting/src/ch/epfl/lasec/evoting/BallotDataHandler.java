package ch.epfl.lasec.evoting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;

import android.content.Context;
import ch.epfl.lasec.IOHelper;

/**
 * Class to handle the storage of the ballots. It allows to save/load them.
 * When created it takes the context of the application.
 * 
 * @author Sebastien Duc
 *
 */
public class BallotDataHandler {
	
	
	/**
	 * Context of the application calling the BallotDataHandler.
	 */
	private final Context c;
	
	/**
	 * Constructor for BallotDataHandler.
	 * 
	 * @param c The context of the application
	 */
	public BallotDataHandler(Context c){
		this.c = c;
	}

	/**
	 * Load from the phone the ballot with id ballotID
	 * 
	 * @param ballotID The id of the ballot
	 * @return The ballot with id ballotID
	 * @throws BallotIDNotFoundException Exception thrown when the ballotID is not found on the phone
	 * @throws UnexpectedBallotLoadException Exception thrown when something unexpected happens while loading Ballot
	 */
	public Ballot loadBallot(Long ballotID) throws BallotIDNotFoundException, UnexpectedBallotLoadException{
		FileInputStream fis = null;
		BufferedReader br = null;
		Ballot b = null;
		try {
			fis  = c.openFileInput(ballotID.toString());
			br = new BufferedReader(new InputStreamReader(fis));
			b = Ballot.deserialize(br);

		} catch (FileNotFoundException e) {
			throw new BallotIDNotFoundException();
		} catch (IOException e) {
			throw new UnexpectedBallotLoadException();
		} catch (ClassNotFoundException e) {
			throw new UnexpectedBallotLoadException();
		} 
		finally {
			IOHelper.closeQuietly(br);
			IOHelper.closeQuietly(fis);
		}

		return b;
	}

	/**
	 * Save the ballot on the phone
	 * 
	 * @param b Ballot to save on the phone
	 * @throws UnexpectedBallotLoadException Exception thrown when something unexpected happens while saving Ballot
	 */
	public void saveBallot(Ballot b) throws UnexpectedBallotLoadException{
		FileOutputStream fos = null;
		BufferedWriter bw = null;

		try {
			
			fos = c.openFileOutput(b.getBallotID().toString(),Context.MODE_PRIVATE);
			bw = new BufferedWriter(new OutputStreamWriter(fos));
			b.serialize(bw);
			
		} catch (FileNotFoundException e) {
			//should not happen
			assert false;
		} catch (IOException e) {
			throw new UnexpectedBallotLoadException();
		} 
		finally {
			IOHelper.closeQuietly(bw);
			IOHelper.closeQuietly(fos);
		}


	}
	
	public void deleteBallot(Ballot b){
		c.deleteFile(b.getBallotID().toString());
	}
}

/**
 * Exception thrown when a ballot id is not found.
 * 
 * @author Sebastien Duc
 *
 */
class BallotIDNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9053657964649375564L;




}


/**
 * Exception thrown when something unexpected happens while loading or saving ballot.
 * 
 * @author Sebastien Duc
 *
 */
class UnexpectedBallotLoadException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -938503045853791528L;

}
