package ch.epfl.lasec.universitycontest;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * 
 * Activity used to show the user's team score and 
 * the detailed result he did for each challenge.
 * 
 * @author Sebastien Duc
 *
 */
public class TeamScoreActivity extends ListActivity{

	
	public static final String CHALLENGE_NAME ="challenge_name";
	/**
	 * The scores that the team has for each challenge the team did.
	 */
	private Score [] mChallengeScores;
	
	/**
	 * Handle the alert dialog shown to the user when there is an error message.
	 */
	private DialogHandler mDialog = new DialogHandler(this);
	
	/**
	 * Used to start communication with the server of the university of the user
	 */
	private Server advUniveristyServer = new Server(
			Server.ADV_UNIVERSITY_SERVER_IP,
			UniversityContestServer.SERVER_PORT,
			0,null);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.team_score_list_view);
		loadConfig();
		downloadTeamScore();
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
			advUniveristyServer.setIpAddress(c.advServerIpAddress);
			advUniveristyServer.setTeamID(c.teamID);
			advUniveristyServer.setSecret(c.secret);
			
		} catch (IOException e) {
			mDialog.showError("Configuration Error");
		}
	}
	
	/**
	 * Download the score the team did for every 
	 */
	private void downloadTeamScore() {
		try {
			advUniveristyServer.startCommunication();
			this.mChallengeScores = UniversityContestProtocol.askTeamScore(
					advUniveristyServer.getIn(), advUniveristyServer.getOut());
			advUniveristyServer.endCommunication();
		} catch (UnknownHostException e) {
			mDialog.showError(R.string.connection_server_error);
		} catch (IOException e) {
			mDialog.showError(R.string.connection_server_error);
		} catch (InvalidAuthenticationException e) {
			mDialog.showError(R.string.authentication_error);
		}
	}
	
	/**
	 * Populate the GUI using mUniversityScores.
	 */
	private void populateFields() {
		
		if (this.mChallengeScores == null)
			return;
		
		String [] from = new String [] {
				"name",
				"score"};
		
		int [] to = new int [] {
				R.id.challenge_name,
				R.id.challenge_score};
		
		ArrayList<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		
		for (int i = 0; i < this.mChallengeScores.length ; i++) {
			HashMap<String, String> map  = new HashMap<String, String>();
			map.put("name", mChallengeScores[i].getScoreOf());
			map.put("score", ""+mChallengeScores[i].getScore());
			fillMaps.add(map);
		}
		
		setHeader();
		
		this.setListAdapter(new SimpleAdapter(
				this, 
				fillMaps, 
				R.layout.team_score_row_view, 
				from, 
				to)
		);
		
	}
	
	/**
	 * Set the graphical header of the activity
	 * The header contains the cumulated (total) score of the team.
	 */
	private void setHeader() {
		ListView lv = getListView();
		LayoutInflater inflater = getLayoutInflater();
		LinearLayout header = (LinearLayout) inflater.inflate(
				R.layout.team_score_header_view, lv, false);
		TextView totalScoreTextView = (TextView) header
				.findViewById(R.id.total_score_header_text_view);
		totalScoreTextView.setText(""+computeTotal());
		lv.addHeaderView(header,null,false);
	}
	
	/**
	 * Compute the total score of the team.
	 * 
	 * @return the total score of the team
	 */
	private int computeTotal(){
		int total = 0;
		if(mChallengeScores != null && mChallengeScores.length > 0) {
			for (Score s : mChallengeScores) {
				total += s.getScore();
			}
		}
		return total;
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Intent i = new Intent(this,ChallengeResultActivity.class);
		i.putExtra(CHALLENGE_NAME, mChallengeScores[position-1].getScoreOf());
		
		startActivity(i);
	}
}
