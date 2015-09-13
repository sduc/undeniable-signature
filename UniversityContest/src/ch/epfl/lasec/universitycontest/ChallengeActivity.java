package ch.epfl.lasec.universitycontest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;

import ch.epfl.lasec.LogContainer;
import ch.epfl.lasec.mova.Mova;
import ch.epfl.lasec.mova.MovaPublicKey;
import ch.epfl.lasec.mova.MovaSignature;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * Activity to handle the current challenge.
 * 
 * @author Sebastien Duc
 *
 */
public class ChallengeActivity extends Activity{
	
	
	
	// TODO set ip address dynamically
	/**
	 * Server of "MY" university. Used to sign the filled quizzes.
	 */
	private Server myUniversityServer = new Server(
			Server.ADV_UNIVERSITY_SERVER_IP,
			UniversityContestServer.SERVER_PORT,
			0,null);
	
	// TODO set ip address dynamically
	/**
	 * Server of the adversary (university) in the contest. This server provides the quizzes.
	 */
	private Server adversaryUniversityServer = new Server(
			Server.ADV_UNIVERSITY_SERVER_IP,
			UniversityContestServer.SERVER_PORT,
			0,null);
	
	@SuppressWarnings("unused")
	private int teamID = Config.NO_TEAM;

	/**
	 * The current challenge to solve
	 */
	private Challenge currentChallenge;
	
	/**
	 * The dialog handler to handle dialogue messages.
	 */
	private DialogHandler mDialogHandler = new DialogHandler(this);
	
	private ChallengeDataHandler mCdh = new ChallengeDataHandler(this);
	
	/**
	 * used for the savedInstanceState to recover the current challenge
	 */
	private static final String SAVED_CURRENT_CHALLENGE = "savedchallenge";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadConfig();
		getMova();
		this.setContentView(R.layout.challenge_view);
		loadCurrentChallenge(savedInstanceState);
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
			myUniversityServer.setIpAddress(c.myServerIpAddress);
			myUniversityServer.setTeamID(c.teamID);
			myUniversityServer.setSecret(c.secret);
			adversaryUniversityServer.setIpAddress(c.advServerIpAddress);
			adversaryUniversityServer.setTeamID(c.teamID);
			adversaryUniversityServer.setSecret(c.secret);
			teamID = c.teamID;
			
		} catch (IOException e) {
			mDialogHandler.showError("Configuration Error");
		}
	}
	
	/**
	 * If the phone doesn't have the mova instance/key of the servers yet. 
	 * Get the keys from them.
	 */
	private void getMova() {
		
		MovaDataHandler mdh = new MovaDataHandler(this);
		
		if (!mdh.hasKey(adversaryUniversityServer.getId())) {
			try {
				adversaryUniversityServer.startCommunication();
				// first get and save key
				MovaPublicKey mpk = UniversityContestProtocol.askMovaPublicKey(
						adversaryUniversityServer.getIn(),
						adversaryUniversityServer.getOut());
				mdh.saveMPK(mpk, adversaryUniversityServer.getId());
				
				// then get and save mova instance
				Mova m = UniversityContestProtocol.askMovaInstance(
						adversaryUniversityServer.getIn(), 
						adversaryUniversityServer.getOut());
				
				mdh.saveMova(m, adversaryUniversityServer.getId());
				
				adversaryUniversityServer.endCommunication();
				
			} catch (IOException e) {
				mDialogHandler.showError("Connection Error: Couldn't load public key.");
			} catch (InvalidAuthenticationException e) {
				mDialogHandler.showError(R.string.authentication_error);
			}
		}
		
		if(!mdh.hasKey(myUniversityServer.getId())) {
			//TODO retrieve the key from my university server
		}

	}
	
	/**
	 * Load the current challenge on phone if already downloaded
	 * or download it.
	 */
	private void loadCurrentChallenge(Bundle savedInstanceState){
		// first check the saved instance state
		byte [] challengeEnc = (savedInstanceState == null) ? null :
			savedInstanceState.getByteArray(SAVED_CURRENT_CHALLENGE);
		if (challengeEnc != null) {
			this.currentChallenge = Challenge.fromEncoded(challengeEnc);
			return;
		}
		
		// otherwise load it  from the phone or download new challenge from server
		try {
			this.currentChallenge = mCdh.loadChallenge();
			//TODO : delete current challenge if date passed by
		} catch (FileNotFoundException e) {
			downloadChallenge();
		} catch (UnexpectedChallengeLoadException e) {
			mDialogHandler.showError("Critical error when loading the challenge!");
		}
	}
	
	/**
	 * Download if there is the latest challenge from the server
	 */
	private void downloadChallenge(){
		LogContainer logs = new LogContainer();
		
		// download if there is, the last challenge from the server.
		try {
			adversaryUniversityServer.startCommunication();
			
			MovaDataHandler mdh = new MovaDataHandler(this);
			Mova mova = mdh.loadMova(adversaryUniversityServer.getId());
			MovaPublicKey mpk = mdh.loadMPK(adversaryUniversityServer.getId());
			
			// get the challenge and verify the signature.
			this.currentChallenge = UniversityContestProtocol.
					askNewChallenge(adversaryUniversityServer.getIn(), 
							        adversaryUniversityServer.getOut(),
							        mova,
							        mpk, logs);
		
		} catch (UnknownHostException e1) {
			mDialogHandler.showError(getString(R.string.connection_server_error));
			
		} catch (IOException e1) {
			mDialogHandler.showError(getString(R.string.connection_server_error));
			
		} catch (InvalidSignatureException e) {
			mDialogHandler.showError(logs.toString());
			mDialogHandler.showError("Signatue is invalid");
		} catch (InvalidAuthenticationException e) {
			mDialogHandler.showError(R.string.authentication_error);
		} finally {
			try {
				adversaryUniversityServer.endCommunication();
			} catch (IOException e) {
				mDialogHandler.showError("Critical error");
			}
		}
		
	}
	
	/**
	 * Populate fields in the GUI
	 */
	private void populateFields(){
		
		if (this.currentChallenge == null){
			mDialogHandler.showError("No new challenge available");
			return;
		}
		
		// fill the header
		fillHeader();
		
		// fill the question container
		fillQuestionContainer();
		
		// set the sending button listener
		setSendingButton();
	}
	
	/**
	 * Fill header of activity
	 */
	private void fillHeader(){
		TextView date = (TextView) this.findViewById(R.id.date_due_to_text_view);
		date.setText(this.currentChallenge.getDueTo());
	}
	
	/**
	 * fill the linear layout containing all the question and answer fields
	 */
	private void fillQuestionContainer(){
		LinearLayout ll = (LinearLayout) findViewById(R.id.question_container);
		populateChallenge(ll);
	}
	
	/**
	 * Set the action on the sending button
	 */
	private void setSendingButton() {
		Button sendButton = (Button) findViewById(R.id.send_button);
		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// first ask confirmation to the user
				mDialogHandler.showConfirmation(R.string.confirm_sending, 
						new DialogInterface.OnClickListener() {
					
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								handleSendingChallenge();

								ChallengeActivity.this.finish();
							}
				});
				
				
			}
		});
	}
	
	/**
	 * Method used to populate the layout and add questions
	 * @param ll
	 */
	private void populateChallenge(LinearLayout ll){
		if (this.currentChallenge.getClass() == Quiz.class){
			Quiz currentQuiz = (Quiz) this.currentChallenge;
			for (QuizQuestion q : currentQuiz.getQuestions()) {
				if (q.getClass() == MultipleChoiceQuestion.class){
					MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) q;
					populateMultipleChoiceQuestion(mcq,ll);
				}
				else if (q.getClass() == QuizQuestion.class) {
					populateQuestion(q,ll);
				}
				else {
					assert false;
				}
			}
		}
	}
	
	/**
	 * Method used to add one quiz question in the layout
	 * @param q Quiz question to add
	 * @param ll layout in which the question is added
	 */
	private void populateQuestion(QuizQuestion q , LinearLayout ll) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		LinearLayout questionLayout = (LinearLayout) inflater.inflate(
				R.layout.question_layout, null);
		TextView questionTextView = (TextView) questionLayout
				.findViewById(R.id.question_text_view);
		EditText questionAnswerEdit = (EditText) questionLayout
				.findViewById(R.id.question_answer_edit_text);
		questionTextView.setText(q.getQuestion());
		if (q.getAnswer() != null){
			questionAnswerEdit.setText(q.getAnswer());
		}
		questionAnswerEdit.setOnEditorActionListener(new TextEditListener(q));
		ll.addView(questionLayout);
	}

	/**
	 * Method used to add one multiple choice question in the layout
	 * @param mcq multiple choice question to add
	 * @param ll layout in which the multiple choice question is added
	 */
	private void populateMultipleChoiceQuestion(MultipleChoiceQuestion mcq,
			LinearLayout ll) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		LinearLayout mcqLayout = (LinearLayout) inflater.inflate(
				R.layout.mcq_layout, null);
		TextView questionTextView = (TextView) mcqLayout
				.findViewById(R.id.mcq_question_text_view);
		RadioGroup possibleAnswerLayout = (RadioGroup) mcqLayout
				.findViewById(R.id.possible_answer_layout);
		questionTextView.setText(mcq.getQuestion());
		populatePossibleAnswers(mcq, possibleAnswerLayout);
		possibleAnswerLayout.setOnCheckedChangeListener(new RadioGrouplistener(mcq));
		ll.addView(mcqLayout);
	}
	
	/**
	 * Method used to populate the possible answers in layout l.
	 * Creates one radio button for each possible answer.
	 * @param mcq reference to the question
	 * @param l reference to the layout
	 */
	private void populatePossibleAnswers(MultipleChoiceQuestion mcq,
			RadioGroup possibleAnswerLayout) {
		for (int i = 0; i < mcq.getPossibleAnswers().length; i++) {
			RadioButton rb = new RadioButton(this);
			rb.setId(i);
			rb.setText(mcq.getPossibleAnswers()[i]);
			// if the answer is the ith then check the radio button
			if (mcq.getIntAnswer() == i){
				rb.setChecked(true);
			}
			possibleAnswerLayout.addView(rb);
		}
	}
	
	@Override
    protected void onPause() {
    	super.onPause();
    	saveState();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    }
    
    /**
     * Method called when leaving activity. Used to save the state of the application.
     */
    private void saveState(){
    	if (currentChallenge == null)
    		return;
    	try {
			mCdh.saveChallenge(this.currentChallenge);
		} catch (UnexpectedChallengeLoadException e) {
			// show something if fail
		}
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	if (currentChallenge == null)
    		return;
    	try {
			mCdh.saveChallenge(this.currentChallenge);
		} catch (UnexpectedChallengeLoadException e) {
			// show something if fail
		}
    	outState.putByteArray(SAVED_CURRENT_CHALLENGE, this.currentChallenge.getEncoded());
    }
	
    
    /**
     * Handle all actions to be done when send button pressed and sending challenge.
     * First get the mova signature of the filled challenge by sending it to 
     * myUniversityServer. Then send the signed filled challenge to the advUniversityServer.
     */
    private void handleSendingChallenge() {
    	try {
    		MovaSignature s = getSignature(this.currentChallenge);
			sendChallenge(s);
			
			// if the challenge was send then delete it otherwise show message error
			mCdh.deleteCurrentSavedChallenge();
		} catch (UnknownHostException e) {
			Log.e("Connection", e.toString());
			mDialogHandler.showError(R.string.connection_server_error);
		} catch (IOException e) {
			Log.e("Connection",e.toString());
			mDialogHandler.showError(R.string.connection_server_error);
		} catch (InvalidAuthenticationException e) {
			mDialogHandler.showError(R.string.authentication_error);
		}
    }
    
    
    /**
     * Get the university of the filled challenge.
     * @param c
     * @return
     * @throws UnknownHostException
     * @throws IOException
     * @throws InvalidAuthenticationException 
     */
    private MovaSignature getSignature(Challenge c) 
    		throws UnknownHostException, IOException, InvalidAuthenticationException {
    	myUniversityServer.startCommunication();
    	MovaSignature s = null;
    	// get signature from server
    	s = UniversityContestProtocol.askToSign(
    			myUniversityServer.getIn(), 
    			myUniversityServer.getOut(), 
    			c);
    	
    	myUniversityServer.endCommunication();
    	return s;
    }
    
    
    /**
     * Send the signed filled challenge to the advUniversityServer.
     * @param s
     * @throws IOException 
     * @throws InvalidAuthenticationException 
     */
    private void sendChallenge(MovaSignature s) throws IOException, InvalidAuthenticationException{
    	adversaryUniversityServer.startCommunication();
    	
    	UniversityContestProtocol.sendFilledSignedChallenge(
    			adversaryUniversityServer.getIn(),
    			adversaryUniversityServer.getOut(), 
    			this.currentChallenge, s);
    	
    	adversaryUniversityServer.endCommunication();
    }
	
	/**
	 * Listener used to update the answer of challenge when state of radio
	 * group is changed
	 * 
	 * @author Sebastien Duc
	 *
	 */
	class RadioGrouplistener implements OnCheckedChangeListener {
		
		private MultipleChoiceQuestion mcq;
		
		RadioGrouplistener(MultipleChoiceQuestion q){
			this.mcq = q;
		}
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			mcq.setAnswer(checkedId);
		}
	};
	
	/**
	 * Class used to implement the listener used for the text edits of the answers
	 * 
	 * @author Sebastien Duc
	 *
	 */
	class TextEditListener implements OnEditorActionListener {
		
		private QuizQuestion q;
		
		public TextEditListener(QuizQuestion q) {
			this.q = q;
		}
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				q.setAnswer(v.getText().toString());
				InputMethodManager inputManager = (InputMethodManager) 
						getSystemService(Context.INPUT_METHOD_SERVICE);

				inputManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				return true;
			}
			return false;
		}
	}
	
	
}
