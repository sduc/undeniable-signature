package ch.epfl.lasec.evoting;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * This class is the main activity of the EVOting client
 * It handles the list of all votes that have to be done or have already finished
 * When selecting a vote it launches the activity BallotActivity so that the user
 * can fill the ballot.
 * 
 * @author Sebastien Duc
 *
 */
public class EvotingActivity extends ListActivity {
	
	private VotesDbAdapter mDbHelper = null;
	
	private Cursor mNotesCursor;
	
	private static final int ACTIVITY_BALLOT = 0;
	private static final int ACTIVITY_RESULT = 1;
	
	private static final int DELETE_ID = Menu.FIRST;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.vote_list);
        mDbHelper = new VotesDbAdapter(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());
        createTestData();
    }
    
    
    /**
     * This method connects to the server to get new votes.
     * It sends the latest vote it has in the DB so the server can check if it has new stuff
     */
    private void getNewVotes(){
    	//TODO: when server is done
    }
    	
    /**
     * Function used to test the application without needing a server
     */
    private void createTestData(){
    	
    	//////////// TEST FUNCTION ///////////////////////
    	// generate the ballot
    	Ballot b = new Ballot(1000L);
    	String subject1 = "Initiative populaire \"Pour en finir avec les "+
    	    	"constructions envahissantes de résidences secondaires\"";
    	String question1 = "Acceptez-vous l'initiative populaire \"Pour en "+
    	    	"finir avec les constructions envahissantes de résidences secondaires\"?";
    	b.addVotingQuestion(subject1, question1);
    	
    	String subject2 = "Initiative populaire \"6 semaine de vacances pour tous\"";
    	String question2 = "Acceptez-vous l'initiative populaire \"6 semaines "+
    			"de vacances pour tous\"?";
    	b.addVotingQuestion(subject2, question2);
    	
    	String subject3 = "Initiative populaire pour l'épargne logement";
    	String question3 = "Accepetez-vous l'initiative populaire pour l'épargne logement";
    	b.addVotingQuestion(subject3, question3);
    	// save ballot on phone
    	BallotDataHandler bdh = new BallotDataHandler(this);
    	try {
			bdh.saveBallot(b);
		} catch (UnexpectedBallotLoadException e1) {
			assert false;
		}
    	
    	//create the vote
    	Vote v = null;
		try {
			v = new Vote(b, "Election fédéral du 11 avril 2012", 
					"2012-02-10", "2012-04-01", "2012-04-11");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(v != null){
			mDbHelper.createVote(v);
		}
    	fillData();
    	//////////////////////////////////////////////////
    	
    }
    
    
    /**
     * This method is used to fill the ListView with the data (in the SQLite database)
     * It is called each time there is a change in the database
     * The view shows the title of the votes, the deadline and the results date
     */
    private void fillData(){
    	// Get all of the notes from the database and create the item list
    	mNotesCursor = mDbHelper.fetchAllVotes();
        startManagingCursor(mNotesCursor);
        
        String[] from = new String[] { VotesDbAdapter.KEY_TITLE , 
        		VotesDbAdapter.KEY_END_DATE , 
        		VotesDbAdapter.KEY_RESULT_DATE};
        int[] to = new int[] { R.id.text_title, 
        		R.id.text_date_due, 
        		R.id.text_date_res };
        
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter votes =
            new SimpleCursorAdapter(this, R.layout.votes_row, mNotesCursor, from, to);
        setListAdapter(votes);

    }
    

    @Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// add the delete option in the context menu to delete a vote
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	// handle the delete option by deleting the selected vote
        switch(item.getItemId()) {
        case DELETE_ID:
            AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
            mDbHelper.deleteVote(info.id);
            fillData();
            return true;
        }
        return super.onContextItemSelected(item);
    }
    
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Cursor c = mNotesCursor;
        c.moveToPosition(position);
        
        Intent i = new Intent(this,BallotActivity.class);
        i.putExtra(VotesDbAdapter.KEY_ROWID, id);
        i.putExtra(VotesDbAdapter.KEY_TITLE, c.getString(
        		c.getColumnIndexOrThrow(VotesDbAdapter.KEY_TITLE)));
        
        i.putExtra(VotesDbAdapter.KEY_RESULT_DATE, c.getString(
        		c.getColumnIndexOrThrow(VotesDbAdapter.KEY_RESULT_DATE)));
        
        // handle the different cases depending on the current date and if the user 
        // already sent his ballot 
        if(hasPassedDeadline(c)){
        	if(resultsReleased(c)){
        		//TODO: show results
        	}
        	else{
        		//TODO: show dialog (no results yet)
        	}
        }
        else{
        	i.putExtra(VotesDbAdapter.KEY_END_DATE, c.getString(
            		c.getColumnIndexOrThrow(VotesDbAdapter.KEY_END_DATE)));
        	if(hasSentVote(c)){
        		//TODO: show dialog (no results yet)
        	}
        	else{
        		// start BallotActicity so that the user can fill the ballot
        		i.putExtra(VotesDbAdapter.KEY_BALLOT_ID, c.getLong(
        				c.getColumnIndexOrThrow(VotesDbAdapter.KEY_BALLOT_ID)));
        		startActivityForResult(i, ACTIVITY_BALLOT);
        		
        	}
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	
    	Bundle extras = (intent == null) ? null:intent.getExtras();
    	
    	switch (requestCode) {
		case ACTIVITY_BALLOT:
			//TODO: set the vote to "sent" or not depending on the user actions
			break;
    	}
    }
    
    /**
     * Test if the deadline is not passed.
     * @param c Cursor on which the test is done
     * @return True if it has passed date, false otherwise.
     */
    private boolean hasPassedDeadline(Cursor c){
        Date currentDate = new Date();
        DateFormat dfm = new SimpleDateFormat(Vote.getDateFormat());
        Date deadline = null;
        try {
        	
			deadline = dfm.parse(c.getString(
					c.getColumnIndexOrThrow(VotesDbAdapter.KEY_END_DATE)));
			
		} catch (IllegalArgumentException e) {
			// Should not happen
			e.printStackTrace();
		} catch (ParseException e) {
			// Should not happen
			e.printStackTrace();
		}
        
        assert deadline!=null;
        return currentDate.after(deadline);
    }
    
    /**
     * Test if the vote was already sent.
     * @param c Cursor on which the test is done
     * @return true if the vote was sent, false otherwise
     */
    private boolean hasSentVote(Cursor c){
    	int isSent = c.getInt(c.getColumnIndexOrThrow(
    			VotesDbAdapter.KEY_IS_SENT));
    	
    	return isSent!=0;
    }
    
    /**
     * Test if the results of a vote are released.
     * @param c Cursor on which the test is done
     * @return Return true if the results are released, false otherwise
     */
    private boolean resultsReleased(Cursor c){
    	Date currentDate = new Date();
        DateFormat dfm = new SimpleDateFormat(Vote.getDateFormat());
        Date results = null;
        try {
        	
			results = dfm.parse(c.getString(
					c.getColumnIndexOrThrow(VotesDbAdapter.KEY_RESULT_DATE)));
			
		} catch (IllegalArgumentException e) {
			// Should not happen
			e.printStackTrace();
		} catch (ParseException e) {
			// Should not happen
			e.printStackTrace();
		}
        
        assert results != null;
        return currentDate.after(results);
    }


	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}
    
    
}