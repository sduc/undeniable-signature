package ch.epfl.lasec.universitycontest;

import java.io.IOException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * This Activity is called by activity TeamScore to show the result of a particular
 * challenge the team did. 
 * 
 * @author Sebastien Duc
 *
 */
public class ChallengeResultActivity extends Activity {

	/**
	 * Manage dialog windows to show error messages and informations to the user
	 */
	private DialogHandler mDialogHandler = new DialogHandler(this);
	
	/**
	 * Score associated to the chosen challenge
	 */
	private ChallengeScore mChallengeScore = null;
	
	/**
	 * Server used to download the correction of the quiz
	 */
	private Server mAdvServer = new Server(Server.ADV_UNIVERSITY_SERVER_IP,
			UniversityContestServer.SERVER_PORT,0,null);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.challenge_result_activity_layout);
		
		// get the data from the extras
		Bundle extras = getIntent().getExtras();
		String challengeName = extras.getCharSequence(
				TeamScoreActivity.CHALLENGE_NAME).toString();
		
		loadConfig();
		
		downloadResult(challengeName);
		
		populateFields();
		
	}
	
	/**
	 * Load the config of the application from .conf to know the ip addr of both servers
	 * and the team ID. It's the first thing to do.
	 */
	private void loadConfig() {
		Config c = new Config(this);
		try {
			c.loadConfig();
			mAdvServer.setIpAddress(c.advServerIpAddress);
			mAdvServer.setTeamID(c.teamID);
			mAdvServer.setSecret(c.secret);
			
		} catch (IOException e) {
			mDialogHandler.showError("Configuration Error");
		}
	}
	
	private void downloadResult(String challengeName){
		
		try {
			mAdvServer.startCommunication();
			mChallengeScore = UniversityContestProtocol.askChallengeSolution(
					mAdvServer.getIn(), 
					mAdvServer.getOut(), 
					challengeName);
			mAdvServer.endCommunication();
		} catch (UnknownHostException e) {
			mDialogHandler.showError(R.string.connection_server_error);
		} catch (IOException e) {
			mDialogHandler.showError(R.string.connection_server_error);
		} catch (InvalidAuthenticationException e) {
			mDialogHandler.showError(R.string.authentication_error);
		}
		
	}
	
	private void populateFields() {
		if (mChallengeScore == null)
			return;
		
		((TextView) findViewById(R.id.challenge_result_title_text_view))
				.setText(mChallengeScore.getScoreOf());
		
		((TextView) findViewById(R.id.challenge_result_score_text_view))
				.setText(""+mChallengeScore.getScore());
		
		LinearLayout container = (LinearLayout) findViewById(R.id.challenge_result_score_container);

		if (mChallengeScore.getcPointer().getClass() == Quiz.class) {
			Quiz quiz = (Quiz) mChallengeScore.getcPointer();
			for (int i = 0; i < quiz.getQuestions().size(); ++i) {
				addLayoutQuizQuestion(container, quiz.questions.get(i),
						mChallengeScore.getQuestionScore()[i]);
			}

		}
		
		
	}
	
	private void addLayoutQuizQuestion(LinearLayout container, QuizQuestion q, int scoreQ) {
		LayoutInflater inflater = this.getLayoutInflater();
		LinearLayout ll = (LinearLayout) inflater.inflate(
				R.layout.challenge_result_element_view, null, false);
		
		((TextView) ll.findViewById(R.id.question_result_score_text_view)).setText(""+scoreQ);
		
		((TextView) ll.findViewById(R.id.question_text_view)).setText(q.getQuestion());
		
		TextView sol = (TextView) ll.findViewById(R.id.question_solution_text_view);
		if (q.getClass() == MultipleChoiceQuestion.class){
			MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) q;
			sol.setText(mcq.getPossibleAnswers()[mcq.getIntAnswer()]);
		}
		else {
			sol.setText(q.getAnswer());
		}
		
		container.addView(ll);
	}
}
