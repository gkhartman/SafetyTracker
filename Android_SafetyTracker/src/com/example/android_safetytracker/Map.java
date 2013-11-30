package com.example.android_safetytracker;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;


/**
 * This class builds the map fragment and has the option to select how the map is
 * displayed. This is achieved by using a spinner which allows the user to select
 * how he wants the map displayed.
 * 
 * Instructions
 * 1) create intent of Map
 * 2) add extras to intent(date,infraction,latitude,longitude)
 * 3) start intent
 * 
 * @author victor
 *
 */

public class Map extends FragmentActivity implements OnItemSelectedListener
{
	private GoogleMap map;
	private LatLng coordinate;
	private String infraction;
	private String date;
	private Spinner dropDown;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		initComponents();
		retrieveData();
		decorateMap();
	}
	
	private void retrieveData()
	{
		infraction = this.getIntent().getStringExtra("infraction");
		date = this.getIntent().getStringExtra("date");
		double lat = this.getIntent().getDoubleExtra("latitude", -1);
		double lon = this.getIntent().getDoubleExtra("longitude", -1);
		coordinate = new LatLng(lat, lon);
	}
	
	private void initComponents()
	{
		map = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		
		dropDown = (Spinner)findViewById(R.id.mapLayoutSpinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.mapsSpinnerArray, 
																	android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dropDown.setAdapter(adapter);
		dropDown.setOnItemSelectedListener(this);
	}
	
	private void decorateMap()
	{
		CameraUpdate center = CameraUpdateFactory.newLatLng(coordinate);
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
		map.moveCamera(center);
		map.animateCamera(zoom);
		map.addMarker(new MarkerOptions()
		//.icon(BitmapDescriptorFactory.fromResource(R.drawable.warning_sign)) we can add our own icons
		.position(coordinate)
		.title(infraction)
		.snippet(date));
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) 
	{
		if(pos == 0)
			map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		if(pos == 1)
			map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		else
			map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {}

}