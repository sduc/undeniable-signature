package ch.epfl.lasec.universitycontest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import ch.epfl.lasec.IOHelper;

/**
 * 
 * MySQL database handler for university contest
 * 
 * @author Sebastien Duc
 *
 */
public class DataBaseHandler {
	
	// some definitions for the database
	public static final String DATABASE_NAME = "unicontest";
	
	public static final String TEAM_TABLE = "team";
	public static final String UNIVERSITY_TABLE = "university";
	public static final String CHALLENGE_TABLE = "challenge";
	
	/**
	 * file extension for the received, filled-in challenges
	 */
	public static final String CHALLENGE_RESULT_FILE_EXT = ".res";
	
	/**
	 * file extension for the solution the challenges 
	 */
	public static final String CHALLENGE_SOLUTION_FILE_EXT = ".sol";
	
	public static final String CHALLENGE_SCORE_FILE_EXT = ".sc";
	
	public static final String CHALLENGE_DIR = "challenges";

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/"+DATABASE_NAME;

	// Database credentials
	static final String USER = "java";
	static final String PASS = "akfss19uT";
	
	private Connection dBConnection = null;
	
	/**
	 * Connect to the database
	 * @throws NotConnectDBException 
	 */
	public void connect() throws NotConnectDBException{
		try {
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			System.out.println("Connecting to database...");
			dBConnection = DriverManager.getConnection(DB_URL, USER, PASS);

		} catch (SQLException se) {
			throw new NotConnectDBException();
		} catch (Exception e) {
			throw new NotConnectDBException();
		}
	}
	
	/**
	 * Close the connection with the database
	 * @throws NotConnectDBException 
	 */
	public void disconnect() throws NotConnectDBException {
		try {
			if (dBConnection != null)
				dBConnection.close();
		} catch (SQLException se) {
			throw new NotConnectDBException();
		}
		dBConnection = null;
		System.out.println("Disconnecting from the Database");
	}
	
	/**
	 * Add a university in the DB with initial score 0.
	 * @param uni_id ID of the university (for example EPFL)
	 * @throws NotConnectDBException 
	 */
	public void addUniversity(String uni_id) throws NotConnectDBException {
		if (dBConnection == null)
			throw new NotConnectDBException();
		
		Statement stmt = null;
		try {
			stmt = dBConnection.createStatement();
			stmt.execute("INSERT INTO " + UNIVERSITY_TABLE
					+ " (university_id) VALUES(\"" + uni_id + "\");");
		} catch (SQLException e) {
			throw new NotConnectDBException();
		} finally {

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * Add in the DB the following challenge associated with team teamID 
	 * and save the challenge.
	 * @param c Challenge to add in the DB
	 * @param teamID id of the team associated with challenge c to be added.
	 * @throws NotConnectDBException 
	 */
	public void addChallenge(Challenge c,int teamId) throws NotConnectDBException {
		
		if (dBConnection == null)
			throw new NotConnectDBException();
			
			
		int challengeId = c.getTitle().hashCode();
		// if the challenge id is already used
		while (new File(CHALLENGE_DIR+"/"+challengeId).exists() || challengeId == 0){
			challengeId ++;
			System.err.println("Warning challenge ID not stable. " +
					"Change the title of the challenge!");
		}
		
		// save challenge in the challenge dir
		File cFile = new File(CHALLENGE_DIR+"/"+challengeId+CHALLENGE_SOLUTION_FILE_EXT);
		File cFile2 = new File(CHALLENGE_DIR+"/"+challengeId);
		FileOutputStream fos = null;
		FileOutputStream fos2 = null;
		try {
			
			fos = new FileOutputStream(cFile);
			fos2 = new FileOutputStream(cFile2);
			IOHelper.writeEncodedObject(fos, c.getEncoded());
			IOHelper.writeEncodedObject(fos2, ((Quiz)c).getNoSolution().getEncoded());
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOHelper.closeQuietly(fos);
		}
		
		addChallengeInDB(challengeId, teamId, c.getTitle());
			
	}
	
	/**
	 * Add in the DB only, called by addChallenge
	 * @param challengeId
	 * @param teamId
	 * @throws NotConnectDBException 
	 */
	private void addChallengeInDB(int challengeId, int teamId, String challenge_name) 
			throws NotConnectDBException {
		Statement stmt = null;
		try {
			stmt = this.dBConnection.createStatement();
			String sql = "INSERT INTO " + CHALLENGE_TABLE
					+ " (challenge_id,team_id,challenge_name)" + " VALUES(" + challengeId
					+ "," + teamId + "," + "\""+ challenge_name + "\"" + ");";
			stmt.execute(sql);
		} catch (SQLException e) {
			throw new NotConnectDBException();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Add a team in the DB that is in university university.
	 * @param university University of the team to create.
	 * @throws NotConnectDBException 
	 */
	public void addTeam(String university,String psswd) throws NotConnectDBException{
		
		if (dBConnection == null)
			throw new NotConnectDBException();
		
		Statement stmt = null;
		try {
			stmt = dBConnection.createStatement();
			stmt.execute("INSERT INTO " + TEAM_TABLE 
					+ " (university,secret) VALUES(\""+university+"\",\""+psswd+"\");");
		} catch (SQLException e) {
			throw new NotConnectDBException();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Get the current challenge for the team with ID teamId
	 * @param teamId
	 * @return
	 * @throws NotConnectDBException 
	 */
	public Challenge getCurrentChallenge(int teamId) throws NotConnectDBException{
		if(dBConnection == null)
			throw new NotConnectDBException();
		
		// First get the id of the current challenge from the database.
		Statement stmt = null;
		int currentChallengeId = 0;
		try {
			stmt = dBConnection.createStatement();
			String sql = "SELECT current_challenge FROM team WHERE team_id ="+teamId+";";
			ResultSet rs = stmt.executeQuery(sql);
			
			if(rs.next())
				currentChallengeId = rs.getInt(1);
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new NotConnectDBException();
		} finally {
			if (stmt != null){
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (currentChallengeId == 0)
			return null;
		
		// read the challenge in the correct file
		FileInputStream fis = null;
		Challenge cChallenge = null;
		try {
			fis = new FileInputStream(new File(getChallengePath(currentChallengeId)));
			cChallenge = Challenge.fromEncoded(IOHelper.readEncodedObject(fis));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOHelper.closeQuietly(fis);
		}
		
		return cChallenge;
	}
	
	/**
	 * Get the score of all universities.
	 * @return the score of the universities
	 * @throws NotConnectDBException 
	 */
	public Score [] getUniversityScores() throws NotConnectDBException{
		if(dBConnection == null)
			throw new NotConnectDBException();
		
		Statement stmt = null;
		ArrayList<Score> uScores = new ArrayList<Score>();
		
		try {
			stmt = dBConnection.createStatement();
			String sql = "SELECT university_id,score FROM university ORDER BY score DESC;";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				uScores.add(new Score(rs.getString(1),rs.getInt(2)));
			}
		} catch (SQLException e) {
			throw new NotConnectDBException();
		} finally {
			if (stmt != null){
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		// convert the ArrayList to an array
		Score [] res = new Score[uScores.size()];
		uScores.toArray(res);
		return res;
	}
	
	public String [] getUni() throws NotConnectDBException{
		if(dBConnection == null)
			throw new NotConnectDBException();
		
		Statement stmt = null;
		ArrayList<String> uni = new ArrayList<String>();
		
		try {
			stmt = dBConnection.createStatement();
			String sql = "SELECT university_id FROM university ORDER BY university_id DESC;";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				uni.add(rs.getString(1));
			}
		} catch (SQLException e) {
			throw new NotConnectDBException();
		} finally {
			if (stmt != null){
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		// convert the ArrayList to an array
		String [] res = new String[uni.size()];
		uni.toArray(res);
		return res;
	}
	
	/**
	 * Set the current challenge of team with ID teamId to challenge with ID
	 * challengeId
	 * @param teamId the ID of the team
	 * @param challengeId The ID of the challenge
	 * @throws NotConnectDBException 
	 */
	public void setCurrentChallenge(int teamId,int challengeId) 
			throws NotConnectDBException {
		if (dBConnection == null)
			throw new NotConnectDBException();
		
		Statement stmt = null;
		try {
			stmt = dBConnection.createStatement();
			String sql = "UPDATE " + TEAM_TABLE 
					+ " SET current_challenge="+challengeId
					+ " WHERE team_id="+teamId;
			stmt.execute(sql);
		} catch (SQLException e) {
			throw new NotConnectDBException();
		} finally {
			if (stmt != null){
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Set the current challenge of team with ID teamId to NULL
	 * @param teamId
	 * @throws NotConnectDBException
	 */
	public void setCurrentChallengeToNull(int teamId) throws NotConnectDBException{
		if (dBConnection == null)
			throw new NotConnectDBException();
		
		Statement stmt = null;
		try {
			stmt = dBConnection.createStatement();
			String sql = "UPDATE " + TEAM_TABLE 
					+ " SET current_challenge=NULL"
					+ " WHERE team_id="+teamId;
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new NotConnectDBException();
		} finally {
			if (stmt != null){
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Update the score of theam with ID teamId by incrementing it of scoreIncr
	 * @param teamId
	 * @param scoreIncr
	 * @throws NotConnectDBException
	 */
	public void updateTeamScore(int teamId, int scoreIncr) throws NotConnectDBException {
		if (dBConnection == null)
			throw new NotConnectDBException();
		
		int newScore = getTeamScore(teamId) + scoreIncr;
		Statement stmt = null;
		try {
			stmt = dBConnection.createStatement();
			String sql = "UPDATE " + TEAM_TABLE 
					+ " SET score="+newScore 
					+ " WHERE team_id="+teamId;
			stmt.execute(sql);
		} catch (SQLException e) {
			throw new NotConnectDBException();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void setChallengeScore(int challengeID, int score) throws NotConnectDBException {
		if (dBConnection == null)
			throw new NotConnectDBException();
		
		Statement stmt = null;
		try {
			stmt = dBConnection.createStatement();
			String sql = "UPDATE "+ CHALLENGE_TABLE 
					+ " SET score="+ score +" WHERE challenge_id="+challengeID;
			stmt.execute(sql);
		} catch (SQLException e) {
			throw new NotConnectDBException();
		} finally {
			if (stmt != null){
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Update the score of theam with ID teamId by incrementing it of scoreIncr
	 * @param teamId
	 * @param scoreIncr
	 * @throws NotConnectDBException
	 */
	public void updateUniScore(String uni, int scoreIncr) throws NotConnectDBException {
		if (dBConnection == null)
			throw new NotConnectDBException();
		
		int newScore = getUniScore(uni) + scoreIncr;
		Statement stmt = null;
		try {
			stmt = dBConnection.createStatement();
			String sql = "UPDATE " + UNIVERSITY_TABLE 
					+ " SET score="+newScore 
					+ " WHERE university_id="+"\""+uni+"\"";
			stmt.execute(sql);
		} catch (SQLException e) {
			throw new NotConnectDBException();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public int getUniScore(String uni) throws NotConnectDBException {
		if (dBConnection == null)
			throw new NotConnectDBException();
		
		Statement stmt = null;
		int score = -1;
		try {
			stmt = dBConnection.createStatement();
			String sql = "SELECT score FROM "+ UNIVERSITY_TABLE +
					" WHERE university_id="+"\""+uni+"\"";
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()){
				score = rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new NotConnectDBException();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return score;
	}
	
	/**
	 * Get score of team teamId
	 * @param teamId
	 * @return
	 * @throws NotConnectDBException 
	 */
	public int getTeamScore(int teamId) throws NotConnectDBException {
		if (dBConnection == null)
			throw new NotConnectDBException();
		
		Statement stmt = null;
		int score = -1;
		try {
			stmt = dBConnection.createStatement();
			String sql = "SELECT score FROM "+ TEAM_TABLE +
					" WHERE team_id="+teamId;
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()){
				score = rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new NotConnectDBException();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return score;
	}
	
	/**
	 * Get the scores the team did for each quiz the team sent
	 * @param teamID
	 * @return
	 * @throws NotConnectDBException 
	 */
	public Score [] getTeamScores(int teamID) throws NotConnectDBException{
		if (dBConnection == null)
			throw new NotConnectDBException();
		
		Statement stmt = null;
		ArrayList<Score> scores = new ArrayList<Score>();
		try {
			stmt = dBConnection.createStatement();
			String sql = "SELECT challenge_name,score FROM "+ CHALLENGE_TABLE +
					" WHERE team_id="+teamID+" AND corrected=true";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				scores.add(new Score(rs.getString(1),rs.getInt(2)));
			}
		} catch (SQLException e) {
			throw new NotConnectDBException();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		Score [] ret = new Score[scores.size()];
		scores.toArray(ret);
		
		return ret;
	}
	
	/**
	 * Get the university of team teamId
	 * 
	 * @param teamId ID of the team.
	 * @return the universityId of teamId
	 * @throws NotConnectDBException
	 */
	public String getTeamUni(int teamId) throws NotConnectDBException{
		if (dBConnection == null)
			throw new NotConnectDBException();
		
		Statement stmt = null;
		String uni = null;
		try {
			stmt = dBConnection.createStatement();
			String sql = "SELECT university FROM "+ TEAM_TABLE +
					" WHERE team_id="+teamId;
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()){
				uni = rs.getString(1);
			}
		} catch (SQLException e) {
			throw new NotConnectDBException();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return uni;
	}
	
	/**
	 * Get all the team IDs
	 * @return the IDs of all teams
	 * @throws NotConnectDBException
	 */
	public Integer [] getTeams() throws NotConnectDBException{
		if (dBConnection == null)
			throw new NotConnectDBException();
		
		ArrayList<Integer> teams = new ArrayList<Integer>();
		Statement stmt = null;
		
		try {
			
			stmt = dBConnection.createStatement();
			String sql = "SELECT team_id FROM " + TEAM_TABLE;
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				teams.add(rs.getInt(1));
			}
			
		} catch (SQLException e) {
			throw new NotConnectDBException();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		Integer [] ret = new Integer[teams.size()];
		teams.toArray(ret);
		return ret;
		
	}
	
	/**
	 * 
	 * Get the available challenge IDs assigned to teamID.
	 * Available means that they where still not provided to members of teamID.
	 * 
	 * @param teamID
	 * @return
	 * @throws NotConnectDBException 
	 */
	public Integer [] getAvailableChallengeIDs(int teamID) throws NotConnectDBException {
		if (dBConnection == null)
			throw new NotConnectDBException();
		
		ArrayList<Integer> challenges = new ArrayList<Integer>();
		Statement stmt = null;
		
		try {
			
			stmt = dBConnection.createStatement();
			String sql = "SELECT challenge_id FROM " + CHALLENGE_TABLE +
					" WHERE team_id="+teamID+" AND received=false";
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				challenges.add(rs.getInt(1));
			}
			
		} catch (SQLException e) {
			throw new NotConnectDBException();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		Integer [] ret = new Integer[challenges.size()];
		challenges.toArray(ret);
		return ret;
	}
	
	/**
	 * 
	 * Set the challenge with ID challengeId
	 * to received = true
	 * 
	 * @param challengeId
	 * @param teamId
	 * @throws NotConnectDBException 
	 */
	public void setReceived(int challengeId) throws NotConnectDBException {
		if (dBConnection == null)
			throw new NotConnectDBException();
		
		Statement stmt = null;
		try {
			stmt = dBConnection.createStatement();
			String sql = "UPDATE "+ CHALLENGE_TABLE 
					+ " SET received=true WHERE challenge_id="+challengeId;
			stmt.execute(sql);
		} catch (SQLException e) {
			throw new NotConnectDBException();
		} finally {
			if (stmt != null){
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void setCorrected(int challengeId) throws NotConnectDBException {
		if (dBConnection == null)
			throw new NotConnectDBException();
		
		Statement stmt = null;
		try {
			stmt = dBConnection.createStatement();
			String sql = "UPDATE "+ CHALLENGE_TABLE 
					+ " SET corrected=true WHERE challenge_id="+challengeId;
			stmt.execute(sql);
		} catch (SQLException e) {
			throw new NotConnectDBException();
		} finally {
			if (stmt != null){
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void addReceivedChallenge(Challenge c, int teamID) throws NotConnectDBException{
		
		if (dBConnection == null)
			throw new NotConnectDBException();
			
			
		int challengeId = c.getTitle().hashCode();
		
		// save challenge in the challenge dir
		File cFile = new File(CHALLENGE_DIR+"/"+challengeId+CHALLENGE_RESULT_FILE_EXT);
		FileOutputStream fos = null;
		try {
			
			fos = new FileOutputStream(cFile);
			IOHelper.writeEncodedObject(fos, c.getEncoded());
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOHelper.closeQuietly(fos);
		}
		
		setReceived(challengeId);
		
		setCurrentChallengeToNull(teamID);
		
	}

	/**
	 * Get all challenges that where received (filled-in by the client and sent to the server).
	 * 
	 * @param teamID ID of the team for which we want to get the received challenge IDs
	 * @return An array of integers containing the IDs of the received challenges
	 * @throws NotConnectDBException
	 */
	public Integer [] getReceivedChallengeIDs(int teamID) throws NotConnectDBException {
		if (dBConnection == null)
			throw new NotConnectDBException();
		
		ArrayList<Integer> challenges = new ArrayList<Integer>();
		Statement stmt = null;
		
		try {
			
			stmt = dBConnection.createStatement();
			String sql = "SELECT challenge_id FROM " + CHALLENGE_TABLE +
					" WHERE team_id="+teamID+" AND received=true";
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				challenges.add(rs.getInt(1));
			}
			
		} catch (SQLException e) {
			throw new NotConnectDBException();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		Integer [] ret = new Integer[challenges.size()];
		challenges.toArray(ret);
		return ret;
	}
	
	/**
	 * Get all challenges that where received (filled-in by the client and sent to the server)
	 * but not corrected yet.
	 * 
	 * @param teamID ID of the team for which we want to get the received challenge IDs
	 * @return An array of integers containing the IDs of the received challenges
	 * @throws NotConnectDBException
	 */
	public Integer [] getReceivedAndNotCorrectedChallengeIDs(int teamID) 
			throws NotConnectDBException {
		if (dBConnection == null)
			throw new NotConnectDBException();
		
		ArrayList<Integer> challenges = new ArrayList<Integer>();
		Statement stmt = null;
		
		try {
			
			stmt = dBConnection.createStatement();
			String sql = "SELECT challenge_id FROM " + CHALLENGE_TABLE +
					" WHERE team_id="+teamID+" AND received=true AND corrected=false";
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				challenges.add(rs.getInt(1));
			}
			
		} catch (SQLException e) {
			throw new NotConnectDBException();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		Integer [] ret = new Integer[challenges.size()];
		challenges.toArray(ret);
		return ret;
	}
	
	/**
	 * Get all challenges that where corrected.
	 * 
	 * @param teamID ID of the team for which we want to get the corrected challenge IDs
	 * @return An array of integers containing the IDs of the corrected challenges
	 * for team teamID
	 * @throws NotConnectDBException
	 */
	public Integer [] getCorrectedChallengeIDs(int teamID) throws NotConnectDBException {
		if (dBConnection == null)
			throw new NotConnectDBException();
		
		ArrayList<Integer> challenges = new ArrayList<Integer>();
		Statement stmt = null;
		
		try {
			
			stmt = dBConnection.createStatement();
			String sql = "SELECT challenge_id FROM " + CHALLENGE_TABLE +
					" WHERE team_id="+teamID+" AND corrected=true";
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				challenges.add(rs.getInt(1));
			}
			
		} catch (SQLException e) {
			throw new NotConnectDBException();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		Integer [] ret = new Integer[challenges.size()];
		challenges.toArray(ret);
		return ret;
	}
	
	private String getChallengePath(int challengeId){
		return CHALLENGE_DIR + "/" + challengeId;
	}
	
	/**
	 * Check the correctness of the teamID.
	 * @param teamID
	 * @return true if the id exists false otherwise.
	 * @throws NotConnectDBException 
	 */
	public boolean checkTeamID(int teamID) throws NotConnectDBException{
		if (dBConnection == null)
			throw new NotConnectDBException();
		
		Statement stmt = null;
		boolean exists = false;
		try {
			stmt = dBConnection.createStatement();
			String sql = "SELECT team_id FROM "+ TEAM_TABLE +
					" WHERE team_id="+teamID;
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()){
				exists = true;
			}
		} catch (SQLException e) {
			throw new NotConnectDBException();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return exists;
	}
	
	public int getAssociatedTeamID(int challengeID) 
			throws NotConnectDBException {
		if (dBConnection == null)
			throw new NotConnectDBException();
		
		Statement stmt = null;
		int team = -1;
		try {
			stmt = dBConnection.createStatement();
			String sql = "SELECT team_id FROM "+ CHALLENGE_TABLE +
					" WHERE challenge_id="+challengeID;
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()){
				team = rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new NotConnectDBException();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return team;
	}
	
	/**
	 * Get the preshared secred of team teamID.
	 * @param teamID
	 * @return the secret
	 * @throws NotConnectDBException
	 */
	public String getSecret(int teamID) throws NotConnectDBException {
		if (dBConnection == null)
			throw new NotConnectDBException();
		
		Statement stmt = null;
		String secret = null;
		try {
			stmt = dBConnection.createStatement();
			String sql = "SELECT secret FROM "+ TEAM_TABLE +
					" WHERE team_id="+teamID;
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()){
				secret = rs.getString(1);
			}
		} catch (SQLException e) {
			throw new NotConnectDBException();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return secret;
	}

}