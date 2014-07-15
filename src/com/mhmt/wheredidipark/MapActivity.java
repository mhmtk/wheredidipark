package com.mhmt.wheredidipark;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.MapController;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Menu;
import android.widget.Toast;

public class MapActivity extends Activity {

	private GoogleMap googleMap;
	private SharedPreferences mSharedPrefs;
	private LatLngBounds.Builder bc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		mSharedPrefs = this.getSharedPreferences("com.mhmt.wheresmycar", Context.MODE_PRIVATE);
		bc = new LatLngBounds.Builder();
		
		try{
			initializeMap();
			markCurrentLoc();
			markCarLoc();
			fixCamera();
//			objMapController.zoomToSpan(Math.abs(Math.max(curLoc, carLoc)), Math.abs(Math.min(curLoc, carLoc)));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Fixes the camera to include both markers
	 */
	private void fixCamera() {
		googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(),
				100, //width
				100, //height
				0));
	}

	/**
	 * Try to get current location from shared preferences, if it exists, mark it
	 */
	private void markCurrentLoc() {
		// TODO Auto-generated method stub
		if(mSharedPrefs.contains("currentLat") && mSharedPrefs.contains("currentLon"))
		{
			LatLng curLoc = new LatLng(Double.valueOf(mSharedPrefs.getString("currentLat", "0"))
					, Double.valueOf(mSharedPrefs.getString("currentLon", "0")));
			googleMap.addMarker(new MarkerOptions()
			.position(curLoc)
			.title("Me"));
			
			bc.include(curLoc);
		}
	}

	/**
	 * Try to get car location from shared preferences, if it exits, mark it
	 */
	private void markCarLoc() {
		if(mSharedPrefs.contains("carLat") && mSharedPrefs.contains("carLon"))
		{
			LatLng carLoc = new LatLng(Double.valueOf(mSharedPrefs.getString("carLat", "0"))
					, Double.valueOf(mSharedPrefs.getString("carLon", "0")));
			googleMap.addMarker(new MarkerOptions()
			.position(carLoc)
			.title("My Car"));
			
			bc.include(carLoc);
		}
		
	}

	/**
	 * Function to load map. If map is not created it will create it for you
	 */
	private void initializeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment_map)).getMap();
			
			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initializeMap();
		fixCamera();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

}