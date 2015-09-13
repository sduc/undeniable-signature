package ch.epfl.lasec.universitycontest.gui;

import java.awt.BorderLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import ch.epfl.lasec.universitycontest.DataBaseHandler;
import ch.epfl.lasec.universitycontest.ServerManager;

public class ViewChallengesPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String CHALLENGE_DIR = DataBaseHandler.CHALLENGE_DIR;
	
	private CustomFileChooser mChallengeFileChooser = new CustomFileChooser();
	
	private ServerManager sm = new ServerManager();

	public ViewChallengesPanel() {
		
		this.setLayout(new BorderLayout());
		this.add(mChallengeFileChooser,BorderLayout.CENTER);
	}
	
	
	class CustomFileChooser extends JPanel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		JFileChooser jfc = new JFileChooser(CHALLENGE_DIR);
		
		@SuppressWarnings("unused")
		private JPanel _this = this;
		
		public CustomFileChooser() {
			this.setLayout(new BorderLayout(20,20));
			configureFileChooser();
			this.add(jfc.getComponent(2),BorderLayout.CENTER);
		}
		
		private void configureFileChooser(){
			jfc.setMultiSelectionEnabled(false);
			
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			
			// filter files to have only the basic challenge files
			jfc.setFileFilter(new FileFilter() {
				
				@Override
				public String getDescription() {
					return "challenge files";
				}
				
				@Override
				public boolean accept(File f) {
					return !f.getName().endsWith(
							DataBaseHandler.CHALLENGE_RESULT_FILE_EXT)
							&& 
							!f.getName().endsWith(
							DataBaseHandler.CHALLENGE_SOLUTION_FILE_EXT)
							&&
							!f.getName().endsWith(
							DataBaseHandler.CHALLENGE_SCORE_FILE_EXT);
				}
			});
			
			setListeners();
		}
		
		public int getSelectedChallenge(){
			return Integer.parseInt(jfc.getSelectedFile().getName());
		}
		
		private void setListeners(){
			KeyboardFocusManager kmanager = KeyboardFocusManager
					.getCurrentKeyboardFocusManager();
			kmanager.addKeyEventDispatcher(new KeyEventDispatcher() {
				
				@Override
				public boolean dispatchKeyEvent(KeyEvent e) {
					if (e.getID() == KeyEvent.KEY_PRESSED) {
						if (e.getKeyCode() == KeyEvent.VK_DELETE){
							jfc.getSelectedFile().delete();
							jfc.resetChoosableFileFilters();
							// TODO: take care of the solution file too
						}
					}
					return false;
				}
			});
			
			jfc.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					try {
						ChallengeViewerPanel.lauch(sm.loadChallenge(getSelectedChallenge(), 
								ServerManager.TYPE_SOL)
								, false);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "Error while loading challenge!"
								, "File loading error", JOptionPane.ERROR_MESSAGE);
					}
					
				}
			});
		}
	}

}
