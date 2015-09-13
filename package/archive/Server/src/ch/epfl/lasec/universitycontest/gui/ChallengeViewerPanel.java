package ch.epfl.lasec.universitycontest.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import ch.epfl.lasec.universitycontest.MultipleChoiceQuestion;
import ch.epfl.lasec.universitycontest.Quiz;
import ch.epfl.lasec.universitycontest.QuizQuestion;

/**
 * Panel to view a challenge
 * 
 * @author Sebastien Duc
 *
 */
public class ChallengeViewerPanel extends JPanel {

	public static final int MAX_QUESTION_SCORE = 10;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final int MAX_CHAR = 40;
	
	private ArrayList<ScoreComboBox>  scores = null;
	

	public ChallengeViewerPanel(Quiz q,boolean editable, boolean scoreEnabled) {
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		setTitle(q);
		
		if (scoreEnabled)
			scores = new ArrayList<ChallengeViewerPanel.ScoreComboBox>();
		
		for (QuizQuestion qq : q.getQuestions()) {
			if (qq.getClass() == MultipleChoiceQuestion.class){
				MCQPanel mcqp = new MCQPanel((MultipleChoiceQuestion)qq, editable, scoreEnabled);
				this.add(mcqp);
				if (scoreEnabled)
					this.scores.add(mcqp.getScoreComboBox());
			}
			else {
				QuizQuestionPanel qqp = new QuizQuestionPanel(qq, editable, scoreEnabled);
				this.add(qqp);
				if (scoreEnabled)
					this.scores.add(qqp.getScoreComboBox());
			}
		}
		
	}
	
	private void setTitle(Quiz q){
		JLabel title = new JLabel(q.getTitle());
		title.setFont(new Font(title.getFont().getFontName(),
				title.getFont().getStyle(), 20));
		this.add(title);
		this.add(new JSeparator(JSeparator.HORIZONTAL));
	}
	
	public int [] getQuestionScores() {
		if (scores == null){
			return null;
		}
		
		int [] s = new int[scores.size()];
		for (int i = 0; i < s.length; i++) {
			s[i] = scores.get(i).getSelectedScore();
		}
		return s;
	}
	
	class QuizQuestionPanel extends JPanel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private ScoreComboBox scb = new ScoreComboBox(MAX_QUESTION_SCORE);

		public QuizQuestionPanel(QuizQuestion qq, boolean editable, boolean scoreEnabled) {
			this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			
			setQuestionPart(qq, editable, scoreEnabled);
			setAnswerPart(qq, editable);
		}
		
		private void setQuestionPart(QuizQuestion qq, boolean editable, boolean scoreEnabled){
			setSomethingPart("Question", qq.getQuestion(), editable, scoreEnabled);
		}
		
		private void setAnswerPart(QuizQuestion qq, boolean editable){
			setSomethingPart("Answer", qq.getAnswer(), editable,false);
		}
		
		private void setSomethingPart(String somethingTitle, String something, 
				boolean editable, boolean scoreEnabled){
			
			JPanel p = new JPanel();
			JLabel question = new JLabel(somethingTitle);
			p.add(question);
			if(scoreEnabled)
				p.add(scb);
			this.add(p);
			
			if (something.length() < MAX_CHAR) {
				JTextField qtf = new JTextField();
				qtf.setText(something);
				qtf.setEditable(editable);
				qtf.setColumns(something.length());
				this.add(qtf);
			} else {
				JTextArea ta = new JTextArea();
				ta.setText(something);
				ta.setEditable(editable);
				ta.setWrapStyleWord(true);
				ta.setLineWrap(true);
				this.add(ta);
			}
		}
		
		public ScoreComboBox getScoreComboBox(){
			return scb;
		}
		
		
	}
	
	class MCQPanel extends JPanel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private ScoreComboBox scb = new ScoreComboBox(MAX_QUESTION_SCORE);

		public MCQPanel(MultipleChoiceQuestion mcq, boolean editable, boolean scoreEnabled) {
			this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			
			setQuestionPart(mcq, editable, scoreEnabled);
			setAnswerPart(mcq, editable);
		}
		
		private void setQuestionPart(MultipleChoiceQuestion mcq, boolean editable, 
				boolean scoreEnabled){
			JPanel p = new JPanel();
			JLabel question = new JLabel("Question");
			p.add(question);
			if(scoreEnabled)
				p.add(scb);
			this.add(p);
			
			JTextField qtf = new JTextField();
			qtf.setText(mcq.getQuestion());
			qtf.setEditable(editable);
			qtf.setColumns(mcq.getQuestion().length());
			this.add(qtf);
		}
		
		private void setAnswerPart(MultipleChoiceQuestion mcq, boolean editable){
			JLabel answer = new JLabel("Answer");
			this.add(answer);
			
			for (int i = 0; i < mcq.getPossibleAnswers().length; ++i) {
				JPanel p = new JPanel();
				p.setLayout(new FlowLayout(FlowLayout.LEADING));
				JRadioButton rb = new JRadioButton();
				if (i == mcq.getIntAnswer()) {
					rb.setSelected(true);
				}
				rb.setEnabled(false);
				p.add(rb);
				
				JTextField jtf = new JTextField();
				jtf.setText(mcq.getPossibleAnswers()[i]);
				jtf.setEditable(false);
				p.add(jtf);
				
				this.add(p);
			}
		}
		
		public ScoreComboBox getScoreComboBox(){
			return this.scb;
		}
		
	}
	
	/**
	 * Launch the challengeViewer in a new frame.
	 * 
	 * @param q
	 * @param editable
	 */
	public static void lauch(Quiz q, boolean editable){
		JFrame f = new JFrame("Challenge Viewer");
		f.setSize(800,600);
		f.setTitle("University Server Contest Manager");
		f.setLocationRelativeTo(null);
		
		JScrollPane jsp = new JScrollPane(new ChallengeViewerPanel(q, editable,false));
		jsp.getVerticalScrollBar().setUnitIncrement(16);
		f.setContentPane(jsp);
		f.setVisible(true);
	}
	
	/**
	 * 
	 * Combo box to choose the score for a question
	 * 
	 * @author Sebastien Duc
	 *
	 */
	class ScoreComboBox extends JComboBox {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ScoreComboBox(int upperBound) {
			for (int i = 0; i < upperBound; i++) {
				this.addItem(i);
			}
			this.setPreferredSize(new Dimension(40,20));
		}
		
		public int getSelectedScore(){
			return Integer.parseInt(this.getSelectedItem().toString());
		}
		
	}
}
