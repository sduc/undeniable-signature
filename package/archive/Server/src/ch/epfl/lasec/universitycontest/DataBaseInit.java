package ch.epfl.lasec.universitycontest;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * Use to create the database.
 * 
 * @author Sebastien Duc
 * 
 */
public class DataBaseInit {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/";

	// Database credentials
	static final String USER = "java";
	static final String PASS = "akfss19uT";

	public static void main(String[] args) {
		
		createChallengeDir();
		
		Connection conn = null;

		try {
			// STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// STEP 3: Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			// create database and tables
			createDB(conn, DataBaseHandler.DATABASE_NAME);
			createTableUniversity(conn);
			createTableTeam(conn);
			createTableChallenge(conn);

		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}// end finally try
		}// end try
		System.out.println("Goodbye!");
	}// end main

	/**
	 * Create database dbName using connection conn
	 * 
	 * @param conn
	 * @param dbName
	 * @throws SQLException
	 */
	private static void createDB(Connection conn, String dbName)
			throws SQLException {
		Statement stmt = null;
		// STEP 4: Execute a query
		System.out.println("Creating database...");
		stmt = conn.createStatement();

		String sql = "CREATE DATABASE IF NOT EXISTS" + dbName;
		stmt.executeUpdate(sql);
		System.out.println("Database created successfully...");

		if (stmt != null)
			stmt.close();
	}
	
	private static void createTableUniversity(Connection conn) throws SQLException {
		String sql = "CREATE TABLE IF NOT EXISTS "+DataBaseHandler.UNIVERSITY_TABLE
				+ " ("
				+ "university_id VARCHAR(40) PRIMARY KEY,"
				+ "score INT NOT NULL DEFAULT 0,"
				+ ");";
		createTable(conn, sql);
	}

	private static void createTableTeam(Connection conn) throws SQLException {
		
		String sql = "CREATE TABLE IF NOT EXISTS "+DataBaseHandler.TEAM_TABLE
				+ " ("
				+ "team_id SMALLINT AUTO_INCREMENT PRIMARY KEY,"
				+ "score INT NOT NULL DEFAULT 0," 
				+ "university VARCHAR(40),"
				+ "current_challenge INT UNIQUE," 
				+ "secret VARCHAR(20) NOT NULL,"
				+ "CONSTRAINT fk_university"
				+ 		"FOREIGN KEY (university)"
				+		"REFERENCES university(university_id)"
				+ ");";
		
		createTable(conn, sql);
	}
	
	private static void createTableChallenge(Connection conn) throws SQLException {
		
		String sql = "CREATE TABLE IF NOT EXISTS "+DataBaseHandler.CHALLENGE_TABLE
				+ " ("
				+ "challenge_id INT PRIMARY KEY,"
				+ "team_id SMALLINT,"
				+ "score INT NOT NULL DEFAULT 0,"
				+ "received BOOLEAN NOT NULL DEFAULT false,"
				+ "corrected BOOLEAN NOT NULL DEFAULT false,"
				+ "challenge_name VARCHAR(40),"
				+ "CONSTRAINT fk_team_id"
				+ 		"FOREIGN KEY (team_id)"
				+		"REFERENCES team(team_id)"
				+ ");";
		
		createTable(conn, sql);
	}
	
	private static void createTable(Connection conn, String sql) throws SQLException {
		System.out.println("Creating table...");
		Statement stmt = null;
		stmt = conn.createStatement();
		stmt.execute(sql);
		System.out.println("Table created succefully...");
		if (stmt != null)
			stmt.close();
	}
	
	/**
	 * Create the directory containing the challenges (.res and .sol)
	 */
	private static void createChallengeDir(){
		boolean succ = new File(DataBaseHandler.CHALLENGE_DIR).mkdir();
		if (succ)
			System.out.println("Challenge directory created");
		else
			System.out.println("Warning: Challenge directory already exists");
	}
	
	

}
