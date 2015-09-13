package ch.epfl.lasec.universitycontest;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.SimpleAdapter;

/**
 * 
 * Activity used to show the score of all participating universities.
 * It just show the in a list. 
 * 
 * @author Sebastien Duc
 *
 */
public class UniversityScoreActivity extends ListActivity{
	
	/**
	 * The scores of the universities
	 */
	private Score [] mUniversityScores;
	
	/**
	 * Handle the alert dialog shown to the user when there is an error message.
	 */
	private DialogHandler mDialog = new DialogHandler(this);
	
	/**
	 * Used to start communication with the server of the university of the user
	 */
	private Server myUniveristyServer = new Server(
			Server.MY_UNIVERSITY_IP,
			UniversityContestServer.SERVER_PORT,
			0,null);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.university_score_list_activity_view);
		
		loadConfig();
		downloadUniversityScores();
		populateFields();
	}
	
	/**
	 * Load the config of the application from .conf to know the ip addr of both servers
	 * and the team ID. It's the first thing to do.
	 */
	private void loadConfig() {
		Config c = new Config(this);
		try {
			c.loadConfig();
			myUniveristyServer.setIpAddress(c.myServerIpAddress);
			myUniveristyServer.setTeamID(c.teamID);
			myUniveristyServer.setSecret(c.secret);
			
		} catch (IOException e) {
			mDialog.showError("Configuration Error");
		}
	}
	
	/**
	 * Download the scores and assign it to mUniversityScore.
	 */
	private void downloadUniversityScores() {
		
		try {
			myUniveristyServer.startCommunication();
			
			this.mUniversityScores = UniversityContestProtocol
					.askUniversityScore(myUniveristyServer.getIn(),
							myUniveristyServer.getOut());
			
		} catch (UnknownHostException e) {
			mDialog.showError(getString(R.string.connection_server_error));
		} catch (IOException e) {
			mDialog.showError(getString(R.string.connection_server_error));
		} catch (InvalidAuthenticationException e) {
			mDialog.showError(R.string.authentication_error);
		} finally {
			try {
				myUniveristyServer.endCommunication();
			} catch (IOException e) {
				mDialog.showError("Critical error!");
			}
		}
	}
	
	/**
	 * Populate the GUI using mUniversityScores.
	 */
	private void populateFields() {
		
		if (this.mUniversityScores == null)
			return;
		
		String [] from = new String [] {
				"rank",
				"name",
				"score"};
		
		int [] to = new int [] {
				R.id.university_rank,
				R.id.university_name,
				R.id.university_score};
		
		// note that the university are assumed to be sorted in the decreasing order of 
		// the score. Therefore the ranking of the university is simply it's index in the array
		// mUniversityScores + 1.
		ArrayList<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> header = new HashMap<String, String>();
		header.put("rank", "Rank");
		header.put("name", "University");
		header.put("score", "Score");
		fillMaps.add(header);
		
		for (int i = 0; i < this.mUniversityScores.length ; i++) {
			HashMap<String, String> map  = new HashMap<String, String>();
			map.put("rank", ""+(int)(i+1));
			map.put("name", mUniversityScores[i].getScoreOf());
			map.put("score", ""+mUniversityScores[i].getScore());
			fillMaps.add(map);
		}
		
		this.setListAdapter(new SimpleAdapter(
				this, 
				fillMaps, 
				R.layout.university_row_view, 
				from, 
				to)
		);
	}
	
	
}
