package ch.epfl.lasec.universitycontest.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/**
 * 
 * GUI for the server manager
 * 
 * @author Sebastien Duc
 *
 */
public class ServerManagerGUI extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTabbedPane tabs = new JTabbedPane();
	
	private TeamManagerGUI teamManager = new TeamManagerGUI();
	
	private UniversityManagerGUI uniManager = new UniversityManagerGUI();
	
	private ChallengeManagerGUI challengeManager = new ChallengeManagerGUI();
	
	public ServerManagerGUI(){
		setBasicWindowProperties();
		addTabs();
		
		this.setVisible(true);
	}
	
	/**
	 * Set the basic properties of the JFrame
	 */
	private void setBasicWindowProperties(){
		this.setSize(800,600);
		this.setTitle("University Server Contest Manager");
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Add the tabs in the window
	 */
	private void addTabs(){
		this.tabs.addTab("Team Management", teamManager);
		this.tabs.addTab("University Management", uniManager);
		this.tabs.addTab("Challenge Management", challengeManager);
		
		this.getContentPane().add(tabs,BorderLayout.CENTER);
	}
	
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		ServerManagerGUI s = new ServerManagerGUI();
	}
	
}
