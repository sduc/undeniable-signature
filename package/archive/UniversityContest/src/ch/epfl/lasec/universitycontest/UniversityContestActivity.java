package ch.epfl.lasec.universitycontest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Main activity for the application
 * 
 * @author Sebastien Duc
 *
 */
public class UniversityContestActivity extends Activity {
	
	private Button mCurrentChallengeButton;
	private Button mUniversityScoreButton;
	private Button mTeamScoreButton;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        checkConfig();
        
        mCurrentChallengeButton = (Button) findViewById(R.id.button_current_challenge);
        mUniversityScoreButton = (Button) findViewById(R.id.button_uni_score);
        mTeamScoreButton = (Button) findViewById(R.id.button_team_score);
        
        setActionOnButtons();
    }
    
    
    /**
     * Check if there is a config file. If not start ConfigActivity and create the config file.
     */
    private void checkConfig() {
    	Config c = new Config(this);
    	if (!c.configFileExists()){
			Intent i = new Intent(this,ConfigActivity.class);
			startActivity(i);
		}
    }
    
    /**
     * Set action when clicking on the buttons of the main menu
     */
    private void setActionOnButtons(){
    	mCurrentChallengeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(),ChallengeActivity.class);
				startActivity(i);
			}
		});
    	
    	mTeamScoreButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(),TeamScoreActivity.class);
				startActivity(i);
				
			}
		});
    	
    	mUniversityScoreButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(),UniversityScoreActivity.class);
				startActivity(i);
			}
		});
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.main_menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case R.id.about_menu_item:
			//TODO
			return super.onOptionsItemSelected(item);
		case R.id.config_menu_item:
			Intent i = new Intent(this,ConfigActivity.class);
			startActivity(i);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
    }
    
    
}
