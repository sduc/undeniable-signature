package ch.epfl.lasec.universitycontest.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ChallengeManagerGUI extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JComboBox chooseAction = new JComboBox();
	private JLabel chooseActionLabel = new JLabel("Choose action ");
	private JPanel centrePanel = new JPanel();
	private JPanel _this = this;
	
	public ChallengeManagerGUI() {
		this.setLayout(new BorderLayout());
		this.setBackground(Color.white);
		centrePanel.setBackground(Color.white);
		setChooseAction();
		
	}
	
	private void setChooseAction(){
		chooseAction.setPreferredSize(new Dimension(200,20));
		
		chooseAction.addItem("choose action...");
		chooseAction.addItem("new challenge");
		chooseAction.addItem("view challenges");
		
		chooseAction.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				_this.remove(centrePanel);
				
				String itemS = chooseAction.getSelectedItem().toString();
				
				if (itemS.equals("new challenge")){
					centrePanel = new NewChallengeJPanel();
				}
				
				else if (itemS.equals("view challenges")){
					centrePanel = new ViewChallengesPanel();
				}
				
				else {
					centrePanel = new JPanel();
				}
				
				_this.add(centrePanel,BorderLayout.CENTER);
				centrePanel.revalidate();
			}
			
		});
		
		JPanel top = new JPanel();
        top.add(chooseActionLabel);
        top.add(chooseAction);
        
        this.add(top, BorderLayout.NORTH);
        this.add(centrePanel, BorderLayout.CENTER);
	}

	
	
}
