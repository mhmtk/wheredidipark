package com.mhmt.wheredidipark;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.maps.MapController;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;






import org.w3c.dom.Document;

import com.google.android.gms.maps.model.PolylineOptions;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Menu;
import android.widget.Toast;
import app.alexorcist.googlemapsv2direction.GMapV2Direction;

/**
 * 
 * @author Mehmet Kologlu
 * @version July 15th, 2014
 *
 */
public class MapActivity extends Activity {

	private GoogleMap googleMap;
	private SharedPreferences mSharedPrefs;
	private LatLngBounds.Builder bc; // Boundary builder to use while fixing the camera

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		mSharedPrefs = this.getSharedPreferences("com.mhmt.wheresmycar", Context.MODE_PRIVATE);
		bc = new LatLngBounds.Builder();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
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
		if(mSharedPrefs.contains("currentLat") && mSharedPrefs.contains("currentLon"))
		{
			LatLng curLoc = new LatLng(Double.valueOf(mSharedPrefs.getString("currentLat", "0"))
					, Double.valueOf(mSharedPrefs.getString("currentLon", "0")));
			googleMap.addMarker(new MarkerOptions()
			.position(curLoc)
			.title("Me")
			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
			.showInfoWindow();
			
			bc.include(curLoc); // add point to the latlng boundaries
		}
	}
	
	private void getDir() {
		LatLng fromPosition = new LatLng(13, 100);
		LatLng toPosition = new LatLng(15, 150);

		GMapV2Direction md = new GMapV2Direction();

		Document doc = md.getDocument(fromPosition, toPosition, GMapV2Direction.MODE_DRIVING);
		ArrayList<LatLng> directionPoint = md.getDirection(doc);
		PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);

		for(int i = 0 ; i < directionPoint.size() ; i++) {          
		rectLine.add(directionPoint.get(i));
		}

		googleMap.addPolyline(rectLine);
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
			
			bc.include(carLoc); // add point to the latlng boundaries
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