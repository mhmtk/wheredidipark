package com.mhmt.wheredidipark;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

public class HomePage extends FragmentActivity
implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener
{
	LocationClient mLocClient;
	Location mCurrentLoc;

	SharedPreferences mSharedPrefs;
	SharedPreferences.Editor mSharedPrefsEditor;

	TextView textCurrentLoc;
	TextView textCarLoc;

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_page);

		textCurrentLoc = (TextView) findViewById(R.id.edit_currentlocation);
		textCarLoc = (TextView) findViewById(R.id.edit_storedlocation);

		mLocClient = new LocationClient(this, this, this);

		mSharedPrefs = this.getSharedPreferences("com.mhmt.wheresmycar", Context.MODE_PRIVATE);

		//display the stored car loc if there is one
		displayCarLoc();
	}

	@Override
	public void onConnected(Bundle dataBundle) {
		//get the current loc and store it
		getCurrentLoc();
		//display the current loc
		displayCurrentLoc();
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
	}

	/*
	 * gets the current location and stores it
	 */
	private void getCurrentLoc() {
		//get current loc
		mCurrentLoc = mLocClient.getLastLocation();
		//store current loc
		storeCurrentLoc(mCurrentLoc.getLatitude(), mCurrentLoc.getLongitude());

	}

	/*
	 * stores the current loc
	 */
	private void storeCurrentLoc(double latitude, double longitude) {
		// TODO Auto-generated method stub
		mSharedPrefs.edit().putString("currentLat", Double.toString(latitude)).commit();
		mSharedPrefs.edit().putString("currentLon", Double.toString(longitude)).commit();
	}

	/*
	 * displays the current location on its appropriate view field
	 */
	private void displayCurrentLoc() {
		// TODO Auto-generated method stub
		textCurrentLoc.setText(Double.toString(mCurrentLoc.getLatitude())
				+ ", " + Double.toString(mCurrentLoc.getLongitude()));
	}

	/*
	 * displays the stored car loc if there is one
	 */
	private void displayCarLoc() {
		if(mSharedPrefs.contains("carLat") && mSharedPrefs.contains("carLon")) //if there is a stored lat
		{
			//gets the stored car loc
			double carLat = Double.parseDouble(mSharedPrefs.getString("carLat", ""));
			double carLon = Double.parseDouble(mSharedPrefs.getString("carLon", ""));
			//display the stored car loc on its appropriate view field
			textCarLoc.setText(carLat + ", " + carLon);
		}
		else 
			textCarLoc.setText("No stored car location exists.");
	}

	/*
	 * called when "i parked here" is clicked,
	 * sets the current loc as the car loc
	 * and stores them
	 */
	public void storeCarLoc(View view) {
		if(servicesConnected())
		{
			mCurrentLoc = mLocClient.getLastLocation();

			double carLat = mCurrentLoc.getLatitude();
			double carLon = mCurrentLoc.getLongitude();

			mSharedPrefs.edit().putString("carLat", Double.toString(carLat)).commit();
			mSharedPrefs.edit().putString("carLon", Double.toString(carLon)).commit();

			((TextView) findViewById(R.id.edit_storedlocation))
			.setText(carLat + ", " + carLon);
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Error! Cannot establish connection" , Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * Called when show map is clicked, launches the Map activity,
	 * which displays a map with a marker for current loc and car loc
	 */
	public void viewMap(View view){
		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);
	}

	public static class ErrorDialogFragment extends DialogFragment{
		private Dialog mDialog;

		public ErrorDialogFragment(){
			super();
			mDialog = null;
		}

		public void setDialog(Dialog dialog){
			mDialog = dialog;
		}
		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		switch (requestCode){
		case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			switch (resultCode){
			case Activity.RESULT_OK:
				break;
			}
		}
	}

	private boolean servicesConnected(){
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (ConnectionResult.SUCCESS == resultCode){
			Log.d("Location Updates", "Google Play Services is available");
			return true;
		}else{
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode,this,CONNECTION_FAILURE_RESOLUTION_REQUEST);
			if (errorDialog != null) {
				// Create a new DialogFragment for the error dialog
				ErrorDialogFragment errorFragment =
						new ErrorDialogFragment();
				// Set the dialog in the DialogFragment
				errorFragment.setDialog(errorDialog);
				// Show the error dialog in the DialogFragment
				errorFragment.show(getSupportFragmentManager(),
						"Location Updates");
			}
			return false;
		}
	}

	@Override
	public void onDisconnected() {
		// Display the connection status
		Toast.makeText(this, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects.
		 * If the error has a resolution, try sending an Intent to
		 * start a Google Play services activity that can resolve
		 * error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(
						this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		mLocClient.connect();
	}

	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		mLocClient.disconnect();
		super.onStop();
	}
}