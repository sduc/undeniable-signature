package ch.epfl.lasec.universitycontest.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import ch.epfl.lasec.universitycontest.ChallengeScore;
import ch.epfl.lasec.universitycontest.NotConnectDBException;
import ch.epfl.lasec.universitycontest.Quiz;
import ch.epfl.lasec.universitycontest.ServerManager;

/**
 * 
 * Used by the administrator to correct the quizzes and
 * assign a score.
 * 
 * @author Sebastien Duc
 *
 */
public class ChallengeCorrectionFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ServerManager sm = new ServerManager();
	
	private ChallengeCorrectionFrame _this = this;
	
	private Quiz sol = null;
	
	private ChallengeViewerPanel corr = null;
	
	public ChallengeCorrectionFrame(int challengeId) {
		frameConfig();
		this.setVisible(true);
		try {
			loadChallenges(challengeId);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error while loading challenges",
					"File loading error", JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	private void frameConfig(){
		this.setSize(1600,600);
		this.setTitle("University Server Contest Manager");
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowListener() {
			
			@Override
			public void windowClosing(WindowEvent e) {

				int output = JOptionPane.showConfirmDialog(null,
						"Do you want to save and quit", "Quit",
						JOptionPane.OK_CANCEL_OPTION);
				
				if (output == JOptionPane.OK_OPTION) {
					_this.dispose();
					ChallengeScore toSave = new ChallengeScore(sol, 
							corr.getQuestionScores());
					try {
						sm.saveChallengeScore(toSave);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "Error while saving score",
								"File saving error", JOptionPane.ERROR_MESSAGE);
					} catch (NotConnectDBException e1) {
						JOptionPane.showMessageDialog(null, "DataBase error",
								"DB Error", JOptionPane.ERROR_MESSAGE);
					}
				}

			}

			@Override
			public void windowOpened(WindowEvent e) {
				
			}

			@Override
			public void windowClosed(WindowEvent e) {
				
			}

			@Override
			public void windowIconified(WindowEvent e) {
				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				
			}

			@Override
			public void windowActivated(WindowEvent e) {
				
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				
			}
			
		});
		
	}
	
	private void loadChallenges(int challengeId) throws IOException{
		Quiz c1 = sm.loadChallenge(challengeId, ServerManager.TYPE_RES);
		sol = sm.loadChallenge(challengeId, ServerManager.TYPE_SOL);
		
		corr = new ChallengeViewerPanel(c1, false,true);
		
		JScrollPane scroll1 = new JScrollPane(corr);
		scroll1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		JScrollPane scroll2 = new JScrollPane(new ChallengeViewerPanel(sol, false,false));
		scroll2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		JSplitPane jsp = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, 
				scroll1, 
				scroll2
				);
		
		jsp.setDividerLocation(800);
		
		
		this.setContentPane(jsp);
	}

}
