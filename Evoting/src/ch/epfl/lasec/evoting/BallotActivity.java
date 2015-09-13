package ch.epfl.lasec.evoting;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView.SavedState;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This activity is used for the ballots. The user can fill it and then send it
 * to the server. It can be only called by EvotingActivity.
 * 
 * @author Sebastien Duc
 * 
 */
public class BallotActivity extends Activity {

	private Button mSendButton;
	private LinearLayout mBallotContainerLayout;
	private TextView mTitleText;
	private Long mRowId;
	private Long mBallotId;
	private Ballot mBallot;
	private VotesDbAdapter mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new VotesDbAdapter(this);
		mDbHelper.open();

		setContentView(R.layout.ballot);
		setTitle(R.string.ballot);

		mBallotContainerLayout = (LinearLayout) findViewById(R.id.ballot_container_layout);
		mSendButton = (Button) findViewById(R.id.send_button);
		mTitleText = (TextView) findViewById(R.id.title_ballot_text);

		// handleIntentExtras();
		
		// load the row_id from extras or savedInstanceState
		mRowId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(VotesDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras.getLong(VotesDbAdapter.KEY_ROWID);
		}

		populateFields();

		//loadBallot();

		//setActionOnSendButton();

		//createAllVotingObjectsLayout();

	}

	/**
	 * Create the layouts for all voting objects in the ballot and add them in
	 * the LinearLayout mBallotContainerLayout.
	 */
	private void createAllVotingObjectsLayout() {
		for (String subject : mBallot.getObjects().keySet()) {
			createVotingObjectLayout(subject);
		}
	}

	/**
	 * Create the layout for the voting object with subject {@code subject}.
	 * 
	 * @param subject
	 *            The subject of the voting object for which we create the
	 *            layout
	 */
	private void createVotingObjectLayout(String subject) {
		VotingObject vo = mBallot.getVotingObject(subject);
		if (vo.getType() == VotingObject.TYPE_QUESTION) {
			// create the layout for the question
			LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			LinearLayout ll = (LinearLayout) inflater.inflate(
					R.layout.question_object, null);

			// create related views and widgets
			TextView tvSubject = (TextView) ll
					.findViewById(R.id.question_object_subject_text);
			TextView tvQuestion = (TextView) ll
					.findViewById(R.id.question_text);
			CheckBox cbNo = (CheckBox) ll.findViewById(R.id.answer_no_box);
			CheckBox cbYes = (CheckBox) ll.findViewById(R.id.answer_yes_box);

			// set listener for the check boxes
			YesNoOnCheckedChangeListener listener = new YesNoOnCheckedChangeListener(
					cbYes, cbNo, vo.getSubject());
			cbNo.setOnCheckedChangeListener(listener);
			cbYes.setOnCheckedChangeListener(listener);

			// set the texts and the check boxes
			tvSubject.setText(vo.getSubject());
			tvQuestion.setText(((QuestionObject) vo).getQuestion());
			switch (vo.getAnswer()) {
			case VotingObject.ANSWER_NO:
				cbNo.setChecked(true);
				cbYes.setChecked(false);
				break;
			case VotingObject.ANSWER_YES:
				cbNo.setChecked(false);
				cbYes.setChecked(true);
				break;
			case VotingObject.ANSWER_WHITE:
				cbNo.setChecked(false);
				cbYes.setChecked(false);
				break;
			}

			// add the layout
			mBallotContainerLayout.addView(ll);
		}
	}

	/**
	 * This method is used to retrieve extra information from the intent and
	 * handle it.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void handleIntentExtras() {
		// retrieve title, rowID, and BallotID from extras
		mRowId = null;
		mBallotId = null;
		Bundle extras = getIntent().getExtras();
		assert extras != null;
		String title = extras.getString(VotesDbAdapter.KEY_TITLE);
		assert title != null;
		mRowId = extras.getLong(VotesDbAdapter.KEY_ROWID);
		assert mRowId != null;
		mBallotId = extras.getLong(VotesDbAdapter.KEY_BALLOT_ID);
		assert mBallotId != null;

		// set the text of the Title TextView to title
		mTitleText.setText(title);
	}

	/**
	 * Function used to populate fields such as the title of the vote, the ballot id.
	 */
	private void populateFields() {
		assert mRowId != null;
		Cursor vote = mDbHelper.fetchVote(mRowId);
		startManagingCursor(vote);
		mTitleText.setText(vote.getString(vote
				.getColumnIndexOrThrow(VotesDbAdapter.KEY_TITLE)));
		mBallotId = vote.getLong(vote
				.getColumnIndex(VotesDbAdapter.KEY_BALLOT_ID));
		
		
		loadBallot();
		
		setActionOnSendButton();

		createAllVotingObjectsLayout();

	}
	
	@Override
	protected void onPause() {
		super.onPause();
		saveState();
		mDbHelper.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mDbHelper.open();
		//populateFields();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putSerializable(VotesDbAdapter.KEY_ROWID, mRowId);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}

	/**
	 * Method used to save the state of the activity.
	 */
	private void saveState(){
		saveBallot();
	}

	/**
	 * This method set the action of the send button
	 */
	private void setActionOnSendButton() {
		mSendButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO: send filled ballot to the server
				Bundle bundle = new Bundle();

				bundle.putString(VotesDbAdapter.KEY_TITLE, mTitleText.getText()
						.toString());
				bundle.putLong(VotesDbAdapter.KEY_BALLOT_ID, mBallotId);
				bundle.putLong(VotesDbAdapter.KEY_ROWID, mRowId);

				Intent mIntent = new Intent();
				mIntent.putExtras(bundle);
				setResult(RESULT_OK, mIntent);
				mDbHelper.close();
				finish();

			}

		});
	}

	/**
	 * This method loads the ballot from phone
	 */
	private void loadBallot() {
		BallotDataHandler bdh = new BallotDataHandler(this);
		mBallot = null;
		try {
			mBallot = bdh.loadBallot(mBallotId);
		} catch (BallotIDNotFoundException e) {
			assert false;
		} catch (UnexpectedBallotLoadException e) {
			assert false;
		}
	}
	
	/**
	 * This method is used to save the ballot on the phone.
	 */
	private void saveBallot() {
		BallotDataHandler bdh = new BallotDataHandler(this);
		try {
			bdh.saveBallot(mBallot);
		} catch (UnexpectedBallotLoadException e) {
			assert false;
		}
	}

	/**
	 * Class used to handle and listen the CheckBox yes and no It allow only one
	 * of them to be check at most and when change is made, it updates the
	 * answer entry in the ballot.
	 * 
	 * @author Sebastien Duc
	 * 
	 */
	class YesNoOnCheckedChangeListener implements OnCheckedChangeListener {

		private String qo;
		private CheckBox yesBox;
		private CheckBox noBox;

		/**
		 * Constructor for YesNoOnCheckedChangeListener
		 * 
		 * @param yes
		 *            Reference to the yes CheckBox
		 * @param no
		 *            Reference to the no CheckBox
		 * @param qo
		 *            Reference to the QuestionObject
		 */
		public YesNoOnCheckedChangeListener(CheckBox yes, CheckBox no, String qo) {
			this.yesBox = yes;
			this.noBox = no;
			this.qo = qo;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked) {
				switch (buttonView.getId()) {
				case R.id.answer_no_box:
					noBox.setChecked(true);
					yesBox.setChecked(false);
					mBallot.setAnswerVotingObject(qo, VotingObject.ANSWER_NO);
					break;
				case R.id.answer_yes_box:
					yesBox.setChecked(true);
					noBox.setChecked(false);
					mBallot.setAnswerVotingObject(qo, VotingObject.ANSWER_YES);
					break;
				}

			} else if (!yesBox.isChecked() && !noBox.isChecked()) {
				mBallot.setAnswerVotingObject(qo, VotingObject.ANSWER_WHITE);
			}

		}

	}

}
