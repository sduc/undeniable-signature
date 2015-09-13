package ch.epfl.lasec.universitycontest;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * 
 * Activity that handle the config of the application.
 * It allows the user to choose the config. 
 * 
 * @author Sebastien Duc
 *
 */
public class ConfigActivity extends Activity {

	private Config config;
	private DialogHandler mDialoghandler = new DialogHandler(this);
	private Activity _this = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.config_view);
		
		loadConfig();
		populateFields();
	}
	
	private void loadConfig(){
		config = new Config(this);
		try {
			config.loadConfig();
		} catch (IOException e) {
			//
		}
	}
	
	private void populateFields() {
		EditText myUniEditText = (EditText) findViewById(R.id.config_edit_text_my_uni_address);
		EditText advUniEditText = (EditText) findViewById(R.id.config_edit_text_adv_uni_address);
		EditText teamEditText = (EditText) findViewById(R.id.config_edit_text_team_id);
		
		if(config.myServerIpAddress != null)
			myUniEditText.setText(config.myServerIpAddress);
		if(config.advServerIpAddress != null)
			advUniEditText.setText(config.advServerIpAddress);
		if(config.hasTeamId())
			teamEditText.setText(""+config.teamID);
		
		myUniEditText.setOnEditorActionListener(new TextEditListener(this.config));
		advUniEditText.setOnEditorActionListener(new TextEditListener(this.config));
		teamEditText.setOnEditorActionListener(new TextEditListener(this.config));
	}
	
	/**
	 * Class used to implement the listener used for the text edits
	 * 
	 * @author Sebastien Duc
	 *
	 */
	class TextEditListener implements OnEditorActionListener {

		private Config c;
		
		public TextEditListener(Config c) {
			this.c = c;
		}
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId != EditorInfo.IME_ACTION_DONE)
				return false;
			
			switch (v.getId()) {
			case R.id.config_edit_text_my_uni_address:
				c.myServerIpAddress = v.getText().toString();
				break;

			case R.id.config_edit_text_adv_uni_address:
				c.advServerIpAddress = v.getText().toString();
				break;
				
			case R.id.config_edit_text_team_id:
				c.teamID = Integer.parseInt(v.getText().toString());
				
				AlertDialog.Builder builder = new AlertDialog.Builder(_this);
				builder.setMessage("Enter the password");
				final EditText input = new EditText(_this);
				input.setTransformationMethod(PasswordTransformationMethod.getInstance());
				builder.setView(input);
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						c.secret = input.getText().toString();
						
					}
				});
				builder.show();
				break;
				
			}
			
			InputMethodManager inputManager = (InputMethodManager) 
					getSystemService(Context.INPUT_METHOD_SERVICE);

			inputManager.hideSoftInputFromWindow(getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			
			return true;
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
    	try {
			config.saveConfig();
		} catch (IOException e) {
			// should not happen
		} catch (AddressNotSetException e) {
			mDialoghandler.showInfo("Warning: University Address is missing");
		} catch (TeamNotSetException e) {
			mDialoghandler.showInfo("Warning: No team ID set");
		} catch (SecretNotSetException e) {
			mDialoghandler.showInfo("Warning: No secret is set");
		}
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	try {
			config.saveConfig();
		} catch (IOException e) {
			// should not happen
		} catch (AddressNotSetException e) {
			mDialoghandler.showInfo("Warning: University Address is missing");
		} catch (TeamNotSetException e) {
			mDialoghandler.showInfo("Warning: No team ID set");
		} catch (SecretNotSetException e) {
			mDialoghandler.showInfo("Warning: No secret is set");
		}
    }
    
	@Override
	public void onBackPressed() {
		if (config.incomplete()) {
			mDialoghandler
					.showInfo("Config is incomplete. Please Complete it before leaving.");
		} else {
			super.onBackPressed();
		}
	}
	
}
