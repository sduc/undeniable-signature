package ch.epfl.lasec.universitycontest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import ch.epfl.lasec.IOHelper;

/**
 * 
 * Class used to manage the University Contest Server.
 * Allows the admin to add new challenges, assign new challenges to team.
 * Add new teams. Add new universities. 
 * 
 * @author Sebastien Duc
 *
 */
public class ServerManager {
	
	private DataBaseHandler dbh = new DataBaseHandler();
	
	public static final int TYPE_STD = 0;
	public static final int TYPE_SOL = 1;
	public static final int TYPE_RES = 2;
	
	/**
	 * Return a table where the first column is the university (String) 
	 * and the second is the score (int)
	 * 
	 * @return
	 * @throws NotConnectDBException
	 */
	public Object [][] getUniScoreTable() throws NotConnectDBException{
		dbh.connect();
		Score [] scores = dbh.getUniversityScores();
		Object [][] ret = new Object[scores.length][2];
		for (int i = 0; i < ret.length; i++) {
			ret[i][0] = scores[i].getScoreOf();
			ret[i][1] = scores[i].getScore();
		}
		dbh.disconnect();
		return ret;
	}
	
	/**
	 * Add a university
	 * 
	 * @param uni_id
	 * @throws NotConnectDBException
	 */
	public void addUni(String uni_id) throws NotConnectDBException {
		dbh.connect();
		dbh.addUniversity(uni_id);
		dbh.disconnect();
	}
	
	/**
	 * Get all subscribed teams
	 * @return An array of all team IDs
	 * @throws NotConnectDBException
	 */
	public String [] getTeams() throws NotConnectDBException {
		dbh.connect();
		Integer [] teams = dbh.getTeams();
		dbh.disconnect();
		String [] ret = new String[teams.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = teams[i].toString();
		}
		return ret;
	}
	
	/**
	 * Add a challenge in the DB
	 * 
	 * @param q Quiz to add
	 * @param teamId team associated to the quiz
	 * @throws NotConnectDBException
	 */
	public void addChallenge(Quiz q,int teamId) throws NotConnectDBException {
		dbh.connect();
		dbh.addChallenge(q, teamId);
		dbh.disconnect();
	}
	
	/**
	 * Add a team in the DB
	 * 
	 * @param university university ID
	 * @throws NotConnectDBException
	 */
	public void addTeam(String university,String password) throws NotConnectDBException {
		dbh.connect();
		dbh.addTeam(university,password);
		dbh.disconnect();
	}
	
	/**
	 * Get all the universities that are participating.
	 * @return An array containing the IDs of the participating universities
	 * @throws NotConnectDBException
	 */
	public String[] getUni() throws NotConnectDBException{
		dbh.connect();
		String [] uni = dbh.getUni();
		dbh.disconnect();
		return uni;
	}
	
	/**
	 * Get the score of team with ID teamID
	 * 
	 * @param teamID
	 * @return
	 * @throws NotConnectDBException
	 */
	public int getTeamScore(int teamID) throws NotConnectDBException {
		dbh.connect();
		int s = dbh.getTeamScore(teamID);
		dbh.disconnect();
		return s;
	}
	
	/**
	 * Get the university of team with ID teamID
	 * 
	 * @param teamID ID of the team for which we want to get the university
	 * @return the ID of the university
	 * @throws NotConnectDBException
	 */
	public String getTeamUni(int teamID) throws NotConnectDBException {
		dbh.connect();
		String s = dbh.getTeamUni(teamID);
		dbh.disconnect();
		return s;
	}
	
	/**
	 * Get all available challenges for a team. Available means that they where not used yet,
	 * but just stored on the server.
	 * @param teamID
	 * @return
	 * @throws NotConnectDBException
	 */
	public Integer [] getAvailableChallengeIDs(int teamID) 
			throws NotConnectDBException {
		dbh.connect();
		Integer [] ret = dbh.getAvailableChallengeIDs(teamID);
		dbh.disconnect();
		return ret;
	}
	
	/**
	 * Set the current challenge of team teamId the challenge challengeId
	 * 
	 * @param teamId ID of the team
	 * @param challengeId ID of the challenge
	 * @throws NotConnectDBException
	 */
	public void setCurrentChallenge(int teamId, int challengeId) 
			throws NotConnectDBException{
		dbh.connect();
		dbh.setCurrentChallenge(teamId, challengeId);
		dbh.disconnect();
	}
	
	/**
	 * Set the current challenge of team teamId to no challenge.
	 * 
	 * @param teamId ID of the team
	 * @throws NotConnectDBException
	 */
	public void setCurrentChallengeToNull(int teamId) 
			throws NotConnectDBException{
		dbh.connect();
		dbh.setCurrentChallengeToNull(teamId);
		dbh.disconnect();
	}
	
	/**
	 * Get the received (by the server) challenges of team teamID 
	 * 
	 * @param teamID ID of the team
	 * @return An array of integer containing the received challenge IDs
	 * @throws NotConnectDBException
	 */
	public Integer [] getReceivedChallengeIDs(int teamID) 
			throws NotConnectDBException {
		dbh.connect();
		Integer [] ret = dbh.getReceivedChallengeIDs(teamID);
		dbh.disconnect();
		return ret;
	}
	
	public Integer [] getReceivedAndNotCorrectedChallengeIDs(int teamID) 
			throws NotConnectDBException{
		dbh.connect();
		Integer [] ret = dbh.getReceivedAndNotCorrectedChallengeIDs(teamID);
		dbh.disconnect();
		return ret;
	}
	
	/**
	 * Load the correspond challenge file given TYPE and challengeID
	 * 
	 * @param challengeID ID of the challenge to load
	 * @param TYPE Type can be STD,RES or SOL
	 * @return the load challenge.
	 * @throws IOException 
	 */
	public Quiz loadChallenge(int challengeID, final int TYPE) 
			throws IOException {
		
		File f = null;
		switch (TYPE) {
		case TYPE_STD:
			f = new File(DataBaseHandler.CHALLENGE_DIR + "/" + challengeID);
			break;

		case TYPE_RES:
			f = new File(DataBaseHandler.CHALLENGE_DIR + "/" + 
					challengeID + DataBaseHandler.CHALLENGE_RESULT_FILE_EXT);
			break;

		case TYPE_SOL:
			f = new File(DataBaseHandler.CHALLENGE_DIR + "/" + 
					challengeID + DataBaseHandler.CHALLENGE_SOLUTION_FILE_EXT);
			break;

		default:
			return null;
		}
		
		FileInputStream fis = new FileInputStream(f);
		
		Quiz q = new Quiz(IOHelper.readEncodedObject(fis));
		IOHelper.closeQuietly(fis);
		return q;
	}
	
	/**
	 * Save the challenge score and set in the DB that it was corrected.
	 * @param cs
	 * @throws IOException 
	 * @throws NotConnectDBException 
	 */
	public void saveChallengeScore(ChallengeScore cs) 
			throws IOException, NotConnectDBException{
		int id = cs.getcPointer().getTitle().hashCode();
		File f = new File(DataBaseHandler.CHALLENGE_DIR+"/"+id+
				DataBaseHandler.CHALLENGE_SCORE_FILE_EXT);
		
		FileOutputStream fos = new FileOutputStream(f);
		IOHelper.writeEncodedObject(fos, cs.getEncoded());
		IOHelper.closeQuietly(fos);
		
		dbh.connect();
		dbh.setCorrected(id);
		
		int team = dbh.getAssociatedTeamID(id);
		
		dbh.updateTeamScore(team, cs.getScore());
		dbh.updateUniScore(dbh.getTeamUni(team), cs.getScore());
		dbh.setChallengeScore(id, cs.getScore());
		dbh.disconnect();
	}
	
	public ChallengeScore loadChallengeScore(int challengeID) throws IOException {
		File f = new File(DataBaseHandler.CHALLENGE_DIR+"/"+challengeID
				+DataBaseHandler.CHALLENGE_SCORE_FILE_EXT);
		FileInputStream fis = new FileInputStream(f);
		
		return new ChallengeScore(IOHelper.readEncodedObject(fis));
	}
	
}
