package ch.epfl.lasec.evoting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class VotesDbAdapter {
	
	public static final String KEY_TITLE = "title";
	public static final String KEY_CREATION_DATE = "creationDate";
    public static final String KEY_END_DATE = "endDate";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_RESULT_DATE = "resultDate";
    public static final String KEY_IS_SENT = "isSent";
    public static final String KEY_BALLOT_ID = "ballot_id";

    private static final String TAG = "VotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
        "create table votes ("+KEY_ROWID+" integer primary key autoincrement, "
        + KEY_TITLE +" text not null, "
        + KEY_CREATION_DATE +" text not null, "
        + KEY_END_DATE +" text not null, "
        + KEY_RESULT_DATE + " text not null, "
        + KEY_BALLOT_ID + " integer, "
        + KEY_IS_SENT +" integer);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "votes";
    private static final int DATABASE_VERSION = 3;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS votes");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public VotesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the votes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public VotesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new vote using the title and body provided. If the vote is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param vote Object representing the vote to add
     * @return rowId or -1 if failed
     */
    public long createVote(Vote vote) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, vote.getTitle());
        initialValues.put(KEY_CREATION_DATE, vote.getCreationDateToString());
        initialValues.put(KEY_END_DATE, vote.getEndDateToString());
        initialValues.put(KEY_RESULT_DATE, vote.getResultDateToString());
        initialValues.put(KEY_IS_SENT, 0);
        initialValues.put(KEY_BALLOT_ID, vote.getBallot().getBallotID());

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the vote with the given rowId
     * 
     * @param rowId id of vote to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteVote(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all votes in the database
     * 
     * @return Cursor over all votes
     */
    public Cursor fetchAllVotes() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE, 
        		KEY_CREATION_DATE, KEY_END_DATE, KEY_RESULT_DATE,
        		KEY_BALLOT_ID, KEY_IS_SENT}, 
        		null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the vote that matches the given rowId
     * 
     * @param rowId id of vote to retrieve
     * @return Cursor positioned to matching vote, if found
     * @throws SQLException if vote could not be found/retrieved
     */
    public Cursor fetchVote(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_TITLE, KEY_CREATION_DATE, KEY_END_DATE, 
                    KEY_RESULT_DATE, KEY_BALLOT_ID, KEY_IS_SENT}, 
                    KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the vote using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateNote(long rowId, String title, String body) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        //args.put(KEY_BODY, body);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

}
