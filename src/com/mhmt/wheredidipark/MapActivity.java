package com.mhmt.wheredidipark;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Menu;
import android.widget.Toast;

public class MapActivity extends Activity {

	private GoogleMap googleMap;
	private SharedPreferences mSharedPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		mSharedPrefs = this.getSharedPreferences("com.mhmt.wheresmycar", Context.MODE_PRIVATE);

		try{
			initializeMap();
			markCurrentLoc();
			markCarLoc();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * get current loc from shared prefs if it exists then mark it
	 */
	private void markCurrentLoc() {
		// TODO Auto-generated method stub
		if(mSharedPrefs.contains("curLat"))
		{
			Toast.makeText(getApplicationContext(), "SPref contains curr loc", Toast.LENGTH_SHORT).show();
			LatLng curLoc = new LatLng(Double.valueOf(mSharedPrefs.getString("curLat", "0"))
					, Double.valueOf(mSharedPrefs.getString("curLon", "0")));
			googleMap.addMarker(new MarkerOptions()
			.position(curLoc)
			.title("Me"));
		}
	}

	/*
	 * get car loc from shared prefs if it exits, then mark it
	 */
	private void markCarLoc() {
		if(mSharedPrefs.contains("carLat"))
		{
			LatLng carLoc = new LatLng(Double.valueOf(mSharedPrefs.getString("carLat", "0"))
					, Double.valueOf(mSharedPrefs.getString("carLon", "0")));
			googleMap.addMarker(new MarkerOptions()
			.position(carLoc)
			.title("My Car"));
		}
	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initializeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment_map)).getMap();

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initializeMap();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

}