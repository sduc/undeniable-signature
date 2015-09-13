package ch.epfl.lasec.universitycontest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * 
 * Handle the alert dialog. Only one can be viewed at the same time.
 * Error messages quit the current activity.
 * Info messages just show the dialog.
 * 
 * @author Sebastien Duc
 *
 */
public class DialogHandler {
	
	private AlertDialog mDialog;
	private Context c;
	
	public DialogHandler(Context c){
		this.c = c;
	}

	/**
	 * Show an error message to the user in an AlertDialog.
	 * Then finish the activity
	 * @param errorMsgId
	 */
	public void showError(int errorMsgId) {
		showError(c.getString(errorMsgId));
	}
	
	/**
	 * Show an error message to the user in an AlertDialog.
	 * Then finish the activity
	 * @param errorMsg
	 */
	public void  showError(String errorMsg) {
		
		// if already showing message do nothing
		if ( mDialog != null && this.getmDialog().isShowing())
			return;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setMessage(errorMsg);
		builder.setCancelable(false);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setNeutralButton("OK", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				((Activity) c).finish();
			}
		});
		this.mDialog = builder.create();
		mDialog.show();
	}
	
	/**
	 * Show an info message to the user and do nothing.
	 * @param infoMsgId ressource in R.string
	 */
	public void showInfo(int infoMsgId) {
		showInfo(c.getString(infoMsgId));
	}
	
	/**
	 * Show an info message to the user and do nothing.
	 * @param infoMsg
	 */
	public void  showInfo(String infoMsg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setMessage(infoMsg);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setNeutralButton(R.string.neutral_answer, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		this.mDialog = builder.create();
		mDialog.show();
	}
	
	/**
	 * Show a confirmation message where the user has to confirm an action he did.
	 * @param confMsgId
	 * @param yesOcl Action to do when yes is pressed.
	 */
	public void showConfirmation(int confMsgId,OnClickListener oclYes) {
		showConfirmation(c.getString(confMsgId),oclYes);
	}
	
	/**
	 * Show a confirmation message where the user has to confirm an action he
	 * did.
	 * 
	 * @param confMsg
	 * @param yesOcl
	 *            Action to do when yes is pressed.
	 */
	public void showConfirmation(String confMsg,OnClickListener oclYes) {
			AlertDialog.Builder builder = new AlertDialog.Builder(c);
			builder.setMessage(confMsg);
			builder.setCancelable(false);
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			builder.setPositiveButton(R.string.positive_answer, oclYes);
			builder.setNegativeButton(R.string.negative_answer,
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							mDialog.cancel();
						}
					});
			this.mDialog = builder.create();
			this.mDialog.show();
	}

	/**
	 * Getter for mDialog
	 * @return mDialog
	 */
	public AlertDialog getmDialog() {
		return mDialog;
	}
	
}
