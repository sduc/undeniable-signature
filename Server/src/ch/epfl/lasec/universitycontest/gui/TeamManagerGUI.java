package ch.epfl.lasec.universitycontest.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ch.epfl.lasec.universitycontest.NotConnectDBException;
import ch.epfl.lasec.universitycontest.ServerManager;

/**
 * 
 * Panel providing a GUI to manage everything related to teams.
 * 
 * @author Sebastien Duc
 *
 */
public class TeamManagerGUI extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String NEW_TEAM = "New team...";
	
	/**
	 * Used to choose the team
	 */
	private TeamComboBox mChooseTeam ;
	
	private JPanel mCentralPanel = new JPanel();
	
	private ReceivedChallengeComboBox mRcc;
	
	/**
	 * Server manager used to implement all interactions with the DB
	 */
	private ServerManager sm = new ServerManager();
	
	public TeamManagerGUI() {
		
		this.setLayout(new BorderLayout());
		
		fillNorth();
		
		fillCenter();
		
		JPanel margin = new JPanel();
		margin.add(Box.createRigidArea(new Dimension(20,20)));
		this.add(margin,BorderLayout.WEST);
		
	}
	
	/**
	 * Fill the north part of the panel
	 */
	private void fillNorth(){
		fillmChooseTeam();
		
		JPanel p = new JPanel();
		p.add(new JLabel("Choose a team "));
		p.add(mChooseTeam);
		
		this.add(p,BorderLayout.NORTH);
	}
	
	private void fillCenter(){
		
		mCentralPanel.setLayout(new BoxLayout(mCentralPanel, BoxLayout.PAGE_AXIS));
		
		mCentralPanel.add(Box.createRigidArea(new Dimension(50,50)));
		
		fillTeamScore(mCentralPanel);
		
		fillTeamUniversity(mCentralPanel);
		
		fillChooseCurrentChallenge(mCentralPanel);
		
		fillChallengeReceived(mCentralPanel);
		
		mCentralPanel.add(Box.createVerticalGlue());
		
		this.add(mCentralPanel,BorderLayout.CENTER);
		
	}
	
	private void fillChallengeReceived(JPanel container){
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		this.mRcc = new ReceivedChallengeComboBox(p);
		JButton b = new JButton("Open");
		
		if (mRcc.getItemCount() == 0)
			b.setEnabled(false);
		
		b.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int challengeId = mRcc.getSelectedChallengeID();
				
				new ChallengeCorrectionFrame(challengeId);
			}
		});
		p.add(b);
		
		container.add(p);
	}
	
	private void fillChooseCurrentChallenge(JPanel container) {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		p.add(new JLabel("Set current challenge "));
		fillSetCurrentChallengeComboBox(p);
		
		container.add(p);
	}
	
	private void fillSetCurrentChallengeComboBox(JPanel container) {
		new CurrentChallengeComboBox(container);
	}

	private void fillTeamUniversity(JPanel container) {
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		p.add(new JLabel("Univeristy : "));
		
		if (mChooseTeam.noSelection()){
			container.add(p);
			return;
		}
		
		try {
			String uni = sm.getTeamUni(mChooseTeam.getSelectedTeamID());
			p.add(new JLabel(uni));
		} catch (NotConnectDBException e) {
			JOptionPane.showMessageDialog(null,
					"You must connect to the DB", "DB ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
		
		container.add(p);
	}

	private void fillTeamScore(JPanel container){
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		p.add(new JLabel("Team score : "));
		
		if (mChooseTeam.noSelection()){
			container.add(p);
			return;
		}
		
		int score = 0;
		try {
			score = sm.getTeamScore(mChooseTeam.getSelectedTeamID());
		} catch (NotConnectDBException e) {
			JOptionPane.showMessageDialog(null,
					"You must connect to the DB", "DB ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
		
		p.add(new JLabel(""+score));
		
		container.add(p);
		
	}
	
	/**
	 * Fill the combo box mChooseTeam.
	 */
	private void fillmChooseTeam(){
		
		this.mChooseTeam = new TeamComboBox();
		
	}
	
	private void refreshCentralPanel(){
		this.remove(mCentralPanel);
		mCentralPanel = new JPanel();
		fillCenter();
		this.add(mCentralPanel,BorderLayout.CENTER);
		this.revalidate();
	}
	
	/**
	 * 
	 * Combo box used to choose amongst the universities
	 * 
	 * @author Sebastien Duc
	 *
	 */
	private class UniComboBox extends JComboBox {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public UniComboBox() throws NotConnectDBException {
			String [] uni = sm.getUni();
			for (String s : uni) {
				this.addItem(s);
			}
		}
		
		public String getSelectedUni(){
			return this.getSelectedItem().toString();
		}
		
	}
	
	/**
	 * Combo box to choose the team
	 * 
	 * @author Sebastien Duc
	 *
	 */
	private class TeamComboBox extends JComboBox {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private TeamComboBox _this = this;
		
		public TeamComboBox() {
			this.setPreferredSize(new Dimension(200,20));
			
			addTeams();
			
			this.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					String itemS = _this.getSelectedItem().toString();
					
					if (itemS.equals(NEW_TEAM)) {
						UniComboBox ucb = null;
						try {
							ucb = new UniComboBox();
						} catch (NotConnectDBException e2) {
							JOptionPane.showMessageDialog(null,
									"You must connect to the DB", "DB ERROR",
									JOptionPane.ERROR_MESSAGE);
						} 
						
						int response = JOptionPane.showConfirmDialog(null,
								ucb, "Choose university",
								JOptionPane.OK_CANCEL_OPTION);
						
						if (response == JOptionPane.CANCEL_OPTION){
							return;
						}
						
						String psswd = JOptionPane.showInputDialog(null, 
								"Enter a preshared secret for the team", "Password", 
								JOptionPane.OK_OPTION);
						
						try {
							sm.addTeam(ucb.getSelectedUni(),psswd);
						} catch (NotConnectDBException e1) {
							JOptionPane.showMessageDialog(null,
									"You must connect to the DB", "DB ERROR",
									JOptionPane.ERROR_MESSAGE);
						}
						
						refresh();
						
						return;
					}
					
					@SuppressWarnings("unused")
					int teamId = Integer.parseInt(itemS);
					
					refreshCentralPanel();
					//TODO: when a team ID is selected.
					
				}
			});
			
			
		}
		
		private void addTeams(){
			this.addItem(NEW_TEAM);
			try {
				String [] teams = sm.getTeams();
				for (String s : teams) {
					this.addItem(s);
				}
			} catch (NotConnectDBException e) {
				JOptionPane.showMessageDialog(null,
						"You must connect to the DB", "DB ERROR",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		
		public void refresh(){
			
			this.removeAllItems();
			
			addTeams();
			
		}
		
		/**
		 * @return the selected team ID
		 */
		public int getSelectedTeamID(){
			return Integer.parseInt(this.getSelectedItem().toString());
		}
		
		/**
		 * Test if there is no team selection.
		 * If there is no selection, return true
		 * 
		 * @return true if there is no selection. false otherwise.
		 */
		public boolean noSelection(){
			return this.getSelectedItem().toString().equals(NEW_TEAM);
		}
		
	}
	
	/**
	 * 
	 * ComboBox to choose the current challenge for the selected team
	 * 
	 * @author Sebastien Duc
	 *
	 */
	private class CurrentChallengeComboBox extends JComboBox {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private static final String NONE = "none";
		
		private CurrentChallengeComboBox _this = this;
		
		public CurrentChallengeComboBox(JPanel container) {
			
			this.setPreferredSize(new Dimension(200,20));
			
			if (mChooseTeam.noSelection()) {
				container.add(this);
				return;
			}
			
			try {
				Integer [] available = sm.getAvailableChallengeIDs(
						mChooseTeam.getSelectedTeamID());
				for (Integer integer : available) {
					this.addItem(integer);
				}
				
				this.addItem(NONE);
				
				this.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						
						String itemS = _this.getSelectedItem().toString();
						
						if (itemS.equals(NONE)) {
							try {
								sm.setCurrentChallengeToNull(mChooseTeam.getSelectedTeamID());
							} catch (NotConnectDBException e1) {
								JOptionPane.showMessageDialog(null,
										"You must connect to the DB", "DB ERROR",
										JOptionPane.ERROR_MESSAGE);
							}
						}
						
						else {
							
							int challengeID = Integer.parseInt(itemS);
							
							try {
								sm.setCurrentChallenge(mChooseTeam.getSelectedTeamID(),
										challengeID);
							} catch (NotConnectDBException e1) {
								JOptionPane.showMessageDialog(null,
										"You must connect to the DB", "DB ERROR",
										JOptionPane.ERROR_MESSAGE);
							}
						}
						
					}
				});
				
			} catch (NotConnectDBException e) {
				JOptionPane.showMessageDialog(null,
						"You must connect to the DB", "DB ERROR",
						JOptionPane.ERROR_MESSAGE);
			}
			
			container.add(this);
		}
		
	}
	
	private class ReceivedChallengeComboBox extends JComboBox {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ReceivedChallengeComboBox(JPanel container) {
			
			this.setPreferredSize(new Dimension(200,20));
			
			// Do nothing if no team selected
			if (mChooseTeam.noSelection()) {
				container.add(this);
				return;
			}
			
			// fill the Combo Box
			try {
				
				Integer[] rChallenges = sm.getReceivedAndNotCorrectedChallengeIDs(mChooseTeam
						.getSelectedTeamID());
				
				for (Integer challengeID : rChallenges) {
					this.addItem(challengeID);
				}
				
			} catch (NotConnectDBException e) {
				JOptionPane.showMessageDialog(null,
						"You must connect to the DB", "DB ERROR",
						JOptionPane.ERROR_MESSAGE);
			}

			container.add(this);
		}
		
		@Override
		public Object getSelectedItem() {
			return super.getSelectedItem();
		}
		
		/**
		 * Get the ID of the selected challenge ID
		 * @return
		 */
		public int getSelectedChallengeID() {
			return Integer.parseInt(getSelectedItem().toString());
		}
	}
	
}
