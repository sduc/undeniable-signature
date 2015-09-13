package ch.epfl.lasec.universitycontest.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import com.toedter.calendar.JDateChooser;

import ch.epfl.lasec.universitycontest.MultipleChoiceQuestion;
import ch.epfl.lasec.universitycontest.NotConnectDBException;
import ch.epfl.lasec.universitycontest.Quiz;
import ch.epfl.lasec.universitycontest.QuizQuestion;
import ch.epfl.lasec.universitycontest.ServerManager;

/**
 * 
 * Panel used when the administrator wants to create a new challenge.
 * 
 * @author Sebastien Duc
 *
 */
public class NewChallengeJPanel extends JPanel {
	
	private static final String QUESTION_TYPE_MCQ = "MCQ";
	private static final String QUESTION_TYPE_STD = "standard";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Panel containing all the centre of this
	 */
	private JPanel mContainer = new JPanel();
	
	/**
	 * Text field to enter the title of the challenge
	 */
	private JTextField mChallengeTitle = new JTextField(20);
	
	/**
	 * ComboBox to choose between the teams to assign one to the challenge.
	 */
	private JComboBox mTeamChoiceComboBox = new JComboBox();
	
	/**
	 * Used to choose the due to date.
	 */
	private JDateChooser mDateChooser = new JDateChooser(null,"yyyy-MM-dd");
	
	/**
	 * ComboBox used to select the type of question to add to the challenge.
	 */
	private JComboBox mSelecetQuestionTypeComboBox = new JComboBox();
	
	/**
	 * Panel containing all the panel for the added questions
	 */
	private JPanel mQuestionContainer = new JPanel();
	
	/**
	 * Button used to add a question
	 */
	private JButton mAddQuestionButton = new JButton("+");
	
	/**
	 * Button used to save the challenge.
	 */
	private JButton mSaveChallengeButton = new JButton("save challenge");
	
	/**
	 * Server manager used to implement all interactions with the DB
	 */
	private ServerManager sm = new ServerManager();
	
	/**
	 * array containing all the panels of the added questions.
	 */
	private ArrayList<QuestionPanel> mQuestionPanels = new ArrayList<QuestionPanel>();
	
	
	public NewChallengeJPanel() {
		this.setLayout(new BorderLayout());
		
		setThisPanelTitle();
		fillContainer();
		setSaveChallengeButton();
	}
	
	/**
	 * Set the header of the panel. The title: which is New challenge, and the color, the 
	 * position of the header.
	 */
	private void setThisPanelTitle(){
		
		JLabel mPanelTitle = new JLabel("New challenge");
		
		mPanelTitle.setFont(new Font(mPanelTitle.getFont().getFontName(),
				mPanelTitle.getFont().getStyle(), 20));
		
		JPanel title = new JPanel();
		title.setLayout(new FlowLayout());
		title.add(mPanelTitle);
		title.setBackground(Color.white);
		
		this.add(title, BorderLayout.NORTH);
	}
	
	/**
	 * Fill the container panel. It contains everything except the header.
	 */
	private void fillContainer(){
		
		this.mContainer.setLayout(new BorderLayout());
		
		JPanel topContainer = new JPanel();
		
		GridLayout gl = new GridLayout(3,1);
		gl.setVgap(5);
		topContainer.setLayout(gl);
		
		fillTitleChallenge(topContainer);
		fillChooseTeam(topContainer);
		fillDateDueToSelector(topContainer);
		
		mContainer.add(topContainer,BorderLayout.NORTH);
		
		fillQuestionAdder();
		
		mQuestionContainer.setLayout(new BoxLayout(mQuestionContainer, BoxLayout.PAGE_AXIS));
		
		JScrollPane jsp = new JScrollPane(mQuestionContainer);
		jsp.getVerticalScrollBar().setUnitIncrement(16);
		mContainer.add(jsp,BorderLayout.CENTER);
		
		this.add(mContainer,BorderLayout.CENTER);
	}
	
	/**
	 * Put in the container the text field that allows the user to choose the 
	 * title of the challenge
	 * 
	 * @param container
	 */
	private void fillTitleChallenge(JPanel container){
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT,20,0));
		
		p.add(new JLabel("Challenge title "));
		p.add(this.mChallengeTitle);
		
		container.add(p);
	}
	
	/**
	 * Text field for choosing the assigned team.
	 * 
	 * @param container
	 */
	private void fillChooseTeam(JPanel container){
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT,19,0));
		
		p.add(new JLabel("Assigned team "));
		
		mTeamChoiceComboBox.setPreferredSize(new Dimension(200,20));
		try {
			String [] teams = sm.getTeams();
			for (String s : teams) {
				mTeamChoiceComboBox.addItem(s);
			}
		} catch (NotConnectDBException e) {
			JOptionPane.showMessageDialog(null,
					"You must connect to the DB", "DB ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
		
		p.add(mTeamChoiceComboBox);
		
		container.add(p);
	}
	
	/**
	 * Add the content to set the due to date.
	 * 
	 * @param container
	 */
	private void fillDateDueToSelector(JPanel container){
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT,45,0));
		p.add(new JLabel("Due to "));
		p.add(mDateChooser);
		container.add(p);
	}
	
	/**
	 * button to add questions
	 */
	private void fillQuestionAdder() {
		JPanel p = new JPanel();
		
		mSelecetQuestionTypeComboBox.setPreferredSize(new Dimension(200,20));
		mSelecetQuestionTypeComboBox.addItem(QUESTION_TYPE_STD);
		mSelecetQuestionTypeComboBox.addItem(QUESTION_TYPE_MCQ);
		
		p.add(mSelecetQuestionTypeComboBox);
		
		/* Listener for the button (+) to add new questions in the
		 * quiz.  */
		mAddQuestionButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				// used just for esthetics
				if (mQuestionPanels.size() > 0) {
					mQuestionContainer.add(new JSeparator(JSeparator.HORIZONTAL));
					mQuestionContainer.add(Box.createRigidArea(new Dimension(0,15)));
				}
				
				String qType = mSelecetQuestionTypeComboBox.getSelectedItem().toString();
				if (qType.equals(QUESTION_TYPE_STD)){
					StdQuestionPanel qst = new StdQuestionPanel(mQuestionPanels.size() + 1);
					mQuestionContainer.add(qst);
					mQuestionPanels.add(qst);
					
				}
				else if (qType.equals(QUESTION_TYPE_MCQ)){
					// ask the user for the number of possible solution
					String qNumber = JOptionPane.showInputDialog(null, 
							"How many possible answers", "Add MCQ" ,
							JOptionPane.QUESTION_MESSAGE);
					
					MCQPanel mcq = new MCQPanel(mQuestionPanels.size() + 1 , 
							Integer.parseInt(qNumber));
					mQuestionContainer.add(mcq);
					mQuestionPanels.add(mcq);
					
				}
				mQuestionContainer.add(Box.createRigidArea(new Dimension(0,15)));
				mQuestionContainer.revalidate();
			}
		});
		
		p.add(mAddQuestionButton);
		mContainer.add(p,BorderLayout.SOUTH);
	}
	
	/**
	 * Button the save the challenge.
	 */
	private void setSaveChallengeButton(){
		
		/* Listener for the button to save the challenge. */
		mSaveChallengeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// check that the form was filled
				if (mDateChooser.getDate() == null) {
					JOptionPane.showMessageDialog(null, "Enter a date",
							"Incomplete error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (mChallengeTitle.getText() == null || 
						mChallengeTitle.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "Enter a title",
							"Incomplete error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (mQuestionPanels.size() == 0) {
					JOptionPane.showMessageDialog(null, "The challenge has no question",
							"Incomplete challenge", JOptionPane.ERROR_MESSAGE);
					return;
				}
					
				//create the quiz
				Quiz q = new Quiz(mDateChooser.getDate(), mChallengeTitle.getText());
				addQuizQuestions(q);
				try {
					sm.addChallenge(q, getTeamId());
				} catch (NotConnectDBException e1) {
					JOptionPane.showMessageDialog(null,
							"You must connect to the DB", "DB ERROR",
							JOptionPane.ERROR_MESSAGE);
				}
				
			}
			
		});
		
		JPanel p = new JPanel();
		p.add(mSaveChallengeButton);
		p.setBackground(Color.white);
		this.add(p,BorderLayout.SOUTH);
	}
	
	/**
	 * Get the assigned selected team ID for the challenge
	 * @return
	 */
	private int getTeamId(){
		return Integer.parseInt(this.mTeamChoiceComboBox.getSelectedItem().toString());
	}
	
	/**
	 * Add questions to quiz q given the graphical questions added by the user.
	 * @param q
	 */
	private void addQuizQuestions(Quiz q){
		for (QuestionPanel qp : mQuestionPanels) {
			// MCQ case
			if (qp.getClass() == MCQPanel.class){
				MCQPanel mcqp = (MCQPanel) qp;
				
				MultipleChoiceQuestion mcq = new MultipleChoiceQuestion(
						mcqp.getQuestion(), 
						mcqp.getPossibleSolutions());
				mcq.setAnswer(mcqp.getSolution());
				q.addMultipleChoiceQuestion(mcq);
			}
			//Standard Question Case
			else {
				QuizQuestion qq = new QuizQuestion(qp.getQuestion());
				qq.setAnswer(qp.getSolution());
				q.addQuestion(qq);
			}
		}
	}
	
}


/**
 * Abstract panel that is extended by the standard or the mcq panel
 * 
 * @author Sebastien Duc
 *
 */
abstract class QuestionPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the question number. this is it questionNumber'th added question
	 */
	private int questionNumber;
	
	/**
	 * Text field to enter the question
	 */
	private JTextField mQuestionTextField = new JTextField(50);
	
	public QuestionPanel(int questionNumber){
		this.questionNumber = questionNumber;
	}

	public int getQuestionNumber() {
		return questionNumber;
	}

	public JTextField getmQuestionTextField() {
		return mQuestionTextField;
	}

	public void setmQuestionTextField(JTextField mQuestionTextField) {
		this.mQuestionTextField = mQuestionTextField;
	}
	
	/**
	 * Get the question
	 * @return
	 */
	public String getQuestion(){
		return mQuestionTextField.getText();
	}
	
	/**
	 * Get the solution of the question
	 * @return
	 */
	public abstract String getSolution();
	
}


/**
 * 
 * Panel for a standard question
 * 
 * @author Sebastien Duc
 *
 */
class StdQuestionPanel extends QuestionPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Text field to enter the solution
	 */
	private JTextField mSolutionTextField = new JTextField(50);
	
	public StdQuestionPanel(int questionNumber) {
		super(questionNumber);
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p1.add(new JLabel("Question " + questionNumber));p1.add(super.getmQuestionTextField());
		p2.add(new JLabel("Solution  " + questionNumber));p2.add(mSolutionTextField);
		this.add(p1);this.add(p2);
		this.add(Box.createVerticalGlue());
	}
	
	@Override
	public String getSolution(){
		return this.mSolutionTextField.getText();
	}
	
}

/**
 * 
 * Panel for a multiple choice question
 * 
 * @author Sebastien Duc
 *
 */
class MCQPanel extends QuestionPanel {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Array of custom panel for the possible solution
	 */
	private ArrayList<PossibleSolutionPanel> mPossibleSolutions = 
			new ArrayList<PossibleSolutionPanel>();
	
	/**
	 * Button group for the radio buttons.
	 */
	private ButtonGroup mPossibleSolutionsGroup = new ButtonGroup();

	/**
	 * @param questionNumber 
	 * @param solutionNumber the number of possible solutions
	 */
	public MCQPanel(int questionNumber, int solutionNumber) {
		super(questionNumber);
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p.add(new JLabel("Question " + questionNumber));p.add(super.getmQuestionTextField());
		this.add(p);
		
		for (int i = 0; i < solutionNumber; i++) {
			PossibleSolutionPanel psp = new PossibleSolutionPanel();
			mPossibleSolutions.add(psp);
			mPossibleSolutionsGroup.add(psp.getRadioButton());
			this.add(psp);
		}
		
		this.add(Box.createVerticalGlue());
		
	}

	@Override
	public String getSolution() {
		for (int i = 0; i < mPossibleSolutions.size(); i++) {
			if (mPossibleSolutions.get(i).getRadioButton().getModel() == 
					mPossibleSolutionsGroup.getSelection()){
				return ""+i;
			}
		}
		return null;
	}
	
	/**
	 * Return a array of all the possible answers.
	 * 
	 * @return
	 */
	public String [] getPossibleSolutions() {
		String [] ret = new String [mPossibleSolutions.size()];
		
		for (int i = 0; i < ret.length; i++) {
			ret[i] = mPossibleSolutions.get(i).getAnswer();
		}
		
		return ret;
	}
	
	/**
	 * 
	 * Panel used for one possible answer
	 * 
	 * @author Sebastien Duc
	 *
	 */
	class PossibleSolutionPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private JRadioButton mRadioButton = new JRadioButton();
		
		private JTextField mSolutionTextField = new JTextField(20);
		
		public PossibleSolutionPanel() {
			this.setLayout(new FlowLayout(FlowLayout.LEFT));
			
			this.add(mRadioButton);
			this.add(mSolutionTextField);
		}
		
		public JRadioButton getRadioButton(){
			return mRadioButton;
		}
		
		public JTextField getTextField(){
			return mSolutionTextField;
		}
		
		public String getAnswer(){
			return mSolutionTextField.getText();
		}
		
	}
}